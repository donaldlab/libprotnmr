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
package edu.duke.cs.libprotnmr.analysis;

import java.util.List;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestDistanceRestraintCalculator extends ExtendedTestCase
{
	public void testGetSimluatedRestraints( )
	throws Exception
	{
		double maxDistance = 5.0;
		Protein protein = new ProteinReader().read( getClass().getResourceAsStream( Resources.getPath("1ZLL.oligomer.protein") ) );
		List<DistanceRestraint<AtomAddressInternal>> restraints = DistanceRestraintCalculator.getSimulatedIntersubunitRestraints( 0, 1, protein, maxDistance, 0.0 );
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			// make sure we only get unambiguous restraints
			assertFalse( restraint.isAmbiguous() );
			assertEquals( 1, restraint.getLefts().size() );
			assertEquals( 1, restraint.getRights().size() );
			AtomAddressInternal left = restraint.getLefts().iterator().next();
			AtomAddressInternal right = restraint.getRights().iterator().next();
			
			// get the atoms
			Atom leftAtom = protein.getAtom( left );
			Atom rightAtom = protein.getAtom( right );
			
			// make sure they're both hydrogens
			assertEquals( Element.Hydrogen, leftAtom.getElement() );
			assertEquals( Element.Hydrogen, rightAtom.getElement() );
			
			// make sure they're from different subunits
			assertTrue( left.getSubunitId() != right.getSubunitId() );
			
			// check their distance
			Vector3 leftPos = leftAtom.getPosition();
			Vector3 rightPos = rightAtom.getPosition();
			assertLte( restraint.getMaxDistance() * restraint.getMaxDistance(), leftPos.getSquaredDistance( rightPos ) );
			assertLte( maxDistance, restraint.getMaxDistance() );
		}
	}
}
