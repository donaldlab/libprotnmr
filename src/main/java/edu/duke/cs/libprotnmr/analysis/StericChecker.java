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

package edu.duke.cs.libprotnmr.analysis;

import java.io.IOException;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.atomType.AtomTypeMap;
import edu.duke.cs.libprotnmr.bond.AtomAddressPair;
import edu.duke.cs.libprotnmr.bond.BackboneAtomAddressPairIterator;
import edu.duke.cs.libprotnmr.bond.BondGraph;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.protein.Protein;


public class StericChecker
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final int IgnoredWithinNumBonds = 3;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private AtomTypeMap m_atomTypeMap;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public StericChecker( )
	{
		m_atomTypeMap = AtomTypeMap.getInstance();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean proteinHasBackboneClash( Protein protein, ArrayList<BondGraph> bondGraphs, double epsilon )
	throws IOException
	{
		/* Jeff: 12/01/2008 - NOTE:
			Just for reference, the epsilon parameter here is the size of a tolerated
			overlap between 2 backbone atoms in angstroms.
		*/
		// get an atom pair iterator
		// Jeff: 12/01/2008 - LOL, I need to come up with shorter class names! =P
		BackboneAtomAddressPairIterator iter = new BackboneAtomAddressPairIterator( protein, bondGraphs, IgnoredWithinNumBonds );
		while( iter.hasNext() )
		{
			if( pairHasClash( protein, iter.next(), epsilon ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	// Jeff: 01/13/2009 - NOTE: I would have liked to overloaded this function, but java won't let me.  ;_;
	public boolean proteinHasBackboneClashFromPairs( Protein protein, ArrayList<AtomAddressPair> stericPairs, double epsilon )
	throws IOException
	{
		for( AtomAddressPair pair : stericPairs )
		{
			if( pairHasClash( protein, pair, epsilon ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<AtomAddressPair> getStericPairs( Protein protein, ArrayList<BondGraph> bondGraphs )
	{
		ArrayList<AtomAddressPair> pairs = new ArrayList<AtomAddressPair>();
		BackboneAtomAddressPairIterator iter = new BackboneAtomAddressPairIterator( protein, bondGraphs, IgnoredWithinNumBonds );
		while( iter.hasNext() )
		{
			pairs.add( iter.next() );
		}
		
		return pairs;
	}
		
	public boolean pairHasClash( Protein protein, AtomAddressPair pair, double epsilon )
	{
		// get properties for our atoms
		Vector3 leftPos = protein.getAtom( pair.left ).getPosition();
		Vector3 rightPos = protein.getAtom( pair.right ).getPosition();
		double leftRadius = m_atomTypeMap.getAtomType( protein, pair.left ).getRadius();
		double rightRadius = m_atomTypeMap.getAtomType( protein, pair.right ).getRadius();
		
		// check the overlap
		double overlap = leftRadius + rightRadius - leftPos.getDistance( rightPos );
		return overlap > epsilon;
	}
}
