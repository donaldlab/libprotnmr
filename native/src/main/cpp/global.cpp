

#include <jni.h>

#include "native.h"
#include "global.h"


AbortSignal g_abortSignal;

static jclass g_abstractCleanableClass = NULL;
static jfieldID g_idFieldId = NULL;
static jfieldID g_pointerFieldId = NULL;
static jclass g_vectorClass = NULL;
static jmethodID g_vectorConstructorId = NULL;
static jfieldID g_vectorXFieldId = NULL;
static jfieldID g_vectorYFieldId = NULL;
static jfieldID g_vectorZFieldId = NULL;


void checkException( JNIEnv *jvm )
{
	if( jvm->ExceptionCheck() )
	{
		// bail out of C++ code so we can handle the exception in java
		throw g_abortSignal;
	}
}

void throwException( JNIEnv *jvm, const char *message )
{
	jvm->ThrowNew( jvm->FindClass( "java/lang/Exception" ), message );
	throw g_abortSignal;
}

void throwIllegalArgumentException( JNIEnv *jvm, const char *message )
{
	jvm->ThrowNew( jvm->FindClass( "java/lang/IllegalArgumentException" ), message );
	throw g_abortSignal;
}

int getId( JNIEnv *jvm, jobject self )
{
	// lookup meta info if needed
	if( g_abstractCleanableClass == NULL )
	{
		g_abstractCleanableClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( CGALCLASS( "AbstractCleanable" ) ) );
		checkException( jvm );
	}
	if( g_idFieldId == NULL )
	{
		g_idFieldId = jvm->GetFieldID( g_abstractCleanableClass, "m_id", "I" );
		checkException( jvm );
	}

	// get the id
	int id = jvm->GetIntField( self, g_idFieldId );
	checkException( jvm );

	// make sure the id is valid
	if( id < 0 )
	{
		throwException( jvm, "Inavlid Id!" );
	}

	return id;
}

void setId( JNIEnv *jvm, jobject self, int id )
{
	// lookup meta info if needed
	if( g_abstractCleanableClass == NULL )
	{
		g_abstractCleanableClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( CGALCLASS( "AbstractCleanable" ) ) );
		checkException( jvm );
	}
	if( g_idFieldId == NULL )
	{
		g_idFieldId = jvm->GetFieldID( g_abstractCleanableClass, "m_id", "I" );
		checkException( jvm );
	}

	// set the id
	jvm->SetIntField( self, g_idFieldId, id );
	checkException( jvm );
}

void *getPointer( JNIEnv *jvm, jobject self )
{
	// lookup meta info if needed
	if( g_abstractCleanableClass == NULL )
	{
		g_abstractCleanableClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( CGALCLASS( "AbstractCleanable" ) ) );
		checkException( jvm );
	}
	if( g_pointerFieldId == NULL )
	{
		g_pointerFieldId = jvm->GetFieldID( g_abstractCleanableClass, "m_pointer", "J" );
		checkException( jvm );
	}

	// get the pointer
	void *p = (void *)jvm->GetLongField( self, g_pointerFieldId );
	checkException( jvm );
	return p;
}

void setPointer( JNIEnv *jvm, jobject self, void *p )
{
	// lookup meta info if needed
	if( g_abstractCleanableClass == NULL )
	{
		g_abstractCleanableClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( CGALCLASS( "AbstractCleanable" ) ) );
		checkException( jvm );
	}
	if( g_pointerFieldId == NULL )
	{
		g_pointerFieldId = jvm->GetFieldID( g_abstractCleanableClass, "m_pointer", "J" );
		checkException( jvm );
	}

	// set the id
	jvm->SetLongField( self, g_pointerFieldId, (jlong)p );
	checkException( jvm );
}

jobject newVector3( JNIEnv *jvm, double x, double y, double z )
{
	// lookup meta info if needed
	if( g_vectorClass == NULL )
	{
		g_vectorClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vectorConstructorId == NULL )
	{
		g_vectorConstructorId = jvm->GetMethodID( g_vectorClass, "<init>", "(DDD)V" );
		checkException( jvm );
	}

	// return the vector
	jobject vector = jvm->NewObject( g_vectorClass, g_vectorConstructorId, x, y, z );
	checkException( jvm );
	return vector;
}

double getVectorX( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vectorClass == NULL )
	{
		g_vectorClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vectorXFieldId == NULL )
	{
		g_vectorXFieldId = jvm->GetFieldID( g_vectorClass, "x", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vectorXFieldId );
	checkException( jvm );
	return val;
}

double getVectorY( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vectorClass == NULL )
	{
		g_vectorClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vectorYFieldId == NULL )
	{
		g_vectorYFieldId = jvm->GetFieldID( g_vectorClass, "y", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vectorYFieldId );
	checkException( jvm );
	return val;
}

double getVectorZ( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vectorClass == NULL )
	{
		g_vectorClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vectorZFieldId == NULL )
	{
		g_vectorZFieldId = jvm->GetFieldID( g_vectorClass, "z", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vectorZFieldId );
	checkException( jvm );
	return val;
}


// prototypes for the cleanup functions
void circlesCleanup( JNIEnv *jvm );
void circularArcsCleanup( JNIEnv *jvm );

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_spherical_SphericalCgal_nativeCleanup( JNIEnv *jvm, jclass self )
{
	// release all the resources used by the various sub-components
	circlesCleanup( jvm );
	circularArcsCleanup( jvm );

	if( g_abstractCleanableClass != NULL )
	{
		jvm->DeleteGlobalRef( g_abstractCleanableClass );
		g_abstractCleanableClass = NULL;
	}
	if( g_vectorClass != NULL )
	{
		jvm->DeleteGlobalRef( g_vectorClass );
		g_vectorClass = NULL;
	}
}
