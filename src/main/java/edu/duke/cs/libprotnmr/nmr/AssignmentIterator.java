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
package edu.duke.cs.libprotnmr.nmr;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.duke.cs.libprotnmr.protein.AtomAddress;


public class AssignmentIterator<T extends AtomAddress<T>> implements Iterator<Assignment<T>>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private AssignmentSource<T> m_source;
	private Assignment<T> m_next;
	private Iterator<T> m_iterLeft;
	private T m_left;
	private Iterator<T> m_iterRight;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AssignmentIterator( AssignmentSource<T> source )
	{
		// save parameters
		m_source = source;
		
		// init defaults
		m_iterLeft = source.getLefts().iterator();
		m_left = null;
		m_iterRight = null;
		
		m_next = getNext();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_next != null;
	}
	
	public Assignment<T> next( )
	{
		if( m_next == null )
		{
			throw new NoSuchElementException();
		}
		
		Assignment<T> temp = m_next;
		m_next = getNext();
		return temp;
	}
	
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private Assignment<T> getNext( )
	{
		// get a new left if needed
		if( m_left == null )
		{
			if( m_iterLeft.hasNext() )
			{
				m_left = m_iterLeft.next();
			}
			else
			{
				return null;
			}
		}
		
		// get a new right iter if needed
		if( m_iterRight == null )
		{
			m_iterRight = m_source.getRights().iterator();
		}
		
		// get a new right
		if( !m_iterRight.hasNext() )
		{
			return null;
		}
		T left = m_left;
		T right = m_iterRight.next();
		
		// invalidate right iter if needed
		if( !m_iterRight.hasNext() )
		{
			m_iterRight = null;
			m_left = null;
		}
		
		return new Assignment<T>( left, right );
	}
}
