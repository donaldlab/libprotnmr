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

import static org.junit.Assert.*;

import edu.duke.cs.libprotnmr.geom.Vector3;

import org.junit.Test;


public class TestFuzzyVector3Set
{
	private static final double Delta = 1e-6;
	private static final double Epsilon = 1e-12;
	
	@Test
	public void testAddNoCollions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		
		set.add( a );
		set.add( b );
		
		assertFalse( set.isEmpty() );
		assertEquals( 2, set.size() );
		assertTrue( set.contains( a ) );
		assertTrue( set.contains( b ) );
	}
	
	@Test
	public void testRemoveNoCollisions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		
		set.add( a );
		set.add( b );
		
		set.remove( a );
		
		assertFalse( set.isEmpty() );
		assertEquals( 1, set.size() );
		assertFalse( set.contains( a ) );
		assertTrue( set.contains( b ) );
		
		set.remove( b );
		
		assertTrue( set.isEmpty() );
		assertFalse( set.contains( a ) );
		assertFalse( set.contains( b ) );
	}
	
	@Test
	public void testAddWithCollions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0, 2.0, 3.0 );
		
		set.add( a );
		set.add( b );
		set.add( c );
		
		assertFalse( set.isEmpty() );
		assertEquals( 2, set.size() );
		assertTrue( set.contains( a ) );
		assertTrue( set.contains( b ) );
		assertTrue( set.contains( c ) );
	}
	
	@Test
	public void testRemoveWithCollions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0, 2.0, 3.0 );
		
		set.add( a );
		set.add( b );
		set.add( c );
		
		set.remove( a );
		
		assertFalse( set.isEmpty() );
		assertEquals( 1, set.size() );
		assertFalse( set.contains( a ) );
		assertTrue( set.contains( b ) );
		assertFalse( set.contains( c ) );
	}
	
	@Test
	public void testAddWithEpsilonCollions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0 + Epsilon, 2.0 + Epsilon, 3.0 + Epsilon );
		
		set.add( a );
		set.add( b );
		set.add( c );
		
		assertFalse( set.isEmpty() );
		assertEquals( 2, set.size() );
		assertTrue( set.contains( a ) );
		assertTrue( set.contains( b ) );
		assertTrue( set.contains( c ) );
	}
	
	@Test
	public void testRemoveWithEpsilonCollions( )
	{
		FuzzySet<Vector3> set = new FuzzySet<Vector3>( Delta );
		
		Vector3 a = new Vector3( 1.0, 2.0, 3.0 );
		Vector3 b = new Vector3( 2.0, 3.0, 4.0 );
		Vector3 c = new Vector3( 1.0 + Epsilon, 2.0 + Epsilon, 3.0 + Epsilon );
		
		set.add( a );
		set.add( b );
		set.add( c );
		
		set.remove( a );
		
		assertFalse( set.isEmpty() );
		assertEquals( 1, set.size() );
		assertFalse( set.contains( a ) );
		assertTrue( set.contains( b ) );
		assertFalse( set.contains( c ) );
	}
}
