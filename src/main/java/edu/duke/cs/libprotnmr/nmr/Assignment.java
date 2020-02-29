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

import java.io.Serializable;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.protein.AtomAddress;


public class Assignment<T extends AtomAddress<T>> implements Serializable
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = -5512426021726265727L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private T m_left;
	private T m_right;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Assignment( )
	{
		m_left = null;
		m_right = null;
	}
	
	public Assignment( T left, T right )
	{
		m_left = left;
		m_right = right;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public T getLeft( )
	{
		return m_left;
	}
	public void setLeft( T value )
	{
		m_left = value;
	}

	public T getRight( )
	{
		return m_right;
	}
	public void setRight( T value )
	{
		m_right = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void swap( )
	{
		T swap = m_left;
		m_left = m_right;
		m_right = swap;
	}
	
	public boolean isIntermolecular( )
	{
		return !m_left.isSameSubunit( m_right );
	}
	
	@Override
	public String toString( )
	{
		return "[Assignment] " + m_left + "\t" + m_right;
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes( m_left.hashCode(), m_right.hashCode() );
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals( Object other )
	{
		if( other instanceof Assignment<?> )
		{
			return equals( (Assignment<T>)other );
		}
		return false;
	}
	
	public boolean equals( Assignment<T> other )
	{
		return m_left.equals( other.m_left ) && m_right.equals( other.m_right );
	}
}
