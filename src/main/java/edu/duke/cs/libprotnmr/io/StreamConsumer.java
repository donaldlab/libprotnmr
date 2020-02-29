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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class StreamConsumer implements Runnable
{
	/**************************
	 *   Data Members
	 **************************/
	
	private InputStream m_in;
	private Writer m_out;
	private LineReadListener m_lineReadListener;
	private FilterMatchListener m_filterMatchListener;
	private Thread m_thread;
	private String m_filter;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public StreamConsumer( InputStream in )
	{
		m_in = in;
		m_lineReadListener = null;
		m_filterMatchListener = null;
		m_thread = null;
		m_filter = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setLineReadListener( LineReadListener val )
	{
		m_lineReadListener = val;
	}
	
	public void setFilter( String filter, FilterMatchListener listener )
	{
		m_filter = filter;
		m_filterMatchListener = listener;
	}
	
	public void setOut( Writer val )
	{
		m_out = val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void start( )
	{
		// start a new thread to consume the stream
		m_thread = new Thread( this );
		m_thread.start();
	}
	
	public void waitFor( )
	throws InterruptedException
	{
		m_thread.join();
	}
	
	public void run( )
	{
		try
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( m_in ) );
			String line = null;
			while( ( line = reader.readLine() ) != null )
			{
				// does this line pass the filter?
				if( m_filterMatchListener != null && ( m_filter == null || line.matches( m_filter ) ) )
				{
					m_filterMatchListener.filterMatch( line );
				}
				
				// notify the listener about the line we read if needed
				if( m_lineReadListener != null )
				{
					m_lineReadListener.lineRead( line );
				}
				
				// pipe this stream to out if needed
				if( m_out != null )
				{
					m_out.write( line );
					
					// but put the newline back on
					m_out.write( "\n" );
				}
			}
		}
		catch( IOException ex )
		{
			throw new Error( "StreamConsumer failed!" );
		}
	}
}
