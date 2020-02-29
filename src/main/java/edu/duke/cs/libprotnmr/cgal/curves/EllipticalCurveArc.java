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

public class EllipticalCurveArc extends ParametricCurveArc<EllipticalCurve>
{
	private static final long serialVersionUID = 7673146099319722804L;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public EllipticalCurveArc( EllipticalCurve curve, CircleRange range )
	{
		super( curve, range );
	}
	
	public EllipticalCurveArc( EllipticalCurve curve )
	{
		this( curve, CircleRange.newCircle() );
	}
	
	public EllipticalCurveArc( EllipticalCurve curve, double angle )
	{
		this( curve, CircleRange.newByOffset( angle, Math.PI * 2.0 ) );
	}
	
	public EllipticalCurveArc( EllipticalCurve curve, double angleSource, double angleTarget )
	{
		this( curve, CircleRange.newByCounterclockwiseSegment( angleSource, angleTarget ) );
	}
	
	public EllipticalCurveArc( EllipticalCurve curve, Vector3 point )
	{
		this( curve, curve.getAngle( point ) );
	}
	
	public EllipticalCurveArc( EllipticalCurve curve, Vector3 source, Vector3 target )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ) );
	}
	
	public EllipticalCurveArc( EllipticalCurveArc other )
	{
		this( other.getCurve(), other.getRange() );
	}
	

	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<EllipticalCurveArc> split( Vector3 p )
	{
		// PRECONDITION: p is on the arc interior
		assert( containsPointInInterior( p ) );
		
		List<EllipticalCurveArc> subArcs = new ArrayList<EllipticalCurveArc>();
		double angle = getCurve().getAngle( p );
		for( CircleRange range : getRange().split( angle ) )
		{
			subArcs.add( new EllipticalCurveArc( getCurve(), range ) );
		}
		return subArcs;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof EllipticalCurveArc )
		{
			return equals( (EllipticalCurveArc)other );
		}
		return false;
	}
}
