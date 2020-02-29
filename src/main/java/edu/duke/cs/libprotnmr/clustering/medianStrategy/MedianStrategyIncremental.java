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

import edu.duke.cs.libprotnmr.clustering.KDTreeInteriorNode;
import edu.duke.cs.libprotnmr.clustering.KDTreeLeafNode;
import edu.duke.cs.libprotnmr.clustering.KDTreeNodeFamily;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.math.MultiVectorComparator;


public class MedianStrategyIncremental extends MedianStrategyBase
{
	public Median getMedian( List<MultiVector> points, int depth )
	{
		// just in case...
		assert( points.size() > 0 );
		
		Median median = new Median();
		
		// choose an axis
		median.axis = depth % m_dimension;
		
		// sort along that dimension
		Collections.sort( points, new MultiVectorComparator( median.axis ) );
		
		// find the point index of the first right point
		median.index = points.size() / 2;
		median.value =
			points.get( median.index ).get( median.axis ) / 2.0
			+ points.get( median.index - 1 ).get( median.axis ) / 2.0;
		
		return median;
	}
	
	public Median getNextMedian( KDTreeNodeFamily oldLeafFamily, KDTreeLeafNode newLeafNode )
	{
		Median median = new Median();
		KDTreeLeafNode oldLeafNode = oldLeafFamily.getChild();
		KDTreeInteriorNode oldInteriorNode = oldLeafFamily.getParent();
		MultiVector newPoint = newLeafNode.getCluster().getCenter();
		MultiVector oldPoint = oldLeafNode.getCluster().getCenter();
		
		if( oldInteriorNode == null )
		{
			median.axis = 0;
		}
		else
		{
			median.axis = ( oldInteriorNode.getAxis() + 1 ) % m_dimension;
		}
		
		// determine the median value
		median.value = ( oldPoint.get( median.axis ) + newPoint.get( median.axis ) ) / 2.0;
		
		return median;
	}
}
