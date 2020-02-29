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

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RdcCurveArc extends ParametricCurveArc<RdcCurve>
{
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcCurveArc( RdcCurve curve, CircleRange range )
	{
		super( curve, range );
	}
	
	public RdcCurveArc( RdcCurve curve )
	{
		this( curve, CircleRange.newCircle() );
	}
	
	public RdcCurveArc( RdcCurve curve, double theta )
	{
		this( curve, CircleRange.newByOffset( theta, Math.PI * 2.0 ) );
	}
	
	public RdcCurveArc( RdcCurve curve, double thetaSource, double thetaTarget )
	{
		this( curve, CircleRange.newByCounterclockwiseSegment( thetaSource, thetaTarget ) );
	}
	
	public RdcCurveArc( RdcCurve curve, Vector3 point )
	{
		this( curve, curve.getAngle( point ) );
	}
	
	public RdcCurveArc( RdcCurve curve, Vector3 source, Vector3 target )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ) );
	}
	
	public RdcCurveArc( RdcCurveArc other )
	{
		this( other.getCurve(), other.getRange() );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<RdcCurveArc> split( Vector3 p )
	{
		// PRECONDITION: p is on the arc interior
		assert( containsPointInInterior( p ) );
		
		List<RdcCurveArc> subArcs = new ArrayList<RdcCurveArc>();
		double angle = getCurve().getAngle( p );
		for( CircleRange range : getRange().split( angle ) )
		{
			subArcs.add( new RdcCurveArc( getCurve(), range ) );
		}
		return subArcs;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof RdcCurveArc )
		{
			return equals( (RdcCurveArc)other );
		}
		return false;
	}
}
