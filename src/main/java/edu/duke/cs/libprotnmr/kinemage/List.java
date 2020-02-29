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

package edu.duke.cs.libprotnmr.kinemage;

public class List extends ContainerNode
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_type;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public List( String type, String name )
	{
		super( name );
		m_type = type;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getType( )
	{
		return m_type;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	protected void render( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( "@" );
		buf.append( m_type );
		buf.append( "list " );
		
		renderName( buf );
		
		if( getOptions().size() > 0 )
		{
			buf.append( " " );
			renderOptions( buf );
		}
		
		m_text = buf.toString();
	}
}
