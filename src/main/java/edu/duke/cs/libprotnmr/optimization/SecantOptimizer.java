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

package edu.duke.cs.libprotnmr.optimization;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;


public class SecantOptimizer
{
	public static double getLocalOptimum( DifferentiableFunction f, double guess1, double guess2, double epsilon, int maxNumIterations )
	throws OptimizerFailureException
	{
		double x1 = guess1;
		double x2 = guess2;
		double y1 = f.getDerivative( x1 );
		double y2 = f.getDerivative( x2 );
		
		// DEBUG
		Kinemage kin = null;
		if( true )
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
		
		// are any of these guesses the answer already?
		if( CompareReal.eq( y1, 0.0, epsilon ) )
		{
			return x1;
		}
		if( CompareReal.eq( y2, 0.0, epsilon ) )
		{
			return x2;
		}
		
		if( kin != null )
		{
			KinemageBuilder.appendPoint( kin, new Vector3( x1, y1, 0.0 ), "Guess 1", KinemageColor.Orange, 7 );
			KinemageBuilder.appendPoint( kin, new Vector3( x2, y2, 0.0 ), "Guess 2", KinemageColor.Orange, 7 );
		}
		
		for( int i=0; i<maxNumIterations; i++ )
		{
			// no? then find where the secant line intercepts the x axis
			double dx = x2 - x1;
			double dy = y2 - y1;
			double x3 = -dx/dy*y1 + x1;
			
			if( kin != null )
			{
				KinemageBuilder.appendLine( kin, new Vector3( x2, y2, 0.0 ), new Vector3( x3, 0.0, 0.0 ), "Secant", KinemageColor.Yellow, 1 );
			}
			
			// is x the answer?
			double y3 = f.getDerivative( x3 );
			if( CompareReal.eq( y3, 0.0, epsilon ) )
			{
				if( kin != null )
				{
					KinemageBuilder.appendPoint( kin, new Vector3( x3, f.getValue( x3 ), 0.0 ), "Optimum", KinemageColor.Green, 7 );
					new KinemageWriter().showAndWait( kin );
				}
				
				return x3;
			}
			
			if( kin != null )
			{
				KinemageBuilder.appendPoint( kin, new Vector3( x3, y3, 0.0 ), "Point", KinemageColor.Orange, 5 );
			}
			
			// no? iterate!
			x1 = x2;
			x2 = x3;
			y1 = y2;
			y2 = y3;
		}
		
		if( kin != null )
		{
			new KinemageWriter().showAndWait( kin );
		}
		
		throw new OptimizerFailureException( "Unable to find minimum after " + maxNumIterations + " iterations!" );
	}
}
