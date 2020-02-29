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

package edu.duke.cs.libprotnmr.protein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;
import edu.duke.cs.libprotnmr.util.Bijection;
import edu.duke.cs.libprotnmr.util.HashBijection;


public class SubunitOrder
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_referenceOrder;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public SubunitOrder( String referenceOrder )
	{
		m_referenceOrder = referenceOrder;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public int getNumSubunits( )
	{
		return m_referenceOrder.length();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public static void normalize( Protein protein )
	{
		/*
		 * This algorithm will "normalize" the order of subunits in a protein in the following way:
		 * Consider the plane of the ring. Let side of the induced halfspace containing the N-terminus be the
		 * positive side of the plane. Position the ring plane on the centroid for the protein.
		 * Let the x axis of the plane be in the direction of the first subunit. Then, the second subunit
		 * will (according to this convention) lie counterclockwise from the first subunit when viewed from
		 * the positive halfspace.
		 */
		
		assert( protein.getSubunits().size() >= 2 );
		
		// get the centroids and translate to lie about the origin
		Vector3 centroid = ProteinGeometry.getCentroid( protein, protein.backboneAtoms() );
		List<Vector3> centroids = new ArrayList<Vector3>( protein.getSubunits().size() );
		for( Subunit subunit : protein.getSubunits() )
		{
			Vector3 c = ProteinGeometry.getCentroid( subunit, subunit.backboneAtoms() );
			c.subtract( centroid );
			centroids.add( c );
		}
		
		// find the x axis according to the convention
		Vector3 xAxis = new Vector3( centroids.get( 0 ) );
		xAxis.normalize();
		
		// let the z axis point into the positive halfspace
		Vector3 zAxis = new Vector3();
		xAxis.getCross( zAxis, centroids.get( 1 ) );
		zAxis.normalize();
		
		// get the vector for the n-terminus and make sure the zAxis is pointing the same way
		// NOTE: this assumes the n-terminus is always the first residue
		Vector3 nTerminusDirection = new Vector3( protein.getAtom( 0, 0, 0 ).getPosition() );
		nTerminusDirection.subtract( centroid );
		nTerminusDirection.normalize();
		double dot = zAxis.getDot( nTerminusDirection );
		if( dot < 0.0 )
		{
			zAxis.negate();
		}
		
		// this is unlikely to ever happen
		// if it ever does, we can just pick other atoms along the protein chain
		// they can't all be in the plane of the ring
		assert( dot != 0.0 );
		
		/* TEMP: debug kinemage
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin );
		KinemageBuilder.appendPoints( kin, centroids, "Centroids before", 0, 4 );
		KinemageBuilder.appendVector( kin, xAxis, "X-axis before", 1, 2, 2.0 );
		KinemageBuilder.appendVector( kin, zAxis, "Z-axis", 1, 2, 2.0 );
		*/
		
		// transform all the centroids onto the xy plane
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, zAxis, Vector3.getUnitZ() );
		for( Vector3 c : centroids )
		{
			c.rotate( q );
		}
		xAxis.rotate( q );
		
		/* TEMP: debug kinemage
		KinemageBuilder.appendPoints( kin, centroids, "Centroids middle", 0, 4 );
		KinemageBuilder.appendVector( kin, xAxis, "X-axis middle", 1, 2, 2.0 );
		*/
		
		Quaternion.getRotation( q, xAxis, Vector3.getUnitX() );
		for( Vector3 c : centroids )
		{
			c.rotate( q );
		}
		
		/* TEMP: debug kinemage
		KinemageBuilder.appendPoints( kin, centroids, "Centroids after", 0, 4 );
		new KinemageWriter().showAndWait( kin );
		*/
		
		// compute all the axis angles for the subunits
		final double[] angles = new double[protein.getSubunits().size()];
		for( int i=0; i<centroids.size(); i++ )
		{
			Vector3 c = centroids.get( i );
			angles[i] = Math.atan2( c.y, c.x );
		}
		assert( CompareReal.eq( angles[0], 0.0 ) );
		
		// finally, reorder the subunits
		Collections.sort( protein.getSubunits(), new Comparator<Subunit>( )
		{
			@Override
			public int compare( Subunit a, Subunit b )
			{
				return Double.compare( angles[a.getId()], angles[b.getId()] );
			}
		} );
		for( int i=0; i<protein.getSubunits().size(); i++ )
		{
			protein.getSubunit( i ).setName( (char)( 'A' + i ) );
		}
		protein.updateSubunitIndex();
		protein.updateAtomIndices();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return m_referenceOrder;
	}
	
	public void convertComputedToReference( Protein protein )
	{
		assert( getNumSubunits() == protein.getSubunits().size() );
		
		// build a bijection between computed subunit ids (keys) and reference subunit ids (values)
		Bijection<Integer,Integer> bijection = new HashBijection<Integer,Integer>();
		for( int i=0; i<m_referenceOrder.length(); i++ )
		{
			bijection.put( i, protein.getSubunitId( m_referenceOrder.charAt( i ) ) );
		}
		
		// save the subunit names
		List<Character> subunitNames = new ArrayList<Character>( getNumSubunits() );
		for( int i=0; i<getNumSubunits(); i++ )
		{
			subunitNames.add( protein.getSubunit( i ).getName() );
		}
		
		// perform the mapping
		List<Subunit> subunits = protein.getSubunits();
		List<Subunit> newSubunits = new ArrayList<Subunit>( subunits.size() );
		for( int i=0; i<subunits.size(); i++ )
		{
			newSubunits.add( null );
		}
		for( Bijection.Entry<Integer,Integer> entry : bijection.entries() )
		{
			int computedId = entry.getKey();
			int referenceId = entry.getValue();
			newSubunits.set( referenceId, subunits.get( computedId ) );
		}
		
		// restore the subunit names
		for( int i=0; i<getNumSubunits(); i++ )
		{
			newSubunits.get( i ).setName( subunitNames.get( i ) );
		}
		
		protein.setSubunits( newSubunits );
	}
}
