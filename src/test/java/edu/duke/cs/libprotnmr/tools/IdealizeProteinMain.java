package edu.duke.cs.libprotnmr.vis;

import edu.duke.cs.libprotnmr.analysis.StructureAligner;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.pdb.ProteinWriter;
import edu.duke.cs.libprotnmr.protein.*;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IdealizeProteinMain
{
	public static void main( String[] args )
	throws Exception
	{
		Logging.Normal.init();
		
		// read in the structure
		Protein protein = new ProteinReader().read( new File( "input/2KIQ.pdb" ) );
		NameMapper.ensureProtein( protein, NameScheme.New );
		Protein idealProtein = idealizeProtein( protein );
		
		// DEBUG
		if( true )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendBackbone( kin, protein, "Original", KinemageColor.Grey, 1 );
			KinemageBuilder.appendBackbone( kin, idealProtein, "Idealized", KinemageColor.Green, 1 );
			new KinemageWriter().show( kin );
		}
		
		// write out the idealized protein
		new ProteinWriter().write( idealProtein, new File( "output/idealized.pdb" ) );
	}
	
	public static Protein idealizeProtein( Protein protein )
	{
		Protein idealProtein = new Protein( protein );
		for( int i=0; i<protein.getSubunits().size(); i++ )
		{
			idealProtein.getSubunits().set( i, idealizeSubunit( idealProtein.getSubunits().get( i ) ) );
		}
		return idealProtein;
	}
	
	public static Subunit idealizeSubunit( Subunit subunit )
	{
		Subunit idealSubunit = new Subunit( subunit );
		
		// build an ideal peptide plane that matches the orientation of the first real peptide plane
		PeptidePlane firstRealPlane = PeptidePlane.newFromAfterResidue( subunit, 0 );
		Vector3 centroid = ProteinGeometry.getCentroid( firstRealPlane );
		centroid.negate();
		ProteinGeometry.translate( firstRealPlane, centroid );
		PeptidePlane firstIdealPlane = new PeptidePlane();
		StructureAligner.alignOptimally( firstRealPlane, firstIdealPlane );
		centroid.negate();
		ProteinGeometry.translate( firstRealPlane, centroid );
		ProteinGeometry.translate( firstIdealPlane, centroid );
		
		// build the first residue
		Dipeptide dipeptide = new Dipeptide();
		dipeptide.setCwardsPlane( firstRealPlane );
		dipeptide.updateNwardsPlane( 0, 0 );
		copyAtoms( idealSubunit.getResidue( 0 ), dipeptide );
		
		// handle the rest of the residues
		for( int i=1; i<subunit.getResidues().size()-1; i++ )
		{
			dipeptide.setNwardsPlane( dipeptide.getCwardsPlane() );
			dipeptide.updateCwardsPlane( subunit.getPhiAngle( i ), subunit.getPsiAngle( i ) );
			copyAtoms( idealSubunit.getResidue( i ), dipeptide );
		}
		
		// handle the C terminus
		int ctermId = subunit.getResidues().size() - 1;
		dipeptide.setNwardsPlane( dipeptide.getCwardsPlane() );
		dipeptide.updateCwardsPlane( subunit.getPhiAngle( ctermId ), 0 );
		copyAtoms( idealSubunit.getResidue( ctermId ), dipeptide );
		
		idealSubunit.updateAtomIndices();
		
		return idealSubunit;
	}
	
	private static void copyAtoms( Residue residue, Dipeptide dipeptide )
	{
		// build a list of the new atoms
		List<Atom> atoms = new ArrayList<Atom>();
		switch( residue.getAminoAcid() )
		{
			case Proline:
				// no amide proton
				atoms.add( copyAtom( residue, "HA", dipeptide.getHaAtom() ) );
			break;
			
			case Glycine:
				atoms.add( copyAtom( residue, "H", dipeptide.getNwardsHAtom() ) );
				atoms.add( copyAtom( residue, "HA1", dipeptide.getHaAtom() ) );
			break;
			
			default:
				atoms.add( copyAtom( residue, "H", dipeptide.getNwardsHAtom() ) );
				atoms.add( copyAtom( residue, "HA", dipeptide.getHaAtom() ) );
		}
		
		// same for all amino acids
		atoms.add( copyAtom( residue, "N", dipeptide.getNwardsNAtom() ) );
		atoms.add( copyAtom( residue, "CA", dipeptide.getCaAtom() ) );
		atoms.add( copyAtom( residue, "C", dipeptide.getCwardsCAtom() ) );
		atoms.add( copyAtom( residue, "O", dipeptide.getCwardsOAtom() ) );
		
		// keep only the sepcified atoms
		// NOTE: we don't care about sidechains right now
		residue.getAtoms().clear();
		residue.getAtoms().addAll( atoms );
	}
	
	private static Atom copyAtom( Residue residue, String name, Vector3 pos )
	{
		Atom atom = new Atom( residue.getAtomByName( name ) );
		atom.getPosition().set( pos );
		return atom;
	}
}
