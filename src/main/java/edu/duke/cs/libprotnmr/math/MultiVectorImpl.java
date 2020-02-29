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

import java.util.Collection;

public class MultiVectorImpl implements MultiVector
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double[] m_data;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public MultiVectorImpl( int dimension )
	{
		// check the dimension
		if( dimension <= 0 )
		{
			throw new IllegalArgumentException();
		}
		
		// allocate the data and set to zero
		m_data = new double[dimension];
		for( int i=0; i<dimension; i++ )
		{
			m_data[i] = 0.0;
		}
	}
	
	public MultiVectorImpl( MultiVector other )
	{
		m_data = new double[other.getDimension()];
		for( int i=0; i<other.getDimension(); i++ )
		{
			m_data[i] = other.get( i );
		}
	}
	
	public MultiVectorImpl( double ... data )
	{
		m_data = data;
	}
	
	public MultiVectorImpl( Collection<Double> data )
	{
		m_data = new double[data.size()];
		int i = 0;
		for( Double d : data )
		{
			m_data[i++] = d;
		}
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double get( int i )
	{
		// NOTE: no bounds checking here
		return m_data[i];
	}
	
	public void set( int i, double value )
	{
		// no bounds checking again
		m_data[i] = value;
	}
	
	public int getDimension( )
	{
		return m_data.length;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( "(" );
		for( int i=0; i<m_data.length; i++ )
		{
			if( i > 0 )
			{
				buf.append( "," );
			}
			buf.append( m_data[i] );
		}
		buf.append( ")" );
		return buf.toString();
	}
	
	public void add( MultiVector other )
	{
		for( int i=0; i<m_data.length; i++ )
		{
			m_data[i] += other.get( i );
		}
	}
	
	public void subtract( MultiVector other )
	{
		for( int i=0; i<m_data.length; i++ )
		{
			m_data[i] -= other.get( i );
		}
	}
	
	public double getLengthSquared( )
	{
		double lengthSq = 0.0;
		
		for( int i=0; i<m_data.length; i++ )
		{
			lengthSq += m_data[i] * m_data[i];
		}
		
		return lengthSq;
	}
	
	public double getLength( )
	{
		return Math.sqrt( getLengthSquared() );
	}
	
	public double getDistanceSquared( MultiVector other )
	{
		// make sure the vectors are of the same length
		if( m_data.length != other.getDimension() )
		{
			throw new IllegalArgumentException();
		}
		
		double distSq = 0.0;
		double diff = 0.0;
		
		for( int i=0; i<m_data.length; i++ )
		{
			diff = m_data[i] - other.get( i );
			distSq += diff * diff;
		}
		
		return distSq;
	}
	
	public double getDistance( MultiVector other )
	{
		return Math.sqrt( getDistanceSquared( other ) );
	}
	
	public double getDot( MultiVector other )
	{
		// make sure the vectors are of the same length
		if( m_data.length != other.getDimension() )
		{
			throw new IllegalArgumentException();
		}
		
		double sum = 0.0;
		for( int i=0; i<m_data.length; i++ )
		{
			sum += m_data[i]*other.get( i );
		}
		return sum;
	}
	
	public void normalize( )
	{
		double length = getLength();
		for( int i=0; i<m_data.length; i++ )
		{
			m_data[i] /= length;
		}
	}
}
