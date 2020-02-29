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

public class PointIteratorDelta
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_dimensions;
	private int[] m_indices;
	private int[] m_counts;
	private double[] m_center;
	private double[] m_deltas;
	private double[] m_maxs;
	private long m_numTotal;
	private long m_numServed;
	private double[] m_point;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public PointIteratorDelta( int dimensions, double[] center, double[] deltas, double[] maxs )
	{
		// save parameters
		m_dimensions = dimensions;
		m_center = center;
		m_deltas = deltas;
		m_maxs = maxs;
		
		// init the index counters
		m_indices = new int[m_dimensions];
		for( int i=0; i<m_dimensions; i++ )
		{
			m_indices[i] = 0;
		}
		
		// count the total number of points to sample
		m_counts = new int[m_dimensions];
		m_numTotal = 1;
		for( int i=0; i<m_dimensions; i++ )
		{
			m_counts[i] = 1 + 2 * (int)Math.floor( maxs[i] / deltas[i] );
			m_numTotal *= m_counts[i];
		}
		m_numServed = 0;
		
		m_point = new double[m_dimensions];
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		boolean val = true;
		for( int i=0; i<m_dimensions; i++ )
		{
			val = val && ( m_indices[i] < m_counts[i] );
		}
		return val;
	}
	
	public double[] next( )
	{
		// get the current point
		for( int i=0; i<m_dimensions; i++ )
		{
			m_point[i] = getValue( m_indices[i], m_counts[i], m_center[i], m_deltas[i], m_maxs[i] );
		}
		
		// overflow check
		boolean overflow = true;
		
		// increment the indices
		for( int i=m_dimensions-1; i>=0; i-- )
		{
			m_indices[i]++;
			
			if( m_indices[i] < m_counts[i] )
			{
				overflow = false;
				break;
			}
			else
			{
				m_indices[i] = 0;
			}
		}
		
		// handle the overflow
		if( overflow )
		{
			m_indices[0] = m_counts[0];
		}
		
		// increment the served counter
		m_numServed++;
		
		return m_point;
	}
	
	public long getNumTotal( )
	{
		return m_numTotal;
	}
	
	public long getNumServed( )
	{
		return m_numServed;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private double getValue( int index, long numPoints, double center, double delta, double max )
	{
		if( index == 0 )
		{
			return center;
		}
		else
		{
			int sign = ( index % 2 ) * 2 - 1;
			return sign * ( ( index + 1 ) / 2 ) * delta + center;
		}
	}
}
