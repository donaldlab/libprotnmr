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
import edu.duke.cs.libprotnmr.math.Quaternion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CircularCurveArc extends ParametricCurveArc<CircularCurve> implements Serializable
{
	private static final long serialVersionUID = -7709906488030358955L;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public CircularCurveArc( CircularCurve curve, CircleRange range )
	{
		super( curve, range );
	}
	
	public CircularCurveArc( CircularCurve curve )
	{
		this( curve, CircleRange.newCircle() );
	}
	
	public CircularCurveArc( CircularCurve curve, double angle )
	{
		this( curve, CircleRange.newByOffset( angle, Math.PI * 2.0 ) );
	}
	
	public CircularCurveArc( CircularCurve curve, double angleSource, double angleTarget )
	{
		this( curve, CircleRange.newByCounterclockwiseSegment( angleSource, angleTarget ) );
	}
	
	public CircularCurveArc( CircularCurve curve, Vector3 point )
	{
		this( curve, curve.getAngle( point ) );
	}
	
	public CircularCurveArc( CircularCurve curve, Vector3 source, Vector3 target )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ) );
	}
	
	public CircularCurveArc( CircularCurveArc other )
	{
		this( other.getCurve(), other.getRange() );
	}
	
	public CircularCurveArc( CircularCurve curve, double angleSource, double angleTarget, double angleInterior )
	{
		this( curve, CircleRange.newByThreePoints( angleSource, angleTarget, angleInterior ) );
	}
	
	public CircularCurveArc( Vector3 source, Vector3 target, Vector3 interiorPoint )
	{
		this( new CircularCurve( source, interiorPoint, target ), source, target, interiorPoint );
	}
	
	public CircularCurveArc( CircularCurve curve, Vector3 source, Vector3 target, Vector3 interiorPoint )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ), curve.getAngle( interiorPoint ) );
	}
	
	public CircularCurveArc( GeodesicCurveArc arc )
	{
		this( new CircularCurve( arc.getCurve() ), arc.getSource(), arc.getTarget() );
	}
	

	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<CircularCurveArc> split( Vector3 p )
	{
		// PRECONDITION: p is on the arc interior
		assert( containsPointInInterior( p, 1e-10 ) );
		
		List<CircularCurveArc> subArcs = new ArrayList<CircularCurveArc>();
		double angle = getCurve().getAngle( p );
		for( CircleRange range : getRange().split( angle ) )
		{
			subArcs.add( new CircularCurveArc( getCurve(), range ) );
		}
		return subArcs;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof CircularCurveArc )
		{
			return equals( (CircularCurveArc)other );
		}
		return false;
	}
	
	public CircleRange getMinBoundingAnnulus( Vector3 axis )
	{
		// start with the arc endpoints
		List<Vector3> potentialOptima = new ArrayList<Vector3>();
		potentialOptima.add( getSource() );
		potentialOptima.add( getTarget() );
		
		// for the underlying circle, find the min and max points
		GeodesicCurve geodesic = GeodesicCurveArc.newByPointsWithArbitraryNormal( axis, getCurve().getNormal() ).getCurve();
		potentialOptima.addAll( Intersector.getIntersectionPoints( this, geodesic ) );
		
		// now check all the potential points for the min and max
		double min = Math.PI;
		double max = 0.0;
		for( Vector3 p : potentialOptima )
		{
			double angle = Math.acos( p.getDot( axis ) );
			min = Math.min( min, angle );
			max = Math.max( max, angle );
		}
		return CircleRange.newByCounterclockwiseSegment( min, max );
	}
	
	public CircularCurveArc getReverseArc( )
	{
		return new CircularCurveArc( getCurve().getReverseCurve(), getTarget(), getSource() );
	}
	
	public CircularCurveArc getRotatedArc( Quaternion q )
	{
		return new CircularCurveArc( getCurve().getRotatedCurve( q ), getRange() );
	}
}
