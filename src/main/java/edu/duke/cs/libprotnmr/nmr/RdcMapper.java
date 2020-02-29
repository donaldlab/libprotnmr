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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.BondType;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RdcMapper
{
	private static final Logger m_log = LogManager.getLogger(RdcMapper.class);
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ArrayList<Rdc<AtomAddressInternal>> mapReadableToInternal( List<Protein> ensemble, List<Rdc<AtomAddressReadable>> readables )
	{
		// map the RDCs to the first protein
		ArrayList<Rdc<AtomAddressInternal>> internals = mapReadableToInternal( ensemble.get( 0 ), readables );
		
		// collect all the atom addresses into a set
		Set<AtomAddressInternal> addresses = new HashSet<AtomAddressInternal>();
		for( Rdc<AtomAddressInternal> restraint : internals )
		{
			addresses.addAll( restraint.getFroms() );
			addresses.addAll( restraint.getTos() );
		}
		
		// make sure the address are the same across the ensemble
		if( !AddressMapper.areAtomsSameAcrossEnsemble( addresses, ensemble ) )
		{
			throw new IllegalArgumentException( "Ensemble proteins do not have the same atom order! RDCs must be mapped individually!" );
		}
		
		return internals;
	}
	
	public static ArrayList<Rdc<AtomAddressInternal>> mapReadableToInternal( Protein protein, List<Rdc<AtomAddressReadable>> readables )
	{
		return mapReadableToInternal( protein, readables, false );
	}
	
	public static ArrayList<Rdc<AtomAddressInternal>> mapReadableToInternal( Protein protein, List<Rdc<AtomAddressReadable>> readables, boolean addNulls )
	{
		ArrayList<Rdc<AtomAddressInternal>> internals = new ArrayList<Rdc<AtomAddressInternal>>( readables.size() );
		for( Rdc<AtomAddressReadable> readable : readables )
		{
			Rdc<AtomAddressInternal> rdc = mapReadableToInternal( protein, readable );
			if( rdc != null || addNulls )
			{
				internals.add( rdc );
			}
		}
		
		// report any unmapped RDCs if necessary
		if( !addNulls && readables.size() > internals.size() )
		{
			int numMissing = readables.size() - internals.size();
			m_log.warn( numMissing + " RDCs were not mapped!" );
			List<Rdc<AtomAddressInternal>> internalsWithNulls = mapReadableToInternal( protein, readables, true );
			if( internalsWithNulls.size() != readables.size() )
			{
				throw new Error( "RDC mapping error reporting failed!" );
			}
			for( int i=0; i<readables.size(); i++ )
			{
				if( internalsWithNulls.get( i ) == null )
				{
					m_log.warn( readables.get( i ).toString() );
				}
			}
		}
		
		return internals;
	}
	
	public static Rdc<AtomAddressInternal> mapReadableToInternal( Protein protein, Rdc<AtomAddressReadable> readable )
	{
		// HACKHACK: special case for CaHa RDCs on glycines - map Ha to Ha2 and Ha3
		Residue residue = protein.getResidue( readable.getFrom() );
		if( BondType.lookup( readable ) == BondType.CaHa && residue.getAminoAcid() == AminoAcid.Glycine )
		{
			// get the internal addresses
			AtomAddressInternal addressCa = AddressMapper.mapAddress( protein, readable.getFrom() );
			AtomAddressInternal addressHa2 = new AtomAddressInternal( addressCa );
			addressHa2.setAtomId( residue.getAtomByName( "HA2" ).getId() );
			AtomAddressInternal addressHa3 = new AtomAddressInternal( addressCa );
			addressHa3.setAtomId( residue.getAtomByName( "HA3" ).getId() );
			
			// build the RDC instance
			Rdc<AtomAddressInternal> internal = new Rdc<AtomAddressInternal>( addressCa, addressHa2 );
			internal.getTos().add( addressHa3 );
			internal.setValue( readable.getValue() );
			internal.setError( readable.getError() );
			return internal;
		}
		
		List<AtomAddressInternal> froms = AddressMapper.mapAddressExpandPseudoatoms( protein, readable.getFrom() );
		List<AtomAddressInternal> tos = AddressMapper.mapAddressExpandPseudoatoms( protein, readable.getTo() );
		if( froms == null || froms.size() != 1 || tos == null || tos.size() != 1 )
		{
			return null;
		}
		Rdc<AtomAddressInternal> internal = new Rdc<AtomAddressInternal>( froms.get( 0 ), tos.get( 0 ) );
		internal.setValue( readable.getValue() );
		internal.setError( readable.getError() );
		return internal;
	}
	
	public static ArrayList<Rdc<AtomAddressReadable>> mapInternalToReadable( Protein protein, List<Rdc<AtomAddressInternal>> internals )
	{
		ArrayList<Rdc<AtomAddressReadable>> readables = new ArrayList<Rdc<AtomAddressReadable>>( internals.size() );
		for( Rdc<AtomAddressInternal> internal : internals )
		{
			readables.add( mapInternalToReadable( protein, internal ) );
		}
		return readables;
	}	
	
	public static Rdc<AtomAddressReadable> mapInternalToReadable( Protein protein, Rdc<AtomAddressInternal> internal )
	{
		// HACKHACK: special case for CaHa RDCs on glycines - map Ha2 and Ha3 to Ha
		if( protein.getResidue( internal.getFrom() ).getAminoAcid() == AminoAcid.Glycine )
		{
			if( protein.getAtom( internal.getFrom() ).getName().equalsIgnoreCase( "CA" ) )
			{
				if( internal.getTos().size() == 2 )
				{
					List<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>( internal.getTos() );
					String nameA = protein.getAtom( addresses.get( 0 ) ).getName().toUpperCase();
					String nameB = protein.getAtom( addresses.get( 1 ) ).getName().toUpperCase();
					if( ( nameA.equals( "HA2" ) && nameB.equals( "HA3" ) )
						|| ( nameA.equals( "HA3" ) && nameB.equals( "HA2" ) ) )
					{
						// ok, this is definitely a CaHa RDC
						
						// get the new addresses
						AtomAddressReadable addressCa = AddressMapper.mapAddress( protein, internal.getFrom() );
						AtomAddressReadable addressHa = new AtomAddressReadable( addressCa );
						addressHa.setAtomName( "HA" );
						
						// build the RDC instance
						Rdc<AtomAddressReadable> readable = new Rdc<AtomAddressReadable>( addressCa, addressHa );
						readable.setValue( internal.getValue() );
						readable.setError( internal.getError() );
						return readable;
					}
				}
			}
		}
		
		Rdc<AtomAddressReadable> readable = new Rdc<AtomAddressReadable>(
			AddressMapper.mapAddress( protein, internal.getFrom() ),
			AddressMapper.mapAddress( protein, internal.getTo() )
		);
		readable.setValue( internal.getValue() );
		readable.setError( internal.getError() );
		return readable;
	}
}
