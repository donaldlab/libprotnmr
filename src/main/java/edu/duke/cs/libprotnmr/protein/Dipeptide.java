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
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Quaternion;


public class Dipeptide implements HasAtoms
{
	/**************************
	 *   Data Members
	 **************************/
	
	// dihedral angles
	private double m_phi;
	private double m_psi;
	
	// the n-wards peptide
	private Vector3 m_canAtom;
	private Vector3 m_cnAtom;
	private Vector3 m_onAtom;
	private Vector3 m_nnAtom;
	private Vector3 m_hnAtom;
	
	// the c-wards peptide
	private Vector3 m_ccAtom;
	private Vector3 m_ocAtom;
	private Vector3 m_ncAtom;
	private Vector3 m_hcAtom;
	private Vector3 m_cacAtom;
	
	// the middle atoms
	private Vector3 m_caAtom;
	private Vector3 m_haAtom;
	// UNDONE: add cb?
	
	private List<Atom> m_atoms;
	private List<AtomAddressInternal> m_atomIndex;
	private List<AtomAddressInternal> m_backboneIndex;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Dipeptide( )
	{
		m_phi = 0.0;
		m_psi = 0.0;
		
		// just initialize all the atoms to zero
		m_canAtom = new Vector3();
		m_cnAtom = new Vector3();
		m_onAtom = new Vector3();
		m_nnAtom = new Vector3();
		m_hnAtom = new Vector3();
		
		m_ccAtom = new Vector3();
		m_ocAtom = new Vector3();
		m_ncAtom = new Vector3();
		m_hcAtom = new Vector3();
		m_cacAtom = new Vector3();
		
		m_caAtom = new Vector3();
		m_haAtom = new Vector3();
		
		computeIndex();
	}
	
	public Dipeptide( Dipeptide other )
	{
		m_phi = other.m_phi;
		m_psi = other.m_psi;
		
		m_canAtom = new Vector3( other.m_canAtom );
		m_cnAtom = new Vector3( other.m_cnAtom );
		m_onAtom = new Vector3( other.m_onAtom );
		m_nnAtom = new Vector3( other.m_nnAtom );
		m_hnAtom = new Vector3( other.m_hnAtom );
		
		m_ccAtom = new Vector3( other.m_ccAtom );
		m_ocAtom = new Vector3( other.m_ocAtom );
		m_ncAtom = new Vector3( other.m_ncAtom );
		m_hcAtom = new Vector3( other.m_hcAtom );
		m_cacAtom = new Vector3( other.m_cacAtom );
		
		m_caAtom = new Vector3( other.m_caAtom );
		m_haAtom = new Vector3( other.m_haAtom );
		
		computeIndex();
	}
	
	public Dipeptide clone( )
	{
		return new Dipeptide( this );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setNwardsPlane( PeptidePlane plane )
	{
		m_canAtom.set( plane.getCapAtom() );
		m_cnAtom.set( plane.getCAtom() );
		m_onAtom.set( plane.getOAtom() );
		m_nnAtom.set( plane.getNAtom() );
		m_hnAtom.set( plane.getHAtom() );
		m_caAtom.set( plane.getCanAtom() );
	}
	public PeptidePlane getNwardsPlane( )
	{
		return new PeptidePlane(
			new Vector3( m_canAtom ),
			new Vector3( m_cnAtom ),
			new Vector3( m_onAtom ),
			new Vector3( m_nnAtom ),
			new Vector3( m_hnAtom ),
			new Vector3( m_caAtom )
		);
	}
	
	public void setCwardsPlane( PeptidePlane plane )
	{
		m_caAtom.set( plane.getCapAtom() );
		m_ccAtom.set( plane.getCAtom() );
		m_ocAtom.set( plane.getOAtom() );
		m_ncAtom.set( plane.getNAtom() );
		m_hcAtom.set( plane.getHAtom() );
		m_cacAtom.set( plane.getCanAtom() );
	}
	public PeptidePlane getCwardsPlane( )
	{
		return new PeptidePlane(
			new Vector3( m_caAtom ),
			new Vector3( m_ccAtom ),
			new Vector3( m_ocAtom ),
			new Vector3( m_ncAtom ),
			new Vector3( m_hcAtom ),
			new Vector3( m_cacAtom )
		);
	}
	
	public void setPhi( double val )
	{
		m_phi = val;
	}
	public double getPhi( )
	{
		
		return m_phi;
	}
	
	public void setPsi( double val )
	{
		m_psi = val;
	}
	public double getPsi( )
	{
		return m_psi;
	}
	
	public Vector3 getNwardsCaAtom( )
	{
		return m_canAtom;
	}
	
	public Vector3 getNwardsCAtom( )
	{
		return m_cnAtom;
	}
	
	public Vector3 getNwardsOAtom( )
	{
		return m_onAtom;
	}
	
	public Vector3 getNwardsNAtom( )
	{
		return m_nnAtom;
	}
	
	public Vector3 getNwardsHAtom( )
	{
		return m_hnAtom;
	}
	
	public Vector3 getCaAtom( )
	{
		return m_caAtom;
	}
	
	public Vector3 getHaAtom( )
	{
		return m_haAtom;
	}
	
	public Vector3 getCwardsCAtom( )
	{
		return m_ccAtom;
	}
	
	public Vector3 getCwardsOAtom( )
	{
		return m_ocAtom;
	}
	
	public Vector3 getCwardsNAtom( )
	{
		return m_ncAtom;
	}
	
	public Vector3 getCwardsHAtom( )
	{
		return m_hcAtom;
	}
	
	public Vector3 getCwardsCaAtom( )
	{
		return m_cacAtom;
	}
	
	public void getNwardsCaCVector( Vector3 out )
	{
		getBondVector( out, m_canAtom, m_cnAtom );
	}
	
	public void getNwardsCOVector( Vector3 out )
	{
		getBondVector( out, m_cnAtom, m_onAtom );
	}
	
	public void getNwardsCNVector( Vector3 out )
	{
		getBondVector( out, m_cnAtom, m_nnAtom );
	}
	
	public void getNwardsNCaVector( Vector3 out )
	{
		getBondVector( out, m_nnAtom, m_caAtom );
	}
	
	public void getNwardsNHVector( Vector3 out )
	{
		getBondVector( out, m_nnAtom, m_hnAtom );
	}
	
	public void getCaHaVector( Vector3 out )
	{
		getBondVector( out, m_caAtom, m_haAtom );
	}
	
	public void getCwardsCaCVector( Vector3 out )
	{
		getBondVector( out, m_caAtom, m_ccAtom );
	}
	
	public void getCwardsCOVector( Vector3 out )
	{
		getBondVector( out, m_ccAtom, m_ocAtom );
	}
	
	public void getCwardsCNVector( Vector3 out )
	{
		getBondVector( out, m_ccAtom, m_ncAtom );
	}
	
	public void getCwardsNCaVector( Vector3 out )
	{
		getBondVector( out, m_ncAtom, m_cacAtom );
	}
	
	public void getCwardsNHVector( Vector3 out )
	{
		getBondVector( out, m_ncAtom, m_hcAtom );
	}
	
	public double getAngle( BondType aType, ChainDirection aDirection, BondType bType, ChainDirection bDirection )
	{
		Vector3 a = new Vector3();
		aType.getBondVector( a, this, aDirection );
		Vector3 b = new Vector3();
		bType.getBondVector( b, this, bDirection );
		return Math.acos( a.getDot( b ) );
	}
	
	@Override
	public Atom getAtom( int subunitId, int residueId, int atomId )
	{
		return m_atoms.get( atomId );
	}
	
	@Override
	public Atom getAtom( AtomAddressInternal address )
	{
		return getAtom( address.getSubunitId(), address.getResidueId(), address.getAtomId() );
	}
	
	@Override
	public int getNumAtoms( )
	{
		return 12;
	}
	
	@Override
	public int getNumBackboneAtoms( )
	{
		return 7;
	}
	
	@Override
	public List<AtomAddressInternal> atoms( )
	{
		return m_atomIndex;
	}
	
	@Override
	public List<AtomAddressInternal> backboneAtoms( )
	{
		return m_backboneIndex;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getAngle( BondType aType, ChainDirection aDirection, BondType bType, ChainDirection bDirection, double phi, double psi )
	{
		Dipeptide dipeptide = new Dipeptide();
		dipeptide.setNwardsPlane( new PeptidePlane() );
		dipeptide.updateCwardsPlane( phi, psi );
		return dipeptide.getAngle( aType, aDirection, bType, bDirection );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void updateNwardsPlane( double phi, double psi )
	{
		setPhi( phi );
		setPsi( psi );
		updateNwardsPlane();
	}
	
	public void updateNwardsPlane( )
	{
		// just in case...
		assert( !Double.isNaN( m_phi ) );
		assert( !Double.isNaN( m_psi ) );
		
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		Vector3 w = new Vector3();
		
		// forward kinematics in the nwards direction
		
		// add Ha
		m_haAtom.set( m_ccAtom );
		m_haAtom.subtract( m_caAtom );
		m_haAtom.scale( IdealGeometry.LengthCaHa / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_ocAtom, m_ccAtom, m_caAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleNCaC / 2.0 );
		q.rotate( m_haAtom );
		v.getCross( w, m_haAtom );
		w.normalize();
		Quaternion.getRotation( q, w, -IdealGeometry.AngleHaOutOfPlane );
		q.rotate( m_haAtom );
		getBondVector( v, m_ccAtom, m_caAtom );
		Quaternion.getRotation( q, v, m_psi );
		m_haAtom.rotate( q );
		m_haAtom.add( m_caAtom );
		
		// add N
		m_nnAtom.set( m_ccAtom );
		m_nnAtom.subtract( m_caAtom );
		m_nnAtom.scale( IdealGeometry.LengthNCa / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_ocAtom, m_ccAtom, m_caAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleNCaC );
		q.rotate( m_nnAtom );
		getBondVector( v, m_ccAtom, m_caAtom );
		Quaternion.getRotation( q, v, m_psi );
		m_nnAtom.rotate( q );
		m_nnAtom.add( m_caAtom );
		
		// add H
		m_hnAtom.set( m_caAtom );
		m_hnAtom.subtract( m_nnAtom );
		m_hnAtom.scale( IdealGeometry.LengthNH / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_ccAtom, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleCNH );
		q.rotate( m_hnAtom );
		getBondVector( v, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, m_phi );
		m_hnAtom.rotate( q );
		m_hnAtom.add( m_nnAtom );
		
		// add C
		m_cnAtom.set( m_caAtom );
		m_cnAtom.subtract( m_nnAtom );
		m_cnAtom.scale( IdealGeometry.LengthCN / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_ccAtom, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCNCa );
		q.rotate( m_cnAtom );
		getBondVector( v, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, m_phi );
		m_cnAtom.rotate( q );
		m_cnAtom.add( m_nnAtom );
		
		// add O
		m_onAtom.set( m_nnAtom );
		m_onAtom.subtract( m_cnAtom );
		m_onAtom.scale( IdealGeometry.LengthCO / IdealGeometry.LengthCN );
		Vector3.getNormal( v, m_caAtom, m_nnAtom, m_cnAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleOCN );
		q.rotate( m_onAtom );
		getBondVector( v, m_nnAtom, m_cnAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleOmega );
		m_onAtom.rotate( q );
		m_onAtom.add( m_cnAtom );
		
		// add Ca
		m_canAtom.set( m_nnAtom );
		m_canAtom.subtract( m_cnAtom );
		m_canAtom.scale( IdealGeometry.LengthCaC / IdealGeometry.LengthCN );
		Vector3.getNormal( v, m_caAtom, m_nnAtom, m_cnAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCaCN );
		q.rotate( m_canAtom );
		getBondVector( v, m_nnAtom, m_cnAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleOmega );
		m_canAtom.rotate( q );
		m_canAtom.add( m_cnAtom );
	}
	
	public void updateCwardsPlane( double phi, double psi )
	{
		setPhi( phi );
		setPsi( psi );
		updateCwardsPlane();
	}
	
	public void updateCwardsPlane( )
	{
		// just in case...
		assert( !Double.isNaN( m_phi ) );
		assert( !Double.isNaN( m_psi ) );
		
		Quaternion q = new Quaternion();
		Vector3 v = new Vector3();
		Vector3 w = new Vector3();
		
		// forward kinematics in the cwards direction
		
		// add Ha
		m_haAtom.set( m_nnAtom );
		m_haAtom.subtract( m_caAtom );
		m_haAtom.scale( IdealGeometry.LengthCaHa / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_caAtom, m_nnAtom, m_hnAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleNCaC / 2.0 );
		q.rotate( m_haAtom );
		m_haAtom.getCross( w, v );
		w.normalize();
		Quaternion.getRotation( q, w, IdealGeometry.AngleHaOutOfPlane );
		q.rotate( m_haAtom );
		getBondVector( v, m_nnAtom, m_caAtom );
		Quaternion.getRotation( q, v, m_phi );
		m_haAtom.rotate( q );
		m_haAtom.add( m_caAtom );
		
		// add C
		m_ccAtom.set( m_nnAtom );
		m_ccAtom.subtract( m_caAtom );
		m_ccAtom.scale( IdealGeometry.LengthCaC / IdealGeometry.LengthNCa );
		Vector3.getNormal( v, m_caAtom, m_nnAtom, m_hnAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleNCaC );
		m_ccAtom.rotate( q );
		getBondVector( v, m_nnAtom, m_caAtom );
		Quaternion.getRotation( q, v, m_phi );
		m_ccAtom.rotate( q );
		m_ccAtom.add( m_caAtom );
		
		// add O
		m_ocAtom.set( m_caAtom );
		m_ocAtom.subtract( m_ccAtom );
		m_ocAtom.scale( IdealGeometry.LengthCO / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_ccAtom, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCaCO );
		m_ocAtom.rotate( q );
		getBondVector( v, m_caAtom, m_ccAtom );
		Quaternion.getRotation( q, v, m_psi );
		m_ocAtom.rotate( q );
		m_ocAtom.add( m_ccAtom );
		
		// add N
		m_ncAtom.set( m_caAtom );
		m_ncAtom.subtract( m_ccAtom );
		m_ncAtom.scale( IdealGeometry.LengthCN / IdealGeometry.LengthCaC );
		Vector3.getNormal( v, m_ccAtom, m_caAtom, m_nnAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleCaCN );
		m_ncAtom.rotate( q );
		getBondVector( v, m_caAtom, m_ccAtom );
		Quaternion.getRotation( q, v, m_psi );
		m_ncAtom.rotate( q );
		m_ncAtom.add( m_ccAtom );
		
		// add H
		m_hcAtom.set( m_ccAtom );
		m_hcAtom.subtract( m_ncAtom );
		m_hcAtom.scale( IdealGeometry.LengthNH / IdealGeometry.LengthCN );
		Vector3.getNormal( v, m_ncAtom, m_ccAtom, m_caAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCNH );
		m_hcAtom.rotate( q );
		getBondVector( v, m_ccAtom, m_ncAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleOmega );
		m_hcAtom.rotate( q );
		m_hcAtom.add( m_ncAtom );
		
		// add Ca
		m_cacAtom.set( m_hcAtom );
		m_cacAtom.subtract( m_ncAtom );
		m_cacAtom.scale( IdealGeometry.LengthNCa / IdealGeometry.LengthNH );
		Vector3.getNormal( v, m_hcAtom, m_ncAtom, m_ccAtom );
		Quaternion.getRotation( q, v, IdealGeometry.AngleHNCa );
		m_cacAtom.rotate( q );
		m_cacAtom.add( m_ncAtom );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void getBondVector( Vector3 out, Vector3 a, Vector3 b )
	{
		// returns vector ab, normalized
		out.set( b );
		out.subtract( a );
		out.normalize();
	}
	
	private void computeIndex( )
	{
		// build the atoms and index them so HasAtoms works
		m_atoms = new ArrayList<Atom>( getNumAtoms() );
		m_atoms.add( newAtom( 0, "CA", true, m_canAtom ) );
		m_atoms.add( newAtom( 0, "C", true, m_cnAtom ) );
		m_atoms.add( newAtom( 0, "O", false, m_onAtom ) );
		m_atoms.add( newAtom( 1, "N", true, m_nnAtom ) );
		m_atoms.add( newAtom( 1, "H", false, m_hnAtom ) );
		m_atoms.add( newAtom( 1, "CA", true, m_caAtom ) );
		m_atoms.add( newAtom( 1, "HA", false, m_haAtom ) );
		m_atoms.add( newAtom( 1, "C", true, m_ccAtom ) );
		m_atoms.add( newAtom( 1, "O", false, m_ocAtom ) );
		m_atoms.add( newAtom( 2, "N", true, m_ncAtom ) );
		m_atoms.add( newAtom( 2, "H", false, m_hcAtom ) );
		m_atoms.add( newAtom( 2, "CA", true, m_cacAtom ) );
		
		m_atomIndex = new ArrayList<AtomAddressInternal>( getNumAtoms() );
		m_backboneIndex = new ArrayList<AtomAddressInternal>( getNumBackboneAtoms() );
		for( int i=0; i<m_atoms.size(); i++ )
		{
			Atom atom = m_atoms.get( i );
			atom.setId( i );
			AtomAddressInternal address = new AtomAddressInternal( 0, atom.getResidueId(), i );
			m_atomIndex.add( address );
			if( atom.isBackbone() )
			{
				m_backboneIndex.add( address );
			}
		}
	}
	
	private Atom newAtom( int residueId, String atomName, boolean isBackbone, Vector3 pos )
	{
		Atom atom = new Atom();
		atom.setResidueId( residueId );
		atom.setPosition( pos );
		atom.setElement( Element.getByAtomName( atomName ) );
		atom.setIsBackbone( isBackbone );
		atom.setIsPseudoatom( false );
		return atom;
	}
}
