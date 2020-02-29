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
