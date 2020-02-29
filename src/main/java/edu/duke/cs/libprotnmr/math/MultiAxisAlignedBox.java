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
