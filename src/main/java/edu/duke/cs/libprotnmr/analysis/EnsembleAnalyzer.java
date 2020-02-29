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
package edu.duke.cs.libprotnmr.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.analysis.ClashScore.AddressPair;
import edu.duke.cs.libprotnmr.analysis.ClashScore.Spike;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceCluster;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceClusterer;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrix;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Distribution;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.DihedralRestraint;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;
import edu.duke.cs.libprotnmr.rama.RamaCase;
import edu.duke.cs.libprotnmr.rama.RamaSatisfaction;
import edu.duke.cs.libprotnmr.xplor.EnergyCalculator;
import edu.duke.cs.libprotnmr.xplor.XplorException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EnsembleAnalyzer
{
	private static final Logger m_log = LogManager.getLogger(EnsembleAnalyzer.class);
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	public static class AlignmentScore implements Distribution.Value
	{
		public int referenceId;
		public int compareId;
		public double rmsd;
		
		public AlignmentScore( int referenceId, int compareId, double rmsd )
		{
			this.referenceId = referenceId;
			this.compareId = compareId;
			this.rmsd = rmsd;
		}
		
		public AlignmentScore( AlignmentScore other )
		{
			this.referenceId = other.referenceId;
			this.compareId = other.compareId;
			this.rmsd = other.rmsd;
		}
		
		@Override
		public double getValue( )
		{
			return rmsd;
		}
		
		@Override
		public String toString( )
		{
			return Double.toString( rmsd );
		}
		
		public List<HasAtoms> getAlignment( List<HasAtoms> ensembleReference, List<HasAtoms> ensembleCompare, List<AtomAddressInternal> addressesReference, List<AtomAddressInternal> addressesCompare )
		{
			HasAtoms reference = ensembleReference.get( referenceId ).clone();
			HasAtoms compare = ensembleCompare.get( compareId ).clone();
			
			ProteinGeometry.center( reference, addressesReference );
			StructureAligner.alignOptimallyByAtoms( reference, compare, addressesReference, addressesCompare );
			
			// just in case...
			assert( CompareReal.eq( rmsd, RmsdCalculator.getRmsd( reference, compare, addressesReference, addressesCompare ), 1e-10 ) );
			
			return Arrays.asList( reference, compare );
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getOptimalRmsd( HasAtoms a, HasAtoms b, List<AtomAddressInternal> addresses )
	{
		return getOptimalRmsd( a, b, addresses, addresses );
	}
	
	public static double getOptimalRmsd( HasAtoms a, HasAtoms b, List<AtomAddressInternal> addressesA, List<AtomAddressInternal> addressesB )
	{
		// copy the structures so we don't change them
		a = a.clone();
		b = b.clone();
		
		// compute the optimal RMSD
		ProteinGeometry.center( a, addressesA );
		StructureAligner.alignOptimallyByAtoms( a, b, addressesA, addressesB );
		return RmsdCalculator.getRmsd( a, b, addressesA, addressesB );
	}
	
	public static double getMedianToMedianRmsd( List<HasAtoms> ensembleA, List<HasAtoms> ensembleB, List<AtomAddressInternal> addressesA, List<AtomAddressInternal> addressesB )
	{
		return getOptimalRmsd(
			getMedianStructure( ensembleA, addressesA ),
			getMedianStructure( ensembleB, addressesB ),
			addressesA,
			addressesB
		);
	}
	
	public static double getMeanToMeanRmsd( List<HasAtoms> ensembleA, List<HasAtoms> ensembleB, List<AtomAddressInternal> addressesA, List<AtomAddressInternal> addressesB )
	{
		return getOptimalRmsd(
			getMeanStructure( ensembleA ),
			getMeanStructure( ensembleB ),
			addressesA,
			addressesB
		);
	}
	
	public static List<HasAtoms> cloneEnsemble( List<HasAtoms> ensemble )
	{
		List<HasAtoms> ensembleCopy = new ArrayList<HasAtoms>( ensemble.size() );
		for( HasAtoms structure : ensemble )
		{
			ensembleCopy.add( structure.clone() );
		}
		return ensembleCopy;
	}
	
	public static DistanceMatrix getDistanceMatrix( List<HasAtoms> ensemble, List<AtomAddressInternal> addresses )
	{
		// copy the ensemble so we can align it
		List<HasAtoms> ensembleCopy = cloneEnsemble( ensemble );
		
		DistanceMatrix distances = new DistanceMatrix( ensemble.size() );
		for( int i=0; i<ensemble.size(); i++ )
		{
			// center the ith structure
			HasAtoms structureI = ensembleCopy.get( i );
			ProteinGeometry.center( structureI, addresses );
			
			for( int j=0; j<i; j++ )
			{
				// align the jth structure to the ith structure
				HasAtoms structureJ = ensembleCopy.get( j );
				StructureAligner.alignOptimallyByAtoms( structureI, structureJ, addresses, addresses );
				double distance = RmsdCalculator.getRmsd( structureI, structureJ, addresses, addresses );
				distances.set( i, j, distance );
			}
		}
		return distances;
	}
	
	public static HasAtoms getMedianStructure( List<HasAtoms> ensemble, List<AtomAddressInternal> addresses )
	{
		// find the structure with the minimum sum RMSD to the other structures
		
		DistanceMatrix distances = getDistanceMatrix( ensemble, addresses );
		
		// find the median structure
		double bestSum = Double.POSITIVE_INFINITY;
		int bestIndex = -1;
		for( int i=0; i<ensemble.size(); i++ )
		{
			// compute the sum for structure i
			double sum = 0;
			for( int j=0; j<ensemble.size(); j++ )
			{
				sum += distances.get( j, i );
			}
			
			if( sum < bestSum )
			{
				bestSum = sum;
				bestIndex = i;
			}
		}
		return ensemble.get( bestIndex );
	}
	
	public static HasAtoms getMeanStructure( List<HasAtoms> ensemble )
	{
		HasAtoms meanStructure = ensemble.get( 0 ).clone();
		for( AtomAddressInternal address : ensemble.get( 0 ).atoms() )
		{
			Vector3 meanPos = meanStructure.getAtom( address ).getPosition();
			meanPos.set( 0, 0, 0 );
			for( HasAtoms structure : ensemble )
			{
				meanPos.add( structure.getAtom( address ).getPosition() );
			}
			meanPos.scale( 1.0/ensemble.size() );
		}
		return meanStructure;
	}
	
	public static List<Double> getAllPairsRmsds( List<HasAtoms> ensemble, List<AtomAddressInternal> addresses )
	{
		List<Double> rmsds = new ArrayList<Double>();
		for( int i=0; i<ensemble.size(); i++ )
		{
			HasAtoms structureI = ensemble.get( i ).clone();
			ProteinGeometry.center( structureI, addresses );
			for( int j=0; j<i; j++ )
			{
				HasAtoms structureJ = ensemble.get( j ).clone();
				StructureAligner.alignOptimallyByAtoms( structureI, structureJ, addresses, addresses );
				rmsds.add( RmsdCalculator.getRmsd( structureI, structureJ, addresses, addresses ) );
			}
		}
		return rmsds;
	}
	
	public static void alignToReference( List<HasAtoms> ensemble, HasAtoms reference, List<AtomAddressInternal> addresses )
	{
		ProteinGeometry.center( reference, addresses );
		for( HasAtoms structure : ensemble )
		{
			StructureAligner.alignOptimallyByAtoms( reference, structure, addresses, addresses );
		}
	}
	
	public static List<Double> getRmsdsToReference( List<HasAtoms> ensemble, HasAtoms reference, List<AtomAddressInternal> addresses )
	{
		List<Double> rmsds = new ArrayList<Double>( ensemble.size() );
		for( HasAtoms structure : ensemble )
		{
			// skip the reference structure if needed
			if( structure == reference )
			{
				continue;
			}
			
			rmsds.add( RmsdCalculator.getRmsd( reference, structure, addresses, addresses ) );
		}
		return rmsds;
	}
	
	public static void alignToExternalReference( List<Protein> ensemble, Protein reference, List<AtomAddressInternal> referenceAddresses )
	{
		ProteinGeometry.center( reference, referenceAddresses );
		
		// convert the external addresses to readable
		List<AtomAddressReadable> readableAddresses = AddressMapper.mapAddressesToReadable( reference, referenceAddresses );
		
		for( Protein structure : ensemble )
		{
			// remap the addresses to this structure
			List<AtomAddressInternal> internalAddresses = AddressMapper.mapAddressesToInternal( structure, readableAddresses );
			warnUnmappedAddresses( readableAddresses, internalAddresses );
			
			StructureAligner.alignOptimallyByAtoms( reference, structure, referenceAddresses, internalAddresses );
		}
	}
	
	public static List<Double> getRmsdsToExternalReference( List<Protein> ensemble, Protein reference, List<AtomAddressInternal> referenceAddresses )
	{
		// convert the external addresses to readable
		List<AtomAddressReadable> readableAddresses = AddressMapper.mapAddressesToReadable( reference, referenceAddresses );
		
		List<Double> rmsds = new ArrayList<Double>( ensemble.size() );
		for( Protein structure : ensemble )
		{
			// skip the reference structure if needed
			if( structure == reference )
			{
				continue;
			}
			
			// remap the addresses to this structure
			List<AtomAddressInternal> internalAddresses = AddressMapper.mapAddressesToInternal( structure, readableAddresses );
			warnUnmappedAddresses( readableAddresses, internalAddresses );
			
			rmsds.add( RmsdCalculator.getRmsd( reference, structure, referenceAddresses, internalAddresses ) );
		}
		return rmsds;
	}
	
	public static List<Double> getRmsdsToMean( List<HasAtoms> ensemble, List<AtomAddressInternal> addresses )
	{
		// compute the RMSDs
		HasAtoms meanStructure = getMeanStructure( ensemble );
		List<Double> rmsds = new ArrayList<Double>( ensemble.size() );
		for( HasAtoms structure : ensemble )
		{
			rmsds.add( RmsdCalculator.getBackboneRmsd( meanStructure, structure ) );
		}
		return rmsds;
	}
	
	public static List<AlignmentScore> compareEnsembles( List<HasAtoms> referenceEnsemble, List<HasAtoms> compareEnsemble, List<AtomAddressInternal> referenceAddresses, List<AtomAddressInternal> compareAddresses )
	{
		if( referenceAddresses.size() != compareAddresses.size() )
		{
			throw new IllegalArgumentException( "Must have the same number of reference and compare addresses!" );
		}
		
		// copy all the proteins so we can move them without changing the ensembles
		List<HasAtoms> referenceEnsembleCopy = cloneEnsemble( referenceEnsemble );
		List<HasAtoms> compareEnsembleCopy = cloneEnsemble( compareEnsemble );
		
		// find the best alignment for each reference structure
		List<AlignmentScore> scores = new ArrayList<AlignmentScore>();
		for( int i=0; i<referenceEnsemble.size(); i++ )
		{
			HasAtoms referenceStructureCopy = referenceEnsembleCopy.get( i );
			ProteinGeometry.center( referenceStructureCopy, referenceAddresses );
			
			double bestRmsd = Double.POSITIVE_INFINITY;
			int bestIndex = -1;
			for( int j=0; j<compareEnsemble.size(); j++ )
			{
				HasAtoms compareStructureCopy = compareEnsembleCopy.get( j );
				StructureAligner.alignOptimallyByAtoms( referenceStructureCopy, compareStructureCopy, referenceAddresses, compareAddresses );
				double rmsd = RmsdCalculator.getRmsd( referenceStructureCopy, compareStructureCopy, referenceAddresses, compareAddresses );
				
				if( rmsd < bestRmsd )
				{
					bestRmsd = rmsd;
					bestIndex = j;
				}
			}
			
			scores.add( new AlignmentScore( i, bestIndex, bestRmsd ) );
		}
		return scores;
	}
	
	public static List<Protein> getSubEnsemble( List<Protein> ensemble, int startResidueNumber, int stopResidueNumber )
	{
		List<Protein> subEnsemble = new ArrayList<Protein>();
		for( Protein protein : ensemble )
		{
			Protein subProtein = new Protein();
			for( Subunit subunit : protein.getSubunits() )
			{
				subProtein.getSubunits().add( subunit.getFragmentByNumbers( startResidueNumber, stopResidueNumber ) );
			}
			subProtein.updateSubunitIndex();
			subProtein.updateAtomIndices();
			subEnsemble.add( subProtein );
			assert( subProtein.getSubunits().size() == protein.getSubunits().size() );
		}
		return subEnsemble;
	}
	
	public static List<HasAtoms> cluster( List<HasAtoms> ensemble, List<AtomAddressInternal> addresses, double minRmsd )
	{
		// compute the clustering
		DistanceMatrix distances = getDistanceMatrix( ensemble, addresses );
		DistanceClusterer clusterer = new DistanceClusterer();
		List<DistanceCluster> clusters = clusterer.cluster( distances, minRmsd );
		
		// project the clusters
		List<HasAtoms> out = new ArrayList<HasAtoms>();
		for( DistanceCluster cluster : clusters )
		{
			out.add( ensemble.get( cluster.getRepresentativeIndex() ) );
		}
		return out;
	}
	
	public static List<Double> getVdwEnergies( List<Protein> ensemble )
	throws IOException, XplorException
	{
		EnergyCalculator calc = new EnergyCalculator();
		List<Double> energies = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			energies.add( calc.getEnergy( protein ) );
		}
		return energies;
	}
	
	public static List<List<Double>> getDistanceRestraintViolations( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		return getDistanceRestraintViolations( ensemble, restraints, 0.0 );
	}
	
	public static List<List<Double>> getDistanceRestraintViolations( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		List<List<Double>> allViolations = new ArrayList<List<Double>>();
		for( Protein protein : ensemble )
		{
			allViolations.add( new ArrayList<Double>( DistanceRestraintCalculator.getViolations( protein, restraints, allowedViolation ).values() ) );
		}
		return allViolations;
	}
	
	public static List<Double> getDistanceRestraintMaxViolations( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		List<List<Double>> allViolations = getDistanceRestraintViolations( ensemble, restraints );
		List<Double> maxViolations = new ArrayList<Double>();
		for( List<Double> violations : allViolations )
		{
			if( violations.isEmpty() ) 
			{
				maxViolations.add( 0.0 );
			}
			else
			{
				maxViolations.add( Collections.max( violations ) );
			}
		}
		return maxViolations;
	}
	
	public static List<Integer> getDistanceRestraintNumViolations( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		List<List<Double>> allViolations = getDistanceRestraintViolations( ensemble, restraints, allowedViolation );
		List<Integer> numViolations = new ArrayList<Integer>();
		for( List<Double> violations : allViolations )
		{
			numViolations.add( violations.size() );
		}
		return numViolations;
	}
	
	public static List<Double> getDistanceRestraintRmsds( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		List<Double> rmsds = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			rmsds.add( DistanceRestraintCalculator.getRmsd( protein, restraints ) );
		}
		return rmsds;
	}
	
	public static List<Double> getRdcQFactors( List<Protein> ensemble, List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor )
	{
		List<Double> qFactors = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			qFactors.add( tensor.getQFactor( protein, rdcs ) );
		}
		return qFactors;
	}
	
	public static List<Double> getRdcQFactors( List<Protein> ensemble, List<Rdc<AtomAddressInternal>> rdcs )
	{
		List<Double> qFactors = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			
			qFactors.add( AlignmentTensor.compute( protein, rdcs ).getQFactor( protein, rdcs ) );
		}
		return qFactors;
	}
	
	public static List<List<Spike>> getClashes( List<Protein> ensemble )
	{
		List<List<Spike>> clashes = new ArrayList<List<Spike>>();
		for( Protein protein : ensemble )
		{
			clashes.add( ClashScore.getClashes( protein ) );
		}
		return clashes;
	}
	
	public static List<Integer> getNumClashes( List<List<Spike>> allClashes )
	{
		List<Integer> numClashes = new ArrayList<Integer>();
		for( List<Spike> clashes : allClashes )
		{
			numClashes.add( clashes.size() );
		}
		return numClashes;
	}
	
	public static List<Double> getMaxClashes( List<List<Spike>> allClashes )
	{
		List<Double> maxClashes = new ArrayList<Double>();
		for( List<Spike> clashes : allClashes )
		{
			double minGap = 0;
			for( Spike clash : clashes )
			{
				minGap = Math.min( minGap, clash.gap );
			}
			maxClashes.add( minGap );
		}
		return maxClashes;
	}
	
	public static List<Integer> getScalarCouplingNumViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		return getScalarCouplingNumViolations( ensemble, restraints, type, 0 );
	}
	
	public static List<Integer> getScalarCouplingNumViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type, double allowedViolation )
	{
		List<List<Double>> allViolations = getScalarCouplingViolationsHz( ensemble, restraints, type, allowedViolation );
		List<Integer> numViolations = new ArrayList<Integer>();
		for( List<Double> violations : allViolations )
		{
			numViolations.add( violations.size() );
		}
		return numViolations;
	}
	
	public static List<List<Double>> getScalarCouplingViolationsHz( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		return getScalarCouplingViolationsHz( ensemble, restraints, type, 0.0 );
	}
	
	public static List<List<Double>> getScalarCouplingViolationsHz( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type, double allowedViolation )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( type );
		List<List<Double>> allViolations = new ArrayList<List<Double>>();
		for( Protein protein : ensemble )
		{
			allViolations.add( new ArrayList<Double>( calc.getViolationsHz( protein, restraints, allowedViolation ).values() ) );
		}
		return allViolations;
	}
	
	public static List<Double> getScalarCouplingMaxViolationsHz( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		List<List<Double>> allViolations = getScalarCouplingViolationsHz( ensemble, restraints, type );
		List<Double> maxViolations = new ArrayList<Double>();
		for( List<Double> violations : allViolations )
		{
			if( violations.isEmpty() )
			{
				maxViolations.add( 0.0 );
			}
			else
			{
				maxViolations.add( Collections.max( violations ) );
			}
		}
		return maxViolations;
	}
	
	public static List<Double> getScalarCouplingRmsdsHz( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( type );
		List<Double> rmsds = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			rmsds.add( calc.getRmsdHz( protein, restraints ) );
		}
		return rmsds;
	}
	
	public static List<List<Double>> getScalarCouplingViolationsRadians( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		return getScalarCouplingViolationsRadians( ensemble, restraints, type, 0.0 );
	}
	
	public static List<List<Double>> getScalarCouplingViolationsRadians( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type, double allowedViolation )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( type );
		List<List<Double>> allViolations = new ArrayList<List<Double>>();
		for( Protein protein : ensemble )
		{
			allViolations.add( new ArrayList<Double>( calc.getViolationsRadians( protein, restraints, allowedViolation ).values() ) );
		}
		return allViolations;
	}
	
	public static List<Double> getScalarCouplingMaxViolationsRadians( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		List<List<Double>> allViolations = getScalarCouplingViolationsRadians( ensemble, restraints, type );
		List<Double> maxViolations = new ArrayList<Double>();
		for( List<Double> violations : allViolations )
		{
			if( violations.isEmpty() )
			{
				maxViolations.add( 0.0 );
			}
			else
			{
				maxViolations.add( Collections.max( violations ) );
			}
		}
		return maxViolations;
	}
	
	public static List<Double> getScalarCouplingRmsdsRadians( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, ScalarCouplingCalculator.Type type )
	{
		ScalarCouplingCalculator calc = new ScalarCouplingCalculator( type );
		List<Double> rmsds = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			rmsds.add( calc.getRmsdRadians( protein, restraints ) );
		}
		return rmsds;
	}
	
	public static List<List<Double>> getDihedralRestraintViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		return getDihedralRestraintViolations( ensemble, restraints, 0.0 );
	}
	
	public static List<List<Double>> getDihedralRestraintViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		List<List<Double>> allViolations = new ArrayList<List<Double>>();
		for( Protein protein : ensemble )
		{
			allViolations.add( new ArrayList<Double>( DihedralRestraintCalculator.getViolations( restraints, protein, allowedViolation ).values() ) );
		}
		return allViolations;
	}
	
	public static List<Integer> getDihedralRestraintNumViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		return getDihedralRestraintNumViolations( ensemble, restraints, 0 );
	}
	
	public static List<Integer> getDihedralRestraintNumViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints, double allowedViolation )
	{
		List<List<Double>> allViolations = getDihedralRestraintViolations( ensemble, restraints, allowedViolation );
		List<Integer> numViolations = new ArrayList<Integer>();
		for( List<Double> violations : allViolations )
		{
			numViolations.add( violations.size() );
		}
		return numViolations;
	}
	
	public static List<Double> getDihedralRestraintMaxViolations( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		List<List<Double>> allViolations = getDihedralRestraintViolations( ensemble, restraints );
		List<Double> maxViolations = new ArrayList<Double>();
		for( List<Double> violations : allViolations )
		{
			if( violations.isEmpty() )
			{
				maxViolations.add( 0.0 );
			}
			else
			{
				maxViolations.add( Collections.max( violations ) );
			}
		}
		return maxViolations;
	}
	
	public static List<Double> getDihedralRestraintRmsds( List<Protein> ensemble, List<DihedralRestraint<AtomAddressInternal>> restraints )
	{
		List<Double> rmsds = new ArrayList<Double>();
		for( Protein protein : ensemble )
		{
			rmsds.add( DihedralRestraintCalculator.getRmsd( restraints, protein ) );
		}
		return rmsds;
	}
	
	public static List<Map<RamaSatisfaction,Integer>> getRamaSatisfactionCounts( List<Protein> ensemble )
	{
		List<Map<RamaSatisfaction,Integer>> allCounts = new ArrayList<Map<RamaSatisfaction,Integer>>();
		for( Protein protein : ensemble )
		{
			// init the counts
			Map<RamaSatisfaction,Integer> counts = new TreeMap<RamaSatisfaction,Integer>();
			for( RamaSatisfaction satisfaction : RamaSatisfaction.values() )
			{
				counts.put( satisfaction, 0 );
			}
			allCounts.add( counts );
			
			// count the rama satisfaction
			for( Subunit subunit : protein.getSubunits() )
			{
				for( Residue residue : subunit.getResidues() )
				{
					// skip the first and last residues
					if( residue.getId() == 0 || residue.getId() == subunit.getResidues().size() - 1 )
					{
						continue;
					}
					
					RamaCase ramaCase = RamaCase.getCaseByNumber( subunit.getSequence(), residue.getNumber() );
					RamaSatisfaction residueSatisfaction = ramaCase.getSatisfaction(
						Math.toDegrees( subunit.getPhiAngleByNumber( residue.getNumber() ) ),
						Math.toDegrees( subunit.getPsiAngleByNumber( residue.getNumber() ) )
					);
					counts.put( residueSatisfaction, counts.get( residueSatisfaction ) + 1 ); 
				}
			}
		}
		return allCounts;
	}
	
	public static int getRamaResiduesCount( List<Protein> ensemble )
	{
		int count = 0;
		for( int numAngles : getRamaSatisfactionCounts( ensemble ).get( 0 ).values() )
		{
			count += numAngles;
		}
		return count;
	}
	
	public static List<Integer> getRamaFavoredCount( List<Protein> ensemble )
	{
		List<Integer> num = new ArrayList<Integer>();
		for( Map<RamaSatisfaction,Integer> counts : getRamaSatisfactionCounts( ensemble ) )
		{
			num.add( counts.get( RamaSatisfaction.Favored ) );
		}
		return num;
	}
	
	public static List<Integer> getRamaAllowedCount( List<Protein> ensemble )
	{
		List<Integer> num = new ArrayList<Integer>();
		for( Map<RamaSatisfaction,Integer> counts : getRamaSatisfactionCounts( ensemble ) )
		{
			num.add( counts.get( RamaSatisfaction.Allowed ) );
		}
		return num;
	}
	
	public static List<Integer> getRamaFavoredAndAllowedCount( List<Protein> ensemble )
	{
		List<Integer> num = new ArrayList<Integer>();
		for( Map<RamaSatisfaction,Integer> counts : getRamaSatisfactionCounts( ensemble ) )
		{
			num.add( counts.get( RamaSatisfaction.Favored ) + counts.get( RamaSatisfaction.Allowed ) );
		}
		return num;
	}
	
	public static List<Integer> getRamaDisallowedCount( List<Protein> ensemble )
	{
		List<Integer> num = new ArrayList<Integer>();
		for( Map<RamaSatisfaction,Integer> counts : getRamaSatisfactionCounts( ensemble ) )
		{
			num.add( counts.get( RamaSatisfaction.Disallowed ) );
		}
		return num;
	}
	
	public static List<Map<Integer,Integer>> getRamaDisallowedResidues( List<Protein> ensemble )
	{
		List<Map<Integer,Integer>> allResidueCounts = new ArrayList<Map<Integer,Integer>>();
		for( Protein protein : ensemble )
		{
			Map<Integer,Integer> residueCounts = new TreeMap<Integer,Integer>();
			for( Subunit subunit : protein.getSubunits() )
			{
				for( Residue residue : subunit.getResidues() )
				{
					// skip the first and last residues
					if( residue.getId() == 0 || residue.getId() == subunit.getResidues().size() - 1 )
					{
						continue;
					}
					
					RamaCase ramaCase = RamaCase.getCaseByNumber( subunit.getSequence(), residue.getNumber() );
					RamaSatisfaction residueSatisfaction = ramaCase.getSatisfaction(
						Math.toDegrees( subunit.getPhiAngleByNumber( residue.getNumber() ) ),
						Math.toDegrees( subunit.getPsiAngleByNumber( residue.getNumber() ) )
					);
					if( residueSatisfaction == RamaSatisfaction.Disallowed )
					{
						Integer currentCount = residueCounts.get( residue.getNumber() );
						residueCounts.put( residue.getNumber(), currentCount != null ? currentCount + 1 : 1 );
					}
				}
			}
			allResidueCounts.add( residueCounts );
		}
		return allResidueCounts;
	}
	
	public static List<Kinemage> getDistanceRestraintKinemages( List<Protein> ensemble, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		List<Kinemage> kins = new ArrayList<Kinemage>();
		int proteinId = 0;
		for( Protein protein : ensemble )
		{
			Kinemage kin = new Kinemage( "Protein " + (++proteinId) );
			KinemageBuilder.appendProtein( kin, protein, "Protein" );
			KinemageBuilder.appendDistanceRestraints( kin, protein, restraints, "Distance restraints" );
			kins.add( kin );
		}
		return kins;
	}
	
	public static List<Kinemage> getClashKinemages( List<Protein> ensemble )
	{
		List<Kinemage> kins = new ArrayList<Kinemage>();
		int proteinId = 0;
		for( Protein protein : ensemble )
		{
			// get the spike info
			Map<AddressPair,List<Spike>> spikes = ClashScore.getSpikes( protein );
			List<Spike> clashes = ClashScore.getClashes( spikes );
			
			// build the kinemage
			Kinemage kin = new Kinemage( "Protein " + (++proteinId) );
			KinemageBuilder.appendProtein( kin, protein, "Protein" );
			KinemageBuilder.appendClashes( kin, spikes, clashes, "Clashes", KinemageColor.Orange, 1 );
			kins.add( kin );
		}
		return kins;
	}
	
	private static void warnUnmappedAddresses( List<AtomAddressReadable> readableAddresses, List<AtomAddressInternal> internalAddresses )
	{
		assert( readableAddresses.size() == internalAddresses.size() );
		for( int i=0; i<readableAddresses.size(); i++ )
		{
			if( internalAddresses.get( i ) == null )
			{
				m_log.warn( "Unable to map address: " + readableAddresses.get( i ).toString() + "!" );
			}
		}
	}
}
