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

package edu.duke.cs.libprotnmr.atomType;

import java.io.InputStream;
import java.util.HashMap;

import edu.duke.cs.libprotnmr.resources.Resources;

public enum AtomType
{
	/**************************
	 *   Values
	 **************************/
	
	Unknown( "???" ),
	C1( "C.1" ),
	C2( "C.2" ),
	C3 ( "C.3" ),
	Car( "C.ar" ),
	Ccat( "C.cat" ),
	Ca( "Ca" ),
	N1( "N.1" ),
	N2( "N.2" ),
	N3( "N.3" ),
	N4( "N.4" ),
	Nam( "N.am" ),
	Npl3( "N.pl3" ),
	Nar( "N.ar" ),
	H( "H" ),
	Hspc( "H.spc" ),
	Ht3p( "H.t3p" ),
	O2( "O.2" ),
	O3( "O.3" ),
	Oco2( "O.co2" ),
	Ospc( "O.spc" ),
	Ot3p( "O.t3p" ),
	P3( "P.3" ),
	S3( "S.3" ),
	S2( "S.2" ),
	SO( "S.O" ),
	SO2( "S.O2" );

	
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String RadiusPath = Resources.getPath("vdw.radius");
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static HashMap<String,AtomType> m_codeMap;
	private String m_code;
	private double m_radius;
	

	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		AtomType.m_codeMap = new HashMap<String,AtomType>();
		
		// build the code map
		for( AtomType atomType : values() )
		{
			AtomType.m_codeMap.put( atomType.getCode(), atomType );
		}
		
		// load the vdW radii
		try
		{
			InputStream in = AtomType.class.getResourceAsStream( RadiusPath );
			HashMap<String,Double> radii = (new RadiusReader()).read( in );
			for( AtomType type : AtomType.values() )
			{
				Double radius = radii.get( type.m_code.toLowerCase() );
				if( radius != null )
				{
					type.m_radius = radius;
				}
			}
		}
		catch( Exception ex )
		{
			throw new Error( "ERROR: Unable to load vdW radii!\n\t" + RadiusPath, ex );
		}
	}
	
	private AtomType( String code )
	{
		m_code = code;
		m_radius = 0.0;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getCode( )
	{
		return m_code;
	}
	
	public double getRadius( )
	{
		return m_radius;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static AtomType getByCode( String code )
	{
		AtomType atomType = m_codeMap.get( code );
		
		if( atomType == null )
		{
			return Unknown;
		}
		
		return atomType;
	}
}
