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

package edu.duke.cs.libprotnmr.atomType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class AtomTypeReader
{
	/**************************
	 *   Methods
	 **************************/
	
	public TreeMap<String,TreeMap<String,AtomType>> read( String path )
	throws IOException
	{
		return read( new FileInputStream( path ) );
	}
	
	public TreeMap<String,TreeMap<String,AtomType>> read( InputStream in )
	throws IOException
	{
		TreeMap<String,TreeMap<String,AtomType>> typeMap = new TreeMap<String,TreeMap<String,AtomType>>();
		TreeMap<String,AtomType> group = null;
		String groupName = null;
		
		// read file line by line
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// skip blank lines
			if( line.length() == 0 )
			{
				continue;
			}
			
			// determine if the line is an group or a type
			boolean isGroup = line.charAt( 0 ) != '\t';
			
			// skip more blank lines
			line = line.trim();
			if( line.equals( "" ) )
			{
				continue;
			}
			
			if( isGroup )
			{
				// finish up the old group if needed
				if( group != null )
				{
					typeMap.put( groupName, group );
				}
				
				// start a new group
				group = new TreeMap<String,AtomType>();
				groupName = line.trim().toUpperCase();
			}
			else
			{
				// parse the line to get the atom name and the atom type
				String[] parts = line.split( "\\t" );
				String atomName = parts[0];
				AtomType atomType = AtomType.getByCode( parts[1] );
				
				// read the bond and add it to the amino acid
				group.put( atomName, atomType );
			}
		}
		
		// finish up the old group if needed
		if( group != null )
		{
			typeMap.put( groupName, group );
		}
		
		return typeMap;
	}
}
