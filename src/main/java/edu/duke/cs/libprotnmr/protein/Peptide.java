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

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;

public class Peptide
{
	// UNDONE: this class really only represents C-ward directions
	// make it work N-wards someday as well...
	
	public double phi;
	public double psi;
	public double omega;
	
	private Vector3 m_nAtom;
	private Vector3 m_hAtom;
	private Vector3 m_caAtom;
	private Vector3 m_haAtom;
	private Vector3 m_cAtom;
	private Vector3 m_oAtom;
	private Vector3 m_nnAtom;
	private Vector3 m_hnAtom;
	
	public Peptide( )
	{
		// default dihedral angles
		phi = Math.toRadians( 180 );
		psi = Math.toRadians( 180 );
		omega = IdealGeometry.AngleOmega;
		
		// allocate memory for the atom positions
		m_nAtom = new Vector3();
		m_hAtom = new Vector3();
		m_caAtom = new Vector3();
		m_haAtom = new Vector3();
		m_cAtom = new Vector3();
		m_oAtom = new Vector3();
		m_nnAtom = new Vector3();
		m_hnAtom = new Vector3();
	}
	
	public Vector3 getH( )
	{
		return m_hAtom;
	}
	
	public Vector3 getN( )
	{
		return m_nAtom;
	}
	
	public Vector3 getCa( )
	{
		return m_caAtom;
	}
	
	public Vector3 getHa( )
	{
		return m_haAtom;
	}
	
	public Vector3 getC( )
	{
		return m_cAtom;
	}
	
	public Vector3 getO( )
	{
		return m_oAtom;
	}
	
	public Vector3 getHn( )
	{
		return m_hnAtom;
	}
	
	public Vector3 getNn( )
	{
		return m_nnAtom;
	}
	
	public void update( )
	{
		Quaternion q = new Quaternion();
		
		// first, put the NH vector along the positive z-axis
		Vector3 hPos = Vector3.getUnitZ();
		Vector3 nPos = Vector3.getOrigin();
		hPos.scale( IdealGeometry.LengthNH );
		
		// then add the CA in the direction of the x axis
		Vector3 caPos = Vector3.getUnitZ();
		caPos.scale( IdealGeometry.LengthNCa );
		Quaternion.getRotation( q, Vector3.getUnitY(), IdealGeometry.AngleHNCa );
		q.rotate( caPos );
		
		update( hPos, nPos, caPos );
	}
	
	public void update( PeptidePlane plane )
	{
		update( plane.getHAtom(), plane.getNAtom(), plane.getCanAtom() );
	}
	
	public void update( Vector3 hPos, Vector3 nPos, Vector3 caPos )
	{
		// just in case...
		assert( !Double.isNaN( phi ) );
		assert( !Double.isNaN( psi ) );
		
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		Vector3 w = new Vector3();
		
		m_hAtom.set( hPos );
		m_nAtom.set( nPos );
		m_caAtom.set( caPos );
		
		// add C (at phi=0)
		m_cAtom.set( m_nAtom );
		m_cAtom.subtract( m_caAtom );
		m_cAtom.scale( IdealGeometry.LengthCaC / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_caAtom, m_nAtom, m_hAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleNCaC );
		m_cAtom.rotate( q );
		getBondVector( v, m_nAtom, m_caAtom );
		Quaternion.getRotation( q, v, phi );
		m_cAtom.rotate( q );
		m_cAtom.add( m_caAtom );
		
		// add Ha (at phi=180)
		m_haAtom.set( m_nAtom );
		m_haAtom.subtract( m_caAtom );
		m_haAtom.scale( IdealGeometry.LengthCaHa / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_caAtom, m_nAtom, m_hAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleNCaC / 2.0 );
		q.rotate( m_haAtom );
		v.getCross( w, m_haAtom );
		w.normalize();
		Quaternion.getRotation( q, w, IdealGeometry.AngleHaOutOfPlane );
		q.rotate( m_haAtom );
		getBondVector( v, m_nAtom, m_caAtom );
		Quaternion.getRotation( q, v, phi - Math.PI );
		m_haAtom.rotate( q );
		m_haAtom.add( m_caAtom );
		
		// add O
		m_oAtom.set( m_caAtom );
		m_oAtom.subtract( m_cAtom );
		m_oAtom.scale( IdealGeometry.LengthCO / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_cAtom, m_caAtom, m_nAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCaCO );
		m_oAtom.rotate( q );
		getBondVector( v, m_caAtom, m_cAtom );
		Quaternion.getRotation( q, v, psi );
		m_oAtom.rotate( q );
		m_oAtom.add( m_cAtom );
		
		// add the next N (i.e., i + 1)
		m_nnAtom.set( m_caAtom );
		m_nnAtom.subtract( m_cAtom );
		m_nnAtom.scale( IdealGeometry.LengthCN / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_cAtom, m_caAtom, m_nAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleCaCN );
		m_nnAtom.rotate( q );
		getBondVector( v, m_caAtom, m_cAtom );
		Quaternion.getRotation( q, v, psi );
		m_nnAtom.rotate( q );
		m_nnAtom.add( m_cAtom );
		
		// add the next H
		m_hnAtom.set( m_cAtom );
		m_hnAtom.subtract( m_nnAtom );
		m_hnAtom.scale( IdealGeometry.LengthNH / IdealGeometry.LengthCN );
		Vector3.getNormal( v, m_nnAtom, m_cAtom, m_caAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCNH );
		m_hnAtom.rotate( q );
		getBondVector( v, m_cAtom, m_nnAtom );
		Quaternion.getRotation( q, v, omega );
		m_hnAtom.rotate( q );
		m_hnAtom.add( m_nnAtom );
	}
	
	public void getNnHnVector( Vector3 out )
	{
		getBondVector( out, m_nnAtom, m_hnAtom );
	}
	
	public void getNCaVector( Vector3 out )
	{
		getBondVector( out, m_nAtom, m_caAtom );
	}

	public void getCaHaVector( Vector3 out )
	{
		getBondVector( out, m_caAtom, m_haAtom );
	}
	
	public void getCaCVector( Vector3 out )
	{
		getBondVector( out, m_caAtom, m_cAtom );
	}
	
	public void getCNnVector( Vector3 out )
	{
		getBondVector( out, m_cAtom, m_nnAtom );
	}
	
	public void getCan( Vector3 out )
	{
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		
		out.set( m_hnAtom );
		out.subtract( m_nnAtom );
		out.scale( IdealGeometry.LengthNCa / IdealGeometry.LengthNH );
		Vector3.getNormal( v, m_hnAtom, m_nnAtom, m_cAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleHNCa );
		out.rotate( q );
		out.add( m_nnAtom );
	}
	
	public void getNnCanVector( Vector3 out )
	{
		Vector3 can = new Vector3();
		getCan( can );
		getBondVector( out, m_nnAtom, can );
	}
	
	public void translate( Vector3 t )
	{
		// translate all the atoms
		m_hAtom.add( t );
		m_nAtom.add( t );
		m_caAtom.add( t );
		m_cAtom.add( t );
		m_oAtom.add( t );
		m_nnAtom.add( t );
		m_hnAtom.add( t );
	}
	
	public void getBasis( Matrix3 out )
	{
		// UNDONE: optimize out the calls to new
		Vector3 z = new Vector3();
		Vector3 y = new Vector3();
		Vector3 x = new Vector3();
		
		// the z axis is the NH vector
		z.set( m_hnAtom );
		z.subtract( m_nnAtom );
		z.normalize();
		
		// the y axis is normal to the plane
		Vector3.getNormal( y, m_hnAtom, m_nnAtom, m_cAtom );
		
		// the x axis is in the direction of the next Ca
		y.getCross( x, z );
		x.normalize();
		
		out.setColumns( x, y, z );
	}
	
	public PeptidePlane getNextPlane( )
	{
		// build the next ca atom (ca+)
		Vector3 capAtom = new Vector3();
		getCan( capAtom );
		return new PeptidePlane(
			new Vector3( m_caAtom ),
			new Vector3( m_cAtom ),
			new Vector3( m_oAtom ),
			new Vector3( m_nnAtom ),
			new Vector3( m_hnAtom ),
			capAtom
		);
	}
	
	private void getBondVector( Vector3 out, Vector3 a, Vector3 b )
	{
		// returns vector ab, normalized
		out.set( b );
		out.subtract( a );
		out.normalize();
	}
}
