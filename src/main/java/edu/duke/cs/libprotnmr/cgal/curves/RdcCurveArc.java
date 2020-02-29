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
