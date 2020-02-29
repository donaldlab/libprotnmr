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

package edu.duke.cs.libprotnmr.nmr;

import java.util.TreeMap;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class ChemicalShiftPairLookupHomoOligomer extends ChemicalShiftPairLookup
{
	/**************************
	 *   Constructors
	 **************************/
	
	public ChemicalShiftPairLookupHomoOligomer( Iterable<ChemicalShiftPair<AtomAddressReadable>> pairs )
	{
		m_map = new TreeMap<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>>();
		for( ChemicalShiftPair<AtomAddressReadable> pair : pairs )
		{
			m_map.put( pair.getHydrogenShift().getAddress().newCopyNoSubunit(), pair );
		}
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public ChemicalShiftPair<AtomAddressReadable> get( AtomAddressReadable address )
	{
		return m_map.get( address.newCopyNoSubunit() );
	}
}
