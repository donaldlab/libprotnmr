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

public class KDTreeInteriorNode implements KDTreeNode
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_median;
	private int m_axis;
	private KDTreeNode m_left;
	private KDTreeNode m_right;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public KDTreeInteriorNode( )
	{
		m_median = Double.NaN;
		m_axis = -1;
		m_left = null;
		m_right = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getMedian( )
	{
		return m_median;
	}
	public void setMedian( double value )
	{
		m_median = value;
	}
	
	public int getAxis( )
	{
		return m_axis;
	}
	public void setAxis( int value )
	{
		m_axis = value;
	}
	
	public void setLeft( KDTreeNode value )
	{
		m_left = value;
	}
	
	public void setRight( KDTreeNode value )
	{
		m_right = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "(" + m_median + "," + m_axis + ")";
	}
	
	public boolean isLeaf( )
	{
		return false;
	}
	
	public KDTreeInteriorNode getInteriorNode( )
	{
		return this;
	}
	
	public KDTreeLeafNode getLeafNode( )
	{
		return null;
	}
	
	public KDTreeNode getLeft( )
	{
		return m_left;
	}
	
	public KDTreeNode getRight( )
	{
		return m_right;
	}
}
