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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.IndexPairIterator;
import edu.duke.cs.libprotnmr.perf.MessageListener;
import edu.duke.cs.libprotnmr.perf.Progress;


public class DistanceMatrixReader
{
	/**************************
	 *   Definitions
	 **************************/
	
	// is there a sizeof() construct in java?
	private static final int BytesPerDouble = 8;
	
	 
	/**************************
	 *   Data Members
	 **************************/
	
	private static MessageListener m_progressListener;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void setProgressListener( MessageListener progressListener )
	{
		m_progressListener = progressListener;
	}
	
	public static long getNumDistances( File file )
	{
		return file.length() / BytesPerDouble;
	}
	
	public static int getNumPoints( File file )
	{
		long numDistances = getNumDistances( file );
		double dNumPoints = ( 1.0 + Math.sqrt( 1.0 + 8.0 * numDistances ) ) / 2.0;
		int numPoints = (int)dNumPoints;
		
		// just in case, make sure we got something extremely close to an integer
		assert( CompareReal.eq( dNumPoints - (double)numPoints, 0.0 ) );
		
		return numPoints;
	}
	
	public static DistanceMatrix read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public static DistanceMatrix read( File file )
	throws IOException
	{
		// just in case...
		assert( file.length() % BytesPerDouble == 0 );
				
		// get the number of points
		long numDistances = getNumDistances( file );
		int numPoints = getNumPoints( file );
		
		// allocate memory for the distance matrix
		DistanceMatrix distances = new DistanceMatrix( numPoints );
		
		// ALERT
		Progress progress = null;
		if( m_progressListener != null )
		{
			progress = new Progress( numDistances, 15000 );
			progress.setMessageListener( m_progressListener );
		}
		
		// open the file
		DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
		IndexPairIterator iter = new IndexPairIterator( distances.getNumPoints() );
		long i = 0;
		final int step = 1000;
		while( iter.hasNext() )
		{
			distances.set( iter.next(), in.readDouble() );
			
			// ALERT
			if( progress != null && ( ++i % step == 0 ) )
			{
				progress.incrementProgress( step );
			}
		}
		in.close();
		
		return distances;
	}
}
