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
