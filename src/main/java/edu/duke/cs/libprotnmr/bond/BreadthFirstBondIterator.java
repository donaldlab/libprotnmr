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
import java.util.TreeSet;
import java.util.Iterator;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class BreadthFirstBondIterator implements Iterator<ArrayList<Bond>>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private BondGraph m_graph;
	private ArrayList<Bond> m_level;
	private TreeSet<Bond> m_alreadyExploredBonds;
	private TreeSet<AtomAddressInternal> m_alreadyExploredAtoms;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public BreadthFirstBondIterator( BondGraph graph, AtomAddressInternal address )
	{
		// save parameters
		m_graph = graph;
		
		// init our already explored list
		m_alreadyExploredBonds = new TreeSet<Bond>();
		m_alreadyExploredAtoms = new TreeSet<AtomAddressInternal>();
		m_alreadyExploredAtoms.add( address );
		
		// init the first level as the bonds around this address
		m_level = m_graph.getBonds( address );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_level != null && m_level.size() > 0;
	}
	
	public ArrayList<Bond> next( )
	{
		ArrayList<Bond> level = m_level;
		m_level = getNextLevel();
		return level;
	}

	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private ArrayList<Bond> getNextLevel( )
	{
		ArrayList<Bond> nextLevel = new ArrayList<Bond>();
		
		// for each bond in this level...
		for( Bond bond : m_level )
		{
			// explore this bond
			m_alreadyExploredBonds.add( bond );
			
			// which atom do we explore?
			AtomAddressInternal exploreThisAtom = null;
			if( !m_alreadyExploredAtoms.contains( bond.getLeftAddress() ) )
			{
				exploreThisAtom = bond.getLeftAddress();
			}
			else if( !m_alreadyExploredAtoms.contains( bond.getRightAddress() ) )
			{
				exploreThisAtom = bond.getRightAddress();
			}
			
			if( exploreThisAtom == null )
			{
				continue;
			}
			
			// for each bond of the exploration atom...
			m_alreadyExploredAtoms.add( exploreThisAtom );
			ArrayList<Bond> bonds = m_graph.getBonds( exploreThisAtom );
			
			if( bonds != null )
			{
				for( Bond rightBond : bonds )
				{
					// add it to the next level if it haven't been explored yet
					if( !m_alreadyExploredBonds.contains( rightBond ) )
					{
						nextLevel.add( rightBond );
					}
				}
			}
		}
		
		return nextLevel;
	}
}
