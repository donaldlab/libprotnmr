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

public enum ResidueType
{
	/**************************
	 *   Values
	 **************************/
	
	NTerminus,
	CTerminus,
	NonTerminal;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ResidueType valueOf( Subunit subunit, Residue residue )
	{
		if( residue.getNumber() == subunit.getFirstResidueNumber() )
		{
			return ResidueType.NTerminus;
		}
		else if( residue.getNumber() == subunit.getLastResidueNumber() )
		{
			return ResidueType.CTerminus;
		}
		return ResidueType.NonTerminal;
	}
	
	public static ResidueType valueOf( Sequence sequence, int id )
	{
		if( id == 0 )
		{
			return ResidueType.NTerminus;
		}
		else if( id == sequence.getLength() - 1 )
		{
			return ResidueType.CTerminus;
		}
		return ResidueType.NonTerminal;
	}
}
