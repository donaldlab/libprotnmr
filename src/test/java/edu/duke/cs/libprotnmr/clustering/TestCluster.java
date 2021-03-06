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

public class TestCluster extends ClusteringTestCase
{
	public void testConstructor( )
	{
		// get a cluster
		Cluster cluster = new Cluster( newPoint( 1, 2 ) );
		
		assertEquals( 1, cluster.getPoints().size() );
		assertEquals( newPoint( 1, 2 ), cluster.getPoints().get( 0 ) );
		assertEquals( newPoint( 1, 2 ), cluster.getCenter() );
	}
	
	public void testJoinOne( )
	{
		// get same one-clusters
		Cluster cluster = new Cluster( newPoint( 1, 2 ) );
		
		// add one cluster
		cluster.join( new Cluster( newPoint( 2, 3 ) ) );
		
		assertEquals( 2, cluster.getPoints().size() );
		assertEquals( newPoint( 1, 2 ), cluster.getPoints().get( 0 ) );
		assertEquals( newPoint( 2, 3 ), cluster.getPoints().get( 1 ) );
		assertEquals( newPoint( 1.5, 2.5 ), cluster.getCenter() );
		
		// add another cluster
		cluster.join( new Cluster( newPoint( 6, 7 ) ) );
		
		assertEquals( 3, cluster.getPoints().size() );
		assertEquals( newPoint( 1, 2 ), cluster.getPoints().get( 0 ) );
		assertEquals( newPoint( 2, 3 ), cluster.getPoints().get( 1 ) );
		assertEquals( newPoint( 6, 7 ), cluster.getPoints().get( 2 ) );
		assertEquals( newPoint( 3, 4 ), cluster.getCenter() );
		
		// add another cluster just for fun
		cluster.join( new Cluster( newPoint( 7, 4 ) ) );
		
		assertEquals( 4, cluster.getPoints().size() );
		assertEquals( newPoint( 1, 2 ), cluster.getPoints().get( 0 ) );
		assertEquals( newPoint( 2, 3 ), cluster.getPoints().get( 1 ) );
		assertEquals( newPoint( 6, 7 ), cluster.getPoints().get( 2 ) );
		assertEquals( newPoint( 7, 4 ), cluster.getPoints().get( 3 ) );
		assertEquals( newPoint( 4, 4 ), cluster.getCenter() );
	}
	
	public void testJoinMany( )
	{
		// get one cluster
		Cluster a = new Cluster( newPoint( 1, 2 ) );
		a.join( new Cluster( newPoint( 2, 3 ) ) );
		a.join( new Cluster( newPoint( 6, 7 ) ) );
		a.join( new Cluster( newPoint( 7, 4 ) ) );
		
		// get another cluster
		Cluster b = new Cluster( newPoint( 9, 3 ) );
		b.join( new Cluster( newPoint( 4, 5 ) ) );
		b.join( new Cluster( newPoint( 5, 1 ) ) );
		b.join( new Cluster( newPoint( 0, 8 ) ) );
		
		// join the two clusters
		a.join( b );

		assertEquals( 8, a.getPoints().size() );
		assertEquals( newPoint( 1, 2 ), a.getPoints().get( 0 ) );
		assertEquals( newPoint( 2, 3 ), a.getPoints().get( 1 ) );
		assertEquals( newPoint( 6, 7 ), a.getPoints().get( 2 ) );
		assertEquals( newPoint( 7, 4 ), a.getPoints().get( 3 ) );
		assertEquals( newPoint( 9, 3 ), a.getPoints().get( 4 ) );
		assertEquals( newPoint( 4, 5 ), a.getPoints().get( 5 ) );
		assertEquals( newPoint( 5, 1 ), a.getPoints().get( 6 ) );
		assertEquals( newPoint( 0, 8 ), a.getPoints().get( 7 ) );
		assertEquals( newPoint( 4.25, 4.125 ), a.getCenter() );
	}
}
