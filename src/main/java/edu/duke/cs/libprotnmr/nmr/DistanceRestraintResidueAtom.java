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

import java.util.HashSet;
import java.util.Set;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressResidueAtom;


/*
	This class' purpose is to override hashCode() and equals() for different comparisons
*/
public class DistanceRestraintResidueAtom extends DistanceRestraint<AtomAddressInternal>
{
	private static final long serialVersionUID = -629824621576456759L;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public DistanceRestraintResidueAtom( DistanceRestraint<AtomAddressInternal> other )
	{
		super( other );
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
        
        return equals( (DistanceRestraintResidueAtom)other );
	}
	
	public boolean equals( DistanceRestraintResidueAtom other )
	{
		return
			getMinDistance() == other.getMinDistance()
			&& getMaxDistance() == other.getMaxDistance()
			&&
			(
				(
					residueAtomEquals( getLefts(), other.getLefts() )
					&& residueAtomEquals( getRights(), other.getRights() )
				)
				||
				(
					residueAtomEquals( getLefts(), other.getRights() )
					&& residueAtomEquals( getRights(), other.getLefts() )
				)
			);
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Double.valueOf( getMinDistance() ).hashCode(),
			Double.valueOf( getMaxDistance() ).hashCode(),
			HashCalculator.combineHashesCommutative(
				residueAtomHashCode( getLefts() ),
				residueAtomHashCode( getRights() )
			)
		);
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private int residueAtomHashCode( Set<AtomAddressInternal> addresses )
	{
		HashSet<AtomAddressResidueAtom> set = new HashSet<AtomAddressResidueAtom>();
		for( AtomAddressInternal address : addresses )
		{
			set.add( new AtomAddressResidueAtom( address ) );
		}
		return set.hashCode();
	}
	
	private boolean residueAtomEquals( Set<AtomAddressInternal> a, Set<AtomAddressInternal> b )
	{
		HashSet<AtomAddressResidueAtom> x = new HashSet<AtomAddressResidueAtom>();
		for( AtomAddressInternal address : a )
		{
			x.add( new AtomAddressResidueAtom( address ) );
		}
		HashSet<AtomAddressResidueAtom> y = new HashSet<AtomAddressResidueAtom>();
		for( AtomAddressInternal address : b )
		{
			y.add( new AtomAddressResidueAtom( address ) );
		}
		return x.equals( y );
	}
}
