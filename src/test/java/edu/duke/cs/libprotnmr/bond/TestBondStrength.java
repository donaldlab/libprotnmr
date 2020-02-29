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

import edu.duke.cs.libprotnmr.ExtendedTestCase;

public class TestBondStrength extends ExtendedTestCase
{
	public void testBondStrength( )
	{
		assertEquals( BondStrength.Single, BondStrength.valueOf( 1 ) );
		assertEquals( BondStrength.Double, BondStrength.valueOf( 2 ) );
		assertEquals( BondStrength.Triple, BondStrength.valueOf( 3 ) );
		assertEquals( BondStrength.Pseudo, BondStrength.valueOf( 4 ) );
	}
}
