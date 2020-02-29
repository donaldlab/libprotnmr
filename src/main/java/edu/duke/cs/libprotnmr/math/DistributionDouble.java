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
