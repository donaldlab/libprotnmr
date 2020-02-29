/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.math.CompareReal;


public class TestWelzlSphereSolver extends ExtendedTestCase
{
	public void testTwoDeterministic( )
	{
		assertEquals( new Sphere( new Vector3( 0, 0, 0 ), 1.0 ), WelzlSphereSolver.getSphere( Arrays.asList( new Vector3( 1, 0, 0 ), new Vector3( -1, 0, 0 ) ) ) );
		assertEquals( new Sphere( new Vector3( 0, 0, 0 ), 1.0 ), WelzlSphereSolver.getSphere( Arrays.asList( new Vector3( 0, 1, 0 ), new Vector3( 0, -1, 0 ) ) ) );
		assertEquals( new Sphere( new Vector3( 1, 0, 0 ), 5.0 ), WelzlSphereSolver.getSphere( Arrays.asList( new Vector3( 1, -5, 0 ), new Vector3( 1, 5, 0 ) ) ) );
	}
	
	public void testTwoRandom( )
	{
		Sphere sphere = new Sphere( new Vector3( 4, 5, -6 ), 7.0 );
		for( int i=0; i<10; i++ )
		{
			List<Vector3> points = new ArrayList<Vector3>( 2 );
			sampleAntipodalPoints( points, sphere );
			assertEquals( sphere, WelzlSphereSolver.getSphere( points ) );
		}
	}
	
	public void testThreeRandom( )
	{
		Sphere sphere = new Sphere( new Vector3( 4, 5, -6 ), 7.0 );
		for( int i=0; i<10; i++ )
		{
			List<Vector3> points = new ArrayList<Vector3>( 3 );
			sampleAntipodalPoints( points, sphere );
			sampleSphere( points, sphere, 1 );
			assertEquals( sphere, WelzlSphereSolver.getSphere( points ) );
		}
	}
	
	public void testFourRandom( )
	{
		Sphere sphere = new Sphere( new Vector3( 4, 5, -6 ), 7.0 );
		for( int i=0; i<10; i++ )
		{
			List<Vector3> points = new ArrayList<Vector3>( 4 );
			sampleAntipodalPoints( points, sphere );
			sampleSphere( points, sphere, 2 );
			assertEquals( sphere, WelzlSphereSolver.getSphere( points ) );
		}
	}
	
	public void testHigherRandom( )
	{
		Sphere sphere = new Sphere( new Vector3( 4, 5, -6 ), 7.0 );
		for( int j=0; j<4; j++)
		{
			for( int i=0; i<10; i++ )
			{
				List<Vector3> points = new ArrayList<Vector3>( 5 + j );
				sampleAntipodalPoints( points, sphere );
				sampleSphere( points, sphere, 3 + j );
				assertEquals( sphere, WelzlSphereSolver.getSphere( points ) );
			}
		}
	}
	
	public void testSimple( )
	{
		List<Vector3> points = Arrays.asList(
			new Vector3( 1, 0, 0 ),
			new Vector3( -1, 0, 0 ),
			new Vector3( 0, 1, 0 ),
			new Vector3( 0, -1, 0 ),
			new Vector3( 0, 0, 1 ),
			new Vector3( 0, 0, -1 )
		);
		
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		
		assertEquals( new Sphere( new Vector3( 0, 0, 0 ), 1.0 ), minSphere );
	}

	public void testSimpleWithExtras( )
	{
		List<Vector3> points = Arrays.asList(
			new Vector3( 0, 0, 0 ),
			new Vector3( 0.5, 0, 0 ),
			new Vector3( 0.1, 0.1, 0.1 ),
			new Vector3( -0.2, -0.1, 0.0 ),
			new Vector3( 1, 0, 0 ),
			new Vector3( -1, 0, 0 ),
			new Vector3( 0, 1, 0 ),
			new Vector3( 0, -1, 0 ),
			new Vector3( 0, 0, 1 ),
			new Vector3( 0, 0, -1 )
		);
		
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		
		assertEquals( new Sphere( new Vector3( 0, 0, 0 ), 1.0 ), minSphere );
	}
	
	public void testSlightlyHarder( )
	{
		List<Vector3> points = Arrays.asList(
			new Vector3( 3, 2, 3 ),
			new Vector3( -1, 2, 3 ),
			new Vector3( 1, 4, 3 ),
			new Vector3( 1, 0, 3 ),
			new Vector3( 1, 2, 5 ),
			new Vector3( 1, 2, 1 )
		);
		
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		
		assertEquals( new Sphere( new Vector3( 1, 2, 3 ), 2.0 ), minSphere );
	}
	
	public void testSlightlyHarderWithExtras( )
	{
		List<Vector3> points = Arrays.asList(
			new Vector3( 2, 3, 4 ),
			new Vector3( 1, 2, 3 ),
			new Vector3( 0, 1, 4 ),
			new Vector3( 3, 2, 3 ),
			new Vector3( -1, 2, 3 ),
			new Vector3( 1, 4, 3 ),
			new Vector3( 1, 0, 3 ),
			new Vector3( 1, 2, 5 ),
			new Vector3( 1, 2, 1 )
		);
		
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		
		assertEquals( new Sphere( new Vector3( 1, 2, 3 ), 2.0 ), minSphere );
	}
	
	public void testWeirdCase( )
	{
		List<Vector3> points = Arrays.asList(
			new Vector3( 18.634000778198242, 25.437000274658203, 10.6850004196167 ),
			new Vector3( 17.983999252319336, 25.295000076293945, 9.354000091552734 ),
			new Vector3( 18.15999984741211, 23.875999450683594, 8.817999839782715 ),
			new Vector3( 19.259000778198242, 23.44099998474121, 8.536999702453613 ),
			new Vector3( 18.608999252319336, 26.281999588012695, 8.371000289916992 ),
			new Vector3( 18.003000259399414, 26.055999755859375, 6.986000061035156 ),
			new Vector3( 16.47599983215332, 26.05699920654297, 7.091000080108643 ),
			new Vector3( 16.013999938964844, 27.340999603271484, 7.783999919891357 )
		);
		
		Sphere minSphere = WelzlSphereSolver.getSphere( points );
		
		assertEquals( new Sphere( new Vector3( 17.660288, 25.439151, 8.307377 ), 2.569283169 ), minSphere );
	}
	
	private void sampleAntipodalPoints( List<Vector3> out, Sphere sphere )
	{
		// sample antipodal points from the sphere
		Vector3 p = new Vector3(
			getRandomDouble( -5.0, 5.0 ),
			getRandomDouble( -5.0, 5.0 ),
			getRandomDouble( -5.0, 5.0 )
		);
		p.normalize();
		p.scale( sphere.radius );
		Vector3 q = new Vector3( p );
		q.negate();
		p.add( sphere.center );
		q.add( sphere.center );
		assert( CompareReal.eq( p.getDistance( sphere.center ), sphere.radius ) );
		assert( CompareReal.eq( q.getDistance( sphere.center ), sphere.radius ) );
		out.add( p );
		out.add( q );
	}
	
	private void sampleSphere( List<Vector3> out, Sphere sphere, int numSamples )
	{
		for( int i=0; i<numSamples; i++ )
		{
			Vector3 p = new Vector3(
				getRandomDouble( -5.0, 5.0 ),
				getRandomDouble( -5.0, 5.0 ),
				getRandomDouble( -5.0, 5.0 )
			);
			p.normalize();
			p.scale( sphere.radius );
			p.add( sphere.center );
			assert( CompareReal.eq( p.getDistance( sphere.center ), sphere.radius ) );
			out.add( p );
		}
	}
}
