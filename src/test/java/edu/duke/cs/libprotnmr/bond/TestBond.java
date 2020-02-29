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


public class TestBond extends ExtendedTestCase
{
	public void testAssignmentConstructor1( )
	{
		Bond bond = new Bond( "a", "b", BondStrength.Double );
		
		assertEquals( "a", bond.getLeftName() );
		assertEquals( "b", bond.getRightName() );
		assertEquals( BondStrength.Double, bond.getStrength() );
		assertNull( bond.getLeftAddress() );
		assertNull( bond.getLeftAddress() );
	}
	
	public void testAssignmentConstructor2( )
	{
		Protein protein = newProtein();
		AtomAddressInternal addressA = new AtomAddressInternal( 0, 0, 0 );
		AtomAddressInternal addressB = new AtomAddressInternal( 0, 0, 1 );
		
		Bond bond = new Bond( protein, addressA, addressB, BondStrength.Triple );
		
		assertEquals( "a", bond.getLeftName() );
		assertEquals( "b", bond.getRightName() );
		assertEquals( BondStrength.Triple, bond.getStrength() );
		assertSame( addressA, bond.getLeftAddress() );
		assertSame( addressB, bond.getRightAddress() );
	}
	
	public void testCopyConstructor( )
	{
		Bond bond = new Bond( "a", "b", BondStrength.Double );
		Bond copy = new Bond( bond );
		
		assertEquals( "a", copy.getLeftName() );
		assertEquals( "b", copy.getRightName() );
		assertEquals( BondStrength.Double, copy.getStrength() );
		assertNull( copy.getLeftAddress() );
		assertNull( copy.getRightAddress() );
		assertNotSame( bond, copy );
	}
	
	public void testGetOtherName( )
	{
		Bond bond = new Bond( "a", "b", BondStrength.Double );
		
		assertEquals( "b", bond.getOtherName( "a" ) );
		assertEquals( "a", bond.getOtherName( "b" ) );
	}
	
	public void testGetOtherAddress( )
	{
		Protein protein = newProtein();
		AtomAddressInternal addressA = new AtomAddressInternal( 0, 0, 0 );
		AtomAddressInternal addressB = new AtomAddressInternal( 0, 0, 1 );
		
		Bond bond = new Bond( protein, addressA, addressB, BondStrength.Triple );
		
		bond.setLeftAddress( addressA );
		bond.setRightAddress( addressB );
		
		assertSame( addressA, bond.getOtherAddress( addressB ) );
		assertSame( addressB, bond.getOtherAddress( addressA ) );
	}
	
	public void testGetOtherAddressCopy( )
	{
		Protein protein = newProtein();
		AtomAddressInternal addressA = new AtomAddressInternal( 0, 0, 0 );
		AtomAddressInternal addressB = new AtomAddressInternal( 0, 0, 1 );
		
		Bond bond = new Bond( protein, addressA, addressB, BondStrength.Triple );
		
		bond.setLeftAddress( addressA );
		bond.setRightAddress( addressB );
		
		assertEquals( new AtomAddressInternal( addressA ), bond.getOtherAddress( new AtomAddressInternal( addressB ) ) );
		assertEquals( new AtomAddressInternal( addressB ), bond.getOtherAddress( new AtomAddressInternal( addressA ) ) );
	}
	
	private Protein newProtein( )
	{
		// get 2 atoms
		Atom a = new Atom();
		a.setName( "a" );
		a.setId( 0 );
		Atom b = new Atom();
		b.setName( "b" );
		b.setId( 0 );
		
		// get a residue
		Residue residue = new Residue();
		residue.setId( 0 );
		ArrayList<Atom> atoms = new ArrayList<Atom>( 2 );
		atoms.add( a );
		atoms.add( b );
		residue.setAtoms( atoms );
		
		// get a subunit
		Subunit subunit = new Subunit();
		subunit.setId( 0 );
		ArrayList<Residue> residues = new ArrayList<Residue>( 1 );
		residues.add( residue );
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
