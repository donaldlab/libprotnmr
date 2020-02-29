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

import java.util.Set;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class TestDistanceRestraintResidueAtom extends ExtendedTestCase
{
	public void testConstructor( )
	{
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setMinDistance( 1.0 );
		restraint.setMaxDistance( 2.0 );
		restraint.setLefts( new AtomAddressInternal( 1, 2, 3 ) );
		restraint.setRights( new AtomAddressInternal( 6, 5, 4 ) );
		
		DistanceRestraintResidueAtom wrapper = new DistanceRestraintResidueAtom( restraint );
		assertEquals( restraint, wrapper );
		assertEquals( restraint, (DistanceRestraint<AtomAddressInternal>)wrapper );
	}
	
	public void testEquals( )
	{
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).equals(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ) )
		);
		
		// different subunits
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 5, 4 )
			) ).equals(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			) ) )
		);

		// swap sides
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).equals(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			) ) )
		);
		
		assertFalse(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).equals(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 6 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			) ) )
		);
		
		assertFalse(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).equals(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 5, 3 )
			) ) )
		);
	}
	
	public void testHashCodes( )
	{
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).hashCode() ==
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).hashCode()
		);
		
		// different subunits
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 5, 4 )
			) ).hashCode() ==
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			) ).hashCode()
		);

		// swap sides
		assertTrue(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).hashCode() ==
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			) ).hashCode()
		);
		
		assertFalse(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).hashCode() ==
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 6 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 2, 3 )
			) ).hashCode()
		);
		
		assertFalse(
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 6, 5, 4 )
			) ).hashCode() ==
			newWrapper( Transformer.toTreeSet(
				new AtomAddressInternal( 9, 5, 4 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 7, 5, 3 )
			) ).hashCode()
		);
	}

	private DistanceRestraintResidueAtom newWrapper( Set<AtomAddressInternal> lefts, Set<AtomAddressInternal> rights )
	{
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setMinDistance( 1.0 );
		restraint.setMaxDistance( 2.0 );
		restraint.setLefts( lefts );
		restraint.setRights( rights );
		return new DistanceRestraintResidueAtom( restraint );
	}
}
