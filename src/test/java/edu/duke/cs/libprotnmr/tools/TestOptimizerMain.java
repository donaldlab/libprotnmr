package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.optimization.*;
import edu.duke.cs.libprotnmr.cgal.curves.OffsetAngleFunction;
import edu.duke.cs.libprotnmr.cgal.curves.OffsetIntersectionFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class TestOptimizerMain
{
	private static final Logger m_log = LogManager.getLogger(TestOptimizerMain.class);
	
	
	public static void main( String[] args )
	throws Exception
	{
		Logging.Debug.init();
		
		// read the function
		ObjectInputStream in = new ObjectInputStream( new FileInputStream( new File( args[0] ) ) );
		DifferentiableFunction f = (DifferentiableFunction)in.readObject();
		m_log.info( "Read " + f.getClass().getSimpleName() + "." );
		
		// run the optimizer in "debug" mode
		Kinemage kin = SimpleCircleOptimizer.getKinemage( f, null, null );
		List<Double> optima = null;
		List<Double> roots = null;
		try
		{
			optima = SimpleCircleOptimizer.getOptima( f, kin );
			OptimizerKinemageBuilder.appendOptima( kin, f, optima );
			roots = SimpleCircleOptimizer.getRoots( f, optima );
			OptimizerKinemageBuilder.appendRoots( kin, f, roots );
		}
		catch( TooManyOptimaException ex )
		{
			OptimizerKinemageBuilder.appendOptima( kin, f, ex.getOptima() );
			m_log.error( "Optimizer Failure", ex );
		}
		catch( OptimizerFailureException ex )
		{
			m_log.error( "Optimizer Failure", ex );
		}
		new KinemageWriter().show( kin );
		
		// show the geometry next
		if( true )
		{
			kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			if( f instanceof OffsetAngleFunction )
			{
				OffsetAngleFunction oaf = (OffsetAngleFunction)f;
				KinemageBuilder.appendAxes( kin, oaf.getRdcCurve().getTensor(), "PoF", 1, 1.0 );
				KinemageBuilder.appendCurve( kin, oaf.getRdcCurve(), "RDC Curve", KinemageColor.Cobalt, 2 );
				KinemageBuilder.appendPoint( kin, oaf.getPoint(), "Query Point", KinemageColor.Lime, 7 );
				
				// show the derivative curve
				List<Vector3> points = new ArrayList<Vector3>();
				for( double theta : CircleRange.newCircle().samplePoints( Math.toRadians( 2.0 ) ) )
				{
					Vector3 derivative = oaf.getRdcCurve().getDerivative( theta );
					points.add( derivative );
				}
				KinemageBuilder.appendChain( kin, points, true, "Derivative Curve", KinemageColor.Cobalt, 1 );
				
				// show the second derivative as tangent vectors
				final int NumPoints = 24;
				for( int i=0; i<NumPoints; i++ )
				{
					double angle = (double)i / (double)NumPoints * Math.PI * 2.0;
					Vector3 derivative = oaf.getRdcCurve().getDerivative( angle );
					Vector3 secondDerivative = oaf.getRdcCurve().getSecondDerivative( angle );
					KinemageBuilder.appendVector( kin, secondDerivative, derivative, "Point", KinemageColor.Orange, 2, 0.2 );
				}
			}
			else if( f instanceof OffsetIntersectionFunction )
			{
				OffsetIntersectionFunction oif = (OffsetIntersectionFunction)f;
				KinemageBuilder.appendCurve( kin, oif.getRdcOffsetCurve(), "RDC Offset Curve", KinemageColor.Cobalt, 2 );
				KinemageBuilder.appendCurve( kin, oif.getCircularCurve(), "CircularCurve", KinemageColor.Orange, 2 );
				
				// get the intersections if possible
				if( roots != null && !roots.isEmpty() )
				{
					// build the intersection points
					List<Vector3> intersectionPoints = new ArrayList<Vector3>();
					for( Double root : roots )
					{
						intersectionPoints.add( oif.getRdcOffsetCurve().getPoint( root ) );
					}
					KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersections (" + intersectionPoints.size() + ")", KinemageColor.Lime, 7 );
				}
			}
			
			new KinemageWriter().show( kin );
		}
	}
}
