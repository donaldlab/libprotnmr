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

package edu.duke.cs.libprotnmr.xplor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.duke.cs.libprotnmr.io.StreamConsumer;



public abstract class XplorBase
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String XplorName = "pyXplor";
	
	// NOTE: literals in python require specific casing
	protected static final String PythonTrue = "True";
	protected static final String PythonFalse = "False";
	protected static final String PythonNull = "None";
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private File m_workDir;
	private File m_logFile;
	private Process m_process;
	private StreamConsumer m_outConsumer;
	private StreamConsumer m_errConsumer;
	private StringWriter m_errWriter;
	private FileWriter m_logWriter;
	private HashMap<String,String> m_tags;
	private HashSet<File> m_tempFiles;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	protected XplorBase( String workPath )
	{
		this( new File( workPath ) );
	}
	
	protected XplorBase( File workDir )
	{
		// save parameters
		m_workDir = workDir;
		
		// init defaults
		m_logFile = null;
		m_process = null;
		m_outConsumer = null;
		m_errConsumer = null;
		m_errWriter = null;
		m_logWriter = null;
		m_tags = new HashMap<String,String>();
		m_tempFiles = new HashSet<File>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setLog( String path )
	{
		setLog( new File( path ) );
	}
	
	public void setLog( File file )
	{
		m_logFile = file;
	}
	
	public File getLog( )
	{
		return m_logFile;
	}
	
	public File getWorkDir( )
	{
		return m_workDir;
	}
	
	
	/**************************
	 *   Events
	 **************************/
	
	protected void setStreamFilters( StreamConsumer outConsumer, StreamConsumer errConsumer )
	{
		// default behavior is to do nothing
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	protected void setTag( Enum<?> tag, String value )
	{
		m_tags.put( tag.name(), value );
	}
	
	protected void clearTags( )
	{
		m_tags.clear();
	}
	
	protected File addTempFile( File file )
	{
		m_tempFiles.add( file );
		return file;
	}
	
	protected void cleanupTempFiles( )
	{
		for( File file : m_tempFiles )
		{
			file.delete();
		}
		m_tempFiles.clear();
	}
	
	protected void runScript( File scriptFile )
	throws IOException
	{
		// NOTE: assumes XplorName is in $PATH
		assert( m_workDir.equals( scriptFile.getParentFile() ) );
		String command = String.format( "%s -v %s", XplorName, scriptFile.getName() );
		try
		{
			m_process = Runtime.getRuntime().exec( command, getEnv(), m_workDir );
		}
		catch( IOException ex )
		{
			String message = "Unable to find Xplor! output of `which " + XplorName + "`:\n"
				+ getRunOut( "which " + XplorName ) + "\nPATH=" + System.getenv().get( "PATH" );
			throw new IOException( message, ex );
		}
		
		// monitor stdout
		m_outConsumer = new StreamConsumer( m_process.getInputStream() );
		
		// set another consumer to check for errors
		m_errConsumer = new StreamConsumer( m_process.getErrorStream() );
		m_errWriter = new StringWriter();
		m_errConsumer.setOut( m_errWriter );
		
		// save a log if needed
		if( m_logFile != null )
		{
			m_logWriter = new FileWriter( m_logFile );
			m_outConsumer.setOut( m_logWriter );
		}
		
		// allow the child class access to the consumers before they're launched
		setStreamFilters( m_outConsumer, m_errConsumer );
		
		m_outConsumer.start();
		m_errConsumer.start();
	}
	
	protected void waitForXplor( )
	throws XplorException, IOException
	{
		try
		{
			m_process.waitFor();
			m_outConsumer.waitFor();
			m_errConsumer.waitFor();
		}
		catch( InterruptedException ex )
		{
			Thread.currentThread().interrupt();
		}
		
		// finalize file io
		if( m_logWriter != null )
		{
			m_logWriter.close();
		}
		
		// did the process error?
		StringBuffer errors = m_errWriter.getBuffer();
		if( errors.length() > 0 )
		{
			throw new XplorException( errors.toString() );
		}
	}
	
	protected File emitFile( URL file )
	throws IOException
	{
		// get the output filename
		String[] parts = file.getPath().split( "/" ); 
		assert( parts.length > 0 );
		File outFile = new File( m_workDir, parts[parts.length - 1] );
		
		// open the outfile for writing
		BufferedWriter writer = new BufferedWriter( new FileWriter( outFile ) );
		
		// read the template line-by-line
		BufferedReader reader = new BufferedReader( new InputStreamReader( file.openStream() ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// replace any tags in this line
			for( String tag : m_tags.keySet() )
			{
				line = line.replaceAll( "\\$" + tag, m_tags.get( tag ) );
			}
			
			// write out the line
			writer.write( line );
			writer.write( "\n" );
		}
		
		// cleanup files
		reader.close();
		writer.close();
		
		return outFile;
	}
	
	protected String quote( String in )
	{
		return "\"" + in + "\"";
	}
	
	protected String[] getEnv( )
	{
		// make a copy of the current environment vars
		Map<String,String> map = new HashMap<String,String>();
		map.putAll( System.getenv() );
		
		// erase the python path since we might be running inside of a jython environment
		map.put( "PYTHONPATH", "" );
		
		// convert to a string array
		String[] vars = new String[map.size()];
		int i = 0;
		for( String key : map.keySet() )
		{
			vars[i++] = key + "=" + map.get( key );
		}
		
		return vars;
	}
	
	protected String getRunOut( String command )
	{
		// run the command
		Process process = null;
		try
		{
			process = Runtime.getRuntime().exec( command, getEnv(), m_workDir );
		}
		catch( IOException ex )
		{
			return "Error: command failed";
		}
		
		// attach to the process so we can read stdout
		StreamConsumer outConsumer = new StreamConsumer( process.getInputStream() );
		StringWriter writer = new StringWriter();
		outConsumer.setOut( writer );
		outConsumer.start();
		
		try
		{
			process.waitFor();
			outConsumer.waitFor();
		}
		catch( InterruptedException ex )
		{
			// ignore
		}
		
		return writer.getBuffer().toString();
	}
}
