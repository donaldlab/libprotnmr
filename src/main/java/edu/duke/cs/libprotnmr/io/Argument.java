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

package edu.duke.cs.libprotnmr.io;

public class Argument
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_name;
	private ArgumentType m_type;
	private String m_flag;
	private String m_defaultValue;
	private String m_description;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Argument( String name, ArgumentType type, String description )
	{
		this( name, type, null, null, description );
	}
	
	public Argument( String name, ArgumentType type, String flag, String defaultValue, String description )
	{
		m_name = name;
		m_type = type;
		m_flag = flag;
		m_defaultValue = defaultValue;
		m_description = description;
	}


	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	
	public ArgumentType getType( )
	{
		return m_type;
	}
	
	public String getFlag( )
	{
		return m_flag;
	}
	
	public String getDefaultValue( )
	{
		return m_defaultValue;
	}
	
	public String getDescription( )
	{
		return m_description;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void validate( String value )
	throws ArgumentException
	{
		try
		{
			m_type.validate( value );
		}
		catch( ArgumentException ex )
		{
			// add the argument name and rethrow
			throw new ArgumentException( m_name + ": " + ex.getMessage() );
		}
	}
	
	public boolean isStatic( )
	{
		return m_flag == null;
	}
}
	
