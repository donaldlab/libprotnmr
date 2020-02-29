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
