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
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public abstract class ParametricCurveArc<T extends ParametricCurve> implements CurveArc, Serializable
{
	private static final long serialVersionUID = 9110334543360913710L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private T m_curve;
	private CircleRange m_range;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	protected ParametricCurveArc( )
	{
		m_curve = null;
		m_range = null;
	}
	
	protected ParametricCurveArc( T curve, CircleRange range )
	{
		m_curve = curve;
		m_range = range;
	}
	
	protected void set( T curve, CircleRange range )
	{
		m_curve = curve;
		m_range = range;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	@Override
	public T getCurve( )
	{
		return m_curve;
	}
	
	@Override
	public boolean isClosed( )
	{
		return m_range.isCircle();
	}
	
	@Override
	public Vector3 getSource( )
	{
		return m_curve.getPoint( m_range.getSource() );
	}
	
	@Override
	public Vector3 getTarget( )
	{
		return m_curve.getPoint( m_range.getTarget() );
	}
	
	@Override
	public Vector3 getMidpoint( )
	{
		return m_curve.getPoint( m_range.getMidpoint() );
	}
	
	@Override
	public Vector3 getOtherEndpoint( Vector3 p )
	{
		// p must be one of the endpoints
		final double Epsilon = 1e-12;
		if( p.approximatelyEquals( getSource(), Epsilon ) )
		{
			return getTarget();
		}
		else if( p.approximatelyEquals( getTarget(), Epsilon ) )
		{
			return getSource();
		}
		throw new IllegalArgumentException( "Argument must be one of the arc endpoints!" );
	}
	
	@Override
	public Vector3 getOtherEndpoint( Vector3 p, double epsilon )
	{
		// p must be one of the endpoints
		if( p.approximatelyEquals( getSource(), epsilon ) )
		{
			return getTarget();
		}
		else if( p.approximatelyEquals( getTarget(), epsilon ) )
		{
			return getSource();
		}
		throw new IllegalArgumentException( "Argument must be one of the arc endpoints!" );
	}
	
	public CircleRange getRange( )
	{
		return m_range;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<Vector3> samplePoints( )
	{
		return samplePoints( Math.toRadians( 0.25 ) );
	}
	
	public List<Vector3> samplePoints( double resolutionRadians )
	{
		return m_curve.samplePoints( m_range.samplePoints( resolutionRadians ) );
	}
	
	public List<Vector3> samplePoints( int numSamples )
	{
		return m_curve.samplePoints( m_range.samplePoints( numSamples ) );
	}
	
	@Override
	public boolean containsPoint( Vector3 p )
	{
		return containsPoint( p, Curve.DefaultEpsilon );
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		return m_curve.containsPoint( p, epsilon ) && m_range.containsPoint( m_curve.getAngle( p ), epsilon );
	}
	
	@Override
	public boolean containsPointOnBoundary( Vector3 p )
	{
		return containsPointOnBoundary( p, Curve.DefaultEpsilon );
	}
	
	@Override
	public boolean containsPointOnBoundary( Vector3 p, double epsilon )
	{
		return getSource().approximatelyEquals( p, epsilon ) || getTarget().approximatelyEquals( p, epsilon );
	}
	
	@Override
	public boolean containsPointInInterior( Vector3 p )
	{
		return containsPointInInterior( p, Curve.DefaultEpsilon );
	}
	
	@Override
	public boolean containsPointInInterior( Vector3 p, double epsilon )
	{
		return containsPoint( p, epsilon ) && !containsPointOnBoundary( p, epsilon );
	}
	
	@Override
	public boolean hasLength( )
	{
		return !CompareReal.lte( m_range.getLength(), 0.0, 1e-12 ) && m_curve.hasLength();
	}
	
	@Override
	public CircularCurveArc newClosedArc( )
	{
		// clearly this class/interface heirarchy is not perfect
		throw new RuntimeException( "Cannot return a closed arc from another arc" );
	}
	
	@Override
	public CircularCurveArc newClosedArc( Vector3 p )
	{
		// clearly this class/interface heirarchy is not perfect
		throw new RuntimeException( "Cannot return a closed arc from another arc" );
	}
	
	@Override
	public List<? extends CurveArc> split( Iterable<Vector3> points )
	{
		// put the points in curve order
		TreeSet<Vector3> orderedPoints = new TreeSet<Vector3>( new Comparator<Vector3>( )
		{
			@Override
			public int compare( Vector3 a, Vector3 b )
			{
				return Double.compare( m_curve.getAngle( a ), m_curve.getAngle( b ) );
			}
		} );
		for( Vector3 p : points )
		{
			assert( containsPoint( p ) );
			orderedPoints.add( p );
		}
		Vector3 target = getTarget();
		orderedPoints.add( target );

		List<CurveArc> allSubArcs = new ArrayList<CurveArc>();
		
		// short circuit here if possible
		if( orderedPoints.isEmpty() )
		{
			return allSubArcs;
		}
		
		// splitting can happen in order now
		Vector3 lastPoint = getSource();
		Vector3 nextPoint = null;
		CurveArc remainingArc = this;
		while( true )
		{
			// get the next point
			nextPoint = orderedPoints.higher( lastPoint );
			if( nextPoint == null )
			{
				nextPoint = orderedPoints.first();
			}
			
			// is this the last arc?
			if( nextPoint == target )
			{
				allSubArcs.add( remainingArc );
				break;
			}
			
			// build the next sub arc
			List<? extends CurveArc> subArcs = remainingArc.split( nextPoint );
			assert( subArcs.size() == 2 );
			allSubArcs.add( subArcs.get( 0 ) );
			
			remainingArc = subArcs.get( 1 );
			lastPoint = nextPoint;
			
			assert( allSubArcs.size() <= orderedPoints.size() );
		}
		
		// just in case...
		assert( allSubArcs.size() == orderedPoints.size() );
		assert( allSubArcs.get( 0 ).getSource().approximatelyEquals( getSource() ) );
		assert( allSubArcs.get( allSubArcs.size() - 1 ).getTarget().approximatelyEquals( getTarget() ) );
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendCurve( kin, this, "Parent", KinemageColor.Grey, 6 );
			KinemageBuilder.appendPoints( kin, points, "Split Points", KinemageColor.Lime, 7 );
			for( CurveArc arc : allSubArcs )
			{
				KinemageBuilder.appendCurve( kin, arc, "Arc", KinemageColor.Cobalt, 3 );
			}
			new KinemageWriter().showAndWait( kin );
		}
		
		return allSubArcs;
	}
	
	@Override
	public double getApproximateLength( int numSamples )
	{
		List<Vector3> samples = samplePoints( numSamples );
		double length = 0.0;
		for( int i=1; i<samples.size(); i++ )
		{
			length += samples.get( i - 1 ).getDistance( samples.get( i ) );
		}
		return length;
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_curve.hashCode(),
			m_range.hashCode()
		);
	}
	
	public boolean equals( ParametricCurveArc<T> other )
	{
		return m_curve.equals( other.m_curve )
			&& m_range.equals( other.m_range );
	}
}
