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

import edu.duke.cs.libprotnmr.geom.Vector2;

public class Matrix2
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static final int Dimension = 2;
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public double[][] data;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Matrix2( )
	{
	   data = new double[][] {
	    	{ 0.0, 0.0 },
	    	{ 0.0, 0.0 }
	    };	
	}
	
	public Matrix2( Matrix2 other )
	{
		data = other.data.clone();
	}
	
	public Matrix2( double a, double b, double c,double d )
	{
		this();
		set( a, b, c, d );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void getRotation( Matrix2 matrix, double alpha )
	{
		double cos = Math.cos( alpha );
		double sin = Math.sin( alpha );
		matrix.set(
			cos, -sin,
			sin, cos
		);
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( double a, double b, double c, double d )
	{
		data[0][0] = a;
		data[0][1] = b;
		
		data[1][0] = c;
		data[1][1] = d;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return
			data[0][0] + "\t" + data[0][1] + "\n"
			+ data[1][0] + "\t" + data[1][1];
	}
	
	public double getDeterminant( )
	{
		return
			data[0][0] * data[1][1]
			- data[0][1] * data[1][0];
	}
	
	public void multiply( Vector2 v )
	{
		v.set(
			data[0][0] * v.x + data[0][1] * v.y,
			data[1][0] * v.x + data[1][1] * v.y
		);
	}
	
	public void invert( )
	{
		double det = getDeterminant();
		double swap = data[1][1];
		data[1][1] = data[0][0] / det;
		data[0][0] = swap / det;
		data[0][1] = -data[0][1] / det;
		data[1][0] = -data[1][0] / det;
	}
}
