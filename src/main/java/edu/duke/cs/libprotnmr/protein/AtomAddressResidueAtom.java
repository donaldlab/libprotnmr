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

package edu.duke.cs.libprotnmr.protein;

import edu.duke.cs.libprotnmr.io.HashCalculator;

/*
	This class' purpose is to override hashCode() and equals() for different comparisons
*/
public class AtomAddressResidueAtom extends AtomAddressInternal
{
	private static final long serialVersionUID = 4921363311196941445L;
	

	/**************************
	 *   Constructors
	 **************************/

	public AtomAddressResidueAtom( AtomAddressInternal address )
	{
		super( address );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
		{
            return false;
		}
        if( other == this )
        {
            return true;
        }
        if( other.getClass() != getClass() )
        {
            return false;
        }
        
        return equals( (AtomAddressResidueAtom)other );
	}
	
	public boolean equals( AtomAddressResidueAtom other )
	{
		return
			getResidueId() == other.getResidueId()
			&& getAtomId() == other.getAtomId();
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.hashIds( getResidueId(), getAtomId() );
	}
	
	public AtomAddressInternal getAtomAddress( )
	{
		return (AtomAddressInternal)this;
	}
}
