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

import java.util.LinkedList;

public class Progress extends AbstractMessager
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final int DefaultLogTimeoutMilliseconds = 5 * 60 * 1000; // 5 minutes
	private static final Model DefaultModel = Model.Linear;
	private static final int DefaultReportIntervalMilliseconds = 5 * 1000; // 5 seconds
	private static final int UpdateIntervalMilliseconds = 500; // half a second
	
	private static class LogEntry
	{
		public long work;
		public long time;
		
		public LogEntry( long work, long time )
		{
			this.work = work;
			this.time = time;
		}
	}
	
	public static enum Model
	{
		Linear
		{
			@Override
			public long getEta( LinkedList<LogEntry> workLog, long totalWork )
			{
				// not enough data?
				if( workLog.size() < 2 )
				{
					return -1;
				}
				
				// perform simple linear regression
				double sumxy = 0.0;
				double sumx = 0.0;
				double sumy = 0.0;
				double sumxsq = 0.0;
				for( LogEntry entry : workLog )
				{
					sumxy += entry.work * entry.time;
					sumx += entry.work;
					sumy += entry.time;
					sumxsq += entry.work * entry.work;
				}
				
				// solve for slope (a) and intercept (b)
				double a = ( sumxy - sumx * sumy / workLog.size() ) / ( sumxsq - sumx * sumx / workLog.size() );
				double b = ( sumy - a * sumx ) / workLog.size();
				
				// extrapolate the finish time (y = ax+b), then compute the ETA
				double x = totalWork;
				return (long)( a * x + b ) - workLog.getLast().time;
			}
		},
		Quadratic
		{
			@Override
			public long getEta( LinkedList<LogEntry> workLog, long totalWork )
			{
				// NOTE: code shamelessly adapted from:
				// http://www.codeproject.com/KB/recipes/QuadraticRegression.aspx
				
				// not enough data?
				if( workLog.size() < 2 )
				{
					return -1;
				}
				
				// compute our sums
				double s00 = workLog.size();
				double s10 = 0.0; // x
				double s20 = 0.0; // x^2
				double s30 = 0.0; // x^3
				double s40 = 0.0; // x^4
				double s01 = 0.0; // y
				double s11 = 0.0; // xy
				double s21 = 0.0; // x^2y
				for( LogEntry entry : workLog )
				{
					double x = entry.work;
					double y = entry.time;
					s10 += x;
					s01 += y;
					s11 += x * y;
					x *= entry.work;
					s20 += x;
					s21 += x * y;
					x *= entry.work;
					s30 += x;
					x *= entry.work;
					s40 += x;
				}
				
				// compute the quadratic model (y = ax^2 + bx + c)
				// UNDONE: if we really want to optimize this, we can pre-compute the multiplications
				double a =
					(
						s21 * ( s20 * s00 - s10 * s10 )
						- s11 * ( s30 * s00 - s10 * s20 )
						+ s01 * ( s30 * s10 - s20 * s20 )
					) / (
						s40 * ( s20 * s00 - s10 * s10 )
						- s30 * ( s30 * s00 - s10 * s20 )
						+ s20 * ( s30 * s10 - s20 * s20 )
					);
				
				double b =
					(
						s40 * ( s11 * s00 - s01 * s10 )
						- s30 * ( s21 * s00 - s01 * s20 )
						+ s20 * ( s21 * s10 - s11 * s20 )
					) / (
						s40 * ( s20 * s00 - s10 * s10 )
						- s30 * ( s30 * s00 - s10 * s20 )
						+ s20 * ( s30 * s10 - s20 * s20 )
					);
				
				double c =
					(
						s40 * ( s20 * s01 - s10 * s11 )
						- s30 * ( s30 * s01 - s10 * s21 )
						+ s20 * ( s30 * s11 - s20 * s21 )
					) / (
						s40 * ( s20 * s00 - s10 * s10 )
						- s30 * ( s30 * s00 - s10 * s20 )
						+ s20 * ( s30 * s10 - s20 * s20 )
					);
				
				// extrapolate the finish time, then compute the ETA
				double x = totalWork;
				return (long)( a*x*x + b*x + c ) - workLog.getLast().time;
			}
		};
		
		public abstract long getEta( LinkedList<LogEntry> workLog, long totalWork );
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private long m_totalWork;
	private long m_reportIntervalMilliseconds;
	private Model m_model;
	private int m_logTimeoutMilliseconds;
	private long m_currentWork;
	private Timer m_timer;
	private boolean m_showMemory;
	private LinkedList<LogEntry> m_workLog;
	private long m_lastReportMilliseconds;
	private boolean m_isOkToReport;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Progress( long totalWork )
	{
		this( totalWork, DefaultReportIntervalMilliseconds, DefaultModel, DefaultLogTimeoutMilliseconds );
	}
	
	public Progress( long totalWork, long updateIntervalMilliseconds )
	{
		this( totalWork, updateIntervalMilliseconds, DefaultModel, DefaultLogTimeoutMilliseconds );
	}
	
	public Progress( long totalWork, long updateIntervalMilliseconds, Model model )
	{
		this( totalWork, updateIntervalMilliseconds, model, DefaultLogTimeoutMilliseconds );
	}
	
	public Progress( long totalWork, long reportIntervalMilliseconds, Model model, int logTimeoutMilliseconds )
	{
		// save params
		m_totalWork = totalWork;
		m_reportIntervalMilliseconds = reportIntervalMilliseconds;
		m_model = model;
		m_logTimeoutMilliseconds = logTimeoutMilliseconds;
		
		// init defaults
		m_currentWork = 0;
		m_timer = new Timer();
		m_showMemory = false;
		m_workLog = new LinkedList<LogEntry>();
		m_lastReportMilliseconds = 0;
		m_timer.start();
		m_isOkToReport = false;
		
		// add the 0,0 point to the work log
		m_workLog.addLast( new LogEntry( m_currentWork, m_timer.getElapsedMilliseconds() ) );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setShowMemory( boolean val )
	{
		m_showMemory = val;
	}
	
	public long getNumWorkDone( )
	{
		return m_currentWork;
	}
	
	public long getTotalWork( )
	{
		return m_totalWork;
	}
	
	public boolean isFinished( )
	{
		return m_currentWork == m_totalWork;
	}
	
	public boolean isOkToReport( )
	{
		return m_isOkToReport;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean setProgress( long currentWork )
	{
		m_currentWork = currentWork;
		
		// should we update and/or report?
		long elapsedMilliseconds = m_timer.getElapsedMilliseconds();
		boolean update = elapsedMilliseconds - m_workLog.getLast().time >= UpdateIntervalMilliseconds;
		boolean report = elapsedMilliseconds - m_lastReportMilliseconds >= m_reportIntervalMilliseconds;
		
		// if this is the last work done, force an update and a report
		if( isFinished() )
		{
			m_timer.stop();
			update = true;
			report = true;
		}
		
		// update the work log if needed
		if( update )
		{
			m_workLog.addLast( new LogEntry( m_currentWork, elapsedMilliseconds ) );
		}
		
		// report the progress if needed
		if( report )
		{
			// recalculate statistics
			double complete = (double)m_currentWork / (double)m_totalWork;
			
			// build the message
			StringBuilder msg = new StringBuilder();
			msg.append( "Progress: " );
			msg.append( formatPercent( complete ) );
			msg.append( "\tETA: " );
			msg.append( formatTimeInterval( m_model.getEta( m_workLog, m_totalWork ) ) );
			
			// should we show the memory usage too?
			if( m_showMemory )
			{
				msg.append( "\tmem=" );
				msg.append( Profiler.getMemoryUsed() );
			}
			
			message( msg.toString() );
			
			pruneLog();
			m_lastReportMilliseconds = elapsedMilliseconds;
		}
		m_isOkToReport = report;
		
		// add the finished message if needed
		if( m_currentWork == m_totalWork )
		{
			message( "Finished in " + m_timer.getElapsedTime() );
		}
		
		return report;
	}
	
	public boolean incrementProgress( )
	{
		return incrementProgress( 1 );
	}
	
	public boolean incrementProgress( int numWorkDone )
	{
		return setProgress( m_currentWork + numWorkDone );
	}
	
	public float getElapsedSeconds( )
	{
		return m_timer.getElapsedSeconds();
	}
	
	public String getElapsedTime( )
	{
		return m_timer.getElapsedTime();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void pruneLog( )
	{
		// remove old entries from the log if needed
		long elapsedMilliseconds = m_workLog.getLast().time;
		while( m_workLog.size() > 1 && elapsedMilliseconds - m_workLog.getFirst().time > m_logTimeoutMilliseconds )
		{
			m_workLog.removeFirst();
		}
	}
	
	private String formatPercent( double percent )
	{
		return String.format( "%5.1f", percent * 100.0 ) + "%";
	}
	
	private String formatTimeInterval( long milliseconds )
	{
		if( milliseconds < 0 )
		{
			return "calculating...";
		}
		
		long seconds = milliseconds / 1000;
		long hours = seconds / 3600;
		long minutes = ( seconds - hours * 3600 ) / 60;
		seconds = seconds - hours * 3600 - minutes * 60;
		
		StringBuffer buf = new StringBuffer();
		buf.append( hours < 10 ? "0" + hours : hours );
		buf.append( ":" );
		buf.append( minutes < 10 ? "0" + minutes : minutes );
		buf.append( ":" );
		buf.append( seconds < 10 ? "0" + seconds : seconds );
		
		return buf.toString();
	}
}
