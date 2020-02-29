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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraintReader;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestNoeInterpretation extends ExtendedTestCase
{
	private Protein m_protein;
	
	@Override
	public void setUp( )
	throws Exception
	{
		m_protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
	}
	
	public void testNoMap( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> noe = getNoe( "assign (segid C and resid 1 and name HB2) (segid A and resid 1 and name HB3) 3.50 1.00 1.00" );
		
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'c', 1, "hb2" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'a', 1, "hb3" )
		), noe.getRights() );
		assertEquals( 2.5, noe.getMinDistance() );
		assertEquals( 4.5, noe.getMaxDistance() );
	}
	
	public void testMethylToMethylene( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> noe = getNoe( "assign (segid B and resid 47 and name HD1#) (segid A and resid 44 and name HB#) 3.50 1.00 1.00" );
		
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'b', 47, "md" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'a', 44, "qb" )
		), noe.getRights() );
		assertEquals( 2.5, noe.getMinDistance() );
		assertEquals( 7.0, noe.getMaxDistance() );
	}
	
	public void testMethylMethylToProton( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> noe = getNoe( "assign (segid B and resid 39 and name HD#) (segid A and resid 42 and name H) 4.00 1.00 1.00" );
		
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'b', 39, "qd" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'a', 42, "h" )
		), noe.getRights() );
		assertEquals( 3.0, noe.getMinDistance() );
		assertEquals( 7.4, noe.getMaxDistance() );
	}
	
	public void testMethylMethylStarToProton( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> noe = getNoe( "assign (segid B and resid 39 and name HD*) (segid A and resid 42 and name H) 4.00 1.00 1.00" );
		
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'b', 39, "qd" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'a', 42, "h" )
		), noe.getRights() );
		assertEquals( 3.0, noe.getMinDistance() );
		assertEquals( 7.4, noe.getMaxDistance() );
	}
	
	public void testMethylToAormatic( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> noe = getNoe( "assign (segid A and resid 38 and name HD1#) (segid B and resid 32 and name HE#) 4.00 1.00 1.00" );
		
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'a', 38, "md" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'b', 32, "qe" )
		), noe.getRights() );
		assertEquals( 3.0, noe.getMinDistance() );
		assertEquals( 8.5, noe.getMaxDistance() );
	}
	
	private DistanceRestraint<AtomAddressReadable> getNoe( String in )
	throws Exception
	{
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = new DistanceRestraintReader().read( new ByteArrayInputStream( in.getBytes() ) );
		assertEquals( 1, noes.size() );
		
		PseudoatomBuilder.getInstance().buildDistanceRestraints( m_protein.getSequences(), noes );

		return noes.get( 0 );
	}
}
