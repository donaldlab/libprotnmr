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
