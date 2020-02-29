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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class AtomTypeMap
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String BackbonePath = Resources.getPath("backbone.atomTypes");
	private static final String CanonicalPath = Resources.getPath("canonical.atomTypes");
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private TreeMap<String,TreeMap<String,AtomType>> m_canonicalMap;
	private TreeMap<String,TreeMap<String,AtomType>> m_backboneMap;
	
	private static AtomTypeMap m_instance;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_instance = null;
	}
	
	protected AtomTypeMap( TreeMap<String,TreeMap<String,AtomType>> backboneMap, TreeMap<String,TreeMap<String,AtomType>> canonicalMap )
	{
		m_backboneMap = backboneMap;
		m_canonicalMap = canonicalMap;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static AtomTypeMap getInstance( )
	{
		if( m_instance == null )
		{
			try
			{
				AtomTypeReader reader = new AtomTypeReader();
				
				// read the backbone atom types
				InputStream in = AtomTypeMap.class.getResourceAsStream( BackbonePath );
				TreeMap<String,TreeMap<String,AtomType>> backboneMap = reader.read( in );
				
				// read the canonical atom types
				in = AtomTypeMap.class.getResourceAsStream( CanonicalPath );
				TreeMap<String,TreeMap<String,AtomType>> canonicalMap = reader.read( in );
					
				// return the map
				m_instance = new AtomTypeMap( backboneMap, canonicalMap );
			}
			catch( IOException ex )
			{
				// can't handle this... just crash and burn
				throw new Error( "Unable to load atom types!", ex );
			}
		}
		
		return m_instance;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public AtomType getAtomType( Subunit subunit, Residue residue, Atom atom )
	{
		return getAtomType( ResidueType.valueOf( subunit, residue ), residue.getAminoAcid(), atom.getName() );
	}
	
	public AtomType getAtomType( Protein protein, AtomAddressInternal address )
	{
		return getAtomType(
			protein.getSubunit( address.getSubunitId() ),
			protein.getResidue( address.getSubunitId(), address.getResidueId() ),
			protein.getAtom( address )
		);
	}
	
	public AtomType getAtomType( ResidueType residueType, AminoAcid aminoAcid, String atomName )
	{
		// first check the sidechain
		AtomType atomType = getSidechainAtomType( aminoAcid, atomName );
		if( atomType == null )
		{
			// then check the backbone
			atomType = getBackboneAtomType( residueType, atomName );
		}
		return atomType;
	}
	
	public AtomType getBackboneAtomType( ResidueType residueType, String atomName )
	{
		return getBackboneAtomTypes( residueType ).get( atomName );
	}
	
	public AtomType getSidechainAtomType( AminoAcid aminoAcid, String atomName )
	{
		return getSidechainAtomTypes( aminoAcid ).get( atomName );
	}
	
	public Map<String,AtomType> getBackboneAtomTypes( ResidueType residueType )
	{
		return m_backboneMap.get( residueType.name().toUpperCase() );
	}
	
	public Map<String,AtomType> getSidechainAtomTypes( AminoAcid aminoAcid )
	{
		return m_canonicalMap.get( aminoAcid.getAbbreviation().toUpperCase() );
	}
}
