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

package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.BondType;

import java.io.Serializable;

public class RdcBand implements Band, Serializable
{
	private static final long serialVersionUID = -4675589025420955712L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private AlignmentTensor m_tensor;
	private double m_rdcValue;
	private double m_rdcError;
	private BondType m_bondType;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcBand( AlignmentTensor tensor, Rdc<AtomAddressReadable> rdc )
	{
		m_tensor = tensor;
		m_rdcValue = rdc.getValue();
		m_rdcError = rdc.getError();
		m_bondType = BondType.lookup( rdc );
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public AlignmentTensor getTensor( )
	{
		return m_tensor;
	}
	public void setAlignmentTensor( AlignmentTensor val )
	{
		m_tensor = val;
	}
	
	public double getRdcValue( )
	{
		return m_rdcValue;
	}
	public void setRdcValue( double val )
	{
		m_rdcValue = val;
	}
	
	public double getRdcError( )
	{
		return m_rdcError;
	}
	public void setRdcError( double val )
	{
		m_rdcError = val;
	}
	
	public BondType getBondType( )
	{
		return m_bondType;
	}
	public void setBondType( BondType val )
	{
		m_bondType = val;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public RdcCurve getCurve( BandPart part, int arcnum )
	{
		return new RdcCurve( m_tensor, part.getRdcValue( this ), arcnum );
	}
	
	@Override
	public boolean containsPoint( Vector3 point )
	{
		// back-compute the RDC value
		double val = m_tensor.backComputeRdc( point );
		return CompareReal.gte( val, m_rdcValue - m_rdcError ) && CompareReal.lte( val, m_rdcValue + m_rdcError );
	}
	
	@Override
	public boolean boundaryContainsPoint( Vector3 point )
	{
		double val = m_tensor.backComputeRdc( point );
		return CompareReal.eq( val, m_rdcValue - m_rdcError ) || CompareReal.eq( val, m_rdcValue + m_rdcError );
	}
	
	@Override
	public boolean hasCurveOnBoundary( Curve curve )
	{
		return getCurve( BandPart.Max, 0 ).equals( curve )
			|| getCurve( BandPart.Max, 1 ).equals( curve )
			|| getCurve( BandPart.Min, 0 ).equals( curve )
			|| getCurve( BandPart.Min, 1 ).equals( curve );
	}
}
