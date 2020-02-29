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
package edu.duke.cs.libprotnmr.protein;

public class ResidueRange implements Comparable<ResidueRange>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_startNumber;
	private int m_stopNumber;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ResidueRange( int startId, int stopId )
	{
		m_startNumber = startId;
		m_stopNumber = stopId;
	}

	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getStartNumber( )
	{
		return m_startNumber;
	}
	public void setStartNumber( int val )
	{
		m_startNumber = val;
	}

	public int getStopNumber( )
	{
		return m_stopNumber;
	}
	public void setStopNumber( int val )
	{
		m_stopNumber = val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return "[" + m_startNumber + "," + m_stopNumber + "]";
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof ResidueRange )
		{
			return equals( (ResidueRange)other );
		}
		return false;
	}
	
	public boolean equals( ResidueRange other )
	{
		return m_startNumber == other.m_startNumber && m_stopNumber == other.m_stopNumber;
	}

	@Override
	public int compareTo( ResidueRange other )
	{
		if( m_startNumber != other.m_startNumber )
		{
			return m_startNumber - other.m_startNumber;
		}
		
		return m_stopNumber - other.m_stopNumber;
	}
	
	public boolean contains( int residueId )
	{
		return m_startNumber <= residueId && residueId <= m_stopNumber;
	}
	
	public int getLength( )
	{
		return m_stopNumber - m_startNumber + 1;
	}
}
