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
