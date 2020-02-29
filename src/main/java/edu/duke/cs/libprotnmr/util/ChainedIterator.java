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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ChainedIterator<Item> implements Iterator<Item>
{
	/**************************
	 *   Data Memebers
	 **************************/
	
	private List<Iterator<Item>> m_iterators;
	private int m_currentIterator;
	private Item m_next;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ChainedIterator( List<Iterator<Item>> iterators )
	{
		m_iterators = iterators;
		m_currentIterator = 0;
		
		// grab the first item
		m_next = getNextItem();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_next != null;
	}

	public Item next( )
	{
		if( m_next == null )
		{
			throw new NoSuchElementException();
		}
		
		// get the next next item
		Item item = m_next;
		m_next = getNextItem();
		
		return item;
	}

	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private Item getNextItem( )
	{
		Item item = null;
		
		// while we have a current iterator...
		while( m_iterators != null && m_currentIterator < m_iterators.size() )
		{
			// check the current iterator for an item
			Iterator<Item> currentIterator = m_iterators.get( m_currentIterator );
			if( currentIterator != null && currentIterator.hasNext() )
			{
				item = currentIterator.next();
				break;
			}
			else
			{
				// move to the next iterator
				m_currentIterator++;
			}
		}
		
		return item;
	}
}
