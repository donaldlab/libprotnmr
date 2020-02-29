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
