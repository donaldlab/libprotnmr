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

import java.util.ArrayList;
import java.util.Iterator;

import edu.duke.cs.libprotnmr.math.MultiVector;


public class TestKDTreeClusterIterator extends ClusteringTestCase
{
	public void testNullTree( )
	{
		// build a tree and get an iterator
		KDTree tree = new KDTree( 2 );
		Iterator<Cluster> iter = tree.iterator();
		
		assertFalse( iter.hasNext() );
	}
	
	public void testLeafTree( )
	{
		// build a tree and get an iterator
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 1 );
		points.add( newPoint( 2.0, 3.0 ) );
		KDTree tree = new KDTree( points );
		Iterator<Cluster> iter = tree.iterator();
		
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testSmallTree( )
	{
		// build a tree and get an iterator
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		Iterator<Cluster> iter = tree.iterator();
		
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testLeftmostHole( )
	{
		// build a tree and make a hole at the leftmost spot
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		tree.remove( tree.getRoot().getLeft().getLeft().getLeafNode().getCluster() );
		
		// make sure there really is a hole there
		assertFalse( tree.getRoot().getLeft().isLeaf() );
		assertNull( tree.getRoot().getLeft().getLeft() );
		
		Iterator<Cluster> iter = tree.iterator();
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testLeftHole( )
	{
		// build a tree and make a hole at a leyt spot
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		tree.remove( tree.getRoot().getRight().getLeft().getLeafNode().getCluster() );
		
		// make sure there really is a hole there
		assertFalse( tree.getRoot().getRight().isLeaf() );
		assertNull( tree.getRoot().getRight().getLeft() );
		
		Iterator<Cluster> iter = tree.iterator();
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}

	public void testRightmostHole( )
	{
		// build a tree and make a hole at the rightmost spot
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		tree.remove( tree.getRoot().getRight().getLeft().getLeafNode().getCluster() );
		tree.remove( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster() );
		tree.remove( tree.getRoot().getRight().getLeafNode().getCluster() );
		
		// make sure there really is a hole there
		assertFalse( tree.getRoot().isLeaf() );
		assertNull( tree.getRoot().getRight() );
		
		Iterator<Cluster> iter = tree.iterator();
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}
	
	public void testRightHole( )
	{
		// build a tree and make a hole at a right spot
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		tree.add( newCluster( 1.0, 1.0 ) );
		tree.remove( tree.getRoot().getLeft().getRight().getRight().getLeafNode().getCluster() );
		tree.remove( tree.getRoot().getLeft().getRight().getLeafNode().getCluster() );
		
		// make sure there really is a hole there
		assertFalse( tree.getRoot().getLeft().isLeaf() );
		assertNull( tree.getRoot().getLeft().getRight() );
		
		Iterator<Cluster> iter = tree.iterator();
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getLeft().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getLeft().getLeft().getRight().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getLeft().getLeafNode().getCluster(), iter.next() );
		assertTrue( iter.hasNext() );
		assertSame( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster(), iter.next() );
		assertFalse( iter.hasNext() );
	}
}
