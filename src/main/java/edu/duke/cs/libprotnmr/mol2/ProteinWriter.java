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

package edu.duke.cs.libprotnmr.mol2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.atomType.AtomTypeMap;
import edu.duke.cs.libprotnmr.bond.Bond;
import edu.duke.cs.libprotnmr.bond.BondGraph;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class ProteinWriter
{
	/**************************
	 *   Methods
	 **************************/
	
	public void write( Protein protein, ArrayList<BondGraph> bondGraphs, String path )
	throws IOException
	{
		write( protein, bondGraphs, new File( path ) );
	}
	
	public void write( Protein protein, ArrayList<BondGraph> bondGraphs, File file )
	throws IOException
	{
		
		// open a file for writing
		FileWriter writer = new FileWriter( file );
		
		writeProtein( writer, protein, bondGraphs );
		
		// cleanup
		writer.close();
		writer = null;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void writeProtein( FileWriter writer, Protein protein, ArrayList<BondGraph> bondGraphs )
	throws IOException
	{
		writeMoleculeSegment( writer, protein, bondGraphs );
		TreeMap<AtomAddressInternal,Integer> atomIdMap = writeAtomSegment( writer, protein );
		writeBondSegment( writer, protein, bondGraphs, atomIdMap );
	}
	
	private void writeMoleculeSegment( FileWriter writer, Protein protein, ArrayList<BondGraph> bondGraphs )
	throws IOException
	{
		// write out the segment header
		writer.write( "@<TRIPOS>MOLECULE\n" );
		
		// molecule name
		writer.write( protein.getName() + "\n" );
		
		// get the total number of bonds
		int numBonds = 0;
		for( BondGraph bondGraph : bondGraphs )
		{
			numBonds += bondGraph.getBonds().size();
		}
		
		// number of atoms and bonds
		writer.write( protein.getNumAtoms() + " " + numBonds + "\n" );
		
		// molecule type
		writer.write( "PROTEIN\n" );
		
		// charge type
		writer.write( "NO_CHARGES\n" );
		
		// spacer
		writer.write( "\n" );
	}
	
	private TreeMap<AtomAddressInternal,Integer> writeAtomSegment( FileWriter writer, Protein protein )
	throws IOException
	{
		// get an atom type map
		AtomTypeMap atomTypeMap = AtomTypeMap.getInstance();
		
		// init our atom id map
		TreeMap<AtomAddressInternal,Integer> atomIdMap = new TreeMap<AtomAddressInternal,Integer>();
		int nextAtomId = 1;
		
		// write out the segment header
		writer.write( "@<TRIPOS>ATOM\n" );
		
		// for each subunit...
		for( Subunit subunit : protein.getSubunits() )
		{
			// for each residue...
			for( Residue residue : subunit.getResidues() )
			{
				// for each atom...
				for( Atom atom : residue.getAtoms() )
				{
					atomIdMap.put( new AtomAddressInternal( subunit, residue, atom ), nextAtomId );
					writeAtom( writer, subunit, residue, atom, nextAtomId, atomTypeMap );
					nextAtomId++;
				}
			}
		}
		
		// spacer
		writer.write( "\n" );
		
		return atomIdMap;
	}
	
	private void writeAtom( FileWriter writer, Subunit subunit, Residue residue, Atom atom, int nextAtomId, AtomTypeMap atomTypeMap )
	throws IOException
	{
		writer.write( Integer.toString( nextAtomId ) );
		writer.write( "\t" );
		writer.write( atom.getName() );
		writer.write( "\t" );
		writer.write( String.format( "%.3f", atom.getPosition().x ) );
		writer.write( "\t" );
		writer.write( String.format( "%.3f", atom.getPosition().y ) );
		writer.write( "\t" );
		writer.write( String.format( "%.3f", atom.getPosition().z ) );
		writer.write( "\t" );
		writer.write( atomTypeMap.getAtomType( subunit, residue, atom ).getCode() );
		writer.write( "\n" );
	}
	
	private void writeBondSegment( FileWriter writer, Protein protein, ArrayList<BondGraph> bondGraphs, TreeMap<AtomAddressInternal,Integer> atomIdMap )
	throws IOException
	{
		// write out the segment header
		writer.write( "@<TRIPOS>BOND\n" );
		
		int nextBondId = 1;
		
		// for each bond graph
		// NOTE: we assume the bond graphs are in the same order as the subunits
		Iterator<Subunit> iterSubunit = protein.getSubunits().iterator();
		for( BondGraph bondGraph : bondGraphs )
		{
			Subunit subunit = iterSubunit.next();
			
			for( Bond bond : bondGraph.getBonds() )
			{
				Atom leftAtom = subunit.getAtom( bond.getLeftAddress() );
				int leftAtomId = atomIdMap.get( new AtomAddressInternal( subunit.getId(), leftAtom.getResidueId(), leftAtom.getId() ) );
				Atom rightAtom = subunit.getAtom( bond.getRightAddress() );
				int rightAtomId = atomIdMap.get( new AtomAddressInternal( subunit.getId(), rightAtom.getResidueId(), rightAtom.getId() ) );
				
				writer.write( Integer.toString( nextBondId++ ) );
				writer.write( "\t" );
				writer.write( Integer.toString( leftAtomId ) );
				writer.write( "\t" );
				writer.write( Integer.toString( rightAtomId ) );
				writer.write( "\t" );
				writer.write( "un" ); // NOTE: we're leaving the bond types as unknown
				writer.write( "\n" );
			}
		}
		
		// spacer
		writer.write( "\n" );
	}
}
