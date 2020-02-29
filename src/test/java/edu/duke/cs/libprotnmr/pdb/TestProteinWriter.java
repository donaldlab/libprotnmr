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

package edu.duke.cs.libprotnmr.pdb;

import java.io.File;
import java.io.IOException;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestProteinWriter extends ExtendedTestCase
{
	private static final String InPath = Resources.getPath("write.in.protein");
	private static final String ExpectedOutPath = Resources.getPath("write.out.protein");
	private static final String ObservedOutPath = "/tmp/test.protein";
	
	public void testProteinWriter( )
	throws IOException
	{
		// read in a protein
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( InPath ) );
		
		// write it back out
		ProteinWriter proteinWriter = new ProteinWriter();
		proteinWriter.write( protein, ObservedOutPath );
		
		// check the files
		assertEqualsTextFile( getClass().getResourceAsStream( ExpectedOutPath ), ObservedOutPath );
		
		// cleanup
		new File( ObservedOutPath ).delete();
	}
}
