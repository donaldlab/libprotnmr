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
