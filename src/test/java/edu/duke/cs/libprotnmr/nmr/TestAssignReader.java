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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class TestAssignReader extends ExtendedTestCase
{
	public void testReadNoe( )
	throws Exception
	{
		Assign assign = getAssign(
			"assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 -2.2 4.0"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}
	
	public void testReadMultiLineNoe( )
	throws Exception
	{
		Assign assign = getAssign(
			"assign (resid 7 and name ha1\n"
			+ "and segid A)(resid 15 and name hd# and segid C\n"
			+ ") 4.0 -2.2 4.0"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}
	
	public void testReadAssignWithOrs( )
	throws Exception
	{
		Assign assign = getAssign(
			"assign\n"
			+ "((resid 7 and name ha1 and segid A)or(resid 17 and name hg12 and segid D))\n"
			+ "(resid 15 and name hd# and segid C)\n"
			+ "6"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 2, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( new AtomAddressReadable( 'D', 17, "hg12" ), assign.getAddresses().get( 0 ).get( 1 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 1, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 6.0 ), assign.getNumbers() );
	}
	
	public void testReadMultipleAssigns( )
	throws Exception
	{
		ArrayList<Assign> assigns = getAssigns(
			"assign (resid 7 and name ha1\n"
			+ "and segid A)(resid 15 and name hd# and segid C\n"
			+ ") 4.0 -2.2 4.0\n"
			+ "assign\n"
			+ "((resid 7 and name ha1 and segid A)or(resid 17 and name hg12 and segid D))\n"
			+ "(resid 15 and name hd# and segid C)\n"
			+ "6"
		);
		
		Assign assign = null;
		assertEquals( 2, assigns.size() );
		
		assign = assigns.get( 0 );
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
		
		assign = assigns.get( 1 );
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 2, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( new AtomAddressReadable( 'D', 17, "hg12" ), assign.getAddresses().get( 0 ).get( 1 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 1, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 6.0 ), assign.getNumbers() );
	}
	
	public void testReadComments( )
	throws Exception
	{
		Assign assign = getAssign(
			"!comment\n"
			+ "\n"
			+ "assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 -2.2 4.0 !comment\n"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}

	public void testReadAmbiguousComments( )
	throws Exception
	{
		Assign assign = getAssign(
			"#comment\n"
			+ "\n"
			+ "assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 -2.2 4.0 !comment\n"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}
	
	public void testReadAmbiguousCommentsMultiline( )
	throws Exception
	{
		Assign assign = getAssign(
			"assign (resid 7 and name ha1 and segid A)!comment\n"
			+ "(resid 15 and name hd# and segid C)!comment\n"
			+ "4.0 -2.2 4.0!comment\n"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}
	
	public void testSkipCommentedAssigns( )
	throws Exception
	{
		List<Assign> assigns = getAssigns(
			"assign (resid 7 and name ha1 and segid A) (resid 15 and name hd# and segid C) 4.0 -2.2 4.0!comment\n"
			+ "#assign (resid 7 and name ha1 and segid A) (resid 15 and name hd# and segid C) 4.0 -2.2 4.0!comment\n"
			+ "#assign (resid 7 and name ha1 and segid A) (resid 15 and name hd# and segid C) 4.0 -2.2 4.0!comment\n"
			+ "assign (resid 7 and name ha1 and segid A) (resid 15 and name hd# and segid C) 4.0 -2.2 4.0!comment\n"
			+ "assign (resid 7 and name ha1 and segid A) (resid 15 and name hd# and segid C) 4.0 -2.2 4.0!comment\n"
		);
		assertEquals( 3, assigns.size() );
	}

	public void testCase( )
	throws Exception
	{
		Assign assign = getAssign(
			"AssIGN (resID 7 and name HA1 AND segid A)(resid 15 and name hd# and Segid c) 4.0 -2.2 4.0\n"
		);
		
		assertEquals( 2, assign.getAddresses().size() );
		assertEquals( 1, assign.getAddresses().get( 0 ).size() );
		assertEquals( new AtomAddressReadable( 'A', 7, "ha1" ), assign.getAddresses().get( 0 ).get( 0 ) );
		assertEquals( 1, assign.getAddresses().get( 1 ).size() );
		assertEquals( new AtomAddressReadable( 'C', 15, "hd#" ), assign.getAddresses().get( 1 ).get( 0 ) );
		assertEquals( 3, assign.getNumbers().size() );
		assertEquals( Transformer.toArrayList( 4.0, -2.2, 4.0 ), assign.getNumbers() );
	}

	private Assign getAssign( String in )
	throws Exception
	{
		ArrayList<Assign> assigns = new AssignReader().read( new ByteArrayInputStream( in.getBytes() ) );
		assertEquals( 1, assigns.size() );
		return assigns.get( 0 );
	}
	
	private ArrayList<Assign> getAssigns( String in )
	throws Exception
	{
		return new AssignReader().read( new ByteArrayInputStream( in.getBytes() ) );
	}
}
