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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;


public class Subunit implements HasAtoms
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_id;
	private char m_name;
	private ArrayList<Residue> m_residues;
	private TreeMap<Integer,Integer> m_residueIndex;
	private ArrayList<AtomAddressInternal> m_atomIndex;
	private ArrayList<AtomAddressInternal> m_backboneAtomIndex;
	private TreeMap<ResidueRange,BackboneConformation> m_backboneConformations;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Subunit( )
	{
		m_id = 0;
		m_name = '\0';
		m_residues = new ArrayList<Residue>();
		m_residueIndex = new TreeMap<Integer,Integer>();
		m_atomIndex = new ArrayList<AtomAddressInternal>();
		m_backboneAtomIndex = new ArrayList<AtomAddressInternal>();
		m_backboneConformations = new TreeMap<ResidueRange,BackboneConformation>();
	}
	
	public Subunit( Subunit other )
	{
		this( other, true );
	}
	
	public Subunit( Subunit other, boolean isDeep )
	{
		m_id = other.m_id;
		m_name = other.m_name;
		
		if( isDeep )
		{
			m_residues = new ArrayList<Residue>( other.m_residues.size() );
			m_residueIndex = new TreeMap<Integer,Integer>( other.m_residueIndex );
			m_atomIndex = new ArrayList<AtomAddressInternal>();
			m_backboneAtomIndex = new ArrayList<AtomAddressInternal>();
			m_backboneConformations = new TreeMap<ResidueRange,BackboneConformation>( other.m_backboneConformations );
			
			// deep copy the residues
			Iterator<Residue> iterResidue = other.m_residues.iterator();
			while( iterResidue.hasNext() )
			{
				Residue residue = iterResidue.next();
				if( residue == null )
				{
					m_residues.add( null );
				}
				else
				{
					m_residues.add( new Residue( residue ) );
				}
			}
		}
		else
		{
			m_residues = other.m_residues;
			m_residueIndex = other.m_residueIndex;
			m_atomIndex = new ArrayList<AtomAddressInternal>();
			m_backboneAtomIndex = new ArrayList<AtomAddressInternal>();
			m_backboneConformations = other.m_backboneConformations;
		}
		
		updateAtomIndices();
	}
	
	public Subunit clone( )
	{
		return new Subunit( this );
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
	
	public char getName( )
	{
		return m_name;
	}
	public void setName( char val )
	{
		m_name = val;
	}
	
	public ArrayList<Residue> getResidues( )
	{
		return m_residues;
	}
	public void setResidues( ArrayList<Residue> value )
	{
		m_residues = value;
	}
	
	public int getFirstResidueNumber( )
	{
		return m_residueIndex.firstKey();
	}
	
	public int getLastResidueNumber( )
	{
		return m_residueIndex.lastKey();
	}
	
	@Override
	public List<AtomAddressInternal> atoms( )
	{
		return m_atomIndex;
	}
	
	@Override
	public List<AtomAddressInternal> backboneAtoms( )
	{
		return m_backboneAtomIndex;
	}
	
	@Override
	public Atom getAtom( int subunitId, int residueId, int atomId )
	{
		return getAtom( residueId, atomId );
	}
	
	public Atom getAtom( int residueId, int atomId )
	{
		if( !isValueResidueId( residueId ) )
		{
			return null;
		}
		return m_residues.get( residueId ).getAtom( atomId );
	}
	
	@Override
	public Atom getAtom( AtomAddressInternal address )
	{
		return getAtom( address.getResidueId(), address.getAtomId() );
	}
	
	public Atom getAtomByNumber( int residueNumber, String atomName )
	{
		Residue residue = getResidueByNumber( residueNumber );
		if( residue == null )
		{
			return null;
		}
		return residue.getAtomByName( atomName );
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
	
	public Integer getResidueId( int residueNumber )
	{
		return m_residueIndex.get( residueNumber );
	}
	
	public Residue getResidue( int residueId )
	{
		if( residueId < 0 || residueId >= m_residues.size() )
		{
			return null;
		}
		return m_residues.get( residueId );
	}
	
	public Residue getResidueByNumber( int residueNumber )
	{
		Integer index = m_residueIndex.get( residueNumber );
		if( index == null )
		{
			return null;
		}
		return m_residues.get( index );
	}
	
	public Residue getResidue( AtomAddressInternal address )
	{
		return getResidue( address.getResidueId() );
	}
	
	public boolean isValueResidueId( int residueId )
	{
		return residueId >= 0 && residueId < m_residues.size();
	}
	
	public TreeMap<ResidueRange,BackboneConformation> getBackboneConformations( )
	{
		return m_backboneConformations;
	}
	public void setBackboneConformations( TreeMap<ResidueRange,BackboneConformation> val )
	{
		m_backboneConformations = val;
	}
	
	public BackboneConformation getBackboneConformation( int residueNumber )
	{
		// NOTE: we could build an lookup table to do this, but then we have to sync that with residue changes
		// also, this code assumes that no two residue ranges overlap
		for( Map.Entry<ResidueRange,BackboneConformation> entry : m_backboneConformations.entrySet() )
		{
			if( entry.getKey().contains( residueNumber ) )
			{
				return entry.getValue();
			}
		}
		return BackboneConformation.Loop;
	}
	
	public Integer getFirstSSEResidueNumber( )
	{
		if( m_backboneConformations.isEmpty() )
		{
			return null;
		}
		return m_backboneConformations.firstKey().getStartNumber();
	}
	
	public Integer getLastSSEResidueNumber( )
	{
		if( m_backboneConformations.isEmpty() )
		{
			return null;
		}
		return m_backboneConformations.lastKey().getStopNumber();
	}
	
	@Override
	public int getNumAtoms( )
	{
		return m_atomIndex.size();
	}
	
	@Override
	public int getNumBackboneAtoms( )
	{
		return m_backboneAtomIndex.size();
	}
	
	public AminoAcid getAminoAcid( AtomAddressReadable address )
	{
		return getResidueByNumber( address.getResidueNumber() ).getAminoAcid();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return "[Subunit] " + m_residues.size() + " residues";
	}
	
	public String dump( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( toString() );
		buf.append( "\n" );
		
		// dump the indices
		buf.append( "\tAtom Index: " );
		buf.append( m_atomIndex.size() );
		buf.append( " atoms\n" );
		buf.append( "\tBackbone Aton Index: " );
		buf.append( m_backboneAtomIndex.size() );
		buf.append( " atoms\n" );
		
		// dump the residues
		for( Residue residue : m_residues )
		{
			buf.append( residue.dump() );
		}
		return buf.toString();
	}
	
	public void addResidue( Residue residue )
	{
		residue.setId( m_residues.size() );
		m_residues.add( residue );
		m_residueIndex.put( residue.getNumber(), residue.getId() );
	}
	
	public void updateAtomIndices( )
	{
		// clear any existing indices
		m_atomIndex.clear();
		m_backboneAtomIndex.clear();
		
		// for each residue...
		for( int residueId=0; residueId<m_residues.size(); residueId++ )
		{
			// skip empty residues
			Residue residue = m_residues.get( residueId );
			if( residue == null )
			{
				continue;
			}
			residue.setId( residueId );
			
			// for each atom...
			for( int atomId=0; atomId<residue.getAtoms().size(); atomId++ )
			{
				// skip empty atoms
				Atom atom = residue.getAtoms().get( atomId );
				if( atom == null )
				{
					continue;
				}
				atom.setId( atomId );
				
				// get the atom address
				AtomAddressInternal address = new AtomAddressInternal( m_id, residueId, atomId );
				atom.setResidueId( residueId );
				
				// add the atom to the appropriate indices
				m_atomIndex.add( address );
				if( atom.isBackbone() )
				{
					m_backboneAtomIndex.add( address );
				}
			}
		}
	}
	
	public void updateResidueIndex( )
	{
		// first, sort residues by residue number
		Collections.sort( m_residues, new Comparator<Residue>( )
		{
			@Override
			public int compare( Residue a, Residue b )
			{
				return a.getNumber() - b.getNumber();
			}
		} );
		
		// then update the index
		m_residueIndex.clear();
		for( int i=0; i<m_residues.size(); i++ )
		{
			Residue residue = m_residues.get( i );
			residue.setId( i );
			m_residueIndex.put( residue.getNumber(), residue.getId() );
		}
	}
	
	@Deprecated
	public Subunit getBackbone( )
	{
		// make a copy of our subunit
		Subunit backbone = new Subunit( this );
		
		// remove any atom that's not a backbone atom
		for( Residue residue : backbone.getResidues() )
		{
			int atomId = 0;
			Iterator<Atom> iterAtom = residue.getAtoms().iterator();
			while( iterAtom.hasNext() )
			{
				Atom atom = iterAtom.next();
				
				if( !atom.isBackbone() )
				{
					iterAtom.remove();
				}
				else
				{
					atom.setId( atomId++ );
				}
			}
		}
		
		backbone.updateAtomIndices();
		
		return backbone;
	}
	
	public List<AtomAddressInternal> getInternalAddressesByResidueIds( int startId, int stopId )
	{
		ArrayList<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>();
		for( int i=startId; i<=stopId; i++ )
		{
			Residue residue = m_residues.get( i );
			for( int j=0; j<residue.getAtoms().size(); j++ )
			{
				addresses.add( new AtomAddressInternal( m_id, i, j ) );
			}
		}
		return addresses;
	}
	
	public List<AtomAddressInternal> getInternalAddressesByResidueNumbers( int startNumber, int stopNumber )
	{
		return getInternalAddressesByResidueIds( getResidueId( startNumber ), getResidueId( stopNumber ) );
	}

	public List<AtomAddressInternal> getBackboneInternalAddressesByResidueIds( int startId, int stopId )
	{
		List<AtomAddressInternal> addresses = getInternalAddressesByResidueIds( startId, stopId );
		
		// filter out non-backbone atoms
		Iterator<AtomAddressInternal> iterAddress = addresses.iterator();
		while( iterAddress.hasNext() )
		{
			if( !getAtom( iterAddress.next() ).isBackbone() )
			{
				iterAddress.remove();
			}
		}
		
		return addresses;
	}
	
	public List<AtomAddressInternal> getBackboneInternalAddressesByResidueNumbers( int startNumber, int stopNumber )
	{
		return getBackboneInternalAddressesByResidueIds( getResidueId( startNumber ), getResidueId( stopNumber ) );
	}
	
	public Sequence getSequence( )
	{
		Sequence sequence = new Sequence();
		for( Residue residue : m_residues )
		{
			sequence.addResidue( residue.getNumber(), residue.getAminoAcid() );
		}
		return sequence;
	}
	
	public Subunit getFragmentByNumbers( int startNumber, int stopNumber )
	{
		// map the numbers to indices
		int startIndex = m_residueIndex.get( startNumber );
		int stopIndex = m_residueIndex.get( stopNumber );
		
		return getFragment( startIndex, stopIndex );
	}
	
	public Subunit getFragment( int startIndex, int stopIndex )
	{
		Subunit copy = new Subunit();
		copy.m_name = m_name;
		for( int i=startIndex; i<=stopIndex; i++ )
		{
			copy.addResidue( new Residue( m_residues.get( i ) ) );
		}
		copy.updateAtomIndices();
		return copy;
	}
	
	public double getPhiAngleByNumber( int residueNumber )
	{
		return getPhiAngle( getResidueId( residueNumber ) );
	}
	
	public double getPsiAngleByNumber( int residueNumber )
	{
		return getPsiAngle( getResidueId( residueNumber ) );
	}

	public double getPhiAngle( int id )
	{
		return ProteinGeometry.getDihedralAngle(
			getAtomOrThrow( id - 1, "C" ).getPosition(),
			getAtomOrThrow( id, "N" ).getPosition(),
			getAtomOrThrow( id, "CA" ).getPosition(),
			getAtomOrThrow( id, "C" ).getPosition()
		);
	}
	
	public double getPsiAngle( int id )
	{
		return ProteinGeometry.getDihedralAngle(
			getAtomOrThrow( id, "N" ).getPosition(),
			getAtomOrThrow( id, "CA" ).getPosition(),
			getAtomOrThrow( id, "C" ).getPosition(),
			getAtomOrThrow( id + 1, "N" ).getPosition()
		);
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private Atom getAtomOrThrow( int id, String atomName )
	{
		Residue residue = getResidue( id );
		if( residue == null )
		{
			throw new IllegalArgumentException( "Missing residue " + getSequence().getResidueNumber( id ) + "!" );
		}
		Atom atom = residue.getAtomByName( atomName );
		if( atom == null )
		{
			throw new IllegalArgumentException( "Missing atom " + atomName + " in residue " + getSequence().getResidueNumber( id ) + "!" );
		}
		return atom;
	}
}
