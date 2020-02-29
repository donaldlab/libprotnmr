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
package edu.duke.cs.libprotnmr.protein;

import java.util.ArrayList;
import java.util.Random;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;


public class TestCopyConstructors extends ExtendedTestCase
{
	private Random m_rand = new Random();
	
	public void testAtom( )
	{
		Atom atom = getRandomAtom( 0 );
		assertEquals( atom, new Atom( atom ) );
	}
	
	public void testResidue( )
	{
		Residue residue = getRandomResidue();
		assertEquals( residue, new Residue( residue ) );
	}
	
	public void testSubunit( )
	{
		Subunit subunit = getRandomSubunit( 0 );
		assertEquals( subunit, new Subunit( subunit ) );
	}
	
	public void testSubunitConstructor( )
	{
		Subunit subunit = getRandomSubunit( 0 );
		Protein protein = new Protein( subunit );
		
		assertEquals( 1, protein.getSubunits().size() );
		assertEquals( 0, subunit.getId() );
		assertSame( subunit, protein.getSubunit( 0 ) );
	}
	
	public void testProtein( )
	{
		Protein protein = getRandomProtein();
		assertEquals( protein, new Protein( protein ) );

		assertProteinIndicesCorrect( protein );
	}
	
	private Protein getRandomProtein( )
	{
		Protein protein = new Protein();
		protein.setName( randString( 4 ) );
		
		// get some random subunits
		int numSubunits = randInt( 1, 6 );
		int nextSubunitId = 0;
		ArrayList<Subunit> subunits = new ArrayList<Subunit>( numSubunits );
		for( int i=0; i<numSubunits; i++ )
		{
			Subunit subunit = getRandomSubunit( nextSubunitId++ );
			subunits.add( subunit );
		}
		protein.setSubunits( subunits );
		
		return protein;
	}
	
	private Subunit getRandomSubunit( int subunitId )
	{
		Subunit subunit = new Subunit();
		subunit.setId( subunitId );
		
		// get some random residues
		int numResidues = randInt( 1, 20 );
		for( int i=0; i<numResidues; i++ )
		{
			subunit.addResidue( getRandomResidue() );
		}
		
		subunit.updateAtomIndices();
		
		return subunit;
	}
	
	private Residue getRandomResidue( )
	{
		Residue residue = new Residue();
		residue.setNumber( randInt( 1, 100 ) );
		residue.setFirstAtomNumber( randInt( 1, 3000 ) );
		residue.setAminoAcid( randAminoAcid() );
		
		// get some random atoms
		int numAtoms = randInt( 6, 20 );
		ArrayList<Atom> atoms = new ArrayList<Atom>( numAtoms );
		for( int i=0; i<numAtoms; i++ )
		{
			Atom atom = getRandomAtom( atoms.size() );
			atoms.add( atom );
		}
		residue.setAtoms( atoms );
		
		return residue;
	}
	
	private Atom getRandomAtom( int atomId )
	{
		Atom atom = new Atom();
		atom.setId( atomId );
		atom.setNumber( randInt( 1, 3000 ) );
		atom.setName( randString( 1 ) );
		atom.setElement( randElement() );
		atom.setIsBackbone( randBoolean() );
		atom.setPosition( new Vector3( randDouble(), randDouble(), randDouble() ) );
		atom.setOccupancy( randFloat() );
		atom.setTempFactor( randFloat() );
		return atom;
	}
	
	private int randInt( int min, int max )
	{
		return m_rand.nextInt( max - min + 1 ) + min;
	}
	
	private float randFloat( )
	{
		return m_rand.nextFloat();
	}
	
	private double randDouble( )
	{
		return m_rand.nextDouble();
	}
	
	private char randChar( )
	{
		return (char)randInt( 'a', 'z' );
	}
	
	private String randString( int length )
	{
		StringBuffer buf = new StringBuffer();
		buf.ensureCapacity( length );
		
		for( int i=0; i<length; i++ )
		{
			buf.append( randChar() );
		}
		
		return buf.toString();
	}
	
	private Element randElement( )
	{
		return Element.values()[randInt( 0, Element.values().length - 1 )];
	}
	
	private AminoAcid randAminoAcid( )
	{
		return AminoAcid.values()[randInt( 0, AminoAcid.values().length - 1 )];
	}
	
	private boolean randBoolean( )
	{
		return randInt( 0, 1 ) == 1;
	}
}
