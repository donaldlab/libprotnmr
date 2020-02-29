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

package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;

public class GeodesicCurve extends ParametricCurve
{
	private static final long serialVersionUID = 4198354675300771700L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Vector3 m_normal;
	private Matrix3 m_rotParamToMol;
	private Matrix3 m_rotMolToParam;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public GeodesicCurve( Vector3 normal )
	{
		// save parameters
		m_normal = normal;
		
		// init derived info
		m_rotParamToMol = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( m_rotParamToMol, m_normal );
		m_rotMolToParam = new Matrix3( m_rotParamToMol );
		m_rotMolToParam.transpose();
	}
	
	public GeodesicCurve( Matrix3 rotParamToMol )
	{
		m_rotParamToMol = new Matrix3( rotParamToMol );
		m_rotMolToParam = new Matrix3( m_rotParamToMol );
		m_rotMolToParam.transpose();
		
		m_normal = new Vector3();
		m_rotParamToMol.getZAxis( m_normal );
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getNormal( )
	{
		return m_normal;
	}
	
	public Matrix3 getRotParamToMol( )
	{
		return m_rotParamToMol;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getAngle( Vector3 p )
	{
		// PRECONDITION: p is on the curve
		assert( containsPoint( p ) );
		
		Vector3 copy = new Vector3( p );
		m_rotMolToParam.multiply( copy );
		return Math.atan2( copy.y, copy.x );
	}
	
	@Override
	public Vector3 getPoint( double angle )
	{
		Vector3 point = new Vector3( Math.cos( angle ), Math.sin( angle ), 0.0 );
		m_rotParamToMol.multiply( point );
		return point;
	}
	
	@Override
	public Vector3 getDerivative( double angle )
	{
		return getPoint( angle + Math.PI/2.0 );
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		return CompareReal.eq( p.getSquaredLength(), 1.0, epsilon ) && CompareReal.eq( p.getDot( m_normal ), 0.0, epsilon );
	}
	
	@Override
	public boolean hasLength( )
	{
		return true;
	}
	
	@Override
	public GeodesicCurveArc newClosedArc( )
	{
		return new GeodesicCurveArc( this );
	}
	
	@Override
	public GeodesicCurveArc newClosedArc( Vector3 p )
	{
		return new GeodesicCurveArc( this, p );
	}
	
	@Override
	public int hashCode( )
	{
		return m_normal.hashCode();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof GeodesicCurve )
		{
			return equals( (GeodesicCurve)other );
		}
		return false;
	}
	
	public boolean equals( GeodesicCurve other )
	{
		return m_normal.equals( other.m_normal );
	}
}
