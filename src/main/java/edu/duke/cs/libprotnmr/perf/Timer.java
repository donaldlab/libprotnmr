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

public class Timer
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_name;
	private long m_startTime;
	private long m_stopTime;
	private boolean m_isRunning;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Timer( )
	{
		this( "Timer" );
	}
	
	public Timer( String name )
	{
		m_name = name;
		m_startTime = 0;
		m_stopTime = 0;
		m_isRunning = false;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	
	public long getStartTime( )
	{
		return m_startTime;
	}
	
	public long getStopTime( )
	{
		return m_stopTime;
	}
	
	public boolean isRunning( )
	{
		return m_isRunning;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return m_name + " : " + getElapsedTime();
	}
	
	public void start( )
	{
		m_isRunning = true;
		m_startTime = getTime();
		m_stopTime = -1;
	}
	
	public void stop( )
	{
		m_isRunning = false;
		m_stopTime = getTime();
	}
	
	public long getElapsedMilliseconds( )
	{
		if( m_isRunning )
		{
			return getTime() - m_startTime;
		}
		else
		{
			return m_stopTime - m_startTime;
		}
	}
	
	public float getElapsedSeconds( )
	{
		return getElapsedMilliseconds() / 1000.0f;
	}
	
	public float getElapsedMinutes( )
	{
		return getElapsedMilliseconds() / 1000.0f / 60.0f;
	}
	
	public float getElapsedHours( )
	{
		return getElapsedMilliseconds() / 1000.0f / 60.0f / 60.0f;
	}

	public String getElapsedTime( )
	{
		float seconds = getElapsedSeconds();
		if( seconds < 60.0 )
		{
			return String.format( "%.2fs", seconds );
		}
		
		float minutes = getElapsedMinutes();
		if( minutes < 60 )
		{
			return String.format( "%.2fm", minutes );
		}
		
		float hours = getElapsedHours();
		return String.format( "%.2fh", hours );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private long getTime( )
	{
		return System.currentTimeMillis();
	}
}
