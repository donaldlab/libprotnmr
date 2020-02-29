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
