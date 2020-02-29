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

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class TestDistanceRestraintFilterer extends ExtendedTestCase
{
	// UNDONE: test sortSides
	// UNDONE: test pickIntersubunit
	
	@SuppressWarnings( "unchecked" )
	public void testPickUnique( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickUnique( before );
		
		assertEquals( 1, after.size() );
		assertSame( before.get( 0 ), after.get( 0 ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public void testPickUniqueOrder1( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 2, 7, 8 ), new AtomAddressInternal( 3, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 2, 7, 8 ), new AtomAddressInternal( 3, 3, 4 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickUnique( before );
		
		assertEquals( 2, after.size() );
		assertSame( before.get( 0 ), after.get( 0 ) );
		assertSame( before.get( 2 ), after.get( 1 ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public void testPickUniqueOrder2( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 2, 7, 8 ), new AtomAddressInternal( 3, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 2, 7, 8 ), new AtomAddressInternal( 3, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickUnique( before );
		
		assertEquals( 2, after.size() );
		assertSame( before.get( 0 ), after.get( 0 ) );
		assertSame( before.get( 2 ), after.get( 1 ) );
	}
	
	// UNDONE: test pickBetween
	// UNDONE: test pickBetweenEitherSide
	// UNDONE: test pickSubunitEitherSide
	// UNDONE: test mapToSubunit
	
	@SuppressWarnings( "unchecked" )
	public void testPickOneFromSymmetricGroup( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 1, 1, 2 ), new AtomAddressInternal( 0, 5, 6 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickOneFromSymmetricGroup( before );
		
		assertEquals( 1, after.size() );
		assertSameInList( before, after.get( 0 ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public void testPickOneFromSymmetricGroupOrder1( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 1, 1, 2 ), new AtomAddressInternal( 0, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 0, 7, 8 ), new AtomAddressInternal( 1, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 1, 7, 8 ), new AtomAddressInternal( 0, 3, 4 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickOneFromSymmetricGroup( before );
		
		assertEquals( 2, after.size() );
		assertSame( before.get( 0 ), after.get( 0 ) );
		assertSame( before.get( 2 ), after.get( 1 ) );
	}

	@SuppressWarnings( "unchecked" )
	public void testPickOneFromSymmetricGroupOrder2( )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> before = newRestraints(
			newRestraint( new AtomAddressInternal( 0, 7, 8 ), new AtomAddressInternal( 1, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 1, 7, 8 ), new AtomAddressInternal( 0, 3, 4 ) ),
			newRestraint( new AtomAddressInternal( 0, 1, 2 ), new AtomAddressInternal( 1, 5, 6 ) ),
			newRestraint( new AtomAddressInternal( 1, 1, 2 ), new AtomAddressInternal( 0, 5, 6 ) )
		);
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> after = DistanceRestraintFilterer.pickOneFromSymmetricGroup( before );
		
		assertEquals( 2, after.size() );
		assertSame( before.get( 0 ), after.get( 0 ) );
		assertSame( before.get( 2 ), after.get( 1 ) );
	}
	
	private ArrayList<DistanceRestraint<AtomAddressInternal>> newRestraints( DistanceRestraint<AtomAddressInternal> ... restraints )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> list = new ArrayList<DistanceRestraint<AtomAddressInternal>>( restraints.length );
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			list.add( restraint );
		}
		return list;
	}
	
	private DistanceRestraint<AtomAddressInternal> newRestraint( AtomAddressInternal left, AtomAddressInternal right )
	{
		DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
		restraint.setLefts( left );
		restraint.setRights( right );
		return restraint;
	}
}
