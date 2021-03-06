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
import edu.duke.cs.libprotnmr.io.Transformer;

public class TestAtomAddress extends ExtendedTestCase
{
	public void testConstructor( )
	{
		AtomAddressInternal address = new AtomAddressInternal( 1, 2, 3 );
		
		assertEquals( 1, address.getSubunitId() );
		assertEquals( 2, address.getResidueId() );
		assertEquals( 3, address.getAtomId() );
	}
	
	public void testCopyConstructor( )
	{
		AtomAddressInternal address = new AtomAddressInternal( 1, 2, 3 );
		AtomAddressInternal address2 = new AtomAddressInternal( address );
		
		assertNotSame( address, address2 );
		assertEquals( 1, address2.getSubunitId() );
		assertEquals( 2, address2.getResidueId() );
		assertEquals( 3, address2.getAtomId() );
	}
	
	public void testCompareTo( )
	{
		assertTrue( new AtomAddressInternal( 0, 0, 0 ).compareTo( new AtomAddressInternal( 0, 0, 0 ) ) == 0 );
		assertTrue( new AtomAddressInternal( 1, 2, 3 ).compareTo( new AtomAddressInternal( 1, 2, 3 ) ) == 0 );
		
		assertTrue( new AtomAddressInternal( 1, 2, 3 ).compareTo( new AtomAddressInternal( 1, 2, 2 ) ) > 0 );
		assertTrue( new AtomAddressInternal( 1, 3, 1 ).compareTo( new AtomAddressInternal( 1, 2, 1 ) ) > 0 );
		assertTrue( new AtomAddressInternal( 2, 1, 1 ).compareTo( new AtomAddressInternal( 1, 1, 1 ) ) > 0 );
		
		assertTrue( new AtomAddressInternal( 1, 2, 2 ).compareTo( new AtomAddressInternal( 1, 2, 3 ) ) < 0 );
		assertTrue( new AtomAddressInternal( 1, 2, 1 ).compareTo( new AtomAddressInternal( 1, 3, 1 ) ) < 0 );
		assertTrue( new AtomAddressInternal( 1, 1, 1 ).compareTo( new AtomAddressInternal( 2, 1, 1 ) ) < 0 );
	}
	
	public void testEquals( )
	{
		assertTrue( new AtomAddressInternal( 0, 0, 0 ).equals( new AtomAddressInternal( 0, 0, 0 ) ) );
		assertTrue( new AtomAddressInternal( 1, 2, 3 ).equals( new AtomAddressInternal( 1, 2, 3 ) ) );
		
		assertFalse( new AtomAddressInternal( 1, 2, 2 ).equals( new AtomAddressInternal( 1, 2, 3 ) ) );
		assertFalse( new AtomAddressInternal( 1, 1, 3 ).equals( new AtomAddressInternal( 1, 2, 3 ) ) );
		assertFalse( new AtomAddressInternal( 0, 2, 3 ).equals( new AtomAddressInternal( 1, 2, 3 ) ) );
	}
	
	public void testHashCode( )
	{
		assertTrue( new AtomAddressInternal( 0, 0, 0 ).hashCode() == new AtomAddressInternal( 0, 0, 0 ).hashCode() );
		assertTrue( new AtomAddressInternal( 1, 2, 3 ).hashCode() == new AtomAddressInternal( 1, 2, 3 ).hashCode() );
		
		assertFalse( new AtomAddressInternal( 1, 2, 2 ).hashCode() == new AtomAddressInternal( 1, 2, 3 ).hashCode() );
		assertFalse( new AtomAddressInternal( 1, 1, 3 ).hashCode() == new AtomAddressInternal( 1, 2, 3 ).hashCode() );
		assertFalse( new AtomAddressInternal( 0, 2, 3 ).hashCode() == new AtomAddressInternal( 1, 2, 3 ).hashCode() );
	}
	
	public void testSetEquals( )
	{
		assertTrue( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 )
		).equals( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 )
		) ) );
		
		assertTrue( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 ),
			new AtomAddressInternal( 0, 0, 1 )
		).equals( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 ),
			new AtomAddressInternal( 0, 0, 1 )
		) ) );
		
		assertTrue( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 ),
			new AtomAddressInternal( 0, 0, 1 )
		).equals( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 1 ),
			new AtomAddressInternal( 0, 0, 0 )
		) ) );
		
		assertFalse( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 0 )
		).equals( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 1 )
		) ) );
		
		assertFalse( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 1 )
		).equals( Transformer.toTreeSet(
			new AtomAddressInternal( 0, 0, 1 ),
			new AtomAddressInternal( 0, 0, 0 )
		) ) );
	}
}
