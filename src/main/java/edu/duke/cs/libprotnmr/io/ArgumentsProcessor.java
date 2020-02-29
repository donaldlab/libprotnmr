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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ArgumentsProcessor
{
	/**************************
	 *   Data Members
	 **************************/
	
	private ArrayList<Argument> m_staticArguments;
	private HashMap<String,Argument> m_dynamicArguments;
	private HashMap<String,String> m_values;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ArgumentsProcessor( )
	{
		clearArguments();
		m_values = new HashMap<String,String>();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void clearArguments( )
	{
		m_staticArguments = new ArrayList<Argument>();
		m_dynamicArguments = new HashMap<String,Argument>();
	}
	
	public void add( String name, ArgumentType type, String description )
	{
		addArgument( new Argument( name, type, description ) );
	}
	
	public void add( String name, ArgumentType type, String flag, String defaultValue, String description )
	{
		addArgument( new Argument( name, type, flag, defaultValue, description ) );
	}
	
	public void addArgument( Argument argument )
	{
		// add this argument to the appropriate spot
		if( argument.isStatic() )
		{
			m_staticArguments.add( argument );
		}
		else
		{
			m_dynamicArguments.put( argument.getFlag(), argument );
		}
	}
	
	public void process( String[] arguments )
	{
		// clear out any old values
		m_values.clear();
		
		// process the magic help argument first
		if( arguments.length == 0 || arguments[0].equalsIgnoreCase( "--help" ) )
		{
			printHelp();
			System.exit( 0 );
		}
		
		try
		{
			// process the magic file argument next
			if( arguments[0].equalsIgnoreCase( "--file" ) )
			{
				// make sure there's another argument
				if( arguments.length < 2 )
				{
					throw new ArgumentException( "Missing file path!" );
				}
				
				String pathFile = arguments[1];
				try
				{
					arguments = readArgumentsFromFile( pathFile );
				}
				catch( IOException ex )
				{
					throw new ArgumentException( "Arguments file not found! " + pathFile );
				}
			}
			
			readValues( arguments );
			validateValues();
		}
		catch( ArgumentException ex )
		{
			System.out.print( "Error: " );
			System.out.print( ex.getMessage() );
			System.out.print( "\n" );
			printHelp( true );
			System.exit( -1 );
		}
	}
	
	public void modeRequire( String mode, String name )
	{
		String value = m_values.get( name );
		if( value == null || value.equals( "" ) )
		{
			System.out.print( "Error: " );
			System.out.print( name + " is required for mode " + mode + "." );
			System.out.print( "\n" );
			printHelp( true );
			System.exit( -1 );
		}
	}
	
	public void printHelp( )
	{
		printHelp( false );
	}
	
	public void printHelp( boolean printSubmission )
	{
		// print out the usage
		System.out.println( "Usage:" );
		System.out.print( "\tjava VMARGS" );
		for( Argument argument : m_staticArguments )
		{
			System.out.print( " " );
			System.out.print( argument.getName() );
		}
		for( Argument argument : m_dynamicArguments.values() )
		{
			System.out.print( " [-" );
			System.out.print( argument.getFlag() );
			System.out.print( " " );
			System.out.print( argument.getName() );
			System.out.print( "]" );
		}
		System.out.print( "\n" );
		System.out.println( "   or\tjava VMARGS --file filePath" );
		System.out.println( "   or\tjava VMARGS --help" );
		
		// print out the argument descriptions
		System.out.println();
		System.out.println( "Arguments:" );
		for( Argument argument : m_dynamicArguments.values() )
		{
			System.out.print( "   -" );
			System.out.print( argument.getFlag() );
			System.out.print( "\t" );
			System.out.print( argument.getName() );
			System.out.print( "\t" );
			System.out.print( argument.getDescription() );
			System.out.print( "\n" );
		}
		for( Argument argument : m_staticArguments )
		{
			System.out.print( "\t" );
			System.out.print( argument.getName() );
			System.out.print( "\t" );
			System.out.print( argument.getDescription() );
			System.out.print( "\n" );
		}
		
		// print out the submitted arguments if needed
		if( printSubmission )
		{
			System.out.println();
			System.out.println( "Submitted Values:" );
			for( Argument argument : m_dynamicArguments.values() )
			{
				String value = m_values.get( argument.getName() );
				if( value == null )
				{
					value = "";
				}
				System.out.print( "\t" );
				System.out.print( argument.getName() );
				System.out.print( "\t" );
				System.out.print( value );
				System.out.print( "\n" );
			}
			for( Argument argument : m_staticArguments )
			{
				String value = m_values.get( argument.getName() );
				if( value == null )
				{
					value = "";
				}
				System.out.print( "\t" );
				System.out.print( argument.getName() );
				System.out.print( "\t" );
				System.out.print( value );
				System.out.print( "\n" );
			}
		}
	}
	
	public File getFile( String name )
	{
		return ArgumentType.getFile( m_values.get( name ) );
	}
	
	public String getString( String name )
	{
		return m_values.get( name );
	}
	
	public Integer getInteger( String name )
	{
		return new Integer( m_values.get( name ) );
	}
	
	public Double getDouble( String name )
	{
		return new Double( m_values.get( name ) );
	}
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private void readValues( String[] arguments )
	throws ArgumentException
	{
		// populate the default values for dynamic arguments
		for( Argument dynamicArgument : m_dynamicArguments.values() )
		{
			m_values.put( dynamicArgument.getName(), dynamicArgument.getDefaultValue() );
		}
		
		// read in the argument values
		int nextStaticArgument = 0;
		for( int i=0; i<arguments.length; i++ )
		{
			String argument = arguments[i];
			
			// is this argument a flag?
			if( argument.charAt( 0 ) == '-' )
			{
				String flag = argument.substring( 1 );
				
				// look up the name of the argument
				Argument dynamicArgument = m_dynamicArguments.get( flag );
				if( dynamicArgument == null )
				{
					throw new ArgumentException( "No such option: " + flag );
				}
				
				// make sure there's a next argument
				if( arguments.length <= i+1 )
				{
					throw new ArgumentException( "Option " + flag + " has no value!" );
				}
				
				// save the value
				m_values.put( dynamicArgument.getName(), arguments[++i] );
			}
			else
			{
				// make sure there's another static argument
				if( nextStaticArgument >= m_staticArguments.size() )
				{
					throw new ArgumentException( "Too many arguments!" );
				}
				
				// get the argument
				Argument staticArgument = m_staticArguments.get( nextStaticArgument++ );
				
				// save the value
				m_values.put( staticArgument.getName(), argument );
			}
		}
	}
	
	private void validateValues( )
	throws ArgumentException
	{
		// perform argument validation
		for( Argument argument : m_staticArguments )
		{
			String value = m_values.get( argument.getName() );
			if( value == null )
			{
				throw new ArgumentException( "missing required argument: " + argument.getName() );
			}
			
			argument.validate( value );
		}
		
		for( Argument argument : m_dynamicArguments.values() )
		{
			String value = m_values.get( argument.getName() );
			if( value != null )
			{
				argument.validate( value );
			}
		}
	}
	
	private String[] readArgumentsFromFile( String path )
	throws IOException
	{
		// read all of the arguments into one large string
		BufferedReader reader = new BufferedReader( new FileReader( path ) );
		StringBuffer buf = new StringBuffer();
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// skip comments and blank lines
			line = line.trim();
			if( line.length() == 0 || line.charAt( 0 ) == '#' )
			{
				continue;
			}
			
			buf.append( " " );
			buf.append( line );
		}
		reader.close();
		String argString = buf.toString();
		
		// tokenize the string like main would
		// UNDONE: there's probably java code in the runtime that does this better
		ArrayList<String> arguments = new ArrayList<String>();
		buf = new StringBuffer();
		char delimiter = ' ';
		char quote = '"';
		boolean inQuotes = false;
		for( int i=0; i<argString.length(); i++ )
		{
			char c = argString.charAt( i );
			
			// check for quoted strings
			if( c == quote )
			{
				inQuotes = !inQuotes;
			}
			// reset the buffer if we hit a delimiter
			else if( c == delimiter && !inQuotes )
			{
				// add an argument if needed
				if( buf.length() > 0 )
				{
					arguments.add( buf.toString() );
				}
				
				buf = new StringBuffer();
			}
			else
			{
				buf.append( c );
			}
		}
		
		// get the last argument
		if( buf.length() > 0 )
		{
			arguments.add( buf.toString() );
		}
		
		// return a standard string array
		String[] args = new String[arguments.size()];
		arguments.toArray( args );
		return args;
	}
}
