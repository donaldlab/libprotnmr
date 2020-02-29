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

import java.util.Iterator;

public class IndexPairIterator implements Iterator<IndexPair>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_numIndices;
	private int m_iIndex;
	private int m_jIndex;
	private IndexPair m_next;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public IndexPairIterator( int numIndices )
	{
		// just in case...
		assert( numIndices > 1 );
		
		// save parameters
		m_numIndices = numIndices;
		
		// init defaults
		m_iIndex = 1;
		m_jIndex = 0;
		m_next = getNext();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_next != null;
	}
	
	public IndexPair next( )
	{
		IndexPair retval = m_next;
		m_next = getNext();
		return retval;
	}
	
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private IndexPair getNext( )
	{
		// short circuit check
		if( m_iIndex >= m_numIndices )
		{
			return null;
		}
		
		IndexPair pair = new IndexPair( m_iIndex, m_jIndex );
		
		m_jIndex++;
		
		// update the left index if needed
		if( m_jIndex >= m_iIndex )
		{
			m_iIndex++;
			m_jIndex = 0;
		}
		
		return pair;
	}
}
