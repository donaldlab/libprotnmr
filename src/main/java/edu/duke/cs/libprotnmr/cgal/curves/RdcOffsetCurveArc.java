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
