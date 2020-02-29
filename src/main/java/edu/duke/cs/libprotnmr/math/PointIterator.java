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

public class PointIterator implements Iterator<double[]>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_dimension;
	private long[] m_indices;
	private int[] m_counts;
	private double[] m_mins;
	private double[] m_maxs;
	private long m_numTotal;
	private long m_numServed;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public PointIterator( int dimension, int[] counts, double[] mins, double[] maxs )
	{
		// save parameters
		m_dimension = dimension;
		m_counts = counts;
		m_mins = mins;
		m_maxs = maxs;
		
		m_indices = new long[m_dimension];
		for( int i=0; i<m_dimension; i++ )
		{
			m_indices[i] = 0;
		}
		
		m_numTotal = 1;
		for( int i=0; i<m_dimension; i++ )
		{
			m_numTotal *= m_counts[i];
		}
		m_numServed = 0;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean hasNext( )
	{
		boolean val = true;
		
		for( int i=0; i<m_dimension; i++ )
		{
			val = val && ( m_indices[i] < m_counts[i] );
		}
		
		return val;
	}
	
	@Override
	public double[] next( )
	{
		// get the current point
		double[] result = new double[m_dimension];
		for( int i=0; i<m_dimension; i++ )
		{
			result[i] = scaleRange( m_indices[i], m_counts[i], m_mins[i], m_maxs[i] );
		}
		
		// overflow check
		boolean overflow = true;
		
		// increment the indices
		for( int i=m_dimension-1; i>=0; i-- )
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
		
		return result;
	}
	
	@Override
	public void remove( )
	{
		throw new UnsupportedOperationException();
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
	
	private double scaleRange( long current, long count, double min, double max )
	{
		if( count < 2 )
		{
			return min;
		}
		return (double)current / (double)( count - 1 ) * ( max - min ) + min;
	}
}
