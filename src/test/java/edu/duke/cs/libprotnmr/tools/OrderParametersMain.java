package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.analysis.StructureAligner;
import edu.duke.cs.libprotnmr.chart.ChartWriter;
import edu.duke.cs.libprotnmr.chart.Plotter;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.nmr.*;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.*;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;
import edu.duke.cs.libprotnmr.cgal.curves.Arrangement;
import edu.duke.cs.libprotnmr.cgal.curves.Intersector;
import edu.duke.cs.libprotnmr.cgal.curves.RdcCurve;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class OrderParametersMain
{
	private static final Logger m_log = LogManager.getLogger(OrderParametersMain.class);
	
	public static void main( String[] args )
	throws Exception
	{
		Logging.Normal.init();
		
		// settings
		final int ReferenceStructureId = 73;
		final BondType RdcType = BondType.NH;
		final int ResidueNumber = 45; // 12,28,45,61
		final int NumTensorSamples = 10000;
		
		// use these three RDC sets
		// NOTE: 23 and 26 have similar alignments
		final List<Integer> mediaIds = Arrays.asList( 19, 23, 28 );
		
		// read the RDC ensemble proteins
		m_log.info( "Reading proteins..." );
		List<Protein> proteins = new ProteinReader().readAll( new File( "input/2K39.pdb" ) );
		for( Protein protein : proteins )
		{
			NameMapper.ensureProtein( protein, NameScheme.New );
			chopOffCTerminalTail( protein, 70 );
		}
		//Subunit referenceStructure = proteins.get( ReferenceStructureId ).getSubunit( 0 );
		
		// read the reference structure protein
		Protein referenceProtein = new ProteinReader().read( new File( "input/1UBQ.pdb" ) );
		NameMapper.ensureProtein( referenceProtein, NameScheme.New );
		chopOffCTerminalTail( referenceProtein, 70 );
		Subunit referenceStructure = referenceProtein.getSubunit( 0 );
		
		if( true )
		{
			// align all the proteins to the reference structure
			ProteinGeometry.center( referenceStructure );
			for( Protein protein : proteins )
			{
				ProteinGeometry.center( protein );
				StructureAligner.alignOptimallyByAtoms( referenceStructure, protein, referenceStructure.backboneAtoms(), protein.backboneAtoms() );
			}
		}
		
		// show the aligned proteins
		if( false )
		{
			Kinemage kin = new Kinemage();
			
			for( int i=0; i<proteins.size(); i++ )
			{
				Subunit structure = proteins.get( i ).getSubunit( 0 );
				
				// show the backbone
				KinemageBuilder.appendBackbone( kin, structure,
					i == ReferenceStructureId ? "Reference" : "Structure " + i,
					i == ReferenceStructureId ? KinemageColor.Orange : KinemageColor.Grey,
					i == ReferenceStructureId ? 2 : 1
				);
				
				// show the bond vectors
				Vector3 bond = new Vector3();
				RdcType.getBondVectorByNumber( bond, structure, ResidueNumber );
				KinemageBuilder.appendVector( kin,
					bond,
					structure.getResidueByNumber( ResidueNumber ).getAtomByName( "N" ).getPosition(),
					RdcType.name() + " bond",
					i == ReferenceStructureId ? KinemageColor.Orange : KinemageColor.Lime,
					i == ReferenceStructureId ? 2 : 1,
					IdealGeometry.LengthNH
				);
			}
			
			new KinemageWriter().show( kin );
		}
		
		// read our RDCs
		m_log.info( "Reading RDCs..." );
		RdcsContext rdcsContext = getRdcs( referenceStructure, new File( "input/2K39.mr" ) );
		
		// find the best single structure
		if( false )
		{
			double bestQFactor = 1.00;
			int bestStructureId = -1;
			for( int i=0; i<proteins.size(); i++ )
			{
				double minQFactor = 1.00;
				for( AlignmentMedium medium : rdcsContext.media() )
				{
					List<Rdc<AtomAddressReadable>> readableRdcs = rdcsContext.getRdcs( medium, RdcType );
					List<Rdc<AtomAddressInternal>> internalRdcs = RdcMapper.mapReadableToInternal( proteins.get( i ), readableRdcs );
					AlignmentTensor tensor = AlignmentTensor.compute( proteins.get( i ), internalRdcs );
					minQFactor = Math.min( minQFactor, tensor.getQFactor( proteins.get( i ), internalRdcs ) );
				}
				if( minQFactor < bestQFactor )
				{
					bestQFactor = minQFactor;
					bestStructureId = i;
				}
			}
			m_log.info( "Best structure ID: " + bestStructureId );
			m_log.info( "Best Q-Factor: " + bestQFactor );
			return;
		}
		
		// show all the RDCs sorted by Q-Factor
		if( false )
		{
			m_log.info( rdcsContext.dumpToString( RdcType, referenceStructure ) );
			return;
		}
		
		// filter down to the selected RDCs
		int numMedia = rdcsContext.getNumMedia();
		for( int i=0; i<numMedia; i++ )
		{
			int key = i+1;
			if( !mediaIds.contains( key ) )
			{
				rdcsContext.removeRdcs( "Medium " + key );
			}
		}
		m_log.info( rdcsContext.dumpToString( RdcType, referenceStructure ) );
		
		// show RDC histograms
		if( false )
		{
			for( AlignmentMedium medium : rdcsContext.media() )
			{
				List<Rdc<AtomAddressReadable>> readableRdcs = rdcsContext.getRdcs( medium, RdcType );
				
				// calculate lots of tensors
				List<Rdc<AtomAddressInternal>> internalRdcs = RdcMapper.mapReadableToInternal(
					new Protein( referenceStructure ),
					readableRdcs
				);
				List<AlignmentTensor> tensors = AlignmentTensor.compute( referenceStructure, internalRdcs, NumTensorSamples, Rdc.SamplingModel.Gaussian );
				ChartWriter.show( Plotter.plotRdcHistogram(
					readableRdcs,
					medium.getTensor(),
					20,
					medium.getName() + " - Best Tensor"
				) );
				ChartWriter.show( Plotter.plotRdcHistogram(
					readableRdcs,
					tensors,
					20,
					medium.getName() + " - Sampled " + tensors.size() + " Tensors"
				) );
				ChartWriter.show( Plotter.plotRdcFit(
					internalRdcs,
					medium.getTensor(),
					referenceStructure,
					medium.getName() + " - Best Tensor"
				) );
			}
		}
		
		// refine the alignment tensors using RDC perturbation
		if( true )
		{
			for( AlignmentMedium medium : rdcsContext.media() )
			{
				final List<Rdc<AtomAddressReadable>> readableRdcs = rdcsContext.getRdcs( medium, RdcType );
				
				// calculate lots of tensors
				List<Rdc<AtomAddressInternal>> internalRdcs = RdcMapper.mapReadableToInternal(
					new Protein( referenceStructure ),
					readableRdcs
				);
				List<AlignmentTensor> tensors = AlignmentTensor.compute( referenceStructure, internalRdcs, NumTensorSamples, Rdc.SamplingModel.Gaussian );
				
				// pick the "best" tensor
				AlignmentTensor bestTensor = Collections.min( tensors, new Comparator<AlignmentTensor>( )
				{
					@Override
					public int compare( AlignmentTensor a, AlignmentTensor b )
					{
						// rank by degree of order
						//return Double.compare( Math.abs( a.getDzz() ), Math.abs( b.getDzz() ) );
						
						/* rank by eigenvalue spread
						return Double.compare(
							Math.abs( a.getDzz() - a.getDyy() ),
							Math.abs( b.getDzz() - b.getDyy() )
						);
						*/
						
						// rank by out-of-range RDCs
						return Double.compare(
							a.getOutOfRangeRdcs( readableRdcs ).size(),
							b.getOutOfRangeRdcs( readableRdcs ).size()
						);
					}
				} );
				
				if( false )
				{
					// show the RDC histograms
					ChartWriter.show( Plotter.plotRdcHistogram(
						readableRdcs,
						tensors,
						20,
						medium.getName() + " - Sampled " + tensors.size() + " Tensors"
					) );
					double rmsd = bestTensor.getRmsd( referenceStructure, internalRdcs );
					double qFactor = bestTensor.getQFactor( referenceStructure, internalRdcs );
					ChartWriter.show( Plotter.plotRdcHistogram(
						readableRdcs,
						bestTensor,
						20,
						medium.getName() + String.format( " - Best Tensor (RMSD: %.2f, Q: %.2f)", rmsd, qFactor )
					) );
				}
				// update the medium
				medium.setTensor( bestTensor );
			}
		}
		
		// check for out-of-range RDCs
		for( AlignmentMedium medium : rdcsContext.media() )
		{
			int numOutOfRangeRdcs = medium.getTensor().getOutOfRangeRdcs( rdcsContext.getRdcs( medium, RdcType ) ).size();
			if( numOutOfRangeRdcs > 0 )
			{
				m_log.warn( "Medium " + medium.getName() + " has " + numOutOfRangeRdcs + " out-of-range RDCs!" );
			}
		}
		
		// show all pair-wise tensor differences
		List<AlignmentMedium> media = new ArrayList<AlignmentMedium>( rdcsContext.media() );
		for( int i=0; i<media.size(); i++ )
		{
			for( int j=0; j<i; j++ )
			{
				AlignmentMedium mediumA = media.get( i );
				AlignmentMedium mediumB = media.get( j );
				AlignmentTensor tensorA = media.get( i ).getTensor();
				AlignmentTensor tensorB = media.get( j ).getTensor();
				double dist = tensorA.getDistance( tensorB );
				m_log.info( "Tensor distance: " + mediumA.getName() + ", " + mediumB.getName() + ": " + dist );
			}
		}
		
		// TEMP
		if( false )
		{
			RdcCurve rdcCurveA = new RdcCurve( media.get( 1 ).getTensor(), rdcsContext.getRdc( media.get( 1 ), ResidueNumber, RdcType ).getValue(), 0 );
			RdcCurve rdcCurveB = new RdcCurve( media.get( 2 ).getTensor(), rdcsContext.getRdc( media.get( 2 ), ResidueNumber, RdcType ).getValue(), 1 );
			Intersector.getIntersectionPoints( rdcCurveA, rdcCurveB );
			return;
		}
		
		// show the RDC curves
		if( true )
		{
			Kinemage kin = new Kinemage( "Residue " + ResidueNumber );
			KinemageBuilder.appendAxes( kin, 1, 0.3 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			
			// show the actual bond vectors
			for( int i=0; i<proteins.size(); i++ )
			{
				// skip the reference structure for now
				if( i == ReferenceStructureId )
				{
					continue;
				}
				
				Subunit structure = proteins.get( i ).getSubunit( 0 );
				Vector3 bond = new Vector3();
				RdcType.getBondVectorByNumber( bond, structure, ResidueNumber );
				//KinemageBuilder.appendVector( kin, bond, RdcType.name() + " bond", KinemageColor.Lime, 1, 1.0 );
				KinemageBuilder.appendPoint( kin, bond, RdcType.name() + " bond", KinemageColor.Lime, 4 );
			}
			
			// now show the reference Structure
			Vector3 bond = new Vector3();
			RdcType.getBondVectorByNumber( bond, referenceStructure, ResidueNumber );
			KinemageBuilder.appendVector( kin, bond, RdcType.name() + " bond", KinemageColor.Orange, 2, 1.0 );
			KinemageBuilder.appendPoint( kin, bond, RdcType.name() + " bond", KinemageColor.Orange, 7 );
			
			// compute the arrangement of the RDC curves
			Arrangement arrangement = new Arrangement();
			for( AlignmentMedium medium : rdcsContext.media() )
			{
				Rdc<AtomAddressReadable> rdc = rdcsContext.getRdc( medium, ResidueNumber, RdcType );
				
				if( medium.getTensor().isRdcInRange( rdc.getValue() ) )
				{
					// build the RDC curves
					for( int arcnum=0; arcnum<RdcCurve.NumArcs; arcnum++ )
					{
						RdcCurve curve = new RdcCurve( medium.getTensor(), rdc.getValue(), arcnum );
						arrangement.addCurve( curve );
						//KinemageBuilder.appendCurve( kin, curve, medium.getName(), KinemageColor.Cobalt, 2 );
					}
				}
				else
				{
					m_log.warn( "RDC value out of tensor range for medium " + medium.getName() + "!" );
				}
			}
			KinemageBuilder.appendDetailedArrangement( kin, arrangement, "Arrangement", KinemageColor.Cobalt, 1 );
			
			KinemageBuilder.appendAxialView( kin, 1, bond, "Bond" );
			
			// show the kinemage
			new KinemageWriter().show( kin );
		}
	}
	
	private static void chopOffCTerminalTail( Protein protein, int lastIncludedResidueNumber )
	{
		// cut off everything after residue 70
		for( Subunit subunit : protein.getSubunits() )
		{
			int lastIncludedResidueId = subunit.getResidueId( lastIncludedResidueNumber );
			for( int i=subunit.getResidues().size() - 1; i>lastIncludedResidueId; i-- )
			{
				subunit.getResidues().remove( i );
			}
			subunit.updateResidueIndex();
		}
		protein.updateAtomIndices();
	}

	private static RdcsContext getRdcs( Subunit structure, File inFile )
	throws Exception
	{
		// split the mr file into segments and parse RDCs for each alignment medium
		
		RdcsContext rdcsContext = new RdcsContext( structure.getSequence() );
		
		// start our buffers
		StringBuilder buf = null;
		int number = 0;
		
		// start reading line-by-line
		BufferedReader in = new BufferedReader( new FileReader( inFile ) );
		String line = null;
		while( ( line = in.readLine() ) != null )
		{
			// look for lines like this:
			// ! Dipolar couplings from rdc_all36_methyl_expXXX.
			// where XXX is a number from 1 to 36
			final String tag = "! Dipolar couplings from rdc_all36_methyl_exp";
			if( line.length() > tag.length() && line.substring( 0, tag.length() ).equals( tag ) )
			{
				// is there an old buffer to close?
				if( buf != null )
				{
					parseRdcs( rdcsContext, structure, buf, number );
				}
				
				// start a new buffer
				buf = new StringBuilder();
				number = Integer.parseInt( line.substring( tag.length() ).replace( ".", "" ) );
			}
			else if( buf != null )
			{
				// add the line to the open buffer
				buf.append( line );
				buf.append( "\n" );
			}
		}
		in.close();
		
		// any buffers left to close?
		parseRdcs( rdcsContext, structure, buf, number );
		
		return rdcsContext;
	}
	
	private static void parseRdcs( RdcsContext rdcsContext, Subunit structure, StringBuilder buf, int number )
	throws Exception
	{
		// parse the RDCs and pick out only the NH RDCs
		List<Rdc<AtomAddressReadable>> rdcs = new RdcReader().read( new ByteArrayInputStream( buf.toString().getBytes( "UTF-8" ) ) );
		NameMapper.ensureAddresses( new Protein( structure ).getSequences(), rdcs, NameScheme.New );
		List<Rdc<AtomAddressReadable>> nhRdcs = new ArrayList<Rdc<AtomAddressReadable>>();
		for( Rdc<AtomAddressReadable> rdc : rdcs )
		{
			if( BondType.lookup( rdc ) == BondType.NH )
			{
				// wipe out the subunit names
				rdc.getFrom().omitSubunitName();
				rdc.getTo().omitSubunitName();
				
				nhRdcs.add( rdc );
			}
		}
		
		// compute an alignment tensor
		List<Rdc<AtomAddressInternal>> internalNhRdcs = RdcMapper.mapReadableToInternal( new Protein( structure ), nhRdcs );
		AlignmentTensor tensor = AlignmentTensor.compute( structure, internalNhRdcs );
		
		// add the RDCs to the context
		rdcsContext.addRdcs( new AlignmentMedium( "Medium " + number, tensor ), nhRdcs );
	}
}
