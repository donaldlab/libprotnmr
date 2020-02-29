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

public class Node
{
	/**************************
	 *   Data Members
	 **************************/
	
	protected String m_text;
	protected ArrayList<Node> m_children;
	

	/**************************
	 *   Constructors
	 **************************/
	
	public Node( )
	{
		this( "" );
	}
	
	public Node( String text )
	{
		m_text = text;
		m_children = new ArrayList<Node>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getText( )
	{
		render();
		return m_text;
	}
	public void setText( String value )
	{
		m_text = value;
	}
	
	public ArrayList<Node> getChildren( )
	{
		return m_children;
	}


	/**************************
	 *   Accessors
	 **************************/
	
	public void addNode( Node node )
	{
		m_children.add( node );
	}


	/**************************
	 *   Functions
	 **************************/

	protected void render( )
	{
		// override me!
	}
}
