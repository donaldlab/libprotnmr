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


public class TestDistanceRestraint extends ExtendedTestCase
{
	public void testCopyConstructor( )
	{
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setMinDistance( 1.0 );
		restraint.setMaxDistance( 2.0 );
		restraint.setLefts( new AtomAddressInternal( 1, 2, 3 ) );
		restraint.setRights( new AtomAddressInternal( 6, 5, 4 ) );
		
		DistanceRestraint<AtomAddressInternal> newRestraint = new DistanceRestraint<AtomAddressInternal>( restraint );
		assertNotSame( restraint, newRestraint );
		assertEquals( restraint, newRestraint );
	}
	
	public void testEquals( )
	{
		DistanceRestraint<AtomAddressInternal> a = new DistanceRestraint<AtomAddressInternal>();
		a.setMinDistance( 1.0 );
		a.setMaxDistance( 2.0 );
		a.setLefts( new AtomAddressInternal( 1, 2, 3 ) );
		a.setRights( new AtomAddressInternal( 6, 5, 4 ) );
		
		DistanceRestraint<AtomAddressInternal> b = new DistanceRestraint<AtomAddressInternal>();
		b.setMinDistance( 1.0 );
		b.setMaxDistance( 2.0 );
		b.setLefts( new AtomAddressInternal( 1, 2, 3 ) );
		b.setRights( new AtomAddressInternal( 6, 5, 4 ) );
		
		assertNotSame( a, b );
		assertTrue( a.equals( b ) );
		assertTrue( b.equals( a ) );
		
		b.setMaxDistance( 3.0 );
		
		assertFalse( a.equals( b ) );
		assertFalse( b.equals( a ) );
	}
	
	public void testEqualsAddresses( )
	{
		assertTrue(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 2, 3 )
			) ).equals(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 2, 3 )
			) ) )
		);

		// swap sides
		assertTrue(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 2, 3 )
			) ).equals(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 2, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			) ) )
		);
		
		assertFalse(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 2, 2, 3 )
			) ).equals(
			newRestraint( Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			), Transformer.toTreeSet(
				new AtomAddressInternal( 1, 2, 3 )
			) ) )
		);
	}
	
	private DistanceRestraint<AtomAddressInternal> newRestraint( Set<AtomAddressInternal> lefts, Set<AtomAddressInternal> rights )
	{
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setMinDistance( 1.0 );
		restraint.setMaxDistance( 2.0 );
		restraint.setLefts( lefts );
		restraint.setRights( rights );
		return restraint;
	}
}
