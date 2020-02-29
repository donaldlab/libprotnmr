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
