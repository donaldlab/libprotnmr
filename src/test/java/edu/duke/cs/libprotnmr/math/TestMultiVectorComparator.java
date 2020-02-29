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

import java.util.ArrayList;
import java.util.Collections;

import edu.duke.cs.libprotnmr.ExtendedTestCase;


public class TestMultiVectorComparator extends ExtendedTestCase
{
	public void testOneDimensionalSortX( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 6, 7 ) );
		points.add( newPoint( 3, 4 ) );
		points.add( newPoint( 1, 2 ) );
		points.add( newPoint( 4, 5 ) );
		points.add( newPoint( 2, 3 ) );
		points.add( newPoint( 5, 6 ) );
		
		Collections.sort( points, new MultiVectorComparator( 0 ) );
		
		assertEqualsPoint( 1, 2, points.get( 0 ) );
		assertEqualsPoint( 2, 3, points.get( 1 ) );
		assertEqualsPoint( 3, 4, points.get( 2 ) );
		assertEqualsPoint( 4, 5, points.get( 3 ) );
		assertEqualsPoint( 5, 6, points.get( 4 ) );
		assertEqualsPoint( 6, 7, points.get( 5 ) );
	}
	
	public void testOneDimensionalSortY( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 1, 7 ) );
		points.add( newPoint( 4, 4 ) );
		points.add( newPoint( 6, 2 ) );
		points.add( newPoint( 3, 5 ) );
		points.add( newPoint( 5, 3 ) );
		points.add( newPoint( 2, 6 ) );
		
		Collections.sort( points, new MultiVectorComparator( 1 ) );
		
		assertEqualsPoint( 6, 2, points.get( 0 ) );
		assertEqualsPoint( 5, 3, points.get( 1 ) );
		assertEqualsPoint( 4, 4, points.get( 2 ) );
		assertEqualsPoint( 3, 5, points.get( 3 ) );
		assertEqualsPoint( 2, 6, points.get( 4 ) );
		assertEqualsPoint( 1, 7, points.get( 5 ) );
	}
	
	public void testLexicographicalSort( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 3, 4 ) );
		points.add( newPoint( 1, 2 ) );
		points.add( newPoint( 2, 3 ) );
		points.add( newPoint( 1, 1 ) );
		points.add( newPoint( 2, 2 ) );
		points.add( newPoint( 3, 1 ) );
		
		Collections.sort( points, new MultiVectorComparator() );
		
		assertEqualsPoint( 1, 1, points.get( 0 ) );
		assertEqualsPoint( 1, 2, points.get( 1 ) );
		assertEqualsPoint( 2, 2, points.get( 2 ) );
		assertEqualsPoint( 2, 3, points.get( 3 ) );
		assertEqualsPoint( 3, 1, points.get( 4 ) );
		assertEqualsPoint( 3, 4, points.get( 5 ) );
	}
	
	private MultiVector newPoint( double x, double y )
	{
		return new MultiVectorImpl( new double[] { x, y } );
	}
	
	private void assertEqualsPoint( double x, double y, MultiVector point )
	{
		assertEquals( x, point.get( 0 ) );
		assertEquals( y, point.get( 1 ) );
	}
}
