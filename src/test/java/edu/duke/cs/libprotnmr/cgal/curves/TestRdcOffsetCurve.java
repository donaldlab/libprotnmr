package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.GeodesicGrid;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.nmr.*;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.perf.Progress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestRdcOffsetCurve
{
	private static AlignedRdcs<AtomAddressReadable> m_alignedRdcs;
	private static List<Vector3> m_gridPoints;
	
	@BeforeClass
	public static void setup( )
	throws Exception
	{
		Logging.Debug.init();
		
		// read in the structure
		Protein protein = new ProteinReader().read(Resources.get("2KIQ.idealized.pdb"));
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		// read in the NH RDCs
		List<Rdc<AtomAddressReadable>> unmappedRdcsNh = new RdcReader().read(Resources.get("2KIQ.nh.rdcs.mr"));
		NameMapper.ensureAddresses( protein.getSequences(), unmappedRdcsNh, NameScheme.New );
		List<Rdc<AtomAddressInternal>> rdcsNh = RdcMapper.mapReadableToInternal( protein, unmappedRdcsNh );
		
		// compute an alignment tensor and make a medium
		AlignmentTensor tensor = AlignmentTensor.compute( protein, rdcsNh );
		AlignmentMedium medium = new AlignmentMedium( "Alignment Medium 1", tensor );
		
		// backcompute RDCs, use those for testing
		List<Rdc<AtomAddressInternal>> simulatedInternalRdcsNh = Rdc.copyDeep( rdcsNh );
		Rdc.setValues( simulatedInternalRdcsNh, tensor.backComputeRdcs( protein, rdcsNh ) );
		List<Rdc<AtomAddressReadable>> simulatedRdcsNh = RdcMapper.mapInternalToReadable( protein, simulatedInternalRdcsNh );
		
		m_alignedRdcs = new AlignedRdcs<AtomAddressReadable>( medium, simulatedRdcsNh );
		
		GeodesicGrid grid = new GeodesicGrid( 3 );
		m_gridPoints = new ArrayList<Vector3>();
		for( GeodesicGrid.Face face : grid )
		{
			Vector3 point = face.getMidpoint();
			point.normalize();
			m_gridPoints.add( point );
		}
	}
	
	@Test
	public void testGetAngle( )
	throws Exception
	{
		// make sure each point on the grid can be tied to an angle
		Progress progress = new Progress( m_alignedRdcs.getRdcs().size() * m_gridPoints.size() * 2, 5000 );
		for( int i=0; i<m_alignedRdcs.getRdcs().size(); i++ )
		{
			Rdc<AtomAddressReadable> rdc = m_alignedRdcs.getRdcs().get( i );
			
			// for each arcnum...
			for( int a=0; a<2; a++ )
			{
				// get the curve
				RdcOffsetCurve curve = new RdcOffsetCurve( new RdcCurve( m_alignedRdcs.getMedium().getTensor(), rdc.getValue(), a ), Math.toRadians( 5.0 ) );
				
				for( Vector3 point : m_gridPoints )
				{
					double angle = curve.getAngle( point );
					
					// is this a valid angle?
					String tag = "RDC " + i + " from point " + point.toString();
					assertFalse( "Angle for " + tag + " is NaN", Double.isNaN( angle ) );
					assertFalse( "Angle for " + tag + " is Infinity", Double.isInfinite( angle ) );
					
					// is this the right angle?
					assertTrue( new GeodesicCurve( curve.getDerivative( angle ) ).containsPoint( point ) );
					progress.incrementProgress();
				}
			}
		}
	}
	
	//@Test
	public void testGetIntersections( )
	throws Exception
	{
		// make sure a small circle whose normal point is on the curve intersects the curve at least twice
		final int NumSamples = 512;
		Progress progress = new Progress( m_alignedRdcs.getRdcs().size() * NumSamples * 2, 5000 );
		for( int i=0; i<m_alignedRdcs.getRdcs().size(); i++ )
		{
			Rdc<AtomAddressReadable> rdc = m_alignedRdcs.getRdcs().get( i );
			
			// for each arcnum...
			for( int a=0; a<2; a++ )
			{
				// get the curve
				RdcOffsetCurve curve = new RdcOffsetCurve( new RdcCurve( m_alignedRdcs.getMedium().getTensor(), rdc.getValue(), a ), Math.toRadians( 10.0 ) );
				
				for( int n=0; n<NumSamples; n++ )
				{
					double theta = (double)n / (double)(NumSamples) * Math.PI * 2.0;
					
					// get the circle
					CircularCurve circle = new CircularCurve( curve.getPoint( theta ), Math.toRadians( 1.0 ) );
					List<Vector3> intersectionPoints = Intersector.getIntersectionPoints( curve, circle );
					
					// DEBUG
					if( true && intersectionPoints.size() < 2 )
					{
						Kinemage kin = new Kinemage();
						KinemageBuilder.appendAxes( kin, 1, 0.2 );
						KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
						KinemageBuilder.appendCurve( kin, curve, "Offset Curve", KinemageColor.Cobalt, 1 );
						KinemageBuilder.appendCurve( kin, circle, "Circle", KinemageColor.Orange, 1 );
						KinemageBuilder.appendPoints( kin, intersectionPoints, "Intersections (" + intersectionPoints.size() + ")", KinemageColor.Lime, 7 );
						new KinemageWriter().showAndWait( kin );
						
						// save the function so we can inspect it
						new OffsetIntersectionFunction( curve, circle ).save( new File( "output/function.dat" ) );
					}
					
					assertTrue( intersectionPoints.size() >= 2 );
					progress.incrementProgress();
				}
			}
		}
	}
}
