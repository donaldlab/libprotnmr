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
import java.util.List;
import java.util.PriorityQueue;

import edu.duke.cs.libprotnmr.clustering.medianStrategy.MedianStrategy;
import edu.duke.cs.libprotnmr.clustering.stopCondition.StopCondition;
import edu.duke.cs.libprotnmr.clustering.stopCondition.StopConditionNeverStop;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.perf.MessageListener;
import edu.duke.cs.libprotnmr.perf.Timer;


public class Clusterer
{
	/**************************
	 *   Data Members
	 **************************/
	
	private KDTree m_tree;
	private PriorityQueue<ClusterPair> m_heap;
	private StopCondition m_stopCondition;
	private MessageListener m_progressListener;
	
	
	/**************************
	 *   Construstors
	 **************************/
	
	public Clusterer( )
	{
		m_tree = null;
		m_heap = null;
		m_stopCondition = new StopConditionNeverStop();
		m_progressListener = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setStopCondition( StopCondition value )
	{
		if( value == null )
		{
			value = new StopConditionNeverStop();
		}
		m_stopCondition = value;
	}
	
	public void setProgressListener( MessageListener progressListener )
	{
		m_progressListener = progressListener;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public ArrayList<Cluster> cluster( List<MultiVector> points )
	{
		return cluster( points, null );
	}
	
	public ArrayList<Cluster> cluster( List<MultiVector> points, MedianStrategy medianStrategy )
	{
		// PROGRESS
		Timer timer = null;
		if( m_progressListener != null )
		{
			m_progressListener.message( "Building KD Tree..." );
			timer = new Timer( "KD Tree Build" );
			timer.start();
		}
		
		// build the kd tree
		if( medianStrategy == null )
		{
			m_tree = new KDTree( points, m_progressListener );
		}
		else
		{
			m_tree = new KDTree( points, medianStrategy, m_progressListener );
		}
		
		// PROGRESS
		if( m_progressListener != null )
		{
			m_progressListener.message( "Tree complete!" );
			timer.stop();
			m_progressListener.message( timer.toString() );
			timer = new Timer( "Heap Build" );
			timer.start();
			m_progressListener.message( "Building heap..." );
		}
		
		// fill the heap with pairs
		m_heap = new PriorityQueue<ClusterPair>( points.size() );
		for( Cluster cluster : m_tree )
		{
			ClusterAndDist nearest = m_tree.getNearest( cluster );
			ClusterPair pair = new ClusterPair();
			pair.a = cluster;
			pair.b = nearest.cluster;
			pair.dist = nearest.dist;
			m_heap.add( pair );
		}
		
		// PROGRESS
		if( m_progressListener != null )
		{
			m_progressListener.message( "Heap complete!" );
			timer.stop();
			m_progressListener.message( timer.toString() );
			timer = new Timer( "Clustering" );
			timer.start();
			m_progressListener.message( "Starting Clustering..." );
		}
		
		// do the clustering
		while( m_tree.getNumClusters() > 1 )
		{
			// get the closest pair
			ClusterPair closestPair = m_heap.poll();
			
			// check the stop condition
			if( m_stopCondition.stop( m_tree, closestPair ) )
			{
				break;
			}
			
			if( !m_tree.contains( closestPair.a ) )
			{
				// a is already clustered with somebody
			}
			else if( !m_tree.contains( closestPair.b ) )
			{
				// b is invalid. a needs a new partner
				ClusterAndDist nearest = m_tree.getNearest( closestPair.a );
				
				// this is a problem - it means our tree is wrong
				assert( closestPair.b != nearest.cluster );
				
				closestPair.b = nearest.cluster;
				closestPair.dist = nearest.dist;
				m_heap.add( closestPair );
			}
			else
			{
				// join the two clusters
				m_tree.remove( closestPair.a );
				m_tree.remove( closestPair.b );
				Cluster joinedCluster = closestPair.a;
				joinedCluster.join( closestPair.b );
				m_tree.add( joinedCluster );
				
				// find a partner for the joined cluster
				ClusterAndDist nearest = m_tree.getNearest( joinedCluster );
				ClusterPair pair = new ClusterPair();
				pair.a = joinedCluster;
				pair.b = nearest.cluster;
				pair.dist = nearest.dist;
				m_heap.add( pair );
			}
		}
		
		// PROGRESS
		if( m_progressListener != null )
		{
			m_progressListener.message( "Cluster complete!" );
			timer.stop();
			m_progressListener.message( timer.toString() );
			timer = new Timer( "Cluster Gathering" );
			timer.start();
			m_progressListener.message( "Gathering Clusters..." );
		}
		
		// collect the final clusters
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		for( Cluster cluster : m_tree )
		{
			clusters.add( cluster );
		}
		
		// clean up memory
		m_tree = null;
		m_heap = null;
		
		// PROGRESS
		if( m_progressListener != null )
		{
			m_progressListener.message( "Gathering complete!" );
			timer.stop();
			m_progressListener.message( timer.toString() );
		}
		
		return clusters;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
}
