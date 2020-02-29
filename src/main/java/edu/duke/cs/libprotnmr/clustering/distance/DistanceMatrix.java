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

package edu.duke.cs.libprotnmr.clustering.distance;

import edu.duke.cs.libprotnmr.math.IndexPair;

public class DistanceMatrix
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double[][] m_distances;
	private long m_numDistances;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public DistanceMatrix( int numPoints )
	{
		if( numPoints < 2 )
		{
			throw new IllegalArgumentException( "Can only compute a distance matrix for two or more points!" );
		}
		
		m_distances = new double[numPoints-1][];
		for( int i=0; i<numPoints-1; i++ )
		{
			m_distances[i] = new double[i+1];
			
			// initialize to zero
			for( int j=0; j<i; j++ )
			{
				m_distances[i][j] = 0.0;
			}
		}
		
		m_numDistances = getSize( numPoints );
	}
	
	public DistanceMatrix( DistanceMatrix other )
	{
		this( other.getNumPoints() );
		for( int i=0; i<other.getNumPoints(); i++ )
		{
			for( int j=0; j<i; j++ )
			{
				set( i, j, other.get( i, j ) );
			}
		}
		m_numDistances = other.m_numDistances;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double get( IndexPair pair )
	{
		return get( pair.left, pair.right );
	}
	
	public double get( int r, int c )
	{
		if( r == c )
		{
			return 0;
		}
		
		// make sure we're in the right triangle
		if( r < c )
		{
			int swap = r;
			r = c;
			c = swap;
		}
		
		// map to the compacted matrix
		r -= 1;
		
		return m_distances[r][c];
	}
	
	public void set( IndexPair pair, double val )
	{
		set( pair.left, pair.right, val );
	}
	
	public void set( int r, int c, double val )
	{
		if( r == c )
		{
			throw new IllegalArgumentException( "Cannot not set self distances. They must always be zero!" );
		}
		
		// make sure we're in the right triangle
		if( r < c )
		{
			int swap = r;
			r = c;
			c = swap;
		}
		
		// map to the compacted matrix
		r -= 1;
		
		m_distances[r][c] = val;
	}
	
	public int getNumPoints( )
	{
		return m_distances.length + 1;
	}
	
	public long getNumDistances( )
	{
		return m_numDistances;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static long getSize( int numPoints )
	{
		return (long)numPoints * ( (long)numPoints - 1L ) / 2L;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		StringBuilder buf = new StringBuilder();
		
		final int CellSize = 8;
		final String intFormat = "%" + CellSize + "d";
		final String floatFormat = "%" + CellSize + "." + (CellSize-6) + "e";
		
		// add the top axis
		for( int i=0; i<CellSize; i++ )
		{
			buf.append( " " );
		}
		for( int i=0; i<m_distances.length-1; i++ )
		{
			buf.append( " " );
			buf.append( String.format( intFormat, i ) );
		}
		buf.append( "\n" );
		
		// add the distances
		for( int i=1; i<m_distances.length; i++ )
		{
			buf.append( String.format( intFormat, i ) );
			for( int j=0; j<i; j++ )
			{
				buf.append( " " );
				buf.append( String.format( floatFormat, get( i, j ) ) );
			}
			buf.append( "\n" );
		}
		
		return buf.toString();
	}
}
