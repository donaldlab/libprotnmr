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

public class KDTreeLeafNode implements KDTreeNode
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Cluster m_cluster;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public KDTreeLeafNode( )
	{
		m_cluster = null;
	}
	
	public KDTreeLeafNode( Cluster cluster )
	{
		m_cluster = cluster;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public Cluster getCluster( )
	{
		return m_cluster;
	}
	public void setCluster( Cluster value )
	{
		m_cluster = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return m_cluster.toString();
	}
	
	public boolean isLeaf( )
	{
		return true;
	}
	
	public KDTreeInteriorNode getInteriorNode( )
	{
		return null;
	}
	
	public KDTreeLeafNode getLeafNode( )
	{
		return this;
	}
	
	public KDTreeNode getLeft( )
	{
		return null;
	}
	
	public KDTreeNode getRight( )
	{
		return null;
	}
}
