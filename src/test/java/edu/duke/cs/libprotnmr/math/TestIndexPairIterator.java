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
package edu.duke.cs.libprotnmr.math;

import edu.duke.cs.libprotnmr.ExtendedTestCase;

public class TestIndexPairIterator extends ExtendedTestCase
{
	public void testTwo( )
	{
		IndexPairIterator iter = new IndexPairIterator( 2 );
		
		assertTrue( iter.hasNext() );
		assertIndexPair( 1, 0, iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testThree( )
	{
		IndexPairIterator iter = new IndexPairIterator( 3 );
		
		assertTrue( iter.hasNext() );
		assertIndexPair( 1, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 1, iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testFour( )
	{
		IndexPairIterator iter = new IndexPairIterator( 4 );
		
		assertTrue( iter.hasNext() );
		assertIndexPair( 1, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 1, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 1, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 2, iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testFive( )
	{
		IndexPairIterator iter = new IndexPairIterator( 5 );
		
		assertTrue( iter.hasNext() );
		assertIndexPair( 1, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 2, 1, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 1, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 3, 2, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 4, 0, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 4, 1, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 4, 2, iter.next() );
		assertTrue( iter.hasNext() );
		assertIndexPair( 4, 3, iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testLarge( )
	{
		int[] tests = new int[] { 10, 30, 60 };
		for( int i=0; i<tests.length; i++ )
		{
			int numIndices = tests[i];
			IndexPairIterator iter = new IndexPairIterator( numIndices );
			int numExpectedPairs = ( numIndices * numIndices - numIndices ) / 2;
			assertNumPairs( numExpectedPairs, iter );
		}
	}
	
	private void assertIndexPair( int left, int right, IndexPair pair )
	{
		if( pair.left > pair.right )
		{
			assertEquals( left, pair.left );
			assertEquals( right, pair.right );
		}
		else if( pair.left < pair.right )
		{
			assertEquals( left, pair.right );
			assertEquals( right, pair.left );
		}
		else
		{
			fail();
		}
	}
	
	private void assertNumPairs( int expected, IndexPairIterator iter )
	{
		int observed = 0;
		while( iter.hasNext() )
		{
			iter.next();
			observed++;
		}
		assertEquals( expected, observed );
	}
}
