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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;

public class TestAddressMapper extends ExtendedTestCase
{
	public void testProteinReadableToInternal( )
	throws Exception
	{
		// read in the protein
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		
		// map some addresses
		assertProteinReadableToInternal( protein, 0, 8, 13, 'A', 327, "HA" );
		assertProteinReadableToInternal( protein, 2, 15, 4, 'C', 334, "H" );
	}
	
	private void assertProteinReadableToInternal( Protein protein, int subunitId, int residueId, int atomId, char subunitName, int residueNumber, String atomName )
	{
		AtomAddressInternal internal = AddressMapper.mapAddressExpandPseudoatoms( protein, new AtomAddressReadable( subunitName, residueNumber, atomName ) ).get( 0 );
		assertEquals( subunitId, internal.getSubunitId() );
		assertEquals( residueId, internal.getResidueId() );
		assertEquals( atomId, internal.getAtomId() );
	}
}
