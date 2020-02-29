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

package edu.duke.cs.libprotnmr.geom;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.math.CompareReal;

public class TestSphere extends ExtendedTestCase
{
	public void testDefaultConstructor( )
	{
		Sphere sphere = new Sphere();
		
		assertEquals( new Vector3( 0, 0, 0 ), sphere.center );
		assertEquals( 0.0, sphere.radius, CompareReal.getEpsilon() );
	}
	
	public void testConstructor( )
	{
		Sphere s = new Sphere( new Vector3( 1, 2, 3 ), 4 );
		
		assertEquals( new Vector3( 1, 2, 3 ), s.center );
		assertEquals( 4.0, s.radius, CompareReal.getEpsilon() );
	}
	
	public void testSolveConstructor( )
	{
		Vector3 a = new Vector3( 1, 0, 0 );
		Vector3 b = new Vector3( 0, 1, 0 );
		Vector3 c = new Vector3( 0, 0, 1 );
		Vector3 d = new Vector3( -1, 0, 0 );
		
		Sphere s = new Sphere( a, b, c, d );
		
		assertEquals( new Sphere( new Vector3( 0, 0, 0 ), 1.0 ), s );
	}
}
