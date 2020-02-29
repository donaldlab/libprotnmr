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
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DihedralRestraintMapper
{
	private static final Logger m_log = LogManager.getLogger(DihedralRestraintMapper.class);
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ArrayList<DihedralRestraint<AtomAddressInternal>> mapReadableToInternal( List<DihedralRestraint<AtomAddressReadable>> readables, List<Protein> ensemble )
	{
		// map the restraints to the first protein
		ArrayList<DihedralRestraint<AtomAddressInternal>> internals = mapReadableToInternal( readables, ensemble.get( 0 ) );
		
		// collect all the atom addresses into a set
		Set<AtomAddressInternal> addresses = new HashSet<AtomAddressInternal>();
		for( DihedralRestraint<AtomAddressInternal> restraint : internals )
		{
			addresses.add( restraint.getA() );
			addresses.add( restraint.getB() );
			addresses.add( restraint.getC() );
			addresses.add( restraint.getD() );
		}
		
		// make sure the address are the same across the ensemble
		if( !AddressMapper.areAtomsSameAcrossEnsemble( addresses, ensemble ) )
		{
			throw new IllegalArgumentException( "Ensemble proteins do not have the same atom order! Restraints must be mapped individually!" );
		}
		
		return internals;
	}
	
	public static ArrayList<DihedralRestraint<AtomAddressInternal>> mapReadableToInternal( List<DihedralRestraint<AtomAddressReadable>> readables, Protein protein )
	{
		return mapReadableToInternal( readables, protein, false );
	}
	
	public static ArrayList<DihedralRestraint<AtomAddressInternal>> mapReadableToInternal( List<DihedralRestraint<AtomAddressReadable>> readables, Protein protein, boolean addNulls )
	{
		// map the restraints
		ArrayList<DihedralRestraint<AtomAddressInternal>> internals = new ArrayList<DihedralRestraint<AtomAddressInternal>>();
		for( DihedralRestraint<AtomAddressReadable> readable : readables )
		{
			DihedralRestraint<AtomAddressInternal> restraint = mapReadableToInternal( readable, protein );
			if( restraint != null || addNulls )
			{
				internals.add( restraint );
			}
		}
		
		// report any unmapped restraints if necessary
		if( !addNulls && readables.size() > internals.size() )
		{
			int numMissing = readables.size() - internals.size();
			m_log.warn( numMissing + " dihedral restraints were not mapped!" );
			List<DihedralRestraint<AtomAddressInternal>> internalsWithNulls = mapReadableToInternal( readables, protein, true );
			if( internalsWithNulls.size() != readables.size() )
			{
				throw new Error( "Dihedral restraint mapping error reporting failed!" );
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
	
	public static DihedralRestraint<AtomAddressInternal> mapReadableToInternal( DihedralRestraint<AtomAddressReadable> readable, Protein protein )
	{
		// shortcut
		if( readable == null )
		{
			return null;
		}
		
		AtomAddressInternal a = AddressMapper.mapAddress( protein, readable.getA() );
		AtomAddressInternal b = AddressMapper.mapAddress( protein, readable.getB() );
		AtomAddressInternal c = AddressMapper.mapAddress( protein, readable.getC() );
		AtomAddressInternal d = AddressMapper.mapAddress( protein, readable.getD() );
		if( a == null || b == null || c == null || d == null )
		{
			return null;
		}
		
		return new DihedralRestraint<AtomAddressInternal>(
			a, b, c, d,
			readable.getValue(),
			readable.getError()
		);
	}
	
	public static ArrayList<DihedralRestraint<AtomAddressReadable>> mapInternalToReadable( List<DihedralRestraint<AtomAddressInternal>> internals, Protein protein )
	{
		ArrayList<DihedralRestraint<AtomAddressReadable>> readables = new ArrayList<DihedralRestraint<AtomAddressReadable>>( internals.size() );
		for( DihedralRestraint<AtomAddressInternal> internal : internals )
		{
			readables.add( mapInternalToReadable( internal, protein ) );
		}
		return readables;
	}
	
	public static DihedralRestraint<AtomAddressReadable> mapInternalToReadable( DihedralRestraint<AtomAddressInternal> internal, Protein protein )
	{
		// shortcut
		if( internal == null )
		{
			return null;
		}
		
		AtomAddressReadable a = AddressMapper.mapAddress( protein, internal.getA() );
		AtomAddressReadable b = AddressMapper.mapAddress( protein, internal.getB() );
		AtomAddressReadable c = AddressMapper.mapAddress( protein, internal.getC() );
		AtomAddressReadable d = AddressMapper.mapAddress( protein, internal.getD() );
		if( a == null || b == null || c == null || d == null )
		{
			return null;
		}
		
		return new DihedralRestraint<AtomAddressReadable>(
			a, b, c, d,
			internal.getValue(),
			internal.getError()
		);
	}
	
	public static void convertToRadians( DihedralRestraint<?> restraint )
	{
		restraint.setValue( Math.toRadians( restraint.getValue() ) );
		restraint.setError( Math.toRadians( restraint.getError() ) );
	}
	
	public static void convertToRadians( List<DihedralRestraint<?>> restraints )
	{
		for( DihedralRestraint<?> restraint : restraints )
		{
			convertToRadians( restraint );
		}
	}
	
}
