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
package edu.duke.cs.libprotnmr.bond;

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class Util
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Subunit newTriGlycine( int id )
	{
		Subunit subunit = new Subunit();
		subunit.addResidue( Util.newResidue( 0, AminoAcid.Glycine, "H1,H2,H3,N,CA,HA2,HA3,C,O" ) );
		subunit.addResidue( Util.newResidue( 1, AminoAcid.Glycine, "H,N,CA,HA2,HA3,C,O" ) );
		subunit.addResidue( Util.newResidue( 2, AminoAcid.Glycine, "H,N,CA,HA2,HA3,C,O,OXT" ) );
		subunit.updateAtomIndices();
		return subunit;
	}
	
	public static Residue newResidue( int id, AminoAcid aminoAcid, String atomNames )
	{
		Residue residue = new Residue();
		residue.setId( id );
		residue.setNumber( id + 1 );
		residue.setAminoAcid( aminoAcid );
		String[] atomNamesParts = atomNames.split( "," );
		
		// make each atom
		int nextAtomId = 0;
		ArrayList<Atom> atoms = new ArrayList<Atom>();
		for( String atomName : atomNamesParts )
		{
			Atom atom = new Atom();
			atom.setName( atomName );
			atom.setId( nextAtomId++ );
			atom.setResidueId( residue.getId() );
			
			// HACKHACK: check for backbone status
			boolean isBackbone =
				atom.getName().equalsIgnoreCase( "C" )
				|| atom.getName().equalsIgnoreCase( "N" )
				|| atom.getName().equalsIgnoreCase( "CA" );
			atom.setIsBackbone( isBackbone );

			atoms.add( atom );
		}
		residue.setAtoms( atoms );
		
		return residue;
	}
}
