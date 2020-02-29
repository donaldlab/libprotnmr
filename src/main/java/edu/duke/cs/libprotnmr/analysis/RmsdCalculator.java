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

import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class RmsdCalculator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getRmsd( HasAtoms a, HasAtoms b )
	{
		return getRmsd( a, b, a.atoms(), b.atoms() );
	}
	
	public static double getBackboneRmsd( HasAtoms a, HasAtoms b )
	{
		return getRmsd( a, b, a.backboneAtoms(), b.backboneAtoms() );
	}
	
	public static double getRmsdByResidueNumbers( Subunit a, Subunit b, int startNumber, int stopNumber )
	{
		return getRmsd(
			a,
			b,
			a.getInternalAddressesByResidueNumbers( startNumber, stopNumber ),
			b.getInternalAddressesByResidueNumbers( startNumber, stopNumber )
		);
	}
	
	public static double getBackboneRmsdByResidueNumbers( Subunit a, Subunit b, int startNumber, int stopNumber )
	{
		return getRmsd(
			a,
			b,
			a.getBackboneInternalAddressesByResidueNumbers( startNumber, stopNumber ),
			b.getBackboneInternalAddressesByResidueNumbers( startNumber, stopNumber )
		);
	}
	
	public static double getRmsd( HasAtoms a, HasAtoms b, List<AtomAddressInternal> addressesA, List<AtomAddressInternal> addressesB )
	{
		// just in case...
		assert( addressesA.size() == addressesB.size() );
		
		double sum = 0.0;
		int count = 0;
		
		// for each atom...
		for( int i=0; i<addressesA.size(); i++ )
		{
			// get atoms
			Vector3 posA = new Vector3( a.getAtom( addressesA.get( i ) ).getPosition() );
			Vector3 posB = new Vector3( b.getAtom( addressesB.get( i ) ).getPosition() );
			
			// add the sd
			sum += posB.getSquaredDistance( posA );
			count++;
		}
		
		// do the rm part
		return Math.sqrt( sum / (double)count );
	}
}
