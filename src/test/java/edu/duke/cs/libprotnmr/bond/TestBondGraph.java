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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class TestBondGraph extends ExtendedTestCase
{
	public void testBondGraph( )
	{
		BondGraph bondGraph = new BondGraph();
		
		// make a protein with 3 atoms
		Protein protein = newProtein();
		AtomAddressInternal addressA = new AtomAddressInternal( 0, 0, 0 );
		AtomAddressInternal addressB = new AtomAddressInternal( 0, 0, 1 );
		AtomAddressInternal addressC = new AtomAddressInternal( 0, 1, 0 );
		
		// add 2 bonds
		Bond bondA = new Bond( protein, addressA, addressB, BondStrength.Single );
		Bond bondB = new Bond( protein, addressA, addressC, BondStrength.Double );
		
		assertEquals( 0, bondGraph.getBonds().size() );
		bondGraph.addBond( bondA );
		assertEquals( 1, bondGraph.getBonds().size() );
		bondGraph.addBond( bondB );
		assertEquals( 2, bondGraph.getBonds().size() );
		
		ArrayList<Bond> bonds = bondGraph.getBonds( addressA );
		assertEquals( 2, bonds.size() );
		assertSame( bondA, bonds.get( 0 ) );
		assertSame( addressB, bonds.get( 0 ).getOtherAddress( addressA ) );
		assertSame( bondB, bonds.get( 1 ) );
		assertSame( addressC, bonds.get( 1 ).getOtherAddress( addressA ) );
		
		bonds = bondGraph.getBonds( addressB );
		assertEquals( 1, bonds.size() );
		assertSame( bondA, bonds.get( 0 ) );
		assertSame( addressA, bonds.get( 0 ).getOtherAddress( addressB ) );

		bonds = bondGraph.getBonds( addressC );
		assertEquals( 1, bonds.size() );
		assertSame( bondB, bonds.get( 0 ) );
		assertSame( addressA, bonds.get( 0 ).getOtherAddress( addressC ) );
	}
	
	private Protein newProtein( )
	{
		// get 2 atoms
		Atom a = new Atom();
		a.setName( "a" );
		a.setId( 0 );
		Atom b = new Atom();
		b.setName( "b" );
		b.setId( 1 );
		Atom c = new Atom();
		c.setName( "c" );
		c.setId( 0 );
		
		// get 2 residues
		Residue residueA = new Residue();
		residueA.setId( 0 );
		ArrayList<Atom> atoms = new ArrayList<Atom>( 2 );
		atoms.add( a );
		atoms.add( b );
		residueA.setAtoms( atoms );
		Residue residueB = new Residue();
		residueB.setId( 1 );
		atoms = new ArrayList<Atom>( 1 );
		atoms.add( c );
		residueB.setAtoms( atoms );
		
		// get a subunit
		Subunit subunit = new Subunit();
		subunit.setId( 0 );
		ArrayList<Residue> residues = new ArrayList<Residue>( 1 );
		residues.add( residueA );
		residues.add( residueB );
		subunit.setResidues( residues );
		subunit.updateAtomIndices();
		
		// get a protein
		Protein protein = new Protein();
		ArrayList<Subunit> subunits = new ArrayList<Subunit>( 1 );
		subunits.add( subunit );
		protein.setSubunits( subunits );
		
		return protein;
	}
}
