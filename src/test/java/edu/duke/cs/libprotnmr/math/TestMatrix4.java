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

package edu.duke.cs.libprotnmr.math;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;

public class TestMatrix4 extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		Matrix4 m = new Matrix4();

		assertEquals( 0.0, m.data[0][0] );
		assertEquals( 0.0, m.data[0][1] );
		assertEquals( 0.0, m.data[0][2] );
		assertEquals( 0.0, m.data[0][3] );
		assertEquals( 0.0, m.data[1][0] );
		assertEquals( 0.0, m.data[1][1] );
		assertEquals( 0.0, m.data[1][2] );
		assertEquals( 0.0, m.data[1][3] );
		assertEquals( 0.0, m.data[2][0] );
		assertEquals( 0.0, m.data[2][1] );
		assertEquals( 0.0, m.data[2][2] );
		assertEquals( 0.0, m.data[2][3] );
		assertEquals( 0.0, m.data[3][0] );
		assertEquals( 0.0, m.data[3][1] );
		assertEquals( 0.0, m.data[3][2] );
		assertEquals( 0.0, m.data[3][3] );
	}
	
	public void testAssignmentConstructor( )
	{
		Matrix4 m = new Matrix4( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 );
		
		assertEquals( 1.0, m.data[0][0], CompareReal.getEpsilon() );
		assertEquals( 2.0, m.data[0][1], CompareReal.getEpsilon() );
		assertEquals( 3.0, m.data[0][2], CompareReal.getEpsilon() );
		assertEquals( 4.0, m.data[0][3], CompareReal.getEpsilon() );
		assertEquals( 5.0, m.data[1][0], CompareReal.getEpsilon() );
		assertEquals( 6.0, m.data[1][1], CompareReal.getEpsilon() );
		assertEquals( 7.0, m.data[1][2], CompareReal.getEpsilon() );
		assertEquals( 8.0, m.data[1][3], CompareReal.getEpsilon() );
		assertEquals( 9.0, m.data[2][0], CompareReal.getEpsilon() );
		assertEquals( 10.0, m.data[2][1], CompareReal.getEpsilon() );
		assertEquals( 11.0, m.data[2][2], CompareReal.getEpsilon() );
		assertEquals( 12.0, m.data[2][3], CompareReal.getEpsilon() );
		assertEquals( 13.0, m.data[3][0], CompareReal.getEpsilon() );
		assertEquals( 14.0, m.data[3][1], CompareReal.getEpsilon() );
		assertEquals( 15.0, m.data[3][2], CompareReal.getEpsilon() );
		assertEquals( 16.0, m.data[3][3], CompareReal.getEpsilon() );
	}
	
	public void testCopyConstructor( )
	{
		Matrix4 m = new Matrix4( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 );
		Matrix4 n = new Matrix4( m );
		
		assertEquals( 1.0, n.data[0][0], CompareReal.getEpsilon() );
		assertEquals( 2.0, n.data[0][1], CompareReal.getEpsilon() );
		assertEquals( 3.0, n.data[0][2], CompareReal.getEpsilon() );
		assertEquals( 4.0, n.data[0][3], CompareReal.getEpsilon() );
		assertEquals( 5.0, n.data[1][0], CompareReal.getEpsilon() );
		assertEquals( 6.0, n.data[1][1], CompareReal.getEpsilon() );
		assertEquals( 7.0, n.data[1][2], CompareReal.getEpsilon() );
		assertEquals( 8.0, n.data[1][3], CompareReal.getEpsilon() );
		assertEquals( 9.0, n.data[2][0], CompareReal.getEpsilon() );
		assertEquals( 10.0, n.data[2][1], CompareReal.getEpsilon() );
		assertEquals( 11.0, n.data[2][2], CompareReal.getEpsilon() );
		assertEquals( 12.0, n.data[2][3], CompareReal.getEpsilon() );
		assertEquals( 13.0, n.data[3][0], CompareReal.getEpsilon() );
		assertEquals( 14.0, n.data[3][1], CompareReal.getEpsilon() );
		assertEquals( 15.0, n.data[3][2], CompareReal.getEpsilon() );
		assertEquals( 16.0, n.data[3][3], CompareReal.getEpsilon() );
		assertNotSame( m.data, n.data );
	}
	
	public void testDeterminant( )
	{
		Matrix4 m;
		
		m = new Matrix4( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 );
		assertEquals( 0.0, m.getDeterminant(), CompareReal.getEpsilon() );
		
		m = new Matrix4( 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, -1, 0, 0, 1 );
		assertEquals( 2.0, m.getDeterminant(), CompareReal.getEpsilon() );
	}
	
	public void testMultiplyTransformation( )
	{
		Matrix4 m = new Matrix4( 1, 2, 3, 0, 4, 5, 6, 0, 7, 8, 9, 0 );
		Vector3 v = new Vector3( 1, 2, 3 );
		
		m.multiply( v );
		
		assertEquals( 14.0, v.x, CompareReal.getEpsilon() );
		assertEquals( 32.0, v.y, CompareReal.getEpsilon() );
		assertEquals( 50.0, v.z, CompareReal.getEpsilon() );
	}

	public void testMultiplyTranslation( )
	{
		Matrix4 m = new Matrix4( 1, 0, 0, 7, 0, 1, 0, 8, 0, 0, 1, 9 );
		Vector3 v = new Vector3( 1, 2, 3 );
		
		m.multiply( v );
		
		assertEquals( 8.0, v.x, CompareReal.getEpsilon() );
		assertEquals( 10.0, v.y, CompareReal.getEpsilon() );
		assertEquals( 12.0, v.z, CompareReal.getEpsilon() );
	}

	public void testMultiplyTransformationTranslation( )
	{
		Matrix4 m = new Matrix4( 1, 2, 3, 10, 4, 5, 6, 11, 7, 8, 9, 12 );
		Vector3 v = new Vector3( 1, 2, 3 );
		
		m.multiply( v );
		
		assertEquals( 24.0, v.x, CompareReal.getEpsilon() );
		assertEquals( 43.0, v.y, CompareReal.getEpsilon() );
		assertEquals( 62.0, v.z, CompareReal.getEpsilon() );
	}
}
