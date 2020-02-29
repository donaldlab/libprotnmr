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
package edu.duke.cs.libprotnmr.math;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;

public class TestQuaternion extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		Quaternion q = new Quaternion();
		
		assertEquals( 1.0, q.a );
		assertEquals( 0.0, q.b );
		assertEquals( 0.0, q.c );
		assertEquals( 0.0, q.d );
	}
	
	public void testAssignmentConstructor( )
	{
		Quaternion q = new Quaternion( 1.0, 2.0, 3.0, 4.0 );
		
		assertEquals( 1.0, q.a );
		assertEquals( 2.0, q.b );
		assertEquals( 3.0, q.c );
		assertEquals( 4.0, q.d );
	}
	
	public void testVectorConstructor( )
	{
		Vector3 v = new Vector3( 1.0, 2.0, 3.0 );
		Quaternion q = new Quaternion( v );
		
		assertEquals( 0.0, q.a );
		assertEquals( 1.0, q.b );
		assertEquals( 2.0, q.c );
		assertEquals( 3.0, q.d );
	}
	
	public void testCopyConstructor( )
	{
		Quaternion q = new Quaternion( 1.0, 2.0, 3.0, 4.0 );
		Quaternion p = new Quaternion( q );
		
		assertNotSame( p, q );
		assertEquals( p, q );
	}
	
	public void testGet( )
	{
		Quaternion q = new Quaternion( 1.0, 2.0, 3.0, 4.0 );
		
		assertEquals( 1.0, q.get( 0 ) );
		assertEquals( 2.0, q.get( 1 ) );
		assertEquals( 3.0, q.get( 2 ) );
		assertEquals( 4.0, q.get( 3 ) );
	}
	
	public void testSetIndividual( )
	{
		Quaternion q = new Quaternion();
		
		q.set( 0, 1.0 );
		q.set( 1, 2.0 );
		q.set( 2, 3.0 );
		q.set( 3, 4.0 );
		
		assertEquals( new Quaternion( 1.0, 2.0, 3.0, 4.0 ), q );
	}
	
	public void testSetAll( )
	{
		Quaternion q = new Quaternion();
		
		q.set( 1.0, 2.0, 3.0, 4.0 );
		
		assertEquals( new Quaternion( 1.0, 2.0, 3.0, 4.0 ), q );
	}
	
	public void testSetCopy( )
	{
		Quaternion q = new Quaternion( 1.0, 2.0, 3.0, 4.0 );
		Quaternion p = new Quaternion();
		
		p.set( q );
		
		assertNotSame( p, q );
		assertEquals( q, p );
	}
	
	public void testGetSquaredLength( )
	{
		Quaternion q = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		assertEquals( 174.0, q.getSquaredLength() );
	}
	
	public void testGetLength( )
	{
		Quaternion q = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		assertEquals( 13.190905958, q.getLength(), CompareReal.getEpsilon() );
	}
	
	public void testNormalize( )
	{
		Quaternion q = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		
		q.normalize();
		
		assertEquals( new Quaternion( 0.379049022, 0.454858826, 0.530668631, 0.606478435 ), q );
	}

	public void testAdd( )
	{
		Quaternion p = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		Quaternion q = new Quaternion( 1.0, 2.0, 3.0, 4.0 );
		
		p.add( q );
		assertEquals( new Quaternion( 6.0, 8.0, 10.0, 12.0 ), p );
	}
	
	public void testSubtract( )
	{
		Quaternion p = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		Quaternion q = new Quaternion( 4.0, 3.0, 2.0, 1.0 );
		
		p.subtract( q );
		assertEquals( new Quaternion( 1.0, 3.0, 5.0, 7.0 ), p );
	}

	public void testMultiplyIdentity( )
	{
		// check that composition of the identity works as we expect
		Quaternion q = Quaternion.getIdentity();
		
		// now, self-compose the quaternion and check the rotation
		q.multiplyLeft( q );
		assertEquals( Quaternion.getIdentity(), q );
	}
	
	public void testMultiplyByIdentity( )
	{
		Quaternion q = getRandomRotation();
		
		// now multiply q by the identity
		Quaternion p = new Quaternion( q );
		p.multiplyLeft( Quaternion.getIdentity() );
		
		// p should be the same as q
		assertEquals( q, p );
	}
	
	public void testConjugate( )
	{
		Quaternion q = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		
		q.conjugate();
		
		assertEquals( new Quaternion( 5.0, -6.0, -7.0, -8.0 ), q );
	}
	
	public void testGetDot( )
	{
		Quaternion p = new Quaternion( 5.0, 6.0, 7.0, 8.0 );
		Quaternion q = new Quaternion( 4.0, 3.0, 2.0, 1.0 );
		
		assertEquals( 60.0, p.getDot( q ) );
		assertEquals( 60.0, q.getDot( p ) );
	}
	
	public void testRotateIdentity( )
	{
		Quaternion q = Quaternion.getIdentity();
		Vector3 v = new Vector3( 1.0, 2.0, 3.0 );
		q.rotate( v );
		assertEquals( new Vector3( 1.0, 2.0, 3.0 ), v );
	}
	
	public void testGetRotationUnit( )
	{
		Vector3 source = Vector3.getUnitX();
		Vector3 axis = Vector3.getUnitZ();
		
		// 0 deg
		assertEqualsPerpendicularRotation( new Vector3( 1, 0, 0 ), axis, 0.0, source );
		assertEqualsPerpendicularRotation( new Vector3( 1, 0, 0 ), axis, 2.0 * Math.PI, source );
		assertEqualsPerpendicularRotation( new Vector3( 1, 0, 0 ), axis, -2.0 * Math.PI, source );
		
		// 180 deg
		assertEqualsPerpendicularRotation( new Vector3( -1, 0, 0 ), axis, Math.PI, source );
		assertEqualsPerpendicularRotation( new Vector3( -1, 0, 0 ), axis, -Math.PI, source );
		
		// 90 deg
		assertEqualsPerpendicularRotation( new Vector3( 0, 1, 0 ), axis, Math.PI / 2.0, source );
		assertEqualsPerpendicularRotation( new Vector3( 0, -1, 0 ), axis, -Math.PI / 2.0, source );
		
		// 45 deg
		double hsqrt2 = Math.sqrt( 2.0 ) / 2.0;
		assertEqualsPerpendicularRotation( new Vector3( hsqrt2, hsqrt2, 0 ), axis, Math.PI / 4.0, source );
		assertEqualsPerpendicularRotation( new Vector3( hsqrt2, -hsqrt2, 0 ), axis, -Math.PI / 4.0, source );
	}
	
	public void testGetRotationNotUnit( )
	{
		double length = 50.0;
		Vector3 source = new Vector3( length, 0.0, 0.0 );
		Vector3 axis = new Vector3( 0.0, 0.0, 213.0 );
		
		// 0 deg
		assertEqualsPerpendicularRotation( new Vector3( length, 0, 0 ), axis, 0.0, source );
		assertEqualsPerpendicularRotation( new Vector3( length, 0, 0 ), axis, 2.0 * Math.PI, source );
		assertEqualsPerpendicularRotation( new Vector3( length, 0, 0 ), axis, -2.0 * Math.PI, source );
		
		// 180 deg
		assertEqualsPerpendicularRotation( new Vector3( -length, 0, 0 ), axis, Math.PI, source );
		assertEqualsPerpendicularRotation( new Vector3( -length, 0, 0 ), axis, -Math.PI, source );
		
		// 90 deg
		assertEqualsPerpendicularRotation( new Vector3( 0, length, 0 ), axis, Math.PI / 2.0, source );
		assertEqualsPerpendicularRotation( new Vector3( 0, -length, 0 ), axis, -Math.PI / 2.0, source );
		
		// 45 deg
		double hsqrt2 = length * Math.sqrt( 2.0 ) / 2.0;
		assertEqualsPerpendicularRotation( new Vector3( hsqrt2, hsqrt2, 0 ), axis, Math.PI / 4.0, source );
		assertEqualsPerpendicularRotation( new Vector3( hsqrt2, -hsqrt2, 0 ), axis, -Math.PI / 4.0, source );
	}
	
	public void testGetRotationHandedness( )
	{
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		
		// all rotations should be right-handed
		Quaternion.getRotation( q, Vector3.getUnitX(), Math.PI/2.0 );
		Vector3.getUnitY( v );
		q.rotate( v );
		assertEquals( Vector3.getUnitZ(), v );
		
		Quaternion.getRotation( q, Vector3.getUnitY(), Math.PI/2.0 );
		Vector3.getUnitZ( v );
		q.rotate( v );
		assertEquals( Vector3.getUnitX(), v );
		
		Quaternion.getRotation( q, Vector3.getUnitZ(), Math.PI/2.0 );
		Vector3.getUnitX( v );
		q.rotate( v );
		assertEquals( Vector3.getUnitY(), v );
	}
	
	public void testGetRotationSelfCheck( )
	{
		Vector3 axis = new Vector3( 5, 6, 7 );
		axis.normalize();
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, Math.PI / 3.0 );
		Vector3 v = new Vector3( 1, 2, 3 );
		Vector3 originalV = new Vector3( v );
		
		// apply the rotation 6 times and we should be back where we started
		// oh, rotations should preserve distance as well
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		// after three rotations, this should be the same as a rotation by pi
		Quaternion rot = new Quaternion();
		Quaternion.getRotationByPi( rot, axis );
		Vector3 w = new Vector3( originalV );
		rot.rotate( w );
		assertEquals( w, v );
		
		// now, compose the rotation quaternion 3 times and see if it's the same
		Quaternion p = Quaternion.getIdentity();
		p.multiplyLeft( q );
		p.multiplyLeft( q );
		p.multiplyLeft( q );
		Quaternion.getRotationByPi( rot, axis );
		assertEquals( rot, p );
		
		// do the last three rotations
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		assertEquals( originalV, v );
		
		// now compose 3 more rotations to our quaternion and re-check
		p.multiplyLeft( q );
		p.multiplyLeft( q );
		p.multiplyLeft( q );
		assertEquals( Quaternion.getIdentity(), p );
	}
	
	public void testGetRotationByPi( )
	{
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		
		Vector3 negX = Vector3.getUnitX();
		negX.negate();
		Vector3 negY = Vector3.getUnitY();
		negY.negate();
		Vector3 negZ = Vector3.getUnitZ();
		negZ.negate();
		
		// we should end up with the negative axis
		Quaternion.getRotationByPi( q, Vector3.getUnitZ() );
		Vector3.getUnitX( v );
		q.rotate( v );
		assertEquals( negX, v );
		Vector3.getUnitY( v );
		q.rotate( v );
		assertEquals( negY, v );
		
		Quaternion.getRotationByPi( q, Vector3.getUnitY() );
		Vector3.getUnitX( v );
		q.rotate( v );
		assertEquals( negX, v );
		Vector3.getUnitZ( v );
		q.rotate( v );
		assertEquals( negZ, v );
		
		Quaternion.getRotationByPi( q, Vector3.getUnitX() );
		Vector3.getUnitY( v );
		q.rotate( v );
		assertEquals( negY, v );
		Vector3.getUnitZ( v );
		q.rotate( v );
		assertEquals( negZ, v );
	}
	
	public void testGetRotationByPiSelfCheck( )
	{
		Vector3 axis = new Vector3( 5, 6, 7 );
		axis.normalize();
		Quaternion q = new Quaternion();
		Quaternion.getRotationByPi( q, axis );
		Vector3 v = new Vector3( 1, 2, 3 );
		Vector3 originalV = new Vector3( v );
		
		// apply the rotation 2 times and we should be back where we started
		// oh, rotations should preserve distance as well
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		q.rotate( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		assertEquals( originalV, v );
	}
	
	public void testGetRotationBoth( )
	{
		Quaternion p = new Quaternion();
		Quaternion q = new Quaternion();
		Quaternion.getRotation( p, new Vector3( 5, 6, 7 ), Math.PI );
		Quaternion.getRotationByPi( q, new Vector3( 5, 6, 7 ) );
		
		assertEquals( p, q );
	}
	
	public void testComposition( )
	{
		Vector3 axis = Vector3.getUnitZ();
		axis.normalize();
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, Math.PI / 3.0 );
		Quaternion expected = new Quaternion();
		Quaternion composite = Quaternion.getIdentity();
		
		// the second composition should give us a rotation by PI/3
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, Math.PI / 3.0 );
		assertEquals( expected, composite );
		
		// the second composition should give us a rotation by 2PI/3
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, 2.0 * Math.PI / 3.0 );
		assertEquals( expected, composite );
		
		// the next composition should give us a rotation by PI
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, Math.PI );
		assertEquals( expected, composite );

		// the next composition should give us a rotation by 4PI/3
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, 4.0 * Math.PI / 3.0 );
		assertEquals( expected, composite );

		// the next composition should give us a rotation by 6PI/3
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, 5.0 * Math.PI / 3.0 );
		assertEquals( expected, composite );
		
		// the next composition should give us a rotation by 2PI
		composite.multiplyLeft( q );
		Quaternion.getRotation( expected, axis, 2.0 * Math.PI );
		assertEquals( expected, composite );
		
		// p should be the same as the identity
		assertEquals( Quaternion.getIdentity(), composite );
	}
	
	public void testZeroDistance( )
	{
		Quaternion identity = Quaternion.getIdentity();
		Quaternion negIdentity = Quaternion.getIdentity();
		negIdentity.negate();
		
		assertEquals( 0.0, identity.getDistance( identity ), CompareReal.getEpsilon() );
		assertEquals( 0.0, negIdentity.getDistance( negIdentity ), CompareReal.getEpsilon() );
		assertEquals( 0.0, identity.getDistance( negIdentity ), CompareReal.getEpsilon() );
		assertEquals( 0.0, negIdentity.getDistance( identity ), CompareReal.getEpsilon() );
		
		for( int i=0; i<10; i++ )
		{
			Quaternion q = getRandomRotation();
			Quaternion negQ = new Quaternion( q );
			negQ.negate();
			
			assertEquals( 0.0, q.getDistance( q ), CompareReal.getEpsilon() );
			assertEquals( 0.0, negQ.getDistance( negQ ), CompareReal.getEpsilon() );
			assertEquals( 0.0, negQ.getDistance( q ), CompareReal.getEpsilon() );
			assertEquals( 0.0, q.getDistance( negQ ), CompareReal.getEpsilon() );
		}
	}
	
	public void testDistance( )
	{
		int numChecked = 0;
		while( numChecked < 10 )
		{
			Quaternion p = getRandomRotation();
			Quaternion q = getRandomRotation();
			
			// if their dot product is close to 1, they won't have much distance
			// so skip this pair
			if( 1.0 - p.getDot( q ) < 0.01 )
			{
				continue;
			}
			
			assertTrue( p.getDistance( q ) > 0.0 );
			assertTrue( p.getDistance( q ) == q.getDistance( p ) );
			
			numChecked++;
		}
	}
	
	public void testCompositionAndRotation( )
	{
		double length = 50.0;
		Vector3 source = new Vector3( length, 0.0, 0.0 );
		Vector3 axis = new Vector3( 0.0, 0.0, 213.0 );
		
		// 0 deg
		assertRotationDoubleCheck( axis, 0.0, source );
		assertRotationDoubleCheck( axis, 2.0 * Math.PI, source );
		assertRotationDoubleCheck( axis, -2.0 * Math.PI, source );
		
		// 180 deg
		assertRotationDoubleCheck( axis, Math.PI, source );
		assertRotationDoubleCheck( axis, -Math.PI, source );
		
		// 90 deg
		assertRotationDoubleCheck( axis, Math.PI / 2.0, source );
		assertRotationDoubleCheck( axis, -Math.PI / 2.0, source );
		
		// 45 deg
		assertRotationDoubleCheck( axis, Math.PI / 4.0, source );
		assertRotationDoubleCheck( axis, -Math.PI / 4.0, source );
	}
	
	public void testCompositionAndRotationAtRandom( )
	{
		for( int i=0; i<10; i++ )
		{
			assertRotationDoubleCheck(
				getRandomVector( -5.0, 5.0 ),
				getRandomDouble( -2.8 * Math.PI, 2.0 * Math.PI ),
				getRandomVector( -5.0, 5.0 )
			);
		}
	}
	
	public void testToAxisAngle( )
	{
		assertEqualsAxisAngleRotation( Vector3.getUnitX(), Math.PI );
		assertEqualsAxisAngleRotation( Vector3.getUnitY(), Math.PI / 4.0 );
		assertEqualsAxisAngleRotation( Vector3.getUnitZ(), 2.0 * Math.PI / 3.0 );
	}
	
	public void testToAxisAngleAtRandom( )
	{
		for( int i=0; i<10; i++ )
		{
			Vector3 axis = getRandomVector( -5.0, 5.0 );
			axis.normalize();
			assertEqualsAxisAngleRotation( axis, getRandomDouble( -2.0 * Math.PI, 2.0 * Math.PI ) );
		}
	}
	
	public void testGet2VectorRotation( )
	{
		double hsqrt2 = Math.sqrt( 2.0 ) / 2.0;
		assertEquals2VectorRotation( Vector3.getUnitZ(), Math.PI / 2.0, Vector3.getUnitX(), Vector3.getUnitY() );
		assertEquals2VectorRotation( Vector3.getUnitY(), Math.PI / 4.0, Vector3.getUnitX(), new Vector3( hsqrt2, 0.0, -hsqrt2 ) );
	}
	
	public void testGet2VectorRotationAtRandom( )
	{
		int numChecked = 0;
		while( numChecked < 10 )
		{
			// get a random rotation
			Vector3 expectedAxis = getRandomVector( -1.0, 1.0 );
			expectedAxis.normalize();
			double expectedAngle = getRandomDouble( 0, Math.PI );
			
			// if the angle is too small, skip
			if( expectedAngle < 0.01 )
			{
				continue;
			}
			
			// get a random vector that's not too parallel to the axis
			Vector3 from = null;
			do
			{
				from = getRandomVector( -5.0, 5.0 );
			}
			while( 1.0 - Math.abs( from.getDot( expectedAxis ) / from.getLength() ) < 0.1 );
			
			// make from orthogonal to the axis
			Matrix3 p = new Matrix3();
			Matrix3.getOrthogonalProjection( p, expectedAxis );
			p.multiply( from );
			
			assertEqualsReal( 0.0, from.getDot( expectedAxis ) );
			
			// rotate the random vector
			Quaternion q = new Quaternion();
			Quaternion.getRotation( q, expectedAxis, expectedAngle );
			Vector3 to = new Vector3( from );
			q.rotate( to );
			
			assertEquals2VectorRotation( expectedAxis, expectedAngle, from, to );
			
			numChecked++;
		}
	}
	
	private void assertEqualsPerpendicularRotation( Vector3 expected, Vector3 axis, double angle, Vector3 source )
	{
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, angle );
		Vector3 observed = new Vector3( source );
		q.rotate( observed );
		
		// check the expected value
		assertEquals( expected, observed );
		
		assertPerpendicularRotationSane( axis, angle, source );
	}
	
	private void assertPerpendicularRotationSane( Vector3 axis, double angle, Vector3 from )
	{
		// is this really a perpendicular rotation?
		assertEquals( 0.0, Math.abs( axis.getDot( from ) ) / axis.getLength() / from.getLength() );
		
		// compute the rotation
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, angle );
		Vector3 to = new Vector3( from );
		q.rotate( to );
		
		// check some basic properties:
		
		// rotations should preserve distance
		assertEqualsReal( from.getLength(), to.getLength() );
		
		// check the dot product between from and to
		assertEqualsReal( Math.cos( angle ), from.getDot( to ) / from.getLength() / to.getLength() );
	}
	
	private void assertRotationDoubleCheck( Vector3 axis, double angle, Vector3 source )
	{
		// compute the rotation using rotate()
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, angle );
		Vector3 observedA = new Vector3( source );
		q.rotate( observedA );
		
		// compute the rotation using multiply()
		Quaternion p = new Quaternion( q );
		p.conjugate();
		p.multiplyLeft( new Quaternion( source ) );
		p.multiplyLeft( q );
		Vector3 observedB = new Vector3( source );
		q.rotate( observedB );
		
		assertEquals( observedA, observedB );
	}
	
	private void assertEqualsAxisAngleRotation( Vector3 expectedAxis, double expectedAngle )
	{
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, expectedAxis, expectedAngle );
		assertEqualsAxisAngle( expectedAxis, expectedAngle, q );
	}
	
	private void assertEquals2VectorRotation( Vector3 expectedAxis, double expectedAngle, Vector3 from, Vector3 to )
	{
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, from, to );
		assertEqualsAxisAngle( expectedAxis, expectedAngle, q );
	}
	
	private void assertEqualsAxisAngle( Vector3 expectedAxis, double expectedAngle, Quaternion q )
	{
		Vector3 observedAxis = new Vector3();
		double observedAngle = q.toAxisAngle( observedAxis );
		
		// normalize the signs
		if( Math.signum( observedAngle ) != Math.signum( expectedAngle ) )
		{
			observedAngle = -observedAngle;
			observedAxis.negate();
		}
		
		assertEquals( expectedAxis, observedAxis );
		assertEqualsReal( expectedAngle, observedAngle );
	}
}
