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

package edu.duke.cs.libprotnmr.atomType;

import java.util.TreeMap;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestAtomTypeReader extends ExtendedTestCase
{
	public void testAtomTypeReader( )
	throws Exception
	{
		AtomTypeReader reader = new AtomTypeReader();
		TreeMap<String,TreeMap<String,AtomType>> map = reader.read( getClass().getResourceAsStream( Resources.getPath("backbone.atomTypes") ) );
		
		// not-so-random sampling of the read-in values
		assertEquals( AtomType.Nam, map.get( "CTERMINUS" ).get( "N" ) );
		assertEquals( AtomType.Oco2, map.get( "CTERMINUS" ).get( "O" ) );
		assertEquals( AtomType.Oco2, map.get( "CTERMINUS" ).get( "OXT" ) );
		assertEquals( AtomType.H, map.get( "CTERMINUS" ).get( "H" ) );
		assertEquals( AtomType.C3, map.get( "NONTERMINAL" ).get( "CA" ) );
		assertEquals( AtomType.O2, map.get( "NTERMINUS" ).get( "O" ) );
		assertEquals( AtomType.O2, map.get( "NTERMINUS" ).get( "O" ) );
	}
}
