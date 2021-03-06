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

package edu.duke.cs.libprotnmr.nmr;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.HasAddresses;


public class ChemicalShift<T extends AtomAddress<T>> implements HasAddresses<T>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_number;
	private T m_address;
	private AminoAcid m_aminoAcid;
	private Element m_element;
	private double m_value;
	private double m_error;
	private int m_ambiguityCode;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ChemicalShift( )
	{
		m_number = -1;
		m_address = null;
		m_aminoAcid = null;
		m_element = null;
		m_value = Double.NaN;
		m_error = Double.NaN;
		m_ambiguityCode = -1;
	}
	
	public ChemicalShift( ChemicalShift<T> other )
	{
		m_number = other.m_number;
		m_address = other.m_address.newCopy();
		m_aminoAcid = other.m_aminoAcid;
		m_element = other.m_element;
		m_value = other.m_value;
		m_error = other.m_error;
		m_ambiguityCode = other.m_ambiguityCode;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getNumber( )
	{
		return m_number;
	}
	public void setNumber( int val )
	{
		m_number = val;
	}
	
	public T getAddress( )
	{
		return m_address;
	}
	public void setAddress( T val )
	{
		m_address = val;
	}
	
	@Override
	public Iterable<T> addresses( )
	{
		List<T> addresses = new ArrayList<T>();
		addresses.add( m_address );
		return addresses;
	}
	
	public AminoAcid getAminoAcid( )
	{
		return m_aminoAcid;
	}
	public void setAminoAcid( AminoAcid val )
	{
		m_aminoAcid = val;
	}
	
	public Element getElement( )
	{
		return m_element;
	}
	public void setElement( Element element )
	{
		m_element = element;
	}
	
	public double getValue( )
	{
		return m_value;
	}
	public void setValue( double val )
	{
		m_value = val;
	}
	
	public double getError( )
	{
		return m_error;
	}
	public void setError( double val )
	{
		m_error = val;
	}
	
	public int getAmbiguityCode( )
	{
		return m_ambiguityCode;
	}
	public void setAmbiguityCode( int val )
	{
		m_ambiguityCode = val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		StringBuffer buf = new StringBuffer();
		buf.append( "[ChemicalShift] " );
		buf.append( m_address );
		buf.append( ", " );
		buf.append( m_value );
		buf.append( ", " );
		buf.append( m_error );
		return buf.toString();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( !( other instanceof ChemicalShift ) )
		{
			return false;
		}
		// NOTE: don't try to .equals() chemical shifts with different address types
		return equals( (ChemicalShift<T>)other );
	}
	
	public boolean equals( ChemicalShift<T> other )
	{
		return m_address.equals( other.m_address )
			&& m_value == other.m_value
			&& m_error == other.m_error;
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_address.hashCode(),
			Double.valueOf( m_value ).hashCode(),
			Double.valueOf( m_error ).hashCode()
		);
	}
}
