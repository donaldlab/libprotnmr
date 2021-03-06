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

package edu.duke.cs.libprotnmr.math;

import java.util.Arrays;

import edu.duke.cs.libprotnmr.ExtendedTestCase;


public class TestDistribution extends ExtendedTestCase
{
	public void testMin( )
	{
		DistributionDouble d = new DistributionDouble();
		
		d.add( 1.0 );
		assertEquals( 1.0, d.getMinDouble() );
		d.add( 2.0 );
		assertEquals( 1.0, d.getMinDouble() );
		d.add( 0.4 );
		assertEquals( 0.4, d.getMinDouble() );
	}
	
	public void testMax( )
	{
		DistributionDouble d = new DistributionDouble();
		
		d.add( 1.0 );
		assertEquals( 1.0, d.getMaxDouble() );
		d.add( 0.4 );
		assertEquals( 1.0, d.getMaxDouble() );
		d.add( 2.0 );
		assertEquals( 2.0, d.getMaxDouble() );
	}
	
	public void testSum( )
	{
		DistributionDouble d = new DistributionDouble	();
		
		assertEquals( 0.0, d.getSum() );
		d.add( 1.0 );
		assertEquals( 1.0, d.getSum() );
		d.add( 2.0 );
		assertEquals( 3.0, d.getSum() );
		d.add( 3.0 );
		assertEquals( 6.0, d.getSum() );
	}
	
	public void testCount( )
	{
		DistributionDouble d = new DistributionDouble();
		
		assertEquals( 0, d.getCount() );
		d.add( 1.0 );
		assertEquals( 1, d.getCount() );
		d.add( 1.0 );
		assertEquals( 2, d.getCount() );
	}
	
	public void testMean( )
	{
		DistributionDouble d = new DistributionDouble();
		
		d.add( 10.0 );
		assertEquals( 10.0, d.getMean() );
		d.add( 10.0 );
		assertEquals( 10.0, d.getMean() );
		d.add( 40.0 );
		assertEquals( 20.0, d.getMean() );
		d.add( 40.0 );
		assertEquals( 25.0, d.getMean() );
	}
	
	public void testMedian( )
	{
		DistributionDouble d = new DistributionDouble();
		
		d.add( 10.0 );
		assertEquals( 10.0, d.getMedian() );
		d.add( 20.0 );
		assertEquals( 15.0, d.getMedian() );
		d.add( 30.0 );
		assertEquals( 20.0, d.getMedian() );
		d.add( 40.0 );
		assertEquals( 25.0, d.getMedian() );
	}
	
	public void testGetCount( )
	{
		DistributionDouble d = new DistributionDouble( Arrays.asList( 1.0, 1.0, 2.0, 3.0, 3.0, 3.0, 4.0, 4.0, 5.0, 8.0, 9.0, 9.0, 9.0 ) );
		assertEquals( 0, d.getCount( 0.0 ) );
		assertEquals( 2, d.getCount( 1.0 ) );
		assertEquals( 3, d.getCount( 3.0 ) );
		assertEquals( 2, d.getCount( 4.0 ) );
		assertEquals( 1, d.getCount( 5.0 ) );
		assertEquals( 1, d.getCount( 8.0 ) );
		assertEquals( 3, d.getCount( 9.0 ) );
	}
	
	public void testGetNthValue( )
	{
		DistributionDouble d = new DistributionDouble( Arrays.asList( 1.0, 1.0, 2.0, 3.0, 3.0, 3.0, 4.0, 4.0, 5.0, 8.0, 9.0, 9.0, 9.0 ) );
		assertEquals( 1.0, d.getNthValueDouble( 0 ) );
		assertEquals( 1.0, d.getNthValueDouble( 1 ) );
		assertEquals( 2.0, d.getNthValueDouble( 2 ) );
		assertEquals( 3.0, d.getNthValueDouble( 3 ) );
		assertEquals( 3.0, d.getNthValueDouble( 4 ) );
		assertEquals( 3.0, d.getNthValueDouble( 5 ) );
		assertEquals( 4.0, d.getNthValueDouble( 6 ) );
		assertEquals( 4.0, d.getNthValueDouble( 7 ) );
		assertEquals( 5.0, d.getNthValueDouble( 8 ) );
		assertEquals( 8.0, d.getNthValueDouble( 9 ) );
		assertEquals( 9.0, d.getNthValueDouble( 10 ) );
		assertEquals( 9.0, d.getNthValueDouble( 11 ) );
		assertEquals( 9.0, d.getNthValueDouble( 12 ) );
	}
}
