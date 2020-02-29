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
