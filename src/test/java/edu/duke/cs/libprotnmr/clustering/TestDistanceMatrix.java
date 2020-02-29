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
package edu.duke.cs.libprotnmr.clustering;

import java.io.File;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrix;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrixReader;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrixWriter;
import edu.duke.cs.libprotnmr.math.IndexPair;
import edu.duke.cs.libprotnmr.math.IndexPairIterator;


public class TestDistanceMatrix extends ExtendedTestCase
{
	private static final String DistanceMatrixPath = "/tmp/distance.matrix";
	
	public void testConstructor( )
	{
		DistanceMatrix m = null;
		
		m = new DistanceMatrix( 2 );
		assertEquals( 2, m.getNumPoints() );
		assertEquals( 1, m.getNumDistances() );
		
		m = new DistanceMatrix( 3 );
		assertEquals( 3, m.getNumPoints() );
		assertEquals( 3, m.getNumDistances() );
		
		m = new DistanceMatrix( 4 );
		assertEquals( 4, m.getNumPoints() );
		assertEquals( 6, m.getNumDistances() );
		
		m = new DistanceMatrix( 5 );
		assertEquals( 5, m.getNumPoints() );
		assertEquals( 10, m.getNumDistances() );
	}
	
	public void testRawAccessors( )
	{
		DistanceMatrix m = new DistanceMatrix( 4 );
		
		m.set( 1, 0, 1.0 );
		m.set( 2, 0, 2.0 );
		m.set( 2, 1, 3.0 );
		m.set( 3, 0, 4.0 );
		m.set( 3, 1, 5.0 );
		m.set( 3, 2, 6.0 );
		
		assertEquals( 0.0, m.get( 0, 0 ) );
		assertEquals( 0.0, m.get( 1, 1 ) );
		assertEquals( 0.0, m.get( 2, 2 ) );
		assertEquals( 0.0, m.get( 3, 3 ) );
		
		assertEquals( 1.0, m.get( 1, 0 ) );
		assertEquals( 2.0, m.get( 2, 0 ) );
		assertEquals( 3.0, m.get( 2, 1 ) );
		assertEquals( 4.0, m.get( 3, 0 ) );
		assertEquals( 5.0, m.get( 3, 1 ) );
		assertEquals( 6.0, m.get( 3, 2 ) );
	}
	
	public void testPairAccessors( )
	{
		DistanceMatrix m = new DistanceMatrix( 4 );
		
		m.set( new IndexPair( 1, 0 ), 1.0 );
		m.set( new IndexPair( 2, 0 ), 2.0 );
		m.set( new IndexPair( 2, 1 ), 3.0 );
		m.set( new IndexPair( 3, 0 ), 4.0 );
		m.set( new IndexPair( 3, 1 ), 5.0 );
		m.set( new IndexPair( 3, 2 ), 6.0 );
		
		assertEquals( 1.0, m.get( new IndexPair( 1, 0 ) ) );
		assertEquals( 2.0, m.get( new IndexPair( 2, 0 ) ) );
		assertEquals( 3.0, m.get( new IndexPair( 2, 1 ) ) );
		assertEquals( 4.0, m.get( new IndexPair( 3, 0 ) ) );
		assertEquals( 5.0, m.get( new IndexPair( 3, 1 ) ) );
		assertEquals( 6.0, m.get( new IndexPair( 3, 2 ) ) );
	}
	
	public void testSwapGetters( )
	{
		DistanceMatrix m = new DistanceMatrix( 3 );
		
		m.set( 1, 0, 1.0 );
		m.set( 2, 0, 2.0 );
		m.set( 2, 1, 3.0 );
		
		assertEquals( 1.0, m.get( 0, 1 ) );
		assertEquals( 2.0, m.get( 0, 2 ) );
		assertEquals( 3.0, m.get( 1, 2 ) );
	}
	
	public void testSwapSetters( )
	{
		DistanceMatrix m = new DistanceMatrix( 3 );
		
		m.set( 0, 1, 1.0 );
		m.set( 0, 2, 2.0 );
		m.set( 1, 2, 3.0 );

		assertEquals( 1.0, m.get( 1, 0 ) );
		assertEquals( 2.0, m.get( 2, 0 ) );
		assertEquals( 3.0, m.get( 2, 1 ) );
	}
	
	public void testInit( )
	{
		DistanceMatrix m = new DistanceMatrix( 5 );
		
		assertEquals( 0.0, m.get( 1, 0 ) );
		assertEquals( 0.0, m.get( 2, 0 ) );
		assertEquals( 0.0, m.get( 2, 1 ) );
		assertEquals( 0.0, m.get( 3, 0 ) );
		assertEquals( 0.0, m.get( 3, 1 ) );
		assertEquals( 0.0, m.get( 3, 2 ) );
		assertEquals( 0.0, m.get( 4, 0 ) );
		assertEquals( 0.0, m.get( 4, 1 ) );
		assertEquals( 0.0, m.get( 4, 2 ) );
		assertEquals( 0.0, m.get( 4, 3 ) );
	}
	
	public void testIterator( )
	{
		DistanceMatrix m = new DistanceMatrix( 4 );
		
		m.set( 1, 0, 1.0 );
		m.set( 2, 0, 2.0 );
		m.set( 2, 1, 3.0 );
		m.set( 3, 0, 4.0 );
		m.set( 3, 1, 5.0 );
		m.set( 3, 2, 6.0 );
		
		IndexPairIterator iter = new IndexPairIterator( m.getNumPoints() );
		assertTrue( iter.hasNext() );
		assertEquals( 1.0, m.get( iter.next() ) );
		assertTrue( iter.hasNext() );
		assertEquals( 2.0, m.get( iter.next() ) );
		assertTrue( iter.hasNext() );
		assertEquals( 3.0, m.get( iter.next() ) );
		assertTrue( iter.hasNext() );
		assertEquals( 4.0, m.get( iter.next() ) );
		assertTrue( iter.hasNext() );
		assertEquals( 5.0, m.get( iter.next() ) );
		assertTrue( iter.hasNext() );
		assertEquals( 6.0, m.get( iter.next() ) );
		assertFalse( iter.hasNext() );
	}
	
	public void testReadWrite( )
	throws Exception
	{
		DistanceMatrix m = new DistanceMatrix( 4 );
		
		m.set( 1, 0, 1.0 );
		m.set( 2, 0, 2.0 );
		m.set( 2, 1, 3.0 );
		m.set( 3, 0, 4.0 );
		m.set( 3, 1, 5.0 );
		m.set( 3, 2, 6.0 );
		
		DistanceMatrixWriter.write( DistanceMatrixPath, m );
		
		// check the file
		final int BytesPerDouble = 8;
		File file = new File( DistanceMatrixPath );
		assertEquals( BytesPerDouble * 6, file.length() );
		
		DistanceMatrix o = DistanceMatrixReader.read( DistanceMatrixPath );
		
		assertEquals( m, o );
		
		// cleanup
		file.delete();
	}
}
