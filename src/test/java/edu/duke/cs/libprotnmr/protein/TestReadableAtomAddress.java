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
