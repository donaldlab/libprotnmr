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
package edu.duke.cs.libprotnmr.nmr;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.AssertionFailedError;
import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.HomoProtein;
import edu.duke.cs.libprotnmr.protein.HomoSequences;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.resources.Resources;

public class TestDistanceRestraintReassigner extends ExtendedTestCase
{
	final double HydrogenWindowWidth = 0.05; // in ppm
	final double CarbonWindowWidth = 0.5;
	final double NitrogenWindowWidth = 0.5;

	private Sequence m_sequence;
	private List<ChemicalShift<AtomAddressReadable>> m_hydrogenShifts;
	private List<ChemicalShift<AtomAddressReadable>> m_carbonShifts;
	private List<ChemicalShift<AtomAddressReadable>> m_nitrogenShifts;
	private List<DistanceRestraint<AtomAddressReadable>> m_restraints;
	
	@Override
	public void setUp( )
	throws Exception
	{
		// read the sequence
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		PseudoatomBuilder.getInstance().build( protein );
		m_sequence = protein.getSubunit( 0 ).getSequence();
		Sequences sequences = new HomoSequences( m_sequence );
		
		// read the shifts
		List<ChemicalShift<AtomAddressReadable>> shifts = new ChemicalShiftReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.experimental.shift") ) );
		NameMapper.ensureAddresses( sequences, shifts, NameScheme.New );
		PseudoatomBuilder.getInstance().buildShifts( sequences, shifts );
		
		// filter the shifts
		m_hydrogenShifts = ChemicalShiftMapper.filter( shifts, Arrays.asList( Element.Hydrogen, Element.PseudoatomLarge, Element.PseudoatomSmall ) );
		m_carbonShifts = ChemicalShiftMapper.filter( shifts, Element.Carbon );
		m_nitrogenShifts = ChemicalShiftMapper.filter( shifts, Element.Nitrogen );
		
		// read the NOEs
		HomoProtein homoProtein = new HomoProtein( protein.getSubunit( 0 ), "ABC" );
		List<DistanceRestraint<AtomAddressReadable>> noes = new DistanceRestraintReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.experimental.fixed.noe") ) );
		NameMapper.ensureAddresses( sequences, noes, NameScheme.New );
		PseudoatomBuilder.getInstance().buildDistanceRestraints( sequences, noes );
		List<DistanceRestraint<AtomAddressInternal>> mappedRestraints = DistanceRestraintMapper.mapReadableToInternal( noes, homoProtein );
		mappedRestraints = DistanceRestraintFilterer.pickIntersubunit( mappedRestraints );
		mappedRestraints = DistanceRestraintFilterer.pickSubunitEitherSide( mappedRestraints, 0 );
		mappedRestraints = DistanceRestraintFilterer.pickUnique( mappedRestraints );
		mappedRestraints = DistanceRestraintFilterer.pickOneFromSymmetricGroup( mappedRestraints );
		m_restraints = DistanceRestraintMapper.mapInternalToReadable( mappedRestraints, homoProtein );
	}
	
	public void testNoopReassignment1D( )
	throws Exception
	{
		// don't actually reassign any restraints
		assertSameRestraints(
			m_restraints,
			DistanceRestraintReassigner.reassign1D(
				m_sequence, m_restraints, m_hydrogenShifts,
				-1
			)
		);
	}
	
	public void testNoopReassignment2x2D( )
	throws Exception
	{
		// don't actually reassign any restraints
		assertSameRestraints(
			m_restraints,
			DistanceRestraintReassigner.reassignDouble2D(
				m_sequence, m_restraints, m_hydrogenShifts, m_carbonShifts, m_nitrogenShifts,
				-1, -1, -1
			)
		);
	}
	
	public void testNoExtraAssignmentsTwoMethylenes( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> restraint = new DistanceRestraint<AtomAddressReadable>();
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.0 );
		restraint.setLefts( new AtomAddressReadable( 'A', 1, "QB" ) );
		restraint.setRights( new AtomAddressReadable( 'B', 50, "QE" ) );
		reassignAssertNoExtraAssignments( restraint );
	}
	
	public void testNoExtraAssignmentsOneMeythl( )
	throws Exception
	{
		// NOTE: A:1:ME and A:6:HB have the exact same chemical shift
		DistanceRestraint<AtomAddressReadable> restraint = new DistanceRestraint<AtomAddressReadable>();
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.0 );
		restraint.setLefts( new AtomAddressReadable( 'A', 1, "ME" ), new AtomAddressReadable( 'A', 6, "HB" ) );
		restraint.setRights( new AtomAddressReadable( 'B', 50, "HA" ) );
		reassignAssertNoExtraAssignments( restraint );
	}
	
	public void testNoExtraAssignmentsTwoCombinedMethyls( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> restraint = new DistanceRestraint<AtomAddressReadable>();
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.0 );
		restraint.setLefts( new AtomAddressReadable( 'A', 5, "QG" ) );
		restraint.setRights( new AtomAddressReadable( 'B', 12, "QD" ) );
		reassignAssertNoExtraAssignments( restraint );
	}
	
	public void testReassignAssertOriginal( )
	throws Exception
	{
		reassignAssertOriginal( m_restraints );
	}
	
	public void testOriginalAssignmentsCombinedMethyls( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> restraint = new DistanceRestraint<AtomAddressReadable>();
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.0 );
		restraint.setLefts( new AtomAddressReadable( 'A', 5, "QG" ) );
		restraint.setRights( new AtomAddressReadable( 'B', 12, "QD" ) );
		reassignAssertOriginal( restraint );
	}
	
	public void testOriginalAssignmentsSurvived( )
	throws Exception
	{
		// test 1D reassignment
		assertOriginalAssignments(
			m_restraints,
			DistanceRestraintReassigner.reassign1D(
				m_sequence, m_restraints, m_hydrogenShifts,
				HydrogenWindowWidth
			)
		);
		
		// test 2x3D reassignment
		assertOriginalAssignments(
			m_restraints, 
			DistanceRestraintReassigner.reassignDouble2D(
				m_sequence, m_restraints, m_hydrogenShifts, m_carbonShifts, m_nitrogenShifts,
				HydrogenWindowWidth, CarbonWindowWidth, NitrogenWindowWidth
			)
		);
	}
	
	private void assertSameRestraints( List<DistanceRestraint<AtomAddressReadable>> expected, List<DistanceRestraint<AtomAddressReadable>> observed )
	{
		assertEquals( expected.size(), observed.size() );
		for( int i=0; i<expected.size(); i++ )
		{
			assertEquals( expected.get( i ), observed.get( i ) );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void reassignAssertNoExtraAssignments( DistanceRestraint<AtomAddressReadable> restraint )
	throws Exception
	{
		reassignAssertNoExtraAssignments( Arrays.asList( restraint ) );
	}
	
	private void reassignAssertNoExtraAssignments( List<DistanceRestraint<AtomAddressReadable>> restraints )
	throws Exception
	{
		// test 1D reassignment
		assertSameRestraints(
			restraints,
			DistanceRestraintReassigner.reassign1D(
				m_sequence, restraints, m_hydrogenShifts,
				-1
			)
		);
		
		// test 2x2D reassignment
		assertSameRestraints(
			restraints,
			DistanceRestraintReassigner.reassignDouble2D(
				m_sequence, restraints, m_hydrogenShifts, m_carbonShifts, m_nitrogenShifts,
				-1, -1, -1
			)
		);
	}
	
	private void assertOriginalAssignments( List<DistanceRestraint<AtomAddressReadable>> original, List<DistanceRestraint<AtomAddressReadable>> reassigned )
	{
		assertEquals( original.size(), reassigned.size() );
		for( int i=0; i<original.size(); i++ )
		{
			assertOriginalAssignments( original.get( i ), reassigned.get( i ) );
		}
	}
	
	private void assertOriginalAssignments( DistanceRestraint<AtomAddressReadable> original, DistanceRestraint<AtomAddressReadable> reassigned )
	{
		try
		{
			assertOriginalAssignments( original.getLefts(), reassigned.getLefts() );
			assertOriginalAssignments( original.getRights(), reassigned.getRights() );
		}
		catch( AssertionFailedError err )
		
		{
			throw new AssertionFailedError( "Original NOE assignments missing!! original<" + original + "> reassigned<" + reassigned + ">" );
		}
	}
	
	private void assertOriginalAssignments( Set<AtomAddressReadable> original, Set<AtomAddressReadable> reassigned )
	{
		for( AtomAddressReadable address : original )
		{
			assertTrue( reassigned.contains( address ) );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private void reassignAssertOriginal( DistanceRestraint<AtomAddressReadable> restraint )
	throws Exception
	{
		reassignAssertOriginal( Arrays.asList( restraint ) );
	}
	
	private void reassignAssertOriginal( List<DistanceRestraint<AtomAddressReadable>> restraints )
	throws Exception
	{
		// test 1D reassignment
		assertOriginalAssignments(
			restraints,
			DistanceRestraintReassigner.reassign1D(
				m_sequence, restraints, m_hydrogenShifts,
				HydrogenWindowWidth
			)
		);
		
		// test 2x3D reassignment
		assertOriginalAssignments(
			restraints,
			DistanceRestraintReassigner.reassignDouble2D(
				m_sequence, restraints, m_hydrogenShifts, m_carbonShifts, m_nitrogenShifts,
				HydrogenWindowWidth, CarbonWindowWidth, NitrogenWindowWidth
			)
		);
	}
}
