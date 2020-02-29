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

import java.util.List;
import java.util.NoSuchElementException;

import edu.duke.cs.libprotnmr.clustering.medianStrategy.Median;
import edu.duke.cs.libprotnmr.clustering.medianStrategy.MedianStrategy;
import edu.duke.cs.libprotnmr.clustering.medianStrategy.MedianStrategyIncremental;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.perf.MessageListener;
import edu.duke.cs.libprotnmr.perf.Progress;


public class KDTree implements Iterable<Cluster>, MessageListener
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_dimension;
	private KDTreeNode m_root;
	private int m_numClusters;
	private MedianStrategy m_medianStrategy;
	private MessageListener m_progressListener;
	private Progress m_buildProgress;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public KDTree( int dimension )
	{
		this( dimension, new MedianStrategyIncremental(), null );
	}
	
	public KDTree( int dimension, MedianStrategy medianStrategy )
	{
		this( dimension, medianStrategy, null );
	}
	
	public KDTree( int dimension, MedianStrategy medianStrategy, MessageListener progressListener )
	{
		m_dimension = dimension;
		m_root = null;
		m_numClusters = 0;
		m_medianStrategy = medianStrategy;
		m_medianStrategy.setDimension( m_dimension );
		m_progressListener = progressListener;
		m_buildProgress = null;
	}
	
	public KDTree( List<MultiVector> points )
	{
		this( points, new MedianStrategyIncremental(), null );
	}
	
	public KDTree( List<MultiVector> points, MessageListener progressListener )
	{
		this( points, new MedianStrategyIncremental(), progressListener );
	}
	
	public KDTree( List<MultiVector> points, MedianStrategy medianStrategy )
	{
		this( points, medianStrategy, null );
	}
	
	public KDTree( List<MultiVector> points, MedianStrategy medianStrategy, MessageListener progressListener )
	{
		// get the dimension of our points from the first one
		// we assume that all points are of the same dimension
		m_dimension = points.get( 0 ).getDimension();
		
		// init the median strategy
		m_medianStrategy = medianStrategy;
		m_medianStrategy.setDimension( m_dimension );
		
		// handle progress if needed
		m_progressListener = progressListener;
		if( m_progressListener != null )
		{
			m_buildProgress = new Progress( points.size(), 5000 );
			m_buildProgress.setMessageListener( this );
		}
		
		// build the tree
		m_root = buildTree( points, 0, 0, points.size() - 1 );
		
		m_numClusters = points.size();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public KDTreeNode getRoot( )
	{
		return m_root;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public int getNumClusters( )
	{
		return m_numClusters;
	}
	
	public void add( Cluster cluster )
	{
		// create a new leaf node
		KDTreeLeafNode newLeafNode = new KDTreeLeafNode( cluster );
		
		// is the tree empty?
		if( m_root == null )
		{
			m_root = newLeafNode;
		}
		else
		{
			// traverse the tree down to the bottom and find the parent node
			KDTreeNodeFamily family = getNodeFamily( cluster );
			KDTreeLeafNode oldLeafNode = family.getChild();
			KDTreeInteriorNode oldInteriorNode = family.getParent();
			
			// if the child is null, then we don't have to add another interior node
			if( oldLeafNode == null )
			{
				// just add the child to the blank spot on the interior node
				if( family.getParentIsLeft() )
				{
					oldInteriorNode.setLeft( newLeafNode );
				}
				else
				{
					oldInteriorNode.setRight( newLeafNode );
				}
			}
			// otherwise, we do
			else
			{
				// parent is the interior node we'll need to add another interior node to
				KDTreeInteriorNode newInteriorNode = new KDTreeInteriorNode();
				if( oldInteriorNode == null )
				{
					m_root = newInteriorNode;
				}
				else
				{
					if( family.getParentIsLeft() )
					{
						oldInteriorNode.setLeft( newInteriorNode );
					}
					else
					{
						oldInteriorNode.setRight( newInteriorNode );
					}
				}
				
				// determine the median to use
				Median median = m_medianStrategy.getNextMedian( family, newLeafNode );
				newInteriorNode.setAxis( median.axis );
				newInteriorNode.setMedian( median.value );
				
				// connect the leaf nodes to the new interior node
				if( newLeafNode.getCluster().getCenter().get( newInteriorNode.getAxis() ) < median.value )
				{
					newInteriorNode.setLeft( newLeafNode );
					newInteriorNode.setRight( oldLeafNode );
				}
				else
				{
					newInteriorNode.setLeft( oldLeafNode );
					newInteriorNode.setRight( newLeafNode );
				}
			}
		}
		
		m_numClusters++;
		
		// just in case...
		assert( contains( cluster ) );
	}
	
	public void remove( Cluster cluster )
	{
		// find the node family for the cluster
		KDTreeNodeFamily family = getNodeFamily( cluster );
		
		// if the child is not the same as our cluster, we didn't get a match
		if( family.getChild().getCluster() != cluster ) // yes, compare references
		{
			throw new NoSuchElementException( "The cluster was not found in the KD tree!" );
		}
		
		// remove the cluster's leaf node
		KDTreeInteriorNode parent = family.getParent();
		if( parent == null )
		{
			m_root = null;
		}
		else
		{
			if( family.getParentIsLeft() )
			{
				parent.setLeft( null );
			}
			else
			{
				parent.setRight( null );
			}
		}
		
		// if this child has a leaf sibling, move it up a level (removing the parent interior node)
		KDTreeNode sibling = family.getSibling();
		if( sibling != null && sibling.isLeaf() )
		{
			KDTreeLeafNode newChild = sibling.getLeafNode();
			int depth = 2;
			
			// keep collapsing levels until we get a sibling or we're the root
			while( true )
			{
				KDTreeInteriorNode grandparent = family.getAncestor( depth );
				
				if( grandparent == null )
				{
					m_root = newChild;
					break;
				}
				else
				{
					if( family.getAncestorIsLeft( depth ) )
					{
						grandparent.setLeft( newChild );
					}
					else
					{
						grandparent.setRight( newChild );
					}
				}
				
				// check to see if we got a sibling
				sibling = family.getSibling( depth - 1 );
				if( sibling != null )
				{
					break;
				}
				
				depth++;
			}
		}
		
		// UNDONE: update the interior node bounding boxes
		
		m_numClusters--;
	}
	
	public ClusterAndDist getNearest( Cluster cluster )
	{
		return getNearest( cluster, m_root );
	}
	
	public KDTreeClusterIterator iterator( )
	{
		return new KDTreeClusterIterator( this );
	}
	
	public boolean contains( Cluster cluster )
	{
		// find the node family for the cluster
		KDTreeNodeFamily family = getNodeFamily( cluster );
		
		// if we didn't get a child, we didn't get a match
		if( family.getChild() == null )
		{
			return false;
		}
		
		// if the child is not the same as our cluster, we didn't get a match
		return family.getChild().getCluster() == cluster; // yes, compare references
	}
	
	public void message( String msg )
	{
		if( m_progressListener != null )
		{
			m_progressListener.message( msg );
		}
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private KDTreeNode buildTree( List<MultiVector> points, int depth, int start, int stop )
	{
		int numPoints = stop - start + 1;
		
		// just in case...
		assert( numPoints >= 0 );
		
		// do we need to make a leaf node
		if( numPoints == 1 )
		{
			KDTreeLeafNode node = new KDTreeLeafNode();
			node.setCluster( new Cluster( points.get( start ) ) );
			
			return node;
		}
		else
		{
			List<MultiVector> subpoints = points.subList( start, stop + 1 );
			
			// choose an axis to split along
			Median median = m_medianStrategy.getMedian( subpoints, depth );
			
			// update progress
			if( m_buildProgress != null )
			{
				m_buildProgress.incrementProgress();
			}
			
			// build the interior node and recurse
			KDTreeInteriorNode node = new KDTreeInteriorNode();
			node.setAxis( median.axis );
			node.setMedian( median.value );
			node.setLeft( buildTree( points, depth + 1, start, start + median.index - 1 ) );
			node.setRight( buildTree( points, depth + 1, start + median.index, stop ) );
			
			return node;
		}
	}
	
	private KDTreeNodeFamily getNodeFamily( Cluster cluster )
	{
		return getNodeFamily( cluster, m_root );
	}
	
	private KDTreeNodeFamily getNodeFamily( Cluster cluster, KDTreeNode root )
	{
		KDTreeNodeFamily family = new KDTreeNodeFamily();
		
		// null tree?
		if( root == null )
		{
			return family;
		}
		
		KDTreeNode checkNode = root;
		KDTreeInteriorNode interiorNode = null;
		boolean isLeft = false;
		
		// keep going down until we run out of interior nodes
		while( checkNode != null && !checkNode.isLeaf() )
		{
			// do we want to go right or left?
			interiorNode = checkNode.getInteriorNode();
			isLeft = cluster.getCenter().get( interiorNode.getAxis() ) < interiorNode.getMedian();
			
			// update ancestors lists
			family.addAncestor( interiorNode );
			family.addIsLeft( isLeft );
			
			// go down a level
			checkNode = isLeft ? checkNode.getLeft() : checkNode.getRight();
		}
		
		// sometimes the tree isn't full so check node can be null
		if( checkNode != null )
		{
			family.setChild( checkNode.getLeafNode() );
		}

		return family;
	}
	
	private ClusterAndDist getNearest( Cluster probeCluster, KDTreeNode root )
	{
		// first, get the node family for the probe point
		KDTreeNodeFamily family = getNodeFamily( probeCluster, root );
		
		// early out
		if( family.getChild() == null )
		{
			return null;
		}
		
		// UNDONE: look far a way to use squared distances
		
		// the current min dist is the dist to this child's cluster if they're not the same
		ClusterAndDist nearest = new ClusterAndDist();
		nearest.cluster = family.getChild().getLeafNode().getCluster();
		if( probeCluster == nearest.cluster ) // yes, compare references
		{
			nearest.cluster = null;
			nearest.dist = Double.POSITIVE_INFINITY;
		}
		else
		{
			nearest.dist = probeCluster.getCenter().getDistance( nearest.cluster.getCenter() );
		}
		
		// travel up the ancestor list and see if we need to check any other trees
		for( int i=1; i<=family.getDepth(); i++ )
		{
			KDTreeInteriorNode ancestor = family.getAncestor( i );
			boolean isLeft = family.getAncestorIsLeft( i );
			ClusterAndDist possibleNewNearest = null;
			
			if( isLeft )
			{
				// check the right side
				double checkCoordinate = probeCluster.getCenter().get( ancestor.getAxis() ) + nearest.dist; 
				if( checkCoordinate >= ancestor.getMedian() )
				{
					possibleNewNearest = getNearest( probeCluster, ancestor.getRight() );
				}
			}
			else
			{
				// check the left side
				double checkCoordinate = probeCluster.getCenter().get( ancestor.getAxis() ) - nearest.dist; 
				if( checkCoordinate < ancestor.getMedian() )
				{
					possibleNewNearest = getNearest( probeCluster, ancestor.getLeft() );
				}
			}
			
			// see if the possible new nearest needs to become our new nearest
			if( possibleNewNearest != null && possibleNewNearest.dist < nearest.dist )
			{
				nearest = possibleNewNearest;
			}
		}
		
		return nearest;
	}
}
