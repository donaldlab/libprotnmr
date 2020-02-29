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
package edu.duke.cs.libprotnmr.io;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public enum ArgumentType
{
	/**************************
	 *   Values
	 **************************/
	
	InFile,
	OutFile,
	Folder,
	String,
	Integer,
	Double;
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void validate( String value )
	throws ArgumentException
	{
		String methodName = "validate" + this.toString();
		try
		{
			getClass().getDeclaredMethod( methodName, String.class ).invoke( this, value );
		}
		catch( NoSuchMethodException ex )
		{
			ex.printStackTrace( System.err );
		}
		catch( InvocationTargetException ex )
		{
			// rethrow the nester error
			throw (ArgumentException)ex.getCause();
		}
		catch( IllegalAccessException ex )
		{
			ex.printStackTrace( System.err );
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void validateInFile( String value )
	throws ArgumentException
	{
		File file = getFile( value );
		
		if( !file.exists() )
		{
			throw new ArgumentException( "File does not exist! " + value );
		}
		
		if( !file.isFile() )
		{
			throw new ArgumentException( "File is not a file! " + value );
		}
	}
	
	public static void validateOutFile( String value )
	throws ArgumentException
	{
		File folder = getFile( value ).getParentFile();
		
		// just make sure the folder exists
		if( !folder.isDirectory() )
		{
			throw new ArgumentException( "Folder does not exist! " + folder.getAbsolutePath() );
		}
	}
	
	public static void validateFolder( String value )
	throws ArgumentException
	{
		File file = getFile( value );
		
		if( !file.exists() )
		{
			throw new ArgumentException( "Folder does not exist! " + value );
		}
		
		if( !file.isDirectory() )
		{
			throw new ArgumentException( "Folder is not a folder! " + value );
		}
	}
	
	public static void validateString( String value )
	throws ArgumentException
	{
		// nothing to do really
	}
	
	public static void validateInteger( String value )
	throws ArgumentException
	{
		try
		{
			java.lang.Integer.parseInt( value );
		}
		catch( NumberFormatException ex )
		{
			throw new ArgumentException( "Not an integer! " + value );
		}
	}
	
	public static void validateDouble( String value )
	throws ArgumentException
	{
		try
		{
			java.lang.Double.parseDouble( value );
		}
		catch( NumberFormatException ex )
		{
			throw new ArgumentException( "Not a double! " + value );
		}
	}
	
	public static File getFile( String value )
	{
		if( value == null )
		{
			return null;
		}
		
		return new File( value );
	}
	
	public static Integer getInteger( String value )
	{
		if( value == null )
		{
			return null;
		}
		
		return new Integer( value );
	}
	
	public static Double getDouble( String value )
	{
		if( value == null )
		{
			return null;
		}
		
		return new Double( value );
	}
}
