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

package edu.duke.cs.libprotnmr.pdb;

import java.util.ArrayList;
import java.util.Iterator;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestProteinReader extends ExtendedTestCase
{
	public void testAminoAcids( )
	{
		assertEquals( AminoAcid.Glycine, AminoAcid.getByAbbreviation( "GLY" ) );
		assertEquals( AminoAcid.Glycine, AminoAcid.getByAbbreviation( "Gly" ) );
		assertEquals( AminoAcid.Glycine, AminoAcid.getByAbbreviation( "gly" ) );
		assertEquals( AminoAcid.Glycine, AminoAcid.getByCode( 'G' ) );
		assertEquals( AminoAcid.Glycine, AminoAcid.getByCode( 'g' ) );
	}
	
	public void testElement( )
	{
		assertEquals( Element.Carbon, Element.getByCode( "C" ) );
		assertEquals( Element.Carbon, Element.getByCode( "c" ) );
	}
	
	public void testTinyProtein( )
	throws Exception
	{
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("tinyProtein.pdb") ) );
		assertTinyProtein1( protein );
	}
	
	public void testTinyMultiProtein( )
	throws Exception
	{
		ProteinReader reader = new ProteinReader();
		ArrayList<Protein> proteins = reader.readAll( getClass().getResourceAsStream( Resources.getPath("tinyMultiProtein.pdb") ) );
		
		assertEquals( 2, proteins.size() );
		assertTinyProtein1( proteins.get( 0 ) );
		assertTinyProtein2( proteins.get( 1 ) );
	}
	
	public void testTinyMultiProtein1( )
	throws Exception
	{
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("tinyMultiProtein.pdb") ), 0 );
		assertTinyProtein1( protein );
	}
	
	public void testTinyMultiProtein2( )
	throws Exception
	{
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("tinyMultiProtein.pdb") ), 1 );
		assertTinyProtein2( protein );
	}
	
	public void testLargeProtein( )
	throws Exception
	{
		// basically, just make sure it doesn't crash and returns resonable results
		assertLargeProtein( new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) ) );
	}
	
	public void testXplorProtein( )
	throws Exception
	{
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("xplorProtein.pdb") ) );
		
		// check protein
		assertEquals( 1, protein.getSubunits().size() );
		
		// check subunit
		Subunit subunit = protein.getSubunits().get( 0 );
		assertEquals( 0, subunit.getId() );
		assertEquals( 1, subunit.getFirstResidueNumber() );
		assertEquals( 2, subunit.getLastResidueNumber() );
		assertEquals( 2, subunit.getResidues().size() );
		
		// make sure atom indices are populated
		assertEquals( 36, subunit.atoms().size() );
		assertEquals( 6, subunit.backboneAtoms().size() );
		
		// check residue
		Residue residue = subunit.getResidues().get( 1 );
		assertEquals( 1, residue.getId() );
		assertEquals( 2, residue.getNumber() );
		assertEquals( 20, residue.getFirstAtomNumber() );
		assertEquals( AminoAcid.Glutamine, residue.getAminoAcid() );
		assertEquals( 17, residue.getAtoms().size() );
		
		// check a couple atoms
		Atom atom = residue.getAtoms().get( 0 );
		assertEquals( 0, atom.getId() );
		assertEquals( 20, atom.getNumber() );
		assertEquals( "N", atom.getName() );
		assertEquals( 1, atom.getResidueId() );
		assertEquals( Element.Nitrogen, atom.getElement() );
		assertTrue( atom.isBackbone() );
		assertEquals( new Vector3( 2.781, -14.268, 4.026 ), atom.getPosition() );
		assertEquals( 1.00, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.00, atom.getTempFactor(), CompareReal.getEpsilon() );
		
		atom = residue.getAtoms().get( 5 );
		assertEquals( 5, atom.getId() );
		assertEquals( 25, atom.getNumber() );
		assertEquals( "HB1", atom.getName() );
		assertEquals( 1, atom.getResidueId() );
		assertEquals( Element.Hydrogen, atom.getElement() );
		assertFalse( atom.isBackbone() );
		assertEquals( new Vector3( 2.165, -13.066, 6.037 ), atom.getPosition() );
		assertEquals( 1.00, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.00, atom.getTempFactor(), CompareReal.getEpsilon() );
		
		assertProteinIndicesCorrect( protein );
	}
	
	public void testIds( )
	throws Exception
	{
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		
		int subunitId = 0;
		int residueId = 0;
		int atomId = 0;
		
		Iterator<Subunit> iterSubunit = protein.getSubunits().iterator();
		while( iterSubunit.hasNext() )
		{
			Subunit subunit = iterSubunit.next();
			assertEquals( subunitId++, subunit.getId() );
			
			residueId = 0;
			Iterator<Residue> iterResidue = subunit.getResidues().iterator();
			while( iterResidue.hasNext() )
			{
				Residue residue = iterResidue.next();
				assertEquals( residueId++, residue.getId() );
				
				atomId = 0;
				Iterator<Atom> iterAtom = residue.getAtoms().iterator();
				while( iterAtom.hasNext() )
				{
					Atom atom = iterAtom.next();
					assertEquals( atomId++, atom.getId() );
				}
			}
		}
	}
	
	public void testGenerateHeaders( )
	throws Exception
	{
		// basically, just make sure it doesn't crash and returns resonable results
		assertLargeProtein( new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProteinNoHeaders.pdb") ) ) );
	}
	
	private void assertTinyProtein1( Protein protein )
	{
		// check protein
		assertEquals( 1, protein.getSubunits().size() );
		assertProteinIndicesCorrect( protein );
		
		// check subunit
		Subunit subunit = protein.getSubunits().get( 0 );
		assertEquals( 0, subunit.getId() );
		assertEquals( 1, subunit.getFirstResidueNumber() );
		assertEquals( 1, subunit.getLastResidueNumber() );
		assertEquals( 1, subunit.getResidues().size() );
		
		// make sure atom indices are populated
		assertGte( 1, subunit.atoms().size() );
		assertGte( 1, subunit.backboneAtoms().size() );
		
		// check residue
		Residue residue = subunit.getResidues().get( 0 );
		assertEquals( 0, residue.getId() );
		assertEquals( 1, residue.getNumber() );
		assertEquals( 1, residue.getFirstAtomNumber() );
		assertEquals( AminoAcid.Glycine, residue.getAminoAcid() );
		assertEquals( 7, residue.getAtoms().size() );
		
		// check a couple atoms
		Atom atom = residue.getAtoms().get( 0 );
		assertEquals( 0, atom.getId() );
		assertEquals( 1, atom.getNumber() );
		assertEquals( "N", atom.getName() );
		assertEquals( 0, atom.getResidueId() );
		assertEquals( Element.Nitrogen, atom.getElement() );
		assertTrue( atom.isBackbone() );
		assertEquals( new Vector3( 14.651, 11.359, 10.459 ), atom.getPosition() );
		assertEquals( 1.00, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.98, atom.getTempFactor(), CompareReal.getEpsilon() );
		
		atom = residue.getAtoms().get( 5 );
		assertEquals( 5, atom.getId() );
		assertEquals( 6, atom.getNumber() );
		assertEquals( "HA2", atom.getName() );
		assertEquals( 0, atom.getResidueId() );
		assertEquals( Element.Hydrogen, atom.getElement() );
		assertFalse( atom.isBackbone() );
		assertEquals( new Vector3( 14.268, 10.224, 12.200 ), atom.getPosition() );
		assertEquals( 1.00, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.89, atom.getTempFactor(), CompareReal.getEpsilon() );
	}
	
	private void assertTinyProtein2( Protein protein )
	{
		// check protein 2
		assertEquals( 1, protein.getSubunits().size() );
		assertProteinIndicesCorrect( protein );
		
		// check subunit
		Subunit subunit = protein.getSubunits().get( 0 );
		assertEquals( 0, subunit.getId() );
		assertEquals( 1, subunit.getFirstResidueNumber() );
		assertEquals( 1, subunit.getLastResidueNumber() );
		assertEquals( 1, subunit.getResidues().size() );
		
		// check residue
		Residue residue = subunit.getResidues().get( 0 );
		assertEquals( 0, residue.getId() );
		assertEquals( 1, residue.getNumber() );
		assertEquals( 1, residue.getFirstAtomNumber() );
		assertEquals( AminoAcid.Glycine, residue.getAminoAcid() );
		assertEquals( 7, residue.getAtoms().size() );
		
		// check a couple atoms
		Atom atom = residue.getAtoms().get( 0 );
		assertEquals( 0, atom.getId() );
		assertEquals( 1, atom.getNumber() );
		assertEquals( "N", atom.getName() );
		assertEquals( 0, atom.getResidueId() );
		assertEquals( Element.Nitrogen, atom.getElement() );
		assertTrue( atom.isBackbone() );
		assertEquals( new Vector3( 15.651, 12.359, 11.459 ), atom.getPosition() );
		assertEquals( 0.50, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.78, atom.getTempFactor(), CompareReal.getEpsilon() );
		
		atom = residue.getAtoms().get( 5 );
		assertEquals( 5, atom.getId() );
		assertEquals( 6, atom.getNumber() );
		assertEquals( "HA2", atom.getName() );
		assertEquals( 0, atom.getResidueId() );
		assertEquals( Element.Hydrogen, atom.getElement() );
		assertFalse( atom.isBackbone() );
		assertEquals( new Vector3( 15.268, 11.224, 13.200 ), atom.getPosition() );
		assertEquals( 0.50, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.69, atom.getTempFactor(), CompareReal.getEpsilon() );
	}
	
	private void assertLargeProtein( Protein protein )
	{
		// check protein
		assertEquals( 4, protein.getSubunits().size() );
		
		// check subunit
		Subunit subunit = protein.getSubunits().get( 2 );
		assertEquals( 2, subunit.getId() );
		assertEquals( 319, subunit.getFirstResidueNumber() );
		assertEquals( 360, subunit.getLastResidueNumber() );
		assertEquals( 42, subunit.getResidues().size() );
		
		// make sure all subunits have residues and atoms
		Iterator<Subunit> iterSubunit = protein.getSubunits().iterator();
		while( iterSubunit.hasNext() )
		{
			Subunit tempSubunit = iterSubunit.next();
			assertEquals( 42, tempSubunit.getResidues().size() );
			assertEquals( 698, tempSubunit.atoms().size() );
			assertEquals( 42 * 3, tempSubunit.backboneAtoms().size() );
			
			// also, make sure all residues have atoms
			Iterator<Residue> iterResidue = subunit.getResidues().iterator();
			while( iterResidue.hasNext() )
			{
				Residue tempResidue = iterResidue.next();
				
				assertInRange( 1, 50, tempResidue.getAtoms().size() );
			}
		}
		
		// check residue
		Residue residue = subunit.getResidues().get( 5 );
		assertEquals( 5, residue.getId() );
		assertEquals( 324, residue.getNumber() );
		assertEquals( 1500, residue.getFirstAtomNumber() );
		assertEquals( AminoAcid.AsparticAcid, residue.getAminoAcid() );
		assertEquals( 12, residue.getAtoms().size() );
		
		// check an atom
		Atom atom = residue.getAtoms().get( 3 );
		assertEquals( 3, atom.getId() );
		assertEquals( 1503, atom.getNumber() );
		assertEquals( "O", atom.getName() );
		assertEquals( 5, atom.getResidueId() );
		assertEquals( Element.Oxygen, atom.getElement() );
		assertFalse( atom.isBackbone() );
		assertEquals( new Vector3( 15.093, -12.143, -12.249 ), atom.getPosition() );
		assertEquals( 1.00, atom.getOccupancy(), CompareReal.getEpsilon() );
		assertEquals( 0.81, atom.getTempFactor(), CompareReal.getEpsilon() );

		assertProteinIndicesCorrect( protein );
	}
}
