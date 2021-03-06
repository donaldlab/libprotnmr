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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class KDTreeClusterIterator implements Iterator<Cluster>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private KDTree m_tree;
	private KDTreeNodeFamily m_family;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public KDTreeClusterIterator( KDTree tree )
	{
		m_tree = tree;
		
		// set up the node family
		m_family = new KDTreeNodeFamily();
		nextLeaf( false );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_family.getChild() != null;
	}
	
	public Cluster next( )
	{
		// just in case...
		if( !hasNext() )
		{
			throw new NoSuchElementException();
		}
		
		Cluster result = m_family.getChild().getCluster();
		nextLeaf();
		return result;
	}
	
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void nextLeaf( )
	{
		nextLeaf( true );
	}
	
	private void nextLeaf( boolean goingUp )
	{
		/* Jeff: 8/14/2008 - NOTE
			We can't use recursion here for the tree traversal since we're implementing
			an iterator. We have to be able to stop and restart the traversal after each
			leaf. So we have to use iteration and a stack (the node family) to get the
			job done.
		*/
		
		// if we start going down, use the tree root
		KDTreeNode checkNode = null;
		if( !goingUp )
		{
			checkNode = m_tree.getRoot();
		}
		
		// reset the last child
		m_family.setChild( null );
		
		// find the next leaf (left-right traversal)
		while( true )
		{
			if( goingUp )
			{
				// if we've run out of parents, there's nothing to do here
				if( m_family.getParent() == null )
				{
					break;
				}
				
				// first, if we're on a left child, look at the right one
				if( m_family.getParentIsLeft() )
				{
					// update the family
					m_family.removeLastIsLeft();
					m_family.addIsLeft( false );
					
					// get the right node
					checkNode = m_family.getParent().getRight();
					if( checkNode == null )
					{
						// keep going up
					}
					else if( checkNode.isLeaf() )
					{
						m_family.setChild( checkNode.getLeafNode() );
						break;
					}
					else
					{
						// go down a level
						m_family.addAncestor( checkNode.getInteriorNode() );
						m_family.addIsLeft( true );
						goingUp = false;
					}
				}
				// if we're on a right, go up a level
				else
				{
					m_family.removeLastAncestor();
					m_family.removeLastIsLeft();
				}
			}
			else
			{
				if( checkNode == null )
				{
					if( m_family.getParent() == null )
					{
						// empty tree, we're done here
						break;
					}
					else if( m_family.getParentIsLeft() )
					{
						// try to go to the right sibling
						m_family.removeLastIsLeft();
						m_family.addIsLeft( false );
						checkNode = m_family.getParent().getRight();
					}
					else
					{
						// we should always find a leaf before we find a right null going down
						assert( false );
						break;
					}
				}
				else if( checkNode.isLeaf() )
				{
					m_family.setChild( checkNode.getLeafNode() );
					break;
				}
				else
				{
					// go down a level
					// update the family if it hasn't been updated already
					if( m_family.getParent() != checkNode )
					{
						m_family.addAncestor( checkNode.getInteriorNode() );
						m_family.addIsLeft( true );
					}
					checkNode = checkNode.getLeft();
				}
			}	
		}
	}
}
