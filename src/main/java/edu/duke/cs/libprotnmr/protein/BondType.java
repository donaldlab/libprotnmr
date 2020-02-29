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
package edu.duke.cs.libprotnmr.protein;

import java.util.HashMap;
import java.util.Map;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.nmr.Rdc;


public enum BondType
{	
	/*********************************
	 *   Values
	 *********************************/
	
	CaC( "CA", "C", 0, 1 )
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			switch( direction )
			{
				case Cwards:
					dipeptide.getCwardsCaCVector( out );
				break;
				
				case Nwards:
					dipeptide.getNwardsCaCVector( out );
				break;
			}
		}
		
		@Override
		public void getBondVector( Vector3 out, PeptidePlane plane )
		{
			plane.getCaCVector( out );
		}
	},
	CO( "C", "O", 0, 1 )
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			switch( direction )
			{
				case Cwards:
					dipeptide.getCwardsCOVector( out );
				break;
				
				case Nwards:
					dipeptide.getNwardsCOVector( out );
				break;
			}
		}
		
		@Override
		public void getBondVector( Vector3 out, PeptidePlane plane )
		{
			plane.getCOVector( out );
		}
	},
	CN( "C", "N", 0, 1 ) // arbitrary decision: the peptide bond is always part of the residue containing the C atom
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			switch( direction )
			{
				case Cwards:
					dipeptide.getCwardsCNVector( out );
				break;
				
				case Nwards:
					dipeptide.getNwardsCNVector( out );
				break;
			}
		}
		
		@Override
		public void getBondVector( Vector3 out, PeptidePlane plane )
		{
			plane.getCNVector( out );
		}
		
		@Override
		public void getBondVectorByNumber( Vector3 out, Subunit subunit, int residueNumber )
		{
			Atom from = subunit.getAtomByNumber( residueNumber, getFromAtomName() );
			Atom to = subunit.getAtomByNumber( residueNumber + 1, getToAtomName() );
			out.set(to.getPosition());
			out.subtract(from.getPosition());
		}
	},
	NH( "N", "H", -1, 0 )
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			switch( direction )
			{
				case Cwards:
					dipeptide.getCwardsNHVector( out );
				break;
				
				case Nwards:
					dipeptide.getNwardsNHVector( out );
				break;
			}
		}
		
		@Override
		public void getBondVector( Vector3 out, PeptidePlane plane )
		{
			plane.getNHVector( out );
		}
	},
	NCa( "N", "CA", -1, 0 )
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			switch( direction )
			{
				case Cwards:
					dipeptide.getCwardsNCaVector( out );
				break;
				
				case Nwards:
					dipeptide.getNwardsNCaVector( out );
				break;
			}
		}
		
		@Override
		public void getBondVector( Vector3 out, PeptidePlane plane )
		{
			plane.getNCaVector( out );
		}
	},
	CaHa( "CA", "HA", 0, 0 )
	{
		@Override
		public void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction )
		{
			dipeptide.getCaHaVector( out );
		}
	};
	
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private static Map<String,BondType> m_atomNameIndex;
	
	private String m_fromAtomName;
	private String m_toAtomName;
	
	// Ok, this one needs a little explanation:
	// going cwards, this bond's orientation in governed by the phi,psi from which residue
	// relative to the actual residue that contains this bond
	private int m_cwardsGoverningResidueNumber;
	private int m_nwardsGoverningResidueNumber;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	static
	{
		// build the type lookup table
		m_atomNameIndex = new HashMap<String,BondType>();
		for( BondType type : values() )
		{
			m_atomNameIndex.put( getAtomNameKey( type.m_fromAtomName, type.m_toAtomName ), type );
		}
	}
	
	private BondType( String fromAtomName, String toAtomName, int cwardsGoverningResidueNumber, int nwardsGoverningResidueNumber )
	{
		// save parameters
		m_fromAtomName = fromAtomName;
		m_toAtomName = toAtomName;
		m_cwardsGoverningResidueNumber = cwardsGoverningResidueNumber;
		m_nwardsGoverningResidueNumber = nwardsGoverningResidueNumber;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public String getFromAtomName( )
	{
		return m_fromAtomName;
	}
	
	public String getToAtomName( )
	{
		return m_toAtomName;
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static BondType lookup( Rdc<AtomAddressReadable> rdc )
	{
		return m_atomNameIndex.get( getAtomNameKey( rdc.getFrom().getAtomName(), rdc.getTo().getAtomName() ) );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public abstract void getBondVector( Vector3 out, Dipeptide dipeptide, ChainDirection direction );
	
	public void getBondVector( Vector3 out, PeptidePlane plane )
	{
		throw new Error( "Bond type " + name() + " not in a peptide plane." );
	}
	
	public void getBondVectorByNumber( Vector3 out, Subunit subunit, int residueNumber )
	{
		Atom from = subunit.getAtomByNumber( residueNumber, m_fromAtomName );
		Atom to = subunit.getAtomByNumber( residueNumber, m_toAtomName );
		getBondVector( out, from, to );
	}
	
	public void getBondVectorByNumber( Vector3 out, Subunit subunit, int residueNumber, ChainDirection direction )
	{
		getBondVectorByNumber( out, subunit, getContainingResidueNumber( residueNumber, direction ) );
		/* doesn't work for CaHa
		switch( direction )
		{
			case Cwards:
				getBondVector( out, PeptidePlane.newFromAfterResidueByNumber( subunit, residueNumber ) );
			break;
			case Nwards:
				getBondVector( out, PeptidePlane.newFromAfterResidueByNumber( subunit, residueNumber - 1 ) );
			break;
		}
		*/
	}
	
	public int getGoverningResidueNumber( int containingResidueNumber, ChainDirection direction )
	{
		switch( direction )
		{
			case Cwards: return containingResidueNumber + m_cwardsGoverningResidueNumber;
			case Nwards: return containingResidueNumber + m_nwardsGoverningResidueNumber;
		}
		throw new Error( "Unknown direction: " + direction );
	}
	
	public int getContainingResidueNumber( int governingResidueNumber, ChainDirection direction )
	{
		switch( direction )
		{
			case Cwards: return governingResidueNumber - m_cwardsGoverningResidueNumber;
			case Nwards: return governingResidueNumber - m_nwardsGoverningResidueNumber;
		}
		throw new Error( "Unknown direction: " + direction );
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private static String getAtomNameKey( String fromAtomName, String toAtomName )
	{
		return fromAtomName.toLowerCase() + "-" + toAtomName.toLowerCase();
	}
	
	private static void getBondVector( Vector3 out, Atom from, Atom to )
	{
		out.set( to.getPosition() );
		out.subtract( from.getPosition() );
		out.normalize();
	}
}


