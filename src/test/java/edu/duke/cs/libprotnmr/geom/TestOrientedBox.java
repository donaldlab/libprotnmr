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

public class TestOrientedBox extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		OrientedBox box = new OrientedBox();
		
		assertEquals( Vector3.getUnitX(), box.x );
		assertEquals( Vector3.getUnitY(), box.y );
		assertEquals( Vector3.getUnitZ(), box.z );
		assertEquals( new Vector3(), box.min );
		assertEquals( new Vector3(), box.max );
	}
	
	public void testAssignmentConstructor( )
	{
		Vector3 x = new Vector3( 1, 2, 3 );
		Vector3 y = new Vector3( 4, 5, 6 );
		Vector3 z = new Vector3( 7, 8, 9 );
		Vector3 min = new Vector3( 10, 11, 12 );
		Vector3 max = new Vector3( 13, 14, 15 );
		OrientedBox box = new OrientedBox( x, y, z, min, max );
		
		assertEquals( x, box.x );
		assertEquals( y, box.y );
		assertEquals( z, box.z );
		assertEquals( min, box.min );
		assertEquals( max, box.max );
		
		assertNotSame( x, box.x );
		assertNotSame( y, box.y );
		assertNotSame( z, box.z );
		assertNotSame( min, box.min );
		assertNotSame( max, box.max );
	}
	
	public void testCopyConstructor( )
	{
		Vector3 x = new Vector3( 1, 2, 3 );
		Vector3 y = new Vector3( 4, 5, 6 );
		Vector3 z = new Vector3( 7, 8, 9 );
		Vector3 min = new Vector3( 10, 11, 12 );
		Vector3 max = new Vector3( 13, 14, 15 );
		OrientedBox boxa = new OrientedBox( x, y, z, min, max );
		OrientedBox boxb = new OrientedBox( boxa );
		
		assertEquals( x, boxb.x );
		assertEquals( y, boxb.y );
		assertEquals( z, boxb.z );
		assertEquals( min, boxb.min );
		assertEquals( max, boxb.max );
		
		assertNotSame( boxa.x, boxb.x );
		assertNotSame( boxa.y, boxb.y );
		assertNotSame( boxa.z, boxb.z );
		assertNotSame( boxa.min, boxb.min );
		assertNotSame( boxa.max, boxb.max );
	}
	
	public void testGetCorner( )
	{
		Vector3 min = new Vector3( 10, 11, 12 );
		Vector3 max = new Vector3( 13, 14, 15 );
		OrientedBox box = new OrientedBox( Vector3.getUnitX(), Vector3.getUnitY(), Vector3.getUnitZ(), min, max );
		Vector3 c = new Vector3();
		
		box.getCorner( c, 0 );
		assertEquals( new Vector3( 10, 11, 12 ), c );
		box.getCorner( c, 1 );
		assertEquals( new Vector3( 10, 11, 15 ), c );
		box.getCorner( c, 2 );
		assertEquals( new Vector3( 10, 14, 12 ), c );
		box.getCorner( c, 3 );
		assertEquals( new Vector3( 10, 14, 15 ), c );
		box.getCorner( c, 4 );
		assertEquals( new Vector3( 13, 11, 12 ), c );
		box.getCorner( c, 5 );
		assertEquals( new Vector3( 13, 11, 15 ), c );
		box.getCorner( c, 6 );
		assertEquals( new Vector3( 13, 14, 12 ), c );
		box.getCorner( c, 7 );
		assertEquals( new Vector3( 13, 14, 15 ), c );
	}
	
	// UNDONE: test point transform functions
}
