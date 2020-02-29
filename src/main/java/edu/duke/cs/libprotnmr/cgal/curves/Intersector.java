package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector2;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quadratic;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.util.CircularList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Intersector
{
	private static final Logger m_log = LogManager.getLogger( Intersector.class );
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static enum Conservativity
	{
		None, // report the test result at its native precision
		TooFewIntersections,  // even if the curves overlap a little, still call it a miss
		TooManyIntersections // even if the curves are a little apart, still call it a hit
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static List<Vector3> getIntersectionPoints( CurveArc a, CurveArc b )
	{
		// get the intersection points from the supporting curves
		List<Vector3> intersectionPoints = getIntersectionPoints( a.getCurve(), b.getCurve() );
		filterPoints( intersectionPoints, a, b );
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( Curve a, CurveArc b )
	{
		// get the intersection points from the supporting curves
		List<Vector3> intersectionPoints = getIntersectionPoints( a, b.getCurve() );
		filterPoints( intersectionPoints, a, b );
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( CurveArc a, Curve b )
	{
		return getIntersectionPoints( b, a );
	}
	
	public static List<Vector3> getIntersectionPoints( Curve a, Curve b )
	{
		// HACKHACK: I guess I could do this more cleanly using reflection, but this is way easier
		if( a instanceof RdcCurve && b instanceof CircularCurve )
		{
			return getIntersectionPoints( (RdcCurve)a, (CircularCurve)b );
		}
		else if( a instanceof CircularCurve && b instanceof RdcCurve )
		{
			return getIntersectionPoints( (RdcCurve)b, (CircularCurve)a );
		}
		else if( a instanceof CircularCurve && b instanceof CircularCurve )
		{
			return getIntersectionPoints( (CircularCurve)a, (CircularCurve)b );
		}
		else if( a instanceof RdcCurve && b instanceof RdcCurve )
		{
			return getIntersectionPoints( (RdcCurve)a, (RdcCurve)b );
		}
		else if( a instanceof RdcCurve && b instanceof GeodesicCurve )
		{
			return getIntersectionPoints( (RdcCurve)a, (GeodesicCurve)b );
		}
		else if( a instanceof GeodesicCurve && b instanceof RdcCurve )
		{
			return getIntersectionPoints( (RdcCurve)b, (GeodesicCurve)a );
		}
		else if( a instanceof CircularCurve && b instanceof GeodesicCurve )
		{
			return getIntersectionPoints( (CircularCurve)a, (GeodesicCurve)b );
		}
		else if( a instanceof GeodesicCurve && b instanceof CircularCurve )
		{
			return getIntersectionPoints( (CircularCurve)b, (GeodesicCurve)a );
		}
		else if( a instanceof GeodesicCurve && b instanceof GeodesicCurve )
		{
			return getIntersectionPoints( (GeodesicCurve)a, (GeodesicCurve)b );
		}
		else if( a instanceof RdcOffsetCurve && b instanceof CircularCurve )
		{
			return OffsetIntersectionOptimizer.getIntersectionPoints( (RdcOffsetCurve)a, (CircularCurve)b );
		}
		else if( a instanceof CircularCurve && b instanceof RdcOffsetCurve )
		{
			return OffsetIntersectionOptimizer.getIntersectionPoints( (RdcOffsetCurve)b, (CircularCurve)a );
		}
		else if( a instanceof RdcOffsetCurve && b instanceof GeodesicCurve )
		{
			return OffsetIntersectionOptimizer.getIntersectionPoints( (RdcOffsetCurve)a, (GeodesicCurve)b );
		}
		else if( a instanceof GeodesicCurve && b instanceof RdcOffsetCurve )
		{
			return OffsetIntersectionOptimizer.getIntersectionPoints( (RdcOffsetCurve)b, (GeodesicCurve)a );
		}
		else if( a instanceof EllipticalCurve && b instanceof CircularCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)a, (CircularCurve)b );
		}
		else if( a instanceof CircularCurve && b instanceof EllipticalCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)b, (CircularCurve)a );
		}
		else if( a instanceof EllipticalCurve && b instanceof GeodesicCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)a, (GeodesicCurve)b );
		}
		else if( a instanceof GeodesicCurve && b instanceof EllipticalCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)b, (GeodesicCurve)a );
		}
		else if( a instanceof EllipticalCurve && b instanceof EllipticalCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)a, (EllipticalCurve)b );
		}
		else if( a instanceof EllipticalCurve && b instanceof RdcCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)a, (RdcCurve)b );
		}
		else if( a instanceof RdcCurve && b instanceof EllipticalCurve )
		{
			return getIntersectionPoints( (EllipticalCurve)b, (RdcCurve)a );
		}
		else
		{
			throw new IllegalArgumentException( "Intersection of " + a.getClass().getName() + " and " + b.getClass().getName() + " is not supported." );
		}
	}
	
	public static List<Vector3> getIntersectionPoints( RdcCurve rdcCurve, CircularCurve circularCurve )
	{
		// get the rotation from the PoF to the cone space
		Matrix3 rotPofToMol = new Matrix3();
		rdcCurve.getTensor().getRotPofToMol( rotPofToMol );
		Matrix3 rotMolToCone = new Matrix3( circularCurve.getRotConeToMol() );
		rotMolToCone.transpose();
		Matrix3 rotPofToCone = new Matrix3();
		rotPofToMol.multiplyLeft( rotPofToCone, rotMolToCone );
		
		// compute the intersection points
		double alpha = circularCurve.getConeHeight();
		double beta = circularCurve.getConeHalfWidth() * circularCurve.getConeHalfWidth();
		assert( !Double.isNaN( alpha ) );
		assert( !Double.isNaN( beta ) );
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectRdcCircularPoF(
			intersectionPoints,
			rdcCurve.getTensor().getDxx(), rdcCurve.getTensor().getDyy(), rdcCurve.getTensor().getDzz(),
			rdcCurve.getD(),
			rotPofToCone.data[0][0], rotPofToCone.data[0][1], rotPofToCone.data[0][2],
			rotPofToCone.data[1][0], rotPofToCone.data[1][1], rotPofToCone.data[1][2],
			rotPofToCone.data[2][0], rotPofToCone.data[2][1], rotPofToCone.data[2][2],
			alpha, beta
		);
		
		// rotate the points back into the molecular frame
		for( Vector3 p : intersectionPoints )
		{
			/* DEBUG
			if( false && !CompareReal.eq( p.getSquaredLength(), 1.0 ) )
			{
				Kinemage kin = new Kinemage();
				KinemageBuilder.appendAxes( kin, 1, 0.2 );
				KinemageBuilder.appendAxes( kin, rotPofToMol, "PoF" );
				KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
				KinemageBuilder.appendCurve( kin, circularCurve, "Kinematic Curve", KinemageColor.Orange, 1 );
				KinemageBuilder.appendCurve( kin, rdcCurve, "RDC Curve", KinemageColor.Cobalt, 1 );
				p = new Vector3( p );
				rotPofToMol.multiply( p );
				KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersections", KinemageColor.Lime, 7 );
				new KinemageWriter().show( kin );
			}*/
			
			// if the points aren't on the sphere, something went wrong...
			assert( CompareReal.eq( p.getSquaredLength(), 1.0 ) );
			
			rotPofToMol.multiply( p );
		}
		
		/* DEBUG
		if( false )
		{
			Vector3 rdcAxis = new Vector3();
			rdcCurve.getAxis( rdcAxis );
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendCurve( kin, circularCurve, "Kinematic Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendCurve( kin, rdcCurve, "RDC Curve", KinemageColor.Cobalt, 1 );
			KinemageBuilder.appendVector( kin, rdcAxis, "RDC Axis", KinemageColor.Cobalt, 1, 1.0 );
			for( Vector3 p : intersectionPoints )
			{
				KinemageColor color = rdcCurve.containsPoint( p ) &&  circularCurve.containsPoint( p ) ? KinemageColor.Green : KinemageColor.Red;
				KinemageBuilder.appendPoints( kin, Arrays.asList( p ), "Intersection", color, 7 );
			}
			new KinemageWriter().showAndWait( kin );
		}*/
						
		filterPoints( intersectionPoints, rdcCurve, circularCurve );
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( CircularCurve a, CircularCurve b )
	{
		checkDistinctCurves( a, b );
		
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		intersectCircularCircular(
			points,
			a.getNormal(), a.getConeHeight(),
			b.getNormal(), b.getConeHeight()
		);
		return points;
	}
	
	public static List<Vector3> getIntersectionPoints( RdcCurve rdcCurve, GeodesicCurve geodesicCurve )
	{
		// get the rotation from the PoF to the parameter space of the geodesic curve
		Matrix3 rotPofToMol = new Matrix3();
		rdcCurve.getTensor().getRotPofToMol( rotPofToMol );
		Matrix3 rotMolToParam = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( rotMolToParam, geodesicCurve.getNormal() );
		rotMolToParam.transpose();
		Matrix3 rotPofToParam = new Matrix3();
		rotPofToMol.multiplyLeft( rotPofToParam, rotMolToParam );
		
		// compute the intersection points
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectRdcGeodesicPoF(
			intersectionPoints,
			rdcCurve.getTensor().getDxx(), rdcCurve.getTensor().getDyy(), rdcCurve.getTensor().getDzz(),
			rdcCurve.getD(),
			rotPofToParam.data[0][0], rotPofToParam.data[0][1], rotPofToParam.data[0][2],
			rotPofToParam.data[1][0], rotPofToParam.data[1][1], rotPofToParam.data[1][2],
			rotPofToParam.data[2][0], rotPofToParam.data[2][1], rotPofToParam.data[2][2]
		);
		
		// rotate the points back into the molecular frame
		for( Vector3 p : intersectionPoints )
		{
			rotPofToMol.multiply( p );
		}
		
		filterPoints( intersectionPoints, rdcCurve, geodesicCurve );
		
		/* TEMP
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendCurve( kin, geodesicCurve, "Geodesic Curve", KinemageColor.Orange, 1 );
		KinemageBuilder.appendCurve( kin, rdcCurve, "RDC Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersections", KinemageColor.Lime, 7 );
		new KinemageWriter().showAndWait( kin );
		*/
		
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( RdcCurve a, RdcCurve b )
	{
		checkDistinctCurves( a, b );
		
		// get the rotation from b's POF to a's POF
		Matrix3 rotBPofToMol = new Matrix3();
		b.getTensor().getRotPofToMol( rotBPofToMol );
		Matrix3 rotAMolToPof = new Matrix3();
		a.getTensor().getRotMolToPof( rotAMolToPof );
		Matrix3 rotBToA = new Matrix3();
		rotBPofToMol.multiplyLeft( rotBToA, rotAMolToPof );
		
		// compute the intersection points
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectRdcRdc(
			intersectionPoints,
			a.getTensor().getDxx(), a.getTensor().getDyy(), a.getTensor().getDzz(), a.getD(),
			b.getTensor().getDxx(), b.getTensor().getDyy(), b.getTensor().getDzz(), b.getD(),
			rotBToA.data[0][0], rotBToA.data[1][0], rotBToA.data[2][0],
			rotBToA.data[0][1], rotBToA.data[1][1], rotBToA.data[2][1],
			rotBToA.data[0][2], rotBToA.data[1][2], rotBToA.data[2][2]
		);
		
		// rotate the points back into the molecular frame
		Matrix3 rotAPofToMol = new Matrix3();
		a.getTensor().getRotPofToMol( rotAPofToMol );
		for( Vector3 p : intersectionPoints )
		{
			rotAPofToMol.multiply( p );
		}
		
		filterPoints( intersectionPoints, a, b );
		
		/* TEMP
		Kinemage kin = new Kinemage();
		Matrix3 pofBasis = new Matrix3();
		a.getTensor().getBasis( pofBasis );
		KinemageBuilder.appendAxes( kin, pofBasis, "PoF", 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendCurve( kin, a, "RDC Curve A", KinemageColor.Orange, 1 );
		KinemageBuilder.appendCurve( kin, b, "RDC Curve B", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersections", KinemageColor.Lime, 7 );
		new KinemageWriter().show( kin );
		*/
		
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( CircularCurve circularCurve, GeodesicCurve geodesicCurve )
	{
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		intersectCircularCircular(
			points,
			circularCurve.getNormal(), circularCurve.getConeHeight(),
			geodesicCurve.getNormal(), 0.0
		);
		return points;
	}
	
	public static List<Vector3> getIntersectionPoints( GeodesicCurve a, GeodesicCurve b )
	{
		checkDistinctCurves( a, b );
		
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		intersectCircularCircular(
			points,
			a.getNormal(), 0.0,
			b.getNormal(), 0.0
		);
		return points;
	}
	
	public static List<Vector3> getIntersectionPoints( EllipticalCurve ellipticalCurve, CircularCurve circularCurve )
	{
		// rotate the cones so the elliptical cone is aligned with the coordinate axes
		Matrix3 rotMolToECone = ellipticalCurve.getRotMolToCone();
		
		Vector3 rotatedApex = new Vector3( ellipticalCurve.getApex() );
		rotMolToECone.multiply( rotatedApex );
		Vector3 rotatedNormal = new Vector3( circularCurve.getNormal() );
		rotMolToECone.multiply( rotatedNormal );
		
		// compute the intersection points
		double alpha = circularCurve.getConeHeight();
		double beta = circularCurve.getConeHalfWidth() * circularCurve.getConeHalfWidth();
		assert( !Double.isNaN( alpha ) );
		assert( !Double.isNaN( beta ) );
		double tan2M = Math.tan( ellipticalCurve.getMajorTheta() );
		tan2M *= tan2M;
		double tan2m = Math.tan( ellipticalCurve.getMinorTheta() );
		tan2m *= tan2m;
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectEllipticalCircular(
			intersectionPoints,
			rotatedApex.x, rotatedApex.y, rotatedApex.z,
			tan2M, tan2m,
			rotatedNormal.x, rotatedNormal.y, rotatedNormal.z,
			alpha
		);
		
		// rotate the points back into the molecular frame
		Matrix3 rotEConeToMol = new Matrix3( rotMolToECone );
		rotEConeToMol.transpose();
		for( Vector3 p : intersectionPoints )
		{
			// if the points aren't on the sphere, something went wrong...
			assert( CompareReal.eq( p.getSquaredLength(), 1.0 ) );
			
			rotEConeToMol.multiply( p );
		}
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendCurve( kin, circularCurve, "Circular Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendCurve( kin, ellipticalCurve, "Elliptical Curve", KinemageColor.Cobalt, 1 );
			for( Vector3 p : intersectionPoints )
			{
				KinemageColor color = ellipticalCurve.containsPoint( p ) &&  circularCurve.containsPoint( p ) ? KinemageColor.Green : KinemageColor.Red;
				KinemageBuilder.appendPoints( kin, Arrays.asList( p ), "Intersection", color, 7 );
			}
			new KinemageWriter().showAndWait( kin );
		}
		
		filterPoints( intersectionPoints, ellipticalCurve, circularCurve );
		return intersectionPoints;
	}
	
	public static List<Vector3> getIntersectionPoints( EllipticalCurve surfaceCurve, EllipticalCurve originCurve )
	{
		if( true ) throw new Error( "This intersection test is close, but has precision problems! Fix it before using it." );
		
		// only surface vs centered elliptical curve intersections are allowed
		if( surfaceCurve.getMode() == EllipticalCurve.Mode.Surface && originCurve.getMode() == EllipticalCurve.Mode.Origin )
		{
			// all is well
		}
		else if( surfaceCurve.getMode() == EllipticalCurve.Mode.Origin && originCurve.getMode() == EllipticalCurve.Mode.Surface )
		{
			// need to swap the arguments
			EllipticalCurve temp = surfaceCurve;
			surfaceCurve = originCurve;
			originCurve = temp;
		}
		else
		{
			throw new IllegalArgumentException( "Intersection between elliptical curves is only allowed between one origin curve and one surface curve." );
		}
		
		// rotate the surface cone so it is aligned with the coordinate axes
		Matrix3 rotMolToSCone = surfaceCurve.getRotMolToCone();
		Vector3 rotatedApex = new Vector3( surfaceCurve.getApex() );
		rotMolToSCone.multiply( rotatedApex );
		
		// get the rotation describing the origin cone's axis in the surface cone's coordinate space
		Vector3 rotatedAxis = new Vector3( originCurve.getAxis() );
		rotMolToSCone.multiply( rotatedAxis );
		Matrix3 rot = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( rot, rotatedAxis );
		
		// get the cone widths
		double surfaceTanM = Math.tan( surfaceCurve.getMajorTheta() );
		double surfaceTanm = Math.tan( surfaceCurve.getMinorTheta() );
		double originTanM = Math.tan( originCurve.getMajorTheta() );
		double originTanm = Math.tan( originCurve.getMinorTheta() );
		
		// finally, compute the intersections
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectEllipticalElliptical(
			intersectionPoints,
			rotatedApex.x, rotatedApex.y, rotatedApex.z,
			surfaceTanM, surfaceTanm, originTanM, originTanm, 
			rot.data[0][0], rot.data[1][0], rot.data[2][0],
			rot.data[0][1], rot.data[1][1], rot.data[2][1],
			rot.data[0][2], rot.data[1][2], rot.data[2][2]
		);
		
		// rotate the points back into the molecular frame
		Matrix3 rotSConeToMol = new Matrix3( rotMolToSCone );
		rotSConeToMol.transpose();
		for( Vector3 p : intersectionPoints )
		{
			// if the points aren't on the sphere, something went wrong...
			assert( CompareReal.eq( p.getSquaredLength(), 1.0 ) );
			
			rotSConeToMol.multiply( p );
		}
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendCurve( kin, surfaceCurve, "Surface Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendCurve( kin, originCurve, "Origin Curve", KinemageColor.Cobalt, 1 );
			for( Vector3 p : intersectionPoints )
			{
				KinemageColor color = surfaceCurve.containsPoint( p ) &&  originCurve.containsPoint( p ) ? KinemageColor.Green : KinemageColor.Red;
				KinemageBuilder.appendPoints( kin, Arrays.asList( p ), "Intersection", color, 7 );
			}
			new KinemageWriter().showAndWait( kin );
		}

		filterPoints( intersectionPoints, surfaceCurve, originCurve );
		return intersectionPoints;
	}

	public static List<Vector3> getIntersectionPoints( EllipticalCurve ellipticalCurve, GeodesicCurve geodesicCurve )
	{
		CircularCurve circularCurve = new CircularCurve( geodesicCurve.getNormal(), Math.PI/2.0 );
		return getIntersectionPoints( ellipticalCurve, circularCurve );
	}
	
	public static List<Vector3> getIntersectionPoints( EllipticalCurve ellipticalCurve, RdcCurve rdcCurve )
	{
		// rotate the elliptical curve into the PoF of the RDC curve
		Matrix3 rotMolToPof = new Matrix3();
		rdcCurve.getTensor().getRotMolToPof( rotMolToPof );
		Matrix3 rotatedBasis = new Matrix3( ellipticalCurve.getRotConeToMol() );
		rotMolToPof.multiply( rotatedBasis );
		
		// get the cone parameters
		double a = Math.tan( ellipticalCurve.getMajorTheta() );
		double b = Math.tan( ellipticalCurve.getMinorTheta() );
		Vector3 rotatedApex = new Vector3( ellipticalCurve.getApex() );
		rotMolToPof.multiply( rotatedApex );
		
		CurvesCgal.getInstance();
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		intersectRdcEllipticalConePof(
			intersectionPoints,
			rdcCurve.getTensor().getDxx(), rdcCurve.getTensor().getDyy(), rdcCurve.getTensor().getDzz(), rdcCurve.getD(),
			rotatedBasis.data[0][0], rotatedBasis.data[1][0], rotatedBasis.data[2][0],
			rotatedBasis.data[0][1], rotatedBasis.data[1][1], rotatedBasis.data[2][1],
			rotatedBasis.data[0][2], rotatedBasis.data[1][2], rotatedBasis.data[2][2],
			a, b,
			rotatedApex.x, rotatedApex.y, rotatedApex.z
		);
		
		// rotate the points back into the molecular frame
		Matrix3 rotPofToMol = new Matrix3();
		rdcCurve.getTensor().getRotPofToMol( rotPofToMol );
		for( Vector3 p : intersectionPoints )
		{
			// if the points aren't on the sphere, something went wrong...
			assert( CompareReal.eq( p.getSquaredLength(), 1.0 ) );
			
			rotPofToMol.multiply( p );
		}
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendCurve( kin, ellipticalCurve, "Elliptical Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendCurve( kin, rdcCurve, "RDC Curve", KinemageColor.Cobalt, 1 );
			for( Vector3 p : intersectionPoints )
			{
				KinemageColor color = ellipticalCurve.containsPoint( p ) && rdcCurve.containsPoint( p ) ? KinemageColor.Green : KinemageColor.Red;
				KinemageBuilder.appendPoints( kin, Arrays.asList( p ), "Intersection", color, 5 );
			}
			new KinemageWriter().showAndWait( kin );
		}

		filterPoints( intersectionPoints, ellipticalCurve, rdcCurve );
		return intersectionPoints;
	}
	
	public static List<Vector3> getRdcCurveDihedralIntersectionPoints( AlignmentTensor tensor, double rdcValue, Matrix3 rotPoFZToProjectAxis, double theta )
	{
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		getRdcCurveDihedralIntersectionPoints(
			points,
			tensor.getDxx(), tensor.getDyy(), tensor.getDzz(),
			rotPoFZToProjectAxis.data[0][0], rotPoFZToProjectAxis.data[0][1], rotPoFZToProjectAxis.data[0][2],
			rotPoFZToProjectAxis.data[1][0], rotPoFZToProjectAxis.data[1][1], rotPoFZToProjectAxis.data[1][2],
			rotPoFZToProjectAxis.data[2][0], rotPoFZToProjectAxis.data[2][1], rotPoFZToProjectAxis.data[2][2],
			rdcValue, theta
		);
		return points;
	}
	
	public static List<Vector3> getHyperbolaUnitCircleIntersectionPoints( double a, double b, double c, double d )
	{
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		getHyperbolaUnitCircleIntersectionPoints( points, a, b, c, d );
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			
			KinemageBuilder.appendPoint( kin, new Vector3( c, d, 0.0 ), "Center", KinemageColor.Cobalt, 7 );
			
			// sample the hyperbola
			double a2 = a*a;
			double b2 = b*b;
			final int NumSamples = 100;
			List<Vector3> samples = new ArrayList<Vector3>();
			for( int i=0; i<NumSamples; i++ )
			{
				double x = (double)i/(double)( NumSamples - 1 )*2.0 - 1.0;
				double x2 = x*x;
				for( double y : Quadratic.solve( 1.0/b2,
					-2.0*d/b2,
					-1.0/a2*x2 + 2.0*c/a2*x + d*d/b2 - c*c/a2 - 1.0 ) )
				{
					samples.add( new Vector3( x, y, 0.0 ) );
				}
			}
			KinemageBuilder.appendPoints( kin, samples, "Hyperbola", KinemageColor.Cobalt, 2 );
			
			KinemageBuilder.appendPoints( kin, points, "Intersection points", KinemageColor.Green, 7 );
			new KinemageWriter().showAndWait( kin );
		}
		
		return points;
	}
	
	public static List<Vector3> getParabolaUnitCircleIntersectionPoints( double a, double b, double c )
	{
		CurvesCgal.getInstance();
		List<Vector3> points = new ArrayList<Vector3>();
		getParabolaUnitCircleIntersectionPoints( points, a, b, c );
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			
			KinemageBuilder.appendPoint( kin, new Vector3( b, c, 0.0 ), "Center", KinemageColor.Cobalt, 7 );
			
			// sample the parabola
			final int NumSamples = 100;
			List<Vector3> samples = new ArrayList<Vector3>();
			for( int i=0; i<NumSamples; i++ )
			{
				double x = (double)i/(double)( NumSamples - 1 )*2.0 - 1.0;
				double y = (x-b)*(x-b)/a + c;
				samples.add( new Vector3( x, y, 0.0 ) );
			}
			KinemageBuilder.appendPoints( kin, samples, "Parabola", KinemageColor.Cobalt, 2 );
			
			KinemageBuilder.appendPoints( kin, points, "Intersection points", KinemageColor.Green, 7 );
			new KinemageWriter().showAndWait( kin );
		}
		
		return points;
	}
	
	public static List<Vector2> sampleRdcCurveProjectionZ( AlignmentTensor tensor, double rdcValue, Matrix3 rotPoFZToProjectAxis )
	{
		CurvesCgal.getInstance();
		List<Vector2> points = new ArrayList<Vector2>();
		sampleRotatedRdcCurve(
			points,
			tensor.getDxx(), tensor.getDyy(), tensor.getDzz(),
			rotPoFZToProjectAxis.data[0][0], rotPoFZToProjectAxis.data[0][1], rotPoFZToProjectAxis.data[0][2],
			rotPoFZToProjectAxis.data[1][0], rotPoFZToProjectAxis.data[1][1], rotPoFZToProjectAxis.data[1][2],
			rotPoFZToProjectAxis.data[2][0], rotPoFZToProjectAxis.data[2][1], rotPoFZToProjectAxis.data[2][2],
			rdcValue
		);
		return points;
	}
	
	public static List<Vector3> getEllipseOptima( CircularCurve circle, EllipticalCurve ellipse )
	{
		// rotate the elliptical cone into the frame of the circular cone
		Vector3 apex = new Vector3( ellipse.getApex() );
		Matrix3 rotEConeToMol = new Matrix3( ellipse.getRotConeToMol() );
		circle.getRotMolToCone().multiply( apex );
		circle.getRotMolToCone().multiply( rotEConeToMol );
		
		double a2 = Math.tan( ellipse.getMajorTheta() );
		a2 *= a2;
		double b2 = Math.tan( ellipse.getMinorTheta() );
		b2 *= b2;
		
		// Holy Crap! Having z be constant makes my life so much easier...
		final double z = circle.getConeHeight();
		
		double c = apex.x;
		double d = apex.y;
		double e = apex.z;
		
		double f = rotEConeToMol.data[0][0];
		double g = rotEConeToMol.data[1][0];
		double h = rotEConeToMol.data[2][0];
		double i = rotEConeToMol.data[0][1];
		double j = rotEConeToMol.data[1][1];
		double k = rotEConeToMol.data[2][1];
		double l = rotEConeToMol.data[0][2];
		double m = rotEConeToMol.data[1][2];
		double n = rotEConeToMol.data[2][2];
	
		// get the ellipse optima from CGAL
		List<Vector3> optima = new ArrayList<Vector3>();
		CurvesCgal.getInstance();
		getEllipseOptima(
			optima,
			a2, b2, c, d, e,
			f, g, h, i, j, k, l, m, n,
			z
		);
		assert( !optima.isEmpty() );
		
		// transform the optima back to molecular space
		for( Vector3 optimum : optima )
		{
			// rotate back to molecular space
			circle.getRotConeToMol().multiply( optimum );
			
			// project along the cone back to the sphere
			Vector3 dir = new Vector3( optimum );
			dir.subtract( apex );
			double t = -2.0*apex.getDot( dir )/dir.getDot( dir );			
			optimum.set( dir );
			optimum.scale( t );
			optimum.add( apex );
			assert( CompareReal.eq( optimum.getSquaredLength(), 1.0 ) );
		}
		return optima;
	}
	
	public static boolean doCircularEllipticalConesIntersect( CircularCurve circle, EllipticalCurve ellipse )
	{
		return doCircularEllipticalConesIntersect( circle, ellipse, Conservativity.None, false );
	}
	
	public static boolean doCircularEllipticalConesIntersect( CircularCurve circle, EllipticalCurve ellipse, Conservativity convervativity )
	{
		return doCircularEllipticalConesIntersect( circle, ellipse, convervativity, false );
	}
	
	public static boolean doCircularEllipticalConesIntersect( CircularCurve circle, EllipticalCurve ellipse, Conservativity convervativity, boolean showDebug )
	{
		// the CGAL intersection function is too slow!
		//if( true ) return !Intersector.getIntersectionPoints( circle, ellipse ).isEmpty();
		
		// here's a more verbose, but much faster test
		
		// rotate the elliptical cone into the frame of the circular cone
		Vector3 apex = new Vector3( ellipse.getApex() );
		Matrix3 rotEConeToMol = new Matrix3( ellipse.getRotConeToMol() );
		circle.getRotMolToCone().multiply( apex );
		circle.getRotMolToCone().multiply( rotEConeToMol );
		
		double a2 = Math.tan( ellipse.getMajorTheta() );
		a2 *= a2;
		double b2 = Math.tan( ellipse.getMinorTheta() );
		b2 *= b2;
		
		// Holy Crap! Having z be constant makes my life so much easier...
		final double z = circle.getConeHeight();
		
		double c = apex.x;
		double d = apex.y;
		double e = apex.z;
		
		double f = rotEConeToMol.data[0][0];
		double g = rotEConeToMol.data[1][0];
		double h = rotEConeToMol.data[2][0];
		double i = rotEConeToMol.data[0][1];
		double j = rotEConeToMol.data[1][1];
		double k = rotEConeToMol.data[2][1];
		double l = rotEConeToMol.data[0][2];
		double m = rotEConeToMol.data[1][2];
		double n = rotEConeToMol.data[2][2];
	
		// get the ellipse optima from CGAL
		List<Vector3> optima = new ArrayList<Vector3>();
		CurvesCgal.getInstance();
		getEllipseOptima(
			optima,
			a2, b2, c, d, e,
			f, g, h, i, j, k, l, m, n,
			z
		);
		assert( !optima.isEmpty() );
		
		// get the min and max distances
		double minDist = Double.POSITIVE_INFINITY;
		double maxDist = 0.0;
		for( Vector3 optimum : optima )
		{
			double dist = Math.sqrt( optimum.x*optimum.x + optimum.y*optimum.y );
			minDist = Math.min( minDist, dist );
			maxDist = Math.max( maxDist, dist );
		}
		
		// calculate the discriminant of the conic
		// implicit equation of the ellipse: rxx + syy + txy + ux + vy + w
		double r = f*f/a2 + i*i/b2 - l*l;
		double s = g*g/a2 + j*j/b2 - m*m;
		double t = 2*( f*g/a2 + i*j/b2 - l*m );
		double discriminant = t*t - 4*r*s;
		
		// HACKHACK: this next part is a little wonky, so here's the deal:
		// Since this function and getIntersectionPoints() have slightly different precision, that causes problems
		// They might not always agree on the answer, and the agreement matters since we're generally only
		// using this test to find a curve that just touches another curve, so precision is really important here
		// Because of the way this function is used, we'll assume getIntersectionPoints() is ground truth and this fn needs to match it
		// So, that means this function must always return true any time getIntersectionPoints() finds intersections
		// The only way I've found so far to guarantee that is to modify this function slightly to be a bit more conservative
		// ie, if the two cones are JUST BARELY touching, call it a miss anyway because we can't risk saying they touch,
		// but then getIntersectionPoints() gives no intersections.
		// so we do that by using CompareReal.gte(), .lte() instead of >=, <=
		
		final double HackEpsilon = 1e-14; // just a little kludge is all we need to get the precisions to agree
		switch( convervativity )
		{
			case None:
				// how we interpret the distances depends on the type of conic
				if( discriminant < 0.0 ) // ellipse
				{
					return circle.getRadius() >= minDist && circle.getRadius() <= maxDist;
				}
				else // parabola or hyperbola
				{
					return circle.getRadius() >= minDist;
				}
			
			case TooFewIntersections:
				if( discriminant < 0.0 ) // ellipse
				{
					// UNDONE: fix me
					return !CompareReal.lte( circle.getRadius(), minDist, HackEpsilon )
						&& !CompareReal.gte( circle.getRadius(), maxDist, HackEpsilon );
				}
				else // parabola or hyperbola
				{
					return !CompareReal.lte( circle.getRadius(), minDist, HackEpsilon );
				}
			
			case TooManyIntersections:
				if( discriminant < 0.0 ) // ellipse
				{
					return CompareReal.gte( circle.getRadius(), minDist, HackEpsilon )
						&& CompareReal.lte( circle.getRadius(), maxDist, HackEpsilon );
				}
				else // parabola or hyperbola
				{
					return CompareReal.gte( circle.getRadius(), minDist, HackEpsilon );
				}
		}
		
		// stupid compiler... all control paths already have a return value. *sigh*
		assert( false );
		return false;
	}
	
	public static boolean doFaceCapIntersect( Face face, CircularCurve cap )
	{
		// easy test first: does any face vertex lie in the cap?
		for( Vertex v : face.vertices() )
		{
			if( cap.enclosesPoint( v.getPoint() ) )
			{
				return true;
			}
		}
		
		// do the face and cap intersect at their boundaries?
		if( !face.getBoundaryIntersections( cap ).isEmpty() )
		{
			return true;
		}
		
		// most expensive test last: is the cap entirely inside the face?
		if( face.containsPoint( cap.getNormal() ) )
		{
			return true;
		}
		
		// no intersection
		return false;
	}
	
	public static List<Vector3> getSphereLineIntersectionPoints( Vector3 point, Vector3 direction )
	{
		if( !CompareReal.eq( direction.getSquaredLength(), 1.0 ) )
		{
			throw new IllegalArgumentException( "direction must be a unit vector" );
		}
		
		List<Vector3> intersections = new ArrayList<Vector3>();
		for( double t : Quadratic.solve( 1.0, 2.0*point.getDot( direction ), point.getSquaredLength() - 1.0 ) )
		{
			Vector3 intersection = new Vector3( direction );
			intersection.scale( t );
			intersection.add( point );
			intersections.add( intersection );
		}
		return intersections;
	}
	
	public static Vector3 getSphereRayIntersectionPoint( Vector3 point, Vector3 direction )
	{
		if( !CompareReal.eq( point.getSquaredLength(), 1.0 ) )
		{
			throw new IllegalArgumentException( "point must be a unit vector" );
		}
		if( !CompareReal.eq( direction.getSquaredLength(), 1.0 ) )
		{
			throw new IllegalArgumentException( "direction must be a unit vector" );
		}
		
		double t = -2.0*point.getDot( direction );
		Vector3 intersection = new Vector3( direction );
		intersection.scale( t );
		intersection.add( point );
		return intersection;
	}
	
	public static List<Vector3> getApproximateIntersectionPoints( Curve a, Curve b, double resolutionRadians )
	{
		checkDistinctCurves( a, b );
		
		List<Vector3> aSamples = a.samplePoints( resolutionRadians );
		List<Vector3> bSamples = b.samplePoints( resolutionRadians );
		
		// brute force intersect all geodesic segments
		List<Vector3> intersections = new ArrayList<Vector3>();
		for( int i=0; i<aSamples.size(); i++ )
		{
			GeodesicCurveArc aArc = GeodesicCurveArc.newByPointsWithArbitraryNormal(
				CircularList.get( aSamples, i ),
				CircularList.getNext( aSamples, i )
			);
			
			// get the bounding cone
			Vector3 aAxis = aArc.getMidpoint();
			double aAngle = Math.acos( aArc.getSource().getDot( aArc.getTarget() ) );
			
			for( int j=0; j<bSamples.size(); j++ )
			{
				GeodesicCurveArc bArc = GeodesicCurveArc.newByPointsWithArbitraryNormal(
					CircularList.get( bSamples, j ),
					CircularList.getNext( bSamples, j )
				);
				
				// get the bounding cone
				Vector3 bAxis = bArc.getMidpoint();
				double bAngle = Math.acos( bArc.getSource().getDot( bArc.getTarget() ) );
				
				// do the cones intersect?
				double angle = Math.acos( aAxis.getDot( bAxis ) );
				if( angle - aAngle - bAngle <= 0 )
				{
					intersections.addAll( Intersector.getIntersectionPoints( aArc, bArc ) );
				}
			}
		}
		return intersections;
	}
	
	
	/*********************************
	 *   Static Functions
	 *********************************/
	
	private static void checkDistinctCurves( Curve a, Curve b )
	{
		// the two curves must be different
		if( a.equals( b ) )
		{
			throw new IllegalArgumentException( "Can only intersect two non-identical curves!" );
		}
	}

	private static Kinemage getSphereKin( EllipticalCurve ellipse, Vector3 apex, Matrix3 rotEConeToMol, CircularCurve circle, double r, double s, double t, double u, double v, double w, double z, double minRadius, double maxRadius )
	{
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		
		// show the elliptical cone
		EllipticalCurve ellipticalCurve = new EllipticalCurve( apex, rotEConeToMol, ellipse.getMajorTheta(), ellipse.getMinorTheta() );
		KinemageBuilder.appendCurve( kin, ellipticalCurve, "Elliptical curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendPoint( kin, apex, "Apex", KinemageColor.Cobalt, 7 );
		for( Vector3 p : ellipticalCurve.samplePoints( CircleRange.newCircle().samplePoints( Math.toRadians( 30.0 ) ) ) )
		{
			KinemageBuilder.appendLine( kin, p, apex, "Ray", KinemageColor.LightGrey, 1 );
		}

		// show the circular cone
		CircularCurve circularCurve = new CircularCurve( Vector3.getUnitZ(), circle.getConeTheta() );
		KinemageBuilder.appendCurve( kin, circularCurve, "Circular curve", KinemageColor.Orange, 1 );
		
		// show the intersection of the elliptical cone with the plane of the circular curve
		List<Vector3> ellipseSamplesCartesian = new ArrayList<Vector3>();
		for( double x=-3.0; x<=3.0; x+=0.01 )
		{
			for( double y : Quadratic.solve( s, v + t*x, w + u*x + r*x*x ) )
			{
				ellipseSamplesCartesian.add( new Vector3( x, y, z ) );
			}
		}
		KinemageBuilder.appendPoints( kin, ellipseSamplesCartesian, "Ellipse on plane", KinemageColor.Cobalt, 5 );
		
		// show the bound on the elliptical curve
		List<Vector3> minBoundSamples = new ArrayList<Vector3>();
		List<Vector3> maxBoundSamples = new ArrayList<Vector3>();
		for( double theta=0.0; theta<2.0*Math.PI; theta+=Math.PI/128.0 )
		{
			double cos = Math.cos( theta );
			double sin = Math.sin( theta );
			minBoundSamples.add( new Vector3( minRadius*cos, minRadius*sin, z ) );
			maxBoundSamples.add( new Vector3( maxRadius*cos, maxRadius*sin, z ) );
		}
		KinemageBuilder.appendChain( kin, minBoundSamples, true, "Min bound", KinemageColor.Green, 1 );
		KinemageBuilder.appendChain( kin, maxBoundSamples, true, "Max bound", KinemageColor.Green, 1 );
		
		KinemageBuilder.appendDefaultView( kin, 1 );
		return kin;
	}

	private static void filterPoints( List<Vector3> intersectionPoints, Curve a, Curve b )
	{
		// NOTE: we only need to filter points computed using the projections and the algebraic 2d kernel
		// and also for intersections of arcs
		
		// throw out intersection points that don't lie on the curves
		Iterator<Vector3> iter = intersectionPoints.iterator();
		while( iter.hasNext() )
		{
			Vector3 p = iter.next();
			if( !a.containsPoint( p ) || !b.containsPoint( p ) )
			{
				iter.remove();
			}
		}
	}

	private static native void intersectRdcCircularPoF(
		List<Vector3> out,
		double A, double B, double C,
		double r,
		double a, double b, double c, double d, double e, double f, double g, double h, double i,
		double alpha, double beta
	);
	
	private static native void intersectRdcGeodesicPoF(
		List<Vector3> out,
		double A, double B, double C,
		double r,
		double a, double b, double c, double d, double e, double f, double g, double h, double i
	);
	
	private static native void intersectRdcRdc(
		List<Vector3> out,
		double aA, double aB, double aC, double ad,
		double bA, double bB, double bC, double bd,
		double a, double b, double c, double d, double e, double f, double g, double h, double i
	);
	
	private static native void intersectCircularCircular(
		List<Vector3> out, Vector3 n1, double h1, Vector3 n2, double h2
	);
	
	private static native void intersectEllipticalCircular(
		List<Vector3> intersectionPoints,
		double q, double r, double s,
		double a2, double b2,
		double u, double v, double w,
		double alpha
	);
	
	private static native void intersectEllipticalElliptical(
		List<Vector3> intersectionPoints,
		double q, double r, double s,
		double surfaceTanM, double surfaceTanm, double originTanM, double originTanm,
		double d, double e, double f, double g, double h, double i, double j, double k, double l
	);
	
	private static native void intersectRdcEllipticalConePof(
		List<Vector3> intersectionPoints,
		double dx, double dy, double dz, double dr,
		double f, double g, double h, double i, double j, double k, double l, double m, double n,
		double a, double b, double c, double d, double e
	);
	
	private static native void sampleRotatedRdcCurve(
		List<Vector2> out,
		double A, double B, double C,
		double a, double b, double c, double d, double e, double f, double g, double h, double i,
		double r
	);
	
	private static native void getRdcCurveDihedralIntersectionPoints(
		List<Vector3> out,
		double A, double B, double C,
		double a, double b, double c, double d, double e, double f, double g, double h, double i,
		double r, double theta
	);
	
	private static native void getHyperbolaUnitCircleIntersectionPoints(
		List<Vector3> out,
		double a, double b, double c, double d
	);
	
	private static native void getParabolaUnitCircleIntersectionPoints(
		List<Vector3> out,
		double a, double b, double c
	);
	
	private static native void getEllipseOptima(
		List<Vector3> optima,
		double a2, double b2,
		double c, double d, double e,
		double f, double g, double h, double i, double j, double k, double l, double m, double n,
		double z
	);
}
