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

package edu.duke.cs.libprotnmr.cgal.spherical;

import edu.duke.cs.libprotnmr.cgal.Cgal;


public class SphericalCgal extends Cgal
{
	/**************************
	 *   Data Members
	 **************************/
	
	private static SphericalCgal m_instance;
	private Cgal m_cgal;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_instance = null;
	}
	
	private SphericalCgal() {}


	/**************************
	 *   Static Methods
	 **************************/
	
	public static SphericalCgal getInstance( )
	{
		if( m_instance == null )
		{
			m_instance = new SphericalCgal();
		}
		return m_instance;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static native void nativeCleanup( );
	
	
	/**************************
	 *   Events
	 **************************/
	
	@Override
	protected void cleanupNativeResources( )
	{
		m_cgal = null;
		nativeCleanup();
	}
}
