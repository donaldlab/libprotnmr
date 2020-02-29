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
package edu.duke.cs.libprotnmr.clustering.distance;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.duke.cs.libprotnmr.math.IndexPairIterator;


public class DistanceMatrixWriter
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void write( String path, DistanceMatrix distances )
	throws IOException
	{
		write( new File( path ), distances );
	}
	
	public static void write( File file, DistanceMatrix distances )
	throws IOException
	{
		// open the file for binary writing
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );
		
		IndexPairIterator iter = new IndexPairIterator( distances.getNumPoints() );
		while( iter.hasNext() )
		{
			out.writeDouble( distances.get( iter.next() ) );
		}
		
		// cleanup
		out.close();
	}
}
