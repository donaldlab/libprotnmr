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
package edu.duke.cs.libprotnmr.clustering;

import java.util.ArrayList;

public class KDTreeNodeFamily
{
	/**************************
	 *   Data Members
	 **************************/
	
	private ArrayList<KDTreeInteriorNode> m_ancestors;
	private ArrayList<Boolean> m_isLefts;
	private KDTreeLeafNode m_child;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public KDTreeNodeFamily( )
	{
		m_ancestors = new ArrayList<KDTreeInteriorNode>();
		m_isLefts = new ArrayList<Boolean>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public KDTreeLeafNode getChild( )
	{
		return m_child;
	}
	public void setChild( KDTreeLeafNode value )
	{
		m_child = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addAncestor( KDTreeInteriorNode ancestor )
	{
		m_ancestors.add( ancestor );
	}
	
	public void removeLastAncestor( )
	{
		m_ancestors.remove( m_ancestors.size( ) - 1 );
	}
	
	public void addIsLeft( boolean isLeft )
	{
		m_isLefts.add( isLeft );
	}
	
	public void removeLastIsLeft( )
	{
		m_isLefts.remove( m_isLefts.size() - 1 );
	}
	
	public int getDepth( )
	{
		return m_ancestors.size();
	}
	
	public KDTreeInteriorNode getParent( )
	{
		return getAncestor( 1 );
	}
	
	public KDTreeInteriorNode getGrandparent( )
	{
		return getAncestor( 2 );
	}
	
	public KDTreeInteriorNode getAncestor( int ancestor )
	{
		int depth = getDepth();
		
		if( depth >= ancestor )
		{
			return m_ancestors.get( depth - ancestor );
		}
		else
		{
			return null;
		}
	}
	
	public Boolean getParentIsLeft( )
	{
		return getAncestorIsLeft( 1 );
	}
	
	public Boolean getGrandparentIsLeft( )
	{
		return getAncestorIsLeft( 2 );
	}
	
	public Boolean getAncestorIsLeft( int ancestor )
	{
		int depth = getDepth();
		
		if( depth >= ancestor )
		{
			return m_isLefts.get( depth - ancestor );
		}
		else
		{
			return null;
		}
	}
	
	public KDTreeNode getSibling( )
	{
		return getSibling( 0 );
	}
	
	public KDTreeNode getSibling( int depth )
	{
		KDTreeInteriorNode ancestor = getAncestor( depth + 1 );
		if( ancestor == null )
		{
			return null;
		}
		else
		{
			if( getAncestorIsLeft( depth + 1 ) )
			{
				return ancestor.getRight();
			}
			else
			{
				return ancestor.getLeft();
			}
		}
	}
}
