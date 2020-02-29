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

package edu.duke.cs.libprotnmr.protein;

import java.util.HashMap;

public enum AminoAcid
{
	/**************************
	 *   Values
	 **************************/
	
	Unknown( "???", '?' ),
	Alanine( "Ala", 'A' ),
	Arginine( "Arg", 'R' ),
	Asparagine( "Asn", 'N' ),
	AsparticAcid( "Asp", 'D' ),
	Cysteine( "Cys", 'C' ),
	GlutamicAcid( "Glu", 'E' ),
	Glutamine( "Gln", 'Q' ),
	Glycine( "Gly", 'G' ),
	Histidine( "His", 'H' ),
	Isoleucine( "Ile", 'I' ),
	Leucine( "Leu", 'L' ),
	Lysine( "Lys", 'K' ),
	Methionine( "Met", 'M' ),
	Phenylalanine( "Phe", 'F' ),
	Proline( "Pro", 'P' ),
	Serine( "Ser", 'S' ),
	Threonine( "Thr", 'T' ),
	Tryptophan( "Trp", 'W' ),
	Tyrosine( "Tyr", 'Y' ),
	Valine( "Val", 'V' );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_abbreviation;
	private Character m_code;
	private static HashMap<String,AminoAcid> m_abbreviationMap = new HashMap<String,AminoAcid>();
	private static HashMap<Character,AminoAcid> m_codeMap = new HashMap<Character,AminoAcid>();
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		// build the abbreviation and code maps
		for( AminoAcid aminoAcid : AminoAcid.values() )
		{
			AminoAcid.m_abbreviationMap.put( aminoAcid.getAbbreviation(), aminoAcid );
			AminoAcid.m_codeMap.put( aminoAcid.getCode(), aminoAcid );
		}
	}
	
	private AminoAcid( String abbreviation, Character code )
	{
		m_abbreviation = abbreviation.toUpperCase();
		m_code = Character.toUpperCase( code );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getAbbreviation( )
	{
		return m_abbreviation;
	}
	
	public Character getCode( )
	{
		return m_code;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static AminoAcid getByAbbreviation( String abbreviation )
	{
		AminoAcid aminoAcid = m_abbreviationMap.get( abbreviation.toUpperCase() );
		if( aminoAcid == null )
		{
			return Unknown;
		}
		
		return aminoAcid;
	}
	
	public static AminoAcid getByCode( Character code )
	{
		AminoAcid aminoAcid = m_codeMap.get( Character.toUpperCase( code ) );
		if( aminoAcid == null )
		{
			return Unknown;
		}
		
		return aminoAcid;
	}
}
