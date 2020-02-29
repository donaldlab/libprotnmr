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

public class ProfilerCounter implements Comparable<ProfilerCounter>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_name;
	private long m_elapsedMilliseconds;
	private Timer m_timer;
	private double m_percentTime;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ProfilerCounter( String name )
	{
		// save parameters
		m_name = name;
		
		// init defaults
		m_elapsedMilliseconds = 0;
		m_timer = null;
		m_percentTime = 0.0;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	
	public long getElapsedMilliseconds( )
	{
		return m_elapsedMilliseconds;
	}
	
	public double getPercentTime( )
	{
		return m_percentTime;
	}
	
	public void setPercentTime( double val )
	{
		m_percentTime = val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void start( )
	{
		m_timer = new Timer();
		m_timer.start();
	}
	
	public void stop( )
	{
		m_timer.stop();
		m_elapsedMilliseconds += m_timer.getElapsedMilliseconds();
		m_timer = null;
	}
	
	@Override
	public int compareTo( ProfilerCounter other )
	{
		if( m_percentTime > other.m_percentTime )
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}	
}
