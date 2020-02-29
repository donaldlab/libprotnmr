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

package edu.duke.cs.libprotnmr.cgal.spherical;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.cgal.AbstractCleanable;
import edu.duke.cs.libprotnmr.cgal.Cleaner;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Quaternion;


public class CircularArc3 extends AbstractCleanable
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final double DefaultStepRadians = Math.toRadians( 5.0 );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static Cleaner m_cleaner;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_cleaner = new Cleaner( )
		{
			@Override
			public void cleanup( int id )
			{
				CircularArc3.cleanup( id );
			}
		};
	}
	
	public CircularArc3( Circle3 supportingCircle, Vector3 source, Vector3 target )
	{
		super( m_cleaner );
		SphericalCgal.getInstance().addReference( this, m_cleaner );
		init( supportingCircle, source, target );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static native void cleanup( int id );
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public native Circle3 getSupportingCircle( );
	public native Vector3 getSource( );
	public native Vector3 getTarget( );
	
	public List<Vector3> samplePoints( )
	{
		return samplePoints( DefaultStepRadians );
	}
	
	public List<Vector3> samplePoints( double stepRadians )
	{
		Circle3 circle = getSupportingCircle();
		Vector3 center = circle.getCenter();
		Vector3 normal = circle.getNormal();
		Vector3 source = getSource();
		Vector3 target = getTarget();
		
		// how many radians are in the arc?
		Vector3 sourceCopy = new Vector3( source );
		sourceCopy.subtract( center );
		sourceCopy.normalize();
		Vector3 targetCopy = new Vector3( target );
		targetCopy.subtract( center );
		targetCopy.normalize();
		double dot = sourceCopy.getDot( targetCopy );
		
		// HACKHACK: clamp the dot product for numerical stability
		if( dot > 1.0 )
		{
			dot = 1.0;
		}
		else if( dot < -1.0 )
		{
			dot = -1.0;
		}
		double sweptAngle = Math.acos( dot );
		
		// collect the points into a list
		List<Vector3> points = new ArrayList<Vector3>();
		Quaternion q = new Quaternion();
		int numPoints = Math.max( 2, (int)( sweptAngle / stepRadians ) );
		for( int i=0; i<numPoints; i++ )
		{
			// compute the angle
			double angle = (double)i / (double)( numPoints - 1 ) * sweptAngle;
			Quaternion.getRotation( q, normal, angle );
			
			// sample the point
			Vector3 p = new Vector3( source );
			p.subtract( center );
			p.rotate( q );
			p.add( center );
			points.add( p );
		}
		
		return points;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private native void init( Circle3 supportingCircle, Vector3 source, Vector3 target );
}
