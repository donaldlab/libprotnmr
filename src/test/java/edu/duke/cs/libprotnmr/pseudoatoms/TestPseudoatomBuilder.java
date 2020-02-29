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

package edu.duke.cs.libprotnmr.pseudoatoms;

import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.nmr.ChemicalShift;
import edu.duke.cs.libprotnmr.nmr.ChemicalShiftReader;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.HomoProtein;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestPseudoatomBuilder extends ExtendedTestCase
{
	private static final String ProteinPath = Resources.getPath("largeProtein.pdb");
	
	@SuppressWarnings( "incomplete-switch" )
	public void testProtein( )
	throws Exception
	{
		// get a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) );
		PseudoatomBuilder.getInstance().build( protein );
		
		// make sure a few of the pseudoatoms were added
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				switch( residue.getAminoAcid() )
				{
					case Phenylalanine:
						assertNotNull( residue.getAtomByName( "qb" ) );
						assertNotNull( residue.getAtomByName( "qd" ) );
						assertNotNull( residue.getAtomByName( "qe" ) );
						assertNotNull( residue.getAtomByName( "qr" ) );
					break;
					
					case Lysine:
						assertNotNull( residue.getAtomByName( "qb" ) );
						assertNotNull( residue.getAtomByName( "qg" ) );
						assertNotNull( residue.getAtomByName( "qd" ) );
						assertNotNull( residue.getAtomByName( "qe" ) );
					break;
				}
			}
		}
		
		// check a few of the atom positions
		Residue residue = null;
		
		// get a Phe residue in the first subunit
		residue = protein.getResidue( 0, 9 );
		assertEquals( AminoAcid.Phenylalanine, residue.getAminoAcid() );
		assertEquals( new Vector3( 15.38250, 0.63550, 6.19000 ), residue.getAtomByName( "qb" ).getPosition() );
		assertEquals( new Vector3( 15.72000, 1.33700, 8.13050 ), residue.getAtomByName( "qd" ).getPosition() );
		assertEquals( new Vector3( 15.69400, 1.75900, 10.57550 ), residue.getAtomByName( "qe" ).getPosition() );
		assertEquals( new Vector3( 15.70733, 1.54833, 9.35350 ), residue.getAtomByName( "qr" ).getPosition() );
		
		// Lysine
		residue = protein.getResidue( 0, 0 );
		assertEquals( AminoAcid.Lysine, residue.getAminoAcid() );
		assertEquals( new Vector3( 15.34050, 24.45850, 5.86300 ), residue.getAtomByName( "qb" ).getPosition() );
		assertEquals( new Vector3( 14.50100, 26.68850, 6.18800 ), residue.getAtomByName( "qg" ).getPosition() );
		assertEquals( new Vector3( 13.40150, 26.22700, 4.15100 ), residue.getAtomByName( "qd" ).getPosition() );
		assertEquals( new Vector3( 15.54850, 26.14650, 3.16350 ), residue.getAtomByName( "qe" ).getPosition() );
	}
	
	@SuppressWarnings( "incomplete-switch" )
	public void testHomoProtein( )
	throws Exception
	{
		// get a protein
		HomoProtein protein = new HomoProtein( new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) ).getSubunit( 0 ), 2 );
		PseudoatomBuilder.getInstance().build( protein );
		
		// make sure a few of the pseudoatoms were added
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				switch( residue.getAminoAcid() )
				{
					case Phenylalanine:
						assertNotNull( residue.getAtomByName( "qb" ) );
						assertNotNull( residue.getAtomByName( "qd" ) );
						assertNotNull( residue.getAtomByName( "qe" ) );
						assertNotNull( residue.getAtomByName( "qr" ) );
					break;
					
					case Lysine:
						assertNotNull( residue.getAtomByName( "qb" ) );
						assertNotNull( residue.getAtomByName( "qg" ) );
						assertNotNull( residue.getAtomByName( "qd" ) );
						assertNotNull( residue.getAtomByName( "qe" ) );
					break;
				}
			}
		}
		
		// check a few of the atom positions
		Residue residue = null;
		
		// get a Phe residue in the first subunit
		residue = protein.getResidue( 0, 9 );
		assertEquals( AminoAcid.Phenylalanine, residue.getAminoAcid() );
		assertEquals( new Vector3( 15.38250, 0.63550, 6.19000 ), residue.getAtomByName( "qb" ).getPosition() );
		assertEquals( new Vector3( 15.72000, 1.33700, 8.13050 ), residue.getAtomByName( "qd" ).getPosition() );
		assertEquals( new Vector3( 15.69400, 1.75900, 10.57550 ), residue.getAtomByName( "qe" ).getPosition() );
		assertEquals( new Vector3( 15.70733, 1.54833, 9.35350 ), residue.getAtomByName( "qr" ).getPosition() );
		
		// Lysine
		residue = protein.getResidue( 0, 0 );
		assertEquals( AminoAcid.Lysine, residue.getAminoAcid() );
		assertEquals( new Vector3( 15.34050, 24.45850, 5.86300 ), residue.getAtomByName( "qb" ).getPosition() );
		assertEquals( new Vector3( 14.50100, 26.68850, 6.18800 ), residue.getAtomByName( "qg" ).getPosition() );
		assertEquals( new Vector3( 13.40150, 26.22700, 4.15100 ), residue.getAtomByName( "qd" ).getPosition() );
		assertEquals( new Vector3( 15.54850, 26.14650, 3.16350 ), residue.getAtomByName( "qe" ).getPosition() );
	}
	
	public void testProteinPseudoatomFlags( )
	throws Exception
	{
		// get a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) );
		PseudoatomBuilder.getInstance().build( protein );
		
		for( AtomAddressInternal address : protein.atoms() )
		{
			// check the name to see if this should be a pseudoatom
			Atom atom = protein.getAtom( address );
			String name = atom.getName().toLowerCase();
			boolean shouldBePseudoatom = name.startsWith( "m" ) || name.startsWith( "p" ) || name.startsWith( "q" );
			if( shouldBePseudoatom )
			{
				assertTrue( "Should be a pseudoatom: " + name, atom.isPseudoatom() );
			}
			else
			{
				assertFalse( "Should NOT be a pseudoatom: " + name, atom.isPseudoatom() );
			}
			assertEquals( shouldBePseudoatom, atom.isPseudoatom() );
		}
	}
	
	public void testProteinAtomIndex( )
	throws Exception
	{
		// get a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) );
		PseudoatomBuilder.getInstance().build( protein );
		
		// make sure all the atoms get added to the atom index
		int numAtomsFound = 0;
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					numAtomsFound++;
					
					// try to find the atom in the index
					boolean found = false;
					for( AtomAddressInternal address : protein.atoms() )
					{
						if( protein.getAtom( address ) == atom )
						{
							found = true;
							break;
						}
					}
					
					assertTrue( found );
				}
			}
		}
		assertTrue( numAtomsFound > 0 );
		assertEquals( protein.getNumAtoms(), numAtomsFound );
	}
	
	public void testHomoProteinAtomIndex( )
	throws Exception
	{
		// get a protein
		HomoProtein protein = new HomoProtein( new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) ).getSubunit( 0 ), 2 );
		PseudoatomBuilder.getInstance().build( protein );
		
		// make sure all the atoms get added to the atom index
		int numAtomsFound = 0;
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					numAtomsFound++;
					
					// try to find the atom in the index
					boolean found = false;
					for( AtomAddressInternal address : protein.atoms() )
					{
						if( protein.getAtom( address ) == atom )
						{
							found = true;
							break;
						}
					}
					
					assertTrue( found );
				}
			}
		}
		assertTrue( numAtomsFound > 0 );
		assertEquals( protein.getNumAtoms(), numAtomsFound );
	}
	
	public void testChemicalShift( )
	throws Exception
	{
		// read the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) );
		Sequences sequences = protein.getSequences();
		Subunit subunit = protein.getSubunits().get( 0 );
		NameMapper.ensureProtein( subunit, NameScheme.New );
		
		// read the shifts
		List<ChemicalShift<AtomAddressReadable>> shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("chemical.shifts") ) );
		NameMapper.ensureAddresses( sequences, shifts, NameScheme.New );
		assertEquals( 4, shifts.size() );
		
		// build the pseudoatoms - it shouldn't change anything
		PseudoatomBuilder.getInstance().buildShifts( sequences, shifts );
		
		assertEquals( 4, shifts.size() );
		ChemicalShift<AtomAddressReadable> shift = null;
		
		// 43  4 LYS CE   C  41.8662 0.2  1 
		shift = shifts.get( 0 );
		assertEquals( 43, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 4, "CE" ), shift.getAddress() );
		assertEquals( AminoAcid.Lysine, shift.getAminoAcid() );
		assertEquals( Element.Carbon, shift.getElement() );
		assertEquals( 41.8662, shift.getValue() );
		assertEquals( 0.2, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 124 12 LEU HA   H   5.0068 0.02 1 
		shift = shifts.get( 1 );
		assertEquals( 124, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 12, "HA" ), shift.getAddress() );
		assertEquals( AminoAcid.Leucine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 5.0068, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 422 45 TYR H    H   8.8528 0.02 1
		shift = shifts.get( 2 );
		assertEquals( 422, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 45, "H" ), shift.getAddress() );
		assertEquals( AminoAcid.Tyrosine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 8.8528, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 466 50 LYS CG   C  24.5176 0.2  1 
		shift = shifts.get( 3 );
		assertEquals( 466, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 50, "CG" ), shift.getAddress() );
		assertEquals( AminoAcid.Lysine, shift.getAminoAcid() );
		assertEquals( Element.Carbon, shift.getElement() );
		assertEquals( 24.5176, shift.getValue() );
		assertEquals( 0.2, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
	}
	
	public void testChemicalShiftPseudo( )
	throws Exception
	{
		// read the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) );
		Sequences sequences = protein.getSequences();
		Subunit subunit = protein.getSubunits().get( 0 );
		NameMapper.ensureProtein( subunit, NameScheme.New );
		
		// read the shifts
		List<ChemicalShift<AtomAddressReadable>> shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("chemical.pseudo.shifts") ) );
		NameMapper.ensureAddresses( sequences, shifts, NameScheme.New );
		assertEquals( 2, shifts.size() );
		
		// build the pseudoatoms
		PseudoatomBuilder.getInstance().buildShifts( sequences, shifts );
		
		assertEquals( 2, shifts.size() );
		ChemicalShift<AtomAddressReadable> shift = null;

		// 8  1 MET HE   H   2.1100 0.02 1
		shift = shifts.get( 0 );
		assertEquals( 8, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 1, "ME" ), shift.getAddress() );
		assertEquals( AminoAcid.Methionine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 2.1100, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 130 12 LEU HD2  H   0.1190 0.02 2 
		shift = shifts.get( 1 );
		assertEquals( 130, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 12, "MD2" ), shift.getAddress() );
		assertEquals( AminoAcid.Leucine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 0.1190, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 2, shift.getAmbiguityCode() );
	}
}
