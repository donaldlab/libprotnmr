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
