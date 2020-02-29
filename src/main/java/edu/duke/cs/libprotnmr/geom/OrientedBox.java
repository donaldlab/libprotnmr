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

import edu.duke.cs.libprotnmr.math.Matrix3;

public class OrientedBox
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static final int NumCorners = (int)Math.pow( 2, Vector3.Dimension );
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public Vector3 x;
	public Vector3 y;
	public Vector3 z;
	public Vector3 min;
	public Vector3 max;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public OrientedBox( )
	{
		x = Vector3.getUnitX();
		y = Vector3.getUnitY();
		z = Vector3.getUnitZ();
		min = new Vector3();
		max = new Vector3();
	}
	
	public OrientedBox( OrientedBox other )
	{
		x = new Vector3( other.x );
		y = new Vector3( other.y );
		z = new Vector3( other.z );
		min = new Vector3( other.min );
		max = new Vector3( other.max );
	}
	
	public OrientedBox( Vector3 x, Vector3 y, Vector3 z, Vector3 min, Vector3 max )
	{
		this.x = new Vector3( x );
		this.y = new Vector3( y );
		this.z = new Vector3( z );
		this.min = new Vector3( min );
		this.max = new Vector3( max );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void setDegenerate( )
	{
		Vector3.getUnitX( x );
		Vector3.getUnitY( y );
		Vector3.getUnitZ( z );
		Vector3.getOrigin( min );
		Vector3.getOrigin( max );
	}
	
	public String toString( )
	{
		return "[OrientedBox] (" + x + ", " + y + ", " + z + ") min=" + min + ", max=" + max;
	}
	
	public void getCorner( Vector3 out, int i )
	{
		Matrix3 transform = new Matrix3();
		getBoxToWorldTransform( transform );
		getCornerInBoxSpace( out, i );
		transform.multiply( out );
	}
	
	public void getCornerInBoxSpace( Vector3 out, int i )
	{
		out.set(
			( i/4 == 0 ) ? min.x : max.x,
			( i/2%2 == 0 ) ? min.y : max.y,
			( i%2 == 0 ) ? min.z : max.z				
		);
	}
	
	public void getPointInBoxSpace( Vector3 p )
	{
		Matrix3 transform = new Matrix3();
		getWorldToBoxTransform( transform );
		transform.multiply( p );
	}
	
	public void getPointInWorldSpace( Vector3 p )
	{
		Matrix3 transform = new Matrix3();
		getBoxToWorldTransform( transform );
		transform.multiply( p );
	}
	
	public void getWorldToBoxTransform( Matrix3 matrix )
	{
		matrix.setRows( x, y, z );
	}
	
	public void getBoxToWorldTransform( Matrix3 matrix )
	{
		matrix.setColumns( x, y, z );
	}
}
