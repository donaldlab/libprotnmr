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
