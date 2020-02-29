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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class RdcFilterer
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ArrayList<Rdc<AtomAddressInternal>> pickUnique( List<Rdc<AtomAddressInternal>> rdcs )
	{
		HashSet<Rdc<AtomAddressInternal>> filteredRdcs = new HashSet<Rdc<AtomAddressInternal>>();
		filteredRdcs.addAll( rdcs );
		return new ArrayList<Rdc<AtomAddressInternal>>( filteredRdcs );
	}
	
	public static ArrayList<Rdc<AtomAddressInternal>> pickFromSubunit( List<Rdc<AtomAddressInternal>> rdcs, int subunitId )
	{
		ArrayList<Rdc<AtomAddressInternal>> filteredRdcs = new ArrayList<Rdc<AtomAddressInternal>>();
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			if( rdc.getFrom().getSubunitId() == subunitId )
			{
				filteredRdcs.add( rdc );
			}
		}
		return filteredRdcs;
	}
}
