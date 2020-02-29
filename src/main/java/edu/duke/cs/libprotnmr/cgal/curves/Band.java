package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;

public interface Band
{
	boolean containsPoint(Vector3 point);
	boolean boundaryContainsPoint(Vector3 point);
	boolean hasCurveOnBoundary(Curve curve);
}
