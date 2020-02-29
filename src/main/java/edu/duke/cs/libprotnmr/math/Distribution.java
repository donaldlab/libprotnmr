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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Distribution<T extends Distribution.Value>
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static class DoubleValue implements Value
	{
		private double m_val;
		
		public DoubleValue( double val )
		{
			m_val = val;
		}
		
		@Override
		public double getValue( )
		{
			return m_val;
		}
		
		@Override
		public String toString( )
		{
			return Double.toString( m_val );
		}
	}
	
	public static interface Value
	{
		public abstract double getValue( );
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private TreeMap<T,Integer> m_index;
	private long m_count;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Distribution( )
	{
		m_index = new TreeMap<T,Integer>( new Comparator<T>( )
		{
			@Override
			public int compare( T a, T b )
			{
				return Double.compare( a.getValue(), b.getValue() );
			}
		} );
		m_count = 0;
	}
	
	public Distribution( Collection<T> values )
	{
		this();
		addAll( values );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public T getMin( )
	{
		return m_index.firstKey();
	}
	
	public T getMax( )
	{
		return m_index.lastKey();
	}
	
	public double getSum( )
	{
		double sum = 0.0;
		for( Entry<T,Integer> item : m_index.entrySet() )
		{
			sum += item.getKey().getValue()*item.getValue();
		}
		return sum;
	}
	
	public long getCount( )
	{
		return m_count;
	}
	
	public double getMean( )
	{
		return getSum()/m_count;
	}
	
	public double getMedian( )
	{
		if( m_count <= 0 )
		{
			return Double.NaN;
		}
		else if( m_count % 2 == 0 )
		{
			// for even-sized lists, average the two middle values
			double a = getNthValue( m_count/2-1 ).getValue();
			double b = getNthValue( m_count/2 ).getValue();
			return ( a + b )/2;
		}
		else
		{
			// otherwise, return the middle value
			return getNthValue( m_count/2 ).getValue();
		}
	}
	
	public double getVariance( )
	{
		double sum = 0.0;
		double mean = getMean();
		for( Entry<T,Integer> entry : m_index.entrySet() )
		{
			double diff = entry.getKey().getValue() - mean;
			sum += diff*diff*entry.getValue();
		}
		return sum/m_count;
	}
	
	public double getStdDev( )
	{
		return Math.sqrt( getVariance() );
	}
	
	public double getStdError( )
	{
		return getStdDev()/Math.sqrt( getCount() );
	}
	
	public int getCount( T val )
	{
		Integer count = m_index.get( val );
		if( count == null )
		{
			return 0;
		}
		return count;
	}
	
	public T getNthValue( long n )
	{
		if( n < 0 || n >= m_count )
		{
			throw new IndexOutOfBoundsException( "index: " + n + ", size: " + m_count );
		}
		
		Iterator<Entry<T,Integer>> iter = m_index.entrySet().iterator();
		T val = null;
		long count = 0;
		while( count <= n )
		{
			Entry<T,Integer> entry = iter.next();
			val = entry.getKey();
			count += entry.getValue();
		}
		return val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return toString( 8 );
	}
	
	public String toString( int precision )
	{
		String format = "%." + precision + "f";
		
		StringBuffer buf = new StringBuffer();
		buf.append( "[Distribution]" );
		buf.append( "\n\tcount:    " );
		buf.append( m_count );
		buf.append( "\n\tmin:      " );
		buf.append( String.format( format, getMin().getValue() ) );
		buf.append( "\n\tmax:      " );
		buf.append( String.format( format, getMax().getValue() ) );
		buf.append( "\n\tmean:     " );
		buf.append( String.format( format, getMean() ) );
		buf.append( "\n\tmedian:   " );
		buf.append( String.format( format, getMedian() ) );
		buf.append( "\n\tvariance: " );
		buf.append( String.format( format, getVariance() ) );
		buf.append( "\n\tstdDev:   " );
		buf.append( String.format( format, getStdDev() ) );
		buf.append( "\n\tstdError: " );
		buf.append( String.format( format, getStdError() ) );
		return buf.toString();
	}
	
	public void clear( )
	{
		m_index.clear();
		m_count = 0;
	}
	
	public void add( T val )
	{
		// entries are stored by unique key
		// duplicates increment a counter stored in the map value
		m_index.put( val, getCount( val ) + 1 );
		m_count++;
	}
	
	public void addAll( Iterable<T> values )
	{
		for( T value : values )
		{
			add( value );
		}
	}
}
