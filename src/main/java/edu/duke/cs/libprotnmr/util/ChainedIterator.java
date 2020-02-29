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
