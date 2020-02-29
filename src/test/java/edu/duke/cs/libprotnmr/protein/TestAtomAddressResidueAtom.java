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

package edu.duke.cs.libprotnmr.protein;

import edu.duke.cs.libprotnmr.ExtendedTestCase;

public class TestAtomAddressResidueAtom extends ExtendedTestCase
{
	public void testConstructor( )
	{
		AtomAddressInternal address = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressResidueAtom wrapper = new AtomAddressResidueAtom( address );
		
		assertEquals( address, wrapper );
		assertEquals( address, wrapper.getAtomAddress() );
	}
	
	public void testEquals( )
	{
		assertTrue( newAddress( 1, 2, 3 ).equals( newAddress( 1, 2, 3 ) ) );
		assertTrue( newAddress( 1, 2, 3 ).equals( newAddress( 2, 2, 3 ) ) );
		assertTrue( newAddress( 2, 2, 3 ).equals( newAddress( 1, 2, 3 ) ) );
		
		assertFalse( newAddress( 1, 2, 3 ).equals( newAddress( 1, 3, 3 ) ) );
		assertFalse( newAddress( 1, 2, 3 ).equals( newAddress( 2, 3, 3 ) ) );
		assertFalse( newAddress( 1, 2, 3 ).equals( newAddress( 1, 2, 4 ) ) );
		assertFalse( newAddress( 1, 2, 3 ).equals( newAddress( 2, 2, 4 ) ) );
	}
	
	public void testHashCode( )
	{
		assertTrue( newAddress( 1, 2, 3 ).hashCode() == newAddress( 1, 2, 3 ).hashCode() );
		assertTrue( newAddress( 1, 2, 3 ).hashCode() == newAddress( 2, 2, 3 ).hashCode() );
		
		assertFalse( newAddress( 1, 2, 3 ).hashCode() == newAddress( 1, 3, 3 ).hashCode() );
		assertFalse( newAddress( 1, 2, 3 ).hashCode() == newAddress( 2, 3, 3 ).hashCode() );
		assertFalse( newAddress( 1, 2, 3 ).hashCode() == newAddress( 1, 2, 4 ).hashCode() );
		assertFalse( newAddress( 1, 2, 3 ).hashCode() == newAddress( 2, 2, 4 ).hashCode() );
	}
	
	private AtomAddressResidueAtom newAddress( int subunitId, int residueId, int atomId )
	{
		return new AtomAddressResidueAtom( new AtomAddressInternal( subunitId, residueId, atomId ) );
	}
}
