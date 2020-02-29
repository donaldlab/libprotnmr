

#include <jni.h>

#include "native.h"
#include "global.h"
#include "AlgebraicCurveIntersector.h"



JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_sampleRotatedRdcCurve( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dA, jdouble dB, jdouble dC,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di,
	jdouble dr
)
{
	START_SIGNAL_HANDLING
	{
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

		Poly_rat_2 rdcCurve =
			  ( F*F + 4*D*D ) * x4
			+ ( G*G + 4*E*E ) * y4
			+ ( 2*F*H + 8*D*E ) * x3*y
			+ ( 2*G*H + 8*D*E ) * x*y3
			+ ( 2*F*I - 4*D*D ) * x2
			+ ( 2*G*I - 4*E*E ) * y2
			+ ( 2*F*G + H*H + 4*E*E + 4*D*D ) * x2*y2
			+ ( 2*H*I - 8*D*E ) * x*y
			+ I*I;
		intersector.add( intersector.constructCurve( rdcCurve ) );

		// cover the unit disc with horizontal lines
		for( Rational n=-1.0; n<=1.0; n+=0.02 )
		{
			intersector.add( intersector.constructCurve( y - n ) );
		}

		// compute the intersection points
		std::vector<Point_rat_2> points;
		intersector.getIntersectionPoints( &points );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			addVector2ToList( jvm, out,
				CGAL::to_double( point.x() ),
				CGAL::to_double( point.y() )
			);
		}
	}
	STOP_SIGNAL_HANDLING
}
