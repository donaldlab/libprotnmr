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

import java.net.URISyntaxException;
import java.net.URL;

import edu.duke.cs.libprotnmr.resources.Resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public enum Logging
{
	/**************************
	 *   Values
	 **************************/
	
	Normal( Resources.getPath("logging.normal.xml") ),
	NormalLogErrors( Resources.getPath("logging.normalLogErrors.xml") ),
	Silent( Resources.getPath("logging.silent.xml") ),
	Debug( Resources.getPath("logging.debug.xml") );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static boolean m_isInitialized;
	private String m_resourcePath;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_isInitialized = false;
	}
	
	private Logging( String resourcePath )
	{
		m_resourcePath = resourcePath;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Logger getLog( Class<?> c )
	{
		if( !m_isInitialized )
		{
			Silent.init();
		}
		return LogManager.getLogger( c );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void init( )
	{
		init( m_resourcePath );
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static void init( String resourcePath )
	{
		URL url = Logging.class.getResource( resourcePath );
		if( url == null )
		{
			throw new Error( "Unable to load resource: " + resourcePath );
		}

		try {
			LoggerContext.getContext().setConfigLocation(url.toURI());
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		m_isInitialized = true;
	}
}
