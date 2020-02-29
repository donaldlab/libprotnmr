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

package edu.duke.cs.libprotnmr.math;

import java.util.ArrayList;
import java.util.List;

public class Quadratic
{
	public static List<Double> solve( double a, double b, double c )
	{
		List<Double> roots = new ArrayList<Double>();
		double d = b*b - 4.0*a*c;
		if( d >= 0.0 )
		{
			double e = Math.sqrt( d );
			roots.add( ( -b + e )/2.0/a );
			roots.add( ( -b - e )/2.0/a );
		}
		return roots;
	}
}
