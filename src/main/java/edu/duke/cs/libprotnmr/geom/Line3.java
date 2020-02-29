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

import edu.duke.cs.libprotnmr.math.CompareReal;

public class Line3
{
	/**************************
	 *   Fields
	 **************************/
	
	public Vector3 start;
	public Vector3 stop;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Line3( Vector3 start, Vector3 stop )
	{
		set( start, stop );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( Vector3 start, Vector3 stop )
	{
		this.start = start;
		this.stop = stop;
	}
	
	public Vector3 getDirection( )
	{
		Vector3 dir = new Vector3( stop );
		dir.subtract( start );
		return dir;
	}
	
	public Vector3 getPoint( double t )
	{
		Vector3 p = getDirection();
		p.scale( t );
		p.add( start );
		return p;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public Vector3 getIntersectionPoint( Line3 other )
	{
		return getIntersectionPoint( other, CompareReal.getEpsilon() );
	}
	
	public Vector3 getIntersectionPoint( Line3 other, double epsilon )
	{
		Vector3 thisDir = getDirection();
		Vector3 otherDir = other.getDirection();
		
		double a = start.x;
		double b = start.y;
		double c = start.z;
		
		double d = thisDir.x;
		double e = thisDir.y;
		double f = thisDir.z;
		
		double g = other.start.x;
		double h = other.start.y;
		double i = other.start.z;
		
		double j = otherDir.x;
		double k = otherDir.y;
		double l = otherDir.z;
		
		double denom = e*j - d*k;
		double s = -( b*j - h*j - a*k + g*k )/denom;
		double t = -( b*d - a*e + e*g - d*h )/denom;
		
		// is there an intersection at all?
		if( !CompareReal.eq( c+s*f, i+t*l, epsilon ) )
		{
			return null;
		}
		
		// return the intersection point
		Vector3 p = new Vector3( thisDir );
		p.scale( s );
		p.add( start );
		
		// DEBUG
		if( true )
		{
			Vector3 q = new Vector3( otherDir );
			q.scale( t );
			q.add( other.start );
			assert( p.approximatelyEquals( p, epsilon ) );
		}
		
		return p;
	}
	
	@Override
	public String toString( )
	{
		return start.toString() + " -> " + stop.toString();
	}
}
