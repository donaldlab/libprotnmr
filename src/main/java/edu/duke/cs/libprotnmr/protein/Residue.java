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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Residue implements HasAtoms
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_id;
	private int m_number;
	private int m_firstAtomNumber;
	private ArrayList<Atom> m_atoms;
	private AminoAcid m_aminoAcid;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Residue( )
	{
		m_id = -1;
		m_number = 0;
		m_firstAtomNumber = 0;
		m_atoms = new ArrayList<Atom>();
		m_aminoAcid = AminoAcid.Unknown;
	}
	
	public Residue( Residue other )
	{
		m_id = other.m_id;
		m_number = other.m_number;
		m_firstAtomNumber = other.m_firstAtomNumber;
		m_atoms = new ArrayList<Atom>( other.m_atoms.size() );
		m_aminoAcid = other.m_aminoAcid;
		
		// deep copy the atoms
		Iterator<Atom> iterAtom = other.m_atoms.iterator();
		while( iterAtom.hasNext() )
		{
			Atom atom = iterAtom.next();
			if( atom == null )
			{
				m_atoms.add( null );
			}
			else
			{
				m_atoms.add( new Atom( atom ) );
			}
		}
	}
	
	public Residue clone( )
	{
		return new Residue( this );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getId( )
	{
		return m_id;
	}
	public void setId( int value )
	{
		m_id = value;
	}
	
	public int getNumber( )
	{
		return m_number;
	}
	public void setNumber( int value )
	{
		m_number = value;
	}
	
	public int getFirstAtomNumber( )
	{
		return m_firstAtomNumber;
	}
	public void setFirstAtomNumber( int value )
	{
		m_firstAtomNumber = value;
	}
	
	public ArrayList<Atom> getAtoms( )
	{
		return m_atoms;
	}
	public void setAtoms( ArrayList<Atom> value )
	{
		m_atoms = value;
	}
	
	public AminoAcid getAminoAcid( )
	{
		return m_aminoAcid;
	}
	public void setAminoAcid( AminoAcid value )
	{
		m_aminoAcid = value;
	}
	
	public Atom getAtom( int atomId )
	{
		if( !isValidAtomId( atomId ) )
		{
			return null;
		}
		return m_atoms.get( atomId );
	}
	
	public Atom getAtomByName( String name )
	{
		/* Jeff: 12/18/2008 - NOTE:
			Residues aren't that big. A simple sequential search here is fine.
		*/
		
		for( Atom atom : m_atoms )
		{
			if( atom.getName().equalsIgnoreCase( name ) )
			{
				return atom;
			}
		}
		return null;
	}
	
	public boolean isValidAtomId( int atomId )
	{
		return atomId >= 0 && atomId < m_atoms.size();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "[Residue] " + m_number + ":" + m_aminoAcid.getAbbreviation();
	}
	
	public String dump( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( toString() );
		
		// dump the atoms
		buf.append( "\t" );
		for( Atom atom : m_atoms )
		{
			buf.append( atom.getName() );
			buf.append( " " );
		}
		buf.append( "\n" );
		
		return buf.toString();
	}
	
	@Override
	public Atom getAtom( int subunitId, int residueId, int atomId )
	{
		return getAtom( atomId );
	}
	
	@Override
	public Atom getAtom( AtomAddressInternal address )
	{
		return getAtom( address.getAtomId() );
	}
	
	public Atom getAtom( AtomAddressReadable address )
	{
		return getAtomByName( address.getAtomName() );
	}
	
	@Override
	public int getNumAtoms( )
	{
		return m_atoms.size();
	}
	
	@Override
	public int getNumBackboneAtoms( )
	{
		int numBackboneAtoms = 0;
		for( Atom atom : m_atoms )
		{
			if( atom.isBackbone() )
			{
				numBackboneAtoms++;
			}
		}
		return numBackboneAtoms;
	}
	
	@Override
	public List<AtomAddressInternal> atoms( )
	{
		// UNDONE: cache or precompute the address list
		ArrayList<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>();
		for( Atom atom : m_atoms )
		{
			addresses.add( new AtomAddressInternal( 0, atom.getResidueId(), atom.getId() ) );
		}
		return addresses;
	}

	@Override
	public List<AtomAddressInternal> backboneAtoms( )
	{
		// UNDONE: cache or precompute the address list
		ArrayList<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>();
		for( Atom atom : m_atoms )
		{
			if( atom.isBackbone() )
			{
				addresses.add( new AtomAddressInternal( 0, atom.getResidueId(), atom.getId() ) );
			}
		}
		return addresses;
	}
}
