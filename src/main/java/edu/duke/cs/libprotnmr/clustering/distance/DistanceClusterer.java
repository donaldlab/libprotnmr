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

package edu.duke.cs.libprotnmr.clustering.distance;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.perf.AbstractMessager;
import edu.duke.cs.libprotnmr.perf.Progress;


public class DistanceClusterer extends AbstractMessager
{
	/**************************
	 *   Methods
	 **************************/
	
	public List<DistanceCluster> cluster( DistanceMatrix distances, double targetDist )
	{
		// NOTE: this is a pretty spiffy O(n^2logn) algorithm from Day, Edelsbrunner '84
		
		// O(n)
		// make a cluster for every point
		List<DistanceCluster> clusters = new ArrayList<DistanceCluster>( distances.getNumPoints() );
		for( int i=0; i<distances.getNumPoints(); i++ )
		{
			clusters.add( new DistanceCluster( i, distances ) );
		}
		
		// LOGGING
		message( "Initializing " + distances.getNumPoints() + " clusters..." );
		Progress progress = new Progress( distances.getNumPoints(), 5000 );
		
		// init all inter-cluster distances
		// O(n^2logn)
		for( DistanceCluster cluster : clusters )
		{
			cluster.setAllDistances( clusters );
			progress.incrementProgress();
		}
		
		// LOGGING
		message( "Clustering..." );
		progress = new Progress( clusters.size(), 5000 );
		
		// O(n^2logn)
		// keep merging the closest pair until they're too far apart
		while( clusters.size() > 1 )
		{
			// O(n)
			// get the closest pair of clusters
			double minDist = Double.POSITIVE_INFINITY;
			DistanceCluster left = null;
			DistanceCluster right = null;
			for( DistanceCluster cluster : clusters )
			{
				DistanceCluster other = cluster.getClosestCluster();
				
				double dist = distances.get( cluster.getRepresentativeIndex(), other.getRepresentativeIndex() );
				if( dist < minDist )
				{
					minDist = dist;
					left = cluster;
					right = other;
				}
			}
			assert( left != null && right != null );
			if( minDist > targetDist )
			{
				break;
			}
			
			// O(n)
			// remove the right cluster from the list
			clusters.remove( right );
			
			// O(nlogn)
			// remove the two clusters from all the queues
			for( DistanceCluster cluster : clusters )
			{
				if( cluster != left )
				{
					cluster.removeClusterDistance( left );
				}
				cluster.removeClusterDistance( right );
			}
			
			// O(nlogn)
			// add the right cluster to the left cluster
			left.join( right, distances );
			
			// O(nlogn)
			// update the queues with the new distances
			left.setAllDistances( clusters );
			for( DistanceCluster cluster : clusters )
			{
				// don't need to update the left,right clusters
				if( cluster == left || cluster == right )
				{
					continue;
				}
				
				cluster.addClusterDistance( left );
			}
			
			progress.incrementProgress();
		}
		
		// LOGGING
		message( "Clustering complete! " + clusters.size() + " clusters remain." );
		
		return clusters;
	}
}
