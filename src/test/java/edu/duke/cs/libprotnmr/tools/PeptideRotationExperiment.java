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

package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.protein.Peptide;

import java.util.ArrayList;
import java.util.List;

public class PeptideRotationExperiment
{
	public static void main( String[] args )
	{
		// pick a phi/psi point
		double phi = Math.toRadians( 180.0 );
		double psi = Math.toRadians( 0.0 );

		// allocate our memory here
		List<Vector3> points = new ArrayList<Vector3>();
		Matrix3 basis = new Matrix3();
		Quaternion q = new Quaternion();
		
		// TEMP: vector bundles
		List<Vector3> xAxes = new ArrayList<Vector3>();
		List<Vector3> yAxes = new ArrayList<Vector3>();
		List<Vector3> zAxes = new ArrayList<Vector3>();
		
		// sample phi/psi space around the current NH vector orientation
		Peptide peptide = new Peptide();
		final double radians = Math.toRadians( 180.0 );
		final double SampleResolution = Math.toRadians( 2.0 );
		int numSamples = (int)( radians / SampleResolution * 2.0 );
		for( int i=0; i<numSamples; i++ )
		{
			peptide.phi = phi + radians * ( (double)( 2 * i ) / (double)( numSamples - 1 ) - 1.0 );
			for( int j=0; j<numSamples; j++ )
			{
				peptide.psi = psi + radians * ( (double)( 2 * j ) / (double)( numSamples - 1 ) - 1.0 );
				
				// get the rotation point
				peptide.update();
				peptide.getBasis( basis );
				Quaternion.getRotation( q, basis );
				points.add( new Vector3( q.a, q.c, q.d ) );
				
				// get the basis vectors
				Vector3 x = new Vector3();
				basis.getXAxis( x );
				xAxes.add( x );
				
				Vector3 y = new Vector3();
				basis.getYAxis( y );
				yAxes.add( y );
				
				Vector3 z = new Vector3();
				basis.getZAxis( z );
				zAxes.add( z );
			}
		}
		
		// render a kinemage
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 2, 0.1 );
		KinemageBuilder.appendUnitSphere( kin );
		KinemageBuilder.appendPoints( kin, points, "points", KinemageColor.Yellow, 2 );
		//KinemageBuilder.appendPoints( kin, xAxes, "X axes", KinemageColor.Red, 2 );
		//KinemageBuilder.appendPoints( kin, yAxes, "Y axes", KinemageColor.Green, 2 );
		//KinemageBuilder.appendPoints( kin, zAxes, "Z axes", KinemageColor.Blue, 2 );
		new KinemageWriter().showAndWait( kin );
	}
}
