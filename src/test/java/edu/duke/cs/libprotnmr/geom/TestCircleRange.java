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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import edu.duke.cs.libprotnmr.math.CompareReal;

import org.junit.Test;


public class TestCircleRange
{
	private static final double Delta = 1e-4;
	private static final double Epsilon = 1e-12;
	
	public TestCircleRange( )
	{
		assert( Delta > CompareReal.getEpsilon() );
		assert( Epsilon < CompareReal.getEpsilon() );
	}
	
	@Test
	public void testPointFactory( )
	{
		// nothing fancy going on here
		assertEqualsRange( 0.0, 0.0, CircleRange.newByPoint( 0.0 ) );
		assertEqualsRange( Math.PI, Math.PI, CircleRange.newByPoint( Math.PI ) );
		assertFalse( CircleRange.newByPoint( 0.0 ).isCircle() );
		
		// test the wrapping
		assertEqualsRange( Math.PI / 2.0, Math.PI / 2.0, CircleRange.newByPoint( 5.0 / 2.0 * Math.PI ) );
		assertEqualsRange( -Math.PI / 2.0, -Math.PI / 2.0, CircleRange.newByPoint( 3.0 / 2.0 * Math.PI ) );
		assertEqualsRange( -Math.PI / 2.0, -Math.PI / 2.0, CircleRange.newByPoint( -5.0 / 2.0 * Math.PI ) );
		assertEqualsRange( Math.PI / 2.0, Math.PI / 2.0, CircleRange.newByPoint( -3.0 / 2.0 * Math.PI ) );
		
		// test wrapping edge cases
		assertEqualsRange( Math.PI, Math.PI, CircleRange.newByPoint( Math.PI ) );
		assertEqualsRange( Math.PI, Math.PI, CircleRange.newByPoint( -Math.PI ) );
		assertEqualsRange( Math.PI, Math.PI, CircleRange.newByPoint( 3.0 * Math.PI ) );
		assertEqualsRange( Math.PI, Math.PI, CircleRange.newByPoint( -3.0 * Math.PI ) );
		assertEqualsRange( -Math.PI + Delta, -Math.PI + Delta, CircleRange.newByPoint( -Math.PI + Delta ) );
	}
	
	@Test
	public void testShortEdgeFactory( )
	{
		assertEqualsRange( 0.0, 0.0, CircleRange.newByShortSegment( 0.0, 0.0 ) );
		assertFalse( CircleRange.newByShortSegment( 0.0, 0.0 ).isCircle() );
		
		assertEqualsRange( 0.0, 1.0, CircleRange.newByShortSegment( 1.0, 0.0 ) );
		assertEqualsRange( 0.0, 1.0, CircleRange.newByShortSegment( 0.0, 1.0 ) );
		assertEqualsRange( -1.0, 0.0, CircleRange.newByShortSegment( -1.0, 0.0 ) );
		assertEqualsRange( -1.0, 0.0, CircleRange.newByShortSegment( 0.0, -1.0 ) );

		assertEqualsRange( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI, CircleRange.newByShortSegment( -3.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI ) );
		assertEqualsRange( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI, CircleRange.newByShortSegment( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI ) );
		assertFalse( CircleRange.newByShortSegment( 1.0, 0.0 ).isCircle() );
	}
	
	@Test
	public void testCounterclockwiseEdgeFactory( )
	{
		assertEqualsRange( 0.0, 0.0, 0.0, CircleRange.newByCounterclockwiseSegment( 0.0, 0.0 ) );
		assertEqualsRange( 1.0, 0.0, 2.0 * Math.PI - 1.0, CircleRange.newByCounterclockwiseSegment( 1.0, 0.0 ) );
		assertEqualsRange( 0.0, 1.0, 1.0, CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 ) );
		assertEqualsRange( -1.0, 0.0, 1.0, CircleRange.newByCounterclockwiseSegment( -1.0, 0.0 ) );
		assertEqualsRange( 0.0, -1.0, 2.0 * Math.PI - 1.0, CircleRange.newByCounterclockwiseSegment( 0.0, -1.0 ) );
		assertEqualsRange( -3.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI, 3.0 * Math.PI / 2.0, CircleRange.newByCounterclockwiseSegment( -3.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI ) );
		assertEqualsRange( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI, Math.PI / 2.0, CircleRange.newByCounterclockwiseSegment( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI ) );
		assertEqualsRange( -3.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI, 3.0 * Math.PI / 2.0, CircleRange.newByCounterclockwiseSegment( -11.0 / 4.0 * Math.PI, 11.0 / 4.0 * Math.PI ) );
		assertEqualsRange( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI, Math.PI / 2.0, CircleRange.newByCounterclockwiseSegment( 11.0 / 4.0 * Math.PI, -11.0 / 4.0 * Math.PI ) );
	}
	
	@Test
	public void testCircleFactory( )
	{
		CircleRange range = CircleRange.newCircle();
		assertTrue( range.isCircle() );
		assertEquals( 0.0, range.getSource(), 0.0 );
		assertEquals( 0.0, range.getTarget(), 0.0 );
	}
	
	@Test
	public void testContainsPointWithPoints( )
	{
		CircleRange range = CircleRange.newByShortSegment( 0.0, 0.0 );
		assertEquals( 0.0, range.getLength(), 0.0 );
		assertTrue( range.containsPoint( 0.0 ) );
		assertFalse( range.containsPoint( Delta ) );
		assertFalse( range.containsPoint( -Delta ) );
		assertFalse( range.containsPoint( Math.PI ) );
		assertTrue( range.containsPoint( 2.0 * Math.PI ) );
		assertFalse( range.containsPoint( 2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPoint( 2.0 * Math.PI - Delta ) );
		assertTrue( range.containsPoint( -2.0 * Math.PI ) );
		assertFalse( range.containsPoint( -2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPoint( -2.0 * Math.PI - Delta ) );
		
		range = CircleRange.newByShortSegment( Math.PI, Math.PI );
		assertEquals( 0.0, range.getLength(), 0.0 );
		assertTrue( range.containsPoint( Math.PI ) );
		assertFalse( range.containsPoint( Math.PI + Delta ) );
		assertFalse( range.containsPoint( Math.PI - Delta ) );
		assertFalse( range.containsPoint( 0.0 ) );
		assertTrue( range.containsPoint( 3.0 * Math.PI ) );
		assertFalse( range.containsPoint( 3.0 * Math.PI + Delta ) );
		assertFalse( range.containsPoint( 3.0 * Math.PI - Delta ) );
		assertTrue( range.containsPoint( -3.0 * Math.PI ) );
		assertFalse( range.containsPoint( -3.0 * Math.PI + Delta ) );
		assertFalse( range.containsPoint( -3.0 * Math.PI - Delta ) );
	}
	
	@Test
	public void testContainsPointWithCircles( )
	{
		CircleRange range = CircleRange.newCircle();
		assertTrue( range.containsPoint( 0.0 ) );
		assertTrue( range.containsPoint( Delta ) );
		assertTrue( range.containsPoint( -Delta ) );
		assertTrue( range.containsPoint( Math.PI ) );
		assertTrue( range.containsPoint( 2.0 * Math.PI ) );
		assertTrue( range.containsPoint( 2.0 * Math.PI + Delta ) );
		assertTrue( range.containsPoint( 2.0 * Math.PI - Delta ) );
		assertTrue( range.containsPoint( -2.0 * Math.PI ) );
		assertTrue( range.containsPoint( -2.0 * Math.PI + Delta ) );
		assertTrue( range.containsPoint( -2.0 * Math.PI - Delta ) );
	}
	
	@Test
	public void testContainsPointWithRanges( )
	{
		CircleRange range = CircleRange.newByShortSegment( 1.0, 0.0 );
		assertTrue( range.containsPoint( 0.0 ) );
		assertTrue( range.containsPoint( Delta ) );
		assertFalse( range.containsPoint( -Delta ) );
		assertTrue( range.containsPoint( 0.5 ) );
		assertTrue( range.containsPoint( 1.0 - Delta ) );
		assertTrue( range.containsPoint( 1.0 ) );
		assertFalse( range.containsPoint( 1.0 + Delta ) );
		assertTrue( range.containsPoint( -8.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( -7.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -6.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -5.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -3.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -2.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -1.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 0.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 1.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 2.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 3.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 5.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 6.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 7.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 8.0 / 4.0 * Math.PI ) );

		range = CircleRange.newByShortSegment( -1.0, -2.0 );
		assertTrue( range.containsPoint( -1.0 ) );
		assertTrue( range.containsPoint( -1.0 - Delta ) );
		assertFalse( range.containsPoint( -1.0 + Delta ) );
		assertTrue( range.containsPoint( -1.5 ) );
		assertTrue( range.containsPoint( -2.0 ) );
		assertFalse( range.containsPoint( -2.0 - Delta ) );
		assertTrue( range.containsPoint( -2.0 + Delta ) );
		assertFalse( range.containsPoint( -8.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -7.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -6.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -5.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -3.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( -2.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -1.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 0.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 1.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 2.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 3.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 5.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 6.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 7.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 8.0 / 4.0 * Math.PI ) );

		range = CircleRange.newByShortSegment( 2.0, 3.5 );
		assertTrue( range.containsPoint( 2.0 ) );
		assertTrue( range.containsPoint( 2.0 + Delta ) );
		assertFalse( range.containsPoint( 2.0 - Delta ) );
		assertTrue( range.containsPoint( 2.75 ) );
		assertTrue( range.containsPoint( 3.5 ) );
		assertTrue( range.containsPoint( 3.5 - Delta ) );
		assertFalse( range.containsPoint( 3.5 + Delta ) );
		assertFalse( range.containsPoint( -8.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -7.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -6.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( -5.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( -4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -3.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -2.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( -1.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 0.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 1.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 2.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 3.0 / 4.0 * Math.PI ) );
		assertTrue( range.containsPoint( 4.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 5.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 6.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 7.0 / 4.0 * Math.PI ) );
		assertFalse( range.containsPoint( 8.0 / 4.0 * Math.PI ) );
	}
	
	@Test
	public void testContainsPointWithRangesWithEpsilon( )
	{
		CircleRange range = CircleRange.newByCounterclockwiseSegment( 0.0, Math.PI );
		assertFalse( range.containsPoint( 0.0 - Delta ) );
		assertTrue( range.containsPoint( 0.0 - Epsilon ) );
		assertTrue( range.containsPoint( 0.0 ) );
		assertTrue( range.containsPoint( 0.0 + Epsilon ) );
		assertTrue( range.containsPoint( 0.0 + Delta ) );
		assertTrue( range.containsPoint( Math.PI - Delta ) );
		assertTrue( range.containsPoint( Math.PI - Epsilon ) );
		assertTrue( range.containsPoint( Math.PI ) );
		assertTrue( range.containsPoint( Math.PI + Epsilon ) );
		assertFalse( range.containsPoint( Math.PI + Delta ) );
		
		assertFalse( range.containsPoint( 2.0*Math.PI + 0.0 - Delta ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + 0.0 - Epsilon ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + 0.0 ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + 0.0 + Epsilon ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + 0.0 + Delta ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + Math.PI - Delta ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + Math.PI - Epsilon ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + Math.PI ) );
		assertTrue( range.containsPoint( 2.0*Math.PI + Math.PI + Epsilon ) );
		assertFalse( range.containsPoint( 2.0*Math.PI + Math.PI + Delta ) );
		
		assertFalse( range.containsPoint( -2.0*Math.PI + 0.0 - Delta ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + 0.0 - Epsilon ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + 0.0 ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + 0.0 + Epsilon ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + 0.0 + Delta ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + Math.PI - Delta ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + Math.PI - Epsilon ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + Math.PI ) );
		assertTrue( range.containsPoint( -2.0*Math.PI + Math.PI + Epsilon ) );
		assertFalse( range.containsPoint( -2.0*Math.PI + Math.PI + Delta ) );
	}
	
	@Test
	public void testContainsPointOnBoundaryWithPoints( )
	{
		final double Delta = 1e-4;
		assert( Delta > CompareReal.getEpsilon() );
		
		CircleRange range = CircleRange.newByPoint( 0.0 );
		assertTrue( range.containsPointOnBoundary( 0.0 ) );
		assertFalse( range.containsPointOnBoundary( Delta ) );
		assertFalse( range.containsPointOnBoundary( -Delta ) );
		assertFalse( range.containsPointOnBoundary( Math.PI ) );
		assertTrue( range.containsPointOnBoundary( 2.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( 2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( 2.0 * Math.PI - Delta ) );
		assertTrue( range.containsPointOnBoundary( -2.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( -2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( -2.0 * Math.PI - Delta ) );
		
		range = CircleRange.newByPoint( Math.PI );
		assertTrue( range.containsPointOnBoundary( Math.PI ) );
		assertFalse( range.containsPointOnBoundary( Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( Math.PI - Delta ) );
		assertFalse( range.containsPointOnBoundary( 0.0 ) );
		assertTrue( range.containsPointOnBoundary( 3.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( 3.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( 3.0 * Math.PI - Delta ) );
		assertTrue( range.containsPointOnBoundary( -3.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( -3.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( -3.0 * Math.PI - Delta ) );
	}
	
	@Test
	public void testContainsPointOnBoundaryWithCircles( )
	{
		CircleRange range = CircleRange.newCircle();
		assertFalse( range.containsPointOnBoundary( 0.0 ) );
		assertFalse( range.containsPointOnBoundary( Delta ) );
		assertFalse( range.containsPointOnBoundary( -Delta ) );
		assertFalse( range.containsPointOnBoundary( Math.PI ) );
		assertFalse( range.containsPointOnBoundary( 2.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( 2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( 2.0 * Math.PI - Delta ) );
		assertFalse( range.containsPointOnBoundary( -2.0 * Math.PI ) );
		assertFalse( range.containsPointOnBoundary( -2.0 * Math.PI + Delta ) );
		assertFalse( range.containsPointOnBoundary( -2.0 * Math.PI - Delta ) );
	}
	
	@Test
	public void testContainsPointOnBoundaryWithRanges( )
	{
		CircleRange range = CircleRange.newByShortSegment( 1.0, 0.0 );
		assertTrue( range.containsPointOnBoundary( 0.0 ) );
		assertFalse( range.containsPointOnBoundary( Delta ) );
		assertFalse( range.containsPointOnBoundary( -Delta ) );
		assertFalse( range.containsPointOnBoundary( 0.5 ) );
		assertFalse( range.containsPointOnBoundary( 1.0 - Delta ) );
		assertTrue( range.containsPointOnBoundary( 1.0 ) );
		assertFalse( range.containsPointOnBoundary( 1.0 + Delta ) );

		range = CircleRange.newByShortSegment( -1.0, -2.0 );
		assertTrue( range.containsPointOnBoundary( -1.0 ) );
		assertFalse( range.containsPointOnBoundary( -1.0 - Delta ) );
		assertFalse( range.containsPointOnBoundary( -1.0 + Delta ) );
		assertFalse( range.containsPointOnBoundary( -1.5 ) );
		assertTrue( range.containsPointOnBoundary( -2.0 ) );
		assertFalse( range.containsPointOnBoundary( -2.0 - Delta ) );
		assertFalse( range.containsPointOnBoundary( -2.0 + Delta ) );

		range = CircleRange.newByShortSegment( 2.0, 3.5 );
		assertTrue( range.containsPointOnBoundary( 2.0 ) );
		assertFalse( range.containsPointOnBoundary( 2.0 + Delta ) );
		assertFalse( range.containsPointOnBoundary( 2.0 - Delta ) );
		assertFalse( range.containsPointOnBoundary( 2.75 ) );
		assertTrue( range.containsPointOnBoundary( 3.5 ) );
		assertFalse( range.containsPointOnBoundary( 3.5 - Delta ) );
		assertFalse( range.containsPointOnBoundary( 3.5 + Delta ) );
	}
	
	@Test
	public void testContainsPointOnBoundaryWithRangesWithEpsilon( )
	{
		CircleRange range = CircleRange.newByCounterclockwiseSegment( 0.0, Math.PI );
		assertFalse( range.containsPointOnBoundary( 0.0 - Delta ) );
		assertTrue( range.containsPointOnBoundary( 0.0 - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( 0.0 ) );
		assertTrue( range.containsPointOnBoundary( 0.0 + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( 0.0 + Delta ) );
		assertFalse( range.containsPointOnBoundary( Math.PI - Delta ) );
		assertTrue( range.containsPointOnBoundary( Math.PI - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( Math.PI ) );
		assertTrue( range.containsPointOnBoundary( Math.PI + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( Math.PI + Delta ) );
		
		assertFalse( range.containsPointOnBoundary( 2.0*Math.PI + 0.0 - Delta ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + 0.0 - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + 0.0 ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + 0.0 + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( 2.0*Math.PI + 0.0 + Delta ) );
		assertFalse( range.containsPointOnBoundary( 2.0*Math.PI + Math.PI - Delta ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + Math.PI - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + Math.PI ) );
		assertTrue( range.containsPointOnBoundary( 2.0*Math.PI + Math.PI + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( 2.0*Math.PI + Math.PI + Delta ) );
		
		assertFalse( range.containsPointOnBoundary( -2.0*Math.PI + 0.0 - Delta ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + 0.0 - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + 0.0 ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + 0.0 + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( -2.0*Math.PI + 0.0 + Delta ) );
		assertFalse( range.containsPointOnBoundary( -2.0*Math.PI + Math.PI - Delta ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + Math.PI - Epsilon ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + Math.PI ) );
		assertTrue( range.containsPointOnBoundary( -2.0*Math.PI + Math.PI + Epsilon ) );
		assertFalse( range.containsPointOnBoundary( -2.0*Math.PI + Math.PI + Delta ) );
	}

	@Test
	public void testIsIntersectingCircle( )
	{
		// anything intersects a circle
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newCircle() ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByPoint( 0.0 ) ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByPoint( 1.0 ) ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByPoint( Math.PI ) ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByShortSegment( 1.0, 0.0 ) ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByShortSegment( -1.0, -2.0 ) ) );
		assertTrue( CircleRange.newCircle().isIntersecting( CircleRange.newByShortSegment( 2.0, 3.5 ) ) );
	}
	
	@Test
	public void testIsIntersectingPoints( )
	{
		// points only intersect when they're identical
		assertTrue( CircleRange.newByPoint( 0.0 ).isIntersecting( CircleRange.newByPoint( 0.0 ) ) );
		assertTrue( CircleRange.newByPoint( 1.0 ).isIntersecting( CircleRange.newByPoint( 1.0 ) ) );
		assertTrue( CircleRange.newByPoint( Math.PI ).isIntersecting( CircleRange.newByPoint( Math.PI ) ) );
		
		assertFalse( CircleRange.newByPoint( 0.0 ).isIntersecting( CircleRange.newByPoint( 1.0 ) ) );
		assertFalse( CircleRange.newByPoint( 1.0 ).isIntersecting( CircleRange.newByPoint( Math.PI ) ) );
		assertFalse( CircleRange.newByPoint( Math.PI ).isIntersecting( CircleRange.newByPoint( 0.0 ) ) );
	}
	
	@Test
	public void testIsIntersectingRanges( )
	{
		// this test is a little more interesting
		CircleRange range = CircleRange.newByShortSegment( 0.0, 1.0 );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 0.0, 1.0 ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -Delta, -1.0 ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 1.0 + Delta, 2.0 ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment(  -1.0, 2.0 ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( -8.0 / 4.0 * Math.PI, -7.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( -7.0 / 4.0 * Math.PI, -6.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -6.0 / 4.0 * Math.PI, -5.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -5.0 / 4.0 * Math.PI, -4.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -4.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -3.0 / 4.0 * Math.PI, -2.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -2.0 / 4.0 * Math.PI, -1.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( -1.0 / 4.0 * Math.PI, 0.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 0.0 / 4.0 * Math.PI, 1.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 1.0 / 4.0 * Math.PI, 2.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 2.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 3.0 / 4.0 * Math.PI, 4.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 4.0 / 4.0 * Math.PI, 5.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 5.0 / 4.0 * Math.PI, 6.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 6.0 / 4.0 * Math.PI, 7.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 7.0 / 4.0 * Math.PI, 8.0 / 4.0 * Math.PI ) ) );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 2.5, 3.5 ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 3.5 + Delta, 4.0 ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 1.0, 2.5 - Delta ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 2.0, 4.0 ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -8.0 / 4.0 * Math.PI, -7.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -7.0 / 4.0 * Math.PI, -6.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -6.0 / 4.0 * Math.PI, -5.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( -5.0 / 4.0 * Math.PI, -4.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( -4.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -3.0 / 4.0 * Math.PI, -2.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -2.0 / 4.0 * Math.PI, -1.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( -1.0 / 4.0 * Math.PI, 0.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 0.0 / 4.0 * Math.PI, 1.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 1.0 / 4.0 * Math.PI, 2.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 2.0 / 4.0 * Math.PI, 3.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 3.0 / 4.0 * Math.PI, 4.0 / 4.0 * Math.PI ) ) );
		assertTrue( range.isIntersecting( CircleRange.newByShortSegment( 4.0 / 4.0 * Math.PI, 5.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 5.0 / 4.0 * Math.PI, 6.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 6.0 / 4.0 * Math.PI, 7.0 / 4.0 * Math.PI ) ) );
		assertFalse( range.isIntersecting( CircleRange.newByShortSegment( 7.0 / 4.0 * Math.PI, 8.0 / 4.0 * Math.PI ) ) );
	}
	
	@Test
	public void testIsIntersectingOnlyOnBoundaryCircle( )
	{
		// a circle has no boundary
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newCircle() ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 0.0 ) ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 1.0 ) ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByPoint( Math.PI ) ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByShortSegment( 1.0, 0.0 ) ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByShortSegment( -1.0, -2.0 ) ) );
		assertFalse( CircleRange.newCircle().isIntersectingOnlyOnBoundary( CircleRange.newByShortSegment( 2.0, 3.5 ) ) );
	}
	
	@Test
	public void testIsIntersectingOnlyOnBoundaryPoints( )
	{
		// points only intersect when they're identical
		assertTrue( CircleRange.newByPoint( 0.0 ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 0.0 ) ) );
		assertTrue( CircleRange.newByPoint( 1.0 ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 1.0 ) ) );
		assertTrue( CircleRange.newByPoint( Math.PI ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( Math.PI ) ) );
		
		assertFalse( CircleRange.newByPoint( 0.0 ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 1.0 ) ) );
		assertFalse( CircleRange.newByPoint( 1.0 ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( Math.PI ) ) );
		assertFalse( CircleRange.newByPoint( Math.PI ).isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 0.0 ) ) );
	}
	
	@Test
	public void testIsIntersectingOnlyOnBoundaryPointsAndRanges( )
	{
		CircleRange range = CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 0.0 ) ) );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByPoint( -0.5 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 0.5 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByPoint( 1.5 ) ) );
		
		range = CircleRange.newByPoint( 0.0 );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -1.0, 0.0 ) ) );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -1.0, 1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -1.0, -0.5 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 0.5, 1.0 ) ) );
	}
	
	@Test
	public void testIsIntersectingOnlyOnBoundaryRanges( )
	{
		CircleRange range = CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -1.0, 0.0 ) ) );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -0.5, 0.0 ) ) );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 1.0, 1.5 ) ) );
		assertTrue( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 1.0, 2.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 0.0, 0.5 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 0.5, 1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -0.5, 0.5 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( -Delta, -1.0 ) ) );
		assertFalse( range.isIntersectingOnlyOnBoundary( CircleRange.newByCounterclockwiseSegment( 1.0 + Delta, 2.0 ) ) );
	}
	
	@Test
	public void testMergePoints( )
	{
		CircleRange range = CircleRange.newByPoint( 0.0 );
		range.merge( CircleRange.newByPoint( 0.0 ) );
		assertEqualsRange( 0.0, 0.0, range );
		
		range = CircleRange.newByPoint( Math.PI );
		range.merge( CircleRange.newByPoint( Math.PI ) );
		assertEqualsRange( Math.PI, Math.PI, range );
	}
	
	@Test
	public void testMergeWithCircles( )
	{
		// we should always get a circle back
		CircleRange range = CircleRange.newCircle();
		range.merge( CircleRange.newByPoint( 0.0 ) );
		assertTrue( range.isCircle() );
		
		range = CircleRange.newCircle();
		range.merge( CircleRange.newByShortSegment( 0.0, 1.0 ) );
		assertTrue( range.isCircle() );
		
		range = CircleRange.newCircle();
		range.merge( CircleRange.newByPoint( 1.0 ) );
		assertTrue( range.isCircle() );
		
		range = CircleRange.newByPoint( 1.0 );
		range.merge( CircleRange.newCircle() );
		assertTrue( range.isCircle() );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newCircle() );
		assertTrue( range.isCircle() );
	}
	
	@Test
	public void testMergeWithRanges( )
	{
		// this test is a little more interesting
		CircleRange range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 0.0, 1.0 ) );
		assertEqualsRange( 0.0, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 1.0, 3.0 ) );
		assertEqualsRange( 0.0, 3.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( -2.0, 0.0 ) );
		assertEqualsRange( -2.0, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 0.9, 1.5 ) );
		assertEqualsRange( 0.0, 1.5, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( -0.5, 0.1 ) );
		assertEqualsRange( -0.5, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 0.1, 0.9 ) );
		assertEqualsRange( 0.0, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 0.0, 0.9 ) );
		assertEqualsRange( 0.0, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( 0.1, 1.0 ) );
		assertEqualsRange( 0.0, 1.0, range );
		
		range = CircleRange.newByShortSegment( 0.0, 1.0 );
		range.merge( CircleRange.newByShortSegment( -1.0, 2.0 ) );
		assertEqualsRange( -1.0, 2.0, range );
		
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 2.5, 3.5 ) );
		assertEqualsRange( 2.5, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 3.5, 4.0 ) );
		assertEqualsRange( 2.5, 4.0 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 1.0, 2.5 ) );
		assertEqualsRange( 1.0, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 3.4, 5.0 ) );
		assertEqualsRange( 2.5, 5.0 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 1.0, 2.6 ) );
		assertEqualsRange( 1.0, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 2.6, 3.4 ) );
		assertEqualsRange( 2.5, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 2.5, 3.4 ) );
		assertEqualsRange( 2.5, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 2.6, 3.5 ) );
		assertEqualsRange( 2.5, 3.5 - Math.PI * 2.0, range );
		
		range = CircleRange.newByShortSegment( 2.5, 3.5 );
		range.merge( CircleRange.newByShortSegment( 2.0, 4.0 ) );
		assertEqualsRange( 2.0, 4.0 - Math.PI * 2.0, range );
	}
	
	@Test
	public void testMergeSpecialCases( )
	{
		// tests for a roundoff error problem
		CircleRange a = CircleRange.newByOffset( 3.109297220211845, 0.05083403238695672 );
		CircleRange b = CircleRange.newByOffset( -3.1230540545807846, 0.002074922536448476 );
		a.merge( b );
		assertFalse( a.isCircle() );
		assertEqualsRange( 3.109297220211845, -3.120979132, a );
	}
	
	@Test
	public void testSplit( )
	{
		CircleRange range = CircleRange.newByCounterclockwiseSegment( 0.0, 1.0 );
		List<CircleRange> ranges = range.split( 0.5 );
		assertEqualsRange( 0.0, 0.5, ranges.get( 0 ) );
		assertEqualsRange( 0.5, 1.0, ranges.get( 1 ) );
		
		range = CircleRange.newByCounterclockwiseSegment( 3.0 / 4.0 * Math.PI, -3.0 / 4.0 * Math.PI );
		ranges = range.split( Math.PI );
		assertEqualsRange( 3.0 / 4.0 * Math.PI, Math.PI, ranges.get( 0 ) );
		assertEqualsRange( Math.PI, -3.0 / 4.0 * Math.PI, ranges.get( 1 ) );
	}
	
	@Test
	public void testApproximatelyEqualsPoints( )
	{
		CircleRange point = CircleRange.newByPoint( 0.0 );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( 0.0 - Delta ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( 0.0 - Epsilon ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( 0.0 ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( 0.0 + Epsilon ) ) );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( 0.0 + Delta ) ) );
		
		point = CircleRange.newByPoint( Math.PI );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( Math.PI - Delta ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( Math.PI - Epsilon ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( Math.PI ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( Math.PI + Epsilon ) ) );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( Math.PI + Delta ) ) );
		
		point = CircleRange.newByPoint( -Math.PI );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( -Math.PI - Delta ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( -Math.PI - Epsilon ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( -Math.PI ) ) );
		assertTrue( point.approximatelyEquals( CircleRange.newByPoint( -Math.PI + Epsilon ) ) );
		assertFalse( point.approximatelyEquals( CircleRange.newByPoint( -Math.PI + Delta ) ) );
	}
	
	private void assertEqualsRange( double expectedSource, double expectedTarget, CircleRange range )
	{
		assertEquals( expectedSource, range.getSource(), 1e-8 );
		assertEquals( expectedTarget, range.getTarget(), 1e-8 );
	}
	
	private void assertEqualsRange( double expectedSource, double expectedTarget, double expectedLength, CircleRange range )
	{
		assertEquals( expectedSource, range.getSource(), 1e-8 );
		assertEquals( expectedTarget, range.getTarget(), 1e-8 );
		assertEquals( expectedLength, range.getLength(), 1e-8 );
	}
}
