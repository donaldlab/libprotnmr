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
