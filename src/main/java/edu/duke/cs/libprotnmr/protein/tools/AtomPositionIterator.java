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

package edu.duke.cs.libprotnmr.protein.tools;

import java.util.Iterator;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;


public class AtomPositionIterator implements Iterator<Vector3>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private HasAtoms m_protein;
	private Iterator<AtomAddressInternal> m_iter;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AtomPositionIterator( HasAtoms protein )
	{
		this( protein, protein.atoms() );
	}
	
	public AtomPositionIterator( HasAtoms protein, List<AtomAddressInternal> addresses )
	{
		m_protein = protein;
		m_iter = addresses.iterator();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasNext( )
	{
		return m_iter.hasNext();
	}
	
	public Vector3 next( )
	{
		return m_protein.getAtom( m_iter.next() ).getPosition();
	}
	
	public void remove( )
	{
		m_iter.remove();
	}
}
