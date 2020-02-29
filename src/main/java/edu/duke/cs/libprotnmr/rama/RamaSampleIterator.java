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
package edu.duke.cs.libprotnmr.rama;

import java.util.Iterator;

public class RamaSampleIterator implements Iterator<RamaSample>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double[][] m_samples;
	private int m_phiIndex;
	private int m_psiIndex;
	private RamaSample m_nextSample;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public RamaSampleIterator( double[][] samples )
	{
		m_samples = samples;
		m_phiIndex = 0;
		m_psiIndex = 0;
		m_nextSample = getNext();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean hasNext( )
	{
		return m_nextSample != null;
	}
	
	@Override
	public RamaSample next( )
	{
		RamaSample out = m_nextSample;
		m_nextSample = getNext();
		return out;
	}
	
	@Override
	public void remove( )
	{
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	private RamaSample getNext( )
	{
		RamaSample out = null;
		if( m_phiIndex < RamaMap.NumSamplesPerAngle && m_psiIndex < RamaMap.NumSamplesPerAngle )
		{
			out = new RamaSample(
				RamaMap.mapIndexToDegrees( m_phiIndex ),
				RamaMap.mapIndexToDegrees( m_psiIndex ),
				m_samples[m_phiIndex][m_psiIndex]
			);
		}
		
		// update the indices
		m_psiIndex++;
		if( m_psiIndex == RamaMap.NumSamplesPerAngle )
		{
			m_phiIndex++;
			m_psiIndex = 0;
		}
		
		return out;
	}
}
