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
