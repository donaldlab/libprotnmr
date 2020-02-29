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

import java.util.ArrayList;
import java.util.Iterator;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestAssignmentIterator extends ExtendedTestCase
{
	private static final String ProteinPath = Resources.getPath("largeProtein.pdb");
	private static final String NoesPath = Resources.getPath("large.noe");
	
	public void testOneAssignment( )
	{
		// build endpoints
		AtomAddressInternal l1 = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressInternal r1 = new AtomAddressInternal( 7, 6, 5 );
		
		// build the restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( Transformer.toTreeSet( l1 ) );
		restraint.setRights( Transformer.toTreeSet( r1 ) );
		
		// get the iterator
		AssignmentIterator<AtomAddressInternal> iter = new AssignmentIterator<AtomAddressInternal>( restraint );
		
		// check it, yo
		assertNextRestraint( iter, l1, r1 );
		assertFalse( iter.hasNext() );
	}
	
	public void testTwoLeftAssignments( )
	{
		// build endpoints
		AtomAddressInternal l1 = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressInternal l2 = new AtomAddressInternal( 7, 6, 5 );
		AtomAddressInternal r1 = new AtomAddressInternal( 10, 11, 12 );
		
		// build the restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( Transformer.toTreeSet( l1, l2 ) );
		restraint.setRights( Transformer.toTreeSet( r1 ) );
		
		// get the iterator
		AssignmentIterator<AtomAddressInternal> iter = new AssignmentIterator<AtomAddressInternal>( restraint );
		
		// check it, yo
		assertNextRestraint( iter, l1, r1 );
		assertNextRestraint( iter, l2, r1 );
		assertFalse( iter.hasNext() );
	}
	
	public void testTwoRightAssignments( )
	{
		// build endpoints
		AtomAddressInternal l1 = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressInternal r1 = new AtomAddressInternal( 7, 6, 5 );
		AtomAddressInternal r2 = new AtomAddressInternal( 10, 11, 12 );
		
		// build the restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( Transformer.toTreeSet( l1 ) );
		restraint.setRights( Transformer.toTreeSet( r1, r2 ) );
		
		// get the iterator
		AssignmentIterator<AtomAddressInternal> iter = new AssignmentIterator<AtomAddressInternal>( restraint );
		
		// check it, yo
		assertNextRestraint( iter, l1, r1 );
		assertNextRestraint( iter, l1, r2 );
		assertFalse( iter.hasNext() );
	}
	
	public void testTwoRightTwoLeftAssignments( )
	{
		// build endpoints
		AtomAddressInternal l1 = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressInternal l2 = new AtomAddressInternal( 22,21, 20 );
		AtomAddressInternal r1 = new AtomAddressInternal( 7, 6, 5 );
		AtomAddressInternal r2 = new AtomAddressInternal( 10, 11, 12 );
		
		// build the restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( Transformer.toTreeSet( l1, l2 ) );
		restraint.setRights( Transformer.toTreeSet( r1, r2 ) );
		
		// get the iterator
		AssignmentIterator<AtomAddressInternal> iter = new AssignmentIterator<AtomAddressInternal>( restraint );
		
		// check it, yo
		assertNextRestraint( iter, l1, r1 );
		assertNextRestraint( iter, l1, r2 );
		assertNextRestraint( iter, l2, r1 );
		assertNextRestraint( iter, l2, r2 );
		assertFalse( iter.hasNext() );
	}
	
	public void testAllAssignments( )
	throws Exception
	{
		// load some distance restraints and a protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( ProteinPath ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		PseudoatomBuilder.getInstance().build( protein );
		ArrayList<DistanceRestraint<AtomAddressReadable>> readableRestraints = new DistanceRestraintReader().read( getClass().getResourceAsStream( NoesPath ) );
		DistanceRestraint.shiftResidueNumbersIfNeeded( readableRestraints, protein.getSequences() );
		PseudoatomBuilder.getInstance().buildDistanceRestraints( protein.getSequences(), readableRestraints );
		NameMapper.ensureAddresses( protein.getSequences(), readableRestraints, NameScheme.New );
		ArrayList<DistanceRestraint<AtomAddressInternal>> restraints = DistanceRestraintMapper.mapReadableToInternal( readableRestraints, protein, true );
		
		// for each restraint
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// for each assignment
			for( Assignment<AtomAddressInternal> assignment : restraint )
			{
				assertNotNull( assignment );
			}
		}
	}
	
	private void assertNextRestraint( Iterator<Assignment<AtomAddressInternal>> iter, AtomAddressInternal left, AtomAddressInternal right )
	{
		assertTrue( iter.hasNext() );
		Assignment<AtomAddressInternal> assignment = iter.next();
		assertEquals( left, assignment.getLeft() );
		assertEquals( right, assignment.getRight() );
	}
}
