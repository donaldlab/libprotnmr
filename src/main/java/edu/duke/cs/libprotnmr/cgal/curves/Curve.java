package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;

import java.util.List;

public interface Curve
{
	static final double DefaultEpsilon = 1e-6;
	
	List<Vector3> samplePoints();
	List<Vector3> samplePoints(double stepRadians);
	List<Vector3> samplePoints(int numSamples);
	boolean containsPoint(Vector3 p);
	boolean containsPoint(Vector3 p, double epsilon);
	boolean hasLength();
	List<? extends CurveArc> split(Vector3 point);
	List<? extends CurveArc> split(Iterable<Vector3> points);
	CurveArc newClosedArc();
	CurveArc newClosedArc(Vector3 p);
}
