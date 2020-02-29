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
import edu.duke.cs.libprotnmr.clustering.distance.DistanceCluster;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrix;

public class TestDistanceCluster extends ExtendedTestCase
{
	public void testJoin( )
	{
		DistanceMatrix distances = new DistanceMatrix( 2 );
		distances.set( 1, 0, 1.0 );
		
		DistanceCluster a = new DistanceCluster( 0, distances );
		DistanceCluster b = new DistanceCluster( 1, distances );
		
		assertEquals( 1, a.getPointIndices().size() );
		assertEquals( 0, (int)a.getPointIndices().get( 0 ) );
		assertEquals( 1, b.getPointIndices().size() );
		assertEquals( 1, (int)b.getPointIndices().get( 0 ) );
		
		a.join( b, distances );
		b = null;
		
		assertEquals( 2, a.getPointIndices().size() );
		assertEquals( 0, (int)a.getPointIndices().get( 0 ) );
		assertEquals( 1, (int)a.getPointIndices().get( 1 ) );
	}
	
	public void testGetRepresentativeIndex( )
	{
		// need distances for this one
		DistanceMatrix distances = new DistanceMatrix( 4 );
		//    a   b    c    d  (clearly, these distances don't obey the triangle inequality)
		// a  -  10    2    1  (awesomely, no one cares)
		// b  -   -    3    1
		// c  -   -    -    1
		// d  -   -    -    -
		distances.set( 1, 0, 10.0 );
		distances.set( 2, 0, 2.0 );
		distances.set( 2, 1, 3.0 );
		distances.set( 3, 0, 1.0 );
		distances.set( 3, 1, 1.0 );
		distances.set( 3, 2, 1.0 );
		
		DistanceCluster a = new DistanceCluster( 0, distances );
		DistanceCluster b = new DistanceCluster( 1, distances );
		DistanceCluster c = new DistanceCluster( 2, distances );
		DistanceCluster d = new DistanceCluster( 3, distances );
		
		assertEquals( 0, a.getIndex() );
		assertEquals( 0, a.getRepresentativeIndex() );
		assertEquals( 1, b.getIndex() );
		assertEquals( 1, b.getRepresentativeIndex() );
		assertEquals( 2, c.getIndex() );
		assertEquals( 2, c.getRepresentativeIndex() );
		assertEquals( 3, d.getIndex() );
		assertEquals( 3, d.getRepresentativeIndex() );
		
		// join everything and check the representatives
		a.join( b, distances );
		assertEquals( 0, a.getRepresentativeIndex() );
		a.join( c, distances );
		assertEquals( 2, a.getRepresentativeIndex() );
		a.join( d, distances );
		assertEquals( 3, a.getRepresentativeIndex() );
	}
	
	// UNDONE: test queues
}
