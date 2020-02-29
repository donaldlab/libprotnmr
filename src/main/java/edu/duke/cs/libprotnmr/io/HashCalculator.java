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

package edu.duke.cs.libprotnmr.io;

public class HashCalculator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static int combineHashes( int ... nums )
	{
		int hashCode = 1;
		for( int i : nums )
		{
			hashCode = hashCode * 31 + i;
		}
		return hashCode;
	}
	
	public static int combineHashesCommutative( int ... nums )
	{
		int hashCode = 1;
		for( int i : nums )
		{
			hashCode += i;
		}
		return hashCode;
	}
	
	public static int hashIds( int ... nums )
	{
		int hashCode = 1;
		for( int i : nums )
		{
			hashCode = hashCode * 37 ^ ( i + 1 );
		}
		return hashCode;
	}
}
