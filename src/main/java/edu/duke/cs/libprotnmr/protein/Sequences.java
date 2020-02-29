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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Sequences implements Iterable<Map.Entry<Character,Sequence>>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Map<Character,Sequence> m_sequences;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Sequences( )
	{
		m_sequences = new TreeMap<Character,Sequence>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public Sequence getSequence( char name )
	{
		return m_sequences.get( name );
	}
	
	public Sequence getSequence( AtomAddressReadable address )
	{
		if( address.hasSubunitName() )
		{
			return getSequence( address.getSubunitName() );
		}
		else
		{
			if( m_sequences.size() == 1 )
			{
				return m_sequences.values().iterator().next();
			}
			else
			{
				throw new IllegalArgumentException( "Address doesn't have a subunit assignment. Unable to find in sequence. Use Sequence class methods instead." );
			}
		}
	}
	
	public AminoAcid getAminoAcid( char subunitName, int residueNumber )
	{
		Sequence sequence = getSequence( subunitName );
		if( sequence == null )
		{
			return null;
		}
		return sequence.getAminoAcidByNumber( residueNumber );
	}
	
	public AminoAcid getAminoAcid( AtomAddressReadable address )
	{
		if( address.hasSubunitName() )
		{
			return getAminoAcid( address.getSubunitName(), address.getResidueNumber() );
		}
		else
		{
			if( m_sequences.size() == 1 )
			{
				return m_sequences.values().iterator().next().getAminoAcid( address );
			}
			else
			{
				throw new IllegalArgumentException( "Address doesn't have a subunit assignment. Unable to find in sequence. Use Sequence class methods instead." );
			}
		}
	}
	
	public int getNumSequences( )
	{
		return m_sequences.size();
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Sequences getUniqueSequences( List<Protein> ensemble )
	{
		Sequences sequences = ensemble.get( 0 ).getSequences();
		for( int i=1; i<ensemble.size(); i++ )
		{
			if( !ensemble.get( i ).getSequences().equals( sequences ) )
			{
				throw new IllegalArgumentException( "Not all proteins in the ensemble have the same sequences!" );
			}
		}
		return sequences;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void add( char name, Sequence sequence )
	{
		m_sequences.put( name, sequence );
	}
	
	@Override
	public Iterator<Map.Entry<Character,Sequence>> iterator( )
	{
		return m_sequences.entrySet().iterator();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Sequences )
		{
			return equals( (Sequences)other );
		}
		return false;
	}
	
	public boolean equals( Sequences other )
	{
		if( m_sequences.size() != other.m_sequences.size() )
		{
			return false;
		}
		for( Entry<Character,Sequence> entry : m_sequences.entrySet() )
		{
			if( !entry.getValue().equals( other.m_sequences.get( entry.getKey() ) ) )
			{
				return false;
			}
		}
		return true;
	}
}
