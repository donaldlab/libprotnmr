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
package edu.duke.cs.libprotnmr.mapping;

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraintReader;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestNameMapper extends ExtendedTestCase
{
	public void testOldToNew( )
	throws Exception
	{
		NameMapper mapper = new NameMapper( NameScheme.Old, NameScheme.New );
		
		// just test some of the mappings
		// Ideally, we should test them all, but then we'd have to update this test every time
		// we added a mapping. It doesn't make sense to maintain this list twice.
		assertEquals( "H", mapper.mapName( AminoAcid.Leucine, ResidueType.NonTerminal, "HN" ) );
		
		assertEquals( "HB2", mapper.mapName( AminoAcid.Asparagine, ResidueType.NonTerminal, "HB1" ) );
		assertEquals( "HB3", mapper.mapName( AminoAcid.Asparagine, ResidueType.NonTerminal, "HB2" ) );
		
		assertEquals( "HB2", mapper.mapName( AminoAcid.Arginine, ResidueType.NonTerminal, "HB1" ) );
		assertEquals( "HB3", mapper.mapName( AminoAcid.Arginine, ResidueType.NonTerminal, "HB2" ) );
		
		// UNDONE: test some terminal mappings
	}
	
	public void testNewToOld( )
	throws Exception
	{
		NameMapper mapper = new NameMapper( NameScheme.New, NameScheme.Old );
		
		assertEquals( "HN", mapper.mapName( AminoAcid.Leucine, ResidueType.NonTerminal, "H" ) );
		
		assertEquals( "HB1", mapper.mapName( AminoAcid.Asparagine, ResidueType.NonTerminal, "HB2" ) );
		assertEquals( "HB2", mapper.mapName( AminoAcid.Asparagine, ResidueType.NonTerminal, "HB3" ) );
		
		assertEquals( "HB1", mapper.mapName( AminoAcid.Arginine, ResidueType.NonTerminal, "HB2" ) );
		assertEquals( "HB2", mapper.mapName( AminoAcid.Arginine, ResidueType.NonTerminal, "HB3" ) );
		
		// UNDONE: test some terminal mappings
	}
	
	public void testRealProtein( )
	throws Exception
	{
		// read in the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		
		// set some atoms to the old names
		for( int i=0; i<protein.getSubunits().size(); i++ )
		{
			protein.getAtom( i, 323-319, 8 ).setName( "HN" );
			protein.getAtom( i, 358-319, 9 ).setName( "HN" );
		}
		
		// check the atoms
		for( int i=0; i<protein.getSubunits().size(); i++ )
		{
			assertAtom( "HN", protein, i, 323-319, 8 );
			assertAtom( "HN", protein, i, 358-319, 9 );
		}
		
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		// check the mapped atoms
		for( int i=0; i<protein.getSubunits().size(); i++ )
		{
			assertAtom( "H", protein, i, 323-319, 8 );
			assertAtom( "H", protein, i, 358-319, 9 );
		}
	}

	private void assertAtom( String atomName, Protein protein, int subunitId, int residueId, int atomId )
	{
		assertEquals( atomName, protein.getAtom( subunitId, residueId, atomId ).getName() );
	}
	
	public void testDistanceRestraints( )
	throws Exception
	{
		// read in the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		/*
		 * [DistanceRestraint] 1.7999999999999998,8.0	(A:325:HA1)	(C:333:HD#)
[DistanceRestraint] 1.7999999999999998,8.0	(C:325:HA1)	(A:333:HD#)
[DistanceRestraint] 1.7999999999999998,6.0	(A:326:HN)	(C:334:HN)
[DistanceRestraint] 1.7999999999999998,6.0	(C:326:HN)	(A:334:HN)
[DistanceRestraint] 1.8,3.5	(A:327:HA)	(C:334:HN)
[DistanceRestraint] 1.7999999999999998,5.0	(C:327:HA)	(A:333:HN)
[DistanceRestraint] 1.7999999999999998,5.0	(C:327:HA)	(A:333:HN)
[DistanceRestraint] 1.8,3.5	(C:327:HA)	(A:334:HN)
		 */
		
		DistanceRestraint<AtomAddressReadable> noe = new DistanceRestraint<AtomAddressReadable>();
		noe.setLefts( new AtomAddressReadable( 'A', 326, "HN" ) );
		noe.setRights( new AtomAddressReadable( 'C', 334, "HN" ) );
		
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = new ArrayList<DistanceRestraint<AtomAddressReadable>>();
		noes.add( noe );
		NameMapper.ensureAddresses( protein.getSequences(), noes, NameScheme.New );
		
		assertAtomNames( "H", "H", noe );
	}

	public void testRealDistanceRestraints( )
	throws Exception
	{
		// read in the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		ArrayList<DistanceRestraint<AtomAddressReadable>> readableRestraints = new DistanceRestraintReader().read( getClass().getResourceAsStream( Resources.getPath("large.noe") ) );
		
		// HACKHACK: these NOEs are sadly all named with the wrong residue numbers, so fix them
		for( DistanceRestraint<AtomAddressReadable> noe : readableRestraints )
		{
			for( AtomAddressReadable address : noe.getLefts() )
			{
				address.setResidueNumber( address.getResidueNumber() + protein.getSubunit( address.getSubunitName() ).getFirstResidueNumber() - 1 );
			}
			for( AtomAddressReadable address : noe.getRights() )
			{
				address.setResidueNumber( address.getResidueNumber() + protein.getSubunit( address.getSubunitName() ).getFirstResidueNumber() - 1 );
			}
		}
		
		NameMapper.ensureAddresses( protein.getSequences(), readableRestraints, NameScheme.New );
		
		// restraint 4: assign (resid 8 and name hn and segid A)(resid 16 and name hn and segid C) 4.0 2.2 2.0
		assertAtomNames( "H", "H", readableRestraints.get( 4 ) );
	}
	
	public void testUnmappableAddress( )
	throws Exception
	{
		// This should not crash. Instead, just fail gracefully.
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		Sequences sequences = protein.getSequences();
		NameMapperProvider mapper = NameMapperProvider.getInstance();
		
		// this atom should exist
		mapper.mapAddress( NameScheme.Old, NameScheme.New, sequences, new AtomAddressReadable( 'A', 319, "H" ) );
		
		// this atom shouldn't
		mapper.mapAddress( NameScheme.Old, NameScheme.New, sequences, new AtomAddressReadable( 'A', 319, "X" ) );
		
		// this residue shouldn't
		mapper.mapAddress( NameScheme.Old, NameScheme.New, sequences, new AtomAddressReadable( 'A', 4096, "H" ) );
		
		// this subunit shouldn't
		mapper.mapAddress( NameScheme.Old, NameScheme.New, sequences, new AtomAddressReadable( 'X', 319, "H" ) );
	}

	private void assertAtomNames( String leftName, String rightName, DistanceRestraint<AtomAddressReadable> restraint )
	{
		assertEquals( 1, restraint.getLefts().size() );
		assertEquals( 1, restraint.getRights().size() );
		assertEquals( leftName.toUpperCase(), restraint.getLefts().iterator().next().getAtomName().toUpperCase() );
		assertEquals( rightName.toUpperCase(), restraint.getRights().iterator().next().getAtomName().toUpperCase() );
	}
}
