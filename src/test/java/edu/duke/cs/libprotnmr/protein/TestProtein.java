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

public class TestProtein extends ExtendedTestCase
{
	public void testGetBackboneTiny( )
	throws Exception
	{
		// create a protein
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("tinyProtein.pdb") ) );
		
		Protein backbone = protein.getBackbone();
		
		assertEquals( 3, backbone.getNumAtoms() );
		assertSubunitBackboneAtoms( backbone.getSubunit( 0 ) );
		
		// make sure they're the N, CA, and C atoms
		assertTrue( "N".equalsIgnoreCase( backbone.getAtom( 0, 0, 0 ).getName() ) );
		assertTrue( "CA".equalsIgnoreCase( backbone.getAtom( 0, 0, 1 ).getName() ) );
		assertTrue( "C".equalsIgnoreCase( backbone.getAtom( 0, 0, 2 ).getName() ) );
	}
	
	public void testGetBackboneLarge( )
	throws Exception
	{
		// create a protein
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		final int ExpectedNumAtoms =
			protein.getSubunits().size()
			* protein.getSubunit( 0 ).getResidues().size()
			* 3;
		
		Protein backbone = protein.getBackbone();
		
		// check the total number of atoms
		assertEquals( ExpectedNumAtoms, backbone.getNumAtoms() );
		
		// check each subunit
		for( Subunit subunit : backbone.getSubunits() )
		{
			assertSubunitBackboneAtoms( subunit );
		}
	}
	
	private void assertSubunitBackboneAtoms( Subunit subunit )
	{
		// we should have 3 backbone atoms per residue
		int numResidues = subunit.getResidues().size();
		int expectedNumAtoms = numResidues * 3;
		
		int numAtomsFound = 0;
		for( Residue residue : subunit.getResidues() )
		{
			for( Atom atom : residue.getAtoms() )
			{
				assertTrue( atom.isBackbone() );
				numAtomsFound++;
			}
		}
		assertEquals( expectedNumAtoms, numAtomsFound );
		
		// check the indices
		numAtomsFound = 0;
		for( AtomAddressInternal address : subunit.atoms() )
		{
			Atom atom = subunit.getAtom( address );
			assertTrue( atom.isBackbone() );
			numAtomsFound++;
		}
		assertEquals( expectedNumAtoms, numAtomsFound );
		numAtomsFound = 0;
		for( AtomAddressInternal address : subunit.backboneAtoms() )
		{
			Atom atom = subunit.getAtom( address );
			assertTrue( atom.isBackbone() );
			numAtomsFound++;
		}
		assertEquals( expectedNumAtoms, numAtomsFound );
	}
}
