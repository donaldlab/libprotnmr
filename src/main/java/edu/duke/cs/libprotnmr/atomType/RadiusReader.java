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
