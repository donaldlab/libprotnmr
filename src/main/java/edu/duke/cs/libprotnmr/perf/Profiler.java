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

import java.util.HashMap;
import java.util.PriorityQueue;

public class Profiler
{
	/**************************
	 *   Data Members
	 **************************/
	
	private static HashMap<String,ProfilerCounter> m_counters;
	

	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		reset();
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void start( String name )
	{
		ProfilerCounter counter = m_counters.get( name );
		
		if( counter == null )
		{
			counter = new ProfilerCounter( name );
			m_counters.put( name, counter );
		}
		
		counter.start();
	}
	
	public static void stop( String name )
	{
		ProfilerCounter counter = m_counters.get( name );
		counter.stop();
	}
	
	public static void reset( )
	{
		m_counters = new HashMap<String,ProfilerCounter>();
	}
	
	public static String getReport( )
	{
		// calculate total time
		long totalTime = 0;
		for( ProfilerCounter counter : m_counters.values() )
		{
			totalTime += counter.getElapsedMilliseconds();
		}
		
		// update percentages
		for( ProfilerCounter counter : m_counters.values() )
		{
			counter.setPercentTime( 100.0 * (double)counter.getElapsedMilliseconds() / (double)totalTime );
		}
		
		// sort the counters
		PriorityQueue<ProfilerCounter> order = new PriorityQueue<ProfilerCounter>();
		for( ProfilerCounter counter : m_counters.values() )
		{
			order.add( counter );
		}
		
		// build the report
		StringBuilder buf = new StringBuilder();
		buf.append( "Profiling Report:\n" );
		ProfilerCounter counter = null;
		while( ( counter = order.poll() ) != null )
		{
			buf.append( String.format( "%8.2f", (double)counter.getElapsedMilliseconds() / 1000.0 ) );
			buf.append( "s (" );
			buf.append( String.format( "%6.2f", counter.getPercentTime() ) );
			buf.append( "%): " );
			buf.append( counter.getName() );
			buf.append( "\n" );
		}
		
		return buf.toString();
	}
	
	public static String getMemoryUsed( )
	{
		long usedBytes = Runtime.getRuntime().totalMemory();
		
		double usedKibibytes = (double)usedBytes / 1024.0;
		if( usedKibibytes < 1000.0 )
		{
			return String.format( "%.2f", usedKibibytes ) + "KiB";
		}
		
		double usedMebibytes = usedKibibytes / 1024.0;
		if( usedMebibytes < 1000.0 )
		{
			return String.format( "%.2f", usedMebibytes ) + "MiB";
		}
		
		double usedGibibytes = usedMebibytes / 1024.0;
		return String.format( "%.2f", usedGibibytes ) + "GiB";
	}
}
