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
