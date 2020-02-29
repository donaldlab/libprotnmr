/*
 * This file is part of LibProtNMR
 *
 * Copyright (C) 2020 Bruce Donald Lab, Duke University
 *
 * LibProtNMR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibProtNMR.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Info:
 *    Bruce Donald
 *    Duke University
 *    Department of Computer Science
 *    Levine Science Research Center (LSRC)
 *    Durham
 *    NC 27708-0129
 *    USA
 *    e-mail: www.cs.duke.edu/brd/
 *
 * <signature of Bruce Donald>, February, 2020
 * Bruce Donald, Professor of Computer Science
 */

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
