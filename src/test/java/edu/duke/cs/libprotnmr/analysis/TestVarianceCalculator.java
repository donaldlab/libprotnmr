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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestVarianceCalculator extends ExtendedTestCase
{
	public void testVectors( )
	{
		Vector3 a = new Vector3( 1, 2, 3 );
		Vector3 b = new Vector3( 6, 5, 4 );
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		points.add( a );
		points.add( b );
		assertEquals( a.getSquaredDistance( b ) / 4.0, VarianceCalculator.Metric.Variance.evaluate( points ), CompareReal.getEpsilon() );
	}
	
	public void testSingleProtein( )
	throws Exception
	{
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("tinyProtein.pdb") ) );
		ArrayList<Protein> proteins = new ArrayList<Protein>();
		proteins.add( protein );
		assertEquals( 0.0, VarianceCalculator.getAverageVariance( proteins ) );
	}
	
	public void testProteinClones( )
	throws Exception
	{
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("tinyProtein.pdb") ) );
		ArrayList<Protein> proteins = new ArrayList<Protein>();
		proteins.add( protein );
		proteins.add( protein );
		proteins.add( protein );
		proteins.add( protein );
		proteins.add( protein );
		proteins.add( protein );
		assertEquals( 0.0, VarianceCalculator.getAverageVariance( proteins ) );
	}
}
