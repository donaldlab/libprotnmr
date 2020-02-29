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

public class MultiAxisAlignedBox
{
	/**************************
	 *   Fields
	 **************************/
	
	public MultiVector min;
	public MultiVector max;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public MultiAxisAlignedBox( int dimension )
	{
		min = new MultiVectorImpl( dimension );
		max = new MultiVectorImpl( dimension );
	}
	
	public MultiAxisAlignedBox( MultiAxisAlignedBox other )
	{
		min = new MultiVectorImpl( other.min );
		max = new MultiVectorImpl( other.max );
	}
	
	public MultiAxisAlignedBox( MultiVector min, MultiVector max )
	{
		this.min = new MultiVectorImpl( min );
		this.max = new MultiVectorImpl( max );
	}
	
	public MultiAxisAlignedBox( MultiVector point )
	{
		this.min = new MultiVectorImpl( point );
		this.max = new MultiVectorImpl( point );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "[MultiAxisAlignedBox] min=" + min + ", max=" + max;
	}
	
	public MultiVector getDeltas( )
	{
		MultiVector deltas = new MultiVectorImpl( max );
		deltas.subtract( min );
		return deltas;
	}
}
