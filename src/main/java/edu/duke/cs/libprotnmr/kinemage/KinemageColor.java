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

package edu.duke.cs.libprotnmr.kinemage;

public enum KinemageColor
{
	/**************************
	 *   Values
	 **************************/
	
	Blue( "209 100 78", "209 100 52" ),
	Red( "13 95 100", "13 95 99" ),
	Yellow( "48 88 100", "48 88 99" ),
	Green( "93 83 91", "93 83 61" ),
	Rose( "345 100 73", "345 100 49" ),
	SkyBlue( "205 49 100", "205 49 99" ),
	Olive( "75 95 37", "75 95 25" ),
	Lime( "66 83 96", "66 83 64" ),
	Purple( "273 73 64", "273 73 43" ),
	Orange( "34 95 100", "34 95 99" ),
	Magenta( "345 78 87", "345 78 58" ),
	Cobalt( "202 100 100", "202 100 82" ),
	SatisfiedNoe( "149 10 66", "0 0 0" ),
	UnsatisfiedNoe( "32 90 80", "32 100 100" ),
	LightGrey( "0 0 80", "0 0 70" ),
	Grey( "0 0 60", "0 0 50" ),
	DarkGrey( "0 0 40", "0 0 30" );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_darkColor;
	private String m_lightColor;
	

	/**************************
	 *   Constructors
	 **************************/
	
	private KinemageColor( String darkColor, String lightColor )
	{
		m_darkColor = darkColor;
		m_lightColor = lightColor;
	}
	

	/**************************
	 *   Accessors
	 **************************/
	
	public String getColors( )
	{
		return m_darkColor + " " + m_lightColor;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Node getAllColors( )
	{
		StringBuffer buf = new StringBuffer();
		for( KinemageColor color : values() )
		{
			buf.append( "@hsvcolor {" );
			buf.append( color.name() );
			buf.append( "} " );
			buf.append( color.getColors() );
			buf.append( "\n" );
		}
		return new Node( buf.toString() );
	}
}
