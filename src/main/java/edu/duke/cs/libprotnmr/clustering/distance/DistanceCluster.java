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
package edu.duke.cs.libprotnmr.clustering.distance;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.dataStructures.MinHeap;
import edu.duke.cs.libprotnmr.dataStructures.MinHeapNode;



public class DistanceCluster
{
	/**************************
	 *   Data Members
	 **************************/
	
	private List<Integer> m_pointIndices;
	private MinHeap<DistanceCluster> m_clusterQueue;
	private ArrayList<MinHeapNode<DistanceCluster>> m_clusterNodes;
	private int m_representativeIndex;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public DistanceCluster( int index, DistanceMatrix pointDistances )
	{
		// save this cluster's index as the first point index
		m_pointIndices = new ArrayList<Integer>();
		m_pointIndices.add( index );
		
		// start the distance queue with the right three-way comparator
		m_clusterQueue = new MinHeap<DistanceCluster>(
			new DistanceClusterComparator( pointDistances, this ),
			Math.max( 0, pointDistances.getNumPoints() - 1 )
		);
		
		// allocate space to index each cluster node
		m_clusterNodes = new ArrayList<MinHeapNode<DistanceCluster>>( pointDistances.getNumPoints() );
		for( int i=0; i<pointDistances.getNumPoints(); i++ )
		{
			m_clusterNodes.add( null );
		}
		
		m_representativeIndex = index;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getIndex( )
	{
		return m_pointIndices.get( 0 );
	}
	
	public List<Integer> getPointIndices( )
	{
		return m_pointIndices;
	}
	
	public int getRepresentativeIndex( )
	{
		return m_representativeIndex;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void setAllDistances( List<DistanceCluster> clusters )
	{
		// add all the pairwise distances to the queue
		m_clusterQueue.clear();
		for( DistanceCluster cluster : clusters )
		{
			// no self-pairings
			if( cluster == this )
			{
				continue;
			}
			
			addClusterDistance( cluster );
		}
	}
	
	public DistanceCluster getClosestCluster( )
	{
		return m_clusterQueue.getMin();
	}
	
	public void addClusterDistance( DistanceCluster cluster )
	{
		// add the cluster
		MinHeapNode<DistanceCluster> node = m_clusterQueue.add( cluster );
		m_clusterNodes.set( cluster.getIndex(), node );
	}
	
	public void removeClusterDistance( DistanceCluster cluster )
	{
		// lookup the head node index
		int nodeIndex = m_clusterNodes.get( cluster.getIndex() ).getIndex();
		m_clusterQueue.remove( nodeIndex );
	}
	
	public void join( DistanceCluster other, DistanceMatrix distances )
	{
		// merge the two clusters
		m_pointIndices.addAll( other.getPointIndices() );
		m_representativeIndex = computeRepresentativeIndex( distances );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private int computeRepresentativeIndex( DistanceMatrix distances )
	{
		// find the median point
		double bestSum = Double.POSITIVE_INFINITY;
		int bestIndex = -1;
		for( int i=0; i<m_pointIndices.size(); i++ )
		{
			double sum = 0.0;
			for( int j=0; j<m_pointIndices.size(); j++ )
			{
				sum += distances.get( m_pointIndices.get( i ), m_pointIndices.get( j ) );
			}
			
			if( sum < bestSum )
			{
				bestSum = sum;
				bestIndex = m_pointIndices.get( i );
			}
		}
		assert( bestIndex != -1 );
		return bestIndex;
	}
}
