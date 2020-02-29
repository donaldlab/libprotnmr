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
package edu.duke.cs.libprotnmr.clustering;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.math.MultiVectorImpl;

public abstract class ClusteringTestCase extends ExtendedTestCase
{
	protected MultiVector newPoint( double x, double y )
	{
		return new MultiVectorImpl( new double[] { x, y } );
	}
	
	protected Cluster newCluster( double x, double y )
	{
		return new Cluster( newPoint( x, y ) );
	}
	
	protected void assertEqualsInterior( double expectedMedian, int expectedAxis, KDTreeNode observed )
	{
		assertFalse( observed.isLeaf() );
		assertEquals( expectedMedian, observed.getInteriorNode().getMedian() );
		assertEquals( expectedAxis, observed.getInteriorNode().getAxis() );
	}
	
	protected void assertEqualsLeaf( double expectedX, double expectedY, KDTreeNode observed )
	{
		assertTrue( observed.isLeaf() );
		MultiVector point = observed.getLeafNode().getCluster().getCenter();
		assertEquals( expectedX, point.get( 0 ) );
		assertEquals( expectedY, point.get( 1 ) );
	}
}
