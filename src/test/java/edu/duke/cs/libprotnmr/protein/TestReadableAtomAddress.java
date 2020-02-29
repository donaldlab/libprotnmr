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

public class TestReadableAtomAddress extends ExtendedTestCase
{
	public void testConstructor( )
	{
		AtomAddressReadable address = new AtomAddressReadable( 'a', 12, "hi" );
		
		// characters and strings should be upper-case
		assertEquals( 'A', address.getSubunitName() );
		assertEquals( 12, address.getResidueNumber() );
		assertEquals( "HI", address.getAtomName() );
	}
	
	public void testCopyConstructor( )
	{
		AtomAddressReadable address = new AtomAddressReadable( 'a', 12, "hi" );
		AtomAddressReadable address2 = new AtomAddressReadable( address );
		
		assertNotSame( address, address2 );
		assertEquals( 'A', address2.getSubunitName() );
		assertEquals( 12, address2.getResidueNumber() );
		assertEquals( "HI", address2.getAtomName() );
	}
	
	public void testCompareTo( )
	{
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) == 0 );
		assertTrue( new AtomAddressReadable( 'a', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) == 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "HI" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) == 0 );
		assertTrue( new AtomAddressReadable( 'a', 12, "HI" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) == 0 );
		
		// test subunit
		assertTrue( new AtomAddressReadable( 'B', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) > 0 );
		assertTrue( new AtomAddressReadable( 'b', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) > 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).compareTo( new AtomAddressReadable( 'B', 12, "hi" ) ) < 0 );
		assertTrue( new AtomAddressReadable( 'a', 12, "hi" ).compareTo( new AtomAddressReadable( 'B', 12, "hi" ) ) < 0 );
		
		// test residue
		assertTrue( new AtomAddressReadable( 'A', 13, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) > 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 13, "hi" ) ) < 0 );
		
		// test atom
		assertTrue( new AtomAddressReadable( 'A', 12, "hj" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) > 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "HJ" ).compareTo( new AtomAddressReadable( 'A', 12, "hi" ) ) > 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "hj" ) ) < 0 );
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).compareTo( new AtomAddressReadable( 'A', 12, "HJ" ) ) < 0 );
	}
	
	public void testEquals( )
	{
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).equals( new AtomAddressReadable( 'A', 12, "hi" ) ) );
		assertTrue( new AtomAddressReadable( 'a', 12, "hi" ).equals( new AtomAddressReadable( 'A', 12, "hi" ) ) );
		assertTrue( new AtomAddressReadable( 'A', 12, "HI" ).equals( new AtomAddressReadable( 'A', 12, "hi" ) ) );
		assertTrue( new AtomAddressReadable( 'a', 12, "HI" ).equals( new AtomAddressReadable( 'A', 12, "hi" ) ) );
		
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).equals( new AtomAddressReadable( 'b', 12, "hi" ) ) );
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).equals( new AtomAddressReadable( 'a', 13, "hi" ) ) );
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).equals( new AtomAddressReadable( 'a', 12, "fa" ) ) );
	}
	
	public void testHashCode( )
	{
		assertTrue( new AtomAddressReadable( 'A', 12, "hi" ).hashCode() == new AtomAddressReadable( 'A', 12, "hi" ).hashCode() );
		assertTrue( new AtomAddressReadable( 'a', 12, "hi" ).hashCode() == new AtomAddressReadable( 'A', 12, "hi" ).hashCode() );
		assertTrue( new AtomAddressReadable( 'A', 12, "HI" ).hashCode() == new AtomAddressReadable( 'A', 12, "hi" ).hashCode() );
		assertTrue( new AtomAddressReadable( 'a', 12, "HI" ).hashCode() == new AtomAddressReadable( 'A', 12, "hi" ).hashCode() );
		
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).hashCode() == new AtomAddressReadable( 'b', 12, "hi" ).hashCode() );
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).hashCode() == new AtomAddressReadable( 'a', 13, "hi" ).hashCode() );
		assertFalse( new AtomAddressReadable( 'a', 12, "HI" ).hashCode() == new AtomAddressReadable( 'a', 12, "fa" ).hashCode() );
	}
	
	public void testSetEquals( )
	{
		assertTrue( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" )
		).equals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" )
		) ) );
		
		assertTrue( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" ),
			new AtomAddressReadable( 'A', 12, "ho" )
		).equals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" ),
			new AtomAddressReadable( 'A', 12, "ho" )
		) ) );
		
		assertTrue( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "ho" ),
			new AtomAddressReadable( 'A', 12, "hi" )
		).equals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" ),
			new AtomAddressReadable( 'A', 12, "ho" )
		) ) );
		
		assertFalse( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" )
		).equals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "ho" )
		) ) );
		
		assertFalse( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" )
		).equals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 12, "hi" ),
			new AtomAddressReadable( 'A', 12, "ho" )
		) ) );
	}
}
