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

import edu.duke.cs.libprotnmr.geom.Vector3;

public class Matrix4
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static final int Dimension = 4;
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public double[][] data;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Matrix4( )
	{
	   data = new double[][] {
	    	{ 0.0, 0.0, 0.0, 0.0 },
	    	{ 0.0, 0.0, 0.0, 0.0 },
	    	{ 0.0, 0.0, 0.0, 0.0 },
	    	{ 0.0, 0.0, 0.0, 0.0 }
	    };	
	}
	
	public Matrix4( Matrix4 other )
	{
		data = other.data.clone();
	}
	
	public Matrix4( double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p )
	{
		this();
		set( a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p );
	}
	
	public Matrix4( double a, double b, double c, double x, double d, double e, double f, double y, double g, double h, double i, double z )
	{
		this();
		set( a, b, c, x, d, e, f, y, g, h, i, z );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/

	public void set( double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p )
	{
		data[0][0] = a;
		data[0][1] = b;
		data[0][2] = c;
		data[0][3] = d;
		
		data[1][0] = e;
		data[1][1] = f;
		data[1][2] = g;
		data[1][3] = h;
		
		data[2][0] = i;
		data[2][1] = j;
		data[2][2] = k;
		data[2][3] = l;
		
		data[3][0] = m;
		data[3][1] = n;
		data[3][2] = o;
		data[3][3] = p;
	}

	public void set( double a, double b, double c, double x, double d, double e, double f, double y, double g, double h, double i, double z )
	{
		data[0][0] = a;
		data[0][1] = b;
		data[0][2] = c;
		data[0][3] = x;
		
		data[1][0] = d;
		data[1][1] = e;
		data[1][2] = f;
		data[1][3] = y;
		
		data[2][0] = g;
		data[2][1] = h;
		data[2][2] = i;
		data[2][3] = z;
		
		data[3][0] = 0.0;
		data[3][1] = 0.0;
		data[3][2] = 0.0;
		data[3][3] = 1.0;
	}
	
	
	/**************************
	 *   Methods
	 **************************/

	public String toString( )
	{
		return
			data[0][0] + "\t" + data[0][1] + "\t" + data[0][2] + "\t" + data[0][3] + "\n"
			+ data[1][0] + "\t" + data[1][1] + "\t" + data[1][2] + "\t" + data[1][3] + "\n"
			+ data[2][0] + "\t" + data[2][1] + "\t" + data[2][2] + "\t" + data[2][3] + "\n"
			+ data[3][0] + "\t" + data[3][1] + "\t" + data[3][2] + "\t" + data[3][3];
	}
	
	public double getDeterminant( )
	{
		return getDeterminant( data );
	}
	
	public void multiply( Vector3 v )
	{
		v.set(
			data[0][0] * v.x + data[0][1] * v.y + data[0][2] * v.z + data[0][3],
			data[1][0] * v.x + data[1][1] * v.y + data[1][2] * v.z + data[1][3],
			data[2][0] * v.x + data[2][1] * v.y + data[2][2] * v.z + data[2][3]
		);
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private double getDeterminant( double[][] mat )
	{
		/* Jeff: 5/15/2008 - code shamelessly adapted from
			http://home.att.net/~srschmitt/script_determinant4.html
		*/
		
		double det = 0.0;
		
		if( mat.length == 2 )
		{
			det = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];
		}
		else
		{
			double[][] minor = new double[mat.length-1][mat.length-1];
			
			for( int j1=0; j1<mat.length; j1++ )
			{
				for( int i=1; i<mat.length; i++ )
				{
					int j2 = 0;
					for( int j=0; j<mat.length; j++ )
					{
						if( j == j1 )
						{
							continue;
						}
						minor[i-1][j2] = mat[i][j];
						j2++;
					}
				}
			
				det += Math.pow( -1.0, j1 ) * mat[0][j1] * getDeterminant( minor );
			}
		}
		
		return det;
	}
}
