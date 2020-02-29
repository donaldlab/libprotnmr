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

import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;

public enum RdcType
{
	/**************************
	 *   Values
	 **************************/
	
	NH( RdcType.GammaN, RdcType.GammaH, RdcType.RadiusNH )
	{
		@Override
		public boolean isThisType( AtomAddressReadable left, AtomAddressReadable right )
		{
			return left.getAtomName().equalsIgnoreCase( "N" )
				&& right.getAtomName().equalsIgnoreCase( "H" )
				&& left.getResidueNumber() == right.getResidueNumber();
		}
	},
	CaHa( RdcType.GammaC, RdcType.GammaH, RdcType.RadiusCaHa )
	{
		@Override
		public boolean isThisType( AtomAddressReadable left, AtomAddressReadable right )
		{
			return left.getAtomName().equalsIgnoreCase( "CA" )
				&& right.getAtomName().equalsIgnoreCase( "HA" )
				&& left.getResidueNumber() == right.getResidueNumber();
		}
	};
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	// gyromanetic ratios
	private static final double GammaH = 2.675198e4;
	private static final double GammaN = -2.7116e3;
	private static final double GammaC = 6.7283e3;
	
	// bond lengths for RDCs
	private static final double RadiusNH = 1.042;
	private static final double RadiusCaHa = 1.119;

	
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_dMax;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private RdcType( double ga, double gb, double r )
	{
		m_dMax = ga*gb/r/r/r;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getDMax( )
	{
		return m_dMax;
	}
	
	public double getScaling( RdcType to )
	{
		return to.m_dMax/m_dMax;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static RdcType lookup( Rdc<AtomAddressReadable> rdc )
	{
		// use the first assignment of the RDC for the addresses
		Assignment<AtomAddressReadable> assignment = new AssignmentIterator<AtomAddressReadable>( rdc ).next();
		
		for( RdcType type : values() )
		{
			if( type.isThisType( assignment.getLeft(), assignment.getRight() ) )
			{
				return type;
			}
		}
		return null;
	}
	
	public static void scaleRdc( Rdc<AtomAddressReadable> rdc, RdcType to )
	{
		scaleRdc( rdc, lookup( rdc ), to );
	}
	
	public static void scaleRdcs( Iterable<Rdc<AtomAddressReadable>> rdcs, RdcType to )
	{
		scaleRdcs( rdcs, lookup( rdcs.iterator().next() ), to );
	}
	
	public static <T extends AtomAddress<T>> void scaleRdc( Rdc<T> rdc, RdcType from, RdcType to )
	{
		rdc.setValue( rdc.getValue()*from.getScaling( to ) );
	}
	
	public static <T extends AtomAddress<T>> void scaleRdcs( Iterable<Rdc<T>> rdcs, RdcType from, RdcType to )
	{
		for( Rdc<T> rdc : rdcs )
		{
			scaleRdc( rdc, from, to );
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public abstract boolean isThisType( AtomAddressReadable left, AtomAddressReadable right );
}
