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

/*
 * Copyright 2003-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package edu.duke.cs.libprotnmr.dataStructures;

import java.util.ArrayList;
import java.util.Comparator;

/* Jeff: 06/08/2009
	This class is based on the java sources for PriorityQueue.
	I couldn't use the stock data structure because it implements a
	linear-time remove() method! Sadly, that really slows down my
	clustering implementation. This min heap has a logn remove() method.
*/
public class MinHeap<E>
{
	/**************************
	 *   Definitions
	 **************************/
	
    private static final int DefaultCapacity = 11;
    
    
	/**************************
	 *   Data Members
	 **************************/
	
    protected ArrayList<MinHeapNode<E>> m_nodes;
    protected Comparator<E> m_comparator;
    
    
	/**************************
	 *   Constructors
	 **************************/
    
    public MinHeap( )
	{
		this( new Comparator<E>( )
		{
			@Override
			@SuppressWarnings( "unchecked" )
			public int compare( E a, E b )
			{
				// compare them using the natural order
				return ((Comparable<? super E>)a).compareTo( b );
			}
		}, DefaultCapacity );
	}
	
	public MinHeap( Comparator<E> comparator )
	{
		this( comparator, DefaultCapacity );
	}
	
	public MinHeap( Comparator<E> comparator, int capacity )
	{
        m_nodes = new ArrayList<MinHeapNode<E>>( capacity );
        m_comparator = comparator;
    }
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public MinHeapNode<E> add( E e )
	{
    	// just in case...
    	if( e == null )
    	{
    		throw new NullPointerException();
    	}
    	
    	MinHeapNode<E> node = new MinHeapNode<E>( e );
    	
    	// add it to the end of the heap
    	node.setIndex( m_nodes.size() );
    	m_nodes.add( node );
    	
    	// re-heapify
    	if( m_nodes.size() > 1 )
    	{
    		siftUp( node );
    	}
    	
    	return node;
    }
	
	public E getMin( )
	{
		if( m_nodes.isEmpty() )
		{
			return null;
		}
		
		return m_nodes.get( 0 ).getData();
	}
	
	public void remove( MinHeapNode<E> node )
	{
		int i = node.getIndex();
		
		// just in case...
		assert( i >= 0 && i < m_nodes.size() );
		assert( m_nodes.get( i ) == node );
		
		remove( i );
	}
	
	public void remove( int i )
	{
		// remove the last node
		int lastIndex = m_nodes.size() - 1;
		MinHeapNode<E> lastNode = m_nodes.remove( lastIndex );
		
		// we're done if we actually wanted to remove the last node
		if( i == lastIndex )
		{
			return;
		}
		
		// sift down the last node from i
		lastNode.setIndex( i );
		siftDown( lastNode );
		
		// if that didn't work, try sifting up from i
		if( m_nodes.get( i ) == lastNode )
		{
			lastNode.setIndex( i );
			siftUp( lastNode );
		}
	}
	
	public int size( )
	{
		return m_nodes.size();
	}
	
	public void clear( ) 
	{
		m_nodes.clear();
    }
	
	public boolean isEmpty( )
	{
		return m_nodes.isEmpty();
	}
	
	public E extractMin( )
    {
    	// just in case...
        if( m_nodes.isEmpty() )
        {
        	return null;
        }
        
        // get the head node
        MinHeapNode<E> minNode = m_nodes.get( 0 );
        
        // remove the last node
        int lastIndex = m_nodes.size() - 1;
    	MinHeapNode<E> lastNode = m_nodes.remove( lastIndex );
    	
        // sift down the last node from the root if needed
        if( lastIndex > 0 )
        {
        	lastNode.setIndex( 0 );
        	siftDown( lastNode );
        }
        
        return minNode.getData();
    }
    
	
	/**************************
	 *   Functions
	 **************************/
	
	private void siftUp( MinHeapNode<E> node )
	{
    	// get the info from node
    	int i = node.getIndex();
		
		while( i > 0 )
		{
			// get the parent
			int parentIndex = ( i - 1 ) >>> 1;
			MinHeapNode<E> parentNode = m_nodes.get( parentIndex );
			
			// if node >= parent, stop
			if( compareNodes( node, parentNode ) >= 0 )
			{
				break;
			}
			
			// move parent down to this level
			m_nodes.set( i, parentNode );
			parentNode.setIndex( i );
			
			// move up a level
			i = parentIndex;
		}
		
		// put the node at this level
		m_nodes.set( i, node );
		node.setIndex( i );
	}
	
	private void siftDown( MinHeapNode<E> node )
    {
    	// get the info from node
    	int i = node.getIndex();
		
		// while not on a leaf...
		int half = m_nodes.size() >>> 1;
		while( i < half )
		{
			// get left child of node i
			int leftIndex = ( i << 1 ) + 1;
			MinHeapNode<E> leftNode = m_nodes.get( leftIndex );
			
			// until we hear otherwise, use the left node for comparisons
			MinHeapNode<E> compareNode = leftNode;
			int compareIndex = leftIndex;
			
			// is there a right node?
			int rightIndex = leftIndex + 1;
			if( rightIndex < m_nodes.size() )
			{
				MinHeapNode<E> rightNode = m_nodes.get( rightIndex );
				
				// should we compare with it?
				if( compareNodes( leftNode, rightNode ) > 0 )
				{
					compareNode = rightNode;
					compareIndex = rightIndex;
				}
			}
			
			// if e is lte compare node, stop sifting down
			if( compareNodes( node, leftNode ) <= 0 )
			{
				break;
			}
			
			// move compare node up a level
			m_nodes.set( i, compareNode );
			compareNode.setIndex( i );
			
			// go down a level
			i = compareIndex;
		}
		
		// move the node to this level
		m_nodes.set( i, node );
		node.setIndex( i );
    }
    
	private int compareNodes( MinHeapNode<E> leftNode, MinHeapNode<E> rightNode )
    {
   		return m_comparator.compare( leftNode.getData(), rightNode.getData() );
    }
}
