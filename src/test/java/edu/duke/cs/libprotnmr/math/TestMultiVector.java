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

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;


public class TestMultiVector extends ExtendedTestCase
{
	public void testDimensionConstructor( )
	{
		MultiVector v = new MultiVectorImpl( 1 );
		assertEquals( 1, v.getDimension() );
		assertEquals( 0.0, v.get( 0 ) );
		
		v = new MultiVectorImpl( 6 );
		assertEquals( 6, v.getDimension() );
		assertEquals( 0.0, v.get( 0 ) );
		assertEquals( 0.0, v.get( 1 ) );
		assertEquals( 0.0, v.get( 2 ) );
		assertEquals( 0.0, v.get( 3 ) );
		assertEquals( 0.0, v.get( 4 ) );
		assertEquals( 0.0, v.get( 5 ) );
	}
	
	public void testArrayConstructor( )
	{
		MultiVector v = new MultiVectorImpl( new double[] { 5.0 } );
		assertEquals( 1, v.getDimension() );
		assertEquals( 5.0, v.get( 0 ) );
		
		v = new MultiVectorImpl( new double[] { 1.0, 2.0, 3.0 } );
		assertEquals( 3, v.getDimension() );
		assertEquals( 1.0, v.get( 0 ) );
		assertEquals( 2.0, v.get( 1 ) );
		assertEquals( 3.0, v.get( 2 ) );
	}
	
	public void testCollectionConstructor( )
	{
		ArrayList<Double> data = new ArrayList<Double>( 1 );
		data.add( 5.0 );
		
		MultiVector v = new MultiVectorImpl( data );
		assertEquals( 1, v.getDimension() );
		assertEquals( 5.0, v.get( 0 ) );
		
		data = new ArrayList<Double>( 3 );
		data.add( 1.0 );
		data.add( 2.0 );
		data.add( 3.0 );
		
		v = new MultiVectorImpl( data );
		assertEquals( 3, v.getDimension() );
		assertEquals( 1.0, v.get( 0 ) );
		assertEquals( 2.0, v.get( 1 ) );
		assertEquals( 3.0, v.get( 2 ) );
	}
	
	public void testAdd( )
	{
		MultiVector a = new MultiVectorImpl( new double[] { 2.0, 3.0 } );
		MultiVector b = new MultiVectorImpl( new double[] { 9.0, 7.0 } );
		
		a.add( b );
		
		assertEquals( 11.0, a.get( 0 ) );
		assertEquals( 10.0, a.get( 1 ) );
		assertEquals( 9.0, b.get( 0 ) );
		assertEquals( 7.0, b.get( 1 ) );
	}
	
	public void testSebtract( )
	{
		MultiVector a = new MultiVectorImpl( new double[] { 2.0, 3.0 } );
		MultiVector b = new MultiVectorImpl( new double[] { 11.0, 10.0 } );
		
		b.subtract( a );
		
		assertEquals( 2.0, a.get( 0 ) );
		assertEquals( 3.0, a.get( 1 ) );
		assertEquals( 9.0, b.get( 0 ) );
		assertEquals( 7.0, b.get( 1 ) );
	}
	
	public void testLengthSquared( )
	{
		MultiVector v = new MultiVectorImpl( new double[] { 5.0 } );
		assertEquals( 25.0, v.getLengthSquared() );
		
		v = new MultiVectorImpl( new double[] { 2.0, 3.0, 4.0 } );
		assertEquals( 29.0, v.getLengthSquared() );
	}
	
	public void testLength( )
	{
		MultiVector v = new MultiVectorImpl( new double[] { 5.0 } );
		assertEquals( 5.0, v.getLength() );
		
		v = new MultiVectorImpl( new double[] { 2.0, 3.0, 4.0 } );
		assertEquals( Math.sqrt( 29.0 ), v.getLength() );
	}
	
	public void testDistanceSquared( )
	{
		MultiVector a = new MultiVectorImpl( new double[] { 3.0 } );
		MultiVector b = new MultiVectorImpl( new double[] { 6.0 } );
		assertEquals( 9.0, a.getDistanceSquared( b ) );
		assertEquals( 9.0, b.getDistanceSquared( a ) );
		
		a = new MultiVectorImpl( new double[] { 3.0, 4.0, 5.0 } );
		b = new MultiVectorImpl( new double[] { 9.0, 8.0, 7.0 } );
		assertEquals( 56.0, a.getDistanceSquared( b ) );
		assertEquals( 56.0, b.getDistanceSquared( a ) );
	}
	
	public void testDistance( )
	{
		MultiVector a = new MultiVectorImpl( new double[] { 3.0 } );
		MultiVector b = new MultiVectorImpl( new double[] { 6.0 } );
		assertEquals( 3.0, a.getDistance( b ) );
		assertEquals( 3.0, b.getDistance( a ) );
		
		a = new MultiVectorImpl( new double[] { 3.0, 4.0, 5.0 } );
		b = new MultiVectorImpl( new double[] { 9.0, 8.0, 7.0 } );
		assertEquals( Math.sqrt( 56.0 ), a.getDistance( b ) );
		assertEquals( Math.sqrt( 56.0 ), b.getDistance( a ) );
	}
}
