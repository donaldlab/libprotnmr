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

import edu.duke.cs.libprotnmr.math.CompareReal;

public class Intersect
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static boolean isPointIn( Vector3 p, AxisAlignedBox box )
	{
		return
			CompareReal.gte( p.x, box.min.x ) && CompareReal.lte( p.x, box.max.x )
			&& CompareReal.gte( p.y, box.min.y ) && CompareReal.lte( p.y, box.max.y )
			&& CompareReal.gte( p.z, box.min.z ) && CompareReal.lte( p.z, box.max.z );
	}
	
	public static boolean isPointIn( Vector3 p, OrientedBox box )
	{
		Vector3 q = new Vector3( p );
		box.getPointInBoxSpace( q );
		return
			CompareReal.gte( q.x, box.min.x ) && CompareReal.lte( q.x, box.max.x )
			&& CompareReal.gte( q.y, box.min.y ) && CompareReal.lte( q.y, box.max.y )
			&& CompareReal.gte( q.z, box.min.z ) && CompareReal.lte( q.z, box.max.z );
	}
	
	public static boolean isPointIn( Vector3 p, Sphere sphere )
	{
		return CompareReal.lte( p.getSquaredDistance( sphere.center ), sphere.radius * sphere.radius );
	}
	
	public static boolean isOverlapping( AxisAlignedBox box, Sphere sphere )
	{
		return isOverlapping( sphere, box );
	}
	
	public static boolean isOverlapping( Sphere sphere, AxisAlignedBox box )
	{
		return CompareReal.lte( getSquaredDistanceFromPointTo( sphere.center, box ), sphere.radius * sphere.radius );
	}
	
	public static double getSquaredDistanceFromPointTo( Vector3 p, AxisAlignedBox box )
	{
		double val = 0.0;
		double dist = 0.0;
		double totalDistSq = 0.0;
		
		for( int i=0; i<Vector3.Dimension; i++ )
		{
			val = p.get( i );
			if( CompareReal.lte( val, box.min.get( i ) ) )
			{
				dist = val - box.min.get( i );
				totalDistSq += dist * dist;
			}
			else if( CompareReal.gte( val, box.max.get( i ) ) )
			{
				dist = val - box.max.get( i );
				totalDistSq += dist * dist;
			}
		}
		
		return totalDistSq;
	}
	
	// Jeff: 5/29/2008 - this one gets distances from a point inside to the boundary planes
	public static double[] getDistancesFromPointIn( Vector3 p, AxisAlignedBox box )
	{
		double val = 0.0;
		double[] dists = new double[Vector3.Dimension * 2];
		
		for( int i=0; i<Vector3.Dimension; i++ )
		{
			val = p.get( i );
			
			dists[2 * i + 0] = val - box.min.get( i );
			dists[2 * i + 1] = box.max.get( i ) - val;
		}
		
		return dists;
	}
	
	public static double[] getDistancesFromPointIn( Vector3 p, OrientedBox box )
	{
		Vector3 q = new Vector3( p );
		box.getPointInBoxSpace( q );
		return getDistancesFromPointIn(
			q,
			new AxisAlignedBox( box.min, box.max )
		);
	}
}
