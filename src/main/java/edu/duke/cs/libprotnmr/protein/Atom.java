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

import edu.duke.cs.libprotnmr.geom.Vector3;

public class Atom implements Comparable<Atom>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_id;
	private int m_number;
	private String m_name;
	private int m_residueId;
	private Element m_element;
	private boolean m_isBackbone;
	private boolean m_isPseudoatom;
	public Vector3 m_position;
	public float m_occupancy;
	public float m_tempFactor;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Atom( )
	{
		m_id = -1;
		m_number = 0;
		m_name = "";
		m_residueId = -1;
		m_element = Element.Unknown;
		m_isBackbone = false;
		m_isPseudoatom = false;
		m_position = Vector3.getOrigin();
		m_occupancy = 0.0f;
		m_tempFactor = 0.0f;
	}
	
	public Atom( Atom other )
	{
		m_id = other.m_id;
		m_number = other.m_number;
		m_name = other.m_name;
		m_residueId = other.m_residueId;
		m_element = other.m_element;
		m_isBackbone = other.m_isBackbone;
		m_isPseudoatom = other.m_isPseudoatom;
		m_position = new Vector3( other.m_position );
		m_occupancy = other.m_occupancy;
		m_tempFactor = other.m_tempFactor;
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
	
	public int getNumber( )
	{
		return m_number;
	}
	public void setNumber( int value )
	{
		m_number = value;
	}
	
	public String getName( )
	{
		return m_name;
	}
	public void setName( String value )
	{
		m_name = value;
	}
	
	public int getResidueId( )
	{
		return m_residueId;
	}
	public void setResidueId( int value )
	{
		m_residueId = value;
	}
	
	public Element getElement( )
	{
		return m_element;
	}
	public void setElement( Element value )
	{
		m_element = value;
	}
	
	public boolean isBackbone( )
	{
		return m_isBackbone;
	}
	public void setIsBackbone( boolean value )
	{
		m_isBackbone = value;
	}
	
	public boolean isPseudoatom( )
	{
		return m_isPseudoatom;
	}
	public void setIsPseudoatom( boolean value )
	{
		m_isPseudoatom = value;
	}
	
	public Vector3 getPosition( )
	{
		return m_position;
	}
	public void setPosition( Vector3 value )
	{
		m_position = value;
	}
	
	public float getOccupancy( )
	{
		return m_occupancy;
	}
	public void setOccupancy( float value )
	{
		m_occupancy = value;
	}
	
	public float getTempFactor( )
	{
		return m_tempFactor;
	}
	public void setTempFactor( float value )
	{
		m_tempFactor = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		StringBuffer buf = new StringBuffer();
		buf.append( "[Atom] " );
		buf.append( m_id );
		buf.append( ":" );
		buf.append( m_name );
		buf.append( " " );
		buf.append( m_position );
		return buf.toString();
	}
	
	public int hashCode( )
	{
		return m_number;
	}
	
	public int compareTo( Atom other )
	{
		// return negative if we're less than other
		return m_number - other.m_number;
	}
}
