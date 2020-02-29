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
import java.util.HashMap;

public class RadiusReader
{
	/**************************
	 *   Methods
	 **************************/
	
	public HashMap<String,Double> read( String path )
	throws IOException
	{
		return read( new FileInputStream( path ) );
	}
	
	public HashMap<String,Double> read( InputStream in )
	throws IOException
	{
		HashMap<String,Double> radii = new HashMap<String,Double>();
		
		// for each line in the file...
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// trim and ignore comments
			line = line.trim();
			if( line.length() == 0 || line.charAt( 0 ) == '#' )
			{
				continue;
			}
			
			// parse entries
			String[] parts = line.split( "\\t" );
			if( parts.length == 2 )
			{
				radii.put( parts[0].toLowerCase(), Double.parseDouble( parts[1] ) );
			}
		}
		
		return radii;
	}
}
