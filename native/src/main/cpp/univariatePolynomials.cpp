
#include <jni.h>

#include <CGAL/Gmpz.h>
#include <CGAL/Algebraic_kernel_d_1.h>

#include "native.h"
#include "global.h"


typedef CORE::BigRat Rational;
typedef CORE::BigInt Integer;
typedef CGAL::Algebraic_kernel_d_1<Integer> Kernel;
typedef Kernel::Polynomial_1 Poly_int_1;
typedef CGAL::Polynomial_traits_d<Poly_int_1>::Rebind<Rational,1>::Other::Polynomial_d Poly_rat_1;
typedef Kernel::Algebraic_real_1 Algebraic_real_1;


// kernel and traits functors
Kernel kernel; 
Kernel::Solve_1 solve = kernel.solve_1_object();
CGAL::Fraction_traits<Poly_rat_1>::Decompose decompose;


JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_getEllipseOptima( JNIEnv *jvm, jclass clazz,
	jobject out,
	jdouble da2, jdouble db2,
	jdouble dc, jdouble dd, jdouble de,
	jdouble df, jdouble dg, jdouble dh, jdouble di, jdouble dj, jdouble dk, jdouble dl, jdouble dm, jdouble dn,
	jdouble dz
)
{
	START_SIGNAL_HANDLING
	{
		// number type aliases
		Rational a2 = da2;
		Rational b2 = db2;
		
		Rational c = dc;
		Rational d = dd;
		Rational e = de;
		
		Rational f = df;
		Rational g = dg;
		Rational h = dh;
		Rational i = di;
		Rational j = dj;
		Rational k = dk;
		Rational l = dl;
		Rational m = dm;
		Rational n = dn;
		
		Rational z = dz;
		
		// compute the non-x-y-associated terms
		Rational o = h*z - f*c - g*d - h*e;
		Rational p = k*z - i*c - j*d - k*e;
		Rational q = n*z - l*c - m*d - n*e;
		
		// premultiplications
		Rational f2 = f*f;
		Rational g2 = g*g;
		Rational i2 = i*i;
		Rational j2 = j*j;
		Rational l2 = l*l;
		Rational m2 = m*m;
		Rational o2 = o*o;
		Rational p2 = p*p;
		Rational q2 = q*q;
		
		// build the planar conic section
		Rational r = f2/a2 + i2/b2 - l2;
		Rational s = g2/a2 + j2/b2 - m2;
		Rational t = 2*( f*g/a2 + i*j/b2 - l*m );
		Rational u = 2*( f*o/a2 + i*p/b2 - l*q );
		Rational v = 2*( g*o/a2 + j*p/b2 - m*q );
		Rational w = o2/a2 + p2/b2 - q2;
		
		// more premultiplications
		Rational r2 = r*r;
		Rational s2 = s*s;
		Rational t2 = t*t;
		Rational u2 = u*u;
		Rational v2 = v*v;
		Rational w2 = w*w;
		
		Rational r3 = r*r2;
		Rational s3 = s*s2;
		Rational t3 = t*t2;
		Rational u3 = u*u2;
		Rational v3 = v*v2;
		
		Rational t4 = t2*t2;
		
		// build the quartic polynomial
		Poly_rat_1 ratPoly;
		{
			Rational P0 = s*u2*w - t*u*v*w + t2*w2;
			Rational P1 = s*u3 - t*u2*v + s*u*v2 - t*v3 + 4*r*s*u*w - 4*s2*u*w + t2*u*w - 2*r*t*v*w + 4*s*t*v*w;
			Rational P2 = 5*r*s*u2 - 4*s2*u2 - 3*r*t*u*v + 6*s*t*u*v + 2*r*s*v2 - s2*v2 - 3*t2*v2 + 4*r2*s*w - 8*r*s2*w + 4*s3*w + 4*s*t2*w;
			Rational P3 = 8*r2*s*u - 12*r*s2*u + 4*s3*u - r*t2*u + 5*s*t2*u - 2*r2*t*v + 8*r*s*t*v - 2*s2*t*v - 3*t3*v;
			Rational P4 = 4*r3*s - 8*r2*s2 + 4*r*s3 - r2*t2 + 6*r*s*t2 - s2*t2 - t4;
			Poly_rat_1 x = CGAL::shift( Poly_rat_1( 1 ), 1 );
			ratPoly = P0 + P1*x + P2*x*x + P3*x*x*x + P4*x*x*x*x;
		}
		
		// convert it to an integer poly (FOR MAD SPEEED!!)
		Poly_int_1 intPoly;
		Integer denominator;
		decompose( ratPoly, intPoly, denominator );
		
		// solve for the real roots (ie the x-values of the optima)
		std::vector<Algebraic_real_1> xvals;
		solve( intPoly, true, std::back_inserter( xvals ) );
		
		// convert the roots to distances and send them back to java
		for( std::vector<Algebraic_real_1>::iterator xiter = xvals.begin(); xiter != xvals.end(); xiter++ )
		{
			double dx = CGAL::to_double( *xiter );
			Rational x = Rational( dx );
			
			// solve the ellipse poly for y
			Rational A = s;
			Rational B = v + t*x;
			Rational C = w + u*x + r*x*x;
			
			// get the y-values (quadratic formula)
			std::vector<Rational> yvals;
			Rational D = B*B - 4*A*C;
			if( D >= 0 )
			{
				Rational E = Rational( sqrt( CGAL::to_double( D ) ) );
				if( E > 0 )
				{
					yvals.push_back( ( -B + E )/2/A );
					yvals.push_back( ( -B - E )/2/A );
				}
				else
				{
					yvals.push_back( -B/2/A );
				}
			}
			
			// finally, get the optima
			for( std::vector<Rational>::iterator yiter = yvals.begin(); yiter != yvals.end(); yiter++ )
			{
				double dy = CGAL::to_double( *yiter );
				
				addVector3ToList( jvm, out, dx, dy, dz );
			}
		}
	}
	STOP_SIGNAL_HANDLING
}
