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

import java.util.List;

import edu.duke.cs.libprotnmr.math.Matrix4;


public class Sphere
{
	/**************************
	 *   Fields
	 **************************/
	
	public Vector3 center;
	public double radius;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Sphere( )
	{
		center = new Vector3();
		radius = 0.0;
	}
	
	public Sphere( Vector3 center, double radius )
	{
		this.center = new Vector3();
		set( center, radius );
	}
	
	public Sphere( Vector3 a )
	{
		center = new Vector3( a );
		radius = 0.0;
	}
	
	public Sphere( Vector3 a, Vector3 b )
	{
		// this sphere places a and b on opposite poles
		center = new Vector3( a );
		center.add( b );
		center.scale( 0.5 );
		radius = a.getDistance( b ) / 2.0;
	}
	
	/*public Sphere( Vector a, Vector b, Vector c )
	{
		// UNDONE: implement me if needed
	}*/
	
	public Sphere( Vector3 a, Vector3 b, Vector3 c, Vector3 d )
	{
		solveSphere( a, b, c, d );
	}
	
	public Sphere( List<Vector3> points )
	{
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		center = new Vector3( minSphere.center );
		radius = minSphere.radius;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( Vector3 center, double radius )
	{
		this.center.set( center );
		this.radius = radius;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "( " + center.toString() + ", " + radius + " )";
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void solveSphere( Vector3 a, Vector3 b, Vector3 c, Vector3 d )
	{
		/* Jeff: 5/15/2008 - code shamelessly adapted from
			http://home.att.net/~srschmitt/script_sphere_solver.html
			
			each mij is a minor of the original (submatrix less row i and column j)
			
			x^2 + y^2 + z^2 	x 	y 	z 	1
			x1^2 + y1^2 + z1^2	x1	y1	z1	1
			x2^2 + y2^2 + z2^2	x2	y2	z2	1
			x3^2 + y3^2 + z3^2	x3	y3	z3	1
			x4^2 + y4^2 + z4^2	x4	y4	z4	1
			
			or, another way to write it
			
			s.s	sx	sy	sz	1
			a.a	ax	ay	az	1
			b.b	bx	by	bz	1
			c.c	cx	cy	cz	1
			d.d	dx	dy	dz	1
			
			where s is the center of the sphere and a,b,c,d are the points
		*/
		
		double m11 = new Matrix4(
			a.x, a.y, a.z, 1.0,
			b.x, b.y, b.z, 1.0,
			c.x, c.y, c.z, 1.0,
			d.x, d.y, d.z, 1.0
		).getDeterminant();
		
		double m12 = new Matrix4(
			a.getDot( a ), a.y, a.z, 1.0,
			b.getDot( b ), b.y, b.z, 1.0,
			c.getDot( c ), c.y, c.z, 1.0,
			d.getDot( d ), d.y, d.z, 1.0
		).getDeterminant();
		
		double m13 = new Matrix4(
			a.getDot( a ), a.x, a.z, 1.0,
			b.getDot( b ), b.x, b.z, 1.0,
			c.getDot( c ), c.x, c.z, 1.0,
			d.getDot( d ), d.x, d.z, 1.0
		).getDeterminant();
		
		double m14 = new Matrix4(
			a.getDot( a ), a.x, a.y, 1.0,
			b.getDot( b ), b.x, b.y, 1.0,
			c.getDot( c ), c.x, c.y, 1.0,
			d.getDot( d ), d.x, d.y, 1.0
		).getDeterminant();
		
		double m15 = new Matrix4(
			a.getDot( a ), a.x, a.y, a.z,
			b.getDot( b ), b.x, b.y, b.z,
			c.getDot( c ), c.x, c.y, c.z,
			d.getDot( d ), d.x, d.y, d.z
		).getDeterminant();
		
		if( m11 == 0 )
		{
			center = new Vector3();
			radius = 0.0;
		}
		else
		{
			center = new Vector3(
				0.5 * m12 / m11,
				-0.5 * m13 / m11,
				0.5 * m14 / m11
			);
			radius = Math.sqrt( center.x * center.x + center.y * center.y + center.z * center.z - m15 / m11 );
		}
	}
}
