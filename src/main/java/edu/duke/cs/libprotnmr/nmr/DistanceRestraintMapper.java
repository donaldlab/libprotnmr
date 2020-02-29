/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.nmr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.AtomAddressSubunitResidue;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.pseudoatoms.Pseudoatoms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistanceRestraintMapper
{
	private static final Logger m_log = LogManager.getLogger(DistanceRestraintMapper.class);
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	// for each entry in the list, attempt a replacement if the sub-atoms are available
	private static final boolean DefaultCollapsePseudoatoms = false;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> mapReadableToInternal( List<DistanceRestraint<AtomAddressReadable>> readables, List<Protein> ensemble )
	{
		Sequences referenceSequences = Sequences.getUniqueSequences( ensemble );
		
		// handle pseudoatoms
		PseudoatomBuilder builder = PseudoatomBuilder.getInstance();
		boolean hasPseudoatoms = builder.buildDistanceRestraints( referenceSequences, readables ) > 0;
		if( hasPseudoatoms )
		{
			for( Protein protein : ensemble )
			{
				if( !builder.hasPseudoatoms( protein ) )
				{
					builder.build( protein );
				}
			}
		}
		
		// map the restraints to the first protein
		ArrayList<DistanceRestraint<AtomAddressInternal>> internals = mapReadableToInternal( readables, ensemble.get( 0 ) );
		
		// collect all the atom addresses into a set
		Set<AtomAddressInternal> addresses = new HashSet<AtomAddressInternal>();
		for( DistanceRestraint<AtomAddressInternal> restraint : internals )
		{
			addresses.addAll( restraint.getLefts() );
			addresses.addAll( restraint.getRights() );
		}
		
		// make sure the address are the same across the ensemble
		if( !AddressMapper.areAtomsSameAcrossEnsemble( addresses, ensemble ) )
		{
			throw new IllegalArgumentException( "Ensemble proteins do not have the same atom order! Restraints must be mapped individually!" );
		}
		
		return internals;
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> mapReadableToInternal( List<DistanceRestraint<AtomAddressReadable>> readables, Protein protein )
	{
		return mapReadableToInternal( readables, protein, false );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> mapReadableToInternal( List<DistanceRestraint<AtomAddressReadable>> readables, Protein protein, boolean addNulls )
	{
		// map the noes
		ArrayList<DistanceRestraint<AtomAddressInternal>> internals = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressReadable> readable : readables )
		{
			DistanceRestraint<AtomAddressInternal> restraint = mapReadableToInternal( readable, protein );
			if( restraint != null || addNulls )
			{
				internals.add( restraint );
			}
		}
		
		// report any unmapped restraints if necessary
		if( !addNulls && readables.size() > internals.size() )
		{
			int numMissing = readables.size() - internals.size();
			m_log.warn( numMissing + " distance restraints were not mapped!" );
			List<DistanceRestraint<AtomAddressInternal>> internalsWithNulls = mapReadableToInternal( readables, protein, true );
			if( internalsWithNulls.size() != readables.size() )
			{
				throw new Error( "Distance restraint mapping error reporting failed!" );
			}
			for( int i=0; i<readables.size(); i++ )
			{
				if( internalsWithNulls.get( i ) == null )
				{
					m_log.warn( "\t" + readables.get( i ).toString() );
				}
			}
		}
		
		return internals;
	}
	
	public static DistanceRestraint<AtomAddressInternal> mapReadableToInternal( DistanceRestraint<AtomAddressReadable> readable, Protein protein )
	{
		// shortcut
		if( readable == null )
		{
			return null;
		}
		
		DistanceRestraint<AtomAddressInternal> internal = new DistanceRestraint<AtomAddressInternal>();
		internal.setLefts( AddressMapper.mapReadableAtomAddresses( readable.getLefts(), protein ) );
		internal.setRights( AddressMapper.mapReadableAtomAddresses( readable.getRights(), protein ) );
		internal.setMinDistance( readable.getMinDistance() );
		internal.setMaxDistance( readable.getMaxDistance() );
		
		// don't output silly restraints
		if( internal.getLefts().size() <= 0 || internal.getRights().size() <= 0 )
		{
			return null;
		}
		
		return internal;
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressReadable>> mapInternalToReadable( List<DistanceRestraint<AtomAddressInternal>> internals, Protein protein )
	{
		return mapInternalToReadable( internals, protein, DefaultCollapsePseudoatoms );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressReadable>> mapInternalToReadable( List<DistanceRestraint<AtomAddressInternal>> internals, Protein protein, boolean collapsePseudoatoms )
	{
		ArrayList<DistanceRestraint<AtomAddressReadable>> readables = new ArrayList<DistanceRestraint<AtomAddressReadable>>( internals.size() );
		
		for( DistanceRestraint<AtomAddressInternal> internal : internals )
		{
			readables.add( mapInternalToReadable( internal, protein, collapsePseudoatoms ) );
		}
		
		return readables;
	}
	
	public static DistanceRestraint<AtomAddressReadable> mapInternalToReadable( DistanceRestraint<AtomAddressInternal> internal, Protein protein )
	{
		return mapInternalToReadable( internal, protein, DefaultCollapsePseudoatoms );
	}
	
	public static DistanceRestraint<AtomAddressReadable> mapInternalToReadable( DistanceRestraint<AtomAddressInternal> internal, Protein protein, boolean collapsePseudoatoms )
	{
		// shortcut
		if( internal == null )
		{
			return null;
		}
		
		if( collapsePseudoatoms )
		{
			// modify a copy of the restraint
			internal = new DistanceRestraint<AtomAddressInternal>( internal );
			collapsePseudoatoms( protein, internal.getLefts() );
			collapsePseudoatoms( protein, internal.getRights() );
		}
		
		DistanceRestraint<AtomAddressReadable> readable = new DistanceRestraint<AtomAddressReadable>();
		readable.setLefts( AddressMapper.mapInternalAtomAddresses( internal.getLefts(), protein ) );
		readable.setRights( AddressMapper.mapInternalAtomAddresses( internal.getRights(), protein ) );
		readable.setMinDistance( internal.getMinDistance() );
		readable.setMaxDistance( internal.getMaxDistance() );
		
		// don't output silly NOEs
		if( readable.getLefts().size() <= 0 || readable.getRights().size() <= 0 )
		{
			return null;
		}
		
		return readable;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	// NOTE: this function might be general enough to move somewhere else
	private static void collapsePseudoatoms( Protein protein, Set<AtomAddressInternal> addresses )
	{
		// make sets of all atoms for each subunit/residue combo...
		HashMap<AtomAddressSubunitResidue,Set<AtomAddressInternal>> map = new HashMap<AtomAddressSubunitResidue,Set<AtomAddressInternal>>();
		for( AtomAddressInternal address : addresses )
		{
			AtomAddressSubunitResidue subunitResidue = new AtomAddressSubunitResidue( address );
			Set<AtomAddressInternal> set = map.get( subunitResidue );
			if( set == null )
			{
				set = new HashSet<AtomAddressInternal>();
				map.put( subunitResidue, set );
			}
			set.add( address );
		}
		
		Pseudoatoms pseudoatoms = PseudoatomBuilder.getPseudoatoms();
		
		// for each combo...
		for( AtomAddressSubunitResidue subunitResidue : map.keySet() )
		{
			Residue residue = protein.getResidue( subunitResidue );
			Set<AtomAddressInternal> atomSet = map.get( subunitResidue );
			
			// for each pseudoatom at this residue...
			for( String pseudoatomName : pseudoatoms.getPseudoatomNames( residue.getAminoAcid() ) )
			{
				// if all sub-atoms are present
				List<String> subAtomNames = pseudoatoms.getAtoms( residue.getAminoAcid(), pseudoatomName );
				List<AtomAddressInternal> foundAddresses = collectAddresses( protein, subAtomNames, atomSet );
				if( foundAddresses.size() == subAtomNames.size() )
				{
					// remove the sub-atoms
					addresses.removeAll( foundAddresses );
					
					// add the pseudoatom
					addresses.add( new AtomAddressInternal(
						subunitResidue.getSubunitId(),
						subunitResidue.getResidueId(),
						residue.getAtomByName( pseudoatomName ).getId()
					) );
				}
			}
		}
	}
	
	private static List<AtomAddressInternal> collectAddresses( Protein protein, List<String> subAtomNames, Set<AtomAddressInternal> addresses )
	{
		ArrayList<AtomAddressInternal> foundAddresses = new ArrayList<AtomAddressInternal>();
		for( String subAtomName : subAtomNames )
		{
			for( AtomAddressInternal address : addresses )
			{
				if( protein.getAtom( address ).getName().equalsIgnoreCase( subAtomName ) )
				{
					foundAddresses.add( address );
				}
			}
		}
		return foundAddresses;
	}
}
