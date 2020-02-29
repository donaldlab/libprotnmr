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

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.clustering.stopCondition.StopConditionDistance;
import edu.duke.cs.libprotnmr.math.MultiVector;


public class TestClusterer extends ClusteringTestCase
{
	public void testClustererEverything( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 1, 2 ) );
		points.add( newPoint( 2, 3 ) );
		points.add( newPoint( 3, 4 ) );
		points.add( newPoint( 4, 5 ) );
		points.add( newPoint( 5, 6 ) );
		points.add( newPoint( 6, 7 ) );
		
		Clusterer clusterer = new Clusterer();
		ArrayList<Cluster> clusters = clusterer.cluster( points );
		
		assertEquals( 1, clusters.size() );
		assertEquals( 6, clusters.get( 0 ).getPoints().size() );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 0 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 1 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 2 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 3 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 4 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 5 ) ) );
	}
	
	public void testClustererTwoClusters( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 1, 2 ) );
		points.add( newPoint( 2, 3 ) );
		points.add( newPoint( 3, 1 ) );
		points.add( newPoint( 7, 8 ) );
		points.add( newPoint( 8, 9 ) );
		points.add( newPoint( 9, 7 ) );
		
		Clusterer clusterer = new Clusterer();
		clusterer.setStopCondition( new StopConditionDistance( 3 ) );
		ArrayList<Cluster> clusters = clusterer.cluster( points );
		
		assertEquals( 2, clusters.size() );
		assertEquals( 3, clusters.get( 0 ).getPoints().size() );	
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 0 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 1 ) ) );
		assertTrue( clusters.get( 0 ).getPoints().contains( points.get( 2 ) ) );
		assertEquals( 3, clusters.get( 1 ).getPoints().size() );	
		assertTrue( clusters.get( 1 ).getPoints().contains( points.get( 3 ) ) );
		assertTrue( clusters.get( 1 ).getPoints().contains( points.get( 4 ) ) );
		assertTrue( clusters.get( 1 ).getPoints().contains( points.get( 5 ) ) );
	}
}
