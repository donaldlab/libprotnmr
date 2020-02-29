
#include <jni.h>

#include <CGAL/basic.h>

#include "native.h"
#include "global.h"
#include "AlgebraicCurveIntersector.h"

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_getParabolaUnitCircleIntersectionPoints( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble da, jdouble db, jdouble dc
)
{
	START_SIGNAL_HANDLING
	{
		// set up exact number type aliasing
		Rational a = da;
		Rational b = db;
		Rational c = dc;
		
		Rational A = 1/a;
		Rational B = -2*b/a;
		Rational C = b*b/a + c;
		
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );
		
		AlgebraicCurveIntersector intersector;
		intersector.add( intersector.constructCurve( x*x + y*y - 1 ) );
		intersector.add( intersector.constructCurve( A*x*x + B*x - 1*y + C ) );
		
		std::vector<Point_rat_2> points;
		intersector.getIntersectionPoints( &points );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			addVector3ToList( jvm, out,
				CGAL::to_double( point.x() ),
				CGAL::to_double( point.y() ),
				0
			);
		}
	}
	STOP_SIGNAL_HANDLING
}
