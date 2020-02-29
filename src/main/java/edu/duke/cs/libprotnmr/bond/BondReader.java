/*
 * This file is part of LibProtNMR
 *
 * Copyright (C) 2020 Bruce Donald Lab, Duke University
 *
 * LibProtNMR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibProtNMR.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Info:
 *    Bruce Donald
 *    Duke University
 *    Department of Computer Science
 *    Levine Science Research Center (LSRC)
 *    Durham
 *    NC 27708-0129
 *    USA
 *    e-mail: www.cs.duke.edu/brd/
 *
 * <signature of Bruce Donald>, February, 2020
 * Bruce Donald, Professor of Computer Science
 */

package edu.duke.cs.libprotnmr.bond;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class BondReader
{
	/**************************
	 *   Methods
	 **************************/
	
	public HashMap<String,ArrayList<Bond>> read( String path )
	throws IOException
	{
		return read( new FileInputStream( path ) );
	}
	
	public HashMap<String,ArrayList<Bond>> read( InputStream in )
	throws IOException
	{
		HashMap<String,ArrayList<Bond>> bonds = new HashMap<String,ArrayList<Bond>>();
		String aminoAcidName = "";
		ArrayList<Bond> aminoAcid = null;
		
		// read file line by line
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// trim comments
			line = line.replaceAll( "#.*$", "" );
			
			// skip blank lines
			if( line.length() == 0 )
			{
				continue;
			}
			
			// determine if the line is an amino acid or a bond
			boolean isAminoAcid = line.charAt( 0 ) != '\t';
			
			// skip more blank lines
			line = line.trim();
			if( line.equals( "" ) )
			{
				continue;
			}
			
			if( isAminoAcid )
			{
				// finish up the old amino acid if needed
				if( aminoAcid != null )
				{
					bonds.put( aminoAcidName, aminoAcid );
				}
				
				// start a new amino acid
				aminoAcid = new ArrayList<Bond>();
				aminoAcidName = line;
			}
			else
			{
				// read the bond and add it to the amino acid
				aminoAcid.add( readBond( line ) );
			}
		}
		
		// finish up the old amino acid if needed
		if( aminoAcid != null )
		{
			bonds.put( aminoAcidName, aminoAcid );
		}
		
		return bonds;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private Bond readBond( String line )
	{
		String[] parts = line.split( "\\t" );
		
		// check for a next residue flag
		boolean bondsToNextResidue = false;
		if( parts[1].charAt( 0 ) == '+' )
		{
			parts[1] = parts[1].substring( 1 );
			bondsToNextResidue = true;
		}
		
		return new Bond( parts[0], parts[1], BondStrength.valueOf( Integer.parseInt( parts[2] ) ), bondsToNextResidue );
	}
}
