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

package edu.duke.cs.libprotnmr.rama;

import java.io.IOException;
import java.io.InputStream;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.resources.Resources;


public enum RamaCase
{
	/**************************
	 *   Definitions
	 **************************/
	
	General( Resources.getPath("ramaGeneral.dat"), 0.02, 0.0005 ),
	Glycine( Resources.getPath("ramaGlycine.dat"), 0.02, 0.002 ),
	Proline( Resources.getPath("ramaProline.dat"), 0.02, 0.002 ),
	PreProline( Resources.getPath("ramaPreProline.dat"), 0.02, 0.002 );
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_path;
	private double m_favoredCutoff;
	private double m_allowedCutoff;
	private RamaMap m_map;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private RamaCase( String path, double favoredCutoff, double allowedCutoff )
	{
		m_path = path;
		m_favoredCutoff = favoredCutoff;
		m_allowedCutoff = allowedCutoff;
		m_map = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getPath( )
	{
		return m_path;
	}
	
	public double getFavoredCutoff( )
	{
		return m_favoredCutoff;
	}
	
	public double getAllowedCutoff( )
	{
		return m_allowedCutoff;
	}
	
	public RamaMap getMap( )
	{
		if( m_map == null )
		{
			m_map = loadMap();
		}
		return m_map;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static RamaCase getCaseByNumber( Sequence sequence, int residueNumber )
	{
		// is the next residue proline?
		AminoAcid nextAminoAcid = sequence.getAminoAcidByNumber( residueNumber + 1 );
		if( nextAminoAcid == AminoAcid.Proline )
		{
			return PreProline;
		}
		
		// get the amino acid of the residue
		AminoAcid aminoAcid = sequence.getAminoAcidByNumber( residueNumber );
		
		// is it glycine or proline?
		if( aminoAcid == AminoAcid.Glycine )
		{
			return Glycine;
		}
		else if( aminoAcid == AminoAcid.Proline )
		{
			return Proline;
		}
		
		return General;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean isAllowed( double phiDegrees, double psiDegrees )
	{
		return getMap().isSampleAbove( phiDegrees, psiDegrees, m_allowedCutoff );
	}
	
	public boolean isFavored( double phiDegrees, double psiDegrees )
	{
		return getMap().isSampleAbove( phiDegrees, psiDegrees, m_favoredCutoff );
	}
	
	public boolean isAllowed( double minPhiDegrees, double maxPhiDegrees, double minPsiDegrees, double maxPsiDegrees )
	{
		return getMap().isBoxAbove( minPhiDegrees, maxPhiDegrees, minPsiDegrees, maxPsiDegrees, m_allowedCutoff );
	}
	
	public boolean isCompletelyAllowed( double minPhiDegrees, double maxPhiDegrees, double minPsiDegrees, double maxPsiDegrees )
	{
		return getMap().isBoxCompletelyAbove( minPhiDegrees, maxPhiDegrees, minPsiDegrees, maxPsiDegrees, m_allowedCutoff );
	}
	
	public RamaSatisfaction getSatisfaction( double phiDegrees, double psiDegrees )
	{
		if( isFavored( phiDegrees, psiDegrees ) )
		{
			return RamaSatisfaction.Favored;
		}
		else if( isAllowed( phiDegrees, psiDegrees ) )
		{
			return RamaSatisfaction.Allowed;
		}
		return RamaSatisfaction.Disallowed;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private RamaMap loadMap( )
	{
		InputStream in = RamaCase.class.getResourceAsStream( m_path );
		try
		{
			return RamaReader.readOptimized( in );
		}
		catch( IOException ex )
		{
			// don't try to handle this
			throw new Error( ex );
		}
	}
}
