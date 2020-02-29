package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;

import java.util.ArrayList;
import java.util.List;


/**
 * Try to visualize stuff in SO(3)
 */
public class SO3Vis
{
	public static void main( String[] args )
	{
		final int NumSamples = 30;
		
		// allocate our memory here
		Quaternion q = new Quaternion();
		Matrix3 basis = new Matrix3();
		List<Vector3> points = new ArrayList<Vector3>();
		
		// sample SO(3) systematically (but not quite uniformly)
		for( int i=0; i<NumSamples; i++ )
		{
			double theta = (double)i / (double)NumSamples * 2.0 * Math.PI;
			for( int j=0; j<NumSamples; j++ )
			{
				double phi = (double)j / (double)NumSamples * Math.PI;
				for( int k=0; k<NumSamples; k++ )
				{
					double psi = (double)k / (double)NumSamples * 2.0 * Math.PI;
					
					// compute the basis
					Vector3 x = Vector3.getUnitX();
					Vector3 y = Vector3.getUnitY();
					Vector3 z = Vector3.getUnitZ();
					Quaternion.getRotation( q, Vector3.getUnitY(), phi );
					q.rotate( x );
					q.rotate( z );
					Quaternion.getRotation( q, Vector3.getUnitZ(), theta );
					q.rotate( x );
					q.rotate( z );
					Quaternion.getRotation( q, z, psi );
					q.rotate( x );
					z.getCross( y, x );
					x.normalize();
					y.normalize();
					z.normalize();
					basis.setColumns( x, y, z );
					
					// compute the quaternion
					Quaternion.getRotation( q, basis );
					
					// compute the 2-ball point
					points.add( new Vector3( q.a, q.c, q.d ) );
				}
			}
		}
		
		// render a kinemage
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 2, 0.1 );
		KinemageBuilder.appendUnitSphere( kin );
		KinemageBuilder.appendPoints( kin, points );
		new KinemageWriter().showAndWait( kin );
	}
}
