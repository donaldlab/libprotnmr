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

import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;


public class OptimizerKinemageBuilder
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final int DefaultNumSamples = 512;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Kinemage getKinemage( DifferentiableFunction f )
	{
		return getKinemage( f, DefaultNumSamples );
	}
	
	public static Kinemage getKinemage( DifferentiableFunction f, int numSamples )
	{
		// sample the function and its derivative
		List<Vector3> points = new ArrayList<Vector3>();
		List<Vector3> slopes = new ArrayList<Vector3>();
		List<Vector3> slopesEstimate = new ArrayList<Vector3>();
		for( int i=0; i<numSamples; i++ )
		{
			double t = (double)i / (double)( numSamples - 1 ) * 2.0 * Math.PI - Math.PI;
			double value = f.getValue( t );
			double slope = f.getDerivative( t );
			double deltaT = 0.001;
			double slopeEstimate = ( f.getValue( t + deltaT ) - value ) / deltaT;
			points.add( new Vector3( t, value, 0.0 ) );
			slopes.add( new Vector3( t, slope, 0.0 ) );
			slopesEstimate.add( new Vector3( t, slopeEstimate, 0.0 ) );
		}
		
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin );
		KinemageBuilder.appendPoints( kin, points, "Points", KinemageColor.Lime, 3 );
		KinemageBuilder.appendPoints( kin, slopes, "Slopes", KinemageColor.Cobalt, 3 );
		KinemageBuilder.appendPoints( kin, slopesEstimate, "Slopes Estimate", KinemageColor.Cobalt, 2 );
		Vector3 posx = Vector3.getUnitX();
		KinemageBuilder.appendVector( kin, posx, "+X axis", KinemageColor.Red, 1, Math.PI );
		Vector3 negx = Vector3.getUnitX();
		negx.negate();
		KinemageBuilder.appendVector( kin, negx, "-X axis", KinemageColor.Red, 1, Math.PI );
		return kin;
	}
	
	public static void appendValues( Kinemage kin, DifferentiableFunction f, Iterable<Double> thetas, String name, KinemageColor color )
	{
		List<Vector3> points = new ArrayList<Vector3>();
		for( Double theta : thetas )
		{
			points.add( new Vector3( theta, f.getValue( theta ), 0.0 ) );
		}
		KinemageBuilder.appendPoints( kin, points, name, color, 7 );
	}
	
	public static void appendDerivatives( Kinemage kin, DifferentiableFunction f, Iterable<Double> thetas, String name, KinemageColor color )
	{
		List<Vector3> points = new ArrayList<Vector3>();
		for( Double theta : thetas )
		{
			points.add( new Vector3( theta, f.getDerivative( theta ), 0.0 ) );
		}
		KinemageBuilder.appendPoints( kin, points, name, color, 7 );
	}

	public static void appendOptima( Kinemage kin, DifferentiableFunction f, List<Double> optima )
	{
		OptimizerKinemageBuilder.appendValues( kin, f, optima, "Optima (" + optima.size() + ")", KinemageColor.Orange );
	}

	public static void appendRoots( Kinemage kin, DifferentiableFunction f, List<Double> roots )
	{
		OptimizerKinemageBuilder.appendValues( kin, f, roots, "Roots (" + roots.size() + ")", KinemageColor.Yellow );
	}

	public static void appendBound( Kinemage kin, DifferentiableFunction f, CircleRange bound, KinemageColor color, int width )
	{
		double source = bound.getSource();
		double target = bound.getTarget();
		KinemageBuilder.appendLine( kin,
			new Vector3( source, 0.0, 0.0 ),
			new Vector3( source, Math.signum( f.getValue( source ) ), 0.0 ),
			String.format( "Lower Bound %.5f %.5f", source, f.getDerivative( source ) ),
			color, 1
		);
		KinemageBuilder.appendLine( kin,
			new Vector3( target, 0.0, 0.0 ),
			new Vector3( target, Math.signum( f.getValue( target ) ), 0.0 ),
			String.format( "Upper Bound %.5f %.5f", target, f.getDerivative( target ) ),
			color, 1
		);
	}
}
