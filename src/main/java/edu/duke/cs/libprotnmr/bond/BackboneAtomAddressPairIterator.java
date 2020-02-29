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
import java.util.Iterator;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;


public class BackboneAtomAddressPairIterator implements Iterator<AtomAddressPair>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Protein m_protein;
	private int m_ignoreWithinNumBonds;
	private AtomAddressPair m_nextPair;
	private ArrayList<BondGraph> m_bondGraphs;
	private Iterator<AtomAddressInternal> m_backboneAddressIter;
	private AtomAddressInternal m_leftAddress;
	private int m_currentSubunitId;
	private BreadthFirstAtomIterator m_iterBfs;
	private int m_bfsLevel;
	private Iterator<AtomAddressInternal> m_iterBfsLevel;
	private Iterator<AtomAddressInternal> m_subunitBackboneIndex;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public BackboneAtomAddressPairIterator( Protein protein, ArrayList<BondGraph> bondGraphs, int ignoreWithinNumBonds )
	{
		// save parameters
		m_protein = protein;
		m_ignoreWithinNumBonds = ignoreWithinNumBonds;
		
		// init defaults
		m_bondGraphs = bondGraphs;
		m_backboneAddressIter = protein.backboneAtoms().iterator();
		m_leftAddress = null;
		m_currentSubunitId = -1;
		m_iterBfs = null;
		m_bfsLevel = 0;
		m_iterBfsLevel = null;
		m_subunitBackboneIndex = null;
		
		// get the first pair
		m_nextPair = getNextPair();
	}
	

	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_nextPair != null;
	}
	
	public AtomAddressPair next( )
	{
		AtomAddressPair next = m_nextPair;
		m_nextPair = getNextPair();
		return next;
	}
	
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private AtomAddressPair getNextPair( )
	{
		/* Jeff: 12/01/2008 - NOTE:
			This function is pretty messy and hard to follow. I haven't found out a way
			to do iterator-based pull-processing cleanly (as opposed to event-based push-processing).
			I end up having to save all possible state variables as data members and then
			check them on the next request (this function). The request decides how to update the state
			variables to prep for the next request and returns the appropriate value.
			
			It might be possible, however, to break this up into multiple functions to help
			readability.
		*/
		
		boolean doOver = true;
		AtomAddressInternal rightAddress = null;
		
		while( doOver )
		{
			// don't do over unless someone tells us to
			doOver = false;
			
			// do we have a left address?
			if( m_leftAddress == null )
			{
				if( m_backboneAddressIter.hasNext() )
				{
					m_leftAddress = m_backboneAddressIter.next();
					m_currentSubunitId = 0;
				}
				else
				{
					// that's it! We're out of pairs
					return null;
				}
			}
			
			// do we have a right subunit?
			if( m_currentSubunitId >= m_protein.getSubunits().size() )
			{
				// get a new left address
				m_currentSubunitId = 0;
				m_leftAddress = null;
				doOver = true;
				continue;
			}
			
			// what right subunit are we on?
			if( m_leftAddress.getSubunitId() == m_currentSubunitId )
			{
				// same subunit mode, do BFS in the bond graph
				
				// see if we need a bfs iterator
				if( m_iterBfs == null )
				{
					m_iterBfs = new BreadthFirstAtomIterator( m_bondGraphs.get( m_currentSubunitId ), m_leftAddress );
					for( m_bfsLevel=1; m_bfsLevel<=m_ignoreWithinNumBonds; m_bfsLevel++ )
					{
						if( m_iterBfs.hasNext() )
						{
							m_iterBfs.next();
						}
						else
						{
							// do over on another left address
							m_leftAddress = null;
							m_iterBfs = null;
							m_iterBfsLevel = null;
							doOver = true;
							continue;
						}
					}
				}
				
				// see if we need a level
				if( m_iterBfsLevel == null )
				{
					// is there a next level?
					if( m_iterBfs.hasNext() )
					{
						m_iterBfsLevel = m_iterBfs.next().iterator();
						m_bfsLevel++;
					}
					else
					{
						// go to the next subunit on the next call
						m_currentSubunitId++;
						m_iterBfs = null;
						m_iterBfsLevel = null;
						m_subunitBackboneIndex = null;
						doOver = true;
						continue;
					}
				}
				
				// return the next backbone atom address from the level
				rightAddress = null;
				while( m_iterBfsLevel.hasNext() )
				{
					AtomAddressInternal nextAddress = m_iterBfsLevel.next();
					if( m_protein.getAtom( nextAddress ).isBackbone() )
					{
						rightAddress = nextAddress;
						break;
					}
				}
				
				// no new backbone atom?
				if( rightAddress == null )
				{
					// go to the next level on the next call
					m_iterBfsLevel = null;
					doOver = true;
					continue;
				}
			}
			else
			{
				// different subunit mode, iterate over backbone indices
				
				// do we have a backbone index?
				if( m_subunitBackboneIndex == null )
				{
					m_subunitBackboneIndex = m_protein.getSubunit( m_currentSubunitId ).backboneAtoms().iterator();
				}
				
				// get the next right address
				if( m_subunitBackboneIndex.hasNext() )
				{
					rightAddress = m_subunitBackboneIndex.next();
				}
				else
				{
					// go to the next subunit
					m_currentSubunitId++;
					m_iterBfs = null;
					m_iterBfsLevel = null;
					m_subunitBackboneIndex = null;
					doOver = true;
					continue;
				}
			}
		}
		
		return new AtomAddressPair( m_leftAddress, rightAddress );
	}
}
