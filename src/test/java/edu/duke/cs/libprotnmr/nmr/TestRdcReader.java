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

package edu.duke.cs.libprotnmr.nmr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestRdcReader extends ExtendedTestCase
{
	public void testOne( )
	throws Exception
	{
		Rdc<AtomAddressReadable> rdc = readRdc( "assign (resid 500 and name OO ) (resid 500 and name Z  ) (resid 500 and name X  ) (resid 500 and name Y  ) (segid A and residue 5 and name N)(segid B and residue 6 and name HN) 5.0 1.0" );
		
		assertEquals( new AtomAddressReadable( 'A', 5, "N" ), rdc.getFrom() );
		assertEquals( new AtomAddressReadable( 'B', 6, "HN" ), rdc.getTo() );
		assertEquals( 5.0, rdc.getValue() );
		assertEquals( 1.0, rdc.getError() );
	}
	
	public void testTwo( )
	throws Exception
	{
		Rdc<AtomAddressReadable> rdc = readRdc( "assign (resid 500 and name OO ) (resid 500 and name Z  ) (resid 500 and name X  ) (resid 500 and name Y  ) (segid A and residue 5 and name N)(segid B and residue 6 and name HN) 5.0 1.0" );
		
		assertEquals( new AtomAddressReadable( 'A', 5, "N" ), rdc.getFrom() );
		assertEquals( new AtomAddressReadable( 'B', 6, "HN" ), rdc.getTo() );
		assertEquals( 5.0, rdc.getValue() );
		assertEquals( 1.0, rdc.getError() );
	}
	
	public void test2KDC( )
	throws Exception
	{
		ArrayList<Rdc<AtomAddressReadable>> rdcs = new RdcReader().read( getClass().getResourceAsStream( Resources.getPath("2KDC.experimental.rdc") ) );
		assertEquals( 201, rdcs.size() );
		
		// check a couple RDCs
		
		// (segid b and resid 27  and name N  )
		// (segid b and resid 27  and name HN ) 0.8 1.000
		Rdc<AtomAddressReadable> rdc = rdcs.get( 67 );
		assertEquals( new AtomAddressReadable( 'B', 27, "N" ), rdc.getFrom() );
		assertEquals( new AtomAddressReadable( 'B', 27, "HN" ), rdc.getTo() );
		assertEquals( 0.8, rdc.getValue() );
		assertEquals( 1.0, rdc.getError() );
		
		// (segid c and resid 121 and name N  )
		// (segid c and resid 121 and name HN ) -2.4 1.000
		rdc = rdcs.get( 200 );
		assertEquals( new AtomAddressReadable( 'C', 121, "N" ), rdc.getFrom() );
		assertEquals( new AtomAddressReadable( 'C', 121, "HN" ), rdc.getTo() );
		assertEquals( -2.4, rdc.getValue() );
		assertEquals( 1.0, rdc.getError() );
	}
	
	private Rdc<AtomAddressReadable> readRdc( String in )
	throws IOException
	{
		return new RdcReader().read( new ByteArrayInputStream( in.getBytes() ) ).get( 0 );
	}
}
