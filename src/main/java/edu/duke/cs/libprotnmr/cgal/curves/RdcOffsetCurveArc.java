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

public class RdcOffsetCurveArc extends ParametricCurveArc<RdcOffsetCurve>
{
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve, CircleRange range )
	{
		super( curve, range );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve )
	{
		this( curve, CircleRange.newCircle() );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve, double theta )
	{
		this( curve, CircleRange.newByOffset( theta, Math.PI * 2.0 ) );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve, double thetaSource, double thetaTarget )
	{
		this( curve, CircleRange.newByCounterclockwiseSegment( thetaSource, thetaTarget ) );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve, Vector3 point )
	{
		this( curve, curve.getAngle( point ) );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurve curve, Vector3 source, Vector3 target )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ) );
	}
	
	public RdcOffsetCurveArc( RdcOffsetCurveArc other )
	{
		this( other.getCurve(), other.getRange() );
	}
	
	public RdcOffsetCurveArc( RdcCurveArc arc, double offset )
	{
		this( new RdcOffsetCurve( arc.getCurve(), offset ), arc.getRange() );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<RdcOffsetCurveArc> split( Vector3 p )
	{
		// PRECONDITION: p is on the arc interior
		assert( containsPointInInterior( p ) );
		
		List<RdcOffsetCurveArc> subArcs = new ArrayList<RdcOffsetCurveArc>();
		double angle = getCurve().getAngle( p );
		for( CircleRange range : getRange().split( angle ) )
		{
			subArcs.add( new RdcOffsetCurveArc( getCurve(), range ) );
		}
		return subArcs;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof RdcOffsetCurveArc )
		{
			return equals( (RdcOffsetCurveArc)other );
		}
		return false;
	}
}
