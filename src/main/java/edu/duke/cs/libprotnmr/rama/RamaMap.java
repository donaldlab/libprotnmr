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

public class RamaMap implements Iterable<RamaSample>
{
	/**************************
	 *   Definitions
	 **************************/
	
	protected static final int NumSamplesPerAngle = 180;
	private static final double DeltaDegrees = 2.0;
	
	private static enum BoundSide
	{
		Low,
		High;
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private double[][] m_samples;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public RamaMap( )
	{
		this( new double[NumSamplesPerAngle][NumSamplesPerAngle] );
	}
	
	public RamaMap( double[][] samples )
	{
		m_samples = samples;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getSample( double phiDegrees, double psiDegrees )
	{
		return m_samples[mapDegreesToIndex( phiDegrees )][mapDegreesToIndex( psiDegrees )];
	}
	
	public void setSample( double phiDegrees, double psiDegrees, double val )
	{
		m_samples[mapDegreesToIndex( phiDegrees )][mapDegreesToIndex( psiDegrees )] = val;
	}
	
	public Iterator<RamaSample> iterator( )
	{
		return new RamaSampleIterator( m_samples );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean isSampleAbove( double phiDegrees, double psiDegrees, double cutoff )
	{
		return interpolateValue( phiDegrees, psiDegrees ) >= cutoff;
	}
	
	public boolean isBoxAbove( double minPhiDegrees, double maxPhiDegrees, double minPsiDegrees, double maxPsiDegrees, double cutoff )
	{
		minPhiDegrees = mapMinus180To180( minPhiDegrees );
		maxPhiDegrees = mapMinus180To180( maxPhiDegrees );
		minPsiDegrees = mapMinus180To180( minPsiDegrees );
		maxPsiDegrees = mapMinus180To180( maxPsiDegrees );
		
		// are any of the corners allowed?
		if( isSampleAbove( minPhiDegrees, minPsiDegrees, cutoff ) )
		{
			return true;
		}
		if( isSampleAbove( maxPhiDegrees, minPsiDegrees, cutoff ) )
		{
			return true;
		}
		if( isSampleAbove( maxPhiDegrees, maxPsiDegrees, cutoff ) )
		{
			return true;
		}
		if( isSampleAbove( minPhiDegrees, maxPsiDegrees, cutoff ) )
		{
			return true;
		}
		
		// map the corners of the box to the grid
		int mini = mapBoundToIndex( minPhiDegrees, BoundSide.Low );
		int minj = mapBoundToIndex( minPsiDegrees, BoundSide.Low );
		int maxi = mapBoundToIndex( maxPhiDegrees, BoundSide.High );
		int maxj = mapBoundToIndex( maxPsiDegrees, BoundSide.High );
		
		// enumerate the samples in the box and check them all
		// NOTE: there's probably a faster/better way to do this check, but this works and is fast enough for now
		for( int i=mini; i<=maxi; i++ )
		{
			for( int j=minj; j<=maxj; j++ )
			{
				if( m_samples[i][j] >= cutoff )
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isBoxCompletelyAbove( double minPhiDegrees, double maxPhiDegrees, double minPsiDegrees, double maxPsiDegrees, double cutoff )
	{
		minPhiDegrees = mapMinus180To180( minPhiDegrees );
		maxPhiDegrees = mapMinus180To180( maxPhiDegrees );
		minPsiDegrees = mapMinus180To180( minPsiDegrees );
		maxPsiDegrees = mapMinus180To180( maxPsiDegrees );
		
		// are any of the corners disallowed?
		if( !isSampleAbove( minPhiDegrees, minPsiDegrees, cutoff ) )
		{
			return false;
		}
		if( !isSampleAbove( maxPhiDegrees, minPsiDegrees, cutoff ) )
		{
			return false;
		}
		if( !isSampleAbove( maxPhiDegrees, maxPsiDegrees, cutoff ) )
		{
			return false;
		}
		if( !isSampleAbove( minPhiDegrees, maxPsiDegrees, cutoff ) )
		{
			return false;
		}
		
		// map the corners of the box to the grid
		int mini = mapBoundToIndex( minPhiDegrees, BoundSide.Low );
		int minj = mapBoundToIndex( minPsiDegrees, BoundSide.Low );
		int maxi = mapBoundToIndex( maxPhiDegrees, BoundSide.High );
		int maxj = mapBoundToIndex( maxPsiDegrees, BoundSide.High );
		
		// enumerate the samples in the box and check them all
		// NOTE: there's probably a faster/better way to do this check, but this works and is fast enough for now
		for( int i=mini; i<=maxi; i++ )
		{
			for( int j=minj; j<=maxj; j++ )
			{
				if( m_samples[i][j] < cutoff )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private int mapBoundToIndex( double degrees, BoundSide side )
	{
		int index = mapDegreesToIndex( degrees );
		if( !isOnGrid( degrees ) && side == BoundSide.Low )
		{
			index = mapIndex( index + 1 );
		}
		return index;
	}

	/**************************
	 *   Static Functions
	 **************************/
	
	protected static int mapDegreesToIndex( double degrees )
	{
		// here's the mapping
		// -180 -> 179
		// -179.00001 -> 179
		// -179 -> 0
		// -178 -> 0
		// -177 -> 1
		// -176 -> 1
		// -175 -> 2
		// ...
		// 175 -> 177
		// 176 -> 177
		// 177 -> 178
		// 178 -> 178
		// 179 -> 179
		// 180 -> 179
		
		degrees = mapMinus180To180( degrees );
		if( degrees < -179.0 )
		{
			return 179;
		}
		return ( (int)(degrees) + 179 ) / 2;
	}
	
	protected boolean isOnGrid( double degrees )
	{
		if( degrees < -179.0 )
		{
			return false;
		}
		if( degrees > 179.0 )
		{
			return false;
		}
		return degrees % 2.0 == 0.0;
	}

	protected static double mapIndexToDegrees( int index )
	{
		return (double)( index * 2 - 179 );
	}
	
	private static double mapMinus180To180( double degrees )
	{
		while( degrees > 180.0 )
		{
			degrees -= 360.0;
		}
		while( degrees <= -180.0 )
		{
			degrees += 360.0;
		}
		return degrees;
	}
	
	private static int mapIndex( int index )
	{
		while( index < 0 )
		{
			index += NumSamplesPerAngle;
		}
		while( index >= NumSamplesPerAngle )
		{
			index -= NumSamplesPerAngle;
		}
		return index;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private double interpolateValue( double phiDegrees, double psiDegrees )
	{
		// implemented bilinear interpolation for this one:
		// http://en.wikipedia.org/wiki/Bilinear_interpolation
		
		phiDegrees = mapMinus180To180( phiDegrees );
		psiDegrees = mapMinus180To180( psiDegrees );
		
		// find out where in the grid this point is
		int mini = mapDegreesToIndex( phiDegrees );
		int minj = mapDegreesToIndex( psiDegrees );
		int maxi = mapIndex( mini + 1 );
		int maxj = mapIndex( minj + 1 );
		
		// get the values at the grid corners
		// d c
		// a b
		double a = m_samples[mini][minj];
		double b = m_samples[maxi][minj];
		double c = m_samples[maxi][maxj];
		double d = m_samples[mini][maxj];
		
		// get the angles at the grid corners
		double minphi = mapIndexToDegrees( mini );
		double minpsi = mapIndexToDegrees( minj );
		double maxphi = minphi + DeltaDegrees;
		double maxpsi = minpsi + DeltaDegrees;
		
		// interpolate
		return (
			  a*( maxphi - phiDegrees )*( maxpsi - psiDegrees )
			+ b*( phiDegrees - minphi )*( maxpsi - psiDegrees )
			+ c*( phiDegrees - minphi )*( psiDegrees - minpsi )
			+ d*( maxphi - phiDegrees )*( psiDegrees - minpsi )
		) / DeltaDegrees / DeltaDegrees;
	}
}
