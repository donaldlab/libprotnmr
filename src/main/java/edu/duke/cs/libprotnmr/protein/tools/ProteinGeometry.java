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

package edu.duke.cs.libprotnmr.protein.tools;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Sphere;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.PrincipalComponents;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;


public class ProteinGeometry
{
	public static Vector3 center( HasAtoms protein )
	{
		return center( protein, protein.atoms() );
	}
	
	public static Vector3 center( HasAtoms protein, List<AtomAddressInternal> addresses )
	{
		Vector3 c = getCentroid( protein, addresses );
		c.negate();
		translate( protein, c );
		return c;
	}
	
	public static Vector3 getCentroid( HasAtoms protein )
	{
		return getCentroid( protein, protein.atoms() );
	}
	
	public static Vector3 getCentroid( HasAtoms protein, List<AtomAddressInternal> addresses )
	{
		Vector3 centroid = new Vector3();
		for( AtomAddressInternal address : addresses )
		{
			centroid.add( protein.getAtom( address ).getPosition() );
		}
		centroid.scale( 1.0 / addresses.size() );
		return centroid;
	}
	
	public static void translate( HasAtoms protein, Vector3 t )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			protein.getAtom( address ).getPosition().add( t );
		}
	}
	
	public static void rotate( HasAtoms protein, Quaternion q )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			protein.getAtom( address ).getPosition().rotate( q );
		}
	}
	
	public static void rotate( HasAtoms target, HasAtoms source, Quaternion q )
	{
		for( AtomAddressInternal address : source.atoms() )
		{
			Vector3 p = target.getAtom( address ).getPosition();
			p.set( source.getAtom( address ).getPosition() );
			p.rotate( q );
		}
	}
	
	public static void transform( HasAtoms protein, Matrix3 m )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			protein.getAtom( address ).getPosition().transform( m );
		}
	}
	
	public static void transform( HasAtoms protein, Matrix3 m, Vector3 translation )
	{
		for( AtomAddressInternal address : protein.atoms() )
		{
			Vector3 pos = protein.getAtom( address ).getPosition();
			pos.subtract( translation );
			pos.transform( m );
			pos.add( translation );
		}
	}
	
	public static Sphere getBoundingSphere( HasAtoms protein )
	{
		// grab the positions from the atoms
		List<Vector3> points = new ArrayList<Vector3>( protein.atoms().size() );
		for( AtomAddressInternal address : protein.atoms() )
		{
			points.add( protein.getAtom( address ).getPosition() );
		}
		
		// construct the minimum bounding sphere
		return new Sphere( points );
	}
	
	public static double getDihedralAngle( Vector3 a, Vector3 b, Vector3 c, Vector3 d )
	{
		// build a basis so we can rotate the system such that:
		// z = cb
		// x = orthogonal to z, in the direction of cd
		// y defined by right-handedness
		
		// UNDONE: I could probably optimize the shit out of this =P
		Matrix3 m = new Matrix3();
		
		Vector3 z = new Vector3( b );
		z.subtract( c );
		z.normalize();
		
		Vector3 x = new Vector3( d );
		Matrix3.getOrthogonalProjection( m, z );
		x.subtract( c );
		m.multiply( x );
		x.normalize();
		
		Vector3 y = new Vector3();
		z.getCross( y, x );
		y.normalize();
		
		// rotate the system and measure the dihedral angle
		m.setColumns( x, y, z );
		m.transpose();
		Vector3 ap = new Vector3( a );
		ap.subtract( b );
		m.multiply( ap );
		return Math.atan2( ap.y, ap.x );
	}
	
	public static Vector3 getBackboneAxis( HasAtoms protein )
	{
		// get the backbone atom positions
		List<Vector3> points = new ArrayList<Vector3>();
		for( AtomAddressInternal address : protein.backboneAtoms() )
		{
			points.add( protein.getAtom( address ).getPosition() );
		}
		
		return getBackboneAxis( points );
	}
	
	public static Vector3 getBackboneAxis( List<Vector3> points )
	{
		// use PCA to get the major axis
		Vector3 axis = PrincipalComponents.getPrincipalComponents( points ).lastEntry().getValue();
		
		// make sure the axis is pointing in the right direction
		Vector3 backboneDirection = new Vector3( points.get( points.size() - 1 ) );
		backboneDirection.subtract( points.get( 0 ) );
		backboneDirection.normalize();
		if( axis.getDot( backboneDirection ) < 0 )
		{
			axis.negate();
		}
		
		return axis;
	}
}
