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

package edu.duke.cs.libprotnmr.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.math.RotationOptimizer;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.protein.tools.AtomPositionIterator;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;


public class StructureAligner
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void align( HasAtoms reference, HasAtoms computed )
	{
		// just align the computed protein to the reference protein using the first atom
		Atom referenceAtom = reference.getAtom( reference.backboneAtoms().get( 0 ) );
		Atom computedAtom = computed.getAtom( computed.backboneAtoms().get( 0 ) );
		Vector3 offset = new Vector3( referenceAtom.getPosition() );
		offset.subtract( computedAtom.getPosition() );
		
		// apply the offset to every atom in the computed protein
		for( AtomAddressInternal address : computed.atoms() )
		{
			computed.getAtom( address ).getPosition().add( offset );
		}
	}
	
	/* Jeff: 12/02/2008
		These optimal alignment algorithm are based on
		http://cnx.org/content/m11608/latest/
		
		Note: all optimal alignment methods assume the centroid of the reference
		atom set is at the origin.
	*/

	public static void alignOptimally( HasAtoms reference, HasAtoms computed )
	{
		// first, align the proteins by centroids
		ProteinGeometry.center( computed );
		
		// get the optimal rotation from computed to reference
		Quaternion optimalRotation = RotationOptimizer.getOptimalRotation(
			new AtomPositionIterator( reference ),
			new AtomPositionIterator( computed )
		);
		
		// apply the rotation to the computed structure
		ProteinGeometry.rotate( computed, optimalRotation );
	}
	
	public static void alignOptimallyBySubunit( Protein reference, Protein computed, int subunitId )
	{
		/*
			Note: this method assumes the centroid of the reference subunit
			is at the origin.
		*/
		
		Subunit referenceSubunit = reference.getSubunit( subunitId );
		Subunit computedSubunit = computed.getSubunit( subunitId );
		
		// just in case...
		assert( referenceSubunit.atoms().size() == computedSubunit.atoms().size() );
		
		// first, align the proteins by centroids
		Vector3 translation = ProteinGeometry.getCentroid( computedSubunit );
		translation.negate();
		ProteinGeometry.translate( computed, translation );
		
		// get the optimal rotation from computed to reference
		Quaternion optimalRotation = RotationOptimizer.getOptimalRotation(
			new AtomPositionIterator( reference, referenceSubunit.atoms() ),
			new AtomPositionIterator( computed, computedSubunit.atoms() )
		);
		
		// apply the rotation to the computed structure
		ProteinGeometry.rotate( computed, optimalRotation );
	}
	
	public static void alignOptimallyByAtoms( HasAtoms reference, HasAtoms computed, Iterable<AtomAddressReadable> readableAddresses )
	{
		// convert the atom addresses
		ArrayList<AtomAddressInternal> referenceAddresses = new ArrayList<AtomAddressInternal>();
		ArrayList<AtomAddressInternal> computedAddresses = new ArrayList<AtomAddressInternal>();
		for( AtomAddressReadable readableAddress : readableAddresses )
		{
			referenceAddresses.addAll( AddressMapper.mapAddressExpandPseudoatoms( reference, readableAddress ) );
			computedAddresses.addAll( AddressMapper.mapAddressExpandPseudoatoms( computed, readableAddress ) );
		}
		
		alignOptimallyByAtoms( reference, computed, referenceAddresses, computedAddresses );
	}
	
	public static void alignOptimallyByAtoms( HasAtoms reference, HasAtoms computed, List<AtomAddressInternal> referenceAddresses, List<AtomAddressInternal> computedAddresses )
	{
		// first, align the proteins by centroids
		ProteinGeometry.center( computed, computedAddresses );
		
		// get the optimal rotation from computed to reference
		Quaternion optimalRotation = RotationOptimizer.getOptimalRotation(
			new AtomPositionIterator( reference, referenceAddresses ),
			new AtomPositionIterator( computed, computedAddresses )
		);
		
		// apply the rotation to the computed structure
		ProteinGeometry.rotate( computed, optimalRotation );
	}
	
	public static void alignEnsembleOptimally( List<? extends HasAtoms> proteins )
	{
		// use all the atoms of the first non-null protein
		for( HasAtoms protein : proteins )
		{
			if( protein != null )
			{
				alignEnsembleOptimallyByAtoms( proteins, protein.atoms() );
				return;
			}
		}
	}
	
	public static void alignEnsembleOptimallyByAtoms( List<? extends HasAtoms> proteins, List<AtomAddressInternal> addresses )
	{
		HasAtoms firstProtein = null;
		for( HasAtoms protein : proteins )
		{
			if( protein == null )
			{
				continue;
			}
			
			if( firstProtein == null )
			{
				// center the first (non-null) protein
				firstProtein = protein;
				ProteinGeometry.center( firstProtein, addresses );
			}
			else
			{
				// align the rest of the structures to the first one
				alignOptimallyByAtoms( firstProtein, protein, addresses, addresses );
			}
		}
	}
	
	public static double alignSSEs( List<Subunit> sses, Protein reference )
	{
		return alignSSEs( sses, reference, 0 );
	}
	
	public static double alignSSEs( List<Subunit> sses, Protein reference, int sseToProteinResidueOffset )
	{
		// use all atoms by default
		List<List<AtomAddressInternal>> atomAddresses = new ArrayList<List<AtomAddressInternal>>();
		for( Subunit sse : sses )
		{
			atomAddresses.add( sse.atoms() );
		}
		return alignSSEs( sses, atomAddresses, reference, sseToProteinResidueOffset );
	}
	
	public static double alignSSEBackbones( List<Subunit> sses, Protein reference )
	{
		return alignSSEs( sses, reference, 0 );
	}
	
	public static double alignSSEBackbones( List<Subunit> sses, Protein reference, int sseToProteinResidueOffset )
	{
		// use the backbone atoms
		List<List<AtomAddressInternal>> atomAddresses = new ArrayList<List<AtomAddressInternal>>();
		for( Subunit sse : sses )
		{
			atomAddresses.add( sse.backboneAtoms() );
		}
		return alignSSEs( sses, atomAddresses, reference, sseToProteinResidueOffset );
	}
	
	public static double alignSSEs( List<Subunit> sses, List<List<AtomAddressInternal>> addresses, Protein reference )
	{
		return alignSSEs( sses, addresses, reference, 0 );
	}
	
	public static double alignSSEs( List<Subunit> sses, List<List<AtomAddressInternal>> addresses, Protein reference, int sseToProteinResidueOffset )
	{
		if( sses.size() != addresses.size() )
		{
			throw new IllegalArgumentException( "Must give the same number of SSEs and atom address lists!" );
		}
		
		// map the addresses to the reference structure
		List<List<AtomAddressInternal>> referenceAddresses = new ArrayList<List<AtomAddressInternal>>();
		for( int i=0; i<sses.size(); i++ )
		{
			referenceAddresses.add( AddressMapper.translateAddresses( reference, sses.get( i ), addresses.get( i ) ) );
		}
		
		// compute the optimal rotations for each individual SSE alignment
		List<Quaternion> optimalRotations = new ArrayList<Quaternion>();
		for( int i=0; i<sses.size(); i++ )
		{
			Subunit sse = sses.get( i );
			List<AtomAddressInternal> sseAddresses = addresses.get( i );
			List<AtomAddressInternal> referenceSseAddresses = referenceAddresses.get( i );
			
			optimalRotations.add( getOptimalSseRotation( sse, reference, sseAddresses, referenceSseAddresses ) );
		}
		
		// compute the average rotation
		Quaternion avgRotation = new Quaternion( 0, 0, 0, 0 );
		for( Quaternion q : optimalRotations )
		{
			// pick the equivalent quaternion closest to the first one
			if( q.getDot( optimalRotations.get( 0 ) ) < 0 )
			{
				q.negate();
			}
			avgRotation.add( q );
		}
		avgRotation.normalize();
		
		// rotate and center the SSEs on the reference by centroids
		List<Double> rmsds = new ArrayList<Double>();
		int numAtoms = 0;
		for( int i=0; i<sses.size(); i++ )
		{
			Subunit sse = sses.get( i );
			List<AtomAddressInternal> sseAddresses = addresses.get( i );
			List<AtomAddressInternal> referenceSseAddresses = referenceAddresses.get( i );
			
			// apply the rotation
			ProteinGeometry.rotate( sse, avgRotation );
			
			// get the centroids
			Vector3 sseCentroid = ProteinGeometry.getCentroid( sse, sseAddresses );
			Vector3 referenceSseCentroid = ProteinGeometry.getCentroid( reference, referenceSseAddresses );
			
			// translate the SSE to the reference
			Vector3 translation = new Vector3( referenceSseCentroid );
			translation.subtract( sseCentroid );
			ProteinGeometry.translate( sse, translation );
			
			// compute the RMSD
			rmsds.add( RmsdCalculator.getRmsd( sse, reference, sseAddresses, referenceSseAddresses ) );
			numAtoms += sseAddresses.size();
		}
		
		// compute the total RMSD
		double sum = 0;
		for( double rmsd : rmsds )
		{
			// undo the MS part and update the total sum
			sum += rmsd*rmsd*numAtoms;
		}
		// redo the MS part and we're done!
		return Math.sqrt( sum/numAtoms ); 
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static Quaternion getOptimalSseRotation( HasAtoms sse, HasAtoms reference, List<AtomAddressInternal> sseAddresses, List<AtomAddressInternal> referenceAddresses )
	{
		// center the SSE
		Vector3 sseCentroid = ProteinGeometry.getCentroid( sse, sseAddresses );
		Vector3 sseTranslation = new Vector3( sseCentroid );
		sseTranslation.negate();
		ProteinGeometry.translate( sse, sseTranslation );
		
		// center the reference on its SSE
		Vector3 referenceCentroid = ProteinGeometry.getCentroid( reference, referenceAddresses );
		Vector3 referenceTranslation = new Vector3( referenceCentroid );
		referenceTranslation.negate();
		ProteinGeometry.translate( reference, referenceTranslation );
		
		// compute the optimal rotation
		Quaternion optimalRotation = RotationOptimizer.getOptimalRotation(
			new AtomPositionIterator( reference, referenceAddresses ),
			new AtomPositionIterator( sse, sseAddresses )
		);
		
		// translate everything back
		sseTranslation.negate();
		ProteinGeometry.translate( sse, sseTranslation );
		referenceTranslation.negate();
		ProteinGeometry.translate( reference, referenceTranslation );
		
		return optimalRotation;
	}
}
