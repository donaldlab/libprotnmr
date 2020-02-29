package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;

import java.util.ArrayList;
import java.util.List;

public class GeodesicCurveArc extends ParametricCurveArc<GeodesicCurve>
{
	private static final long serialVersionUID = 2227054530373623269L;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public GeodesicCurveArc( GeodesicCurve curve, CircleRange range )
	{
		super( curve, range );
	}
	
	public GeodesicCurveArc( Vector3 source, Vector3 target )
	{
		if( source.equals( target ) )
		{
			throw new IllegalArgumentException( "source and target must be distinct!" );
		}
		
		if( isAntipodal( source, target ) )
		{
			throw new IllegalArgumentException( "source and target cannot be antipodal!" );
		}
		
		// compute the curve normal
		Vector3 normal = new Vector3();
		source.getCross( normal, target );
		normal.normalize();
		
		// construct the arc
		GeodesicCurve curve = new GeodesicCurve( normal );
		CircleRange range = CircleRange.newByShortSegment(
			curve.getAngle( source ),
			curve.getAngle( target )
		);
		
		super.set( curve, range );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve )
	{
		this( curve, CircleRange.newCircle() );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve, Vector3 p )
	{
		this( curve, curve.getAngle( p ) );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve, double angle )
	{
		this( curve, CircleRange.newByOffset( angle, Math.PI * 2.0 ) );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve, Vector3 source, Vector3 target )
	{
		this( curve, CircleRange.newByCounterclockwiseSegment( curve.getAngle( source ), curve.getAngle( target ) ) );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve, double angleSource, double angleTarget, double angleInterior )
	{
		this( curve, CircleRange.newByThreePoints( angleSource, angleTarget, angleInterior ) );
	}
	
	public GeodesicCurveArc( GeodesicCurve curve, Vector3 source, Vector3 target, Vector3 interiorPoint )
	{
		this( curve, curve.getAngle( source ), curve.getAngle( target ), curve.getAngle( interiorPoint ) );
	}
	
	public GeodesicCurveArc( GeodesicCurveArc arc )
	{
		this( arc.getCurve(), arc.getRange() );
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static GeodesicCurveArc newByAntipodalPoints( Vector3 source, Vector3 normal )
	{
		// construct an arc from source to its inverse counterclockwise around the specified normal
		Vector3 target = new Vector3( source );
		target.negate();
		
		GeodesicCurve curve = new GeodesicCurve( normal );
		CircleRange range = CircleRange.newByCounterclockwiseSegment(
			curve.getAngle( source ),
			curve.getAngle( target )
		);
		
		return new GeodesicCurveArc( curve, range );
	}
	
	public static GeodesicCurveArc newByPointsWithArbitraryNormal( Vector3 source, Vector3 target )
	{
		if( isAntipodal( source, target ) )
		{
			// pick an arbitrary normal
			Vector3 normal = new Vector3();
			source.getArbitraryOrthogonal( normal );
			normal.normalize();
			
			return newByAntipodalPoints( source, normal );
		}
		else
		{
			return new GeodesicCurveArc( source, target );
		}
	}
	
	public static boolean isAntipodal( Vector3 a, Vector3 b )
	{
		return CompareReal.eq( a.getDot( b ), -1.0, 1e-12 );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<GeodesicCurveArc> split( Vector3 p )
	{
		// PRECONDITION: p is on the arc interior
		assert( containsPointInInterior( p ) );
		
		List<GeodesicCurveArc> subArcs = new ArrayList<GeodesicCurveArc>();
		double angle = getCurve().getAngle( p );
		for( CircleRange range : getRange().split( angle ) )
		{
			subArcs.add( new GeodesicCurveArc( getCurve(), range ) );
		}
		return subArcs;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof GeodesicCurveArc )
		{
			return equals( (GeodesicCurveArc)other );
		}
		return false;
	}
}
