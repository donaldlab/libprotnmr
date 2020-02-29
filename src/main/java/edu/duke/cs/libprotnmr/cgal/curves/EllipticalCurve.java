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

import Jama.Matrix;
import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector2;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EllipticalCurve extends ParametricCurve implements Serializable
{
	private static final long serialVersionUID = -3524464981347312914L;
	
	private static final Logger m_log = LogManager.getLogger(EllipticalCurve.class);
	
	
	/*********************************
	 *   Definitions
	 *********************************/
	
	public enum Mode
	{
		Surface
		{
			@Override
			public void project( Vector3 p, Vector3 apex )
			{
				Vector3 pma = new Vector3( p );
				pma.subtract( apex );
				double t = 2.0*( 1 - p.getDot( apex ) )/pma.getDot( pma );
				pma.scale( t );
				pma.add( apex );
				p.set( pma );
			}
		},
		Origin
		{
			@Override
			public void project( Vector3 p, Vector3 apex )
			{
				p.normalize();
			}
		};
		
		public abstract void project( Vector3 p, Vector3 apex );
	}
	
	
	/*********************************
	 *   Data Members
	 *********************************/

	private Vector3 m_apex;
	private Vector3 m_axis;
	private double m_majorTheta;
	private double m_minorTheta;
	private Matrix3 m_rotConeToMol;
	private Matrix3 m_rotMolToCone;
	private Mode m_mode;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public EllipticalCurve( Vector3 apex, Vector3 axis, Vector3 majorAxis, double majorTheta, double minorTheta )
	{
		checkThetas( majorTheta, minorTheta );
		
		m_apex = apex;
		m_axis = axis;
		m_majorTheta = majorTheta;
		m_minorTheta = minorTheta;
		computeRotations( axis, majorAxis );
		m_mode = getMode( apex );
	}
	
	public EllipticalCurve( Vector3 apex, Matrix3 rotConeToMol, double majorTheta, double minorTheta )
	{
		checkThetas( majorTheta, minorTheta );
		
		m_apex = apex;
		m_axis = new Vector3();
		rotConeToMol.getZAxis( m_axis );
		m_majorTheta = majorTheta;
		m_minorTheta = minorTheta;
		m_rotConeToMol = rotConeToMol;
		computeRotations( m_rotConeToMol );
		m_mode = getMode( apex );
	}
	
	public EllipticalCurve( EllipticalCurve other )
	{
		m_apex = new Vector3( other.m_apex );
		m_axis = new Vector3( other.m_axis );
		m_majorTheta = other.m_majorTheta;
		m_minorTheta = other.m_minorTheta;
		m_rotConeToMol = new Matrix3( other.m_rotConeToMol );
		m_rotMolToCone = new Matrix3( other.m_rotMolToCone );
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getApex( )
	{
		return m_apex;
	}
	
	public Vector3 getAxis( )
	{
		return m_axis;
	}
	
	public void getMajorAxis( Vector3 out )
	{
		m_rotConeToMol.getXAxis( out );
	}
	
	public void getMinorAxis( Vector3 out )
	{
		m_rotConeToMol.getYAxis( out );
	}
	
	public double getMajorTheta( )
	{
		return m_majorTheta;
	}
	
	public double getMinorTheta( )
	{
		return m_minorTheta;
	}
	
	public Matrix3 getRotConeToMol( )
	{
		return m_rotConeToMol;
	}
	
	public Matrix3 getRotMolToCone( )
	{
		return m_rotMolToCone;
	}
	
	public Mode getMode( )
	{
		return m_mode;
	}
	
	public Vector3 getCenter( )
	{
		return Intersector.getSphereRayIntersectionPoint( m_apex, m_axis );
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static EllipticalCurve newFromSamples( Vector3 apex, Vector3 axis, List<Vector3> samples )
	{
		// transform the samples into cone space
		Matrix3 rotation = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( rotation, axis );
		rotation.transpose();
		List<Vector3> projectedSamples = new ArrayList<Vector3>();
		for( Vector3 p : samples )
		{
			p = new Vector3( p );
			p.subtract( apex );
			
			// this is unstable for points too close to the apex
			if( p.getSquaredLength() < 1e-4 )
			{
				continue;
			}
			
			p.scale( 1.0/p.getDot( axis ) );
			rotation.multiply( p );
			projectedSamples.add( p );
		}
		
		// solve the general implicit equation for conics without translation: Ax^2 + 2Bxy + Cy^2 + D = 0
		Matrix lhs = new Matrix( projectedSamples.size(), 3 );
		Matrix rhs = new Matrix( projectedSamples.size(), 1 );
		for( int i=0; i<projectedSamples.size(); i++ )
		{
			Vector3 p = projectedSamples.get( i );
			lhs.set( i, 0, p.x*p.x );
			lhs.set( i, 1, p.x*p.y );
			lhs.set( i, 2, p.y*p.y );
			rhs.set( i, 0, -1.0 );
		}
		Matrix coefficients = lhs.solve( rhs );
		
		double A = coefficients.get( 0, 0 );
		double B = coefficients.get( 1, 0 )/2.0;
		double C = coefficients.get( 2, 0 );
		double D = 1.0;
		
		// is this even an ellipse?
		if( B*B - 4.0*A*C >= 0.0 )
		{
			// nope, doesn't look like it
			return null;
		}
		
		// conversion from general implicit eqn to ellipse parameters adapted from:
		// http://mathworld.wolfram.com/Ellipse.html
		
		double amc = A - C;
		double radical = Math.sqrt( amc*amc + 4*B*B );
		
		// does it have real axes?
		if( CompareReal.lte( radical - A - C, 0.0 ) || CompareReal.lte( -radical - A - C, 0.0 ) )
		{
			return null;
		}
		
		double semiMajor = Math.sqrt( 2.0*D/( radical - A - C ) );
		double semiMinor = Math.sqrt( 2.0*D/( -radical - A - C ) );
		
		double theta = 0.0;
		if( B == 0.0 )
		{
			if( A > C )
			{
				theta = Math.PI/2.0;
			}
		}
		else
		{
			if( A < C )
			{
				theta = Math.atan( 2.0*B/(A-C) )/2.0;
			}
			else
			{
				theta = Math.atan( 2.0*B/(A-C) )/2.0 + Math.PI/2.0;
			}
		}
		
		// show the fit of the ellipse
		if( false || Double.isNaN( Math.atan( semiMajor ) ) || Double.isNaN( Math.atan( semiMinor ) ) )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin );
			KinemageBuilder.appendPoints( kin, projectedSamples, "Projected Points", KinemageColor.Cobalt, 2 );
			
			// sample points from the computed ellipse
			final int NumSamples = 512;
			List<Vector3> ellipse = new ArrayList<Vector3>( NumSamples );
			Quaternion q = new Quaternion();
			Quaternion.getRotation( q, Vector3.getUnitZ(), theta );
			for( int i=0; i<NumSamples; i++ )
			{
				double t = (double)i / (double)NumSamples * Math.PI*2.0;
				Vector3 point = new Vector3(
					Math.cos( t ) * semiMajor,
					Math.sin( t ) * semiMinor,
					1.0
				);
				q.rotate( point );
				ellipse.add( point );
			}
			KinemageBuilder.appendChain( kin, ellipse, true, "Computed Ellipse", KinemageColor.Yellow, 1 );
			
			// sample points from the computed quadric thingy more directly
			final double Scale = 2.0;
			List<Vector3> ellipse2 = new ArrayList<Vector3>();
			for( int i=0; i<NumSamples; i++ )
			{
				double x = ( (double)i/(double)( NumSamples - 1 )*2.0 - 1.0 )*Scale;
				
				double a = C;
				double b = 2.0*( B*x );
				double c = A*x*x + D;
				
				double d = b*b - 4.0*a*c;
				if( d >= 0.0 )
				{
					double e = Math.sqrt( d );
					ellipse2.add( new Vector3( x, ( -b + e )/2.0/a, 1.0 ) );
					ellipse2.add( new Vector3( x, ( -b - e )/2.0/a, 1.0 ) );
				}
			}
			KinemageBuilder.appendPoints( kin, ellipse2, "Computed Quadric", KinemageColor.Green, 2 );
			
			new KinemageWriter().show( kin );
		}
		
		// convert the 2d ellipse to the elliptical cone
		Vector3 majorAxis = Vector3.getUnitX();
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, Vector3.getUnitZ(), theta );
		q.rotate( majorAxis );
		rotation.transpose();
		rotation.multiply( majorAxis );
		
		EllipticalCurve curve = new EllipticalCurve( apex, axis, majorAxis, Math.atan( semiMajor ), Math.atan( semiMinor ) );
		
		// warn if the fit is bad
		double rmsd = curve.getRmsd( samples );
		if( rmsd > 1e-8 )
		{
			m_log.warn( "The RMSD is a bit high... maybe these points aren't on an elliptical cone? Or the apex/axis are off." );
		}
		
		return curve;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getAngle( Vector3 p )
	{
		// PRECONDITION: p is on the curve
		assert( containsPoint( p, 1e-4 ) );
		
		// transform into cone space
		Vector3 copy = new Vector3( p );
		copy.subtract( m_apex );
		m_rotMolToCone.multiply( copy );
		
		// if this point is too close to the origin, then we have a problem
		if( CompareReal.eq( copy.getSquaredLength(), 0.0, 1e-12 ) )
		{
			// UNDONE: we need to find a way to do this! use algebra? Also, might need to split the curve up into parts
			throw new Error( "Can't getAngle() for point too close to the double point!" );
		}
		
		double a = Math.tan( m_majorTheta );
		double b = Math.tan( m_minorTheta );
		double factor = Math.signum( copy.z );
		double angle = Math.atan2( factor*copy.y/b, factor*copy.x/a );
		assert( getPoint( angle ).approximatelyEquals( p, 1e-8 ) );
		return angle;
	}
	
	@Override
	public Vector3 getPoint( double angle )
	{
		angle = CircleRange.mapZeroToTwoPi( angle );
		
		// sample a point from the elliptical cross-section at z=1
		double a = Math.tan( m_majorTheta );
		double b = Math.tan( m_minorTheta );
		Vector3 p = new Vector3(
			a*Math.cos( angle ),
			b*Math.sin( angle ),
			1.0
		);
		
		// translate to the cone at the apex
		Vector3 apexInConeSpace = new Vector3( m_apex );
		m_rotMolToCone.multiply( apexInConeSpace );
		p.add( apexInConeSpace );
		
		// project along the cone onto the sphere
		m_mode.project( p, apexInConeSpace );
		
		// rotate into the molecular frame
		m_rotConeToMol.multiply( p );
		
		// DEBUG
		if( false )
		{
			assert( containsPoint( p ) );
			double angleAgain = getAngle( p );
			if( !CircleRange.isEq( angleAgain, angle ) )
			{
				System.out.println( "angle: " + Math.toDegrees( angle ) + "\tangleAgain: " + Math.toDegrees( angleAgain ) );
				assert( false );
			}
		}
		
		return p;
	}
	
	@Override
	public Vector3 getDerivative( double angle )
	{
		// get the cone parameters
		double a = Math.tan( m_majorTheta );
		double b = Math.tan( m_minorTheta );
		double a2 = a*a;
		double b2 = b*b;
		Vector3 apexInConeSpace = new Vector3( m_apex );
		m_rotMolToCone.multiply( apexInConeSpace );
		double c = apexInConeSpace.x;
		double d = apexInConeSpace.y;
		double e = apexInConeSpace.z;
		
		double x = Math.cos( angle );
		double y = Math.sin( angle );
		double x2 = x*x;
		double y2 = y*y;
		
		double p = -2*( a*c*x + b*d*y + e );
		double pp = -2*( b*d*x - a*c*y );
		double q = a2*x2 + b2*y2;
		double qp = 2*(b2-a2)*x*y;
		
		double t = p/q;
		double tp = ( pp*q - p*qp )/q/q;
		
		Vector3 tangent = new Vector3(
			a*( tp*x - t*y ),
			b*( tp*y + t*x ),
			tp
		);
		
		// rotate into the molecular frame
		m_rotConeToMol.multiply( tangent );
		
		return tangent;
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		if( !CompareReal.eq( p.getSquaredLength(), 1.0, epsilon ) )
		{
			return false;
		}
		
		return CompareReal.eq( getDifferenceOfAxialSquares( p ), 0, epsilon );
	}
	
	public boolean outSideExcludesPoint( Vector3 p )
	{
		return CompareReal.lte( getDifferenceOfAxialSquares( p ), 0 );
	}
	
	public boolean outSideExcludesPoint( Vector3 p, double epsilon )
	{
		return CompareReal.lte( getDifferenceOfAxialSquares( p ), 0, epsilon );
	}
	
	@Override
	public boolean hasLength( )
	{
		return !CompareReal.eq( m_majorTheta, 0.0 ) && !CompareReal.eq( m_minorTheta, 0.0 );
	}
	
	@Override
	public EllipticalCurveArc newClosedArc( )
	{
		return new EllipticalCurveArc( this );
	}
	
	@Override
	public EllipticalCurveArc newClosedArc( Vector3 p )
	{
		return new EllipticalCurveArc( this, p );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_apex.hashCode(),
			m_axis.hashCode(),
			Double.valueOf( m_majorTheta ).hashCode(),
			Double.valueOf( m_minorTheta ).hashCode(),
			m_rotConeToMol.hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof EllipticalCurve )
		{
			return equals( (EllipticalCurve)other );
		}
		return false;
	}
	
	public boolean equals( EllipticalCurve other )
	{
		return m_apex.equals( other.m_apex )
			&& m_axis.equals( other.m_axis )
			&& m_majorTheta == other.m_majorTheta
			&& m_minorTheta == other.m_minorTheta
			&& m_rotConeToMol.equals( other.m_rotConeToMol );
	}
	
	public double getSquaredAxialDistance( Vector3 p )
	{
		p = new Vector3( p );
		p.subtract( m_apex );
		m_rotMolToCone.multiply( p );
		
		double tan2M = Math.tan( m_majorTheta );
		tan2M *= tan2M;
		double tan2m = Math.tan( m_minorTheta );
		tan2m *= tan2m;
		
		double expectedZ = Math.sqrt( p.x*p.x/tan2M + p.y*p.y/tan2m );
		double observedZ = Math.abs( p.z );
		double dz = expectedZ - observedZ;
		return dz*dz;
	}
	
	public double getDifferenceOfAxialSquares( Vector3 p )
	{
		// rotate the point into cone space
		p = new Vector3( p );
		p.subtract( m_apex );
		m_rotMolToCone.multiply( p );
		
		double tan2M = Math.tan( m_majorTheta );
		tan2M *= tan2M;
		double tan2m = Math.tan( m_minorTheta );
		tan2m *= tan2m;
		
		return p.x*p.x/tan2M + p.y*p.y/tan2m - p.z*p.z;
	}
	
	public double getRmsd( List<Vector3> points )
	{
		double sum = 0.0;
		for( Vector3 p : points )
		{
			sum += getSquaredAxialDistance( p );
		}
		return Math.sqrt( sum/(double)points.size() );
	}
	
	public List<Double> getAnglesWithTangentOrthogonalTo( Vector3 v )
	{
		// rotate the vector into cone space
		v = new Vector3( v );
		m_rotMolToCone.multiply( v );
		
		// get the cone parameters
		double a = Math.tan( m_majorTheta );
		double b = Math.tan( m_minorTheta );
		Vector3 apexInConeSpace = new Vector3( m_apex );
		m_rotMolToCone.multiply( apexInConeSpace );
		double c = apexInConeSpace.x;
		double d = apexInConeSpace.y;
		double e = apexInConeSpace.z;
		
		// solve for the parameter angles
		List<Vector2> intersections = new ArrayList<Vector2>();
		getTangentQueryIntersectionPoints( intersections, a, b, c, d, e, v.x, v.y, v.z );
		assert( intersections.size() == 2 );
		
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin );
			KinemageBuilder.appendPoints( kin, Vector3.asList( intersections ), "Intersections", KinemageColor.Cobalt, 6 );
			new KinemageWriter().show( kin );
		}
		
		List<Double> angles = new ArrayList<Double>( intersections.size() );
		for( Vector2 intersection : intersections )
		{
			angles.add( Math.atan2( intersection.y, intersection.x ) );
		}
		return angles;
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private void checkThetas( double majorTheta, double minorTheta )
	{
		if( Double.isNaN( majorTheta ) )
		{
			throw new IllegalArgumentException( "Major theta cannot be NaN" );
		}
		if( Double.isNaN( minorTheta ) )
		{
			throw new IllegalArgumentException( "Minor theta cannot be NaN" );
		}
	}
	
	private Mode getMode( Vector3 apex )
	{
		double lengthSq = apex.getSquaredLength();
		if( CompareReal.eq( lengthSq, 1.0 ) )
		{
			return Mode.Surface;
		}
		else if( CompareReal.eq( lengthSq, 0.0 ) )
		{
			return Mode.Origin;
		}
		
		throw new IllegalArgumentException( "The cone apex must either be on the unit sphere or be the origin." );
	}
	
	private void computeRotations( Vector3 normal, Vector3 majorAxis )
	{
		m_rotConeToMol = new Matrix3();
		Matrix3.getRightBasisFromXZ( m_rotConeToMol, majorAxis, normal );
		computeRotations( m_rotConeToMol );
	}
	
	private void computeRotations( Matrix3 rotConeToMol )
	{
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
	}
	
	private static Vector3 sampleEllipse( double t, double A, double B, double E )
	{
		return new Vector3(
			Math.sqrt( -E/A )*Math.cos( t ),
			Math.sqrt( -E/B )*Math.sin( t ),
			0.0
		);
	}
	
	private static native void getTangentQueryIntersectionPoints(
		List<Vector2> out,
		double a, double b, double c, double d, double e,
		double cx, double cy, double cz
	);
}
