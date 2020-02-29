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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.resources.Resources;

public class TestSubunitOrder extends ExtendedTestCase
{
	public void testIdentityConvert( )
	throws Exception
	{
		// Computed is always ABCD
		// Reference is ABCD
		SubunitOrder order = new SubunitOrder( "ABCD" );
		
		// create a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		assertEquals( 4, protein.getSubunits().size() );
		assertEquals( 0, protein.getSubunit( 0 ).getId() );
		assertEquals( 1, protein.getSubunit( 1 ).getId() );
		assertEquals( 2, protein.getSubunit( 2 ).getId() );
		assertEquals( 3, protein.getSubunit( 3 ).getId() );
		
		char[] subunitNames = new char[] {
			protein.getSubunit( 0 ).getName(),
			protein.getSubunit( 1 ).getName(),
			protein.getSubunit( 2 ).getName(),
			protein.getSubunit( 3 ).getName()
		};
		
		// save atom references so we can check the order
		Atom atoma = protein.getAtom( new AtomAddressInternal( 0, 0, 0 ) );
		Atom atomb = protein.getAtom( new AtomAddressInternal( 1, 0, 0 ) );
		Atom atomc = protein.getAtom( new AtomAddressInternal( 2, 0, 0 ) );
		Atom atomd = protein.getAtom( new AtomAddressInternal( 3, 0, 0 ) );
		
		// convert it
		order.convertComputedToReference( protein );
		assertEquals( 4, protein.getSubunits().size() );
		assertEquals( 0, protein.getSubunit( 0 ).getId() );
		assertEquals( 1, protein.getSubunit( 1 ).getId() );
		assertEquals( 2, protein.getSubunit( 2 ).getId() );
		assertEquals( 3, protein.getSubunit( 3 ).getId() );
		
		// check the new order
		assertSame( atoma, protein.getAtom( new AtomAddressInternal( 0, 0, 0 ) ) );
		assertSame( atomb, protein.getAtom( new AtomAddressInternal( 1, 0, 0 ) ) );
		assertSame( atomc, protein.getAtom( new AtomAddressInternal( 2, 0, 0 ) ) );
		assertSame( atomd, protein.getAtom( new AtomAddressInternal( 3, 0, 0 ) ) );
		
		assertEquals( subunitNames[0], protein.getSubunit( 0 ).getName() );
		assertEquals( subunitNames[1], protein.getSubunit( 1 ).getName() );
		assertEquals( subunitNames[2], protein.getSubunit( 2 ).getName() );
		assertEquals( subunitNames[3], protein.getSubunit( 3 ).getName() );
	}
	
	public void testSwapConvert( )
	throws Exception
	{
		// Computed is always ABCD
		// Reference is DCBA
		SubunitOrder order = new SubunitOrder( "DCBA" );
		
		// create a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		assertEquals( 4, protein.getSubunits().size() );
		assertEquals( 0, protein.getSubunit( 0 ).getId() );
		assertEquals( 1, protein.getSubunit( 1 ).getId() );
		assertEquals( 2, protein.getSubunit( 2 ).getId() );
		assertEquals( 3, protein.getSubunit( 3 ).getId() );
		
		char[] subunitNames = new char[] {
			protein.getSubunit( 0 ).getName(),
			protein.getSubunit( 1 ).getName(),
			protein.getSubunit( 2 ).getName(),
			protein.getSubunit( 3 ).getName()
		};
		
		// save atom references so we can check the order
		Atom atoma = protein.getAtom( new AtomAddressInternal( 0, 0, 0 ) );
		Atom atomb = protein.getAtom( new AtomAddressInternal( 1, 0, 0 ) );
		Atom atomc = protein.getAtom( new AtomAddressInternal( 2, 0, 0 ) );
		Atom atomd = protein.getAtom( new AtomAddressInternal( 3, 0, 0 ) );
		
		// convert it
		order.convertComputedToReference( protein );
		assertEquals( 4, protein.getSubunits().size() );
		assertEquals( 0, protein.getSubunit( 0 ).getId() );
		assertEquals( 1, protein.getSubunit( 1 ).getId() );
		assertEquals( 2, protein.getSubunit( 2 ).getId() );
		assertEquals( 3, protein.getSubunit( 3 ).getId() );
		
		// check the new order
		assertSame( atomd, protein.getAtom( new AtomAddressInternal( 0, 0, 0 ) ) );
		assertSame( atomc, protein.getAtom( new AtomAddressInternal( 1, 0, 0 ) ) );
		assertSame( atomb, protein.getAtom( new AtomAddressInternal( 2, 0, 0 ) ) );
		assertSame( atoma, protein.getAtom( new AtomAddressInternal( 3, 0, 0 ) ) );
		
		assertEquals( subunitNames[0], protein.getSubunit( 0 ).getName() );
		assertEquals( subunitNames[1], protein.getSubunit( 1 ).getName() );
		assertEquals( subunitNames[2], protein.getSubunit( 2 ).getName() );
		assertEquals( subunitNames[3], protein.getSubunit( 3 ).getName() );
	}

}
