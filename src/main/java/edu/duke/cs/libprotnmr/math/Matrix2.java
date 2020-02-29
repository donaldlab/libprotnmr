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
