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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;


public class PeptidePlane implements HasAtoms, Serializable
{
	private static final long serialVersionUID = 2623412566028308508L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Vector3 m_capAtom;
	private Vector3 m_cAtom;
	private Vector3 m_oAtom;
	private Vector3 m_nAtom;
	private Vector3 m_hAtom;
	private Vector3 m_canAtom;
	private transient List<Atom> m_atoms;
	private transient List<AtomAddressInternal> m_atomIndex;
	private transient List<AtomAddressInternal> m_backboneIndex;
	private transient Matrix3 m_basis;

	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public PeptidePlane( )
	{
		m_capAtom = new Vector3();
		m_cAtom = new Vector3();
		m_oAtom = new Vector3();
		m_nAtom = new Vector3();
		m_hAtom = new Vector3();
		m_canAtom = new Vector3();
		
		// get an arbitrary orientation for the plane
		Peptide peptide = new Peptide();
		peptide.update();
		set( peptide.getNextPlane() );
	}
	
	public PeptidePlane( Vector3 capAtom, Vector3 cAtom, Vector3 oAtom, Vector3 nAtom, Vector3 hAtom, Vector3 canAtom )
	{
		m_capAtom = capAtom;
		m_cAtom = cAtom;
		m_oAtom = oAtom;
		m_nAtom = nAtom;
		m_hAtom = hAtom;
		m_canAtom = canAtom;
		
		// is the amide proton missing? simulate it
		if( m_hAtom == null )
		{
			m_hAtom = simulateAmideProton( capAtom, cAtom, nAtom );
		}
		
		computeIndex();
		computeBasis();
	}
	
	public PeptidePlane( PeptidePlane other )
	{
		set( other );
	}
	
	private void readObject( ObjectInputStream in )
    throws ClassNotFoundException, IOException
    {
		in.defaultReadObject();
		computeIndex();
		computeBasis();
    }
	
	public PeptidePlane clone( )
	{
		return new PeptidePlane( this );
	}
	

	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getCapAtom( )
	{
		return m_capAtom;
	}
	
	public Vector3 getCAtom( )
	{
		return m_cAtom;
	}
	
	public Vector3 getOAtom( )
	{
		return m_oAtom;
	}
	
	public Vector3 getNAtom( )
	{
		return m_nAtom;
	}
	
	public Vector3 getHAtom( )
	{
		return m_hAtom;
	}
	
	public Vector3 getCanAtom( )
	{
		return m_canAtom;
	}
	
	public void getCaCVector( Vector3 out )
	{
		getBondVector( out, m_capAtom, m_cAtom );
	}
	
	public void getCOVector( Vector3 out )
	{
		getBondVector( out, m_cAtom, m_oAtom );
	}

	public void getCNVector( Vector3 out )
	{
		getBondVector( out, m_cAtom, m_nAtom );
	}

	public void getNHVector( Vector3 out )
	{
		getBondVector( out, m_nAtom, m_hAtom );
	}

	public void getNCaVector( Vector3 out )
	{
		getBondVector( out, m_nAtom, m_canAtom );
	}
	
	public double getAngle( BondType aType, BondType bType )
	{
		Vector3 a = new Vector3();
		aType.getBondVector( a, this );
		Vector3 b = new Vector3();
		bType.getBondVector( b, this );
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
		return 6;
	}
	
	@Override
	public int getNumBackboneAtoms( )
	{
		return 4;
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
	
	public void set( PeptidePlane other )
	{
		m_capAtom = new Vector3( other.m_capAtom );
		m_cAtom = new Vector3( other.m_cAtom );
		m_oAtom = new Vector3( other.m_oAtom );
		m_nAtom = new Vector3( other.m_nAtom );
		m_hAtom = new Vector3( other.m_hAtom );
		m_canAtom = new Vector3( other.m_canAtom );
		m_hAtom = new Vector3( other.m_hAtom );
		
		computeIndex();
		computeBasis();
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static PeptidePlane newFromAfterResidueByNumber( Subunit subunit, int residueNumber )
	{
		return newFromAfterResidue( subunit, subunit.getResidueByNumber( residueNumber ).getId() );
	}
	
	public static PeptidePlane newFromAfterResidue( Subunit subunit, int residueId )
	{
		Residue i = subunit.getResidue( residueId );
		Residue n = subunit.getResidue( residueId + 1 );
		
		// is the amide proton missing? (eg, proline)
		Atom amideProton = n.getAtomByName( "H" );
		Vector3 amideProtonPos = null;
		if( amideProton != null )
		{
			amideProtonPos = amideProton.getPosition();
		}
		
		return new PeptidePlane(
			new Vector3( i.getAtomByName( "CA" ).getPosition() ),
			new Vector3( i.getAtomByName( "C" ).getPosition() ),
			new Vector3( i.getAtomByName( "O" ).getPosition() ),
			new Vector3( n.getAtomByName( "N" ).getPosition() ),
			new Vector3( amideProtonPos ),
			new Vector3( n.getAtomByName( "CA" ).getPosition() )
		);
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private Vector3 simulateAmideProton( Vector3 capAtom, Vector3 cAtom, Vector3 nAtom )
	{
		Vector3 v = new Vector3();
		Quaternion q = new Quaternion();
		
		// get the NC bond vector
		Vector3 ncBond = new Vector3( cAtom );
		ncBond.subtract( nAtom );
		ncBond.normalize();
		
		// place the amide proton
		Vector3 hAtom = new Vector3( ncBond );
		hAtom.scale( IdealGeometry.LengthNH );
		Vector3.getNormal( v, nAtom, cAtom, capAtom );
		Quaternion.getRotation( q, v, -IdealGeometry.AngleCNH );
		hAtom.rotate( q );
		Quaternion.getRotation( q, ncBond, IdealGeometry.AngleOmega );
		hAtom.rotate( q );
		hAtom.add( nAtom );
		return hAtom;
	}
	
	private void computeIndex( )
	{
		// build the atoms and index them so HasAtoms works
		m_atoms = new ArrayList<Atom>( getNumAtoms() );
		m_atoms.add( newAtom( 0, "CA", true, m_capAtom ) );
		m_atoms.add( newAtom( 0, "C", true, m_cAtom ) );
		m_atoms.add( newAtom( 0, "O", false, m_oAtom ) );
		m_atoms.add( newAtom( 1, "N", true, m_nAtom ) );
		m_atoms.add( newAtom( 1, "H", false, m_hAtom ) );
		m_atoms.add( newAtom( 1, "CA", true, m_canAtom ) );
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
	
	private void computeBasis( )
	{
		Vector3 z = new Vector3();
		Vector3 y = new Vector3();
		Vector3 x = new Vector3();
		
		// the z axis is the NH vector
		z.set( m_hAtom );
		z.subtract( m_nAtom );
		z.normalize();
		
		// the y axis is normal to the plane
		Vector3.getNormal( y, m_hAtom, m_nAtom, m_cAtom );
		
		// the x axis is in the direction of the next Ca
		y.getCross( x, z );
		x.normalize();
		
		m_basis = new Matrix3();
		m_basis.setColumns( x, y, z );
	}
	
	private void getBondVector( Vector3 out, Vector3 source, Vector3 target )
	{
		out.set( target );
		out.subtract( source );
		out.normalize();
	}
}
