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

package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;

public interface CurveArc extends Curve
{
	Curve getCurve();
	boolean isClosed();
	Vector3 getSource();
	Vector3 getTarget();
	Vector3 getMidpoint();
	Vector3 getOtherEndpoint(Vector3 p);
	Vector3 getOtherEndpoint(Vector3 p, double epsilon);
	boolean containsPointOnBoundary(Vector3 p);
	boolean containsPointOnBoundary(Vector3 p, double epsilon);
	boolean containsPointInInterior(Vector3 p);
	boolean containsPointInInterior(Vector3 p, double epsilon);
	double getApproximateLength(int numSamples);
}
