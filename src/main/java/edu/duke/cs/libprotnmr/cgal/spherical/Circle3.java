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


public class Circle3 extends AbstractCleanable
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
				Circle3.cleanup( id );
			}
		};
	}
	
	public Circle3( Vector3 a, Vector3 b, Vector3 c )
	{
		super( m_cleaner );
		SphericalCgal.getInstance().addReference( this, m_cleaner );
		init( a, b, c );
	}
	
	private Circle3( int id, long pointer )
	{
		super( id, pointer, m_cleaner );
		SphericalCgal.getInstance().addReference( this, m_cleaner );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static native void cleanup( int id );
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public native Vector3 getCenter( );
	public native double getSquaredRadius( );
	public native Vector3 getNormal( );
	
	public List<Vector3> samplePoints( )
	{
		return samplePoints( DefaultStepRadians );
	}
	
	public List<Vector3> samplePoints( double stepRadians )
	{
		Vector3 center = getCenter();
		Vector3 normal = getNormal();
		
		// how many points do we need?
		int numPoints = (int)( Math.PI * 2.0 / stepRadians );
		
		// find any point on the circle
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, Vector3.getUnitZ(), normal );
		Vector3 source = new Vector3( Math.sqrt( getSquaredRadius() ), 0.0, 0.0 );
		source.rotate( q );
		
		// collect the points into a list
		List<Vector3> points = new ArrayList<Vector3>();
		for( int i=0; i<numPoints; i++ )
		{
			// compute the angle
			double angle = (double)i / (double)numPoints * Math.PI * 2.0;
			Quaternion.getRotation( q, normal, angle );
			
			// sample the point
			Vector3 p = new Vector3( source );
			p.rotate( q );
			p.add( center );
			points.add( p );
		}
		
		return points;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private native void init( Vector3 a, Vector3 b, Vector3 c );
}
