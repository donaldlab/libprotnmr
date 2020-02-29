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
