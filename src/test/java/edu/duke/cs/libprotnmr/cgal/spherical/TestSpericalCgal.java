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
