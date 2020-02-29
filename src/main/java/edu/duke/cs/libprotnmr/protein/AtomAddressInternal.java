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

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.io.HashCalculator;


public class AtomAddressInternal implements Serializable, AtomAddress<AtomAddressInternal>, Comparable<AtomAddressInternal>
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = 1013519028531937908L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_subunitId;
	private int m_residueId;
	private int m_atomId;
	

	/**************************
	 *   Constructors
	 **************************/
	
	public AtomAddressInternal( int subunitId, int residueId, int atomId )
	{
		m_subunitId = subunitId;
		m_residueId = residueId;
		m_atomId = atomId;
	}

	public AtomAddressInternal( Subunit subunit, Residue residue, Atom atom )
	{
		m_subunitId = subunit.getId();
		m_residueId = residue.getId();
		m_atomId = atom.getId();
	}
	
	public AtomAddressInternal( AtomAddressInternal other )
	{
		m_subunitId = other.m_subunitId;
		m_residueId = other.m_residueId;
		m_atomId = other.m_atomId;
	}
	

	/**************************
	 *   Accessors
	 **************************/

	public int getSubunitId( )
	{
		return m_subunitId;
	}
	public void setSubunitId( int val )
	{
		m_subunitId = val;
	}

	public int getResidueId( )
	{
		return m_residueId;
	}
	public void setResidueId( int val )
	{
		m_residueId = val;
	}

	public int getAtomId( )
	{
		return m_atomId;
	}
	public void setAtomId( int val )
	{
		m_atomId = val;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void removeDuplicates( Set<AtomAddressInternal> addresses )
	{
		/* Jeff: 10/30/2009 - NOTE:
			It might seem to have silly to have this function since
			we use sets to contain the atom addressses.
			However, the addresses themselves are still mutable, so other
			code has the ability to violate the uniqueness property of the
			set. This function is intended to restore uniqueness after such
			a modification.
		*/
		TreeSet<AtomAddressInternal> copy = new TreeSet<AtomAddressInternal>();
		for( AtomAddressInternal address : addresses )
		{
			copy.add( address );
		}
		addresses.clear();
		addresses.addAll( copy );
	}
	
	public static void mapAddressesToSubunit( Set<AtomAddressInternal> addresses, int subunitId )
	{
		for( AtomAddressInternal address : addresses )
		{
			address.m_subunitId = subunitId;
		}
		removeDuplicates( addresses );
	}
	
	public static int getNumDistinctSubunits( Set<AtomAddressInternal> addresses )
	{
		TreeSet<Integer> set = new TreeSet<Integer>();
		for( AtomAddressInternal address : addresses )
		{
			set.add( address.m_subunitId );
		}
		return set.size();
	}
	
	public static void rotateSubunits( Set<AtomAddressInternal> addresses, int numSubunits, int numAdvance )
	{
		for( AtomAddressInternal address : addresses )
		{
			// determine where the new address goes
			int newSubunitId = ( address.getSubunitId() + numAdvance ) % numSubunits;
			
			// RETARDED: java's implementation of modulus is wrong, so we need to handle negative numbers separately
			if( newSubunitId < 0 )
			{
				newSubunitId += numSubunits;
			}
			
			address.setSubunitId( newSubunitId );
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return "(" + m_subunitId + "," + m_residueId + "," + m_atomId + ")";
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.hashIds( m_subunitId, m_residueId, m_atomId );
	}
	
	@Override
	public int compareTo( AtomAddressInternal other )
	{
		int diff = 0;
		
		diff = m_subunitId - other.m_subunitId;
		if( diff != 0 )
		{
			return diff;
		}
		
		diff = m_residueId - other.m_residueId;
		if( diff != 0 )
		{
			return diff;
		}
		
		diff = m_atomId - other.m_atomId;
		if( diff != 0 )
		{
			return diff;
		}
		
		return 0;
	}
	
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
        
        return equals( (AtomAddressInternal)other );
	}
	
	public boolean equals( AtomAddressInternal other )
	{
		return
			m_subunitId == other.m_subunitId
			&& m_residueId == other.m_residueId
			&& m_atomId == other.m_atomId;
	}
	
	public boolean equalsResidueAtom( AtomAddressInternal other )
	{
		return m_residueId == other.m_residueId
			&& m_atomId == other.m_atomId;
	}
	
	@Override
	public boolean isAmbiguous( )
	{
		return false;
	}
	
	@Override
	public AtomAddressInternal newCopy( )
	{
		return new AtomAddressInternal( this );
	}
	
	@Override
	public boolean isSameSubunit( AtomAddressInternal other )
	{
		return m_subunitId == other.m_subunitId;
	}
	
	@Override
	public boolean isSameAtom( AtomAddressInternal other )
	{
		return m_atomId == other.m_atomId;
	}
}
