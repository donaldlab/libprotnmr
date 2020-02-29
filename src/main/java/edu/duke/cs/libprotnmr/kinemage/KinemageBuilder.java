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
package edu.duke.cs.libprotnmr.kinemage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.duke.cs.libprotnmr.analysis.DihedralRestraintCalculator;
import edu.duke.cs.libprotnmr.analysis.ClashScore.AddressPair;
import edu.duke.cs.libprotnmr.analysis.ClashScore.Spike;
import edu.duke.cs.libprotnmr.bond.Bond;
import edu.duke.cs.libprotnmr.bond.BondGraph;
import edu.duke.cs.libprotnmr.bond.BondGraphBuilder;
import edu.duke.cs.libprotnmr.bond.BreadthFirstBondIterator;
import edu.duke.cs.libprotnmr.geom.GeodesicGrid;
import edu.duke.cs.libprotnmr.geom.Line3;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.Assignment;
import edu.duke.cs.libprotnmr.nmr.DihedralRestraint;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Dipeptide;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Peptide;
import edu.duke.cs.libprotnmr.protein.PeptidePlane;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class KinemageBuilder
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final Vector3 DefaultPosition = new Vector3();
	private static final KinemageColor DefaultColor = KinemageColor.LightGrey;
	private static final int DefaultWidth = 1;
	private static final double DefaultLength = 1.0;
	private static final boolean DefaultShowMarker = false;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void appendLine( Kinemage kinemage, Vector3 source, Vector3 target )
	{
		appendLine( kinemage, source, target, "Line", DefaultColor, DefaultWidth );
	}
	
	public static void appendLine( Kinemage kinemage, Line3 line, String name, KinemageColor color, int width )
	{
		appendLine( kinemage, line.start, line.stop, name, color, width );
	}
	
	public static void appendLine( Kinemage kinemage, Vector3 source, Vector3 target, String name, KinemageColor color, int width )
	{
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		List list = new List( "vector", "line" );
		list.addOption( "width= " + width );
		group.addNode( list );
		
		// add the points
		Point point = new Point( "source", new Vector3( source ) );
		list.addNode( point );
		point = new Point( "target", new Vector3( target ) );
		point.addColor( color );
		list.addNode( point );
	}
	
	public static void appendOrientations( Kinemage kinemage, Iterable<Vector3> orientations )
	{
		appendOrientations( kinemage, orientations, "Orientations", DefaultColor, DefaultWidth, DefaultLength );
	}
	
	public static void appendOrientations( Kinemage kinemage, Iterable<Vector3> orientations, String name, KinemageColor color, int width, double length  )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		// add the source ball
		Vector3 origin = Vector3.getOrigin();
		List list = new List( "ball", "source" );
		group.addNode( list );
		Point point = new Point( "source", origin );
		point.addColor( color );
		point.addOption( "r= " + Double.toString( length / 40.0 ) );
		list.addNode( point );
		
		// add each orientation
		for( Vector3 orientation : orientations )
		{
			// compute the target position
			Vector3 target = new Vector3( orientation );
			target.normalize();
			target.scale( length );
			
			// add the direction line
			list = new List( "vector", "line" );
			list.addOption( "width= " + width );
			group.addNode( list );
			point = new Point( "source", origin );
			list.addNode( point );
			point = new Point( "target", new Vector3( target ) );
			point.addColor( color );
			list.addNode( point );
			
			// add the target ball
			list = new List( "dot", "target" );
			group.addNode( list );
			point = new Point( "target", target );
			point.addColor( color );
			point.addOption( "width= 7" );
			list.addNode( point );
		}
	}
	
	public static void appendVector( Kinemage kinemage, Vector3 orientation )
	{
		appendVector( kinemage, orientation, DefaultPosition, "Vector", DefaultColor, DefaultWidth, DefaultLength );
	}
	
	public static void appendVector( Kinemage kinemage, Vector3 orientation, Vector3 position )
	{
		appendVector( kinemage, orientation, position, "Vector", DefaultColor, DefaultWidth, DefaultLength );
	}
	
	public static void appendVector( Kinemage kinemage, Vector3 orientation, String name, KinemageColor color, int width, double length )
	{
		appendVector( kinemage, orientation, DefaultPosition, name, color, width, length );
	}
	
	public static void appendVector( Kinemage kinemage, Vector3 orientation, Vector3 position, String name, KinemageColor color, int width, double length )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		// compute the target position
		Vector3 target = new Vector3( orientation );
		target.scale( length );
		target.add( position );
		
		// add the direction line
		List list = new List( "vector", "line" );
		list.addOption( "width= " + width );
		group.addNode( list );
		Point point = new Point( "source", new Vector3( position ) );
		list.addNode( point );
		point = new Point( "target", new Vector3( target ) );
		point.addColor( color );
		list.addNode( point );
		
		// add the source marker
		list = new List( "dot", "source" );
		group.addNode( list );
		point = new Point( "source", position );
		point.addColor( color );
		list.addOption( "width= 7" );
		list.addNode( point );
	}
	
	public static void appendAxes( Kinemage kinemage )
	{
		appendAxes( kinemage, DefaultWidth, DefaultLength );
	}
	
	public static void appendAxes( Kinemage kinemage, int width, double length )
	{
		appendAxes( kinemage, "Axes", Vector3.getUnitX(), Vector3.getUnitY(), Vector3.getUnitZ(), width, length );
	}
	
	public static void appendAxes( Kinemage kinemage, Vector3 x, Vector3 y, Vector3 z )
	{
		appendAxes( kinemage, "Axes", x, y, z, DefaultWidth, DefaultLength );
	}
	
	public static void appendAxes( Kinemage kinemage, String name, Vector3 x, Vector3 y, Vector3 z, int width, double length )
	{
		Point point = null;
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		List list = new List( "vector", "Axes" );
		list.addOption( "width= " + width );
		group.addNode( list );
		
		// add the x-axis
		point = new Point( "leftx", Vector3.getOrigin() );
		list.addNode( point );
		x = new Vector3( x );
		x.scale( length );
		point = new Point( "rightx", x );
		point.addColor( KinemageColor.Red );
		list.addNode( point );
		
		// add the y-axis
		point = new Point( "lefty", Vector3.getOrigin() );
		point.addOption( "P" );
		list.addNode( point );
		y = new Vector3( y );
		y.scale( length );
		point = new Point( "righty", y );
		point.addColor( KinemageColor.Green );
		list.addNode( point );
		
		// add the z-axis
		point = new Point( "leftz", Vector3.getOrigin() );
		point.addOption( "P" );
		list.addNode( point );
		z = new Vector3( z );
		z.scale( length );
		point = new Point( "rightz", z );
		point.addColor( KinemageColor.Blue );
		list.addNode( point );
	}
	
	public static void appendAxes( Kinemage kinemage, Matrix3 basis, String name )
	{
		appendAxes( kinemage, basis, name, DefaultWidth, DefaultLength );
	}
	
	public static void appendAxes( Kinemage kinemage, Matrix3 basis, String name, int width, double length )
	{
		// get the axes from the matrix
		Vector3 xaxis = new Vector3();
		basis.getXAxis( xaxis );
		Vector3 yaxis = new Vector3();
		basis.getYAxis( yaxis );
		Vector3 zaxis = new Vector3();
		basis.getZAxis( zaxis );
		appendAxes( kinemage, name, xaxis, yaxis, zaxis, width, length );
	}
	
	public static void appendAxes( Kinemage kinemage, AlignmentTensor tensor, String name )
	{
		appendAxes( kinemage, tensor, name, DefaultWidth, DefaultLength );
	}
	
	public static void appendAxes( Kinemage kinemage, AlignmentTensor tensor, String name, int width, double length )
	{
		// get the axes from the tensor
		Vector3 xaxis = new Vector3( tensor.getXAxis() );
		Vector3 yaxis = new Vector3( tensor.getYAxis() );
		Vector3 zaxis = new Vector3( tensor.getZAxis() );
		appendAxes( kinemage, name, xaxis, yaxis, zaxis, width, length );
	}
	
	public static void appendProtein( Kinemage kinemage, Subunit subunit )
	{
		appendProtein( kinemage, new Protein( subunit ), "Protein", 3 );
	}
	
	public static void appendProtein( Kinemage kinemage, Protein protein )
	{
		appendProtein( kinemage, protein, "Protein", 3 );
	}
	
	public static void appendProtein( Kinemage kinemage, Protein protein, String name )
	{
		appendProtein( kinemage, protein, name, 3 );
	}
	
	public static void appendProtein( Kinemage kinemage, Protein protein, String name, int width )
	{
		// load the bond graphs
		ArrayList<BondGraph> bondGraphs = BondGraphBuilder.getInstance().build( protein );
		
		// list the backbone atoms
		Group proteinGroup = new Group( name );
		kinemage.getRoot().addNode( proteinGroup );
		
		// for each subunit
		for( Subunit subunit : protein.getSubunits() )
		{
			BondGraph bondGraph = bondGraphs.get( subunit.getId() );
			KinemageColor color = KinemageColor.values()[subunit.getId() % KinemageColor.values().length];
			
			// get a group for the subunit
			Subgroup subunitGroup = new Subgroup( "Subunit " + subunit.getName() );
			proteinGroup.addNode( subunitGroup );
			
			// add the backbone chain
			List backboneChain = new List( "vector", "Backbone Chain" );
			backboneChain.addOption( "color= " + color );
			backboneChain.addOption( "width= " + width );
			backboneChain.addOption( "master= {Backbone}" );
			subunitGroup.addNode( backboneChain );
			for( Residue residue : subunit.getResidues() )
			{
				// get the backbone atoms in order
				Atom atomN = residue.getAtomByName( "N" );
				Atom atomCa = residue.getAtomByName( "CA" );
				Atom atomC = residue.getAtomByName( "C" );
				
				// skip empty residues
				if( atomN == null || atomCa == null || atomC == null )
				{
					continue;
				}
				
				String nameN = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomN.getId() ) ).toString();
				String nameCa = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomCa.getId() ) ).toString();
				String nameC = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomC.getId() ) ).toString();
				
				backboneChain.addNode( new Point( nameN, atomN.getPosition() ) );
				backboneChain.addNode( new Point( nameCa, atomCa.getPosition() ) );
				backboneChain.addNode( new Point( nameC, atomC.getPosition() ) );
			}
			
			// prep heavy atoms list
			List heavyAtoms = new List( "vector", "Heavy Atoms" );
			heavyAtoms.addColor( color );
			heavyAtoms.addOption( "width= " + width );
			heavyAtoms.addOption( "off" );
			heavyAtoms.addOption( "master= {Heavy Atoms}" );
			subunitGroup.addNode( heavyAtoms );
			
			// prep hydrogen atoms list
			List hydrogenAtoms = new List( "vector", "Hydrogens" );
			hydrogenAtoms.addColor( color );
			hydrogenAtoms.addOption( "width= " + width );
			hydrogenAtoms.addOption( "off" );
			hydrogenAtoms.addOption( "master= {Hydrogens}" );
			subunitGroup.addNode( hydrogenAtoms );
			
			// prep pseudoatoms list
			List pseudoatoms = new List( "vector", "Pseudoatoms" );
			pseudoatoms.addColor( color );
			pseudoatoms.addOption( "width= " + width );
			pseudoatoms.addOption( "off" );
			pseudoatoms.addOption( "master= {Pseudoatoms}" );
			subunitGroup.addNode( pseudoatoms );
			
			// find the first heavy atom in the protein
			AtomAddressInternal firstAddress = null;
			for( AtomAddressInternal address : subunit.atoms() )
			{
				if( !isHydrogen( protein, address ) )
				{
					firstAddress = address;
					break;
				}
			}
			
			// heavy atoms (do BFS in the bond graph)
			BreadthFirstBondIterator iterBond = new BreadthFirstBondIterator( bondGraph, firstAddress );
			while( iterBond.hasNext() )
			{
				ArrayList<Bond> bonds = iterBond.next();
				for( Bond bond : bonds )
				{
					// add hydrogens and pseudoatoms to separate lists
					if( isPseudoatom( protein, bond.getLeftAddress() ) || isPseudoatom( protein, bond.getRightAddress() ) )
					{
						addBond( protein, pseudoatoms, bond );
					}
					else if( isHydrogen( protein, bond.getLeftAddress() ) || isHydrogen( protein, bond.getRightAddress() ) )
					{
						addBond( protein, hydrogenAtoms, bond );
					}
					else
					{
						addBond( protein, heavyAtoms, bond );
					}
				}
			}
		}
	}
	
	public static void appendBackbone( Kinemage kinemage, Subunit subunit )
	{
		appendBackbone( kinemage, subunit, "Subunit Backbone", DefaultColor, DefaultWidth );
	}
	
	public static void appendBackbone( Kinemage kinemage, Protein protein )
	{
		appendBackbone( kinemage, protein, "Backbone", DefaultColor, DefaultWidth );
	}
	
	public static void appendBackbone( Kinemage kinemage, Subunit subunit, String name, KinemageColor color, int width )
	{
		appendBackbone( kinemage, new Protein( new Subunit( subunit ) ), name, color, width );
	}
	
	public static void appendBackbone( Kinemage kinemage, Protein protein, String name, KinemageColor color, int width )
	{
		appendBackbone( kinemage, protein, name, Arrays.asList( color ), width );
	}
	
	public static void appendBackbone( Kinemage kinemage, Protein protein, String name, java.util.List<KinemageColor> colors, int width )
	{
		// list the backbone atoms
		Group backboneGroup = new Group( name );
		backboneGroup.addOption( "dominant" );
		kinemage.getRoot().addNode( backboneGroup );
		
		// for each subunit
		for( Subunit subunit : protein.getSubunits() )
		{
			// get the color for this subunit
			KinemageColor subunitColor = colors.get( 0 );
			if( subunit.getId() < colors.size() )
			{
				subunitColor = colors.get( subunit.getId() );
			}
			
			// add the backbone chain
			String subunitName = "Subunit " + subunit.getName();
			List subunitList = new List( "vector", subunitName );
			subunitList.addColor( subunitColor );
			subunitList.addOption( "width= " + width );
			subunitList.addOption( "master= {" + subunitName + "}" );
			backboneGroup.addNode( subunitList );
			for( Residue residue : subunit.getResidues() )
			{
				// get the backbone atoms in order
				Atom atomN = residue.getAtomByName( "N" );
				Atom atomCa = residue.getAtomByName( "CA" );
				Atom atomC = residue.getAtomByName( "C" );
				
				// skip empty residues
				if( atomN == null || atomCa == null || atomC == null )
				{
					continue;
				}
				
				String nameN = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomN.getId() ) ).toString();
				String nameCa = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomCa.getId() ) ).toString();
				String nameC = AddressMapper.mapAddress( protein, new AtomAddressInternal( subunit.getId(), residue.getId(), atomC.getId() ) ).toString();
				
				subunitList.addNode( new Point( nameN, atomN.getPosition() ) );
				subunitList.addNode( new Point( nameCa, atomCa.getPosition() ) );
				subunitList.addNode( new Point( nameC, atomC.getPosition() ) );
			}
		}
	}
	
	public static void appendDistanceRestraints( Kinemage kinemage, Protein protein, Iterable<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		appendDistanceRestraints( kinemage, protein, restraints, "Distance Restraints" );
	}
	
	public static void appendDistanceRestraints( Kinemage kinemage, Protein protein, Iterable<DistanceRestraint<AtomAddressInternal>> restraints, String name )
	{
		appendDistanceRestraints( kinemage, protein, restraints, name, 0.5 );
	}
	
	public static void appendDistanceRestraints( Kinemage kinemage, final Protein protein, Iterable<DistanceRestraint<AtomAddressInternal>> restraints, String name, double badViolationThreshold )
	{
		appendDistanceRestraints( kinemage, protein, restraints, name, badViolationThreshold, false );
	}
	
	public static void appendDistanceRestraints( Kinemage kinemage, final Protein protein, Iterable<DistanceRestraint<AtomAddressInternal>> restraints, String name, double badViolationThreshold, boolean expandAmbiguousRestraints )
	{
		// add the distance restraints as vectors
		Group group = new Group( name );
		kinemage.getRoot().addNode( group );
		
		// sort the restraints by min violation
		ArrayList<DistanceRestraint<AtomAddressInternal>> sortedRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			sortedRestraints.add( restraint );
		}
		Collections.sort( sortedRestraints, new Comparator<DistanceRestraint<AtomAddressInternal>>( )
		{
			@Override
			public int compare( DistanceRestraint<AtomAddressInternal> a, DistanceRestraint<AtomAddressInternal> b )
			{
				// sort in descending order
				return Double.compare(
					DistanceRestraint.getMinViolation( b, protein ),
					DistanceRestraint.getMinViolation( a, protein )
				);
			}
		} );
		
		for( DistanceRestraint<AtomAddressInternal> restraint : sortedRestraints )
		{
			Group subgroup = group;
			if( restraint.isAmbiguous() )
			{
				if( expandAmbiguousRestraints )
				{
					// add a subgroup for the assignments
					subgroup = new Group( "Ambiguous restraint" );
					group.addNode( subgroup );
					
					for( Assignment<AtomAddressInternal> assignment : restraint )
					{
						subgroup.addNode( getDistanceRestraintAssignment( protein, restraint, assignment, badViolationThreshold ) );
					}
				}
				else
				{
					// for unexpanded ambiguous assignments, pick the smallest violation
					java.util.List<Assignment<AtomAddressInternal>> minAssignments = DistanceRestraint.getMinViolationAssignments( restraint, protein );
					if( minAssignments.size() == 1 )
					{
						group.addNode( getDistanceRestraintAssignment( protein, restraint, minAssignments.get( 0 ), badViolationThreshold ) );
					}
					{
						// if there are multiple satisfying assignments, show all of them
						for( Assignment<AtomAddressInternal> assignment : minAssignments )
						{
							// UNDONE: make these visually different from the single assignments?
							group.addNode( getDistanceRestraintAssignment( protein, restraint, assignment, badViolationThreshold ) );
						}
					}
				}
			}
			else
			{
				group.addNode( getDistanceRestraint( protein, restraint, badViolationThreshold ) );
			}
		}
	}
	
	public static void appendRdcs( Kinemage kin, final Protein protein, Iterable<Rdc<AtomAddressInternal>> rdcs, final AlignmentTensor tensor, String name )
	{
		appendRdcs( kin, protein, rdcs, tensor, name, 0.2 );
	}
	
	public static void appendRdcs( Kinemage kin, final Protein protein, Iterable<Rdc<AtomAddressInternal>> rdcs, final AlignmentTensor tensor, String name, double badViolationThresholdHz )
	{
		// add the distance restraints as vectors
		Group group = new Group( name );
		kin.getRoot().addNode( group );
		
		// sort the restraints by min violation
		ArrayList<Rdc<AtomAddressInternal>> sortedRdcs = new ArrayList<Rdc<AtomAddressInternal>>();
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			sortedRdcs.add( rdc );
		}
		Collections.sort( sortedRdcs, new Comparator<Rdc<AtomAddressInternal>>( )
		{
			@Override
			public int compare( Rdc<AtomAddressInternal> a, Rdc<AtomAddressInternal> b )
			{
				// add a subgroup for the assignments
				return Double.compare(
					tensor.getViolation( protein, b ),
					tensor.getViolation( protein, a )
				);
			}
		} );
		
		for( Rdc<AtomAddressInternal> rdc : sortedRdcs )
		{
			Group subgroup = group;
			for( Assignment<AtomAddressInternal> assignment : rdc )
			{
				// is the restraint satisfied?
				Atom leftAtom = protein.getAtom( assignment.getLeft() );
				Atom rightAtom = protein.getAtom( assignment.getRight() );
				assert( leftAtom != null ) : "Unable to find atom: " + assignment.getLeft();
				assert( rightAtom != null ) : "Unable to find atom: " + assignment.getRight();
				
				double violation = tensor.getViolation( protein, rdc );
				boolean isSatisfied = tensor.isSatisfied( protein, rdc );
				
				// build the restraint label
				String label = isSatisfied ? "Satisfied" : String.format( "Violation: %.2f", violation ); 
				List list = new List( "vector", label );
				list.addOption( "width= 3" );
				list.addColor( isSatisfied ? KinemageColor.SatisfiedNoe : KinemageColor.UnsatisfiedNoe );
				list.addOption( String.format( "master= {%s}", isSatisfied ? "Satisfied Restraints" : "Violated Restraints" ) );
				if( !isSatisfied )
				{
					list.addOption( String.format( "master= {Violations %s %.1fHz}", violation > badViolationThresholdHz ? ">" : "<=", badViolationThresholdHz ) );
				}
				subgroup.addNode( list );
				
				list.addNode( new Point(
					AddressMapper.mapAddress( protein, assignment.getLeft() ).toString(),
					protein.getAtom( assignment.getLeft() ).getPosition()
				) );
				list.addNode( new Point(
					AddressMapper.mapAddress( protein, assignment.getRight() ).toString(),
					protein.getAtom( assignment.getRight() ).getPosition()
				) );
			}
		}
	}
	
	public static void appendDihedralRestraints( Kinemage kin, final Protein protein, Iterable<DihedralRestraint<AtomAddressInternal>> restraints, String name )
	{
		appendDihedralRestraints( kin, protein, restraints, name, Math.toRadians( 5.0 ) );
	}
	
	public static void appendDihedralRestraints( Kinemage kin, final Protein protein, Iterable<DihedralRestraint<AtomAddressInternal>> restraints, String name, double badViolationThresholdRadians )
	{
		// add the distance restraints as vectors
		Group group = new Group( name );
		kin.getRoot().addNode( group );
		
		// sort the restraints by min violation
		ArrayList<DihedralRestraint<AtomAddressInternal>> sortedRestraints = new ArrayList<DihedralRestraint<AtomAddressInternal>>();
		for( DihedralRestraint<AtomAddressInternal> restraint : restraints )
		{
			sortedRestraints.add( restraint );
		}
		Collections.sort( sortedRestraints, new Comparator<DihedralRestraint<AtomAddressInternal>>( )
		{
			@Override
			public int compare( DihedralRestraint<AtomAddressInternal> a, DihedralRestraint<AtomAddressInternal> b )
			{
				// sort in descending order
				return Double.compare(
					DihedralRestraintCalculator.getViolation( a, protein ),
					DihedralRestraintCalculator.getViolation( b, protein )
				);
			}
		} );
		
		for( DihedralRestraint<AtomAddressInternal> restraint : sortedRestraints )
		{
			// is the restraint satisfied?
			double violation = DihedralRestraintCalculator.getViolation( restraint, protein );
			boolean isSatisfied = DihedralRestraintCalculator.isSatisfied( restraint, protein );
			
			// build the restraint label
			String label = isSatisfied ? "Satisfied" : String.format( "Violation: %.1f", Math.toDegrees( violation ) ); 
			List list = new List( "vector", label );
			list.addOption( "width= 3" );
			list.addColor( isSatisfied ? KinemageColor.SatisfiedNoe : KinemageColor.UnsatisfiedNoe );
			list.addOption( String.format( "master= {%s}", isSatisfied ? "Satisfied Restraints" : "Violated Restraints" ) );
			if( !isSatisfied )
			{
				list.addOption( String.format( "master= {Violations %s %.1fDeg}", violation > badViolationThresholdRadians ? ">" : "<=", Math.toDegrees( badViolationThresholdRadians ) ) );
			}
			group.addNode( list );
			
			// draw the three bonds
			list.addNode( new Point(
				AddressMapper.mapAddress( protein, restraint.getB() ).toString(),
				protein.getAtom( restraint.getB() ).getPosition()
			) );
			list.addNode( new Point(
				AddressMapper.mapAddress( protein, restraint.getC() ).toString(),
				protein.getAtom( restraint.getC() ).getPosition()
			) );
		}
	}
	
	public static void appendPoint( Kinemage kin, Vector3 point, String name, KinemageColor color, int width )
	{
		appendPoints( kin, Arrays.asList( point ), name, color, width );
	}
	
	public static void appendPoints( Kinemage kinemage, Iterable<Vector3> points )
	{
		appendPoints( kinemage, points, "Points", DefaultColor, 7 );
	}
	
	public static void appendPoints( Kinemage kinemage, Iterable<Vector3> points, String name )
	{
		appendPoints( kinemage, points, name, DefaultColor, 7 );
	}
	
	public static void appendPoints( Kinemage kinemage, Iterable<Vector3> points, String name, KinemageColor color, int width )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		for( Vector3 position : points )
		{
			// add the point
			List list = new List( "dot", "source" );
			group.addNode( list );
			Point point = new Point( "dot", new Vector3( position ) );
			point.addColor( color );
			list.addOption( "width= " + Integer.toString( width ) );
			list.addNode( point );
		}
	}
	
	public static void appendChain( Kinemage kinemage, java.util.List<Vector3> points )
	{
		appendChain( kinemage, points, false, "Chain", DefaultColor, DefaultWidth );
	}
	
	public static void appendChain( Kinemage kinemage, java.util.List<Vector3> points, boolean isClosed )
	{
		appendChain( kinemage, points, isClosed, "Chain", DefaultColor, DefaultWidth );
	}
	
	public static void appendChain( Kinemage kinemage, java.util.List<Vector3> points, boolean isClosed, String name )
	{
		appendChain( kinemage, points, isClosed, name, DefaultColor, DefaultWidth );
	}
	
	public static void appendChain( Kinemage kinemage, java.util.List<Vector3> points, boolean isClosed, String name, KinemageColor color, int width )
	{
		appendChain( kinemage, points, isClosed, DefaultShowMarker , name, color, width );
	}
	
	public static void appendChain( Kinemage kinemage, java.util.List<Vector3> points, boolean isClosed, boolean showMarker, String name, KinemageColor color, int width )
	{
		if( points.isEmpty() )
		{
			return;
		}
		
		kinemage.getRoot().addNode( getChain( points, isClosed, showMarker, name, color, width ) );
	}
	
	public static Group getChain( java.util.List<Vector3> points, boolean isClosed, boolean showMarker, String name, KinemageColor color, int width )
	{
		Group group = new Group( name );
		group.addOption( "dominant" );
		
		// add the backbone chain
		List list = new List( "vector", name );
		group.addNode( list );
		list.addColor( color );
		list.addOption( "width= " + Integer.toString( width ) );
		for( Vector3 v : points )
		{
			// if for some reason we get null points, just skip them
			if( v == null )
			{
				continue;
			}
			
			list.addNode( new Point( v ) );
		}
		
		// close the chain if needed
		if( isClosed )
		{
			list.addNode( new Point( points.get( 0 ) ) );
		}
		
		// show the marker if needed
		if( showMarker )
		{
			group.addNode( getMarker( points, name, color, width ) );
		}
		
		return group;
	}
	
	public static void appendDashedChain( Kinemage kin, Iterable<Vector3> points, String name, KinemageColor color, int width )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kin.getRoot().addNode( group );
		
		// add the dashes
		List list = null;
		for( Vector3 v : points )
		{
			if( list == null )
			{
				list = new List( "vector", name );
				group.addNode( list );
				list.addColor( color );
				list.addOption( "width= " + Integer.toString( width ) );
				list.addNode( new Point( v ) );
			}
			else
			{
				list.addNode( new Point( v ) );
				list = null;
			}
		}
	}
	
	public static void appendTriangleStrip( Kinemage kinemage, Iterable<Vector3> points, boolean isClosed, String name, KinemageColor color )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		// add the triangles
		List list = new List( "triangle", name );
		list.addColor( color );
		//list.addOption( "alpha= " + String.format( "%.1f", alpha ) );
		group.addNode( list );
		for( Vector3 v : points )
		{
			list.addNode( new Point( v ) );
		}
		
		// close the strip if needed
		Iterator<Vector3> iter = points.iterator();
		if( isClosed && iter.hasNext() )
		{
			list.addNode( new Point( iter.next() ) );
			if( iter.hasNext() )
			{
				list.addNode( new Point( iter.next() ) );
			}
		}
	}
	
	public static void appendUnitSphere( Kinemage kinemage )
	{
		appendUnitSphere( kinemage, DefaultColor );
	}
	
	public static void appendUnitSphere( Kinemage kinemage, KinemageColor color )
	{
		// get the unit sphere outline
		java.util.List<Vector3> circle1 = new ArrayList<Vector3>();
		java.util.List<Vector3> circle2 = new ArrayList<Vector3>();
		java.util.List<Vector3> circle3 = new ArrayList<Vector3>();
		final int NumCircleSamples = 50;
		for( int i=0; i<=NumCircleSamples; i++ )
		{
			// compute the phase and amplitudes
			double phase = (double)i / (double)NumCircleSamples * 2.0 * Math.PI;
			double cos = Math.cos( phase );
			double sin = Math.sin( phase );
			
			// add the points to the chains
			circle1.add( new Vector3( cos, sin, 0.0 ) );
			circle2.add( new Vector3( 0.0, cos, sin ) );
			circle3.add( new Vector3( sin, 0.0, cos ) );
		}
		
		// render a kinemage
		KinemageBuilder.appendChain( kinemage, circle1, true, "Unit Sphere (xy)", color, 1 );
		KinemageBuilder.appendChain( kinemage, circle2, true, "Unit Sphere (yz)", color, 1 );
		KinemageBuilder.appendChain( kinemage, circle3, true, "Unit Sphere (xz)", color, 1 );
	}
	
	public static void appendPeptide( Kinemage kin, Peptide peptide )
	{
		// add the group
		Group group = new Group( "Peptide" );
		group.addOption( "dominant" );
		kin.getRoot().addNode( group );
		
		// add the bonds
		List list = new List( "vector", "Backbone" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptide.getH() ) );
		list.addNode( new Point( peptide.getN() ) );
		list.addNode( new Point( peptide.getCa() ) );
		list.addNode( new Point( peptide.getC() ) );
		list.addNode( new Point( peptide.getNn() ) );
		list.addNode( new Point( peptide.getHn() ) );
		
		list = new List( "vector", "CaHa" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptide.getCa() ) );
		list.addNode( new Point( peptide.getHa() ) );
		
		list = new List( "vector", "Carbonyl" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptide.getC() ) );
		list.addNode( new Point( peptide.getO() ) );
		
		// add balls for the oxygen and nitrogen atoms
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Red );
		group.addNode( list );
		list.addNode( new Point( peptide.getO() ) );
		
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Blue );
		group.addNode( list );
		list.addNode( new Point( peptide.getN() ) );
		list.addNode( new Point( peptide.getNn() ) );
	}
	
	public static void appendDipeptide( Kinemage kin, Dipeptide dipeptide )
	{
		// copy all the atom positions
		Vector3 can = new Vector3( dipeptide.getNwardsCaAtom() );
		Vector3 cn = new Vector3( dipeptide.getNwardsCAtom() );
		Vector3 on = new Vector3( dipeptide.getNwardsOAtom() );
		Vector3 nn = new Vector3( dipeptide.getNwardsNAtom() );
		Vector3 hn = new Vector3( dipeptide.getNwardsHAtom() );
		Vector3 ca = new Vector3( dipeptide.getCaAtom() );
		Vector3 ha = new Vector3( dipeptide.getHaAtom() );
		Vector3 cc = new Vector3( dipeptide.getCwardsCAtom() );
		Vector3 oc = new Vector3( dipeptide.getCwardsOAtom() );
		Vector3 nc = new Vector3( dipeptide.getCwardsNAtom() );
		Vector3 hc = new Vector3( dipeptide.getCwardsHAtom() );
		Vector3 cac = new Vector3( dipeptide.getCwardsCaAtom() );
		
		// add the group
		Group group = new Group( "Dipeptide" );
		group.addOption( "dominant" );
		kin.getRoot().addNode( group );
		
		// add the bonds
		List list = new List( "vector", "Backbone" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( can ) );
		list.addNode( new Point( cn ) );
		list.addNode( new Point( nn ) );
		list.addNode( new Point( ca ) );
		list.addNode( new Point( cc ) );
		list.addNode( new Point( nc ) );
		list.addNode( new Point( cac ) );
		
		list = new List( "vector", "CaHa" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( ca ) );
		list.addNode( new Point( ha ) );
		
		list = new List( "vector", "NH" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( nn ) );
		list.addNode( new Point( hn ) );
		
		list = new List( "vector", "NH" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( nc ) );
		list.addNode( new Point( hc ) );
		
		list = new List( "vector", "Carbonyl" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( cn ) );
		list.addNode( new Point( on ) );
		
		list = new List( "vector", "Carbonyl" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( cc ) );
		list.addNode( new Point( oc ) );
		
		// add balls for the oxygen and nitrogen atoms
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Red );
		group.addNode( list );
		list.addNode( new Point( on ) );
		list.addNode( new Point( oc ) );
		
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Blue );
		group.addNode( list );
		list.addNode( new Point( nn ) );
		list.addNode( new Point( nc ) );
		
		// add another group for the plane indicators
		java.util.List<Vector3> outline = new ArrayList<Vector3>();
		final double DashLength = 0.1;
		outline.addAll( getLineDashes( ca, on, DashLength ) );
		outline.addAll( getLineDashes( on, can, DashLength ) );
		outline.addAll( getLineDashes( can, hn, DashLength ) );
		outline.addAll( getLineDashes( hn, ca, DashLength ) );
		outline.addAll( getLineDashes( ca, oc, DashLength ) );
		outline.addAll( getLineDashes( oc, cac, DashLength ) );
		outline.addAll( getLineDashes( cac, hc, DashLength ) );
		outline.addAll( getLineDashes( hc, ca, DashLength ) );
		appendDashedChain( kin, outline, "Dipeptide Planes", KinemageColor.Green, 1 );
	}

	public static void appendPeptidePlane( Kinemage kin, PeptidePlane peptidePlane )
	{
		// add the group
		Group group = new Group( "Peptide Plane" );
		group.addOption( "dominant" );
		kin.getRoot().addNode( group );
		
		// add the bonds
		List list = new List( "vector", "Backbone" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptidePlane.getCapAtom() ) );
		list.addNode( new Point( peptidePlane.getCAtom() ) );
		list.addNode( new Point( peptidePlane.getNAtom() ) );
		list.addNode( new Point( peptidePlane.getCanAtom() ) );

		list = new List( "vector", "NH" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptidePlane.getNAtom() ) );
		list.addNode( new Point( peptidePlane.getHAtom() ) );
		
		list = new List( "vector", "Carbonyl" );
		list.addColor( KinemageColor.LightGrey );
		group.addNode( list );
		list.addNode( new Point( peptidePlane.getCAtom() ) );
		list.addNode( new Point( peptidePlane.getOAtom() ) );
		
		// add balls for the oxygen and nitrogen atoms
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Red );
		group.addNode( list );
		list.addNode( new Point( peptidePlane.getOAtom() ) );
		
		list = new List( "ball", "Balls" );
		list.addColor( KinemageColor.Blue );
		group.addNode( list );
		list.addNode( new Point( peptidePlane.getNAtom() ) );
	}
	
	public static void appendGeodesicGridAsPolygon( Kinemage kinemage, GeodesicGrid grid, String name, KinemageColor color )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		// add the triangles
		for( GeodesicGrid.Face face : grid )
		{
			List list = new List( "triangle", name );
			list.addColor( color );
			group.addNode( list );
			for( Vector3 v : face.vertices() )
			{
				list.addNode( new Point( v ) );
			}
		}
	}
	
	public static void appendGeodesicGridAsPolygon( Kinemage kinemage, GeodesicGrid grid, String name, KinemageColor colorA, KinemageColor colorB )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		// do BFS on the grid faces and alternate colors at each depth
		Set<GeodesicGrid.Face> visitedFaces = new HashSet<GeodesicGrid.Face>();
		Map<GeodesicGrid.Face,Integer> depths = new HashMap<GeodesicGrid.Face,Integer>();
		Deque<GeodesicGrid.Face> queue = new ArrayDeque<GeodesicGrid.Face>();
		GeodesicGrid.Face firstFace = grid.iterator().next();
		queue.add( firstFace );
		depths.put( firstFace, 0 );
		while( !queue.isEmpty() )
		{
			// visit this face
			GeodesicGrid.Face face = queue.poll();
			visitedFaces.add( face );
			int depth = depths.get( face );
			
			// get the face color
			KinemageColor color = depth % 2 == 0 ? colorA : colorB;
			if( depth == 0 )
			{
				color = KinemageColor.Rose;
			}
			
			// add the triangle for this face
			List list = new List( "triangle", name );
			list.addColor( color );
			group.addNode( list );
			for( Vector3 v : face.vertices() )
			{
				list.addNode( new Point( v ) );
			}
			
			// add the unvisited neighbor faces to the queue (if not there already)
			for( GeodesicGrid.Face neighbor : face.neighbors() )
			{
				// NOTE: queue.contains is slow!!!
				if( !visitedFaces.contains( neighbor ) && !queue.contains( neighbor ) )
				{
					queue.add( neighbor );
					depths.put( neighbor, depth + 1 );
				}
			}
		}
	}
	
	public static void appendGeodesicGrid( Kinemage kinemage, Iterable<GeodesicGrid.Face> faces, String name, KinemageColor color, int width )
	{
		// add the group
		Group group = new Group( name );
		group.addOption( "dominant" );
		kinemage.getRoot().addNode( group );
		
		String widthOption = "width= " + Integer.toString( width );
		
		final double StepSize = Math.toRadians( 0.5 );
		
		// draw each edge once
		Set<GeodesicGrid.Edge> usedEdges = new HashSet<GeodesicGrid.Edge>();
		for( GeodesicGrid.Face face : faces )
		{
			for( GeodesicGrid.Edge edge : face.edges() )
			{
				// did we draw this edge already?
				if( usedEdges.contains( edge ) )
				{
					continue;
				}
				usedEdges.add( edge );
				
				// set up the king list
				List list = new List( "vector", name );
				group.addNode( list );
				list.addColor( color );
				list.addOption( widthOption );
				
				// how many points should we sample?
				// find the nearest whole number of angular steps between the two points
				double angle = Math.acos( edge.getLeft().getDot( edge.getRight() ) );
				int numPoints = Math.max( (int)( angle / StepSize + 0.5 ), 2 );
				
				// sample points from the geodesic arc
				for( int i=0; i<numPoints; i++ )
				{
					Vector3 v = new Vector3( edge.getLeft() );
					v.subtract( edge.getRight() );
					v.scale( (double)i / (double)( numPoints - 1 ) );
					v.add( edge.getRight() );
					v.normalize();
					
					list.addNode( new Point( v ) );
				}
			}
		}
	}
	
	public static void appendClashes( Kinemage kin, Map<AddressPair,java.util.List<Spike>> spikes, String name, KinemageColor color, int width )
	{
		appendClashes( kin, spikes, null, name, color, width );
	}
	
	public static void appendClashes( Kinemage kin, Map<AddressPair,java.util.List<Spike>> spikes, java.util.List<Spike> clashes, String name, KinemageColor color, int width )
	{
		// add the group
		Group group = new Group( name );
		kin.getRoot().addNode( group );
		
		if( clashes != null )
		{
			// add the spike with clash info
			for( Spike clash : clashes )
			{
				Group subgroup = getSpikesGroup( clash.addresses, spikes.get( clash.addresses ), color, width );
				subgroup.setName( String.format( "%.2f ", clash.gap ) + subgroup.getName() );
				group.addNode( subgroup );
			}
		}
		else
		{
			// add the spikes without clash info
			for( Map.Entry<AddressPair,java.util.List<Spike>> entry : spikes.entrySet() )
			{
				group.addNode( getSpikesGroup( entry.getKey(), entry.getValue(), color, width ) );
			}
		}
	}
	
	public static Node getMarker( java.util.List<Vector3> samples, String name, KinemageColor color, int width )
	{
		// determine the position and direction of the marker
		Vector3 direction = null;
		Vector3 position = null;
		if( samples.size() % 2 == 0 )
		{
			int index = ( samples.size() - 1 ) / 2;
			Vector3 start = samples.get( index );
			Vector3 stop = samples.get( index + 1 );
			direction = new Vector3( stop );
			direction.subtract( start );
			direction.normalize();
			position = new Vector3( start );
			position.add( stop );
			position.scale( 0.5 );
		}
		else
		{
			int index = samples.size() / 2;
			Vector3 start = samples.get( index );
			Vector3 stop = samples.get( index + 1 );
			direction = new Vector3( stop );
			direction.subtract( start );
			direction.normalize();
			position = new Vector3( start );
		}
		
		// compute the marker points
		Vector3 a = new Vector3( position );
		Vector3 b = new Vector3( position );
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, direction, Math.toRadians( 1.0 ) );
		q.rotate( a );
		Quaternion.getRotation( q, direction, Math.toRadians( -1.0 ) );
		q.rotate( b );
		Vector3 translation = new Vector3( direction );
		translation.scale( -0.02 );
		a.add( translation );
		b.add( translation );
		
		// build the marker
		List marker = new List( "vector", name );
		marker.addColor( color );
		marker.addOption( "width= " + Integer.toString( width ) );
		marker.addNode( new Point( a ) );
		marker.addNode( new Point( position ) );
		marker.addNode( new Point( b ) );
		return marker;
	}
	
	public static void appendDefaultView( Kinemage kin, int viewNum )
	{
		kin.getRoot().addNode( new Node( "@" + viewNum + "viewid {Default View}" ) );
		kin.getRoot().addNode( new Node( "@" + viewNum + "center 0 0 0" ) );
		kin.getRoot().addNode( new Node( "@" + viewNum + "matrix 1 0 0 0 1 0 0 0 1" ) );
		kin.getRoot().addNode( new Node( "@" + viewNum + "zslab 1000" ) );
	}
	
	public static void appendAxialView( Kinemage kin, int viewNum, Vector3 axis, String name )
	{
		Matrix3 rot = new Matrix3();
		axis = new Vector3( axis );
		axis.normalize();
		Matrix3.getArbitraryBasisFromZ( rot, axis );
		appendView( kin, viewNum, rot, name );
	}
	
	public static void appendAxialView( Kinemage kin, int viewNum, Vector3 axis, Vector3 up, String name )
	{
		Matrix3 rot = new Matrix3();
		axis = new Vector3( axis );
		axis.normalize();
		Matrix3.getRightBasisFromYZ( rot, up, axis );
		appendView( kin, viewNum, rot, name );
	}
	
	public static void appendView( Kinemage kin, int viewNum, Matrix3 rot, String name )
	{
		kin.getRoot().addNode( new Node( "@" + viewNum + "viewid {" + name + "}" ) );
		kin.getRoot().addNode( new Node( "@" + viewNum + "center 0 0 0" ) );
		
		StringBuilder buf = new StringBuilder();
		for( int r=0; r<3; r++ )
		{
			for( int c=0; c<3; c++ )
			{
				buf.append( String.format( " %.8f", rot.data[r][c] ) );
			}
		}
		kin.getRoot().addNode( new Node( "@" + viewNum + "matrix" + buf.toString() ) );
		kin.getRoot().addNode( new Node( "@" + viewNum + "zslab 1000" ) );
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static boolean isHydrogen( HasAtoms protein, AtomAddressInternal address )
	{
		return protein.getAtom( address ).getElement() == Element.Hydrogen;
	}
	
	private static boolean isPseudoatom( HasAtoms protein, AtomAddressInternal address )
	{
		return protein.getAtom( address ).isPseudoatom();
	}
	
	private static void addBond( Protein protein, List list, Bond bond )
	{
		// get the left atom
		Point leftPoint = new Point(
			AddressMapper.mapAddress( protein, bond.getLeftAddress() ).toString(),
			protein.getAtom( bond.getLeftAddress() ).getPosition()
		);
		leftPoint.addOption( "P" );
		list.addNode( leftPoint );
		
		// get the right atom
		Point rightPoint = new Point(
			AddressMapper.mapAddress( protein, bond.getRightAddress() ).toString(),
			protein.getAtom( bond.getRightAddress() ).getPosition()
		);
		list.addNode( rightPoint );
	}
	
	private static java.util.List<Vector3> getLineDashes( Vector3 source, Vector3 target, double dashLength )
	{
		// do linear interpolation
		Vector3 direction = new Vector3( target );
		direction.subtract( source );
		int numSamples = Math.max( 2, (int)( direction.getLength() / dashLength ) );
		ArrayList<Vector3> dashes = new ArrayList<Vector3>();
		for( int i=0; i<numSamples; i++ )
		{
			double t = (double)i / (double)( numSamples - 1 );
			Vector3 sample = new Vector3( direction );
			sample.scale( t );
			sample.add( source );
			dashes.add( sample );
		}
		return dashes;
	}
	
	private static Group getSpikesGroup( AddressPair addresses, java.util.List<Spike> spikes, KinemageColor color, int width )
	{
		String widthOption = "width= " + Integer.toString( width );
		
		// add a subgroup for the spikes for these addresses
		Group subgroup = new Group( addresses.toString() );
		subgroup.addOption( "dominant" );
		
		for( Spike spike : spikes )
		{
			// add the spike
			List list = new List( "vector", "" );
			list.addColor( color );
			list.addOption( widthOption );
			subgroup.addNode( list );
			
			list.addNode( new Point( spike.line.start ) );
			list.addNode( new Point( spike.line.stop ) );
		}
		
		return subgroup;
	}
	
	private static List getDistanceRestraint( Protein protein, DistanceRestraint<AtomAddressInternal> restraint, double badViolationThreshold )
	{
		return getDistanceRestraintAssignment( protein, restraint, restraint.iterator().next(), badViolationThreshold );
	}
	
	private static List getDistanceRestraintAssignment( Protein protein, DistanceRestraint<AtomAddressInternal> restraint, Assignment<AtomAddressInternal> assignment, double badViolationThreshold )
	{
		// is the restraint satisfied?
		Atom leftAtom = protein.getAtom( assignment.getLeft() );
		Atom rightAtom = protein.getAtom( assignment.getRight() );
		assert( leftAtom != null ) : "Unable to find atom: " + assignment.getLeft();
		assert( rightAtom != null ) : "Unable to find atom: " + assignment.getRight();
		
		double dist = leftAtom.getPosition().getDistance( rightAtom.getPosition() );
		double violation = restraint.getViolation( dist );
		boolean isSatisfied = restraint.isSatisfied( dist );
		
		// build the restraint label
		String label = isSatisfied ? "Satisfied" : String.format( "Violation: %.2f", violation ); 
		List list = new List( "vector", label );
		list.addOption( "width= 3" );
		list.addColor( isSatisfied ? KinemageColor.SatisfiedNoe : KinemageColor.UnsatisfiedNoe );
		list.addOption( String.format( "master= {%s}", isSatisfied ? "Satisfied Restraints" : "Violated Restraints" ) );
		if( !isSatisfied )
		{
			list.addOption( String.format( "master= {Violations %s %.1fA}", violation > badViolationThreshold ? ">" : "<=", badViolationThreshold ) );
		}
		
		list.addNode( new Point(
			AddressMapper.mapAddress( protein, assignment.getLeft() ).toString(),
			protein.getAtom( assignment.getLeft() ).getPosition()
		) );
		list.addNode( new Point(
			AddressMapper.mapAddress( protein, assignment.getRight() ).toString(),
			protein.getAtom( assignment.getRight() ).getPosition()
		) );
		return list;
	}
}

