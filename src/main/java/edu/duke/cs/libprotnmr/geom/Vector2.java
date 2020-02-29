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

import java.io.Serializable;

import edu.duke.cs.libprotnmr.dataStructures.Fuzzy;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix2;


public class Vector2 implements Serializable, Fuzzy<Vector2>
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = 4346075783891245919L;
	
	public static final int Dimension = 2;
	
	private static final Vector2 Origin = new Vector2( 0.0, 0.0 );
	private static final Vector2 UnitX = new Vector2( 1.0, 0.0 );
	private static final Vector2 UnitY = new Vector2( 0.0, 1.0 );
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public double x;
	public double y;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Vector2( )
	{
		x = 0.0;
		y = 0.0;
	}
	
	public Vector2( double x, double y )
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2( double[] vals )
	{
		set( vals );
	}
	
	public Vector2( Vector2 other )
	{
		set( other );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double get( int i )
	{
		switch( i )
		{
			case 0: return x;
			case 1: return y;
		}
		
		assert( false ) : "Invalid index: " + i;
		
		// just to make the compiler happy
		return Double.NaN;
	}
	
	public void set( int i, double val )
	{
		switch( i )
		{
			case 0: x = val; return;
			case 1: y = val; return;
		}
		
		assert( false ) :  "Invalid index: " + i;
	}
	
	public void set( Vector2 other )
	{
		x = other.x;
		y = other.y;
	}
	
	public void set( double a, double b )
	{
		x = a;
		y = b;
	}
	
	public void set( double[] vals )
	{
		assert( vals.length == Vector2.Dimension );
		
		x = vals[0];
		y = vals[1];
	}
	
	public boolean isValid( )
	{
		return !Double.isNaN( x ) && !Double.isInfinite( x )
			&& !Double.isNaN( y ) && !Double.isInfinite( y );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void getUnitX( Vector2 v )
	{
		v.set( UnitX );
	}
	
	public static void getUnitY( Vector2 v )
	{
		v.set( UnitY );
	}
	
	public static void getOrigin( Vector2 v )
	{
		v.set( Origin );
	}

	public static Vector2 getUnitX( )
	{
		return new Vector2( UnitX );
	}
	
	public static Vector2 getUnitY( )
	{
		return new Vector2( UnitY );
	}
	
	public static Vector2 getOrigin( )
	{
		return new Vector2( Origin );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public double getSquaredLength( )
	{
		return x*x + y*y;
	}
	
	public double getLength( )
	{
		return Math.sqrt( getSquaredLength() );
	}
	
	public void normalize( )
	{
		double length = getLength();
		x /= length;
		y /= length;
	}
	
	public double getSquaredDistance( Vector2 other )
	{
		double dx = other.x - x;
		double dy = other.y - y;
		return dx*dx + dy*dy;
	}
	
	public double getDistance( Vector2 other )
	{
		return Math.sqrt( getSquaredDistance( other ) );
	}
	
	public void add( Vector2 other )
	{
		x += other.x;
		y += other.y;
	}
	
	public void subtract( Vector2 other )
	{
		x -= other.x;
		y -= other.y;
	}
	
	public void scale( double s )
	{
		x *= s;
		y *= s;
	}
	
	public void negate( )
	{
		x = -x;
		y = -y;
	}
	
	public double getDot( Vector2 other )
	{
		return x * other.x + y * other.y;
	}
	
	public void transform( Matrix2 m )
	{
		m.multiply( this );
	}
	
	@Override
	public String toString( )
	{
		return "( " + x + ", " + y + " )";
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			new Double( x ).hashCode(),
			new Double( y ).hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Vector2 )
		{
			return equals( (Vector2)other );
		}
		return false;
	}
	
	public boolean equals( Vector2 other )
	{
		// NOTE: don't want fuzzy comparisons here
		return x == other.x && y == other.y;
	}
	
	public boolean approximatelyEquals( Vector2 other )
	{
		return CompareReal.eq( getSquaredDistance( other ), 0.0 );
	}
	
	public boolean approximatelyEquals( Vector2 other, double epsilon )
	{
		return CompareReal.eq( getSquaredDistance( other ), 0.0, epsilon );
	}
}
