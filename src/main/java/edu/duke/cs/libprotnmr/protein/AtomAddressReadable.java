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

import java.io.Serializable;

import edu.duke.cs.libprotnmr.io.HashCalculator;



public class AtomAddressReadable implements AtomAddress<AtomAddressReadable>, Comparable<AtomAddressReadable>, Serializable
{
	private static final long serialVersionUID = 5840565671099321789L;
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	private static final char OmittedSubunitName = '?';
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private char m_subunitName;
	private int m_residueNumber;
	private String m_atomName;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AtomAddressReadable( )
	{
		m_subunitName = OmittedSubunitName;
		m_residueNumber = -1;
		m_atomName = null;
	}
	
	public AtomAddressReadable( char subunitName, int residueNumber, String atomName )
	{
		m_subunitName = Character.toUpperCase( subunitName );
		m_residueNumber = residueNumber;
		m_atomName = atomName.toUpperCase();
	}
	
	public AtomAddressReadable( int residueNumber, String atomName )
	{
		m_subunitName = OmittedSubunitName;
		m_residueNumber = residueNumber;
		m_atomName = atomName.toUpperCase();
	}
	
	public AtomAddressReadable( AtomAddressReadable other )
	{
		m_subunitName = other.m_subunitName;
		m_residueNumber = other.m_residueNumber;
		m_atomName = other.m_atomName;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public char getSubunitName( )
	{
		return m_subunitName;
	}
	public void setSubunitName( char value )
	{
		m_subunitName = Character.toUpperCase( value );
	}
	public boolean hasSubunitName( )
	{
		return m_subunitName != OmittedSubunitName;
	}
	public void omitSubunitName( )
	{
		m_subunitName = OmittedSubunitName;
	}
	
	public int getResidueNumber( )
	{
		return m_residueNumber;
	}
	public void setResidueNumber( int value )
	{
		m_residueNumber = value;
	}
	
	public String getAtomName( )
	{
		return m_atomName;
	}
	public void setAtomName( String value )
	{
		m_atomName = value.toUpperCase();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return m_subunitName + ":" + m_residueNumber + ":" + m_atomName;
	}
	
	public String toResidueAtomString( )
	{
		return m_residueNumber + ":" + m_atomName;
	}
	
	@Override
	public boolean isAmbiguous( )
	{
		return m_atomName.indexOf( '#' ) != -1 || m_atomName.indexOf( '*' ) != -1;
	}
	
	@Override
	public int compareTo( AtomAddressReadable other )
	{
		int diff = 0;
		
		diff = Character.valueOf( m_subunitName ).compareTo( other.m_subunitName );
		if( diff != 0 )
		{
			return diff;
		}
		
		diff = m_residueNumber - other.m_residueNumber;
		if( diff != 0 )
		{
			return diff;
		}
		
		diff = m_atomName.compareTo( other.m_atomName );
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
        
        return equals( (AtomAddressReadable)other );
	}
	
	public boolean equals( AtomAddressReadable other )
	{
		// NOTE: subunitName and atomName are always upper case!
		return m_subunitName == other.m_subunitName
			&& m_residueNumber == other.m_residueNumber
			&& m_atomName.equals( other.m_atomName );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Character.valueOf( m_subunitName ).hashCode(),
			m_residueNumber,
			m_atomName.hashCode()
		);
	}
	
	@Override
	public AtomAddressReadable newCopy( )
	{
		return new AtomAddressReadable( this );
	}
	
	public AtomAddressReadable newCopyNoSubunit( )
	{
		return new AtomAddressReadable( m_residueNumber, m_atomName );
	}
	
	@Override
	public boolean isSameSubunit( AtomAddressReadable other )
	{
		return m_subunitName == other.m_subunitName;
	}
	
	@Override
	public boolean isSameAtom( AtomAddressReadable other )
	{
		return m_atomName.equalsIgnoreCase( other.m_atomName );
	}
}
