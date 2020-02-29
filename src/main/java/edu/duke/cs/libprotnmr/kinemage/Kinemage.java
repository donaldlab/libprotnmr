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
