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

public class MultiIntersect
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static boolean isOverlapping( MultiSphere sphere, MultiAxisAlignedBox box )
	{
		return CompareReal.lte( getSquaredDistanceFromPointTo( sphere.center, box ), sphere.radius * sphere.radius );
	}
	
	public static double getSquaredDistanceFromPointTo( MultiVector p, MultiAxisAlignedBox box )
	{
		double val = 0.0;
		double dist = 0.0;
		double totalDistSq = 0.0;
		
		for( int i=0; i<p.getDimension(); i++ )
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
}
