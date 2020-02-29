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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class DistanceRestraintFilterer
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void sortSides( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			if( restraint.isSubunitAmbiguous() )
			{
				continue;
			}
			
			// normalize NOE so addresses are sorted
			AtomAddressInternal left = restraint.getLefts().iterator().next();
			AtomAddressInternal right = restraint.getRights().iterator().next();
			if( left.compareTo( right ) > 0 )
			{
				restraint.swap();
			}
		}
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickIntersubunit( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// NOTE: make sure we don't disturb the ordering of the restraints
		
		LinkedHashSet<DistanceRestraint<AtomAddressInternal>> filteredRestraints = new LinkedHashSet<DistanceRestraint<AtomAddressInternal>>();
		
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// add subunit ambiguous restraints
			if( restraint.isSubunitAmbiguous() )
			{
				filteredRestraints.add( restraint );
				continue;
			}
			
			// add intersubunit restraints
			boolean isIntersubunit =
				restraint.getLefts().iterator().next().getSubunitId()
				!= restraint.getRights().iterator().next().getSubunitId();
			if( isIntersubunit )
			{
				filteredRestraints.add( restraint );
			}
		}
		
		return new ArrayList<DistanceRestraint<AtomAddressInternal>>( filteredRestraints );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickIntrasubunit( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// NOTE: make sure we don't disturb the ordering of the restraints
		
		LinkedHashSet<DistanceRestraint<AtomAddressInternal>> filteredRestraints = new LinkedHashSet<DistanceRestraint<AtomAddressInternal>>();
		
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// skip subunit ambiguous restraints
			if( restraint.isSubunitAmbiguous() )
			{
				continue;
			}
			
			// add intrasubunit restraints
			boolean isIntrasubunit =
				restraint.getLefts().iterator().next().getSubunitId()
				== restraint.getRights().iterator().next().getSubunitId();
			if( isIntrasubunit )
			{
				filteredRestraints.add( restraint );
			}
		}
		
		return new ArrayList<DistanceRestraint<AtomAddressInternal>>( filteredRestraints );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickUnique( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// NOTE: make sure we don't disturb the ordering of the restraints
		LinkedHashSet<DistanceRestraint<AtomAddressInternal>> filteredRestraints = new LinkedHashSet<DistanceRestraint<AtomAddressInternal>>();
		filteredRestraints.addAll( restraints );
		return new ArrayList<DistanceRestraint<AtomAddressInternal>>( filteredRestraints );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickBetween( List<DistanceRestraint<AtomAddressInternal>> restraints, int leftSubunitId, int rightSubunitId )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> newRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{	
			boolean isLeft = isSubunitInSet( leftSubunitId, restraint.getLefts() );
			boolean isRight = isSubunitInSet( rightSubunitId, restraint.getRights() );
			if( isLeft && isRight )
			{
				newRestraints.add( restraint );
			}
		}
		return newRestraints;
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickBetweenEitherSide( List<DistanceRestraint<AtomAddressInternal>> restraints, int leftSubunitId, int rightSubunitId )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> newRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{	
			boolean isLeftInLeft = isSubunitInSet( leftSubunitId, restraint.getLefts() );
			boolean isLeftInRight = isSubunitInSet( leftSubunitId, restraint.getRights() );
			boolean isRightInLeft = isSubunitInSet( rightSubunitId, restraint.getLefts() );
			boolean isRightInRight = isSubunitInSet( rightSubunitId, restraint.getRights() );
			if( ( isLeftInLeft && isRightInRight ) || ( isLeftInRight && isRightInLeft ) )
			{
				newRestraints.add( restraint );
			}
		}
		return newRestraints;
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickSubunitEitherSide( List<DistanceRestraint<AtomAddressInternal>> restraints, int subunitId )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> newRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{	
			boolean isInLeft = isSubunitInSet( subunitId, restraint.getLefts() );
			boolean isInRight = isSubunitInSet( subunitId, restraint.getRights() );
			if( isInLeft || isInRight )
			{
				newRestraints.add( restraint );
			}
		}
		return newRestraints;
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickOneFromSymmetricGroup( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// NOTE: make sure we don't change the ordering of the distance restraints
		LinkedHashMap<DistanceRestraintResidueAtom,DistanceRestraint<AtomAddressInternal>> map = new LinkedHashMap<DistanceRestraintResidueAtom,DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			DistanceRestraintResidueAtom key = new DistanceRestraintResidueAtom( restraint );
			if( !map.containsKey( key ) )
			{
				map.put( key, restraint );
			}
		}
		return new ArrayList<DistanceRestraint<AtomAddressInternal>>( map.values() );
	}
	
	public static ArrayList<DistanceRestraint<AtomAddressInternal>> pickMismatchedRestraints( List<DistanceRestraint<AtomAddressInternal>> restraints, int numSubunits )
	{
		// This function is mostly for debugging
		HashMap<DistanceRestraintResidueAtom,Integer> counts = new HashMap<DistanceRestraintResidueAtom,Integer>();
		
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			DistanceRestraintResidueAtom wrapper = new DistanceRestraintResidueAtom( restraint );
			Integer count = counts.get( wrapper );
			if( count == null )
			{
				count = new Integer( 0 );
			}
			count++;
			counts.put( wrapper, count );
		}
		
		ArrayList<DistanceRestraint<AtomAddressInternal>> mismatchedRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraintResidueAtom wrapper : counts.keySet() )
		{
			Integer count = counts.get( wrapper );
			if( count != numSubunits )
			{
				mismatchedRestraints.add( wrapper );
			}
		}
		
		return mismatchedRestraints;
	}
	
	public static void mapToSubunit( List<DistanceRestraint<AtomAddressInternal>> restraints, int subunitId )
	{
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			AtomAddressInternal.mapAddressesToSubunit( restraint.getLefts(), subunitId );
			AtomAddressInternal.mapAddressesToSubunit( restraint.getRights(), subunitId );
		}
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static boolean isSubunitInSet( int subunitId, Set<AtomAddressInternal> addresses )
	{
		for( AtomAddressInternal address : addresses )
		{
			if( subunitId == address.getSubunitId() )
			{
				return true;
			}
		}
		return false;
	}
}
