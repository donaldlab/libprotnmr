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


public class BreadthFirstAtomIterator implements Iterator<ArrayList<AtomAddressInternal>>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private BondGraph m_graph;
	private ArrayList<AtomAddressInternal> m_level;
	private TreeSet<AtomAddressInternal> m_alreadyExplored;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public BreadthFirstAtomIterator( BondGraph graph, AtomAddressInternal address )
	{
		// save parameters
		m_graph = graph;
		
		// init the 0th level with our initial address
		m_level = new ArrayList<AtomAddressInternal>();
		m_level.add( address );
		
		// init our already explored list
		m_alreadyExplored = new TreeSet<AtomAddressInternal>();
		
		// get the first level
		m_level = getNextLevel();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_level != null && m_level.size() > 0;
	}
	
	public ArrayList<AtomAddressInternal> next( )
	{
		ArrayList<AtomAddressInternal> level = m_level;
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
	
	private ArrayList<AtomAddressInternal> getNextLevel( )
	{
		ArrayList<AtomAddressInternal> nextLevel = new ArrayList<AtomAddressInternal>();
		
		// for each address in this level...
		for( AtomAddressInternal address : m_level )
		{
			// explore this address
			m_alreadyExplored.add( address );
			
			// for each neighbor of this address...
			ArrayList<Bond> bonds = m_graph.getBonds( address );
			if( bonds != null )
			{
				for( Bond bond : bonds )
				{
					// add it to the next level if they haven't been explored yet
					AtomAddressInternal otherAddress = bond.getOtherAddress( address );
					if( !m_alreadyExplored.contains( otherAddress ) )
					{
						nextLevel.add( otherAddress );
					}
				}
			}
		}
		
		return nextLevel;
	}
}
