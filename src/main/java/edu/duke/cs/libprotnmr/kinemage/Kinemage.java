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

public class Kinemage
{
	/**************************
	 *   Definitions
	 **************************/
	
	public enum BackgroundColor
	{
		Black( false ),
		White( true );
		
		private boolean m_isWhite;
		
		private BackgroundColor( boolean isWhite )
		{
			m_isWhite = isWhite;
		}
		
		public boolean isWhite( )
		{
			return m_isWhite;
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private Node m_root;
	private int m_nextKinemage;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Kinemage( )
	{
		this( "Kinemage 1", BackgroundColor.Black );
	}
	
	public Kinemage( String title )
	{
		this( title, BackgroundColor.Black );
	}
	
	public Kinemage( String title, BackgroundColor backgroundColor )
	{
		m_root = new Node( "" );
		m_nextKinemage = 1;
		
		startNewKinemage( title, backgroundColor );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public Node getRoot( )
	{
		return m_root;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void startNewKinemage( )
	{
		startNewKinemage( "Another Kinemage", BackgroundColor.Black );
	}
	
	public void startNewKinemage( String title )
	{
		startNewKinemage( title, BackgroundColor.Black );
	}
	
	public void startNewKinemage( String title, BackgroundColor backgroundColor )
	{
		// add a node...
		// write the kinemage header;
		m_root.addNode( new Node( "@kinemage " + m_nextKinemage + "\n" ) );
		m_root.addNode( new Node( "@title {" + title + "}\n" ) );
		m_root.addNode( KinemageColor.getAllColors() );
		if( backgroundColor.isWhite() )
		{
			m_root.addNode( new Node( "@whitebackground\n" ) );
		}
		m_nextKinemage++;
	}
}
