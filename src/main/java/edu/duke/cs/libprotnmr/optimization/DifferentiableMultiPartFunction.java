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

package edu.duke.cs.libprotnmr.optimization;

public abstract class DifferentiableMultiPartFunction
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_numParts;
	private int m_maxNumOptima;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	protected DifferentiableMultiPartFunction( int numParts, int maxNumOptima )
	{
		m_numParts = numParts;
		m_maxNumOptima = maxNumOptima;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getNumParts( )
	{
		return m_numParts;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public abstract double getValue( double t, int part );
	public abstract double getDerivative( double t, int part );
	
	public DifferentiableFunction getPart( final int part )
	{
		return new DifferentiableFunction( )
		{
			@Override
			public double getValue( double t )
			{
				return DifferentiableMultiPartFunction.this.getValue( t, part );
			}
			
			@Override
			public double getDerivative( double t )
			{
				return DifferentiableMultiPartFunction.this.getDerivative( t, part );
			}
			
			@Override
			public int getMaxNumOptima( )
			{
				return DifferentiableMultiPartFunction.this.m_maxNumOptima;
			}
		};
	}
}
