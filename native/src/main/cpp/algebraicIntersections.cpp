
#include <jni.h>

#include <CGAL/Cartesian.h>

#include "native.h"
#include "global.h"
#include "AlgebraicCurveIntersector.h"

typedef CGAL::Cartesian<Rational>::Point_3 Point_rat_3;

class Rotation
{
public:
	Rational a;
	Rational b;
	Rational c;
	Rational d;
	Rational e;
	Rational f;
	Rational g;
	Rational h;
	Rational i;

	Rational aa;
	Rational bb;
	Rational cc;
	Rational dd;
	Rational ee;
	Rational ff;
	Rational gg;
	Rational hh;
	Rational ii;

	Rational ab;
	Rational de;
	Rational ac;
	Rational df;
	Rational bc;
	Rational ef;

	Rotation( Rational _a, Rational _b, Rational _c, Rational _d, Rational _e, Rational _f, Rational _g, Rational _h, Rational _i )
	{
		a = _a;
		b = _b;
		c = _c;
		d = _d;
		e = _e;
		f = _f;
		g = _g;
		h = _h;
		i = _i;

		// precompute some shortcuts
		aa = a * a;
		bb = b * b;
		cc = c * c;
		dd = d * d;
		ee = e * e;
		ff = f * f;
		gg = g * g;
		hh = h * h;
		ii = i * i;

		ab = a * b;
		de = d * e;
		ac = a * c;
		df = d * f;
		bc = b * c;
		ef = e * f;
	}

	Rotation( const Rotation &other )
	{
		a = other.a;
		b = other.b;
		c = other.c;
		d = other.d;
		e = other.e;
		f = other.f;
		g = other.g;
		h = other.h;
		i = other.i;

		aa = other.aa;
		bb = other.bb;
		cc = other.cc;
		dd = other.dd;
		ee = other.ee;
		ff = other.ff;
		gg = other.gg;
		hh = other.hh;
		ii = other.ii;

		ab = other.ab;
		de = other.de;
		ac = other.ac;
		df = other.df;
		bc = other.bc;
		ef = other.ef;
	}
};

class Curve
{
public:
	Poly_rat_2 poly;
};

class RdcCurve : public Curve
{
public:
	Rational A;
	Rational B;
	Rational C;
	Rational d;
	
	RdcCurve( Rational _A, Rational _B, Rational _C, Rational _d )
	{
		A = _A;
		B = _B;
		C = _C;
		d = _d;
	}
};

class RotatedRdcCurve : public Curve
{
public:
	Rational A;
	Rational B;
	Rational C;
	Rational d;
	Rotation rot;

	RotatedRdcCurve( Rational _A, Rational _B, Rational _C, Rational _d, const Rotation &_rot )
	: rot( _rot )
	{
		A = _A;
		B = _B;
		C = _C;
		d = _d;
	}
};

class RotatedCircularCurve : public Curve
{
public:
	Rational alpha;
	Rational beta;
	Rotation rot;
	
	RotatedCircularCurve( Rational _alpha, Rational _beta, const Rotation &_rot )
	: rot( _rot )
	{
		alpha = _alpha;
		beta = _beta;
	}

};

class Projection
{
public:
	virtual ~Projection() { };
	virtual RdcCurve getRdcCurve( Rational A, Rational B, Rational C, Rational d ) = 0;
	virtual RotatedRdcCurve getRotatedRdcCurve( Rational A, Rational B, Rational C, Rational d, const Rotation &rot ) = 0;
	virtual RotatedCircularCurve getRotatedCircularCurve( Rational alpha, Rational beta, const Rotation &rot ) = 0;
	virtual RotatedCircularCurve getRotatedGeodesicCurve( const Rotation &rot )
	{
		return getRotatedCircularCurve( Rational( 0 ), Rational( 1 ), rot );
	}
	virtual Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdcCurve, const RotatedCircularCurve &circularCurve ) = 0;
	virtual Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdcCurve, const RotatedRdcCurve &rotatedRdcCurve ) = 0;
};

class XProjection : public Projection
{
public:

	XProjection( )
	{
		y = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		z = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );
	}

	RdcCurve getRdcCurve( Rational A, Rational B, Rational C, Rational d )
	{
		RdcCurve rdcCurve( A, B, C, d );
		rdcCurve.poly = ( B - A )*y*y + ( C - A )*z*z + ( A - d );
		return rdcCurve;
	}

	RotatedCircularCurve getRotatedCircularCurve( Rational alpha, Rational beta, const Rotation &rot )
	{
		RotatedCircularCurve circularCurve( alpha, beta, rot );

		// shortucts
		Rational abpdeog = ( rot.ab + rot.de ) / rot.g;
		Rational acpdfog = ( rot.ac + rot.df ) / rot.g;
		Rational aapddogg = ( rot.aa + rot.dd ) / rot.gg;

		// compute the curve coefficients
		Rational r = rot.bb + rot.ee + rot.hh*aapddogg - 2*rot.h*abpdeog;
		Rational s = rot.cc + rot.ff + rot.ii*aapddogg - 2*rot.i*acpdfog;
		Rational t = 2*( rot.bc + rot.ef + rot.h*rot.i*aapddogg - rot.i*abpdeog - rot.h*acpdfog );
		Rational u = 2*alpha*( abpdeog - rot.h*aapddogg );
		Rational v = 2*alpha*( acpdfog - rot.i*aapddogg );
		Rational w = alpha*alpha*aapddogg - beta;
		circularCurve.poly = r*y*y + s*z*z + t*y*z + u*y + v*z + w;

		return circularCurve;
	}
	
	RotatedRdcCurve getRotatedRdcCurve( Rational A, Rational B, Rational C, Rational d, const Rotation &rot )
	{
		RotatedRdcCurve rotatedRdcCurve( A, B, C, d, rot );
		
		// UNDONE: implement me!!
		rotatedRdcCurve.poly = y + z;
		
		return rotatedRdcCurve;
	}

	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdc, const RotatedCircularCurve &circ )
	{
		Rational py = point.x();
		Rational pz = point.y();
		Rational px = ( circ.alpha - circ.rot.h*py - circ.rot.i*pz )/circ.rot.g;
		return Point_rat_3( px, py, pz );
	}
	
	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdcCurve, const RotatedRdcCurve &rotatedRdcCurve )
	{
		Rational py = point.x();
		Rational pz = point.y();
		Rational px = Rational( CGAL::sqrt( CGAL::to_double( 1 - py*py - pz*pz ) ) );
		return Point_rat_3( px, py, pz );
	}

private:

	Poly_rat_2 y;
	Poly_rat_2 z;
};

class YProjection : public Projection
{
public:

	YProjection( )
	{
		x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		z = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );
	}

	RdcCurve getRdcCurve( Rational A, Rational B, Rational C, Rational d )
	{
		RdcCurve rdcCurve( A, B, C, d );
		rdcCurve.poly = ( A - B )*x*x + ( C - B )*z*z + ( B - d );
		return rdcCurve;
	}

	RotatedCircularCurve getRotatedCircularCurve( Rational alpha, Rational beta, const Rotation &rot )
	{
		RotatedCircularCurve circularCurve( alpha, beta, rot );

		// shortucts
		Rational abpdeoh = ( rot.ab + rot.de ) / rot.h;
		Rational bcpefoh = ( rot.bc + rot.ef ) / rot.h;
		Rational bbpeeohh = ( rot.bb + rot.ee ) / rot.hh;

		// compute the curve coefficients
		Rational r = rot.aa + rot.dd + rot.gg*bbpeeohh - 2*rot.g*abpdeoh;
		Rational s = rot.cc + rot.ff + rot.ii*bbpeeohh - 2*rot.i*bcpefoh;
		Rational t = 2*( rot.ac + rot.df + rot.g*rot.i*bbpeeohh - rot.i*abpdeoh - rot.g*bcpefoh );
		Rational u = 2*alpha*( abpdeoh - rot.g*bbpeeohh );
		Rational v = 2*alpha*( bcpefoh - rot.i*bbpeeohh );
		Rational w = alpha*alpha*bbpeeohh - beta;
		circularCurve.poly = r*x*x + s*z*z + t*x*z + u*x + v*z + w;

		return circularCurve;
	}
	
	RotatedRdcCurve getRotatedRdcCurve( Rational A, Rational B, Rational C, Rational d, const Rotation &rot )
	{
		RotatedRdcCurve rotatedRdcCurve( A, B, C, d, rot );
		
		// UNDONE: implement me!!
		rotatedRdcCurve.poly = x + z;
		
		return rotatedRdcCurve;
	}

	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdc, const RotatedCircularCurve &circ )
	{
		Rational px = point.x();
		Rational pz = point.y();
		Rational py = ( circ.alpha - circ.rot.g*px - circ.rot.i*pz )/circ.rot.h;
		return Point_rat_3( px, py, pz );
	}
	
	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdcCurve, const RotatedRdcCurve &rotatedRdcCurve )
	{
		Rational px = point.x();
		Rational pz = point.y();
		Rational py = Rational( CGAL::sqrt( CGAL::to_double( 1 - px*px - pz*pz ) ) );
		return Point_rat_3( px, py, pz );
	}
	
private:

	Poly_rat_2 x;
	Poly_rat_2 z;
};

class ZProjection : public Projection
{
public:

	ZProjection( )
	{
		x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );
	}

	RdcCurve getRdcCurve( Rational A, Rational B, Rational C, Rational d )
	{
		RdcCurve rdcCurve( A, B, C, d );
		rdcCurve.poly = ( A - C )*x*x + ( B - C )*y*y + ( C - d );
		return rdcCurve;
	}

	RotatedCircularCurve getRotatedCircularCurve( Rational alpha, Rational beta, const Rotation &rot )
	{
		RotatedCircularCurve circularCurve( alpha, beta, rot );

		// shortucts
		Rational acpdfoi = ( rot.ac + rot.df ) / rot.i;
		Rational bcpefoi = ( rot.bc + rot.ef ) / rot.i;
		Rational ccpffoii = ( rot.cc + rot.ff ) / rot.ii;

		// compute the curve coefficients
		Rational r = rot.aa + rot.dd + rot.gg*ccpffoii - 2*rot.g*acpdfoi;
		Rational s = rot.bb + rot.ee + rot.hh*ccpffoii - 2*rot.h*bcpefoi;
		Rational t = 2*( rot.ab + rot.de + rot.g*rot.h*ccpffoii - rot.h*acpdfoi - rot.g*bcpefoi );
		Rational u = 2*alpha*( acpdfoi - rot.g*ccpffoii );
		Rational v = 2*alpha*( bcpefoi - rot.h*ccpffoii );
		Rational w = alpha*alpha*ccpffoii - beta;
		circularCurve.poly = r*x*x + s*y*y + t*x*y + u*x + v*y + w;

		return circularCurve;
	}
	
	RotatedRdcCurve getRotatedRdcCurve( Rational A, Rational B, Rational C, Rational d, const Rotation &rot )
	{
		RotatedRdcCurve rotatedRdcCurve( A, B, C, d, rot );
		
		Rational D = A*rot.a*rot.c + B*rot.d*rot.f + C*rot.g*rot.i;
		Rational E = A*rot.b*rot.c + B*rot.e*rot.f + C*rot.h*rot.i;
		Rational F = A*rot.c*rot.c + B*rot.f*rot.f + C*rot.i*rot.i - A*rot.a*rot.a - B*rot.d*rot.d - C*rot.g*rot.g;
		Rational G = A*rot.c*rot.c + B*rot.f*rot.f + C*rot.i*rot.i - A*rot.b*rot.b - B*rot.e*rot.e - C*rot.h*rot.h;
		Rational H = -2*( A*rot.a*rot.b + B*rot.d*rot.e + C*rot.g*rot.h );
		Rational I = d - A*rot.c*rot.c - B*rot.f*rot.f - C*rot.i*rot.i;
		
		Poly_rat_2 x2 = x*x;
		Poly_rat_2 x3 = x2*x;
		Poly_rat_2 x4 = x2*x2;

		Poly_rat_2 y2 = y*y;
		Poly_rat_2 y3 = y2*y;
		Poly_rat_2 y4 = y2*y2;

		rotatedRdcCurve.poly =
			  ( F*F + 4*D*D ) * x4
			+ ( G*G + 4*E*E ) * y4
			+ ( 2*F*H + 8*D*E ) * x3*y
			+ ( 2*G*H + 8*D*E ) * x*y3
			+ ( 2*F*I - 4*D*D ) * x2
			+ ( 2*G*I - 4*E*E ) * y2
			+ ( 2*F*G + H*H + 4*E*E + 4*D*D ) * x2*y2
			+ ( 2*H*I - 8*D*E ) * x*y
			+ I*I;
		
		return rotatedRdcCurve;
	}

	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdc, const RotatedCircularCurve &circ )
	{
		Rational px = point.x();
		Rational py = point.y();
		Rational pz = ( circ.alpha - circ.rot.g*px - circ.rot.h*py )/circ.rot.i;
		return Point_rat_3( px, py, pz );
	}
	
	Point_rat_3 liftPoint( Point_rat_2 point, const RdcCurve &rdcCurve, const RotatedRdcCurve &rotatedRdcCurve )
	{
		Rational px = point.x();
		Rational py = point.y();
		Rational pz = Rational( CGAL::sqrt( CGAL::to_double( 1 - px*px - py*py ) ) );
		return Point_rat_3( px, py, pz );
	}

private:

	Poly_rat_2 x;
	Poly_rat_2 y;
};


Projection *getProjection( const Rotation &rot )
{
	// only compare the magnitudes
	Rational g = CGAL::abs( rot.g );
	Rational h = CGAL::abs( rot.h );
	Rational i = CGAL::abs( rot.i );

	Rational maxVal = CGAL::max( g, CGAL::max( h, i ) );
	if( maxVal == g )
	{
		// TEMP
		//printf( "X projection %.4f > %.4f,%.4f\n", CGAL::to_double( g ), CGAL::to_double( h ), CGAL::to_double( i ) );
		return new XProjection();
	}
	else if( maxVal == h )
	{
		// TEMP
		//printf( "Y projection %.4f > %.4f,%.4f\n", CGAL::to_double( h ), CGAL::to_double( g ), CGAL::to_double( i ) );
		return new YProjection();
	}
	else// if( maxVal == i )
	{
		// TEMP
		//printf( "Z projection %.4f > %.4f,%.4f\n", CGAL::to_double( i ), CGAL::to_double( g ), CGAL::to_double( h ) );
		return new ZProjection();
	}
}

void addPoint3ToList( JNIEnv *jvm, jobject out, Point_rat_3 point )
{
	addVector3ToList( jvm, out,
		CGAL::to_double( point.x() ),
		CGAL::to_double( point.y() ),
		CGAL::to_double( point.z() )
	);
}

void addPoint2ToList( JNIEnv *jvm, jobject out, Point_rat_2 point )
{
	addVector2ToList( jvm, out,
		CGAL::to_double( point.x() ),
		CGAL::to_double( point.y() )
	);
}

void getIntersectionPoints( std::vector<Point_rat_2> *pPoints, const Poly_rat_2 &a, const Poly_rat_2 &b )
{
	AlgebraicCurveIntersector intersector;
	intersector.add( intersector.constructCurve( a ) );
	intersector.add( intersector.constructCurve( b ) );

	/* TEMP: sample the curves
	for( Rational n=-1.0; n<=1.0; n+=0.02 )
	{
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );
		intersector.add( intersector.constructCurve( y - n ) );
	}
	*/

	// get the intersection points and round them to Rational types
	intersector.getIntersectionPoints( pPoints );
}

void getIntersectionPoints( std::vector<Point_rat_2> *pPoints, const Curve &a, const Curve &b )
{
	getIntersectionPoints( pPoints, a.poly, b.poly );
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectRdcCircularPoF( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dA, jdouble dB, jdouble dC,
	jdouble dr,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di,
	jdouble dalpha, jdouble dbeta
)
{
	START_SIGNAL_HANDLING
	{
		// set up exact number type aliasing
		Rational A = dA;
		Rational B = dB;
		Rational C = dC;
		Rational r = dr;

		// if the cone height is negative, invert the cone axis
		double rootFactor = dalpha > 0 ? 1.0 : -1.0;

		Rotation rot(
			rootFactor * da,
			rootFactor * db,
			rootFactor * dc,
			rootFactor * dd,
			rootFactor * de,
			rootFactor * df,
			rootFactor * dg,
			rootFactor * dh,
			rootFactor * di
		);
		Rational alpha = rootFactor * dalpha;
		Rational beta = dbeta;

		// just in case...
		if( alpha < 0 )
		{
			throwException( jvm, "Cone height should always be positive!" );
		}

		// return the intersections
		Projection *pProjection = getProjection( rot );
		RdcCurve rdcCurve = pProjection->getRdcCurve( A, B, C, r );
		RotatedCircularCurve circularCurve = pProjection->getRotatedCircularCurve( alpha, beta, rot );
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, rdcCurve, circularCurve );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			addPoint3ToList( jvm, out, pProjection->liftPoint( *iter, rdcCurve, circularCurve ) );
		}
		delete pProjection;
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectRdcGeodesicPoF( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dA, jdouble dB, jdouble dC,
	jdouble dr,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di
)
{
	START_SIGNAL_HANDLING
	{
		// set up exact number type aliasing
		Rational A = dA;
		Rational B = dB;
		Rational C = dC;
		Rational r = dr;
		Rotation rot( da, db, dc, dd, de, df, dg, dh, di );

		// return the intersections
		Projection *pProjection = getProjection( rot );
		RdcCurve rdcCurve = pProjection->getRdcCurve( A, B, C, r );
		RotatedCircularCurve geodesicCurve = pProjection->getRotatedGeodesicCurve( rot );
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, rdcCurve, geodesicCurve );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			addPoint3ToList( jvm, out, pProjection->liftPoint( *iter, rdcCurve, geodesicCurve ) );
		}
		delete pProjection;
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectRdcRdc( JNIEnv *jvm, jclass clazz,
  jobject out,
  jdouble daA, jdouble daB, jdouble daC, jdouble dad,
  jdouble dbA, jdouble dbB, jdouble dbC, jdouble dbd,
  jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di
)
{
	START_SIGNAL_HANDLING
	{
		// set up exact number type aliasing
		Rational aA = daA;
		Rational aB = daB;
		Rational aC = daC;
		Rational ad = dad;
		Rational bA = dbA;
		Rational bB = dbB;
		Rational bC = dbC;
		Rational bd = dbd;
		
		Rotation rot( da, db, dc, dd, de, df, dg, dh, di );
		
		// return intersections
		//Projection *pProjection = getProjection( rot );
		// TEMP
		Projection *pProjection = new ZProjection();
		//
		RdcCurve rdcCurve = pProjection->getRdcCurve( aA, aB, aC, ad );
		RotatedRdcCurve rotatedRdcCurve = pProjection->getRotatedRdcCurve( bA, bB, bC, bd, rot );
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, rdcCurve, rotatedRdcCurve );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_3 point = pProjection->liftPoint( *iter, rdcCurve, rotatedRdcCurve );
			addPoint3ToList( jvm, out, point );
			addPoint3ToList( jvm, out, Point_rat_3( point.x(), point.y(), -point.z() ) );
		}
		delete pProjection;
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectEllipticalCircular( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dq, jdouble dr, jdouble ds,
	jdouble da2, jdouble db2,
	jdouble du, jdouble dv, jdouble dw,
	jdouble dalpha
)
{
	START_SIGNAL_HANDLING
	{
		// set up exact number type aliasing
		Rational q = dq;
		Rational r = dr;
		Rational s = ds;
		Rational a2 = da2;
		Rational b2 = db2;
		Rational u = du;
		Rational v = dv;
		Rational w = dw;
		Rational alpha = dalpha;

		// we're always using the z-projection here, so don't bother with all the fancy classes
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );

		// compute the elliptical curve projection
		Rational denom = -2*s;
		Rational A = ( 1/a2 + 1 )/denom;
		Rational B = ( 1/b2 + 1 )/denom;
		Rational C = -q/a2/denom;
		Rational D = -r/b2/denom;
		Rational E = ( q*q/a2 + r*r/b2 - 1 - s*s )/denom;

		Rational F = A*A;
		Rational G = 2*A*B;
		Rational H = B*B;
		Rational I = 4*A*C;
		Rational J = 4*A*D;
		Rational K = 4*B*C;
		Rational L = 4*B*D;
		Rational M = 1 + 4*C*C + 2*A*E;
		Rational N = 8*C*D;
		Rational O = 1 + 4*D*D + 2*B*E;
		Rational P = 4*C*E;
		Rational Q = 4*D*E;
		Rational R = E*E - 1;

		Poly_rat_2 ellipticalPoly = F*x*x*x*x + G*x*x*y*y + H*y*y*y*y
			+ I*x*x*x + J*x*x*y + K*x*y*y + L*y*y*y
			+ M*x*x + N*x*y + O*y*y
			+ P*x + Q*y + R;

		// compute the circular projection
		Rational w2 = w*w;
		Rational S = u*u/w2 + 1;
		Rational T = v*v/w2 + 1;
		Rational U = u*v/w2;
		Rational V = -alpha*u/w2;
		Rational W = -alpha*v/w2;
		Rational X = alpha*alpha/w2 - 1;

		Poly_rat_2 circularPoly = S*x*x + T*y*y + 2*U*x*y + 2*V*x + 2*W*y + X;
		
		// return the intersection points
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, ellipticalPoly, circularPoly );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			addPoint3ToList( jvm, out, Point_rat_3(
				point.x(),
				point.y(),
				( alpha - u*point.x() - v*point.y() )/w
			) );
		}
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectEllipticalElliptical( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble dq, jdouble dr, jdouble ds,
	jdouble dsa, jdouble dsb, jdouble doa, jdouble dob,
	jdouble dd, jdouble de, jdouble df, jdouble dg, jdouble dh, jdouble di, jdouble dj, jdouble dk, jdouble dl
)
{
	START_SIGNAL_HANDLING
	{
		// number type aliasing
		Rational q = dq;
		Rational r = dr;
		Rational s = ds;
		Rational sa = dsa;
		Rational sb = dsb;
		Rational oa = doa;
		Rational ob = dob;
		Rational d = dd;
		Rational e = de;
		Rational f = df;
		Rational g = dg;
		Rational h = dh;
		Rational i = di;
		Rational j = dj;
		Rational k = dk;
		Rational l = dl;

		// we're always using the z-projection here, so don't bother with all the fancy classes
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );

		Rational sa2 = sa*sa;
		Rational sb2 = sb*sb;
		Rational oa2 = oa*oa;
		Rational ob2 = ob*ob;

		// compute the elliptical paraboloid representation of the surface curve
		Rational denom = -2*s;
		Rational A = ( 1/sa2 + 1 )/denom;
		Rational B = ( 1/sb2 + 1 )/denom;
		Rational C = -q/sa2/denom;
		Rational D = -r/sb2/denom;
		Rational E = ( q*q/sa2 + r*r/sb2 - 1 - s*s )/denom;

		// compute the surface elliptical curve projection
		Poly_rat_2 surfacePoly;
		{
			Rational F = A*A;
			Rational G = 2*A*B;
			Rational H = B*B;
			Rational I = 4*A*C;
			Rational J = 4*A*D;
			Rational K = 4*B*C;
			Rational L = 4*B*D;
			Rational M = 1 + 4*C*C + 2*A*E;
			Rational N = 8*C*D;
			Rational O = 1 + 4*D*D + 2*B*E;
			Rational P = 4*C*E;
			Rational Q = 4*D*E;
			Rational R = E*E - 1;

			surfacePoly = F*x*x*x*x + G*x*x*y*y + H*y*y*y*y
				+ I*x*x*x + J*x*x*y + K*x*y*y + L*y*y*y
				+ M*x*x + N*x*y + O*y*y
				+ P*x + Q*y + R;
		}

		// compute the origin elliptical curve projection
		Poly_rat_2 originPoly;
		{
			Rational S = d*d/oa2 + g*g/ob2 - j*j;
			Rational T = e*e/oa2 + h*h/ob2 - k*k;
			Rational U = f*f/oa2 + i*i/ob2 - l*l;
			Rational V = 2*( d*e/oa2 + g*h/ob2 - j*k );
			Rational W = 2*( d*f/oa2 + g*i/ob2 - j*l );
			Rational X = 2*( e*f/oa2 + h*i/ob2 - k*l );

			Rational F = A*A*U;
			Rational G = 2*A*B*U;
			Rational H = B*B*U;
			Rational I = 4*A*C*U + A*W;
			Rational J = 4*A*D*U + A*X;
			Rational K = 4*B*C*U + B*W;
			Rational L = 4*B*D*U + B*X;
			Rational M = 2*A*E*U + 4*C*C*U + 2*C*W + S;
			Rational N = 8*C*D*U + 2*C*X + 2*D*W + V;
			Rational O = 2*B*E*U + 4*D*D*U + 2*D*X + T;
			Rational P = 4*C*E*U + E*W;
			Rational Q = 4*D*E*U + E*X;
			Rational R = E*E*U;

			originPoly = F*x*x*x*x + G*x*x*y*y + H*y*y*y*y
				+ I*x*x*x + J*x*x*y + K*x*y*y + L*y*y*y
				+ M*x*x + N*x*y + O*y*y
				+ P*x + Q*y + R;
		}

		// return the intersection points
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, surfacePoly, originPoly );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			addPoint3ToList( jvm, out, Point_rat_3(
				point.x(),
				point.y(),
				A*point.x()*point.x() + B*point.y()*point.y() + 2*C*point.x() + 2*D*point.y() + E
			) );
		}
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectRdcEllipticalConePof( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble ddx, jdouble ddy, jdouble ddz, jdouble ddr,
	jdouble df, jdouble dg, jdouble dh, jdouble di, jdouble dj, jdouble dk, jdouble dl, jdouble dm, jdouble dn,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de
)
{
	START_SIGNAL_HANDLING
	{
		// number type aliasing
		Rational dx = ddx;
		Rational dy = ddy;
		Rational dz = ddz;
		Rational dr = ddr;

		Rational f = df;
		Rational g = dg;
		Rational h = dh;
		Rational i = di;
		Rational j = dj;
		Rational k = dk;
		Rational l = dl;
		Rational m = dm;
		Rational n = dn;

		Rational a = da;
		Rational b = db;
		Rational c = dc;
		Rational d = dd;
		Rational e = de;

		// we're always using the z-projection here, so don't bother with all the fancy classes
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );

		// do the easy one first, the RDC projection
		Poly_rat_2 rdcCurve;
		{
			Rational A = dx - dz;
			Rational B = dy - dz;
			Rational C = dz;
			rdcCurve = A*x*x + B*y*y + C;
		}

		// ok, now for the pain: the elliptical projection
		Poly_rat_2 ellipticalCurve;
		{
			// dot products are fun!! @_@
			Rational p = c*f + d*g + e*h;
			Rational q = c*i + d*j + e*k;
			Rational r = c*l + d*m + e*n;

			Rational a2 = a*a;
			Rational b2 = b*b;
			Rational f2 = f*f;
			Rational g2 = g*g;
			Rational h2 = h*h;
			Rational i2 = i*i;
			Rational j2 = j*j;
			Rational k2 = k*k;
			Rational l2 = l*l;
			Rational m2 = m*m;
			Rational n2 = n*n;
			Rational p2 = p*p;
			Rational q2 = q*q;
			Rational r2 = r*r;

			Rational f3 = f2*f;
			Rational g3 = g2*g;

			Rational a4 = a2*a2;
			Rational b4 = b2*b2;
			Rational f4 = f2*f2;
			Rational g4 = g2*g2;
			Rational h4 = h2*h2;
			Rational k4 = k2*k2;
			Rational n4 = n2*n2;
			Rational p4 = p2*p2;

			// NOTE: expressions generated automatically by Mathematica
			// let's hope they work...
			// I'm tired of writing all the math out by hand
			// also, this one's a doozy!
			// Aaaannd the verdict is in. HOLY CRAP this actually works!!
			// UNDONE: could try to optimize this arithmetic
			Rational A = (1/b)*(a4*(i2 + k2)*(i2 + k2) + 
					   b4*(f4 + h4 - 8*a2*f*h*l*n + 
					     2*a2*h2*(l2 - n2) + a4*(l2 + n2)*(l2 + n2) + 
					     2*f2*(h2 + a2*(-l2 + n2))) - 
					   2*a2*b2*(-4*f*h*i*k + h2*(i2 - k2) + 
					     f2*(-i2 + k2) + a2*(4*i*k*l*n + 
					       i2*(l2 - n2) + k2*(-l2 + n2))));
			Rational B = (1/b)*(4*(a4*i*j*(i2 + k2) + 
				    a2*b2*(f2*i*j - h2*i*j + 2*g*h*i*k + 
				      f*(2*h*j*k + g*(i2 - k2)) - 
				      a2*(i2*l*m + k*l*((-k)*m + 2*j*n) + 
				        i*(j*l2 + 2*k*m*n - j*n2))) + 
				    b4*(f3*g - a2*f2*l*m + 
				      a2*l*(h2*m - 2*g*h*n + a2*m*(l2 + n2)) + 
				      f*(-2*a2*h*m*n + g*(h2 + a2*(-l2 + n2))))));
			Rational C = (1/b)*(2*(a4*(k2*(j2 + k2) + i2*(3*j2 + k2)) + 
				    a2*b2*(f2*j2 + 4*f*h*i*k - f2*k2 + 
				      4*g*j*(f*i + h*k) - h2*(i2 + j2 - 2*k2) + 
				      g2*(i2 - k2) - a2*j2*l2 + a2*k2*l2 - 
				      4*a2*i*j*l*m - a2*i2*m2 + a2*k2*m2 - 
				      4*a2*i*k*l*n - 4*a2*j*k*m*n + a2*i2*n2 + 
				      a2*j2*n2 - 2*a2*k2*n2) + 
				    b4*(h4 + a2*h2*l2 + a2*h2*m2 + 
				      3*a4*l2*m2 - 4*a2*g*h*m*n - 2*a2*h2*n2 + 
				      a4*l2*n2 + a4*m2*n2 + a4*n4 - 
				      4*a2*f*l*(g*m + h*n) + 
				      g2*(h2 + a2*(-l2 + n2)) + 
				      f2*(3*g2 + h2 + a2*(-m2 + n2)))));
			Rational D = (1/b)*(4*(a4*i*j*(j2 + k2) + 
				    a2*b2*(g2*i*j - h2*i*j + 2*f*h*j*k + 
				      g*(2*h*i*k + f*(j2 - k2)) - 
				      a2*(j2*l*m + k*m*((-k)*l + 2*i*n) + 
				        j*(i*m2 + 2*k*l*n - i*n2))) + 
				    b4*(f*(g3 - 2*a2*h*m*n + 
				        g*(h2 + a2*(-m2 + n2))) + 
				      a2*l*((-g2)*m - 2*g*h*n + 
				        m*(h2 + a2*(m2 + n2))))));
			Rational E = (1/b)*(a4*(j2 + k2)*(j2 + k2) + 
					   b4*(g4 + h4 - 8*a2*g*h*m*n + 
					     2*a2*h2*(m2 - n2) + a4*(m2 + n2)*(m2 + n2) + 
					     2*g2*(h2 + a2*(-m2 + n2))) - 
					   2*a2*b2*(-4*g*h*j*k + h2*(j2 - k2) + 
					     g2*(-j2 + k2) + a2*(4*j*k*m*n + 
					       j2*(m2 - n2) + k2*(-m2 + n2))));
			Rational F = -((1/b)*(4*(a4*i*(i2 + k2)*q + 
				     b4*(f3*p - a2*f2*l*r + 
				       f*(h2*p + a2*(-l2 + n2)*p - 2*a2*h*n*r) + 
				       a2*l*(-2*h*n*p + h2*r + a2*(l2 + n2)*r)) + 
				     a2*b2*(2*h*i*k*p + f2*i*q - h2*i*q + 
				       f*(i2*p + k*((-k)*p + 2*h*q)) - 
				       a2*(i2*l*r + k*l*(2*n*q - k*r) + 
				         i*(l2*q - n2*q + 2*k*n*r))))));
			Rational G = -((1/b)*(4*(a4*j*(3*i2 + k2)*q + 
				     a2*b2*(2*f*i*j*p + 2*h*j*k*p + f2*j*q - 
				       h2*j*q - a2*j*l2*q - 2*a2*i*l*m*q - 
				       2*a2*k*m*n*q + a2*j*n2*q + 
				       g*(i2*p + 2*f*i*q + k*((-k)*p + 2*h*q)) - 
				       2*a2*i*j*l*r - a2*i2*m*r + a2*k2*m*r - 
				       2*a2*j*k*n*r) + b4*(-2*a2*f*l*(m*p + g*r) + 
				       f2*(3*g*p - a2*m*r) + 
				       g*(h2*p + a2*(-l2 + n2)*p - 2*a2*h*n*r) + 
				       a2*m*(-2*h*n*p + h2*r + a2*(3*l2 + n2)*
				          r)))));
			Rational H = -((1/b)*(4*(a4*i*(3*j2 + k2)*q + 
				     a2*b2*(2*h*i*k*p + g2*i*q - h2*i*q - 
				       2*a2*j*l*m*q - a2*i*m2*q - 2*a2*k*l*n*q + 
				       a2*i*n2*q + 2*g*j*(i*p + f*q) + 
				       f*(j2*p - k2*p + 2*h*k*q) - a2*j2*l*r + 
				       a2*k2*l*r - 2*a2*i*j*m*r - 2*a2*i*k*n*r) + 
				     b4*(f*(3*g2*p + h2*p + a2*(-m2 + n2)*p - 
				         2*a2*g*m*r - 2*a2*h*n*r) + 
				       a2*l*(-2*g*m*p - 2*h*n*p - g2*r + h2*r + 
				         a2*(3*m2 + n2)*r)))));
			Rational I = -((1/b)*(4*(a4*j*(j2 + k2)*q + 
				     b4*(g3*p - a2*g2*m*r + 
				       g*(h2*p + a2*(-m2 + n2)*p - 2*a2*h*n*r) + 
				       a2*m*(-2*h*n*p + h2*r + a2*(m2 + n2)*r)) + 
				     a2*b2*(2*h*j*k*p + g2*j*q - h2*j*q + 
				       g*(j2*p + k*((-k)*p + 2*h*q)) - 
				       a2*(j2*m*r + k*m*(2*n*q - k*r) + 
				         j*(m2*q - n2*q + 2*k*n*r))))));
			Rational J = -((1/b)*(2*(a4*(k4 - k2*q2 + i2*(k2 - 3*q2)) + 
				     a2*b2*(a2*k2*l2 - 4*a2*i*k*l*n + 
				       a2*i2*n2 - 2*a2*k2*n2 - i2*p2 + 
				       k2*p2 - 4*f*i*p*q + a2*l2*q2 - 
				       a2*n2*q2 + 4*h*k*(f*i - p*q) - 
				       f2*(k2 + q2) + h2*(-i2 + 2*k2 + q2) + 
				       4*a2*i*l*q*r + 4*a2*k*n*q*r + a2*i2*r2 - 
				       a2*k2*r2) + b4*(h4 + 4*a2*h*n*p*r + 
				       4*a2*f*l*((-h)*n + p*r) + 
				       h2*(-p2 + a2*(l2 - 2*n2 - r2)) + 
				       f2*(h2 - 3*p2 + a2*(n2 + r2)) + 
				       a2*((l2 - n2)*p2 + a2*(n4 - n2*r2 + 
				           l2*(n2 - 3*r2)))))));
			Rational K = -((1/b)*(4*(a4*i*j*(k2 - 3*q2) + 
				     a2*b2*((-h2)*i*j + 2*h*(g*i + f*j)*k + 
				       a2*k2*l*m - 2*a2*j*k*l*n - 2*a2*i*k*m*n + 
				       a2*i*j*n2 - i*j*p2 - 2*g*i*p*q + 
				       a2*l*m*q2 - f*(2*j*p*q + g*(k2 + q2)) + 
				       2*a2*j*l*q*r + 2*a2*i*m*q*r + a2*i*j*r2) + 
				     b4*(a2*l*(h2*m - 2*g*h*n + p*(m*p + 2*g*r) + 
				         a2*m*(n2 - 3*r2)) + 
				       f*(2*a2*m*((-h)*n + p*r) + 
				         g*(h2 - 3*p2 + a2*(n2 + r2)))))));
			Rational L = -((1/b)*(2*(a4*(k4 - k2*q2 + j2*(k2 - 3*q2)) + 
				     a2*b2*(a2*k2*m2 - 4*a2*j*k*m*n + 
				       a2*j2*n2 - 2*a2*k2*n2 - j2*p2 + 
				       k2*p2 - 4*g*j*p*q + a2*m2*q2 - 
				       a2*n2*q2 + 4*h*k*(g*j - p*q) - 
				       g2*(k2 + q2) + h2*(-j2 + 2*k2 + q2) + 
				       4*a2*j*m*q*r + 4*a2*k*n*q*r + a2*j2*r2 - 
				       a2*k2*r2) + b4*(h4 + 4*a2*h*n*p*r + 
				       4*a2*g*m*((-h)*n + p*r) + 
				       h2*(-p2 + a2*(m2 - 2*n2 - r2)) + 
				       g2*(h2 - 3*p2 + a2*(n2 + r2)) + 
				       a2*((m2 - n2)*p2 + a2*(n4 - n2*r2 + 
				           m2*(n2 - 3*r2)))))));
			Rational M = (1/b)*(4*(a4*i*q*(k2 - q2) + 
				    a2*b2*((-h2)*i*q - 2*a2*k*l*n*q + 
				      a2*i*n2*q - i*p2*q + 2*h*k*(i*p + f*q) - 
				      f*p*(k2 + q2) + a2*k2*l*r - 2*a2*i*k*n*r + 
				      a2*l*q2*r + a2*i*q*r2) + 
				    b4*(a2*l*(-2*h*n*p + h2*r + 
				        r*(p2 + a2*(n2 - r2))) + 
				      f*(h2*p - 2*a2*h*n*r + 
				        p*(-p2 + a2*(n2 + r2))))));
			Rational N = (1/b)*(4*(a4*j*q*(k2 - q2) + 
				    a2*b2*((-h2)*j*q - 2*a2*k*m*n*q + 
				      a2*j*n2*q - j*p2*q + 2*h*k*(j*p + g*q) - 
				      g*p*(k2 + q2) + a2*k2*m*r - 2*a2*j*k*n*r + 
				      a2*m*q2*r + a2*j*q*r2) + 
				    b4*(a2*m*(-2*h*n*p + h2*r + 
				        r*(p2 + a2*(n2 - r2))) + 
				      g*(h2*p - 2*a2*h*n*r + 
				        p*(-p2 + a2*(n2 + r2))))));
			Rational O = (1/b)*(a4*(k2 - q2)*(k2 - q2) + 
					   b4*(h4 + p4 + 8*a2*h*n*p*r + 
					     a4*(n2 - r2)*(n2 - r2) - 2*a2*p2*(n2 + r2) - 
					     2*h2*(p2 + a2*(n2 + r2))) - 
					   2*a2*b2*(4*h*k*p*q - h2*(k2 + q2) - 
					     p2*(k2 + q2) + a2*(-4*k*n*q*r + 
					       k2*(n2 + r2) + q2*(n2 + r2))));
			ellipticalCurve =
				A*x*x*x*x + B*x*x*x*y + C*x*x*y*y + D*x*y*y*y + E*y*y*y*y
				+ F*x*x*x + G*x*x*y + H*x*y*y + I*y*y*y
				+ J*x*x + K*x*y + L*y*y
				+ M*x + N*y + O;
		}
		
		// return the intersection points
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, rdcCurve, ellipticalCurve );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			double px = CGAL::to_double( point.x() );
			double py = CGAL::to_double( point.y() );
			double pz = sqrt( CGAL::to_double( 1 - px*px - py*py ) );
			addPoint3ToList( jvm, out, Point_rat_3(
				point.x(),
				point.y(),
				pz
			) );
			addPoint3ToList( jvm, out, Point_rat_3(
				point.x(),
				point.y(),
				-pz
			) );
		}
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_EllipticalCurve_getTangentQueryIntersectionPoints( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble da, jdouble db, jdouble dc, jdouble dd, jdouble de,
	jdouble dcx, jdouble dcy, jdouble dcz
)
{
	START_SIGNAL_HANDLING
	{
		// number type aliasing
		Rational a = da;
		Rational b = db;
		Rational c = dc;
		Rational d = dd;
		Rational e = de;
		Rational cx = dcx;
		Rational cy = dcy;
		Rational cz = dcz;
		
		// set up our independent variables
		Poly_rat_2 x = CGAL::shift( Poly_rat_2( 1 ), 1, 0 );
		Poly_rat_2 y = CGAL::shift( Poly_rat_2( 1 ), 1, 1 );

		// build the curves
		Poly_rat_2 p = -2*( a*c*x + b*d*y + e );
		Poly_rat_2 pp = -2*( b*d*x - a*c*y );
		Poly_rat_2 q = a*a*x*x + b*b*y*y + 1;
		Poly_rat_2 qp = 2*( b*b - a*a )*x*y;
		Poly_rat_2 poly = ( pp*q - p*qp )*( cx*a*x + cy*b*y + cz ) - p*q*( cx*a*y - cy*b*x );
		Poly_rat_2 unitCircle = x*x + y*y - 1;
		
		// return the intersection points
		std::vector<Point_rat_2> points;
		getIntersectionPoints( &points, poly, unitCircle );
		for( std::vector<Point_rat_2>::iterator iter = points.begin(); iter != points.end(); iter++ )
		{
			Point_rat_2 point = *iter;
			addPoint2ToList( jvm, out, Point_rat_2(
				point.x(),
				point.y()
			) );
		}
	}
	STOP_SIGNAL_HANDLING
}
