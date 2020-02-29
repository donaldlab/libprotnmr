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

package edu.duke.cs.libprotnmr.clustering.medianStrategy;

import java.util.Collections;
import java.util.List;

import edu.duke.cs.libprotnmr.clustering.KDTreeLeafNode;
import edu.duke.cs.libprotnmr.clustering.KDTreeNodeFamily;
import edu.duke.cs.libprotnmr.math.MultiAxisAlignedBox;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.math.MultiVectorComparator;


public class MedianStrategyLongestDimension extends MedianStrategyBase
{
	public Median getMedian( List<MultiVector> points, int depth )
	{
		Median median = new Median();
		
		// calculate an aabb for these points
		MultiAxisAlignedBox box = new MultiAxisAlignedBox( m_dimension );
		for( int d=0; d<m_dimension; d++ )
		{
			box.min.set( d, Double.POSITIVE_INFINITY );
			box.max.set( d, Double.NEGATIVE_INFINITY );
		}
		
		for( MultiVector point : points )
		{
			for( int d=0; d<m_dimension; d++ )
			{
				double val = point.get( d );
				if( val < box.min.get( d ) )
				{
					box.min.set( d, val );
				}
				if( val > box.max.get( d ) )
				{
					box.max.set( d, val );
				}
			}
		}
		
		// choose the longest side of the box
		MultiVector deltas = box.getDeltas();
		
		double maxLen = Double.NEGATIVE_INFINITY;
		int axis = -1;
		for( int d=0; d<m_dimension; d++ )
		{
			double len = deltas.get( d );
			if( len > maxLen )
			{
				axis = d;
				maxLen = len; 
			}
		}
		median.axis = axis;
		
		// sort along that dimension
		Collections.sort( points, new MultiVectorComparator( median.axis ) );
		
		// find the point index of the first right point
		median.index = points.size() / 2;
		
		// but we need to make sure that values of the left and right points are different
		// so modify the index if needed by searching nearby indices for a break
		double leftValue = points.get( median.index - 1 ).get( median.axis );
		double rightValue = points.get( median.index ).get( median.axis );
		if( leftValue >= rightValue )
		{
			int index = 0;
			int offset = 0;
			boolean found = false;
			boolean isInLow = true;
			boolean isInHigh = true;
			while( isInLow || isInHigh )
			{
				// try the low side
				index = median.index - offset;
				isInLow = index >= 1;
				if( isInLow )
				{
					leftValue = points.get( index - 1 ).get( median.axis );
					rightValue = points.get( index ).get( median.axis );
					if( leftValue < rightValue )
					{
						found = true;
						break;
					}
				}
				
				// try high side
				index = median.index + offset;
				isInHigh = index < points.size();
				if( isInHigh )
				{
					leftValue = points.get( index - 1 ).get( median.axis );
					rightValue = points.get( index ).get( median.axis );
					if( leftValue < rightValue )
					{
						found = true;
						break;
					}
				}
				
				offset++;
			}
			
			// just in case...
			assert( found );
			
			median.index = index;
		}
		median.value = ( leftValue + rightValue ) / 2.0;
		
		return median;
	}
	
	public Median getNextMedian( KDTreeNodeFamily oldLeafFamily, KDTreeLeafNode newLeafNode )
	{
		Median median = new Median();
		KDTreeLeafNode oldLeafNode = oldLeafFamily.getChild();
		MultiVector newPoint = newLeafNode.getCluster().getCenter();
		MultiVector oldPoint = oldLeafNode.getCluster().getCenter();
		
		// determine the longest axis-aligned distance between the two points
		double maxLen = Double.NEGATIVE_INFINITY;
		int axis = -1;
		for( int d=0; d<m_dimension; d++ )
		{
			double len = Math.abs( newPoint.get( d ) - oldPoint.get( d ) );
			if( len > maxLen )
			{
				axis = d;
				maxLen = len;
			}
		}
		median.axis = axis;
		
		// determine the median value
		median.value = ( oldPoint.get( median.axis ) + newPoint.get( median.axis ) ) / 2.0;
		
		return median;
	}
}
