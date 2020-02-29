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

public class DistributionDouble extends Distribution<Distribution.DoubleValue>
{
	/**************************
	 *   Constructors
	 **************************/
	
	public DistributionDouble( )
	{
		super();
	}
	
	public DistributionDouble( Collection<Double> values )
	{
		super();
		addAll( values );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getMinDouble( )
	{
		return getMin().getValue();
	}
	
	public double getMaxDouble( )
	{
		return getMax().getValue();
	}
	
	public int getCount( double val )
	{
		return getCount( new Distribution.DoubleValue( val ) );
	}
	
	public double getNthValueDouble( int n )
	{
		return getNthValue( n ).getValue();
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static DistributionDouble newFromIntegers( Collection<Integer> values )
	{
		DistributionDouble dist = new DistributionDouble();
		dist.addAllIntegers( values );
		return dist;
	}
	
	public static DistributionDouble newFromRadians( Collection<Double> values )
	{
		DistributionDouble dist = new DistributionDouble();
		for( double val : values )
		{
			dist.add( Math.toDegrees( val ) );
		}
		return dist;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void add( double val )
	{
		add( new Distribution.DoubleValue( val ) );
	}
	
	public void addAll( Collection<Double> values )
	{
		for( Double val : values )
		{
			add( val );
		}
	}
	
	public void addAllIntegers( Collection<Integer> values )
	{
		for( Integer val : values )
		{
			add( val );
		}
	}
}
