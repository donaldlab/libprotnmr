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

package edu.duke.cs.libprotnmr.mapping;

import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;
import edu.duke.cs.libprotnmr.protein.HasAtoms;


public enum NameScheme
{
	/**************************
	 *   Values
	 **************************/
	
	Old,
	New;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static NameScheme getSchemeForProtein( HasAtoms protein )
	{
		/* Jeff: 12/28/2008 - NOTE:
			Here's the simplest way I've found to tell the difference between
			files using the old names and the new names. If a file references
			an HN atom, it's using the old names. Otherwise, it's using the new
			names.
		*/
		
		for( AtomAddressInternal address : protein.atoms() )
		{
			if( protein.getAtom( address ).getName().equalsIgnoreCase( "HN" ) )
			{
				return Old;
			}
		}
		return New;
	}
	
	public static NameScheme getSchemeForAddresses( List<? extends HasAddresses<AtomAddressReadable>> allAddresses )
	{
		for( HasAddresses<AtomAddressReadable> addresses : allAddresses )
		{
			for( AtomAddressReadable address : addresses.addresses() )
			{
				if( address.getAtomName().equalsIgnoreCase( "HN" ) )
				{
					return Old;
				}
			}
		}
		return New;
	}
}
