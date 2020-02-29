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

import java.util.HashMap;

public enum Element
{
	/**************************
	 *   Values
	 **************************/
	
	Unknown( "?", false ),
	Carbon( "C", false ),
	Oxygen( "O", false ),
	Nitrogen( "N", false ),
	Hydrogen( "H", false ),
	Sulfur( "S", false ),
	PseudoatomSmall( "M", true ),
	PseudoatomLarge( "Q", true );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_code;
	private boolean m_isPseudoatom;
	private static HashMap<String,Element> m_codeMap = new HashMap<String,Element>();
	

	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		// build the code map
		for( Element element : Element.values() )
		{
			m_codeMap.put( element.getCode().toLowerCase(), element );
		}
	}
	
	private Element( String code, boolean isPseudoatom )
	{
		m_code = code;
		m_isPseudoatom = isPseudoatom;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getCode( )
	{
		return m_code;
	}
	
	public boolean isPseudoatom( )
	{
		return m_isPseudoatom;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Element getByCode( String code )
	{
		Element element = m_codeMap.get( code.toLowerCase() );
		if( element == null )
		{
			return Unknown;
		}
		return element;
	}
	
	public static Element getByAtomName( String name )
	{
		// just take the first letter of the name
		return getByCode( name.substring( 0, 1 ) );
	}
}
