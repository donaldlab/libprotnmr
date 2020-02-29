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

import edu.duke.cs.libprotnmr.clustering.medianStrategy.MedianStrategyLongestDimension;
import edu.duke.cs.libprotnmr.math.MultiVector;


public class TestKDTree extends ClusteringTestCase
{
	public void testTreeConstruction( )
	{
		// get a list of points
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		
		KDTree tree = new KDTree( points );
		
		assertTreeExample( tree );
	}
	
	public void testTreeInsert( )
	{
		KDTree tree = new KDTree( 2 );
		
		// we should have an empty tree
		assertEquals( 0, tree.getNumClusters() );
		assertEquals( null, tree.getRoot() );
		
		// add one node. It should end up at the root
		tree.add( newCluster( 2.0, 3.0 ) );
		assertEquals( 1, tree.getNumClusters() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot() );
		
		// add another node. It should be the right child
		tree.add( newCluster( 4.0, 1.0 ) );
		assertEquals( 2, tree.getNumClusters() );
		assertEqualsInterior( 3.0, 0, tree.getRoot() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot().getLeft() );
		assertEqualsLeaf( 4.0, 1.0, tree.getRoot().getRight() );
		
		// add another node
		tree.add( newCluster( 1.0, 2.0 ) );
		assertEquals( 3, tree.getNumClusters() );
		assertEqualsInterior( 3.0, 0, tree.getRoot() );
		assertEqualsInterior( 2.5, 1, tree.getRoot().getLeft() );
		assertEqualsLeaf( 4.0, 1.0, tree.getRoot().getRight() );
		assertEqualsLeaf( 1.0, 2.0, tree.getRoot().getLeft().getLeft() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot().getLeft().getRight() );
		
		// add another node
		tree.add( newCluster( 6.0, 5.0 ) );
		assertEquals( 4, tree.getNumClusters() );
		assertEqualsInterior( 3.0, 0, tree.getRoot() );
		assertEqualsInterior( 2.5, 1, tree.getRoot().getLeft() );
		assertEqualsInterior( 3.0, 1, tree.getRoot().getRight() );
		assertEqualsLeaf( 1.0, 2.0, tree.getRoot().getLeft().getLeft() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot().getLeft().getRight() );
		assertEqualsLeaf( 4.0, 1.0, tree.getRoot().getRight().getLeft() );
		assertEqualsLeaf( 6.0, 5.0, tree.getRoot().getRight().getRight() );
		
		// HAHA! Add Another Node!!
		tree.add( newCluster( 5.0, 2.0 ) );
		assertEquals( 5, tree.getNumClusters() );
		assertEqualsInterior( 3.0, 0, tree.getRoot() );
		assertEqualsInterior( 2.5, 1, tree.getRoot().getLeft() );
		assertEqualsInterior( 3.0, 1, tree.getRoot().getRight() );
		assertEqualsLeaf( 1.0, 2.0, tree.getRoot().getLeft().getLeft() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot().getLeft().getRight() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getRight().getLeft() );
		assertEqualsLeaf( 6.0, 5.0, tree.getRoot().getRight().getRight() );
		assertEqualsLeaf( 4.0, 1.0, tree.getRoot().getRight().getLeft().getLeft() );
		assertEqualsLeaf( 5.0, 2.0, tree.getRoot().getRight().getLeft().getRight() );
		
		// MUWAHAHAHAHA!! Another node's soul is MNIE!!
		tree.add( newCluster( 0.0, 0.0 ) );
		assertEquals( 6, tree.getNumClusters() );
		assertEqualsInterior( 3.0, 0, tree.getRoot() );
		assertEqualsInterior( 2.5, 1, tree.getRoot().getLeft() );
		assertEqualsInterior( 3.0, 1, tree.getRoot().getRight() );
		assertEqualsInterior( 0.5, 0, tree.getRoot().getLeft().getLeft() );
		assertEqualsLeaf( 2.0, 3.0, tree.getRoot().getLeft().getRight() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getRight().getLeft() );
		assertEqualsLeaf( 6.0, 5.0, tree.getRoot().getRight().getRight() );
		assertEqualsLeaf( 0.0, 0.0, tree.getRoot().getLeft().getLeft().getLeft() );
		assertEqualsLeaf( 1.0, 2.0, tree.getRoot().getLeft().getLeft().getRight() );
		assertEqualsLeaf( 4.0, 1.0, tree.getRoot().getRight().getLeft().getLeft() );
		assertEqualsLeaf( 5.0, 2.0, tree.getRoot().getRight().getLeft().getRight() );
	}
	
	public void testTreeRemove( )
	{
		// build a tree
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		assertEquals( 6, tree.getNumClusters() );
		
		// remove a node
		tree.remove( tree.getRoot().getRight().getRight().getRight().getLeafNode().getCluster() );
		assertEquals( 5, tree.getNumClusters() );
		assertEqualsInterior( 6, 0, tree.getRoot() );
		assertEqualsInterior( 3.5, 1, tree.getRoot().getLeft() );
		assertEqualsInterior( 1.5, 1, tree.getRoot().getRight() );
		assertEqualsLeaf( 2, 3, tree.getRoot().getLeft().getLeft() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getLeft().getRight() );
		assertEqualsLeaf( 8, 1, tree.getRoot().getRight().getLeft() );
		assertEqualsLeaf( 7, 2, tree.getRoot().getRight().getRight() );
		assertEqualsLeaf( 4, 7, tree.getRoot().getLeft().getRight().getLeft() );
		assertEqualsLeaf( 5, 4, tree.getRoot().getLeft().getRight().getRight() );
		
		// remove another node
		tree.remove( tree.getRoot().getRight().getLeft().getLeafNode().getCluster() );
		assertEquals( 4, tree.getNumClusters() );
		assertEqualsInterior( 6, 0, tree.getRoot() );
		assertEqualsInterior( 3.5, 1, tree.getRoot().getLeft() );
		assertEqualsLeaf( 7, 2, tree.getRoot().getRight() );
		assertEqualsLeaf( 2, 3, tree.getRoot().getLeft().getLeft() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getLeft().getRight() );
		assertEqualsLeaf( 4, 7, tree.getRoot().getLeft().getRight().getLeft() );
		assertEqualsLeaf( 5, 4, tree.getRoot().getLeft().getRight().getRight() );
		
		// remove yet another node
		tree.remove( tree.getRoot().getRight().getLeafNode().getCluster() );
		assertEquals( 3, tree.getNumClusters() );
		assertEqualsInterior( 6, 0, tree.getRoot() );
		assertEqualsInterior( 3.5, 1, tree.getRoot().getLeft() );
		assertEqualsLeaf( 2, 3, tree.getRoot().getLeft().getLeft() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getLeft().getRight() );
		assertEqualsLeaf( 4, 7, tree.getRoot().getLeft().getRight().getLeft() );
		assertEqualsLeaf( 5, 4, tree.getRoot().getLeft().getRight().getRight() );
		
		// remove yet another node... again
		tree.remove( tree.getRoot().getLeft().getLeft().getLeafNode().getCluster() );
		assertEquals( 2, tree.getNumClusters() );
		assertEqualsInterior( 6, 0, tree.getRoot() );
		assertEqualsInterior( 3.5, 1, tree.getRoot().getLeft() );
		assertEqualsInterior( 4.5, 0, tree.getRoot().getLeft().getRight() );
		assertEqualsLeaf( 4, 7, tree.getRoot().getLeft().getRight().getLeft() );
		assertEqualsLeaf( 5, 4, tree.getRoot().getLeft().getRight().getRight() );
		
		// DO EEET AGAIN!
		tree.remove( tree.getRoot().getLeft().getRight().getLeft().getLeafNode().getCluster() );
		assertEquals( 1, tree.getNumClusters() );
		assertEqualsLeaf( 5, 4, tree.getRoot() );
		
		// ONE LAST TIME!!!!!
		tree.remove( tree.getRoot().getLeafNode().getCluster() );
		assertEquals( 0, tree.getNumClusters() );
		assertNull( tree.getRoot() );
	}
	
	public void testTreeGetNearest( )
	{
		// build a tree
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		
		// simple test - nearest point is in the first cell
		assertEquals( newCluster( 2, 3 ), tree.getNearest( newCluster( 1.8, 3 ) ).cluster );
		
		// harder test - nearest point is in the sibling region
		assertEquals( newCluster( 5, 4 ), tree.getNearest( newCluster( 4, 5 ) ).cluster );
		
		// hardest test - nearest point is in the farthest away branch
		assertEquals( newCluster( 5, 4 ), tree.getNearest( newCluster( 6.1, 4 ) ).cluster );
		
		// trick test - the closest point is in the initial region,
		// but we still have to check a lot of the tree to be sure
		assertEquals( newCluster( 7, 2 ), tree.getNearest( newCluster( 7, 3.5 ) ).cluster );
		
		// one more test just for kicks
		assertEquals( newCluster( 8, 1 ), tree.getNearest( newCluster( 5.8, -3 ) ).cluster );
		
		// make sure we don't return the probe cluster
		Cluster probeCluster = tree.getRoot().getRight().getLeft().getLeafNode().getCluster();
		assertEquals( newCluster( 7, 2 ), tree.getNearest( probeCluster ).cluster );
	}
	
	public void testContains( )
	{
		// build a tree
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		
		for( Cluster cluster : tree )
		{
			assertTrue( tree.contains( cluster ) );
		}
	}
	
	public void testRemoveAdd( )
	{
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 6 );
		points.add( newPoint( 2.0, 3.0 ) );
		points.add( newPoint( 5.0, 4.0 ) );
		points.add( newPoint( 9.0, 6.0 ) );
		points.add( newPoint( 4.0, 7.0 ) );
		points.add( newPoint( 8.0, 1.0 ) );
		points.add( newPoint( 7.0, 2.0 ) );
		KDTree tree = new KDTree( points );
		
		// remove each node and add it back
		for( Cluster cluster : tree )
		{
			tree.remove( cluster );
			tree.add( cluster );
			assertTreeExample( tree );
		}
	}
	
	public void testGridContains( )
	{
		// build a tree from a grid of points
		ArrayList<MultiVector> points = new ArrayList<MultiVector>( 9 );
		points.add( newPoint( 4, 2 ) );
		points.add( newPoint( 4, 4 ) );
		points.add( newPoint( 4, 6 ) );
		points.add( newPoint( 6, 2 ) );
		points.add( newPoint( 6, 4 ) );
		points.add( newPoint( 6, 6 ) );
		points.add( newPoint( 8, 2 ) );
		points.add( newPoint( 8, 4 ) );
		points.add( newPoint( 8, 6 ) );
		KDTree tree = new KDTree( points, new MedianStrategyLongestDimension() );
		
		// wake sure we can find each point
		for( Cluster cluster : tree )
		{
			assertTrue( tree.contains( cluster ) );
		}
	}
	
	public void testGridRemoveAdd( )
	{
		// build a tree
		KDTree treeExpected = new KDTree( 2, new MedianStrategyLongestDimension() );
		treeExpected.add( newCluster( 8, 4 ) );
		treeExpected.add( newCluster( 6, 4 ) );
		treeExpected.add( newCluster( 6, 6 ) );
		treeExpected.add( newCluster( 8, 6 ) );
		treeExpected.add( newCluster( 4, 2 ) );
		treeExpected.add( newCluster( 8, 2 ) );
		treeExpected.add( newCluster( 6, 2 ) );
		treeExpected.add( newCluster( 4, 4 ) );
		treeExpected.add( newCluster( 4, 6 ) );
		
		// build another tree
		KDTree tree = new KDTree( 2, new MedianStrategyLongestDimension() );
		tree.add( newCluster( 8, 4 ) );
		tree.add( newCluster( 6, 4 ) );
		tree.add( newCluster( 6, 6 ) );
		tree.add( newCluster( 8, 6 ) );
		tree.add( newCluster( 4, 2 ) );
		tree.add( newCluster( 8, 2 ) );
		tree.add( newCluster( 6, 2 ) );
		tree.add( newCluster( 4, 4 ) );
		tree.add( newCluster( 4, 6 ) );
		
		// the two trees should be the same
		assertTreeSame( treeExpected, tree );
		
		// remove each node and add it back
		for( Cluster cluster : tree )
		{
			tree.remove( cluster );
			tree.add( cluster );
			assertTreeSame( treeExpected, tree );
		}
	}
	
	private void assertTreeExample( KDTree tree )
	{
		assertEquals( 6, tree.getNumClusters() );
		KDTreeNode root = tree.getRoot();
		assertEqualsInterior( 6, 0, root );
		assertEqualsInterior( 3.5, 1, root.getLeft() );
		assertEqualsInterior( 1.5, 1, root.getRight() );
		assertEqualsLeaf( 2, 3, root.getLeft().getLeft() );
		assertEqualsInterior( 4.5, 0, root.getLeft().getRight() );
		assertEqualsLeaf( 8, 1, root.getRight().getLeft() );
		assertEqualsInterior( 8, 0, root.getRight().getRight() );
		assertEqualsLeaf( 4, 7, root.getLeft().getRight().getLeft() );
		assertEqualsLeaf( 5, 4, root.getLeft().getRight().getRight() );
		assertEqualsLeaf( 7, 2, root.getRight().getRight().getLeft() );
		assertEqualsLeaf( 9, 6, root.getRight().getRight().getRight() );
	}
	
	private void assertTreeSame( KDTree expected, KDTree observed )
	{
		assertTreeNodeSame( expected.getRoot(), observed.getRoot() );
	}
	
	private void assertTreeNodeSame( KDTreeNode expected, KDTreeNode observed )
	{
		boolean isExpectedNull = expected == null;
		boolean isObservedNull = observed == null;
		assertEquals( isExpectedNull, isObservedNull );
		
		assertEquals( expected.isLeaf(), observed.isLeaf() );
		if( expected.isLeaf() )
		{
			assertEquals( expected.getLeafNode().getCluster().getCenter(), observed.getLeafNode().getCluster().getCenter() );
		}
		else
		{
			assertEquals( expected.getInteriorNode().getMedian(), observed.getInteriorNode().getMedian() );
			assertEquals( expected.getInteriorNode().getAxis(), observed.getInteriorNode().getAxis() );
			assertTreeNodeSame( expected.getLeft(), observed.getLeft() );
			assertTreeNodeSame( expected.getRight(), observed.getRight() );
		}
	}
}
