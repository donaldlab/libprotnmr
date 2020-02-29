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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class LineIterator implements Iterator<String>
{
	/**************************
	 *   DataMembers
	 **************************/
	
	private BufferedReader m_reader;
	private String m_nextLine;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public LineIterator( Reader reader )
	{
		// save parameters
		m_reader = new BufferedReader( reader );
		
		// init defaults
		m_nextLine = getNextLine();
	}
	

	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean hasNext( )
	{
		return m_nextLine != null;
	}

	@Override
	public String next( )
	{
		String ret = m_nextLine;
		m_nextLine = getNextLine();
		return ret;
	}

	@Override
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private String getNextLine( )
	throws RuntimeException
	{
		// interators can't throw exceptions, so recast as runtime exception
		try
		{
			return m_reader.readLine();
		}
		catch( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
