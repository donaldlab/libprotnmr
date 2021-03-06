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

package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.optimization.DifferentiableFunction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class PreconditionedFunction implements DifferentiableFunction, Serializable
{
	private static final long serialVersionUID = -1634038680884751671L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private int m_maxNumOptima;
	private transient Double m_scale;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	protected PreconditionedFunction( int maxNumOptima )
	{
		m_maxNumOptima = maxNumOptima;
		m_scale = null;
	}
	
	private void readObject( ObjectInputStream in )
    throws ClassNotFoundException, IOException
    {
		in.defaultReadObject();
		m_scale = null;
    }
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	@Override
	public int getMaxNumOptima( )
	{
		return m_maxNumOptima;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public abstract double getUnconditionedValue( double theta );
	public abstract double getUnconditionedDerivative( double theta );
	
	@Override
	public double getValue( double theta )
	{
		return condition( getUnconditionedValue( theta ) );
	}
	
	@Override
	public double getDerivative( double theta )
	{
		return condition( getUnconditionedDerivative( theta ) );
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private double condition( double val )
	{
		// compute the scale if needed
		if( m_scale == null )
		{
			m_scale = computeScale();
		}
		return m_scale * relax( val );
	}
	
	private double computeScale( )
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for( int t=0; t<128; t++ )
		{
			double derivative = relax( getUnconditionedDerivative( t ) );
			min = Math.min( min, derivative );
			max = Math.max( max, derivative );
		}
		return 1.0 / ( max - min );
	}
	
	private double relax( double in )
	{
		if( in > 0.0 )
		{
			return Math.log( in + 1.0 );
		}
		else if( in < 0.0 )
		{
			return -Math.log( -in + 1.0 );
		}
		return in;
	}
}
