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
