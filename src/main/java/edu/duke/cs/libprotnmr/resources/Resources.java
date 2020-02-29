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

package edu.duke.cs.libprotnmr.resources;

import java.io.InputStream;

public class Resources
{
	private static String m_resourcePath;

	static
	{
		// get the absolute path to the resources package independent of its actual package
		StringBuilder buf = new StringBuilder();
		buf.append( "/" );
		String[] packageComponents = Resources.class.getName().split( "\\." );
		for( int i=0; i<packageComponents.length-2; i++ )
		{
			buf.append( packageComponents[i] );
			buf.append( "/" );
		}
		m_resourcePath = buf.toString();
	}

	public static String getPath( String path )
	{
		// to my infinite sadness, parent directory references don't work in a jar classLoader!! ;_;
		// so we have to build absolute paths for all our resources
		return m_resourcePath + path;
	}

	public static InputStream get(String path) {
		return Resources.class.getResourceAsStream(getPath(path));
	}
}
