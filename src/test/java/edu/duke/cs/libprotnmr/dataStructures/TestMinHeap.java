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

package edu.duke.cs.libprotnmr.dataStructures;

import edu.duke.cs.libprotnmr.ExtendedTestCase;

public class TestMinHeap extends ExtendedTestCase
{
	private class MinHeapPublisher<E> extends MinHeap<E>
	{
		public void assertIndices( )
		{
			for( int i=0; i<m_nodes.size(); i++ )
			{
				assertEquals( i, m_nodes.get( i ).getIndex() );
			}
		}
	}
	
	public void testAdd( )
	{
		MinHeapPublisher<Integer> heap = new MinHeapPublisher<Integer>();
		
		assertTrue( heap.isEmpty() );
		assertEquals( 0, heap.size() );
		assertNull( heap.getMin() );
		heap.assertIndices();
		
		heap.add( 4 );
		assertFalse( heap.isEmpty() );
		assertEquals( 1, heap.size() );
		assertEquals( new Integer( 4 ), heap.getMin() );
		heap.assertIndices();
		
		heap.add( 2 );
		assertFalse( heap.isEmpty() );
		assertEquals( 2, heap.size() );
		assertEquals( new Integer( 2 ), heap.getMin() );
		heap.assertIndices();
		
		heap.add( 3 );
		assertFalse( heap.isEmpty() );
		assertEquals( 3, heap.size() );
		assertEquals( new Integer( 2 ), heap.getMin() );
		heap.assertIndices();
		
		heap.add( 1 );
		assertFalse( heap.isEmpty() );
		assertEquals( 4, heap.size() );
		assertEquals( new Integer( 1 ), heap.getMin() );
		heap.assertIndices();
	}
	
	public void testRemove( )
	{
		MinHeapPublisher<Integer> heap = new MinHeapPublisher<Integer>();
		
		MinHeapNode<Integer> nodeA = heap.add( 5 );
		MinHeapNode<Integer> nodeB = heap.add( 1 );
		MinHeapNode<Integer> nodeC = heap.add( 3 );
		MinHeapNode<Integer> nodeD = heap.add( 2 );
		MinHeapNode<Integer> nodeE = heap.add( 4 );
		
		heap.remove( nodeA );
		assertFalse( heap.isEmpty() );
		assertEquals( 4, heap.size() );
		assertEquals( new Integer( 1 ), heap.getMin() );
		heap.assertIndices();
		
		heap.remove( nodeB );
		assertFalse( heap.isEmpty() );
		assertEquals( 3, heap.size() );
		assertEquals( new Integer( 2 ), heap.getMin() );
		heap.assertIndices();
		
		heap.remove( nodeC );
		assertFalse( heap.isEmpty() );
		assertEquals( 2, heap.size() );
		assertEquals( new Integer( 2 ), heap.getMin() );
		heap.assertIndices();
		
		heap.remove( nodeD );
		assertFalse( heap.isEmpty() );
		assertEquals( 1, heap.size() );
		assertEquals( new Integer( 4 ), heap.getMin() );
		heap.assertIndices();
		
		heap.remove( nodeE );
		assertTrue( heap.isEmpty() );
		assertEquals( 0, heap.size() );
		assertNull( heap.getMin() );
		heap.assertIndices();
	}
	
	public void testExtractMin( )
	{
		MinHeapPublisher<Integer> heap = new MinHeapPublisher<Integer>();
		
		heap.add( 5 );
		heap.add( 1 );
		heap.add( 3 );
		heap.add( 2 );
		heap.add( 4 );
		
		assertEquals( new Integer( 1 ), heap.extractMin() );
		assertFalse( heap.isEmpty() );
		assertEquals( 4, heap.size() );
		heap.assertIndices();
		
		assertEquals( new Integer( 2 ), heap.extractMin() );
		assertFalse( heap.isEmpty() );
		assertEquals( 3, heap.size() );
		heap.assertIndices();
		
		assertEquals( new Integer( 3 ), heap.extractMin() );
		assertFalse( heap.isEmpty() );
		assertEquals( 2, heap.size() );
		heap.assertIndices();
		
		assertEquals( new Integer( 4 ), heap.extractMin() );
		assertFalse( heap.isEmpty() );
		assertEquals( 1, heap.size() );
		heap.assertIndices();
		
		assertEquals( new Integer( 5 ), heap.extractMin() );
		assertTrue( heap.isEmpty() );
		assertEquals( 0, heap.size() );
		heap.assertIndices();
	}
}
