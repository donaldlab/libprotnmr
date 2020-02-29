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

package edu.duke.cs.libprotnmr.perf;

import java.io.PrintStream;

public class StreamMessageListener implements MessageListener
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String DefaultPrefix = "";
	private static final PrintStream DefaultStream = System.out;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private PrintStream m_stream;
	private String m_prefix;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public StreamMessageListener( )
	{
		this( DefaultStream, DefaultPrefix );
	}

	public StreamMessageListener( PrintStream stream )
	{
		this( stream, DefaultPrefix );
	}
	
	public StreamMessageListener( PrintStream stream, String prefix )
	{
		m_stream = stream;
		m_prefix = prefix;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public void message( String msg )
	{
		if( m_stream != null )
		{
			m_stream.print( m_prefix );
			m_stream.println( msg );
		}
	}
}
