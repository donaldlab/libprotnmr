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

package edu.duke.cs.libprotnmr.bond;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;

public class AtomAddressPair
{
	/**************************
	 *   Fields
	 **************************/
	
	public AtomAddressInternal left;
	public AtomAddressInternal right;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AtomAddressPair( AtomAddressInternal leftAddress, AtomAddressInternal rightAddress )
	{
		left = leftAddress;
		right = rightAddress;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public int hashCode( )
	{
		int leftHash = left.hashCode();
		int rightHash = right.hashCode();
		
		/* Jeff: 12/01/2008 - NOTE:
			We want an atom address pair to hash to the same value regardless
			of in which order the atoms appear. So, sort the left and right
			hashes before combining them.
		*/
		
		int smallestHash = 0;
		int largestHash = 0;
		if( leftHash < rightHash )
		{
			smallestHash = leftHash;
			largestHash = rightHash;
		}
		else
		{
			smallestHash = rightHash;
			largestHash = leftHash;
		}
		
		// now, just do a simple two-integer hash
		return smallestHash * 31 + largestHash;
	}
}
