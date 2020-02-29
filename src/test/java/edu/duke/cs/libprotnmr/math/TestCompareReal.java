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

public class TestCompareReal extends ExtendedTestCase
{
	private static final double Epsilon = 0.00001;
	
	public void testEq( )
	{
		CompareReal.setEpsilon( Epsilon );
		
		assertTrue( CompareReal.eq( 1.0, 1.0 ) );
		assertTrue( CompareReal.eq( 0.1, 0.1 ) );
		assertTrue( CompareReal.eq( 0.01, 0.01 ) );
		assertTrue( CompareReal.eq( 0.001, 0.001 ) );
		assertTrue( CompareReal.eq( 0.0001, 0.0001 ) );
		assertTrue( CompareReal.eq( 0.00001, 0.00001 ) );
		assertTrue( CompareReal.eq( 0.000001, 0.000001 ) );
		assertTrue( CompareReal.eq( 0.0000001, 0.0000001 ) );
		assertTrue( CompareReal.eq( 0.000001, 0.000002 ) );
		assertFalse( CompareReal.eq( 0.00001, 0.00002 ) );
		assertFalse( CompareReal.eq( 0.0001, 0.0002 ) );
		assertFalse( CompareReal.eq( 0.001, 0.002 ) );
		assertFalse( CompareReal.eq( 0.01, 0.02 ) );
		assertFalse( CompareReal.eq( 0.1, 0.2 ) );
		assertFalse( CompareReal.eq( 1.0, 2.0 ) );
	}
	
	public void testNeq( )
	{
		CompareReal.setEpsilon( Epsilon );
		
		assertTrue( CompareReal.neq( 1.0, 2.0 ) );
		assertTrue( CompareReal.neq( 0.01, 0.02 ) );
		assertTrue( CompareReal.neq( 0.001, 0.002 ) );
		assertTrue( CompareReal.neq( 0.0001, 0.0002 ) );
		assertTrue( CompareReal.neq( 0.00001, 0.00003 ) );
		assertFalse( CompareReal.neq( 0.000001, 0.000002 ) );
		assertFalse( CompareReal.neq( 0.0, 0.0 ) );
		assertFalse( CompareReal.neq( 1.0, 1.0 ) );
		assertFalse( CompareReal.neq( 0.1, 0.1 ) );
		assertFalse( CompareReal.neq( 0.01, 0.01 ) );
		assertFalse( CompareReal.neq( 0.001, 0.001 ) );
		assertFalse( CompareReal.neq( 0.0001, 0.0001 ) );
		assertFalse( CompareReal.neq( 0.00001, 0.00001 ) );
	}
	
	public void testLte( )
	{
		CompareReal.setEpsilon( Epsilon );
		
		assertTrue( CompareReal.lte( 1.0, 2.0 ) );
		assertFalse( CompareReal.lte( 2.0, 1.0 ) );
		assertTrue( CompareReal.lte( 0.1, 0.2 ) );
		assertTrue( CompareReal.lte( 0.01, 0.02 ) );
		assertTrue( CompareReal.lte( 0.001, 0.002 ) );
		assertTrue( CompareReal.lte( 0.0001, 0.0002 ) );
		assertTrue( CompareReal.lte( 0.00001, 0.00002 ) );
		assertTrue( CompareReal.lte( 1.00000, 1.00000 ) );
		assertTrue( CompareReal.lte( 1.000001, 1.00000 ) );
		assertFalse( CompareReal.lte( 1.00001, 1.00000 ) );
		assertFalse( CompareReal.lte( 1.0001, 1.0000 ) );
	}
	
	public void testGte( )
	{
		CompareReal.setEpsilon( Epsilon );
		
		assertTrue( CompareReal.gte( 2.0, 1.0 ) );
		assertFalse( CompareReal.gte( 1.0, 2.0 ) );
		assertTrue( CompareReal.gte( 0.2, 0.1 ) );
		assertTrue( CompareReal.gte( 0.02, 0.01 ) );
		assertTrue( CompareReal.gte( 0.002, 0.001 ) );
		assertTrue( CompareReal.gte( 0.0002, 0.0001 ) );
		assertTrue( CompareReal.gte( 0.00002, 0.00001 ) );
		assertTrue( CompareReal.gte( 1.00000, 1.00000 ) );
		assertTrue( CompareReal.gte( 1.00000, 1.000001 ) );
		assertFalse( CompareReal.gte( 1.00000, 1.00001 ) );
		assertFalse( CompareReal.gte( 1.0000, 1.0001 ) );
	}
}
