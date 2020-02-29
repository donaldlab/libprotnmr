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

package edu.duke.cs.libprotnmr.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NameMapperProvider
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String PathOldToNew = Resources.getPath("atomNameOldToNew.map");
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static final Logger m_log = LogManager.getLogger(NameMapperProvider.class);
	private static NameMapperProvider m_instance;
	
	private HashMap<String,HashMap<String,String>> m_aminoAcidMaps;
	private HashMap<String,HashMap<String,String>> m_terminiMaps;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private NameMapperProvider( )
	{
		/* Jeff: 12/28/2008 - NOTE:
			There might be a more elegant way to load the map files based on the
			enum values, but loading them explicitly will work just fine.
		*/
		
		m_aminoAcidMaps = new HashMap<String,HashMap<String,String>>();
		
		HashMap<String,String> oldToNew = new HashMap<String,String>();
		HashMap<String,String> newToOld = new HashMap<String,String>();
		m_aminoAcidMaps.put( getMapKey( NameScheme.Old, NameScheme.New ), oldToNew );
		m_aminoAcidMaps.put( getMapKey( NameScheme.New, NameScheme.Old ), newToOld );
		
		// load the old to new map and reverse
		loadAminoAcidMaps( oldToNew, newToOld, PathOldToNew );
		
		// populate the termini maps
		m_terminiMaps = new HashMap<String,HashMap<String,String>>();
		
		oldToNew = new HashMap<String,String>();
		newToOld = new HashMap<String,String>();
		m_terminiMaps.put( getMapKey( NameScheme.Old, NameScheme.New ), oldToNew );
		m_terminiMaps.put( getMapKey( NameScheme.New, NameScheme.Old ), newToOld );
		
		oldToNew.put( getMapKey( ResidueType.NTerminus, "HT1" ), "H1" );
		oldToNew.put( getMapKey( ResidueType.NTerminus, "HT2" ), "H2" );
		oldToNew.put( getMapKey( ResidueType.NTerminus, "HT3" ), "H3" );
		oldToNew.put( getMapKey( ResidueType.CTerminus, "OT1" ), "O" );
		oldToNew.put( getMapKey( ResidueType.CTerminus, "OT2" ), "OXT" );
		
		newToOld.put( getMapKey( ResidueType.NTerminus, "H1" ), "HT1" );
		newToOld.put( getMapKey( ResidueType.NTerminus, "H2" ), "HT2" );
		newToOld.put( getMapKey( ResidueType.NTerminus, "H3" ), "HT3" );
		newToOld.put( getMapKey( ResidueType.CTerminus, "O" ), "OT1" );
		newToOld.put( getMapKey( ResidueType.CTerminus, "OXT" ), "OT2" );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static NameMapperProvider getInstance( )
	{
		if( m_instance == null )
		{
			m_instance = new NameMapperProvider();
		}
		
		return m_instance;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String mapName( NameScheme source, NameScheme destination, AminoAcid aminoAcid, ResidueType residueType, String atomName )
	{
		// check the termini first
		String newName = m_terminiMaps.get( getMapKey( source, destination ) ).get( getMapKey( residueType, atomName ) );
		if( newName != null )
		{
			return newName;
		}
		
		// then check the amino acid maps
		newName = m_aminoAcidMaps.get( getMapKey( source, destination ) ).get( getMapKey( aminoAcid, atomName ) );
		if( newName != null )
		{
			return newName;
		}
		
		// otherwise, just return the original name
		return atomName;
	}
	
	public void mapAtom( NameScheme source, NameScheme destination, AminoAcid aminoAcid, ResidueType residueType, Atom atom )
	{
		atom.setName( mapName( source, destination, aminoAcid, residueType, atom.getName() ) );
	}
	
	public void mapProtein( NameScheme source, NameScheme destination, Protein protein )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			Subunit subunit = protein.getSubunit( address.getSubunitId() );
			Residue residue = protein.getResidue( address );
			Atom atom = protein.getAtom( address );
			
			// map it
			mapAtom( source, destination, residue.getAminoAcid(), ResidueType.valueOf( subunit, residue ), atom );
		}
	}
	
	public void mapAddresses( NameScheme source, NameScheme destination, Sequences sequences, List<? extends HasAddresses<AtomAddressReadable>> allAddresses )
	{
		for( HasAddresses<AtomAddressReadable> addresses : allAddresses )
		{
			for( AtomAddressReadable address : addresses.addresses() )
			{
				mapAddress( source, destination, sequences, address );
			}
		}
	}
	
	public void mapAddress( NameScheme source, NameScheme destination, Sequences sequences, AtomAddressReadable address )
	{
		// does this residue exist in the sequences?
		AminoAcid aminoAcid = sequences.getAminoAcid( address );
		if( aminoAcid == null )
		{
			m_log.warn( "Unable to map atom name, atom address not in sequence! " + address.toString() );
		}
		else
		{
			ResidueType residueType = sequences.getSequence( address ).getResidueTypeByNumber( address.getResidueNumber() );
			address.setAtomName( mapName( source, destination, aminoAcid, residueType, address.getAtomName() ) );
		}
	}
	
	public boolean hasMap( NameScheme source, NameScheme destination )
	{
		return m_aminoAcidMaps.containsKey( getMapKey( source, destination ) );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void loadAminoAcidMaps( HashMap<String,String> forwardMap, HashMap<String,String> reverseMap, String path )
	{
		try
		{
			// open the resource file
			InputStream in = getClass().getResourceAsStream( path );
			if( in == null )
			{
				throw new IllegalArgumentException( "Invalid resource path: " + path );
			}
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			
			// read each line...
			String line = null;
			while( ( line = reader.readLine() ) != null )
			{
				// skip blank lines
				line = line.trim();
				if( line.equals( "" ) )
				{
					continue;
				}
				
				// parse the entry
				String[] parts = line.split( ":" );
				AminoAcid aminoAcid = AminoAcid.getByAbbreviation( parts[0] );
				String source = parts[1];
				String destination = parts[2];
				
				// add it to the maps
				forwardMap.put( getMapKey( aminoAcid, source ), destination );
				reverseMap.put( getMapKey( aminoAcid, destination ), source );
			}
		}
		catch( IOException ex )
		{
			throw new Error( "Unable to load name map at " + path );
		}
	}
	
	private String getMapKey( NameScheme source, NameScheme destination )
	{
		return source + "_" + destination;
	}
	
	private String getMapKey( AminoAcid aminoAcid, String atomName )
	{
		return aminoAcid.getCode() + "_" + atomName.toUpperCase();
	}
	
	private String getMapKey( ResidueType residueType, String atomName )
	{
		return residueType.name() + "_" + atomName.toUpperCase();
	}
}
