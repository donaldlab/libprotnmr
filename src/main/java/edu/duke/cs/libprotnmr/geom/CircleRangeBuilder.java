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

package edu.duke.cs.libprotnmr.geom;

import java.util.Iterator;
import java.util.TreeSet;


public class CircleRangeBuilder implements Iterable<CircleRange>
{
	// UNDONE: unit test the piss out of this class!!!
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private TreeSet<CircleRange> m_ranges; // all ranges here are non-overlapping
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public CircleRangeBuilder( )
	{
		m_ranges = new TreeSet<CircleRange>();
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public Iterator<CircleRange> iterator( )
	{
		return m_ranges.iterator();
	}
	
	public boolean isEmpty( )
	{
		return m_ranges.isEmpty();
	}
	
	public int getNumRanges( )
	{
		return m_ranges.size();
	}
	
	public void clear( )
	{
		m_ranges.clear();
	}
	
	public void add( CircleRange range )
	{
		// find the first intersecting range and merge the two
		CircleRange first = null;
		Iterator<CircleRange> iter = m_ranges.iterator();
		while( iter.hasNext() )
		{
			CircleRange other = iter.next();
			if( other.isIntersecting( range ) )
			{
				other.merge( range );
				first = other;
				break;
			}
		}
		
		// did we merge anything?
		if( first != null )
		{
			// is there anything else to intersect with?
			if( m_ranges.size() > 1 )
			{
				// UNDONE: get the last intersecting range (with wrap-around)
				
				// get the next range (wrap around if needed)
				CircleRange next = null;
				if( iter.hasNext() )
				{
					next = iter.next();
				}
				else
				{
					next = m_ranges.iterator().next();
				}
				
				// see if we intersect that range too
				if( first.isIntersecting( next ) )
				{
					first.merge( next );
					m_ranges.remove( next );
				}
			}
		}
		// otherwise, add it to the ranges
		else
		{
			m_ranges.add( range );
		}
	}
}
