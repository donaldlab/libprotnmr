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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.duke.cs.libprotnmr.bond.BondGraphBuilder;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistanceRestraintReassigner
{
	private static Logger m_log = LogManager.getLogger(DistanceRestraintReassigner.class);
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static List<DistanceRestraint<AtomAddressReadable>> reassign1D( Sequence sequence, List<DistanceRestraint<AtomAddressReadable>> restraints, Iterable<ChemicalShift<AtomAddressReadable>> hydrogenShifts, double hydrogenWindowWidth )
	{
		// do the chemical shifts have subunit assignments?
		boolean shiftsHaveSubunitAssignments = doShiftsHaveSubunitAssignments( hydrogenShifts );
		
		Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> lookup = ChemicalShiftMapper.buildShiftLookup( hydrogenShifts );
		ArrayList<DistanceRestraint<AtomAddressReadable>> reassignedRestraints = new ArrayList<DistanceRestraint<AtomAddressReadable>>( restraints.size() );
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			DistanceRestraint<AtomAddressReadable> reassignedRestraint = new DistanceRestraint<AtomAddressReadable>( restraint );
			reassignedRestraint.setLefts( getAddressesNearAddress1D( sequence, restraint.getLefts(), lookup, hydrogenWindowWidth, shiftsHaveSubunitAssignments ) );
			reassignedRestraint.setRights( getAddressesNearAddress1D( sequence, restraint.getRights(), lookup, hydrogenWindowWidth, shiftsHaveSubunitAssignments ) );
			reassignedRestraints.add( reassignedRestraint );
			restorePseudoatoms( sequence, restraint, reassignedRestraint );
		}
		return reassignedRestraints;
	}
	
	public static List<DistanceRestraint<AtomAddressReadable>> reassignDouble2D( Sequence sequence, List<DistanceRestraint<AtomAddressReadable>> restraints, Iterable<ChemicalShift<AtomAddressReadable>> hydrogenShifts, Iterable<ChemicalShift<AtomAddressReadable>> carbonShifts, Iterable<ChemicalShift<AtomAddressReadable>> nitrogenShifts, double hydrogenWindowWidth, double carbonWindowWidth, double nitrogenWindowWidth )
	{
		// build the pairs
		Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenLookup = ChemicalShiftMapper.buildShiftLookup( hydrogenShifts );
		Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> carbonLookup = ChemicalShiftMapper.buildShiftLookup( carbonShifts );
		Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> nitrogenLookup = ChemicalShiftMapper.buildShiftLookup( nitrogenShifts );
		List<ChemicalShiftPair<AtomAddressReadable>> carbonPairs = ChemicalShiftMapper.associatePairs( sequence, hydrogenLookup, carbonLookup );
		List<ChemicalShiftPair<AtomAddressReadable>> nitrogenPairs = ChemicalShiftMapper.associatePairs( sequence, hydrogenLookup, nitrogenLookup );
		
		return reassignDouble2DWithPairs( sequence, restraints, hydrogenShifts, carbonPairs, nitrogenPairs, hydrogenWindowWidth, carbonWindowWidth, nitrogenWindowWidth );
	}
	
	public static List<DistanceRestraint<AtomAddressReadable>> reassignDouble2DWithPairs( Sequence sequence, List<DistanceRestraint<AtomAddressReadable>> restraints, Iterable<ChemicalShift<AtomAddressReadable>> hydrogenShifts, Iterable<ChemicalShiftPair<AtomAddressReadable>> carbonPairs, Iterable<ChemicalShiftPair<AtomAddressReadable>> nitrogenPairs, double hydrogenWindowWidth, double carbonWindowWidth, double nitrogenWindowWidth )
	{
		// do the chemical shifts have subunit assignments?
		boolean shiftsHaveSubunitAssignments = doShiftsHaveSubunitAssignments( hydrogenShifts );
		
		// build the lookup structures
		Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenLookup = ChemicalShiftMapper.buildShiftLookup( hydrogenShifts );
		Map<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> carbonPairsLookup = ChemicalShiftMapper.buildPairLookup( carbonPairs );
		Map<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> nitrogenPairsLookup = ChemicalShiftMapper.buildPairLookup( nitrogenPairs );
		
		ArrayList<DistanceRestraint<AtomAddressReadable>> reassignedRestraints = new ArrayList<DistanceRestraint<AtomAddressReadable>>( restraints.size() );
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			DistanceRestraint<AtomAddressReadable> reassignedRestraint = new DistanceRestraint<AtomAddressReadable>( restraint );
			
			// which was the 2D side? It probably only has one assignment
			// NOTE: we can't ever really know which one was the 2D side, so it's not critical that we get it right here
			boolean swapSides = false;
			Set<AtomAddressReadable> side2D = null;
			Set<AtomAddressReadable> side1D = null;
			if( restraint.getLefts().size() == 1 )
			{
				side2D = restraint.getLefts();
				side1D = restraint.getRights();
			}
			else
			{
				side2D = restraint.getRights();
				side1D = restraint.getLefts();
				swapSides = true;
			}
			
			// make sure we always add the original assignments (need a deep copy)
			Set<AtomAddressReadable> side2DCopy = new HashSet<AtomAddressReadable>();
			for( AtomAddressReadable address : side2D )
			{
				side2DCopy.add( new AtomAddressReadable( address ) );
			}
			reassignedRestraint.setLefts( side2DCopy );
			Set<AtomAddressReadable> side1DCopy = new HashSet<AtomAddressReadable>();
			for( AtomAddressReadable address : side1D )
			{
				side1DCopy.add( new AtomAddressReadable( address ) );
			}
			reassignedRestraint.setRights( side1DCopy );
			
			// handle the 2D side
			Element heavyElement = getHeavyElement( sequence, side2D.iterator().next() );
			if( heavyElement == Element.Carbon )
			{
				reassignedRestraint.getLefts().addAll( getAddressesNearAddress2D(
					sequence,
					side2D,
					hydrogenLookup,
					carbonPairsLookup,
					hydrogenWindowWidth,
					carbonWindowWidth,
					shiftsHaveSubunitAssignments
				) );
			}
			else if( heavyElement == Element.Nitrogen )
			{
				reassignedRestraint.getLefts().addAll( getAddressesNearAddress2D(
					sequence,
					side2D,
					hydrogenLookup,
					nitrogenPairsLookup,
					hydrogenWindowWidth,
					nitrogenWindowWidth,
					shiftsHaveSubunitAssignments
				) );
			}
			else
			{
				assert( false ) : "Unknown element: " + heavyElement;
			}
			
			// handle the 1D side
			reassignedRestraint.getRights().addAll( getAddressesNearAddress1D(
				sequence,
				side1D,
				hydrogenLookup,
				hydrogenWindowWidth,
				shiftsHaveSubunitAssignments
			) );
			
			if( swapSides )
			{
				reassignedRestraint.swap();
			}
			reassignedRestraints.add( reassignedRestraint );
			
			restorePseudoatoms( sequence, restraint, reassignedRestraint );
		}
		
		return reassignedRestraints;
	}
	
	public static HashSet<AtomAddressReadable> getAddressesNearShift1D( Iterable<ChemicalShift<AtomAddressReadable>> shifts, double value, double windowSize )
	{
		HashSet<AtomAddressReadable> nearbyAddresses = new HashSet<AtomAddressReadable>();
				
		// NOTE: this could probably be sped up to O(logn+k) using a geometric algorithm
		// but it's probably not worth the trouble to implement
		for( ChemicalShift<AtomAddressReadable> shift : shifts )
		{		
			if( isNearby1D( shift, value, windowSize ) )
			{
				nearbyAddresses.add( shift.getAddress() );
			}
		}
		return nearbyAddresses;
	}
	
	public static HashSet<AtomAddressReadable> getAddressesNearShift2D( Iterable<ChemicalShiftPair<AtomAddressReadable>> pairs, double hydrogenValue, double heavyValue, double hydrogenWindowSize, double heavyWindowSize )
	{
		// NOTE: again, we could probably implement a fast geometric algorithm here, but it's probably not worth the trouble
		HashSet<AtomAddressReadable> nearbyAddresses = new HashSet<AtomAddressReadable>();
		for( ChemicalShiftPair<AtomAddressReadable> pair : pairs )
		{
			if( isNearby2D( pair, hydrogenValue, heavyValue, hydrogenWindowSize, heavyWindowSize ) )
			{
				nearbyAddresses.add( pair.getHydrogenShift().getAddress() );
			}
		}
		return nearbyAddresses;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static boolean doShiftsHaveSubunitAssignments( Iterable<ChemicalShift<AtomAddressReadable>> shifts )
	{
		for( ChemicalShift<AtomAddressReadable> shift : shifts )
		{
			if( shift.getAddress().hasSubunitName() )
			{
				return true;
			}
		}
		return false;
	}
	
	private static Set<AtomAddressReadable> getAddressesNearAddress1D( Sequence sequence, Iterable<AtomAddressReadable> addresses, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, double hydrogenWindowWidth, boolean shiftsHaveSubunitAssignments )
	{
		HashSet<AtomAddressReadable> relaxedAddresses = new HashSet<AtomAddressReadable>();
		for( AtomAddressReadable address : addresses )
		{
			relaxedAddresses.addAll( getAddressesNearAddress1D( sequence, address, hydrogenShifts, hydrogenWindowWidth, shiftsHaveSubunitAssignments ) );
		}
		return relaxedAddresses;
	}
	
	private static Set<AtomAddressReadable> getAddressesNearAddress1D( Sequence sequence, AtomAddressReadable address, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, double hydrogenWindowWidth, boolean shiftsHaveSubunitAssignments )
	{
		// if the shifts don't have subunit assignments, remove the subunit assignment of the address
		AtomAddressReadable addressCopy = new AtomAddressReadable( address );
		if( !shiftsHaveSubunitAssignments )
		{
			addressCopy.omitSubunitName();
		}
		
		// find all the chemical shifts related to this address (pseudoatoms make this complicated)
		List<ChemicalShift<AtomAddressReadable>> addressShifts = new ArrayList<ChemicalShift<AtomAddressReadable>>();
		
		// try looking for a shift for the address directly
		ChemicalShift<AtomAddressReadable> shift = hydrogenShifts.get( addressCopy );
		if( shift != null )
		{
			addressShifts.add( shift );
		}
		// otherwise, search the sub-atoms of the pseudoatom for shifts
		else if( Element.getByAtomName( addressCopy.getAtomName() ).isPseudoatom() )
		{
			for( AtomAddressReadable subAddress : getSubAddresses( sequence, addressCopy ) )
			{
				shift = hydrogenShifts.get( subAddress );
				if( shift != null )
				{
					addressShifts.add( shift );
				}
			}
			if( addressShifts.isEmpty() )
			{
				m_log.warn( "No chemical shift found for atom: " + address + " and no chemical shifts found for sub-atoms either." );
			}
		}
		else
		{
			m_log.warn( "No chemical shift found for atom: " + address );
		}
		
		// always add the original address
		HashSet<AtomAddressReadable> relaxedAddresses = new HashSet<AtomAddressReadable>(); 
		relaxedAddresses.add( new AtomAddressReadable( address ) );
		
		// then add other addresses of nearby shifts 
		for( ChemicalShift<AtomAddressReadable> addressShift : addressShifts )
		{
			Set<AtomAddressReadable> addresses = getAddressesNearShift1D(
				hydrogenShifts.values(),
				addressShift.getValue(),
				hydrogenWindowWidth
			);
			addresses.remove( address );
			relaxedAddresses.addAll( changeToSubunit( address.getSubunitName(), addresses ) );
		}
		return relaxedAddresses;
	}
	
	private static Set<AtomAddressReadable> getAddressesNearAddress2D( Sequence sequence, Iterable<AtomAddressReadable> addresses, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, Map<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> heavyPairs, double hydrogenWindowWidth, double heavyWindowWidth, boolean shiftsHaveSubunitAssignments )
	{
		HashSet<AtomAddressReadable> relaxedAddresses = new HashSet<AtomAddressReadable>();
		for( AtomAddressReadable address : addresses )
		{
			relaxedAddresses.addAll( getAddressesNearAddress2D( sequence, address, hydrogenShifts, heavyPairs, hydrogenWindowWidth, heavyWindowWidth, shiftsHaveSubunitAssignments ) );
		}
		return relaxedAddresses;
	}
	
	private static Set<AtomAddressReadable> getAddressesNearAddress2D( Sequence sequence, AtomAddressReadable address, Map<AtomAddressReadable,ChemicalShift<AtomAddressReadable>> hydrogenShifts, Map<AtomAddressReadable,ChemicalShiftPair<AtomAddressReadable>> heavyPairs, double hydrogenWindowWidth, double heavyWindowWidth, boolean shiftsHaveSubunitAssignments )
	{
		// if the shifts don't have subunit assignments, remove the subunit assignment of the address
		AtomAddressReadable addressCopy = new AtomAddressReadable( address );
		if( !shiftsHaveSubunitAssignments )
		{
			addressCopy.omitSubunitName();
		}
		
		// find all the chemical shifts related to this address (pseudoatoms make this complicated)
		List<ChemicalShiftPair<AtomAddressReadable>> addressPairs = new ArrayList<ChemicalShiftPair<AtomAddressReadable>>();
		
		// try looking for a shift for the address directly
		ChemicalShiftPair<AtomAddressReadable> pair = heavyPairs.get( addressCopy );
		if( pair != null )
		{
			addressPairs.add( pair );
		}
		// otherwise, search the sub-atoms of the pseudoatom for shifts
		else if( Element.getByAtomName( addressCopy.getAtomName() ).isPseudoatom() )
		{
			for( AtomAddressReadable subAddress : getSubAddresses( sequence, addressCopy ) )
			{
				pair = heavyPairs.get( subAddress );
				if( pair != null )
				{
					addressPairs.add( pair );
				}
			}
			if( addressPairs.isEmpty() )
			{
				m_log.warn( "No chemical shift pair found for atom: " + address + " and no chemical shift pairs found for sub-atoms either." );
			}
		}
		else
		{
			m_log.warn( "No chemical shift pair found for atom: " + address );
		}
		
		// get all the addresses of nearby shifts 
		HashSet<AtomAddressReadable> relaxedAddresses = new HashSet<AtomAddressReadable>();
		for( ChemicalShiftPair<AtomAddressReadable> addressPair : addressPairs )
		{
			Set<AtomAddressReadable> addresses = getAddressesNearShift2D(
				heavyPairs.values(),
				addressPair.getHydrogenShift().getValue(),
				addressPair.getHeavyShift().getValue(),
				hydrogenWindowWidth,
				heavyWindowWidth
			);
			relaxedAddresses.addAll( changeToSubunit( address.getSubunitName(), addresses ) );
		}
		return relaxedAddresses;
	}
	
	private static boolean isNearby1D( ChemicalShift<AtomAddressReadable> shift, double value, double windowWidth )
	{
		return Math.abs( value - shift.getValue() ) <= windowWidth/2;
	}
	
	private static boolean isNearby2D( ChemicalShiftPair<AtomAddressReadable> pair, double hydrogenValue, double heavyValue, double hydrogenWindowWidth, double heavyWindowWidth )
	{
		return isNearby1D( pair.getHydrogenShift(), hydrogenValue, hydrogenWindowWidth )
			&& isNearby1D( pair.getHeavyShift(), heavyValue, heavyWindowWidth );
	}
	
	private static ArrayList<AtomAddressReadable> getSubAddresses( Sequence sequence, AtomAddressReadable address )
	{
		// find which sub-atoms are represented by this pseudoatom
		AminoAcid aminoAcid = sequence.getAminoAcidByNumber( address.getResidueNumber() );
		List<String> subatomNames = PseudoatomBuilder.getPseudoatoms().getAtoms( aminoAcid, address.getAtomName() );
		
		/* HACKHACK:
			For valine and leucine, we could have combined methyls.
			So, look at the methyl pseudoatoms instead of the individual protons.
		*/
		if( aminoAcid == AminoAcid.Leucine && address.getAtomName().equalsIgnoreCase( "QD" ) )
		{
			subatomNames = Arrays.asList( "md1", "md2" );
		}
		else if( aminoAcid == AminoAcid.Valine && address.getAtomName().equalsIgnoreCase( "QG" ) )
		{
			subatomNames = Arrays.asList( "mg1", "mg2" );
		}
		
		// convert to an address list
		ArrayList<AtomAddressReadable> subAddresses = new ArrayList<AtomAddressReadable>();
		for( String name : subatomNames )
		{
			subAddresses.add( new AtomAddressReadable(
				address.getSubunitName(),
				address.getResidueNumber(),
				name
			) );
		}
		return subAddresses;
	}
	
	private static List<AtomAddressReadable> changeToSubunit( char subunitName, Iterable<AtomAddressReadable> addresses )
	{
		List<AtomAddressReadable> changedAddresses = new ArrayList<AtomAddressReadable>();
		for( AtomAddressReadable address : addresses )
		{
			changedAddresses.add( changeToSubunit( subunitName, address ) );
		}
		return changedAddresses;
	}
	
	private static AtomAddressReadable changeToSubunit( char subunitName, AtomAddressReadable address )
	{
		AtomAddressReadable changedAddress = new AtomAddressReadable( address );
		changedAddress.setSubunitName( subunitName );
		return changedAddress;
	}
	
	private static Element getHeavyElement( Sequence sequence, AtomAddressReadable hydrogenAddress )
	{
		String heavyAtomName = BondGraphBuilder.getInstance().getHeavyAtomName(
			hydrogenAddress.getAtomName(),
			sequence.getAminoAcidByNumber( hydrogenAddress.getResidueNumber() ),
			sequence.getResidueTypeByNumber( hydrogenAddress.getResidueNumber() )
		);
		return Element.getByAtomName( heavyAtomName );
	}
	
	private static void restorePseudoatoms( Sequence sequence, DistanceRestraint<AtomAddressReadable> restraint, DistanceRestraint<AtomAddressReadable> reassignedRestraint )
	{
		restorePseudoatom( sequence, restraint.getLefts(), reassignedRestraint.getLefts() );
		restorePseudoatom( sequence, restraint.getRights(), reassignedRestraint.getRights() );
	}
	
	private static void restorePseudoatom( Sequence sequence, Set<AtomAddressReadable> addresses, Set<AtomAddressReadable> reassignedAddresses )
	{
		for( AtomAddressReadable pseudoatomAddress : addresses )
		{
			// skip the non-pseudoatom addresses
			if( !Element.getByAtomName( pseudoatomAddress.getAtomName() ).isPseudoatom() )
			{
				continue;
			}
			
			// get the sub-addresses for this pseudoatom
			AminoAcid aminoAcid = sequence.getAminoAcidByNumber( pseudoatomAddress.getResidueNumber() );
			List<String> subatomNames = PseudoatomBuilder.getPseudoatoms().getAtoms( aminoAcid, pseudoatomAddress.getAtomName() );
			
			// HACKHACK: combined methyls need to be handled specially
			if( aminoAcid == AminoAcid.Leucine && pseudoatomAddress.getAtomName().equalsIgnoreCase( "QD" ) )
			{
				subatomNames = Transformer.toArrayList( "md1", "md2" );
			}
			else if( aminoAcid == AminoAcid.Valine && pseudoatomAddress.getAtomName().equalsIgnoreCase( "QG" ) )
			{
				subatomNames = Transformer.toArrayList( "mg1", "mg2" );
			}
			
			// find all the sub-atoms for this pseudoatom
			ArrayList<AtomAddressReadable> foundAddresses = new ArrayList<AtomAddressReadable>();
			for( AtomAddressReadable address : reassignedAddresses )
			{
				// skip atoms not in this residue
				if( address.getResidueNumber() != pseudoatomAddress.getResidueNumber() )
				{
					continue;
				}
				
				for( String subatomName : subatomNames )
				{
					if( subatomName.equalsIgnoreCase( address.getAtomName() ) )
					{
						foundAddresses.add( address );
						break;
					}
				}
			}
			
			// did we find them all?
			if( foundAddresses.size() == subatomNames.size() )
			{
				// restore the pseudoatom
				reassignedAddresses.removeAll( foundAddresses );
				reassignedAddresses.add( changeToSubunit( foundAddresses.get( 0 ).getSubunitName(), pseudoatomAddress ) );
			}
		}
	}
}
