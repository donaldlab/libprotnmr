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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Line3;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.PrincipalComponents;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;


public class SymmetryAxisCalculator
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Line3 getSymmetryAxis( Protein protein )
	{
		if( !protein.isHomoOligomer() )
		{
			throw new IllegalArgumentException( "Can only calculate symmetry axes for homo oligomers!" );
		}
		
		// average backbone coords across subunits
		
		// get the iters for each subunit
		List<Iterator<AtomAddressInternal>> iters = new ArrayList<Iterator<AtomAddressInternal>>();
		for( Subunit subunit : protein.getSubunits() )
		{
			iters.add( subunit.backboneAtoms().iterator() );
		}
		
		// march down the backbones of all the subunits
		List<Vector3> points = new ArrayList<Vector3>( protein.getSubunit( 0 ).backboneAtoms().size() );
		while( iters.get( 0 ).hasNext() )
		{
			Vector3 p = new Vector3();
			for( int i=0; i<protein.getSubunits().size(); i++ )
			{
				p.add( protein.getSubunit( i ).getAtom( iters.get( i ).next() ).getPosition() );
			}
			p.scale( 1.0/protein.getSubunits().size() );
			
			points.add( p );
		}
		
		// make sure all the iters are empty
		for( Iterator<AtomAddressInternal> iter : iters )
		{
			assert( !iter.hasNext() );
		}
		
		// do PCA to get the axis orientation
		Vector3 orientation = PrincipalComponents.getPrincipalComponents( points ).lastEntry().getValue();
		
		// build the axis
		Vector3 start = ProteinGeometry.getCentroid( protein, protein.backboneAtoms() );
		Vector3 stop = new Vector3( start );
		stop.add( orientation );
		return new Line3( start, stop );
	}
}
