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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.nmr.DihedralRestraint;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;


public class DihedralRestraintCalculator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getAngle( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return ProteinGeometry.getDihedralAngle(
			structure.getAtom( restraint.getA() ).getPosition(),
			structure.getAtom( restraint.getB() ).getPosition(),
			structure.getAtom( restraint.getC() ).getPosition(),
			structure.getAtom( restraint.getD() ).getPosition()
		);
	}
	
	public static CircleRange getDihedralInterval( DihedralRestraint<AtomAddressInternal> restraint )
	{
		return CircleRange.newByOffset(
			restraint.getValue() - restraint.getError(),
			restraint.getError()*2
		);
	}
	
	public static int getNumSatisfied( List<DihedralRestraint<AtomAddressInternal>> restraints, HasAtoms structure )
	{
		int count = 0;
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			if( isSatisfied( restraint, structure ) )
			{
				count++;
			}
		}
		return count;
	}
	
	public static double getViolation( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return getDihedralInterval( restraint ).getDistance( getAngle( restraint, structure ) );
	}
	
	public static boolean isSatisfied( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return CompareReal.lte( getViolation( restraint, structure ), 0 );
	}
	
	public static Map<DihedralRestraint<AtomAddressInternal>,Double> getViolations( List<DihedralRestraint<AtomAddressInternal>> restraints, HasAtoms structure )
	{
		return getViolations( restraints, structure, 0 );
	}
	
	public static Map<DihedralRestraint<AtomAddressInternal>,Double> getViolations( List<DihedralRestraint<AtomAddressInternal>> restraints, HasAtoms structure, double allowedViolation )
	{
		Map<DihedralRestraint<AtomAddressInternal>,Double> violations = new HashMap<DihedralRestraint<AtomAddressInternal>,Double>();
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			double violation = getViolation( restraint, structure );
			if( violation > allowedViolation )
			{
				violations.put( restraint, violation );
			}
		}
		return violations;
	}
	
	public static double getRmsd( List<DihedralRestraint<AtomAddressInternal>> restraints, HasAtoms structure )
	{
		double totalViolationSq = 0.0;
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			double violation = getViolation( restraint, structure );
			totalViolationSq += violation*violation;
		}
		return Math.sqrt( totalViolationSq/restraints.size() );
	}
}
