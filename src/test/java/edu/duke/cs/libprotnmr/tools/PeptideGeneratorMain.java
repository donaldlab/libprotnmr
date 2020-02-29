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

package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.pdb.ProteinWriter;
import edu.duke.cs.libprotnmr.protein.*;

import java.io.File;

public class PeptideGeneratorMain
{
	public static void main( String[] args )
	throws Exception
	{
		final int NumResidues = 3;
		
		Subunit subunit = new Subunit();
		subunit.setName( 'A' );
		
		// extended conformation
		final double Phi = Math.PI;
		final double Psi = Math.PI;
		
		// alpha helix conformation
		//final double Phi = Math.toRadians( -90.0 );
		//final double Psi = Math.toRadians( 0.0 );
		
		// pick an arbitrary peptide plane orientation for the first residue
		PeptidePlane firstPlane = new PeptidePlane();
		Dipeptide dipeptide = new Dipeptide();
		dipeptide.setNwardsPlane( firstPlane );
		dipeptide.updateCwardsPlane( Phi, Psi );
		
		for( int i=1; i<=NumResidues; i++ )
		{
			// make the first residue
			Residue residue = new Residue();
			residue.setAminoAcid( AminoAcid.Alanine );
			residue.setNumber( i );
			
			residue.getAtoms().add( newAtom( "H", dipeptide.getNwardsHAtom(), false ) );
			residue.getAtoms().add( newAtom( "N", dipeptide.getNwardsNAtom(), true ) );
			residue.getAtoms().add( newAtom( "CA", dipeptide.getCaAtom(), true ) );
			residue.getAtoms().add( newAtom( "HA", dipeptide.getHaAtom(), false ) );
			residue.getAtoms().add( newAtom( "C", dipeptide.getCwardsCAtom(), true ) );
			residue.getAtoms().add( newAtom( "O", dipeptide.getCwardsOAtom(), false ) );
			subunit.getResidues().add( residue );
		
			// get the next plane
			dipeptide.setNwardsPlane( dipeptide.getCwardsPlane() );
			dipeptide.updateCwardsPlane( Phi, Psi );
		}
		
		// bookkeeping
		subunit.updateResidueIndex();
		subunit.updateAtomIndices();
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendBackbone( kin, subunit, "Backbone", KinemageColor.Cobalt, 1 );
			new KinemageWriter().show( kin );
		}
		
		// write out the protein structure
		new ProteinWriter().write( new Protein( subunit ), new File( "output/polypeptide.pdb" ) );
	}

	private static Atom newAtom( String atomName, Vector3 pos, boolean isBackbone )
	{
		Atom atom = new Atom();
		atom.setName( atomName );
		atom.getPosition().set( pos );
		atom.setIsBackbone( isBackbone );
		atom.setElement( Element.getByAtomName( atomName ) );
		return atom;
	}
}
