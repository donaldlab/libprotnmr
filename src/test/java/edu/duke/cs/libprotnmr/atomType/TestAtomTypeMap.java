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
package edu.duke.cs.libprotnmr.atomType;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;

public class TestAtomTypeMap extends ExtendedTestCase
{
	public void testAtomTypeMap( )
	throws Exception
	{
		// load the atom map
		AtomTypeMap map = AtomTypeMap.getInstance();
		
		// load a tiny protein
		ProteinReader reader = new ProteinReader();
		Protein protein = reader.read( getClass().getResourceAsStream( Resources.getPath("largeProtein.pdb") ) );
		
		// check backbone types for the n-terminus residue
		assertAtomTypeEquals( AtomType.N4, map, protein, 0, 0, 0 ); // N
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 0, 1 ); // CA
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 0, 2 ); // C
		assertAtomTypeEquals( AtomType.O2, map, protein, 0, 0, 3 ); // O
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 0, 9 ); // CA
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 0, 21 ); // H1
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 0, 22 ); // H2
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 0, 23 ); // H3
		
		// check backbone types for a normal residue
		assertAtomTypeEquals( AtomType.Nam, map, protein, 0, 1, 0 ); // N
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 1, 1 ); // CA
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 1, 2 ); // C
		assertAtomTypeEquals( AtomType.O2, map, protein, 0, 1, 3 ); // O
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 1, 9 ); // H
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 1, 10 ); // HA
		
		// check backbone types for the c-terminus residue
		assertAtomTypeEquals( AtomType.Nam, map, protein, 0, 41, 0 ); // N
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 41, 1 ); // CA
		assertAtomTypeEquals( AtomType.C3, map, protein, 0, 41, 2 ); // C
		assertAtomTypeEquals( AtomType.Oco2, map, protein, 0, 41, 3 ); // O
		assertAtomTypeEquals( AtomType.Oco2, map, protein, 0, 41, 4 ); // OXT
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 41, 5 ); // H
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 41, 6 ); // HA2
		assertAtomTypeEquals( AtomType.H, map, protein, 0, 41, 7 ); // HA3
	}
	
	private void assertAtomTypeEquals( AtomType expected, AtomTypeMap map, Protein protein, int subunitId, int residueId, int atomId )
	{
		Subunit subunit = protein.getSubunit( subunitId );
		Residue residue = subunit.getResidues().get( residueId );
		Atom atom = residue.getAtoms().get( atomId );
		assertEquals( expected, map.getAtomType( subunit, residue, atom ) );
	}
}
