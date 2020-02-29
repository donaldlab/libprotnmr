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

package edu.duke.cs.libprotnmr.chart;

import java.awt.Shape;
import java.awt.geom.Path2D;

public enum Shapes
{
	/**************************
	 *   Values
	 **************************/
	
	Cross
	{
		@Override
		public Shape getShape( )
		{
			double delta = ShapeSize / 2.0;
			
			Path2D path = new Path2D.Double( Path2D.WIND_NON_ZERO, 2 );
			path.moveTo( -delta, -delta );
			path.lineTo( delta, delta );
			path.moveTo( -delta, delta );
			path.lineTo( delta, -delta );
			return path;
		}
	},
	Plus
	{
		@Override
		public Shape getShape( )
		{
			double delta = ShapeSize / 2.0;
			
			Path2D path = new Path2D.Double( Path2D.WIND_NON_ZERO, 2 );
			path.moveTo( -delta, 0.0 );
			path.lineTo( delta, 0.0 );
			path.moveTo( 0.0, -delta );
			path.lineTo( 0.0, delta );
			return path;
		}
	},
	Star
	{
		@Override
		public Shape getShape( )
		{
			double delta = ShapeSize / 2.0;
			
			double half = 0.5 * delta;
			double sqrt3over2 = Math.sqrt( 3.0 ) / 2.0 * delta;
			
			Path2D path = new Path2D.Double( Path2D.WIND_NON_ZERO, 2 );
			path.moveTo( -delta, 0.0 );
			path.lineTo( delta, 0.0 );
			path.moveTo( -half, -sqrt3over2 );
			path.lineTo( half, sqrt3over2 );
			path.moveTo( -half, sqrt3over2 );
			path.lineTo( half, -sqrt3over2 );
			return path;
		}
	};
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	private static final double ShapeSize = 8.0;
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public abstract Shape getShape( );
}
