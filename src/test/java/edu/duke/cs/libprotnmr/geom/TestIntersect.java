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

public class TestIntersect extends ExtendedTestCase
{
	public void testIsPointInAxisAlignedBox( )
	{
		assertTrue( ipaab( 0.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( 1.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( 0.0, 1.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( 0.0, 0.0, 1.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( -1.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( 0.0, -1.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( ipaab( 0.0, 0.0, -1.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( 1.1, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( 0.0, 1.1, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( 0.0, 0.0, 1.1, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( -1.1, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( 0.0, -1.1, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( ipaab( 0.0, 0.0, -1.1, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
	}
	
	// UNDONE: testIsPointInOrientedBox( )
	// UNDONE: testIsPointInSphere( )
	
	public void testIsOverlappingAxisAlignedBoxSphere( )
	{
		assertTrue( isaab( 0.0, 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( -3.0, 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, -3.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, 0.0, -3.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 3.0, 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, 3.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, 0.0, 3.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( -2.414213562, -2.414213562, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, -2.414213562, -2.414213562, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( -2.414213562, 0.0, -2.414213562, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 2.414213562, 2.414213562, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 2.414213562, 0.0, 2.414213562, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 0.0, 2.414213562, 2.414213562, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( -2.154700538, -2.154700538, -2.154700538, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertTrue( isaab( 2.154700538, 2.154700538, 2.154700538, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( -3.1, 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, -3.1, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, 0.0, -3.1, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 3.1, 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, 3.1, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, 0.0, 3.1, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( -2.5, -2.5, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( -2.5, 0.0, -2.5, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, -2.5, -2.5, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 2.5, 2.5, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 2.5, 0.0, 2.5, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 0.0, 2.5, 2.5, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( -2.2, -2.2, -2.2, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
		assertFalse( isaab( 2.2, 2.2, 2.2, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ) );
	}
	
	public void testGetDistancePointToAxisAlignedBox( )
	{
		assertEquals( 0.0, sdpaab( 0.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 0.0, sdpaab( -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 0.0, sdpaab( 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( -2.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( 2.0, 0.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( 0.0, -2.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( 0.0, 2.0, 0.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( 0.0, 0.0, -2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
		assertEquals( 1.0, sdpaab( 0.0, 0.0, 2.0, -1.0, -1.0, -1.0, 1.0, 1.0, 1.0 ), CompareReal.getEpsilon() );
	}
	
	// UNDONE: testGetDistancesPointInAxisAlignedBox( )
	// UNDONE: testGetDistancesPointInOrientedBox( )
	
	// intersect point w/ axis aligned box (paab)
	private boolean ipaab( double a, double b, double c, double d, double e, double f, double g, double h, double i )
	{
		return Intersect.isPointIn(
			new Vector3( a, b, c ),
			new AxisAlignedBox(
				new Vector3( d, e, f ),
				new Vector3( g, h, i )
			)
		);
	}
	
	// intersect sphere w/ axis aligned box
	private boolean isaab( double a, double b, double c, double d, double e, double f, double g, double h, double i, double j )
	{
		return Intersect.isOverlapping(
			new Sphere(
				new Vector3( a, b, c ),
				d
			),
			new AxisAlignedBox(
				new Vector3( e, f, g ),
				new Vector3( h, i, j )
			)
		);
	}
	
	// squared distance from a point to an axis aligned box
	private double sdpaab( double a, double b, double c, double d, double e, double f, double g, double h, double i )
	{
		return Intersect.getSquaredDistanceFromPointTo(
			new Vector3( a, b, c ),
			new AxisAlignedBox(
				new Vector3( d, e, f ),
				new Vector3( g, h, i )
			)
		);
	}
}
