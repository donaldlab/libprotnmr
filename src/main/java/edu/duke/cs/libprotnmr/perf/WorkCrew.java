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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public abstract class WorkCrew<T>
{
	/**************************
	 *   Definitions
	 **************************/
	
	private class Worker extends Thread
	{
		private int m_workloadSize;
		
		public Worker( int workloadSize )
		{
			m_workloadSize = workloadSize;
		}
		
		@Override
		public void run( )
		{
			List<T> workload = new ArrayList<T>( m_workloadSize );
			try
			{
				// grab work when it's available, wait if needed
				while( true )
				{
					workload.add( m_queue.take() ); // blocking
					fillAndProcessWorkload( workload );
				}
			}
			catch( InterruptedException ex )
			{
				Thread.currentThread().interrupt();
			}
			
			// keep working until the queue is empty
			while( !m_queue.isEmpty() )
			{
				fillAndProcessWorkload( workload );
			}
		}
		
		private void fillAndProcessWorkload( List<T> workload )
		{
			// fill up the workload without blocking
			while( workload.size() < m_workloadSize )
			{
				T work = m_queue.poll();
				if( work == null )
				{
					break;
				}
				workload.add( work );
			}
			
			// process the work
			for( T work : workload )
			{
				processInWorkerThread( work );
			}
			incrementNumWorksProcessed( workload.size() );
			workload.clear();
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private List<Worker> m_workers;
	private BlockingQueue<T> m_queue;
	private int m_numWorksAdded;
	private int m_numWorksProcessed;
	private Progress m_progress;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public WorkCrew( )
	{
		// NOTE: don't try to throw an interruption to the thread hosting this instance
		m_workers = null;
		m_queue = null;
		m_numWorksAdded = 0;
		m_numWorksProcessed = 0;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setProgress( Progress progress )
	{
		m_progress = progress;
	}
	
	public int getNumWorksAdded( )
	{
		return m_numWorksAdded;
	}
	
	public synchronized int getNumWorksProcessed( )
	{
		return m_numWorksProcessed;
	}
	private synchronized void incrementNumWorksProcessed( int num )
	{
		m_numWorksProcessed += num;
		
		if( m_progress != null )
		{
			m_progress.incrementProgress( num );
			if( m_progress.isOkToReport() )
			{
				reportProgress();
			}
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void startWorkers( int numWorkers )
	{
		// default to a workload size of 1
		startWorkers( numWorkers, 1 );
	}
	
	public void startWorkers( int numWorkers, int workloadSize )
	{
		// automatically pick a queue size (heuristic)
		int queueSize = numWorkers*2*workloadSize;
		
		startWorkers( numWorkers, workloadSize, queueSize );
	}
	
	public void startWorkers( int numWorkers, int workloadSize, int queueSize )
	{
		m_workers = new ArrayList<Worker>( numWorkers );
		for( int i=0; i<numWorkers; i++ )
		{
			m_workers.add( new Worker( workloadSize ) );
		}
		m_numWorksAdded = 0;
		m_numWorksProcessed = 0;
		m_queue = new ArrayBlockingQueue<T>( queueSize );
		for( Worker worker : m_workers )
		{
			worker.start();
		}
	}
	
	public void addWork( T work )
	{
		if( m_queue != null )
		{
			try
			{
				m_queue.put( work );
			}
			catch( InterruptedException ex )
			{
				Thread.currentThread().interrupt();
			}
			m_numWorksAdded++;
		}
	}
	
	public void clearPendingWork( )
	{
		m_queue.clear();
	}
	
	public void waitUntilWorkIsFinished( )
	{
		if( m_workers != null )
		{
			for( Worker worker : m_workers )
			{
				worker.interrupt();
				try
				{
					worker.join();
				}
				catch( InterruptedException ex )
				{
					Thread.currentThread().interrupt();
				}
			}
		}
		assert( m_queue.size() == 0 );
		m_queue = null;
		m_workers = null;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	protected abstract void processInWorkerThread( T work );
	
	protected void reportProgress( )
	{
		// do nothing
	}
}
