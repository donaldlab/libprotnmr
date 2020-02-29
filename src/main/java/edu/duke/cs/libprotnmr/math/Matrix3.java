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

import java.io.Serializable;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;

import Jama.Matrix;

public class Matrix3 implements Serializable
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = 1400224150777882871L;
	
	public static final int Dimension = 3;
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public double[][] data;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Matrix3( )
	{
	   data = new double[][] {
	    	{ 0.0, 0.0, 0.0 },
	    	{ 0.0, 0.0, 0.0 },
	    	{ 0.0, 0.0, 0.0 }
	    };	
	}
	
	public Matrix3( Matrix3 other )
	{
		this();
		set( other );
	}
	
	public Matrix3( double a, double b, double c,double d, double e, double f, double g, double h, double i )
	{
		this();
		set( a, b, c, d, e, f, g, h, i );
	}
	
	public Matrix3( Matrix in )
	{
		this();
		fromJama( in );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void getRotation( Matrix3 matrix, Vector3 axis, double angle )
	{
		getRotation( matrix, axis, Math.cos( angle ), Math.sin( angle ) );
	}
	
	public static void getRotation( Matrix3 matrix, Vector3 axis, double cos, double sin )
	{
		// just in case...
		assert( CompareReal.eq( axis.getSquaredLength(), 1.0 ) );
		
		double axx = axis.x * axis.x;
		double ayy = axis.y * axis.y;
		double azz = axis.z * axis.z;
		double axy = axis.x * axis.y;
		double axz = axis.x * axis.z;
		double ayz = axis.y * axis.z;
		double mcos = 1.0 - cos;
		matrix.set(
			( 1.0 - axx ) * cos + axx,		-axis.z * sin + axy * mcos,		axis.y * sin + axz * mcos,
			axis.z * sin + axy * mcos,		( 1.0 - ayy ) * cos + ayy,		-axis.x * sin + ayz * mcos,
			-axis.y * sin + axz * mcos,		axis.x * sin + ayz * mcos,		( 1.0 - azz ) * cos + azz
		);
	}
	
	public static void getRotation( Matrix3 matrix, Quaternion q )
	{
		// first, convert to axis,angle
		Vector3 axis = new Vector3();
		double angle = q.toAxisAngle( axis );
		getRotation( matrix, axis, angle );
	}
	
	public static void getRotationByPi( Matrix3 matrix, Vector3 axis )
	{
		// just in case...
		assert( CompareReal.eq( axis.getSquaredLength(), 1.0 ) );
		
		double axx = axis.x * axis.x;
		double ayy = axis.y * axis.y;
		double azz = axis.z * axis.z;
		double axy = axis.x * axis.y;
		double axz = axis.x * axis.z;
		double ayz = axis.y * axis.z;
		matrix.set(
			2.0 * axx - 1.0,	2.0 * axy,			2.0 * axz,
			2.0 * axy,			2.0 * ayy - 1.0,	2.0 * ayz,
			2.0 * axz,			2.0 * ayz,			2.0 * azz - 1.0
		);
	}
	
	public static void getOrthogonalProjection( Matrix3 matrix, Vector3 a )
	{
		// just in case...
		assert( CompareReal.eq( a.getSquaredLength(), 1.0 ) );
		
		// M = I - aa'
		double xx = a.x*a.x;
		double xy = a.x*a.y;
		double xz = a.x*a.z;
		double yy = a.y*a.y;
		double yz = a.y*a.z;
		double zz = a.z*a.z;
		
		matrix.set(
			1.0 - xx, 0.0 - xy, 0.0 - xz,
			0.0 - xy, 1.0 - yy, 0.0 - yz,
			0.0 - xz, 0.0 - yz, 1.0 - zz
		);
	}
	
	public static void getArbitraryBasisFromZ( Matrix3 out, Vector3 z )
	{
		Vector3 x = new Vector3();
		Vector3 y = new Vector3();
		z = new Vector3( z );
		
		z.normalize();
		z.getArbitraryOrthogonal( y );
		y.normalize();
		y.getCross( x, z );
		x.normalize();
		
		out.setColumns( x, y, z );
	}
	
	public static void getRightBasisFromXZ( Matrix3 out, Vector3 x, Vector3 z )
	{
		// normalize the vectors just in case
		x = new Vector3( x );
		x.normalize();
		z = new Vector3( z );
		z.normalize();
		
		// get y from the right hand rule
		Vector3 y = new Vector3();
		z.getCross( y, x );
		z.normalize();
		
		out.setColumns( x, y, z );
	}
	
	public static void getRightBasisFromXY( Matrix3 out, Vector3 x, Vector3 y )
	{
		// normalize the vectors just in case
		x = new Vector3( x );
		x.normalize();
		y = new Vector3( y );
		y.normalize();
		
		// get y from the right hand rule
		Vector3 z = new Vector3();
		x.getCross( z, y );
		z.normalize();
		
		out.setColumns( x, y, z );
	}
	
	public static void getRightBasisFromYZ( Matrix3 out, Vector3 y, Vector3 z )
	{
		// normalize the vectors just in case
		y = new Vector3( y );
		y.normalize();
		z = new Vector3( z );
		z.normalize();
		
		// get x from the right hand rule
		Vector3 x = new Vector3();
		y.getCross( x, z );
		x.normalize();
		
		out.setColumns( x, y, z );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setRows( Vector3 x, Vector3 y, Vector3 z )
	{
		set(
			x.x, x.y, x.z,
			y.x, y.y, y.z,
			z.x, z.y, z.z
		);
	}
	
	public void setColumns( Vector3 x, Vector3 y, Vector3 z )
	{
		set(
			x.x, y.x, z.x,
			x.y, y.y, z.y,
			x.z, y.z, z.z
		);
	}
	
	public void set( double a, double b, double c,double d, double e, double f, double g, double h, double i )
	{
		data[0][0] = a;
		data[0][1] = b;
		data[0][2] = c;
		
		data[1][0] = d;
		data[1][1] = e;
		data[1][2] = f;
		
		data[2][0] = g;
		data[2][1] = h;
		data[2][2] = i;
	}
	
	public void set( Matrix3 other )
	{
		data[0][0] = other.data[0][0];
		data[0][1] = other.data[0][1];
		data[0][2] = other.data[0][2];
		
		data[1][0] = other.data[1][0];
		data[1][1] = other.data[1][1];
		data[1][2] = other.data[1][2];
		
		data[2][0] = other.data[2][0];
		data[2][1] = other.data[2][1];
		data[2][2] = other.data[2][2];
	}
	
	public void setIdentity( )
	{
		set(
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0,
			0.0, 0.0, 1.0
		);
	}
	
	public void getXAxis( Vector3 out )
	{
		getAxis( out, 0 );
	}
	
	public void getYAxis( Vector3 out )
	{
		getAxis( out, 1 );
	}
	
	public void getZAxis( Vector3 out )
	{
		getAxis( out, 2 );
	}
	
	public void getAxis( Vector3 out, int i )
	{
		// of course, return the ith column vector
		out.set( data[0][i], data[1][i], data[2][i] );
	}
	
	public void setAxis( Vector3 in, int i )
	{
		data[0][i] = in.x;
		data[1][i] = in.y;
		data[2][i] = in.z;
	}
	
	public void negate( )
	{
		data[0][0] = -data[0][0];
		data[0][1] = -data[0][1];
		data[0][2] = -data[0][2];
		
		data[1][0] = -data[1][0];
		data[1][1] = -data[1][1];
		data[1][2] = -data[1][2];
		
		data[2][0] = -data[2][0];
		data[2][1] = -data[2][1];
		data[2][2] = -data[2][2];
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return
			data[0][0] + "\t" + data[0][1] + "\t" + data[0][2] + "\n"
			+ data[1][0] + "\t" + data[1][1] + "\t" + data[1][2] + "\n"
			+ data[2][0] + "\t" + data[2][1] + "\t" + data[2][2];
	}
	
	public double getDeterminant( )
	{
		return
			data[0][0] * ( data[1][1] * data[2][2] - data[1][2] * data[2][1] )
			+ data[0][1] * ( data[1][2] * data[2][0] - data[1][0] * data[2][2] )
			+ data[0][2] * ( data[1][0] * data[2][1] - data[1][1] * data[2][0] );
	}
	
	public void multiply( Vector3 v )
	{
		v.set(
			data[0][0] * v.x + data[0][1] * v.y + data[0][2] * v.z,
			data[1][0] * v.x + data[1][1] * v.y + data[1][2] * v.z,
			data[2][0] * v.x + data[2][1] * v.y + data[2][2] * v.z
		);
	}
	
	public void multiplyRight( Matrix3 out, Matrix3 m )
	{
		// out = this * m
		out.set(
			data[0][0] * m.data[0][0] + data[0][1] * m.data[1][0] + data[0][2] * m.data[2][0],
			data[0][0] * m.data[0][1] + data[0][1] * m.data[1][1] + data[0][2] * m.data[2][1],
			data[0][0] * m.data[0][2] + data[0][1] * m.data[1][2] + data[0][2] * m.data[2][2],
			
			data[1][0] * m.data[0][0] + data[1][1] * m.data[1][0] + data[1][2] * m.data[2][0],
			data[1][0] * m.data[0][1] + data[1][1] * m.data[1][1] + data[1][2] * m.data[2][1],
			data[1][0] * m.data[0][2] + data[1][1] * m.data[1][2] + data[1][2] * m.data[2][2],
			
			data[2][0] * m.data[0][0] + data[2][1] * m.data[1][0] + data[2][2] * m.data[2][0],
			data[2][0] * m.data[0][1] + data[2][1] * m.data[1][1] + data[2][2] * m.data[2][1],
			data[2][0] * m.data[0][2] + data[2][1] * m.data[1][2] + data[2][2] * m.data[2][2]
		);
	}
	
	public void multiplyLeft( Matrix3 out, Matrix3 m )
	{
		// out = m * this
		out.set(
			m.data[0][0] * data[0][0] + m.data[0][1] * data[1][0] + m.data[0][2] * data[2][0],
			m.data[0][0] * data[0][1] + m.data[0][1] * data[1][1] + m.data[0][2] * data[2][1],
			m.data[0][0] * data[0][2] + m.data[0][1] * data[1][2] + m.data[0][2] * data[2][2],
			
			m.data[1][0] * data[0][0] + m.data[1][1] * data[1][0] + m.data[1][2] * data[2][0],
			m.data[1][0] * data[0][1] + m.data[1][1] * data[1][1] + m.data[1][2] * data[2][1],
			m.data[1][0] * data[0][2] + m.data[1][1] * data[1][2] + m.data[1][2] * data[2][2],
			
			m.data[2][0] * data[0][0] + m.data[2][1] * data[1][0] + m.data[2][2] * data[2][0],
			m.data[2][0] * data[0][1] + m.data[2][1] * data[1][1] + m.data[2][2] * data[2][1],
			m.data[2][0] * data[0][2] + m.data[2][1] * data[1][2] + m.data[2][2] * data[2][2]
		);
	}
	
	public void multiply( Matrix3 basis )
	{
		for( int i=0; i<Matrix3.Dimension; i++ )
		{
			Vector3 axis = new Vector3();
			basis.getAxis( axis, i );
			multiply( axis );
			basis.setAxis( axis, i );
		}
	}
	
	public void transpose( )
	{
		double swap = 0.0;
		
		swap = data[0][1];
		data[0][1] = data[1][0];
		data[1][0] = swap;
		
		swap = data[0][2];
		data[0][2] = data[2][0];
		data[2][0] = swap;
		
		swap = data[1][2];
		data[1][2] = data[2][1];
		data[2][1] = swap;
	}
	
	public void toJama( Matrix out )
	{
		assert( out.getRowDimension() == 3 && out.getColumnDimension() == 3 );

		out.set( 0, 0, data[0][0] );
		out.set( 0, 1, data[0][1] );
		out.set( 0, 2, data[0][2] );
		
		out.set( 1, 0, data[1][0] );
		out.set( 1, 1, data[1][1] );
		out.set( 1, 2, data[1][2] );
		
		out.set( 2, 0, data[2][0] );
		out.set( 2, 1, data[2][1] );
		out.set( 2, 2, data[2][2] );
	}
	
	public void fromJama( Matrix in )
	{
		assert( in.getRowDimension() == 3 && in.getColumnDimension() == 3 );
		
		data[0][0] = in.get( 0, 0 );
		data[0][1] = in.get( 0, 1 );
		data[0][2] = in.get( 0, 2 );

		data[1][0] = in.get( 1, 0 );
		data[1][1] = in.get( 1, 1 );
		data[1][2] = in.get( 1, 2 );

		data[2][0] = in.get( 2, 0 );
		data[2][1] = in.get( 2, 1 );
		data[2][2] = in.get( 2, 2 );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Double.valueOf( data[0][0] ).hashCode(),
			Double.valueOf( data[0][1] ).hashCode(),
			Double.valueOf( data[0][2] ).hashCode(),
			Double.valueOf( data[1][0] ).hashCode(),
			Double.valueOf( data[1][1] ).hashCode(),
			Double.valueOf( data[1][2] ).hashCode(),
			Double.valueOf( data[2][0] ).hashCode(),
			Double.valueOf( data[2][1] ).hashCode(),
			Double.valueOf( data[2][2] ).hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Matrix3 )
		{
			return equals( (Matrix3)other );
		}
		return false;
	}
	
	public boolean equals( Matrix3 other )
	{
		return data[0][0] == other.data[0][0]
			&& data[0][1] == other.data[0][1]
			&& data[0][2] == other.data[0][2]
			&& data[1][0] == other.data[1][0]
			&& data[1][1] == other.data[1][1]
			&& data[1][2] == other.data[1][2]
			&& data[2][0] == other.data[2][0]
			&& data[2][1] == other.data[2][1]
			&& data[2][2] == other.data[2][2];
	}
}
