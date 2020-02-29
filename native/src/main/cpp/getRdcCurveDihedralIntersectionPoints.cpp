
#include <jni.h>

#include <CGAL/basic.h>

#include "native.h"
#include "global.h"
#include "AlgebraicCurveIntersector.h"


JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_getRdcCurveDihedralIntersectionPoints( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dA, jdouble dB, jdouble dC,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di,
	jdouble dr, jdouble theta
)
{
	START_SIGNAL_HANDLING
	{
		// find our java classes and methods
		jclass classList = jvm->FindClass( "java/util/List" );
		checkException( jvm );
		jmethodID methodListAdd = jvm->GetMethodID( classList, "add", "(Ljava/lang/Object;)Z" );
		checkException( jvm );

		// set up exact number type aliasing
		Rational A = dA;
		Rational B = dB;
		Rational C = dC;

		Rational a = da;
		Rational b = db;
		Rational c = dc;
		Rational d = dd;
		Rational e = de;
		Rational f = df;
		Rational g = dg;
		Rational h = dh;
		Rational i = di;

		Rational r = dr;

		AlgebraicCurveIntersector intersector;
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );

		// compute the curve coefficients
		Rational D = A*a*c + B*d*f + C*g*i;
		Rational E = A*b*c + B*e*f + C*h*i;
		Rational F = A*c*c + B*f*f + C*i*i - A*a*a - B*d*d - C*g*g;
		Rational G = A*c*c + B*f*f + C*i*i - A*b*b - B*e*e - C*h*h;
		Rational H = -2*( A*a*b + B*d*e + C*g*h );
		Rational I = r - A*c*c - B*f*f - C*i*i;

		Poly_rat_2 x2 = x*x;
		Poly_rat_2 x3 = x2*x;
		Poly_rat_2 x4 = x2*x2;

		Poly_rat_2 y2 = y*y;
		Poly_rat_2 y3 = y2*y;
		Poly_rat_2 y4 = y2*y2;

		Poly_rat_2 rdcPoly =
			  ( F*F + 4*D*D ) * x4
			+ ( G*G + 4*E*E ) * y4
			+ ( 2*F*H + 8*D*E ) * x3*y
			+ ( 2*G*H + 8*D*E ) * x*y3
			+ ( 2*F*I - 4*D*D ) * x2
			+ ( 2*G*I - 4*E*E ) * y2
			+ ( 2*F*G + H*H + 4*E*E + 4*D*D ) * x2*y2
			+ ( 2*H*I - 8*D*E ) * x*y
			+ I*I;
		Curve_2 rdcCurve = intersector.constructCurve( rdcPoly );
		intersector.add( rdcCurve );

		// construct the query line segment
		double length = 1.1;
		Point_2 lineSegmentTarget = Point_2(
			Rational( length * cos( theta ) ),
			Rational( length * sin( theta ) )
		);
		X_monotone_curve_2 lineSegment = intersector.constructXCurve( Point_2( 0, 0 ), lineSegmentTarget );
		intersector.add( lineSegment );

		Traits traits;
		Traits::Is_on_2 is_on = traits.is_on_2_object();

		// compute the intersection points
		Arrangement_2 arrangement = intersector.getArrangement();
		for( Arrangement_2::Vertex_iterator iter = arrangement.vertices_begin(); iter != arrangement.vertices_end(); iter++ )
		{
			Arrangement_2::Vertex vertex = *iter;

			// the degree must be at least 3 to represent an intersection
			if( vertex.degree() < 3 )
			{
				continue;
			}

			// make sure the point lies on the query line
			if( !is_on( vertex.point(), lineSegment ) )
			{
				continue;
			}

			// NOTE: this step is approximate and pretty slow
			Point_2 point = vertex.point();
			double pointx = CGAL::to_double( point.x() );
			double pointy = CGAL::to_double( point.y() );

			// use both roots for z
			double z = sqrt( 1 - pointx*pointx - pointy*pointy );
			jvm->CallBooleanMethod( out, methodListAdd, newVector3( jvm, pointx, pointy, z ) );
			jvm->CallBooleanMethod( out, methodListAdd, newVector3( jvm, pointx, pointy, -z ) );
		}
	}
	STOP_SIGNAL_HANDLING
}
