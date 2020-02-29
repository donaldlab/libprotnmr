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
package edu.duke.cs.libprotnmr.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.pseudoatoms.Pseudoatoms;


public class AddressMapper
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final boolean DefaultExpandPseudoatoms = false;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Integer mapSubunitNameToId( Protein protein, char subunitName )
	{
		return protein.getSubunitId( subunitName );
	}
	
	public static Character mapSubunitIdToName( Protein protein, int subunitId )
	{
		Subunit subunit = protein.getSubunits().get( subunitId );
		if( subunit == null )
		{
			return null;
		}
		return subunit.getName();
	}
	
	public static Integer mapResidueNumberToId( Protein protein, int subunitId, int residueNumber )
	{
		Subunit subunit = protein.getSubunit( subunitId );
		if( subunit == null )
		{
			return null;
		}
		return mapResidueNumberToId( subunit, residueNumber );
	}
	
	public static Integer mapResidueNumberToId( Subunit subunit, int residueNumber )
	{
		return subunit.getResidueId( residueNumber );
	}
	
	public static Integer mapResidueIdToNumber( Protein protein, int subunitId, int residueId )
	{
		Subunit subunit = protein.getSubunit( subunitId );
		if( subunit == null )
		{
			return null;
		}
		return mapResidueIdToNumber( subunit, residueId );
	}
	
	public static Integer mapResidueIdToNumber( Subunit subunit, int residueId )
	{
		Residue residue = subunit.getResidue( residueId );
		if( residue == null )
		{
			return null;
		}
		return residue.getNumber();
	}
	
	public static Integer mapAtomNameToId( Protein protein, int subunitId, int residueId, String atomName )
	{
		Subunit subunit = protein.getSubunit( subunitId );
		if( subunit == null )
		{
			return null;
		}
		return mapAtomNameToId( subunit, residueId, atomName );
	}
	
	public static Integer mapAtomNameToId( Subunit subunit, int residueId, String atomName )
	{
		// get the residue
		Residue residue = subunit.getResidue( residueId );
		if( residue == null )
		{
			return null;
		}
		
		// do a sequential search through the atoms of the residue to find the atom
		for( Atom atom : residue.getAtoms() )
		{
			if( atom.getName().equalsIgnoreCase( atomName ) )
			{
				return atom.getId();
			}
		}
		
		return null;
	}
	
	public static ArrayList<Integer> mapAmbiguousAtomNameToIds( Protein protein, int subunitId, int residueId, String atomName )
	{
		return mapAmbiguousAtomNameToIds( protein.getSubunit( subunitId ), residueId, atomName );
	}
	
	public static ArrayList<Integer> mapAmbiguousAtomNameToIds( Subunit subunit, int residueId, String atomName )
	{
		// get the residue
		if( residueId < 0 || residueId >= subunit.getResidues().size() )
		{
			return null;
		}
		Residue residue = subunit.getResidues().get( residueId );
		
		String pattern = "^" + atomName.replaceAll( "[#\\*]", "\\\\d+" ) + "$";
		Pattern regex = Pattern.compile( pattern, Pattern.CASE_INSENSITIVE );
		
		// do a sequential search through the atoms of the residue to find the matching atoms
		ArrayList<Integer> atomIds = new ArrayList<Integer>();
		for( Atom atom : residue.getAtoms() )
		{
			// transform the atom name into a regex
			if( regex.matcher( atom.getName() ).matches() )
			{
				atomIds.add( atom.getId() );
			}
		}
		
		return atomIds;
	}
	
	public static String mapAtomIdToName( Protein protein, AtomAddressInternal address )
	{
		Atom atom = protein.getAtom( address );
		if( atom == null )
		{
			return null;
		}
		return atom.getName();
	}
	
	public static String mapAtomIdToName( Protein protein, int subunitId, int residueId, int atomId )
	{
		Atom atom = protein.getAtom( subunitId, residueId, atomId );
		if( atom == null )
		{
			return null;
		}
		return atom.getName();
	}
	
	public static String mapAtomIdToName( Subunit subunit, AtomAddressInternal address )
	{
		Atom atom = subunit.getAtom( address );
		if( atom == null )
		{
			return null;
		}
		return atom.getName();
	}
	
	public static List<AtomAddressInternal> translateAddresses( Protein target, Protein source, List<AtomAddressInternal> addresses )
	{
		return translateAddresses( target, source, addresses, 0 );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Protein target, Protein source, List<AtomAddressInternal> addresses, int residueOffset )
	{
		List<AtomAddressReadable> readableAddresses = mapAddressesToReadable( source, addresses );
		shiftResidueNumbers( readableAddresses, residueOffset );
		return mapAddressesToInternal( target, readableAddresses );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Subunit target, Subunit source, List<AtomAddressInternal> addresses )
	{
		return translateAddresses( target, source, addresses, 0 );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Subunit target, Subunit source, List<AtomAddressInternal> addresses, int residueOffset )
	{
		List<AtomAddressReadable> readableAddresses = mapAddressesToReadable( source, addresses );
		shiftResidueNumbers( readableAddresses, residueOffset );
		return mapAddressesToInternal( target, readableAddresses );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Subunit target, Protein source, List<AtomAddressInternal> addresses )
	{
		return translateAddresses( target, source, addresses, 0 );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Subunit target, Protein source, List<AtomAddressInternal> addresses, int residueOffset )
	{
		List<AtomAddressReadable> readableAddresses = mapAddressesToReadable( source, addresses );
		shiftResidueNumbers( readableAddresses, residueOffset );
		return mapAddressesToInternal( target, readableAddresses );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Protein target, Subunit source, List<AtomAddressInternal> addresses )
	{
		return translateAddresses( target, source, addresses, 0 );
	}
	
	public static List<AtomAddressInternal> translateAddresses( Protein target, Subunit source, List<AtomAddressInternal> addresses, int residueOffset )
	{
		List<AtomAddressReadable> readableAddresses = mapAddressesToReadable( source, addresses );
		shiftResidueNumbers( readableAddresses, residueOffset );
		return mapAddressesToInternal( target, readableAddresses );
	}
	
	public static void shiftResidueNumbers( List<AtomAddressReadable> addresses, int offset )
	{
		for( AtomAddressReadable address : addresses )
		{
			address.setResidueNumber( address.getResidueNumber() + offset );
		}
	}
	
	public static List<AtomAddressInternal> mapAddressesToInternal( Protein protein, List<AtomAddressReadable> readableAddresses )
	{
		List<AtomAddressInternal> internalAddresses = new ArrayList<AtomAddressInternal>();
		for( AtomAddressReadable readableAddress : readableAddresses )
		{
			internalAddresses.add( mapAddress( protein, readableAddress ) );
		}
		return internalAddresses;
	}
	
	public static List<AtomAddressInternal> mapAddressesToInternal( Subunit subunit, List<AtomAddressReadable> readableAddresses )
	{
		List<AtomAddressInternal> internalAddresses = new ArrayList<AtomAddressInternal>();
		for( AtomAddressReadable readableAddress : readableAddresses )
		{
			internalAddresses.add( mapAddress( subunit, readableAddress ) );
		}
		return internalAddresses;
	}
	
	public static AtomAddressInternal mapAddress( Protein protein, AtomAddressReadable readableAddress )
	{
		Integer subunitId = mapSubunitNameToId( protein, readableAddress.getSubunitName() );
		if( subunitId == null )
		{
			return null;
		}
		return mapAddress( protein.getSubunit( subunitId ), readableAddress );
	}
	
	public static AtomAddressInternal mapAddress( Subunit subunit, AtomAddressReadable readableAddress )
	{
		// just in case...
		if( subunit == null )
		{
			return null;
		}
		
		Integer residueId = mapResidueNumberToId( subunit, readableAddress.getResidueNumber() );
		if( residueId == null )
		{
			return null;
		}
		
		// get the atom id
		Integer atomId = mapAtomNameToId( subunit, residueId, readableAddress.getAtomName() );
		if( atomId == null )
		{
			return null;
		}
		
		return new AtomAddressInternal( subunit.getId(), residueId, atomId );
	}
	
	public static ArrayList<AtomAddressInternal> mapAddressExpandPseudoatoms( HasAtoms protein, AtomAddressReadable readableAddress )
	{
		// HACKHACK: delegate to a more specific subroutine
		if( protein instanceof Protein )
		{
			return mapAddressExpandPseudoatoms( (Protein)protein, readableAddress );
		}
		else if( protein instanceof Subunit )
		{
			return mapAddressExpandPseudoatoms( (Subunit)protein, readableAddress );
		}
		
		throw new IllegalArgumentException( "function expects either a Protein or a Subunit instance!" );
	}
	
	public static ArrayList<AtomAddressInternal> mapAddressExpandPseudoatoms( Protein protein, AtomAddressReadable readableAddress )
	{
		if( readableAddress.hasSubunitName() )
		{
			Integer subunitId = mapSubunitNameToId( protein, readableAddress.getSubunitName() );
			if( subunitId == null )
			{
				return null;
			}
			return mapAddressExpandPseudoatoms( protein.getSubunit( subunitId ), readableAddress );
		}
		else
		{
			// HACKHACK: if the subunit in omitted, map the address to the first subunit
			return mapAddressExpandPseudoatoms( protein.getSubunit( 0 ), readableAddress );
		}
	}
	
	public static ArrayList<AtomAddressInternal> mapAddressExpandPseudoatoms( Subunit subunit, AtomAddressReadable readableAddress )
	{
		// just in case...
		if( subunit == null )
		{
			return new ArrayList<AtomAddressInternal>();
		}
		
		Integer residueId = mapResidueNumberToId( subunit, readableAddress.getResidueNumber() );
		if( residueId == null )
		{
			return new ArrayList<AtomAddressInternal>();
		}
		
		// get the atom IDs
		ArrayList<Integer> atomIds = mapAmbiguousAtomNameToIds( subunit, residueId, readableAddress.getAtomName() );
		if( atomIds == null )
		{
			return new ArrayList<AtomAddressInternal>();
		}
		
		// build the list of addresses
		ArrayList<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>();
		for( Integer atomId : atomIds )
		{
			addresses.add( new AtomAddressInternal( subunit.getId(), residueId, atomId ) );
		}
		
		return addresses;
	}
	
	public static AtomAddressInternal mapAddressToResidue( Protein protein, AtomAddressReadable readableAddress )
	{
		Integer subunitId = mapSubunitNameToId( protein, readableAddress.getSubunitName() );
		if( subunitId == null )
		{
			return null;
		}
		Integer residueId = mapResidueNumberToId( protein, subunitId, readableAddress.getResidueNumber() );
		if( residueId == null )
		{
			return null;
		}
		
		return new AtomAddressInternal( subunitId, residueId, -1 );
	}
	
	public static List<AtomAddressReadable> mapAddressesToReadable( Protein protein, List<AtomAddressInternal> internalAddresses )
	{
		List<AtomAddressReadable> readableAddresses = new ArrayList<AtomAddressReadable>();
		for( AtomAddressInternal internalAddress : internalAddresses )
		{
			readableAddresses.add( mapAddress( protein, internalAddress ) );
		}
		return readableAddresses;
	}
	
	public static List<AtomAddressReadable> mapAddressesToReadable( Subunit subunit, List<AtomAddressInternal> internalAddresses )
	{
		List<AtomAddressReadable> readableAddresses = new ArrayList<AtomAddressReadable>();
		for( AtomAddressInternal internalAddress : internalAddresses )
		{
			readableAddresses.add( mapAddress( subunit, internalAddress ) );
		}
		return readableAddresses;
	}
	
	public static AtomAddressReadable mapAddress( Protein protein, AtomAddressInternal address )
	{
		return new AtomAddressReadable(
			mapSubunitIdToName( protein, address.getSubunitId() ),
			mapResidueIdToNumber( protein, address.getSubunitId(), address.getResidueId() ),
			mapAtomIdToName( protein, address )
		);
	}
	
	public static AtomAddressReadable mapAddress( Subunit subunit, AtomAddressInternal address )
	{
		return new AtomAddressReadable(
			subunit.getName(),
			mapResidueIdToNumber( subunit, address.getResidueId() ),
			mapAtomIdToName( subunit, address )
		);
	}
	
	public static TreeSet<AtomAddressInternal> mapReadableAtomAddresses( Set<AtomAddressReadable> ins, Protein protein )
	{
		return mapReadableAtomAddresses( ins, protein, DefaultExpandPseudoatoms );
	}
	
	public static TreeSet<AtomAddressInternal> mapReadableAtomAddresses( Set<AtomAddressReadable> ins, Protein protein, boolean expandPseudoatoms )
	{
		TreeSet<AtomAddressInternal> outs = new TreeSet<AtomAddressInternal>();
		for( AtomAddressReadable in : ins )
		{
			if( expandPseudoatoms )
			{
				outs.addAll( mapAddressExpandPseudoatoms( protein, in ) );
			}
			else
			{
				AtomAddressInternal address = mapAddress( protein, in );
				if( address != null )
				{
					outs.add( address );
				}
			}
		}
		return outs;
	}
	
	public static TreeSet<AtomAddressReadable> mapInternalAtomAddresses( Set<AtomAddressInternal> ins, Protein protein )
	{
		TreeSet<AtomAddressReadable> outs = new TreeSet<AtomAddressReadable>();
		for( AtomAddressInternal in : ins )
		{
			outs.add( mapAddress( protein, in ) );
		}
		return outs;
	}
	
	public static void mapPseudoaomNamesToMasks( Protein protein, List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			mapPseudoaomNamesToMasks( protein, restraint );
		}
	}
	
	public static void mapPseudoaomNamesToMasks( Protein protein, DistanceRestraint<AtomAddressReadable> restraint )
	{
		mapPseudoaomNamesToMasks( protein, restraint.getLefts() );
		mapPseudoaomNamesToMasks( protein, restraint.getRights() );
	}
	
	public static void mapPseudoaomNamesToMasks( Protein protein, Set<AtomAddressReadable> addresses )
	{
		Pseudoatoms pseudoatoms = PseudoatomBuilder.getPseudoatoms();		
		
		for( AtomAddressReadable address : addresses )
		{
			// get the amino acid
			AminoAcid aminoAcid = protein.getResidue( mapAddressToResidue( protein, address ) ).getAminoAcid();
			
			// convert the names
			for( String pseudoatomName : pseudoatoms.getPseudoatomNames( aminoAcid ) )
			{
				if( address.getAtomName().equalsIgnoreCase( pseudoatomName ) )
				{
					String mask = pseudoatoms.getMask( aminoAcid, pseudoatomName );
					if( mask != null )
					{
						address.setAtomName( mask );
					}
				}
			}
		}
	}
	
	public static boolean areAtomsSameAcrossEnsemble( Collection<AtomAddressInternal> addresses, List<Protein> ensemble )
	{
		// make sure each address maps to the same atom across the ensemble
		for( AtomAddressInternal address : addresses )
		{
			if( !isAtomSameAcrossEnsemble( address, ensemble ) )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isAtomSameAcrossEnsemble( AtomAddressInternal address, List<Protein> ensemble )
	{
		AtomAddressReadable referenceAddress = mapAddress( ensemble.get( 0 ), address );
		for( int i=1; i<ensemble.size(); i++ )
		{
			AtomAddressReadable compareAddress = mapAddress( ensemble.get( i ), address );
			if( !referenceAddress.equals( compareAddress ) )
			{
				return false;
			}
		}
		return true;
	}
}
