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
package edu.duke.cs.libprotnmr.geom;

public class Circle
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final boolean DefaultIsClockwise = true;
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public Vector2 center;
	public double radius;
	public boolean isClockwise;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Circle( )
	{
		this( new Vector2(), 0.0 );
	}
	
	public Circle( Vector2 center, double radius )
	{
		this( center, radius, DefaultIsClockwise );
	}
	
	public Circle( Vector2 center, double radius, boolean isClockwise )
	{
		this.center = new Vector2();
		set( center, radius, isClockwise );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( Vector2 center, double radius )
	{
		this.center.set( center );
		this.radius = radius;
	}
	
	public void set( Vector2 center, double radius, boolean isClockwise )
	{
		this.center.set( center );
		this.radius = radius;
		this.isClockwise = isClockwise;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "( " + center.toString() + ", " + radius + " )";
	}

	public Vector2 getPointClosestTo( Vector2 q )
	{
		// handle degenerate circles
		if( radius <= 0 )
		{
			return center;
		}
		
		// construct a (normalized) vector from the circle center to the query point
		Vector2 centerToQ = new Vector2( q );
		centerToQ.subtract( center );
		centerToQ.normalize();
		
		// return the circle point
		Vector2 point = new Vector2( center );
		centerToQ.scale( radius );
		point.add( centerToQ );
		return point;
	}
	
	public Vector2 getPointFarthestFrom( Vector2 q )
	{
		// handle degenerate circles
		if( radius <= 0 )
		{
			return center;
		}
		
		// construct a (normalized) vector from the query point to the circle center
		Vector2 qToCenter = new Vector2( center );
		qToCenter.subtract( q );
		qToCenter.normalize();
		
		// return the circle point
		Vector2 point = new Vector2( center );
		qToCenter.scale( radius );
		point.add( qToCenter );
		return point;
	}
	
	public boolean equals( Circle other )
	{
		return center.equals( other.center ) && radius == other.radius && isClockwise == other.isClockwise;
	}
}
