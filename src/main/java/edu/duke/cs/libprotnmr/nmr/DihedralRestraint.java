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
import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.HasAddresses;


public class DihedralRestraint<T extends AtomAddress<T>> implements HasAddresses<T>, Serializable
{
	private static final long serialVersionUID = 6723778611238526057L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private T m_a;
	private T m_b;
	private T m_c;
	private T m_d;
	private double m_value;
	private double m_error;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public DihedralRestraint( T a, T b, T c, T d, double value, double error )
	{
		m_a = a;
		m_b = b;
		m_c = c;
		m_d = d;
		m_value = value;
		m_error = error;
	}
	
	public DihedralRestraint( DihedralRestraint<T> other )
	{
		m_a = other.m_a.newCopy();
		m_b = other.m_b.newCopy();
		m_c = other.m_c.newCopy();
		m_d = other.m_d.newCopy();
		m_value = other.m_value;
		m_error = other.m_error;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public T getA( )
	{
		return m_a;
	}
	public void setA( T val )
	{
		m_a = val;
	}
	
	public T getB( )
	{
		return m_b;
	}
	public void setB( T val )
	{
		m_b = val;
	}
	
	public T getC( )
	{
		return m_c;
	}
	public void setC( T val )
	{
		m_c = val;
	}
	
	public T getD( )
	{
		return m_d;
	}
	public void setD( T val )
	{
		m_d = val;
	}
	
	@Override
	public Iterable<T> addresses( )
	{
		List<T> addresses = new ArrayList<T>();
		addresses.add( m_a );
		addresses.add( m_b );
		addresses.add( m_c );
		addresses.add( m_d );
		return addresses;
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
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof DihedralRestraint<?> )
		{
			// no way to check this cast...
			return equals( (DihedralRestraint<T>)other );
		}
		return false;
	}
	
	public boolean equals( DihedralRestraint<T> other )
	{
		return m_a.equals( other.m_a )
			&& m_b.equals( other.m_b )
			&& m_c.equals( other.m_c )
			&& m_d.equals( other.m_d )
			&& m_value == other.m_value
			&& m_error == other.m_error;
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_a.hashCode(),
			m_b.hashCode(),
			m_c.hashCode(),
			m_d.hashCode(),
			Double.valueOf( m_value ).hashCode(),
			Double.valueOf( m_error ).hashCode()
		);
	}
	
	@Override
	public String toString( )
	{
		return String.format( "[DihedralRestraint] %.2f,%.2f %s %s %s %s",
			m_value,
			m_error,
			m_a,
			m_b,
			m_c,
			m_d
		);
	}
}
