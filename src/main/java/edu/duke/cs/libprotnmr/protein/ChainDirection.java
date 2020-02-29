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

public enum ChainDirection
{
	/*********************************
	 *   Values
	 *********************************/
	
	Cwards( BondType.NCa, BondType.CaC )
	{
		@Override
		public ChainDirection getOtherDirection( )
		{
			return Nwards;
		}

		@Override
		public PeptidePlane getDipeptidePlane( Dipeptide dipeptide )
		{
			return dipeptide.getCwardsPlane();
		}
		
		@Override
		public void setDipeptidePlane( Dipeptide dipeptide, PeptidePlane plane )
		{
			dipeptide.setCwardsPlane( plane );
		}
		
		@Override
		public void updatePeptidePlane( Dipeptide dipeptide )
		{
			dipeptide.updateCwardsPlane();
		}

		@Override
		public void updatePeptidePlane( Dipeptide dipeptide, double phi, double psi )
		{
			dipeptide.updateCwardsPlane( phi, psi );
		}
	},
	Nwards( BondType.CaC, BondType.NCa )
	{
		@Override
		public ChainDirection getOtherDirection( )
		{
			return Cwards;
		}

		@Override
		public PeptidePlane getDipeptidePlane( Dipeptide dipeptide )
		{
			return dipeptide.getNwardsPlane();
		}
		
		@Override
		public void setDipeptidePlane( Dipeptide dipeptide, PeptidePlane plane )
		{
			dipeptide.setNwardsPlane( plane );
		}
		
		@Override
		public void updatePeptidePlane( Dipeptide dipeptide )
		{
			dipeptide.updateNwardsPlane();
		}

		@Override
		public void updatePeptidePlane( Dipeptide dipeptide, double phi, double psi )
		{
			dipeptide.updateNwardsPlane( phi, psi );
		}
	};
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private BondType m_firstRotatableBondType;
	private BondType m_secondRotatableBondType;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	private ChainDirection( BondType firstRotatableBondType, BondType secondRotatableBondType )
	{
		m_firstRotatableBondType = firstRotatableBondType;
		m_secondRotatableBondType = secondRotatableBondType;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public BondType getFirstRotatableBondType( )
	{
		return m_firstRotatableBondType;
	}
	
	public BondType getSecondRotatableBondType( )
	{
		return m_secondRotatableBondType;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public abstract ChainDirection getOtherDirection( );
	public abstract void setDipeptidePlane( Dipeptide dipeptide, PeptidePlane plane );
	public abstract void updatePeptidePlane( Dipeptide dipeptide );
	public abstract void updatePeptidePlane( Dipeptide dipeptide, double phi, double psi );
	public abstract PeptidePlane getDipeptidePlane( Dipeptide dipeptide );
}
