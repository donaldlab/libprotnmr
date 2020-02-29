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
