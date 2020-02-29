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


public class TestDistanceRestraintMapper extends ExtendedTestCase
{
	public void testNoeToRestraintAtoms( )
	throws Exception
	{
		ProteinReader proteinReader = new ProteinReader();
		Protein protein = proteinReader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		DistanceRestraintReader noeReader = new DistanceRestraintReader();
		ArrayList<DistanceRestraint<AtomAddressReadable>> readableRestraints = noeReader.read( getClass().getResourceAsStream( Resources.getPath("large.noe") ) );
		DistanceRestraint.shiftResidueNumbersIfNeeded( readableRestraints, protein.getSequences() );
		NameMapper.ensureAddresses( protein.getSequences(), readableRestraints, NameScheme.New );
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> restraints = DistanceRestraintMapper.mapReadableToInternal( readableRestraints, protein, true );
		
		// make sure we have the right number of restraints
		assertEquals( readableRestraints.size(), restraints.size() );
		
		// for each restraint...
		Iterator<DistanceRestraint<AtomAddressInternal>> iterRestraint = restraints.iterator();
		while( iterRestraint.hasNext() )
		{
			DistanceRestraint<AtomAddressInternal> restraint = iterRestraint.next();
			
			// skip null restraints
			if( restraint == null )
			{
				continue;
			}
			
			// make sure the atoms exist
			for( AtomAddressInternal endpoint : restraint.getLefts() )
			{
				assertNotNull( protein.getAtom( endpoint ) );
			}
			for( AtomAddressInternal endpoint : restraint.getRights() )
			{
				assertNotNull( protein.getAtom( endpoint ) );
			}
		}
	}
	
	public void testUnambiguousRestraintToNoe( )
	throws Exception
	{
		// read in stuff
		ProteinReader proteinReader = new ProteinReader();
		Protein protein = proteinReader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		
		// build our restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( new AtomAddressInternal( 1, 11, 12 ) );
		restraint.setRights( new AtomAddressInternal( 3, 13, 16 ) );
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.5 );
		ArrayList<DistanceRestraint<AtomAddressInternal>> restraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		restraints.add( restraint );
		
		// get our noes
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = DistanceRestraintMapper.mapInternalToReadable( restraints, protein );
		assertEquals( 1, noes.size() );
		
		// check the NOE, should be B:12:hg D:14:hd11 1.8 0.0 5.7
		DistanceRestraint<AtomAddressReadable> noe = noes.get( 0 );
		assertNotNull( noe );
		int firstResidueNumberm = protein.getSubunit( 0 ).getFirstResidueNumber() - 1;
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'B', firstResidueNumberm + 12, "hg" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'D', firstResidueNumberm + 14, "hd11" )
		), noe.getRights() );
		assertEquals( 1.8, noe.getMinDistance() );
		assertEquals( 7.5, noe.getMaxDistance() );
	}

	public void testAmbiguousRestraintToNoe( )
	throws Exception
	{
		// read in stuff
		ProteinReader proteinReader = new ProteinReader();
		Protein protein = proteinReader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		PseudoatomBuilder.getInstance().build( protein );
		
		// build our restraint
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts(
			new AtomAddressInternal( 1, 11, 10),
			new AtomAddressInternal( 1, 11, 11 )
		);
		restraint.setRights( new AtomAddressInternal( 3, 13, 16 ) );
		restraint.setMinDistance( 1.8 );
		restraint.setMaxDistance( 7.5 );
		ArrayList<DistanceRestraint<AtomAddressInternal>> restraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		restraints.add( restraint );
		
		// get our noes
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = DistanceRestraintMapper.mapInternalToReadable( restraints, protein, true );
		assertEquals( 1, noes.size() );
		
		// check the NOE, should be (B:12:qb) D:14:hd11 1.8 0.0 5.7
		DistanceRestraint<AtomAddressReadable> noe = noes.get( 0 );
		assertNotNull( noe );
		int firstResidueNumberm = protein.getSubunit( 0 ).getFirstResidueNumber() - 1;
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'B', firstResidueNumberm + 12, "qb" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'D', firstResidueNumberm + 14, "hd11" )
		), noe.getRights() );
		assertEquals( 1.8, noe.getMinDistance() );
		assertEquals( 7.5, noe.getMaxDistance() );
	}
	
	public void testMappingCycle( )
	throws Exception
	{
		/* Jeff: 6/09/2008
			The idea here is that we should be able to map back and forth from noes to
			distance restraints without losing any information.
		*/
		
		// read in stuff
		ProteinReader proteinReader = new ProteinReader();
		Protein protein = proteinReader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		PseudoatomBuilder.getInstance().build( protein );
		
		DistanceRestraintReader noeReader = new DistanceRestraintReader();
		ArrayList<DistanceRestraint<AtomAddressReadable>> readableNoes = noeReader.read( getClass().getResourceAsStream( Resources.getPath("large.noe") ) );
		DistanceRestraint.shiftResidueNumbersIfNeeded( readableNoes, protein.getSequences() );
		NameMapper.ensureAddresses( protein.getSequences(), readableNoes, NameScheme.New );
		PseudoatomBuilder.getInstance().buildDistanceRestraints( protein.getSequences(), readableNoes );
		
		// map to distance restraints
		ArrayList<DistanceRestraint<AtomAddressInternal>> internalNoes = DistanceRestraintMapper.mapReadableToInternal( readableNoes, protein, true );
		assertEquals( readableNoes.size(), internalNoes.size() );
		
		for( int i=0; i<readableNoes.size(); i++ )
		{
			if( internalNoes.get( i ) == null )
			{
				fail( "Restraint failed to map: " + readableNoes.get( i ).toString() );
			}
		}
		
		// map back to noes
		ArrayList<DistanceRestraint<AtomAddressReadable>> readableNoesAgain = DistanceRestraintMapper.mapInternalToReadable( internalNoes, protein, true );
		
		// and map back to distance restraints again
		ArrayList<DistanceRestraint<AtomAddressInternal>> internalNoesAgain = DistanceRestraintMapper.mapReadableToInternal( readableNoesAgain, protein, true );
		
		// now make sure our restraints match!
		assertEquals( internalNoes.size(), internalNoesAgain.size() );
		for( int i=0; i<internalNoes.size(); i++ )
		{
			assertEquals( internalNoes.get( i ), internalNoesAgain.get( i ) );
		}
	}	
}
