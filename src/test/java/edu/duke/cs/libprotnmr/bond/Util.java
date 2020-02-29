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
