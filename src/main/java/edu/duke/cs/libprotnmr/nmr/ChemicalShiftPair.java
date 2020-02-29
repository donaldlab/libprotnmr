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

import edu.duke.cs.libprotnmr.protein.AtomAddress;

public class ChemicalShiftPair<T extends AtomAddress<T>>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private ChemicalShift<T> m_hydrogenShift;
	private ChemicalShift<T> m_heavyShift;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ChemicalShiftPair( ChemicalShift<T> hydrogenShift, ChemicalShift<T> heavyShift )
	{
		m_hydrogenShift = hydrogenShift;
		m_heavyShift = heavyShift;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public ChemicalShift<T> getHydrogenShift( )
	{
		return m_hydrogenShift;
	}
	
	public ChemicalShift<T> getHeavyShift( )
	{
		return m_heavyShift;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		StringBuffer buf = new StringBuffer();
		buf.append( "[MappedChemicalShiftPair]\n\t" );
		buf.append( m_hydrogenShift.toString() );
		buf.append( "\n\t" );
		buf.append( m_heavyShift.toString() );
		buf.append( "\n" );
		return buf.toString();
	}

}
