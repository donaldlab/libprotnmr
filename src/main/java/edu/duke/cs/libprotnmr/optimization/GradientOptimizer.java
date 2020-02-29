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
package edu.duke.cs.libprotnmr.optimization;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;


public class GradientOptimizer
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final double[] GradientDampers = { 0.2, 0.4, 0.8 }; // damping rate for gradient descent
	
	private static class OptimumInfo
	{
		public double x;
		public Double lowerBound;
		public Double upperBound;
		
		public OptimumInfo( )
		{
			x = 0.0;
			lowerBound = null;
			upperBound = null;
		}
		
		public boolean isBoundComplete( )
		{
			return lowerBound != null && upperBound != null;
		}
		
		public void updateBound( double gradient )
		{
			// if the gradient is positive, the root is towards negative x
			if( gradient > 0.0 )
			{
				upperBound = x;
			}
			else if( gradient < 0.0 )
			{
				lowerBound = x;
			}
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	@SuppressWarnings( "unused" )
	public static double getLocalMinimum( DifferentiableFunction f, double guess, double epsilon, int maxNumIterations )
	throws OptimizerFailureException
	{
		OptimumInfo info = new OptimumInfo();
		info.x = guess;
		
		// DEBUG
		Kinemage kin = null;
		if( false )
		{
			kin = new Kinemage();
			KinemageBuilder.appendAxes( kin );
			
			// sample the function and its derivative
			List<Vector3> functionSamples = new ArrayList<Vector3>();
			List<Vector3> derivativeSamples = new ArrayList<Vector3>();
			for( double x=-1.0; x<=1.0; x+=0.001 )
			{
				functionSamples.add( new Vector3( x, f.getValue( x ), 0.0 ) );
				derivativeSamples.add( new Vector3( x, f.getDerivative( x ), 0.0 ) );
			}
			KinemageBuilder.appendPoints( kin, functionSamples, "F", KinemageColor.Cobalt, 2 );
			KinemageBuilder.appendPoints( kin, derivativeSamples, "F'", KinemageColor.Lime, 2 );
		}
		
		// did we just happen to guess it right?
		double yp = f.getDerivative( info.x );
		if( CompareReal.eq( yp, 0.0, epsilon ) )
		{
			return info.x;
		}
		
		if( kin != null )
		{
			KinemageBuilder.appendPoint( kin, new Vector3( info.x, f.getValue( info.x ), 0.0 ), "Guess", KinemageColor.Orange, 7 );
		}
		
		// do gradient descent
		for( int i=0; i<maxNumIterations; i++ )
		{
			double gradient = f.getDerivative( info.x );
			
			// did we bound the root? Switch to a more efficient method
			info.updateBound( gradient );
			if( info.isBoundComplete() )
			{
				double x = findBoundedOptimum( f, info, epsilon );
				
				if( kin != null )
				{
					KinemageBuilder.appendPoint( kin, new Vector3( x, f.getValue( x ), 0.0 ), "Minimum", KinemageColor.Green, 7 );
					new KinemageWriter().show( kin );
				}
				
				return x;
			}
			
			// what's the best damper?
			double bestDamper = GradientDampers[0];
			double bestValue = info.x - bestDamper*gradient;
			for( int j=1; j<GradientDampers.length; j++ )
			{
				double damper = GradientDampers[j];
				double nextValue = info.x - damper*gradient;
				
				if( nextValue < bestValue )
				{
					bestValue = nextValue;
					bestDamper = damper;
				}
			}
			
			// get the next x
			info.x = bestValue;
			yp = f.getDerivative( info.x );
			if( CompareReal.eq( yp, 0.0, epsilon ) )
			{
				if( kin != null )
				{
					KinemageBuilder.appendPoint( kin, new Vector3( info.x, f.getValue( info.x ), 0.0 ), "Minimum", KinemageColor.Green, 7 );
					new KinemageWriter().show( kin );
				}
				
				return info.x;
			}
			
			if( kin != null )
			{
				KinemageBuilder.appendPoint( kin, new Vector3( info.x, f.getValue( info.x ), 0.0 ), "Point " + yp, KinemageColor.Orange, 5 );
			}
		}
		
		if( kin != null )
		{
			new KinemageWriter().show( kin );
		}
		
		// did we run out of steps? Maybe we oscillated. Check to see if we have bounds
		if( info.isBoundComplete() )
		{
			return findBoundedOptimum( f, info, epsilon );
		}
		
		throw new OptimizerFailureException( "Unable to find minimum in " + maxNumIterations + " iterations!" );
	}
	
	public static double findBoundedOptimum( DifferentiableFunction f, double min, double max, double epsilon )
	throws OptimizerFailureException
	{
		OptimumInfo info = new OptimumInfo();
		info.lowerBound = min;
		info.upperBound = max;
		return findBoundedOptimum( f, info, epsilon );
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static double findBoundedOptimum( DifferentiableFunction f, OptimumInfo info, double epsilon )
	throws OptimizerFailureException
	{
		// evaluate the function at the bounds
		double fLower = f.getDerivative( info.lowerBound );
		double fUpper = f.getDerivative( info.upperBound );
		
		// are the bounds well defined?
		if( Double.isNaN( fLower ) || Double.isNaN( fUpper ) )
		{
			throw new IllegalArgumentException( "Function derivative is undefined at the bounds!" );
		}

		// is there an optimum in this bound?
		if( CompareReal.eq( fLower, 0.0, epsilon ) )
		{
			return info.lowerBound;
		}
		else if( CompareReal.eq( fUpper, 0.0, epsilon ) )
		{
			return info.lowerBound;
		}
		else if( Math.signum( fLower ) == Math.signum( fUpper ) )
		{
			throw new OptimizerFailureException( "No optimum in this bound." );
		}
		
		// ok, find it using interval bisection
		while( true )
		{
			double mid = ( info.upperBound + info.lowerBound )/2.0;
			double fMid = f.getDerivative( mid );
			if( Math.abs( fMid ) < epsilon )
			{
				// we win!! =D
				return mid;
			}
			if( Math.signum( fMid ) == Math.signum( fLower ) )
			{
				// update the lower bound
				info.lowerBound = mid;
				fLower = fMid;
			}
			else
			{
				// update the upper bound
				info.upperBound = mid;
				fUpper = fMid;
			}
			
			// loop invariant: optimum is always in the interval
			assert( Math.signum( fLower ) != Math.signum( fUpper ) );
		}
	}
}
