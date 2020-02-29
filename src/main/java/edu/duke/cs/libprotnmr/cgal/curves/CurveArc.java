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
