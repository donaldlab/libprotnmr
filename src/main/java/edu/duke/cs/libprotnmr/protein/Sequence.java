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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class Sequence implements Iterable<Sequence.Entry>, Serializable
{
	private static final long serialVersionUID = 103974235879986626L;
	

	/**************************
	 *   Data Members
	 **************************/
	
	public static class Entry implements Serializable
	{
		private static final long serialVersionUID = 8095434014292944226L;
		
		public int residueNumber;
		public AminoAcid aminoAcid;
		
		public Entry( int residueNumber, AminoAcid aminoAcid )
		{
			this.residueNumber = residueNumber;
			this.aminoAcid = aminoAcid;
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private List<Entry> m_entries;
	private TreeMap<Integer,Integer> m_index;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Sequence( )
	{
		m_entries = new ArrayList<Entry>();
		m_index = new TreeMap<Integer,Integer>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public boolean hasResidueNumber( int residueNumber )
	{
		return m_index.containsKey( residueNumber );
	}
	
	public Integer getResidueNumber( int id )
	{
		Entry entry = m_entries.get( id );
		if( entry == null )
		{
			return null;
		}
		return entry.residueNumber;
	}
	
	public Integer getId( int residueNumber )
	{
		return m_index.get( residueNumber );
	}
	
	public AminoAcid getAminoAcidById( int id )
	{
		Entry entry = m_entries.get( id );
		if( entry == null )
		{
			return null;
		}
		return entry.aminoAcid;
	}
	
	public AminoAcid getAminoAcidByNumber( int number )
	{
		Integer id = m_index.get( number );
		if( id == null )
		{
			return null;
		}
		return getAminoAcidById( id );
	}
	
	public AminoAcid getAminoAcid( AtomAddressReadable address )
	{
		return getAminoAcidByNumber( address.getResidueNumber() );
	}
	
	public int getLength( )
	{
		return m_entries.size();
	}
	
	public ResidueType getResidueTypeById( int id )
	{
		return ResidueType.valueOf( this, id );
	}
	
	public ResidueType getResidueTypeByNumber( int residueNumber )
	{
		if( !hasResidueNumber( residueNumber ) )
		{
			return null;
		}
		return getResidueTypeById( getId( residueNumber ) );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addResidue( int residueNumber, AminoAcid aminoAcid )
	{
		m_index.put( residueNumber, m_entries.size() );
		m_entries.add( new Entry( residueNumber, aminoAcid ) );
	}
	
	@Override
	public Iterator<Entry> iterator( )
	{
		return m_entries.iterator();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Sequence )
		{
			return equals( (Sequence)other );
		}
		return false;
	}
	
	public boolean equals( Sequence other )
	{
		if( getLength() != other.getLength() )
		{
			return false;
		}
		for( int i=0; i<getLength(); i++ )
		{
			if( getAminoAcidById( i ) != other.getAminoAcidById( i ) )
			{
				return false;
			}
		}
		return true;
	}
}
