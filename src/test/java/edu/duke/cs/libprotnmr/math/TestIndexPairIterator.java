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
