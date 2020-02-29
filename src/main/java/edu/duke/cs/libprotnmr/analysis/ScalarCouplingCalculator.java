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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Quadratic;
import edu.duke.cs.libprotnmr.nmr.DihedralRestraint;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;
import edu.duke.cs.libprotnmr.util.CircularList;


public class ScalarCouplingCalculator
{
	// karplus coefficients taken from:
	// Determination of the Backbone Dihedral Angles phi in Human Ubiquitin from Reparametrized Empirical Karplus Equations
	// Andy C. Wang and Ad Bax (1995)
	// http://spin.niddk.nih.gov/bax/lit/508/229.pdf
	
	/**************************
	 *   Definitions
	 **************************/
	
	public static enum Type
	{
		HnHa( Math.toRadians( -60.0 ), 6.98, -1.38, 1.72 ),
		HaC( Math.toRadians( -60.0 ), 3.75, 2.19, 1.28 ),
		HnCb( Math.toRadians( 60.0 ), 3.39, -0.94, 0.07 ),
		HnC( Math.toRadians( 0.0 ), 4.32, 0.84, 0.0 );
		
		private double m_phase;
		private double m_A;
		private double m_B;
		private double m_C;
		
		private Type( double phase, double A, double B, double C )
		{
			m_phase = phase;
			m_A = A;
			m_B = B;
			m_C = C;
		}
		
		public double getPhase( )
		{
			return m_phase;
		}
		
		public double getA( )
		{
			return m_A;
		}
		
		public double getB( )
		{
			return m_B;
		}
		
		public double getC( )
		{
			return m_C;
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private Type m_type;

	
	/**************************
	 *   Constructors
	 **************************/
	
	public ScalarCouplingCalculator( Type type )
	{
		m_type = type;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public Type getType( )
	{
		return m_type;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public double getSymmetricAngle( double angle )
	{
		return CircleRange.mapZeroToTwoPi( -angle - 2*m_type.getPhase() );
	}
	
	public List<CircleRange> getDihedralIntervals( DihedralRestraint<AtomAddressInternal> restraint )
	{
		// get all the interval boundaries
		List<Double> boundaries = new ArrayList<Double>();
		boundaries.addAll( getAngles( restraint.getValue() - restraint.getError() ) );
		boundaries.addAll( getAngles( restraint.getValue() + restraint.getError() ) );
		
		// sort them 0 to 2Pi
		Collections.sort( boundaries );
		
		// check each possible interval
		List<CircleRange> intervals = new ArrayList<CircleRange>();
		for( int i=0; i<boundaries.size(); i++ )
		{
			// build the interval
			CircleRange interval = CircleRange.newByCounterclockwiseSegment(
				boundaries.get( i ),
				CircularList.getNext( boundaries, i )
			);
			
			// if any point on this interval has a value in the range, the interval is good
			if( isSatisfied( restraint, getHz( interval.getMidpoint() ) ) )
			{
				intervals.add( interval );
			}
		}
		return intervals;
	}
	
	public double getViolationHz( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return getViolationHz( restraint, getHz( getAngle( restraint, structure ) ) );
	}
	
	public double getViolationHz( DihedralRestraint<AtomAddressInternal> restraint, double hz )
	{
		double min = restraint.getValue() - restraint.getError();
		double max = restraint.getValue() + restraint.getError();
		if( !CompareReal.gte( hz, min ) )
		{
			return min - hz;
		}
		else if( !CompareReal.lte( hz, max ) )
		{
			return hz - max;
		}
		return 0.0;
	}
	
	public double getViolationRadians( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return getViolationRadians( restraint, getAngle( restraint, structure ) );
	}
	
	private double getViolationRadians( DihedralRestraint<AtomAddressInternal> restraint, double dihedral )
	{
		// get the min violation of all the intervals
		double minViolation = Double.POSITIVE_INFINITY;
		for( CircleRange interval : getDihedralIntervals( restraint ) )
		{
			double violation = interval.getDistance( dihedral );
			minViolation = Math.min( minViolation, violation );
		}
		return minViolation;
	}
	
	public boolean isSatisfied( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return getViolationHz( restraint, structure ) <= 0;
	}
	
	public boolean isSatisfied( DihedralRestraint<AtomAddressInternal> restraint, double hz )
	{
		return getViolationHz( restraint, hz ) <= 0;
	}
	
	public Map<DihedralRestraint<AtomAddressInternal>,Double> getViolationsHz( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		return getViolationsHz( protein, restraints, 0 );
	}
	
	public Map<DihedralRestraint<AtomAddressInternal>,Double> getViolationsHz( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		Map<DihedralRestraint<AtomAddressInternal>,Double> violations = new HashMap<DihedralRestraint<AtomAddressInternal>,Double>();
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			// if the violation passes our threshold, add it
			double violation = getViolationHz( restraint, protein );
			if( violation > allowedViolation )
			{
				violations.put( restraint, violation );
			}
		}
		return violations;
	}
	
	public Map<DihedralRestraint<AtomAddressInternal>,Double> getViolationsRadians( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		return getViolationsRadians( protein, restraints, 0 );
	}
	
	public Map<DihedralRestraint<AtomAddressInternal>,Double> getViolationsRadians( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		Map<DihedralRestraint<AtomAddressInternal>,Double> violations = new HashMap<DihedralRestraint<AtomAddressInternal>,Double>();
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			// if the violation passes our threshold, add it
			double violation = getViolationRadians( restraint, protein );
			if( violation > allowedViolation )
			{
				violations.put( restraint, violation );
			}
		}
		return violations;
	}
	
	public Double getRmsdHz( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		double totalDistOverSq = 0.0;
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			double violation = getViolationHz( restraint, protein );
			totalDistOverSq += violation * violation;
		}
		return Math.sqrt( totalDistOverSq/restraints.size() ); 
	}
	
	public Double getRmsdRadians( Protein protein, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		double totalDistOverSq = 0.0;
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			double violation = getViolationRadians( restraint, protein );
			totalDistOverSq += violation * violation;
		}
		return Math.sqrt( totalDistOverSq/restraints.size() ); 
	}
	
	public double getAngle( DihedralRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		return ProteinGeometry.getDihedralAngle(
			structure.getAtom( restraint.getA() ).getPosition(),
			structure.getAtom( restraint.getB() ).getPosition(),
			structure.getAtom( restraint.getC() ).getPosition(),
			structure.getAtom( restraint.getD() ).getPosition()
		);
	}
	
	public double getHz( double angle )
	{
		double cos = Math.cos( angle + m_type.getPhase() );
		return m_type.getA()*cos*cos + m_type.getB()*cos + m_type.getC();
	}
	
	public List<Double> getAngles( double hz )
	{
		// solve the Karplus equation for the angles
		List<Double> angles = new ArrayList<Double>();
		for( double root : Quadratic.solve( m_type.getA(), m_type.getB(), m_type.getC() - hz ) )
		{
			// skip roots outside the range of acos
			if( root < -1.0 || root > 1.0 )
			{
				continue;
			}
			
			double baseAngle = CircleRange.mapZeroToTwoPi( Math.acos( root ) - m_type.getPhase() );
			for( double angle : Arrays.asList( baseAngle, getSymmetricAngle( baseAngle ) ) )
			{
				assert( CompareReal.eq( hz, getHz( angle ) ) );
				angles.add( angle );
			}
		}
		return angles;
	}
}
