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

public class AxisAlignedBox
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static final int NumCorners = (int)Math.pow( 2, Vector3.Dimension );
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public Vector3 min;
	public Vector3 max;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AxisAlignedBox( )
	{
		min = new Vector3();
		max = new Vector3();
	}
	
	public AxisAlignedBox( AxisAlignedBox other )
	{
		min = new Vector3( other.min );
		max = new Vector3( other.max );
	}
	
	public AxisAlignedBox( Vector3 min, Vector3 max )
	{
		this.min = new Vector3( min );
		this.max = new Vector3( max );
	}
	
	public AxisAlignedBox( double minx, double miny, double minz, double maxx, double maxy, double maxz )
	{
		min = new Vector3( minx, miny, minz );
		max = new Vector3( maxx, maxy, maxz );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "[AxisAlignedBox] min=" + min + ", max=" + max;
	}
	
	public void getCorner( Vector3 out, int i )
	{
		out.set(
			( i/4 == 0 ) ? min.x : max.x,
			( i/2%2 == 0 ) ? min.y : max.y,
			( i%2 == 0 ) ? min.z : max.z				
		);
	}
	
	public double getDiagonalSquared( )
	{
		double dx = max.x - min.x;
		double dy = max.y - min.y;
		double dz = max.z - min.z;
		
		return dx * dx + dy * dy + dz * dz;
	}
}
