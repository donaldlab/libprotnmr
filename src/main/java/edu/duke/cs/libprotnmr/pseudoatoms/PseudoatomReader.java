/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.pseudoatoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.protein.AminoAcid;


public class PseudoatomReader
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Pseudoatoms read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public static Pseudoatoms read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public static Pseudoatoms read( InputStream in )
	throws IOException
	{
		Pseudoatoms pseudoatoms = new Pseudoatoms();
		AminoAcid aminoAcid = null;
		
		// read in the file line-by-line
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// skip blank lines
			line = line.trim();
			if( line.length() == 0 )
			{
				continue;
			}
			
			// lines that start with # are comments. Ignore them
			if( line.charAt( 0 ) == '#' )
			{
				continue;
			}
			
			// do we need to start a new amino acid?
			if( line.length() == 3 )
			{
				aminoAcid = AminoAcid.getByAbbreviation( line );
			}
			else
			{
				parseEntry( pseudoatoms, aminoAcid, line );
			}
		}
		
		// cleanup
		reader.close();
		
		return pseudoatoms;
	}
	
	private static void parseEntry( Pseudoatoms pseudoatoms, AminoAcid aminoAcid, String line )
	{
		// parse the line
		String[] parts = line.split( ":" );
		String pseudoatomName = parts[0];
		String[] atomNames = parts[1].split( "," );
		double correction = Double.valueOf( parts[2] );
		
		// handle the masks if needed
		if( parts.length == 4 )
		{
			String[] masks = parts[3].split( "," );
			for( String mask : masks )
			{
				pseudoatoms.addMaskToName( aminoAcid, mask, pseudoatomName );
				pseudoatoms.addNameToMask( aminoAcid, pseudoatomName, mask );
			}
		}
		
		// build the names list
		ArrayList<String> atomNamesList = new ArrayList<String>( atomNames.length );
		for( int i=0; i<atomNames.length; i++ )
		{
			atomNamesList.add( atomNames[i] );
		}
		
		// add the record
		pseudoatoms.addNameToAtoms( aminoAcid, pseudoatomName, atomNamesList );
		
		// add the correction
		pseudoatoms.addCorrection( aminoAcid, pseudoatomName, correction );
	}
}
