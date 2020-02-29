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

package edu.duke.cs.libprotnmr.geom;

public class LineSegment2
{
	/**************************
	 *   Fields
	 **************************/
	
	public Vector2 start;
	public Vector2 stop;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public LineSegment2( Vector2 start, Vector2 stop )
	{
		set( start, stop );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( Vector2 start, Vector2 stop )
	{
		this.start = start;
		this.stop = stop;
	}
}
