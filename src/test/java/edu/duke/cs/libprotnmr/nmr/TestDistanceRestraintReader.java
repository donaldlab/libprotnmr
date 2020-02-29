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
package edu.duke.cs.libprotnmr.nmr;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestDistanceRestraintReader extends ExtendedTestCase
{
	public void testTiny( )
	throws Exception
	{
		DistanceRestraintReader reader = new DistanceRestraintReader();
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = reader.read( getClass().getResourceAsStream( Resources.getPath("tiny.noe") ) );
		
		assertEquals( 1, noes.size() );
		
		// assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 2.2 4.0
		DistanceRestraint<AtomAddressReadable> noe = noes.get( 0 );
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 7, "ha1" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'C', 15, "hd#" )
		), noe.getRights() );
		assertEquals( 1.8, noe.getMinDistance(), CompareReal.getEpsilon() );
		assertEquals( 8.0, noe.getMaxDistance(), CompareReal.getEpsilon() );
	}
	
	public void testLarge( )
	throws Exception
	{
		DistanceRestraintReader reader = new DistanceRestraintReader();
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = reader.read( getClass().getResourceAsStream( Resources.getPath("large.noe") ) );
		
		assertEquals( 856, noes.size() );
		
		// line 393: assign (resid 9 and name ha and segid D)(resid 15 and name hn and segid B) 4.0 2.2 1.0
		DistanceRestraint<AtomAddressReadable> noe = noes.get( 392 );
		assertEquals( 1, noe.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'D', 9, "ha" )
		), noe.getLefts() );
		assertEquals( 1, noe.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'B', 15, "hn" )
		), noe.getRights() );
		assertEquals( 1.8, noe.getMinDistance(), CompareReal.getEpsilon() );
		assertEquals( 5.0, noe.getMaxDistance(), CompareReal.getEpsilon() );
	}
	
	public void testPre( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> pre = getNoe(
			"assign (Segid a and resid 46 and name CA) \n"
			+ "((segid a and resid 26 and name HN) \n"
			+ "OR \n"
			+ "(segid b and resid 26 and name HN) \n"
			+ "OR \n"
			+ "(segid c and resid 26 and name HN) \n"
			+ ")  25 4.0 100  "
		);
		
		assertEquals( 1, pre.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 46, "ca" )
		), pre.getLefts() );
		assertEquals( 3, pre.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 26, "hn" ),
			new AtomAddressReadable( 'B', 26, "hn" ),
			new AtomAddressReadable( 'C', 26, "hn" )
		), pre.getRights() );
		assertEquals( 21.0, pre.getMinDistance(), CompareReal.getEpsilon() );
		assertEquals( 125.0, pre.getMaxDistance(), CompareReal.getEpsilon() );
	}
	
	public void testDisulfideBond( )
	throws Exception
	{
		DistanceRestraint<AtomAddressReadable> db = getNoe(
			"assign (Segid c and resid 52 and name CA)"
			+ "((segid a and resid 52 and name CA) OR (segid b and resid 52 and name CA)) \n" 
			+ "8.0 4.0 2.0  \n"
		);
		
		assertEquals( 1, db.getLefts().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'C', 52, "ca" )
		), db.getLefts() );
		assertEquals( 2, db.getRights().size() );
		assertEquals( Transformer.toTreeSet(
			new AtomAddressReadable( 'A', 52, "ca" ),
			new AtomAddressReadable( 'B', 52, "ca" ) 
		), db.getRights() );
		assertEquals( 4.0, db.getMinDistance(), CompareReal.getEpsilon() );
		assertEquals( 10.0, db.getMaxDistance(), CompareReal.getEpsilon() );
	}
	
	private DistanceRestraint<AtomAddressReadable> getNoe( String in )
	throws Exception
	{
		ArrayList<DistanceRestraint<AtomAddressReadable>> noes = new DistanceRestraintReader().read( new ByteArrayInputStream( in.getBytes() ) );
		assertEquals( 1, noes.size() );
		return noes.get( 0 );
	}
}
