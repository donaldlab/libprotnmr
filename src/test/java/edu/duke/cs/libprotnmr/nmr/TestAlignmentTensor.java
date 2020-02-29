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

import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HomoProtein;
import edu.duke.cs.libprotnmr.protein.HomoSequences;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestAlignmentTensor extends ExtendedTestCase
{
	private static final double Epsilon = 1e-3;
	
	public void testDAGK( )
	throws Exception
	{
		// read the subunit structure and the RDCs
		Subunit subunit = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("2KDC.monomer.protein") ) ).getSubunit( 0 );
		List<Rdc<AtomAddressReadable>> readableRdcs = new RdcReader().read( getClass().getResourceAsStream( Resources.getPath("2KDC.experimental.rdc") ) );
		NameMapper.ensureAddresses( new HomoSequences( subunit.getSequence() ), readableRdcs, NameScheme.New );
		HomoProtein homoProtein = new HomoProtein( subunit, Rdc.getSubunitNames( readableRdcs ) );
		List<Rdc<AtomAddressInternal>> rdcs = RdcMapper.mapReadableToInternal( homoProtein, readableRdcs );
		rdcs = RdcFilterer.pickFromSubunit( rdcs, 0 );
		
		// compute the alignment tensor
		AlignmentTensor tensor = AlignmentTensor.compute( subunit, rdcs );
		
		assertEquals( -6.653, tensor.getDxx(), Epsilon );
		assertEquals( -7.049, tensor.getDyy(), Epsilon );
		assertEquals( 13.702, tensor.getDzz(), Epsilon );
		assertEqualsReal( 0.0, tensor.getDxx() + tensor.getDyy() + tensor.getDzz() );
		assertEqualsReal( 0.028861, tensor.getAsymmetry() );
		assertEqualsReal( 0.019241, tensor.getRhombicity() );
		assertEquals( 0.276, tensor.getRmsd( subunit, rdcs ), Epsilon );
	}
	
	public void testGB1( )
	throws Exception
	{
		// read the subunit structure and the RDCs
		Subunit subunit = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) ).getSubunit( 0 );
		List<Rdc<AtomAddressReadable>> readableRdcs = new RdcReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.experimental.rdc") ) );
		NameMapper.ensureAddresses( new HomoSequences( subunit.getSequence() ), readableRdcs, NameScheme.New );
		HomoProtein homoProtein = new HomoProtein( subunit, Rdc.getSubunitNames( readableRdcs ) );
		List<Rdc<AtomAddressInternal>> rdcs = RdcMapper.mapReadableToInternal( homoProtein, readableRdcs );
		rdcs = RdcFilterer.pickFromSubunit( rdcs, 0 );
		
		// compute the alignment tensor
		AlignmentTensor tensor = AlignmentTensor.compute( subunit, rdcs );
		
		assertEquals( 4.848, tensor.getDxx(), Epsilon );
		assertEquals( 30.980, tensor.getDyy(), Epsilon );
		assertEquals( -35.829, tensor.getDzz(), Epsilon );
		assertEqualsReal( 0.0, tensor.getDxx() + tensor.getDyy() + tensor.getDzz() );
		assertEqualsReal( 0.729363, tensor.getAsymmetry() );
		assertEqualsReal( 0.486242, tensor.getRhombicity() );
		assertEquals( 0.565, tensor.getRmsd( subunit, rdcs ), Epsilon );
	}
	
	public void testComputeEigenvalues( )
	throws Exception
	{
		// override epsilon for this one. We can be WAY more precise
		final double Epsilon = 1e-12;
		
		// read the subunit structure and the RDCs
		Subunit subunit = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.monomer.protein") ) ).getSubunit( 0 );
		List<Rdc<AtomAddressReadable>> readableRdcs = new RdcReader().read( getClass().getResourceAsStream( Resources.getPath("1Q10.experimental.rdc") ) );
		NameMapper.ensureAddresses( new HomoSequences( subunit.getSequence() ), readableRdcs, NameScheme.New );
		HomoProtein homoProtein = new HomoProtein( subunit, Rdc.getSubunitNames( readableRdcs ) );
		List<Rdc<AtomAddressInternal>> rdcs = RdcMapper.mapReadableToInternal( homoProtein, readableRdcs );
		rdcs = RdcFilterer.pickFromSubunit( rdcs, 0 );
		
		// compute the alignment tensor
		AlignmentTensor tensor = AlignmentTensor.compute( subunit, rdcs );
		
		// save the eigenvalues and rmsd
		double Dxx = tensor.getDxx();
		double Dyy = tensor.getDyy();
		double Dzz = tensor.getDzz();
		double rmsd = tensor.getRmsd( subunit, rdcs );
		assertEquals( 0, Dxx + Dyy + Dzz, Epsilon );
		
		// re-compute the eigenvalues
		Matrix3 rot = new Matrix3();
		tensor.getRotPofToMol( rot );
		AlignmentTensor tensorAgain = AlignmentTensor.compute( subunit, rdcs, rot );
		
		// we should get the same eigenvalues back. We shouldn't have been able to optimize them any more
		assertEquals( Math.abs( Dxx ), Math.abs( tensorAgain.getDxx() ), Epsilon );
		assertEquals( Math.abs( Dyy ), Math.abs( tensorAgain.getDyy() ), Epsilon );
		assertEquals( Math.abs( Dzz ), Math.abs( tensorAgain.getDzz() ), Epsilon );
		assertEquals( 0, tensorAgain.getDxx() + tensorAgain.getDyy() + tensorAgain.getDzz(), Epsilon );
		assertEquals( rmsd, tensorAgain.getRmsd( subunit, rdcs ), Epsilon );
	}
}
