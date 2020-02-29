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

import java.util.ArrayList;

public class ContainerNode extends Node
{
	/**************************
	 *   Data Members
	 **************************/
	
	protected String m_name;
	protected ArrayList<String> m_options;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	protected ContainerNode( String name )
	{
		m_name = name;
		m_options = new ArrayList<String>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	public void setName( String value )
	{
		m_name = value;
	}
	
	public ArrayList<String> getOptions( )
	{
		return m_options;
	}
	
	
	/**************************
	 *   Methods
	 **************************/

	public void addOption( String option )
	{
		m_options.add( option );
	}
	
	public void addColor( KinemageColor color )
	{
		addOption( "color= " + color.name() );
	}
	

	/**************************
	 *   Functions
	 **************************/
	
	protected void renderOptions( StringBuffer buf )
	{
		boolean isFirst = true;
		for( String option : m_options )
		{
			if( !isFirst )
			{
				buf.append( " " );
			}
			buf.append( option );
			isFirst = false;
		}
	}
	
	protected void renderName( StringBuffer buf )
	{
		buf.append( "{" );
		buf.append( m_name );
		buf.append( "}" );
	}
}
