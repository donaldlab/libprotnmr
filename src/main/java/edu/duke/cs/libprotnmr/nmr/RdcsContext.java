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

package edu.duke.cs.libprotnmr.nmr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.BondType;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class RdcsContext implements Serializable
{
	private static final long serialVersionUID = -8310070944855055185L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Sequence m_sequence;
	private HashMap<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> m_rdcIndex;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcsContext( Sequence sequence )
	{
		m_sequence = sequence;
		m_rdcIndex = new LinkedHashMap<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>>();
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Sequence getSequence( )
	{
		return m_sequence;
	}
	
	public Set<AlignmentMedium> media( )
	{
		return m_rdcIndex.keySet();
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public void addRdcs( AlignmentMedium medium, List<Rdc<AtomAddressReadable>> rdcs )
	{
		// does this medium have an entry yet?
		TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> mediumRdcs = m_rdcIndex.get( medium );
		if( mediumRdcs == null )
		{
			mediumRdcs = new TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>();
			m_rdcIndex.put( medium, mediumRdcs );
			
			// add an entry for each residue
			for( Sequence.Entry entry : m_sequence )
			{
				mediumRdcs.put( entry.residueNumber, new TreeMap<BondType,Rdc<AtomAddressReadable>>() );
			}
		}
		
		for( Rdc<AtomAddressReadable> rdc : rdcs )
		{
			int residueNumber = rdc.getFrom().getResidueNumber();
			
			// is this RDC even in the sequence index? (fake rdcs aren't)
			Map<BondType,Rdc<AtomAddressReadable>> residueRdcs = mediumRdcs.get( residueNumber );
			if( residueRdcs == null )
			{
				continue;
			}
			
			// add the rdc value
			residueRdcs.put( BondType.lookup( rdc ), rdc );
		}
	}
	
	public List<Rdc<AtomAddressReadable>> getRdcs( String mediumName, BondType type )
	{
		return getRdcs( getMediumCheck( mediumName ), type );
	}
	
	public List<Rdc<AtomAddressReadable>> getRdcs( AlignmentMedium medium, BondType type )
	{
		List<Rdc<AtomAddressReadable>> rdcs = new ArrayList<Rdc<AtomAddressReadable>>();
		for( TreeMap<BondType,Rdc<AtomAddressReadable>> residueRdcs : m_rdcIndex.get( medium ).values() )
		{
			Rdc<AtomAddressReadable> rdc = residueRdcs.get( type );
			if( rdc != null )
			{
				rdcs.add( rdc );
			}
		}
		return rdcs;
	}
	
	public boolean removeRdcs( String mediumName )
	{
		return removeRdcs( getMediumCheck( mediumName ) );
	}
	
	public boolean removeRdcs( AlignmentMedium medium )
	{
		return m_rdcIndex.remove( medium ) != null;
	}
	
	public int getNumMedia( )
	{
		return m_rdcIndex.size();
	}
	
	public AlignmentMedium getMedium( String mediumName )
	{
		for( AlignmentMedium medium : m_rdcIndex.keySet() )
		{
			if( medium.getName().equalsIgnoreCase( mediumName ) )
			{
				return medium;
			}
		}
		return null;
	}
	
	public Rdc<AtomAddressReadable> getRdc( String mediumName, int residueNumber, BondType type )
	{
		return getRdc( getMediumCheck( mediumName ), residueNumber, type );
	}
	
	public Rdc<AtomAddressReadable> getRdc( AlignmentMedium medium, int residueNumber, BondType type )
	{
		TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> mediumRdcs = m_rdcIndex.get( medium );
		if( mediumRdcs == null )
		{
			return null;
		}
		TreeMap<BondType,Rdc<AtomAddressReadable>> residueRdcs = mediumRdcs.get( residueNumber );
		if( residueRdcs == null )
		{
			return null;
		}
		return residueRdcs.get( type );
	}
	
	public List<AlignedRdc<AtomAddressReadable>> getRdcs( int residueNumber, BondType type )
	{
		List<AlignedRdc<AtomAddressReadable>> alignedRdcs = new ArrayList<AlignedRdc<AtomAddressReadable>>();
		
		// for each medium...
		for( Map.Entry<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> entry : m_rdcIndex.entrySet() )
		{
			TreeMap<BondType,Rdc<AtomAddressReadable>> residueRdcs = entry.getValue().get( residueNumber );
			if( residueRdcs != null )
			{
				Rdc<AtomAddressReadable> rdc = residueRdcs.get( type );
				if( rdc != null )
				{
					alignedRdcs.add( new AlignedRdc<AtomAddressReadable>( entry.getKey(), rdc ) );
				}
			}
		}
		
		return alignedRdcs;
	}
	
	public List<AlignedRdcs<AtomAddressReadable>> getRdcs( int residueNumber )
	{
		List<AlignedRdcs<AtomAddressReadable>> alignedRdcs = new ArrayList<AlignedRdcs<AtomAddressReadable>>();
		
		// for each medium...
		for( Map.Entry<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> entry : m_rdcIndex.entrySet() )
		{
			TreeMap<BondType,Rdc<AtomAddressReadable>> residueRdcs = entry.getValue().get( residueNumber );
			if( residueRdcs != null )
			{
				// add rdcs from all types
				List<Rdc<AtomAddressReadable>> rdcs = new ArrayList<Rdc<AtomAddressReadable>>( residueRdcs.values() );
				alignedRdcs.add( new AlignedRdcs<AtomAddressReadable>( entry.getKey(), rdcs ) );
			}
		}
		
		return alignedRdcs;
	}
	
	public String dumpToString( )
	{
		StringBuilder buf = new StringBuilder();
		
		// for each medium...
		for( Map.Entry<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> mediumEntry : m_rdcIndex.entrySet() )
		{
			buf.append( mediumEntry.getKey().getName() );
			buf.append( "\n" );
			
			TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> mediumRdcs = mediumEntry.getValue();
			
			// set up a matrix full of falses
			int numResidues = mediumRdcs.lastKey() - mediumRdcs.firstKey() + 1;
			boolean[][] flags = new boolean[BondType.values().length][numResidues];
			for( boolean[] row : flags )
			{
				Arrays.fill( row, false );
			}
			
			// fill out the true values
			int firstResidueNumber = mediumRdcs.firstKey();
			for( Map.Entry<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> entry : mediumRdcs.entrySet() )
			{
				for( BondType type : entry.getValue().keySet() )
				{
					flags[type.ordinal()][entry.getKey() - firstResidueNumber] = true;
				}
			}
			
			// finally, render to string
			buf.append( "      " );
			for( int i=0; i<numResidues; i += 5 )
			{
				buf.append( String.format( "%-5d", i + firstResidueNumber ) );
			}
			buf.append( "\n" );
			for( BondType type : BondType.values() )
			{
				buf.append( String.format( "%5s ", type.name() ) );
				for( boolean flag : flags[type.ordinal()] )
				{
					buf.append( flag ? "X" : " " );
				}
				buf.append( "\n" );
			}
		}
		return buf.toString();
	}
	
	public String dumpToString( BondType bondType )
	{
		return dumpToString( bondType, null );
	}
	
	public String dumpToString( BondType bondType, Subunit structure )
	{
		StringBuilder buf = new StringBuilder();
		
		// find the longest medium name and the first and last residues
		int longestNameLength = 0;
		int firstResidueNumber = Integer.MAX_VALUE;
		int lastResidueNumber = 0;
		for( Map.Entry<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> mediumEntry : m_rdcIndex.entrySet() )
		{
			longestNameLength = Math.max( longestNameLength, mediumEntry.getKey().getName().length() );
			firstResidueNumber = Math.min( firstResidueNumber, mediumEntry.getValue().firstKey() );
			lastResidueNumber = Math.max( lastResidueNumber, mediumEntry.getValue().lastKey() );
		}
		int numResidues = lastResidueNumber - firstResidueNumber + 1;
		
		// render the ladder
		buf.append( String.format( "%" + longestNameLength + "s  ", "" ) );
		for( int i=0; i<numResidues; i += 5 )
		{
			buf.append( String.format( "%-5d", i + firstResidueNumber ) );
		}
		buf.append( "\n" );
		
		// choose the medium ordering
		List<AlignmentMedium> media = new ArrayList<AlignmentMedium>( m_rdcIndex.keySet() );
		final Map<AlignmentMedium,Double> qFactors = new HashMap<AlignmentMedium,Double>();
		if( structure != null )
		{
			// compute all the Q-factors
			for( Map.Entry<AlignmentMedium,TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>>> mediumEntry : m_rdcIndex.entrySet() )
			{
				List<Rdc<AtomAddressReadable>> rdcs = getRdcs( mediumEntry.getKey(), bondType );
				List<Rdc<AtomAddressInternal>> internalRdcs = RdcMapper.mapReadableToInternal( new Protein( structure ), rdcs );
				double qFactor = mediumEntry.getKey().getTensor().getQFactor( structure, internalRdcs );
				qFactors.put( mediumEntry.getKey(), qFactor );
			}
			
			// sort by RMSD
			Collections.sort( media, new Comparator<AlignmentMedium>( )
			{
				@Override
				public int compare( AlignmentMedium a, AlignmentMedium b )
				{
					return Double.compare( qFactors.get( a ), qFactors.get( b ) );
				}
			} );
		}
		
		// for each medium, find out which residues have RDCs
		for( AlignmentMedium medium : media )
		{
			TreeMap<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> mediumRdcs = m_rdcIndex.get( medium );
			
			// set up an array full of falses
			boolean[] flags = new boolean[numResidues];
			Arrays.fill( flags, false );
			
			// for each residue...
			for( Map.Entry<Integer,TreeMap<BondType,Rdc<AtomAddressReadable>>> entry : mediumRdcs.entrySet() )
			{
				flags[entry.getKey() - firstResidueNumber] = !entry.getValue().isEmpty();
			}
			
			// render this line
			buf.append( String.format( "%" + longestNameLength + "s  ", medium.getName() ) );
			for( boolean flag : flags )
			{
				buf.append( flag ? "X" : " " );
			}
			// add the RMSD if needed
			if( structure != null )
			{
				buf.append( String.format( "  Q-Factor: %.2f", qFactors.get( medium ) ) );
			}
			buf.append( "\n" );
		}
		return buf.toString();
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private AlignmentMedium getMediumCheck( String mediumName )
	{
		AlignmentMedium medium = getMedium( mediumName );
		if( medium == null )
		{
			throw new IllegalArgumentException( mediumName + " was not found!" );
		}
		return medium;
	}
}
