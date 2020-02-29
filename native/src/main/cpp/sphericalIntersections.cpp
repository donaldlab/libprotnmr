

#include <jni.h>

#include <CGAL/basic.h>
#include <CGAL/Sweep_line_2_algorithms.h>
#include <CGAL/Exact_spherical_kernel_3.h>

#include "native.h"
#include "global.h"

typedef CGAL::Exact_spherical_kernel_3 Spherical_k;
typedef CGAL::Point_3<Spherical_k> Point_3;
typedef CGAL::Vector_3<Spherical_k> Vector_3;
typedef CGAL::Sphere_3<Spherical_k> Sphere_3;
typedef CGAL::Circle_3<Spherical_k> Circle_3;
typedef CGAL::Plane_3<Spherical_k> Plane_3;
typedef Spherical_k::Circular_arc_point_3 Circular_arc_point_3;


static const Sphere_3 UNIT_SPHERE = Sphere_3( CGAL::ORIGIN, 1 );
static const CGAL::SphericalFunctors::Intersect_3<Spherical_k> intersect_3 = Spherical_k::Intersect_3();

static jclass g_classList = NULL;
static jmethodID g_methodListAdd = NULL;


void returnIntersectionPoints( JNIEnv *jvm, jobject out, Circle_3 a, Circle_3 b )
{
	// find our java classes and methods
	if( g_classList == NULL )
	{
		g_classList = (jclass)jvm->NewGlobalRef( jvm->FindClass( "java/util/List" ) );
		checkException( jvm );
	}
	if( g_methodListAdd == NULL )
	{
		g_methodListAdd = jvm->GetMethodID( g_classList, "add", "(Ljava/lang/Object;)Z" );
		checkException( jvm );
	}

	// enumerate the intersection points
	std::vector<CGAL::Object> intersections;
	intersect_3( a, b, std::back_inserter( intersections ) );
	for( std::vector<CGAL::Object>::iterator iter = intersections.begin(); iter != intersections.end(); iter++ )
	{
		CGAL::Object obj = *iter;
		if( const std::pair<Circular_arc_point_3,unsigned> *pair = CGAL::object_cast<std::pair<Circular_arc_point_3,unsigned> >( &obj ) )
		{
			// add the point to the out list (don't care about multiplicity)
			jvm->CallBooleanMethod( out, g_methodListAdd, newVector3( jvm,
				CGAL::to_double( pair->first.x() ),
				CGAL::to_double( pair->first.y() ),
				CGAL::to_double( pair->first.z() )
			) );
			checkException( jvm );
		}
	}
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_Intersector_intersectCircularCircular( JNIEnv *jvm, jclass clazz,
	jobject out,
	jobject jn1, jdouble dh1,
	jobject jn2, jdouble dh2
)
{
	START_SIGNAL_HANDLING
	{
		// convert the inputs to kernel types
		Vector_3 n1( getVector3X( jvm, jn1 ), getVector3Y( jvm, jn1 ), getVector3Z( jvm, jn1 ) );
		Vector_3 n2( getVector3X( jvm, jn2 ), getVector3Y( jvm, jn2 ), getVector3Z( jvm, jn2 ) );
		
		// build the circles
		Spherical_k::FT h1 = dh1;
		Spherical_k::FT h2 = dh2;
		Circle_3 a( UNIT_SPHERE, Plane_3( n1.x(), n1.y(), n1.z(), -h1 ) );
		Circle_3 b( UNIT_SPHERE, Plane_3( n2.x(), n2.y(), n2.z(), -h2 ) );

		returnIntersectionPoints( jvm, out, a, b );
	}
	STOP_SIGNAL_HANDLING
}
