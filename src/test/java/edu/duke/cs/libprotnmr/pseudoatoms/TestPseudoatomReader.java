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
package edu.duke.cs.libprotnmr.pseudoatoms;

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestPseudoatomReader extends ExtendedTestCase
{
	private static final String DefsPath = Resources.getPath("pseudo.atoms");
	public void testRead( )
	throws Exception
	{
		Pseudoatoms pseudoatoms = PseudoatomReader.read( getClass().getResourceAsStream( DefsPath ) );
		
		// check a few entries
		assertSize( pseudoatoms, AminoAcid.Alanine, 1 );
		assertEntry( pseudoatoms, AminoAcid.Alanine, "mb", "hb1,hb2,hb3", 1.5, "hb#" );
		
		assertSize( pseudoatoms, AminoAcid.Phenylalanine, 4 );
		assertEntry( pseudoatoms, AminoAcid.Phenylalanine, "qb", "hb2,hb3", 1.0, "hb#" );
		assertEntry( pseudoatoms, AminoAcid.Phenylalanine, "qd", "hd1,hd2", 2.0, "hd#" );
		assertEntry( pseudoatoms, AminoAcid.Phenylalanine, "qe", "he1,he2", 2.0, "he#" );
		assertEntry( pseudoatoms, AminoAcid.Phenylalanine, "qr", "cg,cd1,cd2,ce1,ce2,cz", 2.4, null );
		
		assertSize( pseudoatoms, AminoAcid.Valine, 3 );
		assertEntry( pseudoatoms, AminoAcid.Valine, "mg1", "hg11,hg12,hg13", 1.5, "hg1#" );
		assertEntry( pseudoatoms, AminoAcid.Valine, "mg2", "hg21,hg22,hg23", 1.5, "hg2#" );
		assertEntry( pseudoatoms, AminoAcid.Valine, "qg", "hg11,hg12,hg13,hg21,hg22,hg23", 2.4, "hg#" );
	}
	
	private void assertSize( Pseudoatoms pseudoatoms, AminoAcid aminoAcid, int expectedSize )
	{
		assertEquals( expectedSize, pseudoatoms.getPseudoatomNames( aminoAcid ).size() );
	}
	
	private void assertEntry( Pseudoatoms pseudoatoms, AminoAcid aminoAcid, String pseudoatomName, String expectedAtomNames, double correction, String mask )
	{
		ArrayList<String> observedAtomNames = pseudoatoms.getAtoms( aminoAcid, pseudoatomName );
		assertNotNull( observedAtomNames );
		
		// for each name in the expected names...
		for( String expected : expectedAtomNames.split( "," ) )
		{
			// search for it in the defs
			boolean found = false;
			for( String observed : observedAtomNames )
			{
				found = found || observed.equalsIgnoreCase( expected );
			}
			assertTrue( "Didn't find atom " + expected + " in " + pseudoatomName, found );
		}
		
		// check the correction
		assertEquals( correction, pseudoatoms.getCorrection( aminoAcid, pseudoatomName ) );
		
		// check the mask
		if( mask != null )
		{
			assertEquals( pseudoatomName.toUpperCase(), pseudoatoms.getPseudoatomName( aminoAcid, mask ).toUpperCase() );
		}
	}
}
