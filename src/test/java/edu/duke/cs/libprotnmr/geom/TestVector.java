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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.math.CompareReal;

public class TestVector extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		Vector3 vec = new Vector3();
		
		assertEquals( 0.0, vec.x );
		assertEquals( 0.0, vec.y );
		assertEquals( 0.0, vec.z );
	}
	public void testAssignmentConstructor1( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		assertEquals( 1.0, vec.x );
		assertEquals( 2.0, vec.y );
		assertEquals( 3.0, vec.z );
	}
	
	public void testAssignmentConstructor2( )
	{
		Vector3 vec = new Vector3( new double[] { 1, 2, 3 } );
		
		assertEquals( 1.0, vec.x );
		assertEquals( 2.0, vec.y );
		assertEquals( 3.0, vec.z );
	}
	
	public void testCopyConstructor( )
	{
		Vector3 veca = new Vector3( 1, 2, 3 );
		
		Vector3 vecb = new Vector3( veca );
		
		assertEquals( 1.0, vecb.x );
		assertEquals( 2.0, vecb.y );
		assertEquals( 3.0, vecb.z );
	}
	
	public void testGet( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		assertEquals( 1.0, vec.get( 0 ) );
		assertEquals( 2.0, vec.get( 1 ) );
		assertEquals( 3.0, vec.get( 2 ) );
	}
	
	public void testSet( )
	{
		Vector3 vec = new Vector3( 0, 0, 0 );
		
		vec.set( 0, 1 );
		vec.set( 1, 2 );
		vec.set( 2, 3 );
		
		assertEquals( 1.0, vec.x );
		assertEquals( 2.0, vec.y );
		assertEquals( 3.0, vec.z );
	}
	
	public void testSetAll( )
	{
		Vector3 vec = new Vector3();
		Vector3 newVec = new Vector3( 1, 2, 3 );
		
		vec.set( newVec );
		
		assertEquals( 1.0, vec.x );
		assertEquals( 2.0, vec.y );
		assertEquals( 3.0, vec.z );
	}
	
	public void testSquaredLength( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		assertEquals( 14.0, vec.getSquaredLength(), CompareReal.getEpsilon() );
	}

	public void testLength( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		assertEquals( 3.741657387, vec.getLength(), CompareReal.getEpsilon() );
	}

	public void testNormalize( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		vec.normalize();
		
		assertEquals( new Vector3( 0.267261242, 0.534522484, 0.801783726 ), vec );
	}
	
	public void testGetSquaredDistance( )
	{
		Vector3 veca = new Vector3( 1, 2, 3 );
		Vector3 vecb = new Vector3( 9, 8, 7 );
		
		assertEquals( 116.0, veca.getSquaredDistance( vecb ) );
	}
	
	public void testGetDistance( )
	{
		Vector3 veca = new Vector3( 1, 2, 3 );
		Vector3 vecb = new Vector3( 9, 8, 7 );
		
		assertEquals( 10.770329614, veca.getDistance( vecb ), CompareReal.getEpsilon() );
	}
	
	public void testAdd( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		vec.add( new Vector3( 7, 8, 9 ) );
		
		assertEquals( new Vector3( 8, 10, 12 ), vec );
	}

	public void testSubtract( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		
		vec.subtract( new Vector3( 9, 8, 7 ) );
		
		assertEquals( new Vector3( -8, -6, -4 ), vec );
	}
	
	public void testScale( )
	{
		Vector3 vec = new Vector3( 1, 2, 3 );
		vec.scale( 6 );
		
		assertEquals( new Vector3( 6, 12, 18 ), vec );
	}
	
	public void testGetDot( )
	{
		Vector3 veca = new Vector3( 1, 2, 3 );
		Vector3 vecb = new Vector3( 7, 8, 9 );
		
		assertEquals( 50.0, veca.getDot( vecb ), CompareReal.getEpsilon() );
	}
	
	public void testGetCross( )
	{
		Vector3 veca = new Vector3( 1, 2, 3 );
		Vector3 vecb = new Vector3( 7, 8, 9 );
		Vector3 temp = new Vector3();
		
		veca.getCross( temp, vecb );
		assertEquals( new Vector3( -6, 12, -6 ), temp );
		
		// cross products should be right-handed!
		Vector3.getUnitX().getCross( temp, Vector3.getUnitY() );
		assertEquals( Vector3.getUnitZ(), temp );
		Vector3.getUnitY().getCross( temp, Vector3.getUnitZ() );
		assertEquals( Vector3.getUnitX(), temp );
		Vector3.getUnitZ().getCross( temp, Vector3.getUnitX() );
		assertEquals( Vector3.getUnitY(), temp );
	}
}
