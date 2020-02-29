/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
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
