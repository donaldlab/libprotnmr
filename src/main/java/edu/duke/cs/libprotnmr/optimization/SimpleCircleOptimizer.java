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

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.math.CompareReal;


public class SimpleCircleOptimizer
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final double GradientEpsilon = 1e-10; // cutoff for defining an optimum
	private static final double[] GradientDampers = { 0.05, 0.1, 0.2 }; // damping rate for gradient (a|de)scent
	private static final double GradientSteepEnough = 1e-4; // how steep should the gradient be to follow it
	private static final double GradientInitialDelta = 1e-10; // how small a theta step do we start with when looking for steeper gradients
	private static final double DeltaGrowthRate = 1.4; // delta *= rate
	private static final double OptimumEpsilon = 1e-10; // how precisely do we want to find bounded optima?
	private static final double RootEpsilon = 1e-10; // how precisely do we want to find bounded roots?
	private static final int MaxGradientIterations = 100000;
	
	public static enum Direction
	{
		Minimize( 1.0 )
		{
			@Override
			public Direction swap( )
			{
				return Maximize;
			}
		},
		Maximize( -1.0 )
		{
			@Override
			public Direction swap( )
			{
				return Minimize;
			}
		};
		
		private double m_factor;
		
		private Direction( double factor )
		{
			m_factor = factor;
		}
		
		public double getFactor( )
		{
			return m_factor;
		}
		
		public abstract Direction swap( );
	}
	
	private static class OptimumInfo
	{
		public double theta;
		public Direction direction;
		public CircleRange bound;
		
		private boolean m_isBoundComplete;
		
		public OptimumInfo( )
		{
			theta = 0.0;
			direction = Direction.Minimize;
			bound = CircleRange.newCircle();
			
			m_isBoundComplete = false;
		}
		
		public boolean isBoundComplete( )
		{
			return m_isBoundComplete;
		}
		
		public void updateTheta( double delta )
		{
			theta = CircleRange.mapMinusPiToPi( theta + delta );
		}
		
		public void resetBound( )
		{
			bound = CircleRange.newByOffset( theta, Math.PI*2.0 );
			m_isBoundComplete = false;
		}
		
		public void updateBound( double gradient )
		{
			// NOTE: the next root is always in the positive theta direction
			if( direction.getFactor()*gradient > 0.0 )
			{
				bound = CircleRange.newByCounterclockwiseSegment( bound.getSource(), theta );
				m_isBoundComplete = true;
			}
			else if( direction.getFactor()*gradient < 0.0 )
			{
				bound = CircleRange.newByCounterclockwiseSegment( theta, bound.getTarget() );
			}
		}
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static List<Double> getOptima( DifferentiableFunction f )
	throws TooManyOptimaException, OptimizerFailureException
	{
		return getOptima( f, null );
	}
	
	public static List<Double> getOptima( DifferentiableFunction f, Kinemage kin )
	throws TooManyOptimaException, OptimizerFailureException
	{
		OptimumInfo info = new OptimumInfo();
		
		// start at an arbitrary theta that's not an optimum!
		info.theta = 0.0;
		while( Math.abs( getGradient( f, info.theta ) ) < GradientSteepEnough )
		{
			// NOTE: want to pick an offset here that delays cycles in theta for as long as possible
			// ie, don't pick some rational fraction of PI
			info.updateTheta( 1.0 );
		}
		
		// VIS
		if( kin != null )
		{
			KinemageBuilder.appendPoint( kin, new Vector3( info.theta, f.getValue( info.theta ), 0.0 ), "Starting Theta", KinemageColor.LightGrey, 7 );
		}
		
		// do we minimize or maximize to get the first optimum?
		info.direction = getGradient( f, info.theta ) < 0 ? Direction.Minimize : Direction.Maximize;
		
		// find all the optima
		List<Double> optima = new ArrayList<Double>();
		while( true )
		{
			double optimum = getNextOptimum( f, info, kin );
			
			// VIS
			if( kin != null )
			{
				KinemageBuilder.appendPoint( kin,
					new Vector3( info.theta, f.getValue( info.theta ), 0.0 ),
					String.format( "Optimum @ %.5f %.5f", info.theta, f.getValue( info.theta ) ),
					KinemageColor.Orange, 7
				);
			}
			
			// did we find this optimum already?
			if( !optima.isEmpty() )
			{
				boolean found = false;
				for( int i=0; i<optima.size() && !found; i++ )
				{
					found |= CompareReal.eq( optimum, optima.get( i ), 2*OptimumEpsilon );
				}
				if( found )
				{
					break;
				}
			}
			
			// did we find too many optima?
			if( optima.size() == f.getMaxNumOptima() )
			{
				throw new TooManyOptimaException( optima );
			}
			
			optima.add( optimum );
			
			// set up the info for the next iteration
			info.theta = optimum;
			findSteeperGradient( f, info, kin );
		}
		return optima;
	}
	
	public static List<Double> getRoots( DifferentiableFunction f, List<Double> optima )
	{
		// PRECONDITION: optima are in counterclockwise order
		
		// find the roots
		List<Double> roots = new ArrayList<Double>();
		for( int i=0; i<optima.size(); i++ )
		{
			// the root (if one exists) must either be at an optima, or be bound between the two optima
			
			// get the interval between two optima
			double lower = optima.get( i );
			double upper = optima.get( ( i + 1 ) % optima.size() );
			
			// make sure the upper is actually bigger
			if( upper < lower )
			{
				upper += Math.PI*2.0;
			}
			
			// is there a root at an optimum?
			double fLower = f.getValue( lower );
			double fUpper = f.getValue( upper );
			if( fLower == 0.0 )
			{
				roots.add( fLower );
			}
			else if( fUpper == 0.0 )
			{
				roots.add( fUpper );
			}
			// is there a root in the interval?
			else if( Math.signum( fLower ) != Math.signum( fUpper ) )
			{
				roots.add( findRoot( f, lower, upper ) );
			}
			
			// no root here, move along
		}
		return roots;
	}
	
	public static Kinemage getKinemage( DifferentiableFunction f, List<Double> optima, List<Double> roots )
	{
		Kinemage kin = OptimizerKinemageBuilder.getKinemage( f );
		if( optima != null )
		{
			OptimizerKinemageBuilder.appendOptima( kin, f, optima );
		}
		if( roots != null )
		{
			OptimizerKinemageBuilder.appendRoots( kin, f, roots );
		}
		return kin;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static double getGradient( DifferentiableFunction f, double theta )
	throws OptimizerFailureException
	{
		double gradient = f.getDerivative( theta );
		
		// do we even have a gradient?
		if( Double.isNaN( gradient ) )
		{
			throw new OptimizerFailureException( "Gradient is undefined at theta = " + theta );
		}
		else if( Double.isInfinite( gradient ) )
		{
			throw new OptimizerFailureException( "Gradient is infinity at theta = " + theta );
		}
		
		return gradient;
	}
	
	private static double getNextOptimum( DifferentiableFunction f, OptimumInfo info, Kinemage kin )
	throws OptimizerFailureException
	{
		info.resetBound();
		
		while( true )
		{
			// follow the gradient until something happens
			followGradient( f, info, kin );
			
			// did we hit an optimum?
			double gradient = getGradient( f, info.theta );
			if( Math.abs( gradient ) < GradientEpsilon )
			{
				return info.theta;
			}
			
			// did we get a bound?
			if( info.isBoundComplete() )
			{
				return findBoundedOptimum( f, info.bound, kin );
			}
			
			// do we still have a usable gradient?
			if( Math.abs( gradient ) > GradientSteepEnough )
			{
				// looks like we just ran out of gradient iterations
				throw new OptimizerFailureException( "Unable to find next optimum after " + MaxGradientIterations + " iterations" );
			}
			
			// gradient is too small then, change tactics
			exploreFlatZone( f, info, kin );
			
			// did it find an optimum?
			gradient = getGradient( f, info.theta );
			if( Math.abs( gradient ) < GradientEpsilon )
			{
				return info.theta;
			}
			
			// did we get a bound?
			if( info.isBoundComplete() )
			{
				return findBoundedOptimum( f, info.bound, kin );
			}
			
			// we have a usable gradient again. reset the direction and keep looking
			info.direction = gradient > 0.0 ? Direction.Maximize : Direction.Minimize;
		}
	}
	
	private static void followGradient( DifferentiableFunction f, OptimumInfo info, Kinemage kin )
	throws OptimizerFailureException
	{
		// VIS
		List<Double> steps = null;
		if( kin != null )
		{
			steps = new ArrayList<Double>();
		}
		
		try
		{
			// do gradient (a|de)scent
			for( int i=0; i<MaxGradientIterations; i++ )
			{
				// VIS
				if( kin != null )
				{
					steps.add( info.theta );
				}
				
				double gradient = getGradient( f, info.theta );
				
				// in case gradient (a|de)scent oscillates, try to bound the optimum in an interval
				info.updateBound( gradient );
				
				// is the gradient too small to use?
				if( Math.abs( gradient ) < GradientSteepEnough )
				{
					return;
				}
				
				// what's the best damper?
				double bestDamper = GradientDampers[0];
				double bestValue = info.direction.getFactor() * CircleRange.mapMinusPiToPi( info.theta - info.direction.getFactor()*bestDamper*gradient );
				for( int j=1; j<GradientDampers.length; j++ )
				{
					double damper = GradientDampers[j];
					double nextValue = info.direction.getFactor() * CircleRange.mapMinusPiToPi( info.theta - info.direction.getFactor()*damper*gradient );
					
					if( nextValue < bestValue )
					{
						bestValue = nextValue;
						bestDamper = damper;
					}
				}
				
				// get the next theta
				info.updateTheta( -info.direction.getFactor()*bestDamper*gradient );
			}
			
			// oops, we ran out of iterations (oscillation?)
		}
		finally
		{
			// VIS
			if( kin != null )
			{
				List<Vector3> points = new ArrayList<Vector3>();
				for( Double t : steps )
				{
					points.add( new Vector3( t, f.getValue( t ), 0.0 ) );
				}
				KinemageBuilder.appendPoints( kin, points, "Gradient " + info.direction.name() + " (" + steps.size() + ")", KinemageColor.LightGrey, 5 );
			}
		}
	}
	
	private static double findBoundedOptimum( DifferentiableFunction f, CircleRange bound, Kinemage kin )
	throws OptimizerFailureException
	{
		// VIS
		if( kin != null )
		{
			OptimizerKinemageBuilder.appendBound( kin, f, bound, KinemageColor.Orange, 1 );
		}
		
		// is there an optimum in this bound?
		double fLower = getGradient( f, bound.getSource() );
		double fUpper = getGradient( f, bound.getTarget() );
		if( fLower == 0.0 )
		{
			return bound.getSource();
		}
		else if( fUpper == 0.0 )
		{
			return bound.getTarget();
		}
		else if( Math.signum( fLower ) == Math.signum( fUpper ) )
		{
			throw new OptimizerFailureException( "No optimum in this bound." );
		}
		
		// ok, find it using interval bisection
		while( true )
		{
			double mid = bound.getMidpoint();
			double fMid = getGradient( f, mid );
			if( bound.getLength() < OptimumEpsilon )
			{
				// we win!! =D
				return mid;
			}
			if( Math.signum( fMid ) == Math.signum( fLower ) )
			{
				// update the lower bound
				bound = CircleRange.newByCounterclockwiseSegment( mid, bound.getTarget() );
				fLower = fMid;
			}
			else
			{
				// update the upper bound
				bound = CircleRange.newByCounterclockwiseSegment( bound.getSource(), mid );
				fUpper = fMid;
			}
			
			// loop invariant: optimum is always in the interval
			assert( Math.signum( fLower ) != Math.signum( fUpper ) );
		}
	}
	
	private static void exploreFlatZone( DifferentiableFunction f, OptimumInfo info, Kinemage kin )
	throws OptimizerFailureException
	{
		// PRECONDITION: the gradient is too small to use gradient (a|de)scent
		// so, since we're probably near an optimum, just move in the positive theta direction until we bound it
		// but, if we're not near an optimum, keep going till the gradient gets big enough to use again
		
		// NOTE: this function is pretty hack-ey and can sometimes skip over optima
		// but if they were too close together anyway, we probably didn't need that much precision
		
		// VIS
		List<Double> steps = null;
		if( kin != null )
		{
			steps = new ArrayList<Double>();
		}
		
		try
		{
			double delta = GradientInitialDelta;
			while( true )
			{
				// VIS
				if( kin != null )
				{
					steps.add( info.theta );
				}
				
				double gradient = getGradient( f, info.theta );
				
				info.updateBound( gradient );
				
				// did we pin down a bound?
				if( info.isBoundComplete() )
				{
					return;
				}
				
				// are we out of the flat zone?
				if( Math.abs( gradient ) > GradientSteepEnough )
				{
					return;
				}
				
				info.updateTheta( delta );
				delta *= DeltaGrowthRate;
			}
		}
		finally
		{
			// VIS
			if( kin != null )
			{
				List<Vector3> points = new ArrayList<Vector3>();
				for( Double t : steps )
				{
					points.add( new Vector3( t, f.getValue( t ), 0.0 ) );
				}
				KinemageBuilder.appendPoints( kin, points, "Exploring (" + steps.size() + ")", KinemageColor.LightGrey, 5 );
			}
		}
	}

	private static void findSteeperGradient( DifferentiableFunction f, OptimumInfo info, Kinemage kin )
	throws OptimizerFailureException
	{
		// VIS
		List<Double> steps = null;
		if( kin != null )
		{
			steps = new ArrayList<Double>();
		}
		
		double delta = GradientInitialDelta;
		while( true )
		{
			// VIS
			if( kin != null )
			{
				steps.add( info.theta );
			}
			
			double gradient = getGradient( f, info.theta );
			if( Math.abs( gradient ) > GradientSteepEnough )
			{
				// update the direction
				info.direction = gradient > 0.0 ? Direction.Maximize : Direction.Minimize;
				
				break;
			}
			info.updateTheta( delta );
			delta *= DeltaGrowthRate;
		}
		
		// VIS
		if( kin != null )
		{
			List<Vector3> points = new ArrayList<Vector3>();
			for( Double t : steps )
			{
				points.add( new Vector3( t, f.getValue( t ), 0.0 ) );
			}
			KinemageBuilder.appendPoints( kin, points, "Steeper (" + steps.size() + ") now, " + info.direction, KinemageColor.LightGrey, 5 );
		}
	}
	
	private static double findRoot( DifferentiableFunction f, double lower, double upper )
	{
		// PRECONDITION: interval is well-formed (easy to get wrong since these are on the circle)
		assert( upper > lower );
		
		// PRECONDITION: a root is in this interval
		double fLower = f.getValue( lower );
		double fUpper = f.getValue( upper );
		assert( Math.signum( fLower ) != Math.signum( fUpper ) );
		
		// so find it using bisection
		while( upper - lower > RootEpsilon )
		{
			double mid = ( lower + upper )/2.0;
			double fMid = f.getValue( mid );
			if( fMid == 0.0 )
			{
				// we win!! =D
				return mid;
			}
			if( Math.signum( fMid ) == Math.signum( fLower ) )
			{
				lower = mid;
				fLower = fMid;
			}
			else
			{
				upper = mid;
				fUpper = fMid;
			}
			
			// loop invariant: root is always in the interval
			assert( Math.signum( fLower ) != Math.signum( fUpper ) );
		}
		return CircleRange.mapMinusPiToPi( ( lower + upper )/2.0 );
	}
}
