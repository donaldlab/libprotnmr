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
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class TestBreadthFirstIterator extends ExtendedTestCase
{
	public void testEmpty( )
	throws Exception
	{
		// create an empty subunit
		Subunit subunit = new Subunit();
		
		// build the graph
		BondGraphBuilder builder = BondGraphBuilder.getInstance();
		BondGraph graph = builder.build( subunit );
		
		// check our iterator
		BreadthFirstAtomIterator iter = new BreadthFirstAtomIterator( graph, new AtomAddressInternal( 0, 0, 0 ) );
		assertFalse( iter.hasNext() );
	}
	
	public void testTriGlycine( )
	throws Exception
	{
		// make a small subunit
		Subunit subunit = Util.newTriGlycine( 0 );
		
		// build the bond graph
		BondGraph graph = BondGraphBuilder.getInstance().build( subunit );
		
		// start with the 1:HA2 atom
		AtomAddressInternal address = new AtomAddressInternal( 0, 1, 3 );
		assertNotNull( subunit.getAtom( address ) );
		
		// check our iterator
		BreadthFirstAtomIterator iter = new BreadthFirstAtomIterator( graph, address );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "1:CA" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "1:N,1:HA3,1:C" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:C,1:H,1:O,2:N" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:CA,0:O,2:H,2:CA" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:N,0:HA2,0:HA3,2:HA2,2:HA3,2:C" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:H1,0:H2,0:H3,2:O,2:OXT" );
		assertFalse( iter.hasNext() );
	}
		
	private void assertLevel( Subunit subunit, ArrayList<AtomAddressInternal> addresses, String namesList )
	{
		int numMatched = 0;
		
		// parse the names
		String[] names = namesList.split( "," );
		for( String entry : names )
		{
			String[] parts = entry.split( ":" );
			int residueId = Integer.parseInt( parts[0] );
			String name = parts[1];
			
			// this residue:name must be in the level (in any order)
			boolean found = false;
			for( AtomAddressInternal address : addresses )
			{
				found =
					subunit.getAtom( address ).getName().equalsIgnoreCase( name )
					&& address.getResidueId() == residueId;
				if( found )
				{
					numMatched++;
					break;
				}
			}
			assertTrue( found );
		}
		
		assertEquals( addresses.size(), numMatched );
	}
}
