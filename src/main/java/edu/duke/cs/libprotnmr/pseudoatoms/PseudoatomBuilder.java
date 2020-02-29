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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.duke.cs.libprotnmr.atomType.AtomType;
import edu.duke.cs.libprotnmr.atomType.AtomTypeMap;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.nmr.Assignment;
import edu.duke.cs.libprotnmr.nmr.ChemicalShift;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PseudoatomBuilder
{
	private static final Logger m_log = LogManager.getLogger(PseudoatomBuilder.class);
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String PseudoatomsPath = Resources.getPath("pseudo.atoms");
	private static final String PseudoatomNameChars = "mpq";
	private static final boolean DefaultApplyCorrections = true;
	private static final boolean DefaultRemoveCorrections = true;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static PseudoatomBuilder m_instance;
	private Pseudoatoms m_pseudoatoms;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private PseudoatomBuilder( )
	{
		// read in the defs
		InputStream in = getClass().getResourceAsStream( PseudoatomsPath );
		try
		{
			m_pseudoatoms = PseudoatomReader.read( in );
		}
		catch( IOException ex )
		{
			// don't try to handle this
			throw new Error( ex );
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static PseudoatomBuilder getInstance( )
	{
		if( m_instance == null )
		{
			m_instance = new PseudoatomBuilder();
		}
		
		return m_instance;
	}
	
	public static boolean distanceRestraintsHavePseudoatoms( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		// NOTE: this function is somewhat heuristic.
		// It just checks for atoms names whose first character is in a list
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			for( Assignment<AtomAddressReadable> assignment : restraint )
			{
				Character leftChar = Character.toLowerCase( assignment.getLeft().getAtomName().charAt( 0 ) );
				Character rightChar = Character.toLowerCase( assignment.getRight().getAtomName().charAt( 0 ) );
				
				boolean hasPseudoatoms =
					PseudoatomNameChars.indexOf( leftChar ) >= 0
					|| PseudoatomNameChars.indexOf( rightChar ) >= 0;
				if( hasPseudoatoms == true )
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static Pseudoatoms getPseudoatoms( )
	{
		return getInstance().m_pseudoatoms;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public int build( Protein protein )
	{
		int numBuilt = 0;
		
		// for each subunit...
		for( Subunit subunit : protein.getSubunits() )
		{
			numBuilt += build( subunit );
		}
		
		return numBuilt;
	}
	
	public int build( Subunit subunit )
	{
		int numBuilt = 0;
		
		// for each residue
		for( Residue residue : subunit.getResidues() )
		{
			AminoAcid aminoAcid = residue.getAminoAcid();
			
			// get the pseudoatoms if any
			Set<String> pseudoatomNames = m_pseudoatoms.getPseudoatomNames( aminoAcid );
			if( pseudoatomNames == null )
			{
				continue;
			}
			
			// for each pseudoatom...
			for( String pseudoatomName : pseudoatomNames )
			{
				ArrayList<String> atomNames = m_pseudoatoms.getAtoms( aminoAcid, pseudoatomName );
				addPseudoatom( pseudoatomName, atomNames, residue );
				
				numBuilt++;
			}
		}
		
		subunit.updateAtomIndices();
		
		return numBuilt;
	}
	
	public int buildDistanceRestraints( Sequences sequence, List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		return buildDistanceRestraints( sequence, restraints, DefaultApplyCorrections );
	}
	
	public int buildDistanceRestraints( Sequences sequence, List<DistanceRestraint<AtomAddressReadable>> restraints, boolean applyCorrections )
	{
		int numBuilt = 0;
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			double maxLeftCorrection = 0.0;
			double maxRightCorrection = 0.0;
			
			for( Assignment<AtomAddressReadable> assignment : restraint )
			{
				double leftCorrection = interpretAddress( sequence, assignment.getLeft() );
				double rightCorrection = interpretAddress( sequence, assignment.getRight() );
				
				// update the max distances
				maxLeftCorrection = Math.max( maxLeftCorrection, leftCorrection );
				maxRightCorrection = Math.max( maxRightCorrection, rightCorrection );
			}
			
			// apply the largest corrections if needed
			if( applyCorrections )
			{
				restraint.setMaxDistance( restraint.getMaxDistance() + maxLeftCorrection + maxRightCorrection );
			}
			
			if( maxLeftCorrection > 0.0 || maxRightCorrection > 0.0 )
			{
				numBuilt++;
			}
		}
		return numBuilt;
	}
	
	public int unbuildDistanceRestraints( Sequences sequence, List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		return unbuildDistanceRestraints( sequence, restraints, DefaultRemoveCorrections );
	}
	
	public int unbuildDistanceRestraints( Sequences sequence, List<DistanceRestraint<AtomAddressReadable>> restraints, boolean removeCorrections )
	{
		// correction removal is completely untested
		if( removeCorrections )
		{
			m_log.warn( "Pseudoatom correction removal is completely untested!! It probably doesn't work." ); 
		}
		
		int numUnbuilt = 0;
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			double maxLeftCorrection = 0.0;
			double maxRightCorrection = 0.0;
			
			for( Assignment<AtomAddressReadable> assignment : restraint )
			{
				double leftCorrection = uninterpretAddress( sequence, assignment.getLeft() );
				double rightCorrection = uninterpretAddress( sequence, assignment.getRight() );
				
				// update the max distances
				maxLeftCorrection = Math.max( maxLeftCorrection, leftCorrection );
				maxRightCorrection = Math.max( maxRightCorrection, rightCorrection );
			}
			
			// apply the largest corrections if needed
			if( removeCorrections )
			{
				restraint.setMaxDistance( restraint.getMaxDistance() - maxLeftCorrection - maxRightCorrection );
			}
			
			if( maxLeftCorrection > 0.0 || maxRightCorrection > 0.0 )
			{
				numUnbuilt++;
			}
		}
		return numUnbuilt;
	}
	
	public int buildShifts( Sequences sequence, List<ChemicalShift<AtomAddressReadable>> shifts )
	{
		int numBuilt = 0;
		for( ChemicalShift<AtomAddressReadable> shift : shifts )
		{
			// they don't add in the wildcards for pseudoatoms so, add the wildcard
			AtomAddressReadable addressCopy = new AtomAddressReadable( shift.getAddress() );
			addressCopy.setAtomName( addressCopy.getAtomName() + "#" );
			
			// then check for the pseudoatom
			AminoAcid aminoAcid = sequence.getAminoAcid( shift.getAddress() );
			String pseudoatomName = m_pseudoatoms.getPseudoatomName( aminoAcid, addressCopy.getAtomName() );
			if( pseudoatomName != null )
			{
				shift.getAddress().setAtomName( pseudoatomName );
				numBuilt++;
			}
		}
		return numBuilt;
	}
	
	public boolean hasPseudoatoms( Protein protein )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			Atom atom = protein.getAtom( address );
			if( atom.isPseudoatom() )
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void addPseudoatom( String pseudoatomName, ArrayList<String> referenceAtoms, Residue residue )
	{
		// if this residue already has this pseudoatom, bail
		if( residue.getAtomByName( pseudoatomName ) != null )
		{
			return;
		}
		
		// determine the average position of the reference atoms
		Vector3 avgPos = new Vector3();
		int numAtomsAdded = 0;
		for( String referenceAtom : referenceAtoms )
		{
			Atom atom = residue.getAtomByName( referenceAtom );
			assert( atom != null ) : "Didn't find atom: " + residue.getNumber() + ":" + referenceAtom;
			avgPos.add( atom.getPosition() );
			numAtomsAdded++;
		}
		assert( numAtomsAdded == referenceAtoms.size() );
		avgPos.scale( 1.0 / numAtomsAdded );
		
		// add a new atom to the residue
		Atom pseudoatom = new Atom();
		pseudoatom.setIsPseudoatom( true );
		pseudoatom.setName( pseudoatomName );
		pseudoatom.setPosition( avgPos );
		pseudoatom.setId( residue.getAtoms().size() );
		residue.getAtoms().add( pseudoatom );
	}
	
	private double interpretAddress( Sequences sequences, AtomAddressReadable address )
	{
		// shortcut
		if( !address.isAmbiguous() )
		{
			return 0.0;
		}
		
		AminoAcid aminoAcid = sequences.getAminoAcid( address );
		if( aminoAcid == null )
		{
			return 0.0;
		}
		
		// is there a pseudoatom?
		String pseudoatomName = m_pseudoatoms.getPseudoatomName( aminoAcid, address.getAtomName() );
		if( pseudoatomName != null )
		{
			address.setAtomName( pseudoatomName );
			return m_pseudoatoms.getCorrection( aminoAcid, pseudoatomName );
		}
		
		// is the wildcard unnecessary?
		String wildcardPattern = "[\\*#]+$";
		if( address.getAtomName().matches( "[A-Z]+" + wildcardPattern ) )
		{
			String unwildcardedName = address.getAtomName().replaceAll( wildcardPattern, "" );
			ResidueType residueType = sequences.getSequence( address ).getResidueTypeByNumber( address.getResidueNumber() );
			AtomType atomType = AtomTypeMap.getInstance().getAtomType( residueType, aminoAcid, unwildcardedName );
			if( atomType != null )
			{
				address.setAtomName( unwildcardedName );
			}
		}
		
		return 0.0;
	}
	
	private double uninterpretAddress( Sequences sequences, AtomAddressReadable address )
	{
		// shortcut
		if( !Element.getByAtomName( address.getAtomName() ).isPseudoatom() )
		{
			return 0.0;
		}
		
		AminoAcid aminoAcid = sequences.getAminoAcid( address );
		
		// is there a pseudoatom?
		String mask = m_pseudoatoms.getMask( aminoAcid, address.getAtomName() );
		if( mask != null )
		{
			double correction = m_pseudoatoms.getCorrection( aminoAcid, address.getAtomName() );
			address.setAtomName( mask );
			return correction;
		}
		
		return 0.0;
	}
}
