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

import edu.duke.cs.libprotnmr.ExtendedTestCase;

public class TestPointIterator extends ExtendedTestCase
{
	public void testPointIterator( )
	{
		int xLast = 7;
		int yLast = 7;
		int zLast = 7;
		double xMin = -10.0;
		double yMin = -20.0;
		double zMin = -40.0;
		double xMax = 0.0;
		double yMax = 5.0;
		double zMax = 15.0;
		
		PointIterator iter = new PointIterator(
			3,
			new int[] { xLast + 1, yLast + 1, zLast + 1 },
			new double[] { xMin, yMin, zMin },
			new double[] { xMax, yMax, zMax }
		);
		
		for( int x=0; x<=xLast; x++ )
		{
			for( int y=0; y<=yLast; y++ )
			{
				for( int z=0; z<=zLast; z++ )
				{
					assertTrue( iter.hasNext() );
					double[] vals = iter.next();
					
					double xval = (double)x / (double)xLast * ( xMax - xMin ) + xMin;
					double yval = (double)y / (double)yLast * ( yMax - yMin ) + yMin;
					double zval = (double)z / (double)zLast * ( zMax - zMin ) + zMin;
					
					assertEquals( xval, vals[0] ); 
					assertEquals( yval, vals[1] ); 
					assertEquals( zval, vals[2] ); 
				}
			}
		}
		
		assertFalse( iter.hasNext() );
	}
	
	public void testCounters( )
	{
		PointIterator iter = new PointIterator(
			3,
			new int[] { 3, 4, 5 },
			new double[] { 0.0, 0.0, 0.0 },
			new double[] { 1.0, 1.0, 1.0 }
		);
		
		assertEquals( 60, iter.getNumTotal() );
		assertEquals( 0, iter.getNumServed() );
		
		int counter = 0;
		while( iter.hasNext() )
		{
			iter.next();
			assertEquals( ++counter, iter.getNumServed() );
		}
		
		assertEquals( iter.getNumTotal(), iter.getNumServed() );
	}
}
