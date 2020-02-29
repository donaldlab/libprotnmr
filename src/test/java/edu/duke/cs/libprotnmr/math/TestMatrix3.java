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

public class TestMatrix3 extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		Matrix3 m = new Matrix3();
		
		assertEquals( 0.0, m.data[0][0] );
		assertEquals( 0.0, m.data[0][1] );
		assertEquals( 0.0, m.data[0][2] );
		assertEquals( 0.0, m.data[1][0] );
		assertEquals( 0.0, m.data[1][1] );
		assertEquals( 0.0, m.data[1][2] );
		assertEquals( 0.0, m.data[2][0] );
		assertEquals( 0.0, m.data[2][1] );
		assertEquals( 0.0, m.data[2][2] );
	}
	
	public void testAssignentConstructor1( )
	{
		Matrix3 m = new Matrix3( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		
		assertEquals( 1.0, m.data[0][0] );
		assertEquals( 2.0, m.data[0][1] );
		assertEquals( 3.0, m.data[0][2] );
		assertEquals( 4.0, m.data[1][0] );
		assertEquals( 5.0, m.data[1][1] );
		assertEquals( 6.0, m.data[1][2] );
		assertEquals( 7.0, m.data[2][0] );
		assertEquals( 8.0, m.data[2][1] );
		assertEquals( 9.0, m.data[2][2] );
	}
	
	public void testSetRows( )
	{
		Matrix3 m = new Matrix3();
		m.setRows( new Vector3( 1, 2, 3 ), new Vector3( 4, 5, 6 ), new Vector3( 7, 8, 9 ) );
		
		assertEquals( 1.0, m.data[0][0] );
		assertEquals( 2.0, m.data[0][1] );
		assertEquals( 3.0, m.data[0][2] );
		assertEquals( 4.0, m.data[1][0] );
		assertEquals( 5.0, m.data[1][1] );
		assertEquals( 6.0, m.data[1][2] );
		assertEquals( 7.0, m.data[2][0] );
		assertEquals( 8.0, m.data[2][1] );
		assertEquals( 9.0, m.data[2][2] );
	}
	
	public void testSetColumns( )
	{
		Matrix3 m = new Matrix3();
		m.setColumns( new Vector3( 1, 2, 3 ), new Vector3( 4, 5, 6 ), new Vector3( 7, 8, 9 ) );
		
		assertEquals( 1.0, m.data[0][0] );
		assertEquals( 2.0, m.data[1][0] );
		assertEquals( 3.0, m.data[2][0] );
		assertEquals( 4.0, m.data[0][1] );
		assertEquals( 5.0, m.data[1][1] );
		assertEquals( 6.0, m.data[2][1] );
		assertEquals( 7.0, m.data[0][2] );
		assertEquals( 8.0, m.data[1][2] );
		assertEquals( 9.0, m.data[2][2] );
	}
	
	public void testCopyConstructor( )
	{
		Matrix3 m = new Matrix3( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		Matrix3 n = new Matrix3( m );
		
		assertEquals( 1.0, n.data[0][0] );
		assertEquals( 2.0, n.data[0][1] );
		assertEquals( 3.0, n.data[0][2] );
		assertEquals( 4.0, n.data[1][0] );
		assertEquals( 5.0, n.data[1][1] );
		assertEquals( 6.0, n.data[1][2] );
		assertEquals( 7.0, n.data[2][0] );
		assertEquals( 8.0, n.data[2][1] );
		assertEquals( 9.0, n.data[2][2] );
		assertNotSame( m.data, n.data );
		assertNotSame( m.data[0], n.data[0] );
		assertNotSame( m.data[1], n.data[1] );
		assertNotSame( m.data[2], n.data[2] );
	}
	
	public void testDeterminant( )
	{
		Matrix3 m = new Matrix3( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		
		assertEquals( 0.0, m.getDeterminant(), CompareReal.getEpsilon() );
	}
	
	public void testMultiplyVector( )
	{
		Matrix3 m = new Matrix3( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		Vector3 v = new Vector3( 1, 2, 3 );
		
		m.multiply( v );
		
		assertEquals( 14.0, v.x, CompareReal.getEpsilon() );
		assertEquals( 32.0, v.y, CompareReal.getEpsilon() );
		assertEquals( 50.0, v.z, CompareReal.getEpsilon() );
	}
	
	public void testMultiplyMatrix3( )
	{
		Matrix3 m = new Matrix3( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		Matrix3 n = new Matrix3( 10, 11, 12, 13, 14, 15, 16, 17, 18 );
		Matrix3 result = new Matrix3( 138, 171, 204, 174, 216, 258, 210, 261, 312 );
		
		Matrix3 temp = new Matrix3();
		n.multiplyRight( temp, m );
		assertEquals( result, temp );
	}
	
	public void testGetRotationNull( )
	{
		Matrix3 m = new Matrix3();
		Vector3 v = null;
		
		Matrix3.getRotation( m, Vector3.getUnitX(), 0.0 );
		v = new Vector3( 1, 2, 3 );
		m.multiply( v );
		assertEquals( new Vector3( 1, 2, 3 ), v );
		
		Matrix3.getRotation( m, Vector3.getUnitX(), Math.PI * 2.0 );
		v = new Vector3( 1, 2, 3 );
		m.multiply( v );
		assertEquals( new Vector3( 1, 2, 3 ), v );
	}
	
	public void testGetRotationHandedness( )
	{
		Matrix3 rot = new Matrix3();
		Vector3 v = new Vector3();
		
		// all rotations should be right-handed
		Matrix3.getRotation( rot, Vector3.getUnitX(), Math.PI/2.0 );
		Vector3.getUnitY( v );
		rot.multiply( v );
		assertEquals( Vector3.getUnitZ(), v );
		
		Matrix3.getRotation( rot, Vector3.getUnitY(), Math.PI/2.0 );
		Vector3.getUnitZ( v );
		rot.multiply( v );
		assertEquals( Vector3.getUnitX(), v );
		
		Matrix3.getRotation( rot, Vector3.getUnitZ(), Math.PI/2.0 );
		Vector3.getUnitX( v );
		rot.multiply( v );
		assertEquals( Vector3.getUnitY(), v );
	}
	
	public void testGetRotationSelfCheck( )
	{
		Vector3 axis = new Vector3( 5, 6, 7 );
		axis.normalize();
		Matrix3 m = new Matrix3();
		Matrix3.getRotation( m, axis, Math.PI / 3.0 );
		Vector3 v = new Vector3( 1, 2, 3 );
		Vector3 originalV = new Vector3( v );
		
		// apply the rotation 6 times and we should be back where we started
		// oh, rotations should preserve distance as well
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		// after three rotations, this should be the same as a rotation by pi
		Vector3 w = new Vector3( originalV );
		Matrix3 rot = new Matrix3();
		Matrix3.getRotationByPi( rot, axis );
		rot.multiply( w );
		assertEquals( w, v );
		
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		assertEquals( originalV, v );
	}
	
	public void testGetRotationByPi( )
	{
		Matrix3 m = new Matrix3();
		Vector3 v = new Vector3();
		
		Vector3 negX = Vector3.getUnitX();
		negX.negate();
		Vector3 negY = Vector3.getUnitY();
		negY.negate();
		Vector3 negZ = Vector3.getUnitZ();
		negZ.negate();
		
		// we should end up with the negative axis
		Matrix3.getRotationByPi( m, new Vector3( 0, 0, 1 ) );
		Vector3.getUnitX( v );
		m.multiply( v );
		assertEquals( negX, v );
		Vector3.getUnitY( v );
		m.multiply( v );
		assertEquals( negY, v );
		
		Matrix3.getRotationByPi( m, new Vector3( 0, 1, 0 ) );
		Vector3.getUnitX( v );
		m.multiply( v );
		assertEquals( negX, v );
		Vector3.getUnitZ( v );
		m.multiply( v );
		assertEquals( negZ, v );
		
		Matrix3.getRotationByPi( m, new Vector3( 1, 0, 0 ) );
		Vector3.getUnitY( v );
		m.multiply( v );
		assertEquals( negY, v );
		Vector3.getUnitZ( v );
		m.multiply( v );
		assertEquals( negZ, v );
	}
	
	public void testGetRotationByPiSelfCheck( )
	{
		Vector3 axis = new Vector3( 5, 6, 7 );
		axis.normalize();
		Matrix3 m = new Matrix3();
		Matrix3.getRotationByPi( m, axis );
		Vector3 v = new Vector3( 1, 2, 3 );
		Vector3 originalV = new Vector3( v );
		
		// apply the rotation 2 times and we should be back where we started
		// oh, rotations should preserve distance as well
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		m.multiply( v );
		assertEquals( originalV.getLength(), v.getLength(), CompareReal.getEpsilon() );
		
		assertEquals( originalV, v );
	}
	
	public void testGetRotationBoth( )
	{
		Matrix3 m = new Matrix3();
		Matrix3 n = new Matrix3();
		
		Vector3 axis = new Vector3( 5, 6, 7 );
		axis.normalize();
		Matrix3.getRotation( m, axis, Math.PI );
		Matrix3.getRotationByPi( n, axis );
		
		assertEquals( m, n );
	}
}
