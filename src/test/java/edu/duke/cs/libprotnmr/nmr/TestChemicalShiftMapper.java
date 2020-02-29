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

package edu.duke.cs.libprotnmr.nmr;

import java.util.List;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestChemicalShiftMapper extends ExtendedTestCase
{
	private Protein m_protein;
	private Sequences m_sequences;
	private Subunit m_subunit;
	private Sequence m_sequence;
	private List<ChemicalShift<AtomAddressReadable>> m_gb1Shifts;
	
	@Override
	public void setUp( )
	throws Exception
	{
		Logging.Debug.init();
		
		// read the protein
		m_protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) );
		NameMapper.ensureProtein( m_protein, NameScheme.New );
		PseudoatomBuilder.getInstance().build( m_protein );
		m_sequences = m_protein.getSequences();
		m_subunit = m_protein.getSubunits().get( 0 );
		m_sequence = m_subunit.getSequence();
		
		// read the shifts
		m_gb1Shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.experimental.shift") ) );
		NameMapper.ensureAddresses( m_sequences, m_gb1Shifts, NameScheme.New );
		PseudoatomBuilder.getInstance().buildShifts( m_sequences, m_gb1Shifts );
	}
	
	public void testMapping( )
	throws Exception
	{
		// read the shifts
		List<ChemicalShift<AtomAddressReadable>> shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("chemical.shifts") ) );
		NameMapper.ensureAddresses( m_sequences, shifts, NameScheme.New );
		List<ChemicalShift<AtomAddressInternal>> mappedShifts = ChemicalShiftMapper.mapReadableToInternal( shifts, m_subunit, true );
		assertEquals( 4, mappedShifts.size() );
		
		// make sure all shifts are mapped
		assertEquals( shifts.size(), mappedShifts.size() );
		for( int i=0; i<shifts.size(); i++ )
		{
			assertNotNull( "Shift not mapped: " + shifts.get( i ).toString(), mappedShifts.get( i ) );
		}
		
		ChemicalShift<AtomAddressReadable> shift = null;
		ChemicalShift<AtomAddressInternal> mappedShift = null;
		
		// 43  4 LYS CE   C  41.8662 0.2  1 
		shift = shifts.get( 0 );
		mappedShift = mappedShifts.get( 0 );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 4, m_subunit.getResidue( mappedShift.getAddress() ).getNumber() );
		assertEquals( AminoAcid.Lysine, m_subunit.getResidue( mappedShift.getAddress() ).getAminoAcid() );
		assertEquals( "CE", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
		
		// 124 12 LEU HA   H   5.0068 0.02 1 
		shift = shifts.get( 1 );
		mappedShift = mappedShifts.get( 1 );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 12, m_subunit.getResidue( mappedShift.getAddress() ).getNumber() );
		assertEquals( AminoAcid.Leucine, m_subunit.getResidue( mappedShift.getAddress() ).getAminoAcid() );
		assertEquals( "HA", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
		
		// 422 45 TYR H    H   8.8528 0.02 1
		shift = shifts.get( 2 );
		mappedShift = mappedShifts.get( 2 );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 45, m_subunit.getResidue( mappedShift.getAddress() ).getNumber() );
		assertEquals( AminoAcid.Tyrosine, m_subunit.getResidue( mappedShift.getAddress() ).getAminoAcid() );
		assertEquals( "H", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
		
		// 466 50 LYS CG   C  24.5176 0.2  1 
		shift = shifts.get( 3 );
		mappedShift = mappedShifts.get( 3 );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 50, m_subunit.getResidue( mappedShift.getAddress() ).getNumber() );
		assertEquals( AminoAcid.Lysine, m_subunit.getResidue( mappedShift.getAddress() ).getAminoAcid() );
		assertEquals( "CG", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
	}
	
	public void testMappingPseudoatoms( )
	throws Exception
	{
		// read the shifts
		List<ChemicalShift<AtomAddressReadable>> shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("chemical.pseudo.shifts") ) );
		NameMapper.ensureAddresses( m_sequences, shifts, NameScheme.New );
		PseudoatomBuilder.getInstance().buildShifts( m_sequences, shifts );
		
		List<ChemicalShift<AtomAddressInternal>> mappedShifts = ChemicalShiftMapper.mapReadableToInternal( shifts, m_subunit, true );
		assertEquals( 2, mappedShifts.size() );
		
		ChemicalShift<AtomAddressReadable> shift = null;
		ChemicalShift<AtomAddressInternal> mappedShift = null;
		
		// spot check a couple addresses
		
		// 8  1 MET HE   H   2.1100 0.02 1
		// should map to HE# / me
		shift = shifts.get( 0 );
		mappedShift = mappedShifts.get( 0 ); 
		assertNotNull( mappedShift );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 1, m_subunit.getResidue( mappedShift.getAddress().getResidueId() ).getNumber() );
		assertEquals( AminoAcid.Methionine, m_subunit.getResidue( mappedShift.getAddress().getResidueId() ).getAminoAcid() );
		assertEquals( "ME", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
		
		// 130 12 LEU HD2  H   0.1190 0.02 2  
		// should map to HD2# / MD2
		shift = shifts.get( 1 );
		mappedShift = mappedShifts.get( 1 );
		assertNotNull( mappedShift );
		assertEquals( shift.getValue(), mappedShift.getValue() );
		assertEquals( shift.getError(), mappedShift.getError() );
		assertEquals( 12, m_subunit.getResidue( mappedShift.getAddress() ).getNumber() );
		assertEquals( AminoAcid.Leucine, m_subunit.getResidue( mappedShift.getAddress() ).getAminoAcid() );
		assertEquals( "MD2", m_subunit.getAtom( mappedShift.getAddress() ).getName() );
	}
	
	public void testLookup( )
	throws Exception
	{
		List<ChemicalShift<AtomAddressReadable>> hydrogenShifts = ChemicalShiftMapper.filter( m_gb1Shifts, Element.Hydrogen );
		TreeMap<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> lookup = ChemicalShiftMapper.buildShiftLookup( hydrogenShifts );
		assertNotNull( lookup.get( new AtomAddressReadable( 35, "HB3" ) ) );
		assertNotNull( lookup.get( new AtomAddressReadable( 24, "HA" ) ) );
		assertNotNull( lookup.get( new AtomAddressReadable( 10, "HG3" ) ) );
		assertNotNull( lookup.get( new AtomAddressReadable( 1, "ME" ) ) );
	}
	
	public void testAssociatePairs( )
	throws Exception
	{
		List<ChemicalShift<AtomAddressReadable>> hydrogenShifts = ChemicalShiftMapper.filter( m_gb1Shifts, Element.Hydrogen );
		List<ChemicalShift<AtomAddressReadable>> carbonShifts = ChemicalShiftMapper.filter( m_gb1Shifts, Element.Carbon );
		List<ChemicalShift<AtomAddressReadable>> nitrogenShifts = ChemicalShiftMapper.filter( m_gb1Shifts, Element.Nitrogen );
		
		List<ChemicalShiftPair<AtomAddressReadable>> carbonPairs = ChemicalShiftMapper.associatePairs(
			m_sequence,
			ChemicalShiftMapper.buildShiftLookup( hydrogenShifts ),
			ChemicalShiftMapper.buildShiftLookup( carbonShifts )
		);
		assertFalse( carbonPairs.isEmpty() );
		for( ChemicalShiftPair<AtomAddressReadable> pair : carbonPairs )
		{
			assertNotNull( pair );
			assertNotNull( pair.getHydrogenShift() );
			assertNotNull( pair.getHeavyShift() );
		}
		
		List<ChemicalShiftPair<AtomAddressReadable>> nitrogenPairs = ChemicalShiftMapper.associatePairs(
			m_sequence,
			ChemicalShiftMapper.buildShiftLookup( hydrogenShifts ),
			ChemicalShiftMapper.buildShiftLookup( nitrogenShifts )
		);
		assertFalse( nitrogenPairs.isEmpty() );
		for( ChemicalShiftPair<AtomAddressReadable> pair : nitrogenPairs )
		{
			assertNotNull( pair );
			assertNotNull( pair.getHydrogenShift() );
			assertNotNull( pair.getHeavyShift() );
		}
	}
}
