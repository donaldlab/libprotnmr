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

package edu.duke.cs.libprotnmr.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.nmr.Assignment;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;


public class DistanceRestraintCalculator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static int getNumSatisfied( Protein protein, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		int count = 0;
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			if( DistanceRestraint.isSatisfied( restraint, protein ) )
			{
				count++;
			}
		}
		return count;
	}
	
	public static double getRmsd( Protein protein, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		double totalDistOverSq = 0.0;
		int count = 0;
		
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			double minViolation = DistanceRestraint.getMinViolation( restraint, protein );
			
			// update the rmsd
			totalDistOverSq += minViolation * minViolation;
			count++;
		}
		
		return Math.sqrt( totalDistOverSq / (double)count ); 
	}
	
	public static Map<DistanceRestraint<AtomAddressInternal>,Double> getViolations( Protein protein, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		return getViolations( protein, restraints, 0.0 );
	}
	
	public static Map<DistanceRestraint<AtomAddressInternal>,Double> getViolations( Protein protein, List<DistanceRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		Map<DistanceRestraint<AtomAddressInternal>,Double> violations = new HashMap<DistanceRestraint<AtomAddressInternal>,Double>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// if the violation passes our threshold, add it
			double minViolation = DistanceRestraint.getMinViolation( restraint, protein );
			if( minViolation > allowedViolation )
			{
				violations.put( restraint, minViolation );
			}
		}
		return violations;
	}
	
	public static double getViolation( Protein protein, DistanceRestraint<AtomAddressInternal> restraint, AssignmentFilter<AtomAddressInternal> filter )
	{
		double minViolation = Double.POSITIVE_INFINITY;
		for( Assignment<AtomAddressInternal> assignment : restraint )
		{
			// check the assignment filter if needed
			if( filter != null && filter.filter( restraint, assignment ) == AssignmentFilter.Result.Block )
			{
				continue;
			}
			
			double violation = restraint.getViolation( assignment, protein );
			if( violation < minViolation )
			{
				minViolation = violation;
			}
		}
		assert( minViolation < Double.POSITIVE_INFINITY );
		return minViolation;
	}
	
	public static List<DistanceRestraint<AtomAddressInternal>> getSimulatedIntersubunitRestraints( int leftSubunitId, int rightSubunitId, Protein protein, double maxDistance, double tolerance )
	{
		return getSimulatedRestraints(
			protein,
			protein,
			getHydrogenAddresses( protein.getSubunit( leftSubunitId ) ),
			getHydrogenAddresses( protein.getSubunit( rightSubunitId ) ),
			maxDistance,
			tolerance
		);
	}
	
	public static List<DistanceRestraint<AtomAddressInternal>> getSimulatedRestraints( HasAtoms leftStructure, HasAtoms rightStructure, List<AtomAddressInternal> leftAddresses, List<AtomAddressInternal> rightAddresses, double maxDistance, double tolerance )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> restraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		double thresholdDistanceSq = maxDistance * maxDistance;
		
		// for every pair of atoms, create a restraint if their distance is less than some distance
		// this is a simple brute force implementation. Proteins aren't that big
		for( AtomAddressInternal leftAddress : leftAddresses )
		{
			Atom leftAtom = leftStructure.getAtom( leftAddress );
			for( AtomAddressInternal rightAddress : rightAddresses )
			{
				Atom rightAtom = rightStructure.getAtom( rightAddress );
				
				// make sure we have a pair of distinct atoms
				if( leftAddress.equals( rightAddress ) )
				{
					continue;
				}
				
				// if the atoms are close enough
				double distSq = rightAtom.getPosition().getSquaredDistance( leftAtom.getPosition() );
				if( distSq < thresholdDistanceSq )
				{
					double dist = Math.sqrt( distSq );
					
					// add the distance restraint
					DistanceRestraint<AtomAddressInternal> restraint = new DistanceRestraint<AtomAddressInternal>();
					restraint.setLefts( leftAddress );
					restraint.setRights( rightAddress );
					restraint.setMinDistance( Math.max( 0.0, dist - tolerance ) );
					restraint.setMaxDistance( dist + tolerance );
					restraints.add( restraint );
				}
			}
		}
		
		return restraints;
	}
	
	public static double getXplorNoePotential( HasAtoms structure, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		return getXplorNoePotential( structure, restraints, 6.0 );
	}
	
	public static double getXplorNoePotential( HasAtoms structure, List<DistanceRestraint<AtomAddressInternal>> restraints, double aveExp )
	{
		// from the Xplor python documentation
		// http://nmr.cit.nih.gov/xplor-nih/doc/current/python/ref/noePot.html
		// E = scale * delta^hardExp
		//         /
        //        | d - dMinus - r             (if r < d - dMinus)
        //delta = | d - (r + dPlus - dOffset)  (if r > d + dPlus - dOffset)
        //        | 0                          (otherwise)
        //         \
		// r = [ <sum_pairs sum_ij | q_i - q_j |^(-aveExp) / nMono> ] ^(-1/aveExp)
		
		// variables:
		// nMono      - number of monomers [1]
		// scale      - scale factor [1]
		// aveExp     - exponential to use in sum averaging [6]
		// dOffset    - potential offset [0]
		// hardExp    - exponential in hard region of potential [2]
		// d          - restraint distance
		// dMinus     - restraint distance minus
		// dPlus      - restraint distance plus
		
		// except d - (r + dPlus - dOffset) doesn't make any sense!
		// I'm going to assume they meant this instead:
		// r - (d + dPlus - dOffset)
		
		// sadly, distance restraints don't store d, dMinus, dPlus
		// but we can make these substitutions
		// dMin = d - dMinus
		// dMax = d + dPlus
		
		// also, we can simplify all the 0 and 1 constants
		// nMono = 1, scale = 1, dOffset = 0
		
		// so the equations change to this:
		// E = delta^hardExp
		//         /
        //        | dMin - r    (if r < dMin)
        //delta = | r - dMax    (if r > dMax)
        //        | 0           (otherwise)
        //         \
		// r = [ sum_i( dist_i^[-aveExp] ) ]^(-1/aveExp)
		// where dist_i is the distance between the two atoms indicated by the assignment i
		
		// constants
		final double hardExp = 2.0;
		
		double sum = 0.0;
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			double dMin = restraint.getMinDistance();
			double dMax = restraint.getMaxDistance();
			
			// get the distances
			List<Double> distances = new ArrayList<Double>();
			for( Assignment<AtomAddressInternal> assignment : restraint )
			{
				Vector3 posLeft = structure.getAtom( assignment.getLeft() ).getPosition();
				Vector3 posRight = structure.getAtom( assignment.getRight() ).getPosition();
				distances.add( posLeft.getDistance( posRight ) );
			}
			
			// compute r the "sum" way
			double sumForRestraint = 0.0;
			for( double distance : distances )
			{
				sumForRestraint += 1.0/Math.pow( distance, aveExp );
			}
			double r = 1.0/Math.pow( sumForRestraint, 1.0/aveExp );
			
			// compute r the min way
			// NOTE: This way makes more sense, but I'm guessing Xplor
			// can't do this because it's not a continuous potential...
			//double r = Collections.min( distances );
			
			double delta = 0.0;
			if( r < dMin )
			{
				delta = dMin - r;
			}
			else if( r > dMax )
			{
				delta = r - dMax;
			}
			
			double E = Math.pow( delta, hardExp );
			sum += E;
		}
		
		return sum;
	}
	
	private static List<AtomAddressInternal> getHydrogenAddresses( HasAtoms structure )
	{
		List<AtomAddressInternal> addresses = new ArrayList<AtomAddressInternal>();
		for( AtomAddressInternal address : structure.atoms() )
		{
			if( structure.getAtom( address ).getElement() == Element.Hydrogen )
			{
				addresses.add( address );
			}
		}
		return addresses;
	}
}
