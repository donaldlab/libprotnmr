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
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.util.UnmodifiableCompositeList;


public class Protein implements HasAtoms
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_name;
	private List<Subunit> m_subunits;
	private Map<Character,Integer> m_subunitIndex;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Protein( )
	{
		m_name = "";
		m_subunits = new ArrayList<Subunit>();
		m_subunitIndex = new TreeMap<Character,Integer>();
	}
	
	public Protein( Protein other )
	{
		m_name = other.m_name;
		m_subunits = new ArrayList<Subunit>( other.m_subunits.size() );
		m_subunitIndex = new TreeMap<Character,Integer>();
		
		// deep copy the subunits
		Iterator<Subunit> iterSubunit = other.m_subunits.iterator();
		while( iterSubunit.hasNext() )
		{
			Subunit subunit = iterSubunit.next();
			if( subunit == null )
			{
				m_subunits.add( null );
			}
			else
			{
				m_subunits.add( new Subunit( subunit ) );
			}
		}
		updateSubunitIndex();
	}
	
	public Protein( Subunit subunit )
	{
		m_name = "";
		m_subunits = new ArrayList<Subunit>( 1 );
		m_subunits.add( subunit );
		m_subunitIndex = new TreeMap<Character,Integer>();
		
		// redo the ids and indices
		subunit.setId( 0 );
		subunit.updateAtomIndices();
		updateSubunitIndex();
	}
	
	public Protein( List<Subunit> subunits )
	{
		m_name = "";
		m_subunits = subunits;
		m_subunitIndex = new TreeMap<Character,Integer>();
		
		// redo the ids and indices
		for( int i=0; i<m_subunits.size(); i++ )
		{
			Subunit subunit = m_subunits.get( i );
			subunit.setId( i );
			subunit.updateAtomIndices();
		}
		updateSubunitIndex();
	}
	
	public Protein clone( )
	{
		return new Protein( this );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	public void setName( String value )
	{
		m_name = value;
	}
	
	public List<Subunit> getSubunits( )
	{
		return m_subunits;
	}
	public void setSubunits( List<Subunit> value )
	{
		m_subunits = value;
		updateAtomIndices();
		updateSubunitIndex();
	}
	
	public Integer getSubunitId( char subunitName )
	{
		return m_subunitIndex.get( subunitName );
	}
	
	public Subunit getSubunit( int subunitId )
	{
		if( subunitId < 0 || subunitId >= m_subunits.size() )
		{
			return null;
		}
		return m_subunits.get( subunitId );
	}
	
	public Subunit getSubunit( char subunitName )
	{
		Integer subunitId = m_subunitIndex.get( subunitName );
		if( subunitId == null )
		{
			return null;
		}
		return m_subunits.get( subunitId );
	}

	public Residue getResidue( int subunitId, int residueId )
	{
		Subunit subunit = getSubunit( subunitId );
		if( subunit == null )
		{
			return null;
		}
		return subunit.getResidue( residueId );
	}
	
	public Residue getResidue( AtomAddressInternal address )
	{
		return getResidue( address.getSubunitId(), address.getResidueId() );
	}
	
	public Residue getResidue( AtomAddressReadable address )
	{
		return getResidueByNumber( address.getSubunitName(), address.getResidueNumber() );
	}
	
	public Residue getResidueByNumber( char subunitName, int residueNumber )
	{
		Subunit subunit = getSubunit( subunitName );
		if( subunit == null )
		{
			return null;
		}
		return subunit.getResidueByNumber( residueNumber );
	}
	
	@Override
	public Atom getAtom( int subunitId, int residueId, int atomId )
	{
		Residue residue = getResidue( subunitId, residueId );
		if( residue == null )
		{
			return null;
		}
		List<Atom> atoms = residue.getAtoms();
		if( atomId < 0 || atomId >= atoms.size() )
		{
			return null;
		}
		return atoms.get( atomId );
	}
	
	@Override
	public Atom getAtom( AtomAddressInternal address )
	{
		return getAtom( address.getSubunitId(), address.getResidueId(), address.getAtomId() );
	}
	
	public Atom getAtom( char subunitName, int residueNumber, String atomName )
	{
		// UNDONE: optimize out the new call
		return getAtom( new AtomAddressReadable( subunitName, residueNumber, atomName ) );
	}
	
	public Atom getAtom( AtomAddressReadable address )
	{
		List<AtomAddressInternal> addresses = AddressMapper.mapAddressExpandPseudoatoms( this, address );
		if( addresses.size() != 1 )
		{
			return null;
		}
		return getAtom( addresses.get( 0 ) );
	}
	
	@Override
	public int getNumAtoms( )
	{
		int numAtoms = 0;
		for( Subunit subunit : m_subunits )
		{
			numAtoms += subunit.atoms().size();
		}
		return numAtoms;
	}
	
	@Override
	public int getNumBackboneAtoms( )
	{
		int numAtoms = 0;
		for( Subunit subunit : m_subunits )
		{
			numAtoms += subunit.backboneAtoms().size();
		}
		return numAtoms;
	}
	
	public AminoAcid getAminoAcid( AtomAddressReadable address )
	{
		return getSubunit( address.getSubunitName() ).getResidueByNumber( address.getResidueNumber() ).getAminoAcid();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return "[Protein] " + m_name + " : " + m_subunits.size() + " subunits";
	}
	
	public String dump( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( toString() );
		buf.append( "\n" );
		buf.append( "\tAtoms: " );
		buf.append( getNumAtoms() );
		buf.append( "\n" );
		
		// dump the subunits
		for( Subunit subunit : m_subunits )
		{
			buf.append( subunit.dump() );
		}
		
		return buf.toString();
	}
	
	@Override
	public List<AtomAddressInternal> atoms( )
	{
		// build the list of iterators
		List<List<AtomAddressInternal>> lists = new ArrayList<List<AtomAddressInternal>>( m_subunits.size() );
		for( Subunit subunit : m_subunits )
		{
			lists.add( subunit.atoms() );
		}
		return new UnmodifiableCompositeList<AtomAddressInternal>( lists );
	}
	
	@Override
	public List<AtomAddressInternal> backboneAtoms( )
	{
		List<List<AtomAddressInternal>> lists = new ArrayList<List<AtomAddressInternal>>( m_subunits.size() );
		for( Subunit subunit : m_subunits )
		{
			lists.add( subunit.backboneAtoms() );
		}
		return new UnmodifiableCompositeList<AtomAddressInternal>( lists );
	}
	
	@Deprecated
	public Protein getBackbone( )
	{
		// return a copy of the protein that has only backbone atoms
		
		// make a copy of our original protein
		Protein slim = new Protein( this );
		for( int i=0; i<slim.getSubunits().size(); i++ )
		{
			slim.getSubunits().set( i, slim.getSubunits().get( i ).getBackbone() );
		}
		
		return slim;
	}
	
	public boolean isHomoOligomer( )
	{
		int numResidues = getSubunit( 0 ).getResidues().size();
		
		// easy check first: all subunits must have the same number of residues
		for( Subunit subunit : m_subunits )
		{
			if( subunit.getResidues().size() != numResidues )
			{
				return false;
			}
		}
		
		// the sequences for all subunits must be the same
		for( int i=0; i<numResidues; i++ )
		{
			AminoAcid aminoAcid = getResidue( 0, i ).getAminoAcid();
			for( Subunit subunit : m_subunits )
			{
				if( subunit.getResidues().get( i ).getAminoAcid() != aminoAcid )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void updateSubunitIndex( )
	{
		m_subunitIndex.clear();
		for( int i=0; i<m_subunits.size(); i++ )
		{
			Subunit subunit = m_subunits.get( i );
			subunit.setId( i );
			m_subunitIndex.put( subunit.getName(), i );
		}
	}
	
	public void updateAtomIndices( )
	{
		for( int i=0; i<m_subunits.size(); i++ )
		{
			Subunit subunit = m_subunits.get( i );
			subunit.setId( i );
			subunit.updateAtomIndices();
		}
	}
	
	public void addSubunit( Subunit subunit )
	{
		subunit.setId( m_subunits.size() );
		m_subunits.add( subunit );
		subunit.updateAtomIndices();
		m_subunitIndex.put( subunit.getName(), subunit.getId() );
	}
	
	public Sequences getSequences( )
	{
		Sequences sequences = new Sequences();
		for( Subunit subunit : m_subunits )
		{
			sequences.add( subunit.getName(), subunit.getSequence() );
		}
		return sequences;
	}
	
	public Protein getFragmentByNumbers( int startNumber, int stopNumber )
	{
		Protein fragment = new Protein();
		fragment.setName( getName() );
		for( Subunit subunit : m_subunits )
		{
			fragment.addSubunit( subunit.getFragmentByNumbers( startNumber, stopNumber ) );
		}
		fragment.updateSubunitIndex();
		fragment.updateAtomIndices();
		return fragment;
	}
	
	public Protein getFragment( int startIndex, int stopIndex )
	{
		Protein fragment = new Protein();
		fragment.setName( getName() );
		for( Subunit subunit : m_subunits )
		{
			fragment.addSubunit( subunit.getFragment( startIndex, stopIndex ) );
		}
		fragment.updateSubunitIndex();
		fragment.updateAtomIndices();
		return fragment;
	}
}
