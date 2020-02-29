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

package edu.duke.cs.libprotnmr.bond;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class BondGraphBuilder
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String CanonicalBondsPath = Resources.getPath("canonical.bonds");
	private static final String BackboneBondsPath = Resources.getPath("backbone.bonds");
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private TreeMap<AminoAcid,ArrayList<Bond>> m_canonicalBonds;
	private TreeMap<ResidueType,ArrayList<Bond>> m_backboneBonds;
	
	private static BondGraphBuilder m_instance;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_instance = null;
	}
	
	private BondGraphBuilder( String canonicalBondsPath, String backboneBondsPath )
	throws IOException
	{
		// read in canonical bonds
		InputStream canonicalIn = getClass().getResourceAsStream( canonicalBondsPath );
		assert( canonicalIn != null ) : canonicalBondsPath;
		HashMap<String,ArrayList<Bond>> canonicalBonds = new BondReader().read( canonicalIn );
		m_canonicalBonds = new TreeMap<AminoAcid,ArrayList<Bond>>();
		for( String key : canonicalBonds.keySet() )
		{
			m_canonicalBonds.put(
				AminoAcid.getByAbbreviation( key ),
				canonicalBonds.get( key )
			);
		}
		
		// read in backbone bonds
		InputStream backboneIn = getClass().getResourceAsStream( backboneBondsPath );
		assert( backboneIn != null ) : backboneBondsPath;
		HashMap<String,ArrayList<Bond>> backboneBonds = new BondReader().read( backboneIn );
		m_backboneBonds = new TreeMap<ResidueType,ArrayList<Bond>>();
		for( String key : backboneBonds.keySet() )
		{
			m_backboneBonds.put(
				ResidueType.valueOf( key ),
				backboneBonds.get( key )
			);
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static BondGraphBuilder getInstance( )
	{
		if( m_instance == null )
		{
			try
			{
				m_instance = new BondGraphBuilder( CanonicalBondsPath, BackboneBondsPath );
			}
			catch( IOException ex )
			{
				// can't really do anything about this. Just bail out of the program
				throw new Error( ex );
			}
		}
		return m_instance;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public ArrayList<BondGraph> build( Protein protein )
	{
		return build( protein, null );
	}
	
	public ArrayList<BondGraph> build( Protein protein, String extraBondsPath )
	{
		ArrayList<BondGraph> bondGraphs = new ArrayList<BondGraph>( protein.getSubunits().size() );
		
		for( Subunit subunit : protein.getSubunits() )
		{
			bondGraphs.add( build( subunit, extraBondsPath ) );
		}
		
		return bondGraphs;
	}
	
	public BondGraph build( Subunit subunit )
	{
		return build( subunit, null );
	}
	
	public BondGraph build( Subunit subunit, String extraBondsPath )
	{
		BondGraph bonds = new BondGraph();
		
		// UNDONE: extra bonds aren't implemented yet
		if( extraBondsPath != null )
		{
			throw new UnsupportedOperationException( "Extra bonds aren't implemented yet!" );
		}
		
		// for each residue...
		for( Residue residue : subunit.getResidues() )
		{
			// get the next residue if needed
			Residue nextResidue = null;
			if( residue.getId() < subunit.getResidues().size() - 1 )
			{
				nextResidue = subunit.getResidues().get( residue.getId() + 1 );
			}
			
			// get a copy of the backbone bonds
			ResidueType residueType = ResidueType.valueOf( subunit, residue );
			ArrayList<Bond> backboneBonds = new ArrayList<Bond>( m_backboneBonds.get( residueType ) );
			
			// HACKHACK: Glycine doesn't have CA:HA bonds
			if( residue.getAminoAcid() == AminoAcid.Glycine )
			{
				Iterator<Bond> iterBond = backboneBonds.iterator();
				while( iterBond.hasNext() )
				{
					Bond bond = iterBond.next();
					if( bond.getLeftName().equals( "CA" ) && bond.getRightName().equals( "HA" ) )
					{
						iterBond.remove();
						break;
					}
				}
			}
			
			// apply the backbone bonds
			addBonds( backboneBonds, bonds, subunit, residue, nextResidue );
			
			// handle r-group bonds
			ArrayList<Bond> rgroupBonds = m_canonicalBonds.get( residue.getAminoAcid() );
			// Jeff: 01/16/2009 - warn if using undefined r-group bonds
			if( rgroupBonds == null || rgroupBonds.size() == 0 )
			{
				System.err.println( "WARNING: no bonds defined for " + residue.getAminoAcid() );
			}
			addBonds( rgroupBonds, bonds, subunit, residue, nextResidue );
		}
		
		return bonds;
	}
	
	public String getHeavyAtomName( String protonName, AminoAcid aminoAcid, ResidueType residueType )
	{
		// check the backbone bonds first
		for( Bond bond : m_backboneBonds.get( residueType ) )
		{
			if( bond.getLeftName().equalsIgnoreCase( protonName ) )
			{
				return bond.getRightName();
			}
			if( bond.getRightName().equalsIgnoreCase( protonName ) )
			{
				return bond.getLeftName();
			}
		}
		
		// then check the sidechain bonds
		for( Bond bond : m_canonicalBonds.get( aminoAcid ) )
		{
			if( bond.getLeftName().equalsIgnoreCase( protonName ) )
			{
				return bond.getRightName();
			}
			if( bond.getRightName().equalsIgnoreCase( protonName ) )
			{
				return bond.getLeftName();
			}
		}
		
		// couldn't find anything
		return null;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void addBonds( ArrayList<Bond> bondList, BondGraph bonds, Subunit subunit, Residue residue, Residue nextResidue )
	{
		if( bondList == null )
		{
			return;
		}
		
		// for each bond...
		for( Bond bond : bondList )
		{
			// change residues if this bond spans residues
			Residue rightResidue = residue;
			if( bond.bondsToNextResidue() )
			{
				rightResidue = nextResidue;
			}
			
			// link up the atoms to the bond
			Bond newBond = new Bond( bond );
			newBond.setLeftAddress( getAddress( subunit, residue, newBond.getLeftName() ) );
			newBond.setRightAddress( getAddress( subunit, rightResidue, newBond.getRightName() ) );
			
			// if either side of the bond is null, that atom doesn't exist in this protein. Just forget about the bond
			if( newBond.getLeftAddress() != null && newBond.getRightAddress() != null )
			{
				bonds.addBond( newBond );
			}
		}
	}
	
	private AtomAddressInternal getAddress( Subunit subunit, Residue residue, String name )
	{
		// try to find the atom
		Atom atom = getAtom( name, residue );
		if( atom == null )
		{
			return null;
		}
		
		return new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
	}
	
	private Atom getAtom( String name, Residue residue )
	{
		// resides only have 20 or so atoms, so a sequential search is fine here
		for( Atom atom : residue.getAtoms() )
		{
			if( atom.getName().equalsIgnoreCase( name ) )
			{
				return atom;
			}
		}
		
		return null;
	}
}
