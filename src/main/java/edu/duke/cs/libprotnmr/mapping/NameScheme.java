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
package edu.duke.cs.libprotnmr.mapping;

import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;
import edu.duke.cs.libprotnmr.protein.HasAtoms;


public enum NameScheme
{
	/**************************
	 *   Values
	 **************************/
	
	Old,
	New;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static NameScheme getSchemeForProtein( HasAtoms protein )
	{
		/* Jeff: 12/28/2008 - NOTE:
			Here's the simplest way I've found to tell the difference between
			files using the old names and the new names. If a file references
			an HN atom, it's using the old names. Otherwise, it's using the new
			names.
		*/
		
		for( AtomAddressInternal address : protein.atoms() )
		{
			if( protein.getAtom( address ).getName().equalsIgnoreCase( "HN" ) )
			{
				return Old;
			}
		}
		return New;
	}
	
	public static NameScheme getSchemeForAddresses( List<? extends HasAddresses<AtomAddressReadable>> allAddresses )
	{
		for( HasAddresses<AtomAddressReadable> addresses : allAddresses )
		{
			for( AtomAddressReadable address : addresses.addresses() )
			{
				if( address.getAtomName().equalsIgnoreCase( "HN" ) )
				{
					return Old;
				}
			}
		}
		return New;
	}
}
