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
package edu.duke.cs.libprotnmr.cgal;

import edu.duke.cs.libprotnmr.resources.Native;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Cgal implements AutoCloseable
{
	/**************************
	 *   Data Members
	 **************************/
	
	private ReferenceQueue<AbstractCleanable> m_referenceQueue;
	private Set<CleanablePhantomReference> m_references;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	protected Cgal()
	{
		Native.init();

		// init defaults
		m_referenceQueue = new ReferenceQueue<AbstractCleanable>();
		m_references = Collections.synchronizedSet( new HashSet<CleanablePhantomReference>() );
		
		// add a jvm shutdown hook to make sure resources get cleaned up
		Runtime.getRuntime().addShutdownHook( new Thread( )
		{
			@Override
			public void run( )
			{
				cleanup();
			}
		} );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addReference( AbstractCleanable obj, Cleaner cleaner )
	{
		m_references.add( new CleanablePhantomReference( obj, m_referenceQueue, cleaner ) );
	}
	
	public void cleanup( )
	{
		cleanupJavaResources();
		cleanupNativeResources();
	}

	@Override
	public void close() {
		cleanup();
	}
	
	public int cleanupUnreferenced( )
	{
		int numCleanedUp = 0;
		CleanablePhantomReference ref = null;
		while( ( ref = (CleanablePhantomReference)m_referenceQueue.poll() ) != null )
		{
			// cleanup native resources
			ref.cleanup();
			
			// break reference and allow the heap memory to be released
			ref.clear();
			m_references.remove( ref );
			
			numCleanedUp++;
		}
		return numCleanedUp;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	protected static String getLibTag( )
	{
		// TEMP: for debugging
		//if( true ) return "";
		
		String arch = System.getProperty( "os.arch" ).toLowerCase();
		if( arch.equals( "i386" ) || arch.equals( "x86" ) )
		{
			return ".x86";
		}
		if( arch.startsWith( "amd64" ) || arch.startsWith( "x86_64" ) )
		{
			return ".x86_64";
		}
		
		String os = System.getProperty( "os.name" );
		throw new Error( "Unable to load Cgal library wrapper. Your operating system and architecture (" + os + ", " + arch + ") are not currently supported." );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	protected void cleanupJavaResources( )
	{
		// clear out all references
		while( m_referenceQueue.poll() != null );
		m_references.clear();
	}
	
	protected abstract void cleanupNativeResources( );
}
