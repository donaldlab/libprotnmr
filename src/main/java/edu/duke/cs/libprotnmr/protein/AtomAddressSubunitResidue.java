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

import edu.duke.cs.libprotnmr.io.HashCalculator;

/*
	This class' purpose is to override hashCode() and equals() for different comparisons
*/
public class AtomAddressSubunitResidue extends AtomAddressInternal
{
	private static final long serialVersionUID = 3503059937915697654L;
	

	/**************************
	 *   Constructors
	 **************************/

	public AtomAddressSubunitResidue( AtomAddressInternal address )
	{
		super( address );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
		{
            return false;
		}
        if( other == this )
        {
            return true;
        }
        if( other.getClass() != getClass() )
        {
            return false;
        }
        
        return equals( (AtomAddressSubunitResidue)other );
	}
	
	public boolean equals( AtomAddressSubunitResidue other )
	{
		return
			getSubunitId() == other.getSubunitId()
			&& getResidueId() == other.getResidueId();
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.hashIds( getSubunitId(), getResidueId() );
	}
	
	public AtomAddressInternal getAtomAddress( )
	{
		return (AtomAddressInternal)this;
	}
}
