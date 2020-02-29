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
package edu.duke.cs.libprotnmr.protein.tools;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.Dipeptide;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.IdealGeometry;
import edu.duke.cs.libprotnmr.protein.PeptidePlane;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class PolypeptideGenerator
{
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static Subunit newPolypeptide( List<Double> angles )
	{
		return newPolypeptide( angles, new PeptidePlane() );
	}
	
	public static Subunit newPolypeptide( List<Double> angles, PeptidePlane firstPlane )
	{
		if( angles == null || angles.size() < 2 )
		{
			throw new IllegalArgumentException( "At least two angles must be specified!" );
		}
		if( angles.size() % 2 != 0 )
		{
			throw new IllegalArgumentException( "There must be an even number of angles!" );
		}
		
		// make the subunit
		Subunit subunit = new Subunit();
		subunit.setName( 'A' );
		
		// init the dipeptide
		Dipeptide dipeptide = new Dipeptide();
		dipeptide.setCwardsPlane( firstPlane );
		
		// for each residue...
		int numResidues = angles.size()/2;
		for( int i=0; i<numResidues; i++ )
		{
			// update the dipeptide
			dipeptide.setNwardsPlane( dipeptide.getCwardsPlane() );
			dipeptide.updateCwardsPlane( angles.get( 0 ), angles.get( 1 ) );
			
			// make the residue
			Residue residue = new Residue();
			residue.setAminoAcid( AminoAcid.Alanine );
			residue.setNumber( i );
			subunit.getResidues().add( residue );
			
			// add the atoms
			residue.getAtoms().add( newAtom( "H", dipeptide.getNwardsHAtom(), false ) );
			residue.getAtoms().add( newAtom( "N", dipeptide.getNwardsNAtom(), true ) );
			residue.getAtoms().add( newAtom( "CA", dipeptide.getCaAtom(), true ) );
			residue.getAtoms().add( newAtom( "HA", dipeptide.getHaAtom(), false ) );
			residue.getAtoms().add( newAtom( "C", dipeptide.getCwardsCAtom(), true ) );
			residue.getAtoms().add( newAtom( "O", dipeptide.getCwardsOAtom(), false ) );
		}
		
		// bookkeeping
		subunit.updateResidueIndex();
		subunit.updateAtomIndices();
		
		return subunit;
	}
	
	public static Subunit newHelix( int numResidues )
	{
		return newHelix( numResidues, new PeptidePlane() );
	}
	
	public static Subunit newHelix( int numResidues, PeptidePlane firstPlane )
	{
		// build the angles
		List<Double> angles = new ArrayList<Double>( numResidues*2 );
		for( int i=0; i<numResidues; i++ )
		{
			angles.add( IdealGeometry.HelixPhi );
			angles.add( IdealGeometry.HelixPsi );
		}
		
		return newPolypeptide( angles, firstPlane );
	}
	
	public static Subunit newStrand( int numResidues )
	{
		return newStrand( numResidues, new PeptidePlane() );
	}
	
	public static Subunit newStrand( int numResidues, PeptidePlane firstPlane )
	{
		// build the angles
		List<Double> angles = new ArrayList<Double>( numResidues*2 );
		for( int i=0; i<numResidues; i++ )
		{
			angles.add( IdealGeometry.StrandPhi );
			angles.add( IdealGeometry.StrandPsi );
		}
		
		return newPolypeptide( angles, firstPlane );
	}
	
	public static void numberResidues( Subunit subunit, int startResidueNumber )
	{
		for( int i=0; i<subunit.getResidues().size(); i++ )
		{
			subunit.getResidue( i ).setNumber( startResidueNumber + i );
		}
		subunit.updateResidueIndex();
		subunit.updateAtomIndices();
	}
	
	
	/*********************************
	 *   Static Functions
	 *********************************/
	
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
