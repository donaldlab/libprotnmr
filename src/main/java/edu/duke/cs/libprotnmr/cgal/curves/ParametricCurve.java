package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ParametricCurve implements Curve, Serializable
{
	private static final long serialVersionUID = -2025117421537308183L;
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<Vector3> samplePoints( )
	{
		return samplePoints( Math.toRadians( 0.25 ) );
	}
	
	public List<Vector3> samplePoints( int numSamples )
	{
		return samplePoints( removeLastSample( CircleRange.newCircle().samplePoints( numSamples ) ) );
	}
	
	public List<Vector3> samplePoints( double resolutionRadians )
	{
		return samplePoints( removeLastSample( CircleRange.newCircle().samplePoints( resolutionRadians ) ) );
	}
	
	public List<Vector3> samplePoints( Iterable<Double> thetaSamples )
	{
		List<Vector3> points = new ArrayList<Vector3>();
		for( Double t : thetaSamples )
		{
			points.add( getPoint( t ) );
		}
		return points;
	}
	
	public List<? extends CurveArc> split( Vector3 point )
	{
		return Arrays.asList( newClosedArc( point ) );
	}
	
	public List<? extends CurveArc> split( Iterable<Vector3> points )
	{
		// convert the points to a list
		List<Vector3> pointsList = new ArrayList<Vector3>();
		for( Vector3 p : points )
		{
			pointsList.add( p );
		}
		
		if( pointsList.isEmpty() )
		{
			throw new IllegalArgumentException( "points cannot be empty!" );
		}
		
		CurveArc closedArc = newClosedArc( pointsList.get( 0 ) );
		return closedArc.split( pointsList.subList( 1, pointsList.size() ) );
	}
	
	@Override
	public boolean containsPoint( Vector3 p )
	{
		return containsPoint( p, Curve.DefaultEpsilon );
	}
	
	public abstract double getAngle( Vector3 p );
	public abstract Vector3 getPoint( double angle );
	public abstract Vector3 getDerivative( double angle );
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private Iterable<Double> removeLastSample( List<Double> samples )
	{
		samples.remove( samples.size() - 1 );
		return samples;
	}
}
