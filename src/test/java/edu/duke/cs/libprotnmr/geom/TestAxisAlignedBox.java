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

public class TestAxisAlignedBox extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		AxisAlignedBox box = new AxisAlignedBox();
		
		assertEquals( new Vector3(), box.min );
		assertEquals( new Vector3(), box.max );
	}
	
	public void testAssignmentConstructor1( )
	{
		Vector3 min = new Vector3( 1, 2, 3 );
		Vector3 max = new Vector3( 4, 5, 6 );
		AxisAlignedBox box = new AxisAlignedBox( min, max );
		
		assertEquals( min, box.min );
		assertEquals( max, box.max );
		assertNotSame( min, box.min );
		assertNotSame( max, box.max );
	}
	
	public void testAssignmentConstructor2( )
	{
		AxisAlignedBox box = new AxisAlignedBox( 1, 2, 3, 4, 5, 6 );
		
		assertEquals( 1.0, box.min.x );
		assertEquals( 2.0, box.min.y );
		assertEquals( 3.0, box.min.z );
		assertEquals( 4.0, box.max.x );
		assertEquals( 5.0, box.max.y );
		assertEquals( 6.0, box.max.z );
	}
	
	public void testCopyConstructor( )
	{
		AxisAlignedBox box = new AxisAlignedBox( 1, 2, 3, 4, 5, 6 );
		AxisAlignedBox newBox = new AxisAlignedBox( box );
		
		assertEquals( box.min, newBox.min );
		assertEquals( box.max, newBox.max );
		assertNotSame( box.min, newBox.min );
		assertNotSame( box.max, newBox.max );
	}
	
	public void testCorners( )
	{
		AxisAlignedBox box = new AxisAlignedBox( 1, 2, 3, 4, 5, 6 );
		Vector3 c = new Vector3();
		
		assertEquals( 8, AxisAlignedBox.NumCorners );
		box.getCorner( c, 0 );
		assertEquals( new Vector3( 1, 2, 3 ), c );
		box.getCorner( c, 1 );
		assertEquals( new Vector3( 1, 2, 6 ), c );
		box.getCorner( c, 2 );
		assertEquals( new Vector3( 1, 5, 3 ), c );
		box.getCorner( c, 3 );
		assertEquals( new Vector3( 1, 5, 6 ), c );
		box.getCorner( c, 4 );
		assertEquals( new Vector3( 4, 2, 3 ), c );
		box.getCorner( c, 5 );
		assertEquals( new Vector3( 4, 2, 6 ), c );
		box.getCorner( c, 6 );
		assertEquals( new Vector3( 4, 5, 3 ), c );
		box.getCorner( c, 7 );
		assertEquals( new Vector3( 4, 5, 6 ), c );
	}
	
	public void testDiagonalSquared( )
	{
		assertEquals( 27.0, new AxisAlignedBox( 1, 2, 3, 4, 5, 6 ).getDiagonalSquared() );
		assertEquals( 35.0, new AxisAlignedBox( 1, 2, 3, 6, 5, 4 ).getDiagonalSquared() );
	}
}
