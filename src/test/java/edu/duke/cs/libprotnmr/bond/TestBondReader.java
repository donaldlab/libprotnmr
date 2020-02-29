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

import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestBondReader extends ExtendedTestCase
{
	public void testRead( )
	throws Exception
	{
		BondReader reader = new BondReader();
		HashMap<String,ArrayList<Bond>> bonds = reader.read( getClass().getResourceAsStream( Resources.getPath("backbone.bonds") ) );
		
		assertEquals( 3, bonds.keySet().size() );
		
		// check N terminus
		ArrayList<Bond> bondList = bonds.get( "NTerminus" );
		assertEquals( 8, bondList.size() );
		assertEquals( new Bond( "N", "H1", BondStrength.Single ), bondList.get( 0 ) );
		assertEquals( new Bond( "C", "O", BondStrength.Double ), bondList.get( 6 ) );
		assertEquals( new Bond( "C", "N", BondStrength.Single, true ), bondList.get( 7 ) );
		
		// check non-teminal
		bondList = bonds.get( "NonTerminal" );
		assertEquals( 6, bondList.size() );
		assertEquals( new Bond( "N", "H", BondStrength.Single ), bondList.get( 0 ) );
		assertEquals( new Bond( "C", "O", BondStrength.Double ), bondList.get( 4 ) );
		assertEquals( new Bond( "C", "N", BondStrength.Single, true ), bondList.get( 5 ) );
		
		// check C terminus
		bondList = bonds.get( "CTerminus" );
		assertEquals( 6, bondList.size() );
		assertEquals( new Bond( "N", "H", BondStrength.Single ), bondList.get( 0 ) );
		assertEquals( new Bond( "C", "O", BondStrength.Double ), bondList.get( 4 ) );
		assertEquals( new Bond( "C", "OXT", BondStrength.Single ), bondList.get( 5 ) );
	}
}
