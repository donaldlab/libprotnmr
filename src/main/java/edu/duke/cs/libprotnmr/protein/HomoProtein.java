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

import java.util.List;

public class HomoProtein extends Protein
{
	/**************************
	 *   Constructors
	 **************************/
	
	public HomoProtein( Subunit subunit, String subunitNames )
	{
		for( int i=0; i<subunitNames.length(); i++ )
		{
			char name = subunitNames.charAt( i );
			addSubunit( newShallowClone( subunit, name ) );
		}
	}
	
	public HomoProtein( Subunit subunit, List<Character> subunitNames )
	{
		for( Character name : subunitNames )
		{
			addSubunit( newShallowClone( subunit, name ) );
		}
	}
	
	public HomoProtein( Subunit subunit, int numSubunits )
	{
		for( int i=0; i<numSubunits; i++ )
		{
			addSubunit( newShallowClone( subunit, (char)( 'A' + i ) ) );
		}
	}
	

	/**************************
	 *   Accessors
	 **************************/
		
	@Override
	public boolean isHomoOligomer( )
	{
		return true;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private Subunit newShallowClone( Subunit subunit, char name )
	{
		// make a copy of the subunit, but don't copy any of the residues or atoms
		Subunit clone = new Subunit( subunit, false );
		clone.setName( name );
		return clone;
	}
}
