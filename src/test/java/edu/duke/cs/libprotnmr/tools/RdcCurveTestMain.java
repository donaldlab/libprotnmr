package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.geom.GeodesicGrid;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.nmr.RdcMapper;
import edu.duke.cs.libprotnmr.nmr.RdcReader;
import edu.duke.cs.libprotnmr.optimization.SimpleCircleOptimizer;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.perf.Progress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.cgal.curves.*;
import edu.duke.cs.libprotnmr.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class RdcCurveTestMain
{
	private static final Logger m_log = LogManager.getLogger(RdcCurveTestMain.class);
	
	public static void main( String[] args )
	throws Exception
	{
		Logging.Normal.init();
		
		// read in the structure
		Protein protein = new ProteinReader().read(Resources.get("2KIQ.idealized.pdb"));
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		// read in all the RDCs
		List<Rdc<AtomAddressReadable>> unmappedRdcsNh = new RdcReader().read(Resources.get("2KIQ.nh.rdcs.mr"));
		NameMapper.ensureAddresses( protein.getSequences(), unmappedRdcsNh, NameScheme.New );
		List<Rdc<AtomAddressInternal>> rdcsNh = RdcMapper.mapReadableToInternal( protein, unmappedRdcsNh );
		
		// compute an alignment tensor from all the data and structural information
		AlignmentTensor tensor = AlignmentTensor.compute( protein, rdcsNh );
		m_log.info( "NH RDC RMSD:   " + tensor.getRmsd( protein, rdcsNh ) );
		m_log.info( tensor.getStats() );
		
		// backcompute RDCs, use those for testing
		List<Rdc<AtomAddressInternal>> simulatedInternalRdcsNh = Rdc.copyDeep( rdcsNh );
		Rdc.setValues( simulatedInternalRdcsNh, tensor.backComputeRdcs( protein, rdcsNh ) );
		List<Rdc<AtomAddressReadable>> simulatedRdcsNh = RdcMapper.mapInternalToReadable( protein, simulatedInternalRdcsNh );
		
		final double OffsetDistance = Math.toRadians( 5.0 );
		
		// pick an rdc (and tweak it)
		Rdc<AtomAddressReadable> rdc = simulatedRdcsNh.get( 1 );
		rdc.setValue( 1 );
		//rdc.setError( 0.1 );
		
		// build an offset curve
		RdcOffsetCurve offsetCurve = new RdcOffsetCurve( new RdcCurve( tensor, rdc.getValue(), 0 ), OffsetDistance );
		
		if( false )
		{
			// show the angle function
			Vector3 point = new Vector3( 1.0, 1.0, 1.0 );
			point.normalize();
			OffsetAngleFunction f = new OffsetAngleFunction( offsetCurve.getRdcCurve(), point );
			new KinemageWriter().show( SimpleCircleOptimizer.getKinemage( f, null, null ) );
			
			if( false )
			{
				Kinemage kin = new Kinemage();
				KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
				//KinemageBuilder.appendAxes( kin, 1, 0.2 );
				KinemageBuilder.appendAxes( kin, tensor, "PoF", 1, 0.2 );
				KinemageBuilder.appendCurve( kin, offsetCurve, "Offset Curve", KinemageColor.Cobalt, 1 );
				appendDerivative( kin, offsetCurve, KinemageColor.Orange );
				new KinemageWriter().show( kin );
			}
		}
		
		if( true )
		{
			// show the intersection function
			Vector3 point = new Vector3( 1.0, 1.0, 1.0 );
			point.normalize();
			CircularCurve circle = new CircularCurve( point, Math.toRadians( 10.0 ) );
			OffsetIntersectionFunction f = new OffsetIntersectionFunction( offsetCurve, circle );
			new KinemageWriter().show( SimpleCircleOptimizer.getKinemage( f, null, null ) );
		}
		
		if( false ) return;
		
		// compute the RDC offset faces
		RdcBand rdcBand = new RdcBand( tensor, rdc );
		List<Face> certainFaces = IntersectionFaceBuilder.getIntersectionFaces( rdcBand );
		List<Face> faces = IntersectionFaceBuilder.getIntersectionFaces( rdcBand, OffsetDistance );
		
		// DEBUG
		if( true )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendAxes( kin, tensor, "PoF", 1, 1.0 );
			for( Face face : certainFaces )
			{
				KinemageBuilder.appendBoundary( kin, face.boundary(), "Face", KinemageColor.LightGrey, 1 );
			}
			for( Face face : faces )
			{
				KinemageBuilder.appendBoundary( kin, face.boundary(), "Offset face", KinemageColor.Cobalt, 1 );
			}
			appendDerivative( kin, rdcBand.getCurve( BandPart.Min, 0 ), KinemageColor.Orange );
			appendDerivative( kin, rdcBand.getCurve( BandPart.Max, 0 ), KinemageColor.Orange );
			appendDerivative( kin, rdcBand.getCurve( BandPart.Min, 1 ), KinemageColor.Orange );
			appendDerivative( kin, rdcBand.getCurve( BandPart.Max, 1 ), KinemageColor.Orange );
			new KinemageWriter().show( kin );
			
			return;
		}
		
		// build a list of points to test
		List<Vector3> testPoints = new ArrayList<Vector3>();
		GeodesicGrid grid = new GeodesicGrid( 2 );
		for( GeodesicGrid.Face gridFace : grid )
		{
			Vector3 midpoint = gridFace.getMidpoint();
			midpoint.normalize();
			testPoints.add( midpoint );
		}
		
		// TEMP: just test this point
		if( false )
		{
			testPoints.clear();
			Vector3 v = new Vector3( 0.142, 0.817, -0.559 );
			v.normalize();
			testPoints.add( v );
		}
		
		// do some point inclusion tests
		for( Face face : faces )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendRdcBand( kin, rdcBand, "RDC Band", KinemageColor.Grey, 1 );
			KinemageBuilder.appendBoundary( kin, face.boundary(), "Offset face", KinemageColor.Cobalt, 1 );
			List<Vector3> inPoints = new ArrayList<Vector3>();
			List<Vector3> outPoints = new ArrayList<Vector3>();
			Progress progress = new Progress( testPoints.size(), 5000 );
			for( Vector3 p : testPoints )
			{
				/* TEMP: skip points that are too far away
				double d = tensor.backComputeRdc( p );
				double dd = rdc.getError() + 10.0;
				if( p.getDot( tensor.getYAxis() ) < 0 || d < rdc.getValue() - dd || d > rdc.getValue() + dd )
				{
					progress.incrementProgress();
					continue;
				}
				*/
				
				if( face.containsPoint( p ) )
				{
					inPoints.add( p );
				}
				else
				{
					outPoints.add( p );
				}
				progress.incrementProgress();
			}
			KinemageBuilder.appendPoints( kin, inPoints, "In Points", KinemageColor.Green, 5 );
			KinemageBuilder.appendPoints( kin, outPoints, "Out Points", KinemageColor.Red, 5 );
			new KinemageWriter().show( kin );
		}

		// visualize some more stuff
		RdcCurve rdcCurve = new RdcCurve( tensor, rdc.getValue(), 1 );
		RdcOffsetCurve rdcOffsetCurve = new RdcOffsetCurve( rdcCurve, OffsetDistance );
		
		final double CircleSize = Math.toRadians( 94.0 );
		Vector3 circleNormal = new Vector3( 0.0822, 0.0822, -1.0 );
		circleNormal.normalize();
		CircularCurve circularCurve = new CircularCurve( circleNormal, CircleSize );
		
		// compute intersection points
		List<Vector3> intersectionPoints = OffsetIntersectionOptimizer.getIntersectionPoints( rdcOffsetCurve, circularCurve );
		
		// find some thetas
		Vector3 q = new Vector3( -2.0, 1.4, -3.0 );
		q.normalize();
		double angle = rdcOffsetCurve.getAngle( q );
		
		// show the curves
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendCurve( kin, rdcCurve, "RDC Curve", KinemageColor.Cobalt, 2 );
		appendNormalGeodesics( kin, rdcCurve, OffsetDistance, KinemageColor.Cobalt );
		KinemageBuilder.appendCurve( kin, rdcOffsetCurve, "Offset Curve", KinemageColor.Cobalt, 1 );
		appendNormalGeodesics( kin, rdcOffsetCurve, OffsetDistance, KinemageColor.Cobalt );
		KinemageBuilder.appendCurve( kin, circularCurve, "Circular Curve", KinemageColor.Orange, 2 );
		
		KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersection Points", KinemageColor.Lime, 7 );
		
		// show the angle
		KinemageBuilder.appendPoint( kin, q, "Angle Point", KinemageColor.Magenta, 7 );
		KinemageBuilder.appendPoint( kin, rdcOffsetCurve.getPoint( angle ), "Offset Curve Point", KinemageColor.Magenta, 7 );
		KinemageBuilder.appendCurve( kin, new GeodesicCurve( rdcOffsetCurve.getDerivative( angle ) ), "Angle Geodesic", KinemageColor.Lime, 1 );
		
		new KinemageWriter().show( kin );
	}
	
	private static void appendDerivative( Kinemage kin, ParametricCurve curve, KinemageColor color )
	{
		final int NumPoints = 24;
		for( int i=0; i<NumPoints; i++ )
		{
			// get a point on the curve and its derivative
			double angle = (double)i / (double)NumPoints * Math.PI * 2.0;
			Vector3 p = curve.getPoint( angle );
			Vector3 derivative = curve.getDerivative( angle );
			if( derivative == null )
			{
				m_log.warn( "Invalid derivative!" );
				continue;
			}
			//derivative.normalize();
			
			KinemageBuilder.appendVector( kin, derivative, p, "Derivative", color, 2, 0.2 );
		}
	}
	
	private static void appendNormalGeodesics( Kinemage kin, ParametricCurve curve, double geodesicDistance, KinemageColor color )
	{
		final int NumPoints = 64;
		Quaternion q = new Quaternion();
		for( int i=0; i<NumPoints; i++ )
		{
			// get a point on the curve and its derivative
			double angle = (double)i / (double)NumPoints * Math.PI * 2.0;
			Vector3 curvePoint = curve.getPoint( angle );
			Vector3 derivative = curve.getDerivative( angle );
			if( derivative == null )
			{
				m_log.warn( "Invalid derivative!" );
				continue;
			}
			derivative.normalize();
			
			// now, offset the point
			Quaternion.getRotation( q, derivative, geodesicDistance );
			Vector3 offsetPoint = new Vector3( curvePoint );
			q.rotate( offsetPoint );
			
			// build the geodesic arc
			GeodesicCurveArc arc = new GeodesicCurveArc( new GeodesicCurve( derivative ), curvePoint, offsetPoint );
			KinemageBuilder.appendCurve( kin, arc, "Normal", color, 1 );
		}
	}
}
