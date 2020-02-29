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

import java.io.IOException;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class TestBackboneAtomAddresserPairIterator extends ExtendedTestCase
{
	public void testSingleSubunit( )
	throws IOException
	{
		// make a small protein
		Protein protein = new Protein();
		ArrayList<Subunit> subunits = new ArrayList<Subunit>();
		subunits.add( Util.newTriGlycine( 0 ) );
		protein.setSubunits( subunits );
		
		// get the bond graph
		ArrayList<BondGraph> bondGraphs = BondGraphBuilder.getInstance().build( protein );
		
		// get our iterator
		BackboneAtomAddressPairIterator iter = new BackboneAtomAddressPairIterator( protein, bondGraphs, 3 );
		
		// check each pair
		assertAtomPairList( protein, iter, "0:0:N", "0:1:CA,0:1:C,0:2:N,0:2:CA,0:2:C" );
		assertAtomPairList( protein, iter, "0:0:CA", "0:1:C,0:2:N,0:2:CA,0:2:C" );
		assertAtomPairList( protein, iter, "0:0:C", "0:2:N,0:2:CA,0:2:C" );
		assertAtomPairList( protein, iter, "0:1:N", "0:2:CA,0:2:C" );
		assertAtomPairList( protein, iter, "0:1:CA", "0:0:N,0:2:C" );
		assertAtomPairList( protein, iter, "0:1:C", "0:0:CA,0:0:N" );
		assertAtomPairList( protein, iter, "0:2:N", "0:0:C,0:0:CA,0:0:N" );
		assertAtomPairList( protein, iter, "0:2:CA", "0:1:N,0:0:C,0:0:CA,0:0:N" );
		assertAtomPairList( protein, iter, "0:2:C", "0:1:CA,0:1:N,0:0:C,0:0:CA,0:0:N" );
		
		assertFalse( iter.hasNext() );
	}
	
	public void testTwoSubunits( )
	throws IOException
	{
		// make a small protein
		Protein protein = new Protein();
		ArrayList<Subunit> subunits = new ArrayList<Subunit>();
		subunits.add( Util.newTriGlycine( 0 ) );
		subunits.add( Util.newTriGlycine( 1 ) );
		protein.setSubunits( subunits );
		
		// get the bond graph
		ArrayList<BondGraph> bondGraphs = BondGraphBuilder.getInstance().build( protein );
		
		// get our iterator
		BackboneAtomAddressPairIterator iter = new BackboneAtomAddressPairIterator( protein, bondGraphs, 3 );

		// check each pair for left atoms on subunit 0
		final String subunit1FullList = "1:0:N,1:0:CA,1:0:C,1:1:N,1:1:CA,1:1:C,1:2:N,1:2:CA,1:2:C";
		assertAtomPairList( protein, iter, "0:0:N", "0:1:CA,0:1:C,0:2:N,0:2:CA,0:2:C," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:0:CA", "0:1:C,0:2:N,0:2:CA,0:2:C," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:0:C", "0:2:N,0:2:CA,0:2:C," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:1:N", "0:2:CA,0:2:C," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:1:CA", "0:0:N,0:2:C," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:1:C", "0:0:CA,0:0:N," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:2:N", "0:0:C,0:0:CA,0:0:N," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:2:CA", "0:1:N,0:0:C,0:0:CA,0:0:N," + subunit1FullList );
		assertAtomPairList( protein, iter, "0:2:C", "0:1:CA,0:1:N,0:0:C,0:0:CA,0:0:N," + subunit1FullList );
		
		// check each pair for left atoms on subunit 1
		final String subunit0FullList = "0:0:N,0:0:CA,0:0:C,0:1:N,0:1:CA,0:1:C,0:2:N,0:2:CA,0:2:C";
		assertAtomPairList( protein, iter, "1:0:N", subunit0FullList + ",1:1:CA,1:1:C,1:2:N,1:2:CA,1:2:C" );
		assertAtomPairList( protein, iter, "1:0:CA", subunit0FullList + ",1:1:C,1:2:N,1:2:CA,1:2:C" );
		assertAtomPairList( protein, iter, "1:0:C", subunit0FullList + ",1:2:N,1:2:CA,1:2:C" );
		assertAtomPairList( protein, iter, "1:1:N", subunit0FullList + ",1:2:CA,1:2:C" );
		assertAtomPairList( protein, iter, "1:1:CA", subunit0FullList + ",1:0:N,1:2:C" );
		assertAtomPairList( protein, iter, "1:1:C", subunit0FullList + ",1:0:CA,1:0:N" );
		assertAtomPairList( protein, iter, "1:2:N", subunit0FullList + ",1:0:C,1:0:CA,1:0:N" );
		assertAtomPairList( protein, iter, "1:2:CA", subunit0FullList + ",1:1:N,1:0:C,1:0:CA,1:0:N" );
		assertAtomPairList( protein, iter, "1:2:C", subunit0FullList + ",1:1:CA,1:1:N,1:0:C,1:0:CA,1:0:N" );
		
		assertFalse( iter.hasNext() );
	}
	
	private void assertAtomPairList( Protein protein, BackboneAtomAddressPairIterator iter, String leftDesc, String listDesc )
	{
		// split up the list
		String[] rightDescs = listDesc.split( "," );
		for( String rightDesc : rightDescs )
		{
			assertAtomPair( protein, iter, leftDesc, rightDesc );
		}
	}
	
	private void assertAtomPair( Protein protein, BackboneAtomAddressPairIterator iter, String leftDesc, String rightDesc )
	{
		// process the description
		String[] leftParts = leftDesc.split( ":" );
		String[] rightParts = rightDesc.split( ":" );
		
		// get expected values
		int expectedLeftSubunitId = Integer.parseInt( leftParts[0] );
		int expectedRightSubunitId = Integer.parseInt( rightParts[0] );
		int expectedLeftResidueId =  Integer.parseInt( leftParts[1] );
		int expectedRightResidueId = Integer.parseInt( rightParts[1] );
		String expectedLeftName = leftParts[2];
		String expectedRightName = rightParts[2];
		
		// get the next pair
		assertTrue( iter.hasNext() );
		AtomAddressPair pair = iter.next();
		
		// get observed values
		int observedLeftSubunitId = pair.left.getSubunitId();
		int observedRightSubunitId = pair.right.getSubunitId();
		int observedLeftResidueId =  pair.left.getResidueId();
		int observedRightResidueId = pair.right.getResidueId();
		String observedLeftName = protein.getAtom( pair.left ).getName();
		String observedRightName = protein.getAtom( pair.right ).getName();
		
		boolean same =
			expectedLeftSubunitId == observedLeftSubunitId
			&& expectedLeftResidueId == observedLeftResidueId
			&& expectedLeftName.equals( observedLeftName )
			&& expectedRightSubunitId == observedRightSubunitId
			&& expectedRightResidueId == observedRightResidueId
			&& expectedRightName.equals( observedRightName );
		if( !same )
		{
			fail( String.format(
				"AtomAddresser pairs not the same! expected=[%d:%d:%s,%d:%d:%s] observed=[%d:%d:%s,%d:%d:%s]",
				expectedLeftSubunitId, expectedLeftResidueId, expectedLeftName,
				expectedRightSubunitId, expectedRightResidueId, expectedRightName,
				observedLeftSubunitId, observedLeftResidueId, observedLeftName,
				observedRightSubunitId, observedRightResidueId, observedRightName
			) );
		}
	}
}
