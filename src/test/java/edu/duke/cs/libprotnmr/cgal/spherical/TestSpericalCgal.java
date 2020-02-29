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

package edu.duke.cs.libprotnmr.cgal.spherical;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;

public class TestSpericalCgal extends ExtendedTestCase
{
	// UNDONE: break into separate tests -- one for each class
	// use this class to test SpericalCgal methods!
	public void testCircle( )
	{
		Circle3 circle = new Circle3(
			new Vector3( 3.0, 1.0, 7.0 ),
			new Vector3( 1.0, 3.0, 7.0 ),
			new Vector3( -1.0, 1.0, 7.0 )
		);
		
		assertEquals( new Vector3( 1.0, 1.0, 7.0 ), circle.getCenter() );
		assertEquals( 4.0, circle.getSquaredRadius() );
	}
	
	public void testCircularArc( )
	{
		CircularArc3 arc = new CircularArc3(
			new Circle3(
				new Vector3( 3.0, 1.0, 7.0 ),
				new Vector3( 1.0, 3.0, 7.0 ),
				new Vector3( -1.0, 1.0, 7.0 )
			),
			new Vector3( 3.0, 1.0, 7.0 ),
			new Vector3( 1.0, 3.0, 7.0 )
		);
		
		assertEquals( new Vector3( 3.0, 1.0, 7.0 ), arc.getSource() );
		assertEquals( new Vector3( 1.0, 3.0, 7.0 ), arc.getTarget() );
		assertEquals( new Vector3( 1.0, 1.0, 7.0 ), arc.getSupportingCircle().getCenter() );
		assertEquals( 4.0, arc.getSupportingCircle().getSquaredRadius() );
	}
}
