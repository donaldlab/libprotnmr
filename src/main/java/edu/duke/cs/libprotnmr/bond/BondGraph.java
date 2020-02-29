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
package edu.duke.cs.libprotnmr.bond;

import java.util.ArrayList;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class BondGraph
{
	/**************************
	 *   Data Members
	 **************************/
	
	private TreeMap<AtomAddressInternal,ArrayList<Bond>> m_adjacency;
	private ArrayList<Bond> m_bonds;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public BondGraph( )
	{
		m_adjacency = new TreeMap<AtomAddressInternal,ArrayList<Bond>>();
		m_bonds = new ArrayList<Bond>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public ArrayList<Bond> getBonds( )
	{
		return m_bonds;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addBond( Bond bond )
	{
		// just in case...
		assert( bond.getLeftAddress() != null );
		assert( bond.getRightAddress() != null );
		
		// add forward and backwards edges
		addEdge( bond.getLeftAddress(), bond );
		addEdge( bond.getRightAddress(), bond );
		
		// add to the edge list
		m_bonds.add( bond );
	}
	
	public ArrayList<Bond> getBonds( AtomAddressInternal address )
	{
		return m_adjacency.get( address );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void addEdge( AtomAddressInternal leftAddress, Bond bond )
	{
		// make sure there's a list for this atom
		if( !m_adjacency.containsKey( leftAddress ) )
		{
			m_adjacency.put( leftAddress, new ArrayList<Bond>() );
		}
		
		// add the edge
		m_adjacency.get( leftAddress ).add( bond );
	}
}
