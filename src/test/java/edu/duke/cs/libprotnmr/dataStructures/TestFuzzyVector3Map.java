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

package edu.duke.cs.libprotnmr.dataStructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.duke.cs.libprotnmr.geom.Vector3;

import org.junit.Test;


public class TestFuzzyVector3Map
{
	private static final double Delta = 1e-6;
	private static final double Epsilon = 1e-12;
	
	@Test
	public void testPutGetNoCollions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		
		map.put( a, 1 );
		map.put( b, 2 );
		
		assertFalse( map.isEmpty() );
		assertEquals( 2, map.size() );
		assertTrue( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		assertEquals( 1, map.get( a ).intValue() );
		assertEquals( 2, map.get( b ).intValue() );
	}
	
	@Test
	public void testRemoveNoCollisions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		
		map.put( a, 1 );
		map.put( b, 2 );
		
		map.remove( a );
		
		assertFalse( map.isEmpty() );
		assertEquals( 1, map.size() );
		assertFalse( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		
		map.remove( b );
		
		assertTrue( map.isEmpty() );
		assertFalse( map.containsKey( a ) );
		assertFalse( map.containsKey( b ) );
	}
	
	@Test
	public void testPutWithCollions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0, 2.0, 3.0 );
		
		map.put( a, 1 );
		map.put( b, 2 );
		map.put( c, 3 );
		
		assertFalse( map.isEmpty() );
		assertEquals( 2, map.size() );
		assertTrue( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		assertTrue( map.containsKey( c ) );
		assertEquals( 3, map.get( a ).intValue() );
		assertEquals( 2, map.get( b ).intValue() );
		assertEquals( 3, map.get( c ).intValue() );
	}
	
	@Test
	public void testRemoveWithCollions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0, 2.0, 3.0 );
		
		map.put( a, 1 );
		map.put( b, 2 );
		map.put( c, 3 );
		
		map.remove( a );
		
		assertFalse( map.isEmpty() );
		assertEquals( 1, map.size() );
		assertFalse( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		assertFalse( map.containsKey( c ) );
		assertEquals( 2, map.get( b ).intValue() );
	}
	
	@Test
	public void testPutWithEpsilonCollions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0 + Epsilon, 2.0 + Epsilon, 3.0 + Epsilon );
		
		map.put( a, 1 );
		map.put( b, 2 );
		map.put( c, 3 );
		
		assertFalse( map.isEmpty() );
		assertEquals( 2, map.size() );
		assertTrue( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		assertTrue( map.containsKey( c ) );
		assertEquals( 3, map.get( a ).intValue() );
		assertEquals( 2, map.get( b ).intValue() );
		assertEquals( 3, map.get( c ).intValue() );
	}
	
	@Test
	public void testRemoveWithEpsilonCollions( )
	{
		FuzzyMap<Vector3,Integer> map = new FuzzyMap<Vector3,Integer>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0 + Epsilon, 2.0 + Epsilon, 3.0 + Epsilon );
		
		map.put( a, 1 );
		map.put( b, 2 );
		map.put( c, 3 );
		
		map.remove( a );
		
		assertFalse( map.isEmpty() );
		assertEquals( 1, map.size() );
		assertFalse( map.containsKey( a ) );
		assertTrue( map.containsKey( b ) );
		assertFalse( map.containsKey( c ) );
		assertEquals( 2, map.get( b ).intValue() );
	}
}
