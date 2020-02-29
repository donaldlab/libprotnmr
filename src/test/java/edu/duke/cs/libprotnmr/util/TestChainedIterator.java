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

package edu.duke.cs.libprotnmr.util;

import java.util.ArrayList;
import java.util.Iterator;

import edu.duke.cs.libprotnmr.ExtendedTestCase;


public class TestChainedIterator extends ExtendedTestCase
{
	public void testSingle( )
	{
		ArrayList<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
		iters.add( getNumbers( 1, 10 ).iterator() );
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( iters );
		
		int i = 1;
		while( iter.hasNext() )
		{
			assertEquals( i++, iter.next().intValue() );
		}
		
		assertEquals( 11, i );
	}
	
	public void testMultiple( )
	{
		ArrayList<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
		iters.add( getNumbers( 1, 10 ).iterator() );
		iters.add( getNumbers( 11, 20 ).iterator() );
		iters.add( getNumbers( 21, 30 ).iterator() );
		iters.add( getNumbers( 31, 40 ).iterator() );
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( iters );
		
		int i = 1;
		while( iter.hasNext() )
		{
			assertEquals( i++, iter.next().intValue() );
		}
		
		assertEquals( 41, i );
	}
	
	public void testMultipleDifferentLengths( )
	{
		ArrayList<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
		iters.add( getNumbers( 1, 3 ).iterator() );
		iters.add( getNumbers( 4, 9 ).iterator() );
		iters.add( getNumbers( 10, 11 ).iterator() );
		iters.add( getNumbers( 12, 15 ).iterator() );
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( iters );
		
		int i = 1;
		while( iter.hasNext() )
		{
			assertEquals( i++, iter.next().intValue() );
		}
		
		assertEquals( 16, i );
	}
	
	public void testNull( )
	{
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( null );
		assertFalse( iter.hasNext() );
	}
	
	public void testNullsInChain( )
	{
		ArrayList<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
		iters.add( getNumbers( 1, 3 ).iterator() );
		iters.add( null );
		iters.add( getNumbers( 4, 6 ).iterator() );
		iters.add( null );
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( iters );
		
		int i = 1;
		while( iter.hasNext() )
		{
			assertEquals( i++, iter.next().intValue() );
		}
		
		assertEquals( 7, i );
	}
	
	public void testEmptysInChain( )
	{
		ArrayList<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
		iters.add( getNumbers( 1, 3 ).iterator() );
		iters.add( getEmpty().iterator() );
		iters.add( getNumbers( 4, 6 ).iterator() );
		iters.add( getEmpty().iterator() );
		ChainedIterator<Integer> iter = new ChainedIterator<Integer>( iters );
		
		int i = 1;
		while( iter.hasNext() )
		{
			assertEquals( i++, iter.next().intValue() );
		}
		
		assertEquals( 7, i );
	}

	private ArrayList<Integer> getNumbers( int first, int last )
	{
		ArrayList<Integer> numbers = new ArrayList<Integer>( last - first + 1 );
		for( int i=first; i<=last; i++ )
		{
			numbers.add( i );
		}
		return numbers;
	}
	
	private ArrayList<Integer> getEmpty( )
	{
		return new ArrayList<Integer>();
	}
}
