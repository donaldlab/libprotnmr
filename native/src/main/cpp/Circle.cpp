#include <jni.h>

#include "cgal.h"
#include "native.h"
#include "global.h"
#include "types.h"
#include "Storage.h"


#define CLASS                               CLASSTOKEN( cgal_spherical_Circle3 )
#define Circle3_init                        CLASSFN( CLASS, init )
#define Circle3_cleanup                     CLASSFN( CLASS, cleanup )
#define Circle3_getCenter                   CLASSFN( CLASS, getCenter )
#define Circle3_getSquaredRadius            CLASSFN( CLASS, getSquaredRadius )
#define Circle3_getNormal                   CLASSFN( CLASS, getNormal )


static Storage<Circle_3> g_circles( SPHERICALCLASS( "Circle3" ), true );


Circle_3 *getCircle( JNIEnv *jvm, jobject self )
{
	return g_circles.get( jvm, self );
}

jobject newCircle( JNIEnv *jvm, const Circle_3 &circle )
{
	return g_circles.addNew( jvm, new Circle_3( circle ) );
}

void circularArcsCleanup( JNIEnv *jvm )
{
	g_circles.cleanupAll( jvm );
}

JNIEXPORT void JNICALL Circle3_init( JNIEnv *jvm, jobject self, jobject a, jobject b, jobject c )
{
	START_SIGNAL_HANDLING
	{
		g_circles.add( jvm, self, new Circle_3(
			Point_3( getVector3X( jvm, a ), getVector3Y( jvm, a ), getVector3Z( jvm, a ) ),
			Point_3( getVector3X( jvm, b ), getVector3Y( jvm, b ), getVector3Z( jvm, b ) ),
			Point_3( getVector3X( jvm, c ), getVector3Y( jvm, c ), getVector3Z( jvm, c ) )
		) );
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL Circle3_cleanup( JNIEnv *jvm, jclass c, jint id )
{
	g_circles.cleanupAll( jvm );
}

JNIEXPORT jobject JNICALL Circle3_getCenter( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circle_3 *pCircle = getCircle( jvm, self );
		return newVector3(
			jvm,
			to_double( pCircle->center().x() ),
			to_double( pCircle->center().y() ),
			to_double( pCircle->center().z() )
		);
	}
	STOP_SIGNAL_HANDLING
	return NULL;
}

JNIEXPORT jdouble JNICALL Circle3_getSquaredRadius( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circle_3 *pCircle = getCircle( jvm, self );
		return to_double( pCircle->squared_radius() );
	}
	STOP_SIGNAL_HANDLING
	return 0.0;
}

JNIEXPORT jobject JNICALL Circle3_getNormal( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circle_3 *pCircle = getCircle( jvm, self );
		return newVector3(
			jvm,
			to_double( pCircle->supporting_plane().orthogonal_vector().x() ),
			to_double( pCircle->supporting_plane().orthogonal_vector().y() ),
			to_double( pCircle->supporting_plane().orthogonal_vector().z() )
		);
	}
	STOP_SIGNAL_HANDLING
	return NULL;
}

