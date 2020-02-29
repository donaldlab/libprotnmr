#include <jni.h>

#include "cgal.h"
#include "native.h"
#include "global.h"
#include "types.h"
#include "Storage.h"


#define CLASS                               CLASSTOKEN( cgal_spherical_CircularArc3 )
#define CircularArc3_init                   CLASSFN( CLASS, init )
#define CircularArc3_cleanup                CLASSFN( CLASS, cleanup )
#define CircularArc3_getSupportingCircle    CLASSFN( CLASS, getSupportingCircle )
#define CircularArc3_getSource              CLASSFN( CLASS, getSource )
#define CircularArc3_getTarget              CLASSFN( CLASS, getTarget )


static Storage<Circular_arc_3> g_circularArcs( SPHERICALCLASS( "CircularArc3" ), true );


Circular_arc_3 *getCircularArc( JNIEnv *jvm, jobject self )
{
	return g_circularArcs.get( jvm, self );
}

jobject newCircularArc( JNIEnv *jvm, const Circular_arc_3 &circularArc )
{
	return g_circularArcs.addNew( jvm, new Circular_arc_3( circularArc ) );
}

void circlesCleanup( JNIEnv *jvm )
{
	g_circularArcs.cleanupAll( jvm );
}

JNIEXPORT void JNICALL CircularArc3_init( JNIEnv *jvm, jobject self, jobject supportingCircle, jobject source, jobject target )
{
	START_SIGNAL_HANDLING
	{
		Circle_3 *pCircle = getCircle( jvm, supportingCircle );
		g_circularArcs.add( jvm, self, new Circular_arc_3(
			*pCircle,
			Point_3( getVector3X( jvm, source ), getVector3Y( jvm, source ), getVector3Z( jvm, source ) ),
			Point_3( getVector3X( jvm, target ), getVector3Y( jvm, target ), getVector3Z( jvm, target ) )
		) );
	}
	STOP_SIGNAL_HANDLING
}

JNIEXPORT void JNICALL CircularArc3_cleanup( JNIEnv *jvm, jclass c, jint id )
{
	g_circularArcs.cleanupAll( jvm );
}

JNIEXPORT jobject JNICALL CircularArc3_getSupportingCircle( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circular_arc_3 *pCircularArc = getCircularArc( jvm, self );
		return newCircle( jvm, pCircularArc->supporting_circle() );
	}
	STOP_SIGNAL_HANDLING
	return NULL;
}

JNIEXPORT jobject JNICALL CircularArc3_getSource( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circular_arc_3 *pCircularArc = getCircularArc( jvm, self );
		return newVector3(
			jvm,
			to_double( pCircularArc->source().x() ),
			to_double( pCircularArc->source().y() ),
			to_double( pCircularArc->source().z() )
		);
	}
	STOP_SIGNAL_HANDLING
	return NULL;
}

JNIEXPORT jobject JNICALL CircularArc3_getTarget( JNIEnv *jvm, jobject self )
{
	START_SIGNAL_HANDLING
	{
		Circular_arc_3 *pCircularArc = getCircularArc( jvm, self );
		return newVector3(
			jvm,
			to_double( pCircularArc->target().x() ),
			to_double( pCircularArc->target().y() ),
			to_double( pCircularArc->target().z() )
		);
	}
	STOP_SIGNAL_HANDLING
	return NULL;
}
