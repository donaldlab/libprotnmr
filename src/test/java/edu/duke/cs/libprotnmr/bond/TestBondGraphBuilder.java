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
package edu.duke.cs.libprotnmr.bond;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class TestBondGraphBuilder extends ExtendedTestCase
{
	public void testGlycineChain( )
	throws Exception
	{
		// add residues and atoms
		Subunit subunit = new Subunit();
		subunit.addResidue( newResidue( 1, AminoAcid.Glycine, "H1,H2,H3,N,CA,HA2,HA3,C,O" ) );
		subunit.addResidue( newResidue( 2, AminoAcid.Glycine, "H,N,CA,HA2,HA3,C,O" ) );
		subunit.addResidue( newResidue( 3, AminoAcid.Glycine, "H,N,CA,HA2,HA3,C,O,OXT" ) );
		
		// make the bond graph
		BondGraphBuilder builder = BondGraphBuilder.getInstance();
		BondGraph bondGraph = builder.build( subunit );
		
		// check N terminus
		assertAtomBonds( bondGraph, subunit, "1:H1", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:H2", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:H3", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:N", "1:H1,1:H2,1:H3,1:CA" );
		assertAtomBonds( bondGraph, subunit, "1:CA", "1:N,1:HA2,1:HA3,1:C" );
		assertAtomBonds( bondGraph, subunit, "1:HA2", "1:CA" );
		assertAtomBonds( bondGraph, subunit, "1:HA3", "1:CA" );
		assertAtomBonds( bondGraph, subunit, "1:C", "1:CA,1:O,2:N" );
		assertAtomBonds( bondGraph, subunit, "1:O", "1:C" );
		
		// check middle residue
		assertAtomBonds( bondGraph, subunit, "2:H", "2:N" );
		assertAtomBonds( bondGraph, subunit, "2:N", "2:H,2:CA,1:C" );
		assertAtomBonds( bondGraph, subunit, "2:CA", "2:N,2:HA2,2:HA3,2:C" );
		assertAtomBonds( bondGraph, subunit, "2:HA2", "2:CA" );
		assertAtomBonds( bondGraph, subunit, "2:HA3", "2:CA" );
		assertAtomBonds( bondGraph, subunit, "2:C", "2:CA,2:O,3:N" );
		assertAtomBonds( bondGraph, subunit, "2:O", "2:C" );
		
		// check C terminus
		assertAtomBonds( bondGraph, subunit, "3:H", "3:N" );
		assertAtomBonds( bondGraph, subunit, "3:N", "3:H,3:CA,2:C" );
		assertAtomBonds( bondGraph, subunit, "3:CA", "3:N,3:HA2,3:HA3,3:C" );
		assertAtomBonds( bondGraph, subunit, "3:HA2", "3:CA" );
		assertAtomBonds( bondGraph, subunit, "3:HA3", "3:CA" );
		assertAtomBonds( bondGraph, subunit, "3:C", "3:CA,3:O,3:OXT" );
		assertAtomBonds( bondGraph, subunit, "3:O", "3:C" );
		assertAtomBonds( bondGraph, subunit, "3:OXT", "3:C" );
	}
	
	public void testPolyChain( )
	throws Exception
	{
		// make a small protein
		Subunit subunit = new Subunit();
		subunit.addResidue( newResidue( 1, AminoAcid.Alanine, "H1,H2,H3,N,CA,HA,C,O,CB,HB1,HB2,HB3" ) );
		subunit.addResidue( newResidue( 2, AminoAcid.GlutamicAcid, "H,N,CA,HA,C,O,CB,HB2,HB3,CG,HG2,HG3,CD,OE1,OE2" ) );
		subunit.addResidue( newResidue( 3, AminoAcid.Asparagine, "H,N,CA,HA,C,O,OXT,CB,HB2,HB3,CG,OD1,ND2,HD21,HD22" ) );
		
		// make the bond graph
		BondGraphBuilder builder = BondGraphBuilder.getInstance();
		BondGraph bondGraph = builder.build( subunit );
		
		// check N terminus
		assertAtomBonds( bondGraph, subunit, "1:H1", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:H2", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:H3", "1:N" );
		assertAtomBonds( bondGraph, subunit, "1:N", "1:H1,1:H2,1:H3,1:CA" );
		assertAtomBonds( bondGraph, subunit, "1:CA", "1:N,1:HA,1:C,1:CB" );
		assertAtomBonds( bondGraph, subunit, "1:HA", "1:CA" );
		assertAtomBonds( bondGraph, subunit, "1:C", "1:CA,1:O,2:N" );
		assertAtomBonds( bondGraph, subunit, "1:O", "1:C" );
		assertAtomBonds( bondGraph, subunit, "1:CB", "1:CA,1:HB1,1:HB2,1:HB3" );
		assertAtomBonds( bondGraph, subunit, "1:HB1", "1:CB" );
		assertAtomBonds( bondGraph, subunit, "1:HB2", "1:CB" );
		assertAtomBonds( bondGraph, subunit, "1:HB3", "1:CB" );
		
		// check middle residue
		assertAtomBonds( bondGraph, subunit, "2:H", "2:N" );
		assertAtomBonds( bondGraph, subunit, "2:N", "2:H,2:CA,1:C" );
		assertAtomBonds( bondGraph, subunit, "2:CA", "2:N,2:HA,2:C,2:CB" );
		assertAtomBonds( bondGraph, subunit, "2:HA", "2:CA" );
		assertAtomBonds( bondGraph, subunit, "2:C", "2:CA,2:O,3:N" );
		assertAtomBonds( bondGraph, subunit, "2:O", "2:C" );
		assertAtomBonds( bondGraph, subunit, "2:CB", "2:CA,2:HB2,2:HB3,2:CG" );
		assertAtomBonds( bondGraph, subunit, "2:HB2", "2:CB" );
		assertAtomBonds( bondGraph, subunit, "2:HB3", "2:CB" );
		assertAtomBonds( bondGraph, subunit, "2:CG", "2:CB,2:HG2,2:HG3,2:CD" );
		assertAtomBonds( bondGraph, subunit, "2:HG2", "2:CG" );
		assertAtomBonds( bondGraph, subunit, "2:HG3", "2:CG" );
		assertAtomBonds( bondGraph, subunit, "2:CD", "2:CG,2:OE1,2:OE2" );
		assertAtomBonds( bondGraph, subunit, "2:OE1", "2:CD" );
		assertAtomBonds( bondGraph, subunit, "2:OE2", "2:CD" );

		// check C terminus
		assertAtomBonds( bondGraph, subunit, "3:H", "3:N" );
		assertAtomBonds( bondGraph, subunit, "3:N", "3:H,3:CA,2:C" );
		assertAtomBonds( bondGraph, subunit, "3:CA", "3:N,3:HA,3:C,3:CB" );
		assertAtomBonds( bondGraph, subunit, "3:HA", "3:CA" );
		assertAtomBonds( bondGraph, subunit, "3:C", "3:CA,3:O,3:OXT" );
		assertAtomBonds( bondGraph, subunit, "3:O", "3:C" );
		assertAtomBonds( bondGraph, subunit, "3:OXT", "3:C" );
		assertAtomBonds( bondGraph, subunit, "3:CB", "3:CA,3:HB2,3:HB3,3:CG" );
		assertAtomBonds( bondGraph, subunit, "3:HB2", "3:CB" );
		assertAtomBonds( bondGraph, subunit, "3:HB3", "3:CB" );
		assertAtomBonds( bondGraph, subunit, "3:CG", "3:CB,3:OD1,3:ND2" );
		assertAtomBonds( bondGraph, subunit, "3:OD1", "3:CG" );
		assertAtomBonds( bondGraph, subunit, "3:ND2", "3:CG,3:HD21,3:HD22" );
		assertAtomBonds( bondGraph, subunit, "3:HD21", "3:ND2" );
		assertAtomBonds( bondGraph, subunit, "3:HD22", "3:ND2" );
	}
	
	public void testGetHeavyAtomNameNonTerminal( )
	{
		assertEquals( "CB", BondGraphBuilder.getInstance().getHeavyAtomName( "HB2", AminoAcid.Lysine, ResidueType.NonTerminal ) );
		assertEquals( "CB", BondGraphBuilder.getInstance().getHeavyAtomName( "HB3", AminoAcid.Lysine, ResidueType.NonTerminal ) );
		assertEquals( "CA", BondGraphBuilder.getInstance().getHeavyAtomName( "HA2", AminoAcid.Glycine, ResidueType.NonTerminal ) );
		assertEquals( "CA", BondGraphBuilder.getInstance().getHeavyAtomName( "HA3", AminoAcid.Glycine, ResidueType.NonTerminal ) );
		assertEquals( "CE1", BondGraphBuilder.getInstance().getHeavyAtomName( "HE1", AminoAcid.Phenylalanine, ResidueType.NonTerminal ) );
		assertEquals( "CE2", BondGraphBuilder.getInstance().getHeavyAtomName( "HE2", AminoAcid.Phenylalanine, ResidueType.NonTerminal ) );
	}
	
	public void testGetHeavyAtomNameTerminal( )
	{
		assertEquals( "N", BondGraphBuilder.getInstance().getHeavyAtomName( "H1", AminoAcid.Glycine, ResidueType.NTerminus ) );
		assertEquals( "N", BondGraphBuilder.getInstance().getHeavyAtomName( "H2", AminoAcid.Glycine, ResidueType.NTerminus ) );
		assertEquals( "N", BondGraphBuilder.getInstance().getHeavyAtomName( "H2", AminoAcid.Glycine, ResidueType.NTerminus ) );
	}
	
	private Residue newResidue( int number, AminoAcid aminoAcid, String atomNames )
	{
		Residue residue = new Residue();
		residue.setId( number - 1 );
		residue.setNumber( number );
		residue.setAminoAcid( aminoAcid );
		String[] atomNamesParts = atomNames.split( "," );
		
		// make each atom
		int nextAtomId = 0;
		ArrayList<Atom> atoms = new ArrayList<Atom>();
		for( String atomName : atomNamesParts )
		{
			Atom atom = new Atom();
			atom.setName( atomName );
			atom.setId( nextAtomId++ );
			atom.setResidueId( residue.getId() );
			atoms.add( atom );
		}
		residue.setAtoms( atoms );
		
		return residue;
	}
	
	private Atom getAtom( Residue residue, String atomName )
	{
		for( Atom atom : residue.getAtoms() )
		{
			if( atom.getName().equals( atomName ) )
			{
				return atom;
			}
		}
		
		assert( false );
		return null;
	}
	
	private AtomAddressInternal getAddress( Subunit subunit, String address )
	{
		String[] parts = address.split( ":" );
		
		int residueNumber = Integer.parseInt( parts[0] );
		String atomName = parts[1];
		
		Residue residue =  subunit.getResidues().get( residueNumber - subunit.getFirstResidueNumber() );
		Atom atom = getAtom( residue, atomName );
		
		return new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
	}
	
	private void assertAtomBonds( BondGraph bondGraph, Subunit subunit, String atomAddress, String bondedAtoms )
	{
		// build the expected atoms set
		TreeSet<AtomAddressInternal> expectedAddresses = new TreeSet<AtomAddressInternal>();
		String[] bondedAtomsParts = bondedAtoms.split( "," );
		for( String bondedAtom : bondedAtomsParts )
		{
			AtomAddressInternal expectedAddress = getAddress( subunit, bondedAtom );
			expectedAddresses.add( expectedAddress );
		}
		
		// get the bonds for our atom
		AtomAddressInternal address = getAddress( subunit, atomAddress );
		ArrayList<Bond> atomBonds = bondGraph.getBonds( address );
		
		// build the observed atom set
		TreeSet<AtomAddressInternal> observedAddresses = new TreeSet<AtomAddressInternal>();
		for( Bond bond : atomBonds )
		{
			observedAddresses.add( bond.getOtherAddress( address ) );
		}
		
		// compare them!
		assertEquals( expectedAddresses.size(), observedAddresses.size() );
		Iterator<AtomAddressInternal> iterExpectedAddress = expectedAddresses.iterator();
		Iterator<AtomAddressInternal> iterObservedAddress = observedAddresses.iterator();
		while( iterExpectedAddress.hasNext() && iterObservedAddress.hasNext() )
		{
			assertEquals( iterExpectedAddress.next(), iterObservedAddress.next() );
		}
		assert( iterExpectedAddress.hasNext() == iterObservedAddress.hasNext() );
	}
}
