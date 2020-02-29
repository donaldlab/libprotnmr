package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.optimization.OptimizerFailureException;
import edu.duke.cs.libprotnmr.optimization.SimpleCircleOptimizer;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class RdcOffsetCurve extends ParametricCurve implements Serializable
{
	private static final long serialVersionUID = -7759989776251088225L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private RdcCurve m_rdcCurve;
	private double m_geodesicDistance;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcOffsetCurve( RdcCurve rdcCurve, double geodesicDistance )
	{
		m_rdcCurve = rdcCurve;
		m_geodesicDistance = geodesicDistance;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public RdcCurve getRdcCurve( )
	{
		return m_rdcCurve;
	}
	
	public double getGeodesicDistance( )
	{
		return m_geodesicDistance;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getAngle( Vector3 p )
	{
		OffsetAngleFunction f = new OffsetAngleFunction( m_rdcCurve, p );
		List<Double> optima;
		try
		{
			optima = SimpleCircleOptimizer.getOptima( f );
		}
		catch( OptimizerFailureException ex )
		{
			// DEBUG: save the function for later inspection
			if( true )
			{
				f.save( new File( "output/function.dat" ) );
			}
			throw new Error( "Unable to find angle. Numerical methods failed. =(", ex );
		}
		List<Double> roots = SimpleCircleOptimizer.getRoots( f, optima ); 
		
		// if we didn't get any roots, that's a problem
		if( roots.isEmpty() )
		{
			// DEBUG: save the function for later inspection
			if( true )
			{
				f.save( new File( "output/function.dat" ) );
			}
			throw new Error( "Didn't find any roots! Numerical methods failed! =(" );
		}
		
		// choose the root with the minimum geodesic distance to the curve
		double maxDot = -1.0;
		double closestRoot = Double.NaN;
		for( Double root : roots )
		{
			double dot = getPoint( root ).getDot( p );
			if( dot > maxDot )
			{
				maxDot = dot;
				closestRoot = root;
			}
		}
		assert( !Double.isNaN( closestRoot ) );
		return closestRoot;
	}
	
	@Override
	public Vector3 getPoint( double angle )
	{
		Vector3 p = new Vector3();
		Vector3 d = new Vector3();
		if( !m_rdcCurve.getPointAndDerivatives( p, d, null, angle ) )
		{
			return null;
		}
		
		// now, offset the point (rotation about the tangent vector by the geodesic distance)
		double l = d.getLength();
		double u = Math.cos( m_geodesicDistance );
		double v = Math.sin( m_geodesicDistance );
		double vol = v/l;
		Vector3 out = new Vector3(
			u*p.x + vol*( p.z*d.y - p.y*d.z ),
			u*p.y + vol*( p.x*d.z - p.z*d.x ),
			u*p.z + vol*( p.y*d.x - p.x*d.y )
		);
		
		// make sure the point is actually the right distance from the rdc curve
		assert( CompareReal.eq( p.getDot( out ), Math.cos( m_geodesicDistance ) ) );
		
		return out;
	}
	
	@Override
	public Vector3 getDerivative( double angle )
	{
		Vector3 p = new Vector3();
		Vector3 d = new Vector3();
		Vector3 e = new Vector3();
		if( !m_rdcCurve.getPointAndDerivatives( p, d, e, angle ) )
		{
			return null;
		}
		
		double l = d.getLength();
		double dl = d.getDot( e )/l;
		double u = Math.cos( m_geodesicDistance );
		double v = Math.sin( m_geodesicDistance );
		double voll = v/l/l;
		Vector3 out = new Vector3(
			u*d.x + voll*( l*( p.z*e.y - p.y*e.z ) - dl*( p.z*d.y - p.y*d.z ) ),
			u*d.y + voll*( l*( p.x*e.z - p.z*e.x ) - dl*( p.x*d.z - p.z*d.x ) ),
			u*d.z + voll*( l*( p.y*e.x - p.x*e.y ) - dl*( p.y*d.x - p.x*d.y ) )
		);
		
		return out;
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		Vector3 offsetPoint = getPoint( getAngle( p ) );
		return CompareReal.eq( p.getDot( offsetPoint ), 1.0, epsilon );
	}
	
	@Override
	public boolean hasLength( )
	{
		return m_rdcCurve.hasLength();
	}
	
	@Override
	public RdcOffsetCurveArc newClosedArc( )
	{
		return new RdcOffsetCurveArc( this );
	}
	
	@Override
	public RdcOffsetCurveArc newClosedArc( Vector3 p )
	{
		return new RdcOffsetCurveArc( this, p );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_rdcCurve.hashCode(),
			Double.valueOf( m_geodesicDistance ).hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof RdcOffsetCurve )
		{
			return equals( (RdcOffsetCurve)other );
		}
		return false;
	}
	
	public boolean equals( RdcOffsetCurve other )
	{
		return m_rdcCurve.equals( other.m_rdcCurve )
			&& m_geodesicDistance == other.m_geodesicDistance;
	}
}
