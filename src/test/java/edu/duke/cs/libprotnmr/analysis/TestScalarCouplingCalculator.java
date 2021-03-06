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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.nmr.DihedralRestraint;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;


public class TestScalarCouplingCalculator extends ExtendedTestCase
{
	public void testSymmetricAngles( )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( ScalarCouplingCalculator.Type.HnHa );
		for( double angle : CircleRange.newCircle().samplePoints( 50 ) )
		{
			assertEquals( calc.getHz( angle ), calc.getHz( calc.getSymmetricAngle( angle ) ), CompareReal.getEpsilon() );
		}
	}
	
	public void testIntervals( )
	throws Exception
	{
		for( double dihedral : CircleRange.newCircle().samplePoints( 50 ) )
		{
			for( double halfwidth : Arrays.asList( 1.0, 5.0, 10.0, 20.0 ) )
			{
				assertIntervals( dihedral, Math.toRadians( halfwidth ) );
			}
		}
	}
	
	private void assertIntervals( double dihedral, double halfwidth )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( ScalarCouplingCalculator.Type.HnHa );
		
		// get the interval in hz
		List<Double> hzValues = Arrays.asList(
			calc.getHz( dihedral - halfwidth ),
			calc.getHz( dihedral + halfwidth )
		);
		Collections.sort( hzValues );
		double minHz = hzValues.get( 0 );
		double maxHz = hzValues.get( 1 );
		double hzMean = ( minHz + maxHz )/2.0;
		double hzHalfwidth = Math.abs( minHz - maxHz )/2.0;
		
		// build a dummy restraint to get the dihedral intervals back
		DihedralRestraint<AtomAddressInternal> restraint = new DihedralRestraint<AtomAddressInternal>(
			null, null, null, null,
			hzMean, hzHalfwidth
		);
		List<CircleRange> intervals = calc.getDihedralIntervals( restraint );
		
		// make sure each interval has the right hz bounds
		for( CircleRange interval : intervals )
		{
			// each bound has to be one of the hz values
			for( double bound : Arrays.asList( interval.getSource(), interval.getTarget() ) )
			{
				double hz = calc.getHz( bound );
				assertTrue( CompareReal.eq( hz, minHz ) || CompareReal.eq( hz, maxHz ) );
			}
			
			// interior points need to be in the range too
			for( double sample : interval.samplePoints( 10 ) )
			{
				double midHz = calc.getHz( sample );
				assertTrue( CompareReal.gte( midHz, minHz ) && CompareReal.lte( midHz, maxHz ) );
			}
		}
		
		// points outside of the intervals should be outside the range
		for( double angle : CircleRange.newCircle().samplePoints( 100 ) )
		{
			if( !isPointInIntervals( angle, intervals ) )
			{
				double hz = calc.getHz( angle );
				assertFalse( CompareReal.gte( hz, minHz ) && CompareReal.lte( hz, maxHz ) );
			}
		}
	}
	
	private static boolean isPointInIntervals( double point, List<CircleRange> intervals )
	{
		for( CircleRange interval : intervals )
		{
			if( interval.containsPoint( point ) )
			{
				return true;
			}
		}
		return false;
	}
}
