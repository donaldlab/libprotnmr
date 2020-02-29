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

package edu.duke.cs.libprotnmr.cgal;

public abstract class AbstractCleanable
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_id;
	@SuppressWarnings( "unused" )
	private long m_pointer;
	private Cleaner m_cleaner;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	protected AbstractCleanable( )
	{
		// no args constructor for serialization of sub-types
		this( -1, -1, null );
	}
	
	protected AbstractCleanable( Cleaner cleaner )
	{
		this( -1, 0, cleaner );
	}
	
	protected AbstractCleanable( int id, long pointer, Cleaner cleaner )
	{
		m_id = id;
		m_pointer = pointer;
		m_cleaner = cleaner;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void cleanup( )
	{
		if( m_id >= 0 )
		{
			m_cleaner.cleanup( m_id );
			m_id = -1;
		}
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	protected int getId( )
	{
		return m_id;
	}
	protected void setId( int val )
	{
		m_id = val;
	}
	
	protected void setCleaner( Cleaner cleaner )
	{
		m_cleaner = cleaner;
	}
}
