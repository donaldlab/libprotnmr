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

import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestChemicalShiftReader extends ExtendedTestCase
{
	public void testReader( )
	throws Exception
	{
		ChemicalShiftReader reader = new ChemicalShiftReader();
		List<ChemicalShift<AtomAddressReadable>> shifts = reader.read( getClass().getResourceAsStream( Resources.getPath("chemical.shifts") ) );
		assertEquals( 4, shifts.size() );
		
		ChemicalShift<AtomAddressReadable> shift = null;
		
		// 43  4 LYS CE   C  41.8662 0.2  1 
		shift = shifts.get( 0 );
		assertEquals( 43, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 4, "CE" ), shift.getAddress() );
		assertEquals( AminoAcid.Lysine, shift.getAminoAcid() );
		assertEquals( Element.Carbon, shift.getElement() );
		assertEquals( 41.8662, shift.getValue() );
		assertEquals( 0.2, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 124 12 LEU HA   H   5.0068 0.02 1 
		shift = shifts.get( 1 );
		assertEquals( 124, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 12, "HA" ), shift.getAddress() );
		assertEquals( AminoAcid.Leucine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 5.0068, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 422 45 TYR H    H   8.8528 0.02 1
		shift = shifts.get( 2 );
		assertEquals( 422, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 45, "H" ), shift.getAddress() );
		assertEquals( AminoAcid.Tyrosine, shift.getAminoAcid() );
		assertEquals( Element.Hydrogen, shift.getElement() );
		assertEquals( 8.8528, shift.getValue() );
		assertEquals( 0.02, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
		
		// 466 50 LYS CG   C  24.5176 0.2  1 
		shift = shifts.get( 3 );
		assertEquals( 466, shift.getNumber() );
		assertEquals( new AtomAddressReadable( 50, "CG" ), shift.getAddress() );
		assertEquals( AminoAcid.Lysine, shift.getAminoAcid() );
		assertEquals( Element.Carbon, shift.getElement() );
		assertEquals( 24.5176, shift.getValue() );
		assertEquals( 0.2, shift.getError() );
		assertEquals( 1, shift.getAmbiguityCode() );
	}
}
