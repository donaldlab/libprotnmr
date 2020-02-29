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


public class HomoSequences extends Sequences
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Sequence m_sequence;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public HomoSequences( Sequence sequence )
	{
		m_sequence = sequence;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	@Override
	public Sequence getSequence( char name )
	{
		return m_sequence;
	}
	
	@Override
	public Sequence getSequence( AtomAddressReadable address )
	{
		return m_sequence;
	}
	
	@Override
	public AminoAcid getAminoAcid( char subunitName, int residueNumber )
	{
		return m_sequence.getAminoAcidByNumber( residueNumber );
	}
	
	@Override
	public AminoAcid getAminoAcid( AtomAddressReadable address )
	{
		return m_sequence.getAminoAcidByNumber( address.getResidueNumber() );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void add( char name, Sequence sequence )
	{
		throw new UnsupportedOperationException( "Don't modify HomoSequences instances!" );
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof HomoSequences )
		{
			return equals( (HomoSequences)other );
		}
		return false;
	}
	
	public boolean equals( HomoSequences other )
	{
		return m_sequence.equals( other.m_sequence );
	}
}
