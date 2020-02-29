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
package edu.duke.cs.libprotnmr.bond;

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class TestBreadthFirstBondIterator extends ExtendedTestCase
{
	public void testEmpty( )
	throws Exception
	{
		// create an empty subunit
		Subunit subunit = new Subunit();
		
		// build the graph
		BondGraphBuilder builder = BondGraphBuilder.getInstance();
		BondGraph graph = builder.build( subunit );
		
		BreadthFirstBondIterator iter = new BreadthFirstBondIterator( graph, new AtomAddressInternal( 0, 0, 0 ) );
		assertFalse( iter.hasNext() );
	}
	
	public void testTriGlycine( )
	throws Exception
	{
		Subunit subunit = Util.newTriGlycine( 0 );
		
		// build the graph
		BondGraphBuilder builder = BondGraphBuilder.getInstance();
		BondGraph graph = builder.build( subunit );
		
		// start with 1:CA
		BreadthFirstBondIterator iter = new BreadthFirstBondIterator( graph, new AtomAddressInternal( 0, 1, 2 ) );
		
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "1:CA-1:HA2,1:CA-1:HA3,1:CA-1:C,1:CA-1:N" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "1:N-0:C,1:N-1:H,1:C-1:O,1:C-2:N" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:C-0:CA,0:C-0:O,2:N-2:H,2:N-2:CA" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:CA-0:N,0:CA-0:HA2,0:CA-0:HA3,2:CA-2:C,2:CA-2:HA2,2:CA-2:HA3" );
		assertTrue( iter.hasNext() );
		assertLevel( subunit, iter.next(), "0:N-0:H1,0:N-0:H2,0:N-0:H3,2:C-2:O,2:C-2:OXT" );
		assertFalse( iter.hasNext() );
	}
	
	private void assertLevel( Subunit subunit, ArrayList<Bond> bonds, String namesList )
	{
		int numMatched = 0;
		
		// split by bonds
		String[] bondNames = namesList.split( "," );
		for( String bondName : bondNames )
		{
			// split by endpoints
			String[] endpointNames = bondName.split( "-" );
			
			// handle left endpoint
			String[] parts = endpointNames[0].split( ":" );
			int leftResidueId = Integer.parseInt( parts[0] );
			String leftName = parts[1];
			
			// handle right endpoint
			parts = endpointNames[1].split( ":" );
			int rightResidueId = Integer.parseInt( parts[0] );
			String rightName = parts[1];
			
			// this bond must be in the level (in any order)
			boolean found = false;
			for( Bond bond : bonds )
			{
				AtomAddressInternal leftAddress = bond.getLeftAddress();
				AtomAddressInternal rightAddress = bond.getRightAddress();
				found =
					(
						leftAddress.getResidueId() == leftResidueId
						&& subunit.getAtom( leftAddress ).getName().equalsIgnoreCase( leftName )
						&& rightAddress.getResidueId() == rightResidueId
						&& subunit.getAtom( rightAddress ).getName().equalsIgnoreCase( rightName )
					)
					||
					(
						leftAddress.getResidueId() == rightResidueId
						&& subunit.getAtom( leftAddress ).getName().equalsIgnoreCase( rightName )
						&& rightAddress.getResidueId() == leftResidueId
						&& subunit.getAtom( rightAddress ).getName().equalsIgnoreCase( leftName )
					);
				if( found )
				{
					numMatched++;
					break;
				}
			}
			assertTrue( found );
		}
		
		assertEquals( bonds.size(), numMatched );
	}
}
