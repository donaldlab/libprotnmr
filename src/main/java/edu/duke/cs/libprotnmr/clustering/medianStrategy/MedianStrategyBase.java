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

import java.util.List;

import edu.duke.cs.libprotnmr.clustering.KDTreeLeafNode;
import edu.duke.cs.libprotnmr.clustering.KDTreeNodeFamily;
import edu.duke.cs.libprotnmr.math.MultiVector;


public abstract class MedianStrategyBase implements MedianStrategy
{
	/**************************
	 *   Data Members
	 **************************/
	
	protected int m_dimension;
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setDimension( int value )
	{
		m_dimension = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public abstract Median getMedian( List<MultiVector> points, int depth );
	public abstract Median getNextMedian( KDTreeNodeFamily oldLeafFamily, KDTreeLeafNode newLeafNode );
}
