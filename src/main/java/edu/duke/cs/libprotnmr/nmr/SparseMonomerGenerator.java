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
package edu.duke.cs.libprotnmr.nmr;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class SparseMonomerGenerator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Subunit getSparseMonomer( Subunit monomer, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// make a copy of our original protein
		Subunit slim = new Subunit( monomer );
		
		// determine the max number of atoms per residue
		int maxNumAtoms = Integer.MIN_VALUE;
		for( Residue residue : monomer.getResidues() )
		{
			int numAtoms = residue.getAtoms().size();
			if( numAtoms > maxNumAtoms )
			{
				maxNumAtoms = numAtoms;
			}
		}
		
		// generate a list of all the residues and atoms involved in the restraints
		int numResidues = monomer.getResidues().size();
		boolean[] usedResidues = new boolean[numResidues];
		boolean[][] usedAtoms = new boolean[numResidues][maxNumAtoms];
		for( int i=0; i<numResidues; i++ )
		{
			usedResidues[i] = false;
			
			for( int j=0; i<maxNumAtoms; i++ )
			{
				usedAtoms[i][j] = false;
			}
		}
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// for each assignment...
			for( Assignment<AtomAddressInternal> assignment : restraint )
			{
				usedResidues[assignment.getLeft().getResidueId()] = true;
				usedResidues[assignment.getRight().getResidueId()] = true;
				
				usedAtoms[assignment.getLeft().getResidueId()][assignment.getLeft().getAtomId()] = true;
				usedAtoms[assignment.getRight().getResidueId()][assignment.getRight().getAtomId()] = true;
			}
		}
		
		// remove any residue that does not appear in the restraints
		ArrayList<Residue> residues = slim.getResidues();
		ListIterator<Residue> iterResidue = residues.listIterator();
		while( iterResidue.hasNext() )
		{
			Residue residue = iterResidue.next();
			
			if( !usedResidues[residue.getId()] )
			{
				// replace this entry with a null
				residues.set( iterResidue.previousIndex(), null );
			}
			else
			{
				// remove any atoms not used by the restraints
				ListIterator<Atom> iterAtom = residue.getAtoms().listIterator();
				while( iterAtom.hasNext() )
				{
					Atom atom = iterAtom.next();
					
					if( !usedAtoms[residue.getId()][atom.getId()] )
					{
						// replace this atom with a null
						residue.getAtoms().set( iterAtom.previousIndex(), null );
					}
				}
			}
		}
		
		// redo the atom index
		slim.updateAtomIndices();
		
		return slim;
	}
}
