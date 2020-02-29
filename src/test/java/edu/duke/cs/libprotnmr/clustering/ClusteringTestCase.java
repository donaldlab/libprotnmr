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
