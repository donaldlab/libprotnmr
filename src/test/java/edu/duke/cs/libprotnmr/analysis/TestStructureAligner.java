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

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;
import edu.duke.cs.libprotnmr.resources.Resources;

public class TestStructureAligner extends ExtendedTestCase
{
	// UNDONE: this test should be moved into a ProteinGeometry test suite
	public void testCentering( )
	throws Exception
	{
		// get a protein and make a copy
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
		Protein copy = new Protein( protein );
		
		// RMSD should be zero
		assertRmsdZero( protein, copy );
		
		// center the proteins
		ProteinGeometry.center( protein );
		assertRmsdNotZero( protein, copy );
		ProteinGeometry.center( copy );
		
		// rmsd should be zero
		assertRmsdZero( protein, copy );
	}
	
	public void testTranslatedComputedOligomer( )
	throws Exception
	{
		// get a protein and make a copy
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
		Protein copy = new Protein( protein );
		
		// RMSD should be zero
		assertRmsdZero( protein, copy );
		
		// translate our copy
		ProteinGeometry.translate( copy, getRandomVector( -5.0, 5.0 ) );
		
		// the RMSD should NOT be zero here until we find the optimal alignment
		assertRmsdNotZero( protein, copy );
		ProteinGeometry.center( protein );
		StructureAligner.alignOptimally( protein, copy );
		assertRmsdZero( protein, copy );
	}
	
	public void testRotatedComputedOligomer( )
	throws Exception
	{
		// get a protein and make a copy
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
		Protein copy = new Protein( protein );
		
		// RMSD should be zero
		assertRmsdZero( protein, copy );
		
		// rotate our copy with a random rotation
		ProteinGeometry.rotate( protein, getRandomRotation() );		
		
		// the RMSD should NOT be zero here until we find the optimal alignment
		assertRmsdNotZero( protein, copy );
		ProteinGeometry.center( protein );
		StructureAligner.alignOptimally( protein, copy );
		assertRmsdZero( protein, copy );
	}
	
	public void testRotatedAndTranslatedReferenceOligomer( )
	throws Exception
	{
		// get a protein and make a copy
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
		Protein copy = new Protein( protein );
		
		// RMSD should be zero
		assertRmsdZero( protein, copy );
		
		// rotate our copy with a random rotation and translation
		ProteinGeometry.rotate( protein, getRandomRotation() );		
		ProteinGeometry.translate( copy, getRandomVector( -5.0, 5.0 ) );
		
		// the RMSD should NOT be zero here until we find the optimal alignment
		assertRmsdNotZero( protein, copy );
		ProteinGeometry.center( protein );
		StructureAligner.alignOptimally( protein, copy );
		assertRmsdZero( protein, copy );
	}
	
	private void assertRmsdZero( Protein reference, Protein compare )
	{
		assertTrue( CompareReal.eq( RmsdCalculator.getRmsd( reference, compare ), 0.0 ) );
	}
	
	private void assertRmsdNotZero( Protein reference, Protein compare )
	{
		// NOTE: There's a (small) possibility that we randomly chose the identitiy
		//       transformation and this assert could trip.
		assertFalse( CompareReal.eq( RmsdCalculator.getRmsd( reference, compare ), 0.0 ) );
	}
}
