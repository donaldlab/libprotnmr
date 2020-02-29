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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.bond.BondGraphBuilder;
import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class ChemicalShiftMapper
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static ArrayList<ChemicalShift<AtomAddressReadable>> filter( List<ChemicalShift<AtomAddressReadable>> shifts, Element element )
	{
		return filter( shifts, Arrays.asList( element ) );
	}
	
	public static ArrayList<ChemicalShift<AtomAddressReadable>> filter( List<ChemicalShift<AtomAddressReadable>> shifts, Iterable<Element> elements )
	{
		// build a lookup set for the elements
		Set<Element> elementSet = new TreeSet<Element>();
		for( Element element : elements )
		{
			elementSet.add( element );
		}
		
		return filter( shifts, elementSet );
	}
	
	public static ArrayList<ChemicalShift<AtomAddressReadable>> filter( List<ChemicalShift<AtomAddressReadable>> shifts, Set<Element> elements )
	{
		// pull out all the chemical shifts matching the desired elements
		ArrayList<ChemicalShift<AtomAddressReadable>> index = new ArrayList<ChemicalShift<AtomAddressReadable>>();
		for( ChemicalShift<AtomAddressReadable> shift : shifts )
		{
			if( elements.contains( shift.getElement() ) )
			{
				index.add( shift );
			}
		}
		return index;
	}
	
	public static ArrayList<ChemicalShiftPair<AtomAddressReadable>> associatePairs( Sequence sequence, Iterable<ChemicalShift<AtomAddressReadable>> hydrogenShifts, Iterable<ChemicalShift<AtomAddressReadable>> heavyShifts )
	{
		return associatePairs( sequence, buildShiftLookup( hydrogenShifts ), buildShiftLookup( heavyShifts ) );
	}
	
	public static ArrayList<ChemicalShiftPair<AtomAddressReadable>> associatePairs( Sequence sequence, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> heavyShifts )
	{
		// associate the pairs
		ArrayList<ChemicalShiftPair<AtomAddressReadable>> pairs = new ArrayList<ChemicalShiftPair<AtomAddressReadable>>();
		for( ChemicalShift<AtomAddressReadable> hydrogenShift : hydrogenShifts.values() )
		{
			// look up the heavy atom shift
			ResidueType residueType = sequence.getResidueTypeByNumber( hydrogenShift.getAddress().getResidueNumber() );
			AtomAddressReadable heavyAddress = getHeavyAddress( hydrogenShift.getAddress(), hydrogenShift.getAminoAcid(), residueType );
			if( heavyAddress == null )
			{
				// this is bad, don't try to recover
				throw new Error( "Warning: No heavy atom found for proton: " + hydrogenShift.getAddress() );
			}
			
			// get the chemical shift
			ChemicalShift<AtomAddressReadable> heavyShift = heavyShifts.get( heavyAddress );
			if( heavyShift == null )
			{
				continue;
			}
			
			// only return pairs where we have both chemical shifts
			pairs.add( new ChemicalShiftPair<AtomAddressReadable>( hydrogenShift, heavyShift ) );
		}
		return pairs;
	}
	
	public static ArrayList<ChemicalShift<AtomAddressReadable>> getOrphanedHydrogenShifts( Sequence sequence, Iterable<ChemicalShift<AtomAddressReadable>> hydrogenShifts, Iterable<ChemicalShift<AtomAddressReadable>> heavyShifts )
	{
		return getOrphanedHydrogenShifts( sequence, buildShiftLookup( hydrogenShifts ), buildShiftLookup( heavyShifts ) );
	}
	
	public static ArrayList<ChemicalShift<AtomAddressReadable>> getOrphanedHydrogenShifts( Sequence sequence, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> heavyShifts )
	{
		ArrayList<ChemicalShift<AtomAddressReadable>> orphanShifts = new ArrayList<ChemicalShift<AtomAddressReadable>>();
		for( ChemicalShift<AtomAddressReadable> hydrogenShift : hydrogenShifts.values() )
		{
			// is there a heavy atom shift?
			ResidueType residueType = sequence.getResidueTypeByNumber( hydrogenShift.getAddress().getResidueNumber() );
			AtomAddressReadable heavyAddress = getHeavyAddress( hydrogenShift.getAddress(), hydrogenShift.getAminoAcid(), residueType );
			if( !heavyShifts.containsKey( heavyAddress ) )
			{
				// no? then the hydrogen shift is orphaned
				orphanShifts.add( hydrogenShift );
			}
		}
		return orphanShifts;
	}
	
	public static ArrayList<ChemicalShift<AtomAddressInternal>> mapReadableToInternal( List<ChemicalShift<AtomAddressReadable>> readableShifts, Protein protein )
	{
		return mapReadableToInternal( readableShifts, protein, false );
	}
	
	public static ArrayList<ChemicalShift<AtomAddressInternal>> mapReadableToInternal( List<ChemicalShift<AtomAddressReadable>> readableShifts, Subunit subunit )
	{
		return mapReadableToInternal( readableShifts, subunit, false );
	}
	
	public static ArrayList<ChemicalShift<AtomAddressInternal>> mapReadableToInternal( List<ChemicalShift<AtomAddressReadable>> readableShifts, Protein protein, boolean addNulls )
	{
		ArrayList<ChemicalShift<AtomAddressInternal>> mappedShifts = new ArrayList<ChemicalShift<AtomAddressInternal>>();
		for( ChemicalShift<AtomAddressReadable> shift : readableShifts )
		{
			ChemicalShift<AtomAddressInternal> mappedShift = mapReadableToInternal( shift, protein );
			if( mappedShift != null || addNulls )
			{
				mappedShifts.add( mappedShift );
			}
		}
		return mappedShifts;
	}
	
	public static ArrayList<ChemicalShift<AtomAddressInternal>> mapReadableToInternal( List<ChemicalShift<AtomAddressReadable>> readableShifts, Subunit subunit, boolean addNulls )
	{
		ArrayList<ChemicalShift<AtomAddressInternal>> mappedShifts = new ArrayList<ChemicalShift<AtomAddressInternal>>();
		for( ChemicalShift<AtomAddressReadable> shift : readableShifts )
		{
			ChemicalShift<AtomAddressInternal> mappedShift = mapReadableToInternal( shift, subunit );
			if( mappedShift != null || addNulls )
			{
				mappedShifts.add( mappedShift );
			}
		}
		return mappedShifts;
	}
	
	public static ChemicalShift<AtomAddressInternal> mapReadableToInternal( ChemicalShift<AtomAddressReadable> readableShift, Protein protein )
	{
		if( !readableShift.getAddress().hasSubunitName() )
		{
			throw new IllegalArgumentException( "Unable to map shifts without subunit assignments to proteins. Use the subunit methods instead." );
		}
		
		ChemicalShift<AtomAddressInternal> internalShift = new ChemicalShift<AtomAddressInternal>();
		internalShift.setAddress( AddressMapper.mapAddress( protein, readableShift.getAddress() ) );
		internalShift.setValue( readableShift.getValue() );
		internalShift.setError( readableShift.getError() );
		
		if( internalShift.getAddress() == null )
		{
			return null;
		}
		
		return internalShift;
	}
	
	public static ChemicalShift<AtomAddressInternal> mapReadableToInternal( ChemicalShift<AtomAddressReadable> readableShift, Subunit subunit )
	{
		ChemicalShift<AtomAddressInternal> internalShift = new ChemicalShift<AtomAddressInternal>();
		internalShift.setAddress( AddressMapper.mapAddress( subunit, readableShift.getAddress() ) );
		internalShift.setValue( readableShift.getValue() );
		internalShift.setError( readableShift.getError() );
		
		if( internalShift.getAddress() == null )
		{
			return null;
		}
		
		return internalShift;
	}
	
	public static TreeMap<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> buildShiftLookup( Iterable<ChemicalShift<AtomAddressReadable>> shifts )
	{
		TreeMap<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> lookup = new TreeMap<AtomAddressReadable,ChemicalShift<AtomAddressReadable>>();
		for( ChemicalShift<AtomAddressReadable> shift : shifts )
		{
			lookup.put( shift.getAddress(), shift );
		}
		return lookup;
	}
	
	public static TreeMap<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> buildPairLookup( Iterable<ChemicalShiftPair<AtomAddressReadable>> pairs )
	{
		TreeMap<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> lookup = new TreeMap<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>>();
		for( ChemicalShiftPair<AtomAddressReadable> pair : pairs )
		{
			lookup.put( pair.getHydrogenShift().getAddress(), pair );
		}
		return lookup;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static AtomAddressReadable getHeavyAddress( AtomAddressReadable hydrogenAddress, AminoAcid aminoAcid, ResidueType residueType )
	{
		String heavyAtomName = BondGraphBuilder.getInstance().getHeavyAtomName( hydrogenAddress.getAtomName(), aminoAcid, residueType );
		if( heavyAtomName == null )
		{
			return null;
		}
		return new AtomAddressReadable( hydrogenAddress.getSubunitName(), hydrogenAddress.getResidueNumber(), heavyAtomName );
	}
}
