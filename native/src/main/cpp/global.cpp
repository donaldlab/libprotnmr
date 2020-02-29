

#include <jni.h>

#include "native.h"
#include "global.h"


AbortSignal g_abortSignal;

static jclass g_abstractCleanableClass = NULL;
static jfieldID g_idFieldId = NULL;
static jfieldID g_pointerFieldId = NULL;
static jclass g_vector3Class = NULL;
static jmethodID g_vector3ConstructorId = NULL;
static jfieldID g_vector3XFieldId = NULL;
static jfieldID g_vector3YFieldId = NULL;
static jfieldID g_vector3ZFieldId = NULL;
static jclass g_vector2Class = NULL;
static jmethodID g_vector2ConstructorId = NULL;
static jfieldID g_vector2XFieldId = NULL;
static jfieldID g_vector2YFieldId = NULL;
static jclass g_listClass = NULL;
static jmethodID g_listAddMethodId = NULL;
static jclass g_doubleClass = NULL;
static jmethodID g_doubleConstructorId = NULL;


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
	if( g_vector3Class == NULL )
	{
		g_vector3Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vector3ConstructorId == NULL )
	{
		g_vector3ConstructorId = jvm->GetMethodID( g_vector3Class, "<init>", "(DDD)V" );
		checkException( jvm );
	}

	// return the vector
	jobject vector = jvm->NewObject( g_vector3Class, g_vector3ConstructorId, x, y, z );
	checkException( jvm );
	return vector;
}

double getVector3X( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vector3Class == NULL )
	{
		g_vector3Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vector3XFieldId == NULL )
	{
		g_vector3XFieldId = jvm->GetFieldID( g_vector3Class, "x", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vector3XFieldId );
	checkException( jvm );
	return val;
}

double getVector3Y( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vector3Class == NULL )
	{
		g_vector3Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vector3YFieldId == NULL )
	{
		g_vector3YFieldId = jvm->GetFieldID( g_vector3Class, "y", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vector3YFieldId );
	checkException( jvm );
	return val;
}

double getVector3Z( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vector3Class == NULL )
	{
		g_vector3Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector3" ) ) );
	}
	if( g_vector3ZFieldId == NULL )
	{
		g_vector3ZFieldId = jvm->GetFieldID( g_vector3Class, "z", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vector3ZFieldId );
	checkException( jvm );
	return val;
}

jobject newVector2( JNIEnv *jvm, double x, double y )
{
	// lookup meta info if needed
	if( g_vector2Class == NULL )
	{
		g_vector2Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector2" ) ) );
	}
	if( g_vector2ConstructorId == NULL )
	{
		g_vector2ConstructorId = jvm->GetMethodID( g_vector2Class, "<init>", "(DD)V" );
		checkException( jvm );
	}

	// return the vector
	jobject vector = jvm->NewObject( g_vector2Class, g_vector2ConstructorId, x, y );
	checkException( jvm );
	return vector;
}

double getVector2X( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vector2Class == NULL )
	{
		g_vector2Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector2" ) ) );
	}
	if( g_vector2XFieldId == NULL )
	{
		g_vector2XFieldId = jvm->GetFieldID( g_vector2Class, "x", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vector2XFieldId );
	checkException( jvm );
	return val;
}

double getVector2Y( JNIEnv *jvm, jobject vector )
{
	// lookup meta info if needed
	if( g_vector2Class == NULL )
	{
		g_vector2Class = (jclass)jvm->NewGlobalRef( jvm->FindClass( GEOMCLASS( "Vector2" ) ) );
	}
	if( g_vector2YFieldId == NULL )
	{
		g_vector2YFieldId = jvm->GetFieldID( g_vector2Class, "y", "D" );
		checkException( jvm );
	}

	// get the coord
	double val = jvm->GetDoubleField( vector, g_vector2YFieldId );
	checkException( jvm );
	return val;
}

void addVector2ToList( JNIEnv *jvm, jobject out, double x, double y )
{
	// lookup meta info if needed
	if( g_listClass == NULL )
	{
		g_listClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( "java/util/List" ) );
		checkException( jvm );
	}
	if( g_listAddMethodId == NULL )
	{
		g_listAddMethodId = jvm->GetMethodID( g_listClass, "add", "(Ljava/lang/Object;)Z" );
		checkException( jvm );
	}

	// add the point
	jvm->CallBooleanMethod( out, g_listAddMethodId, newVector2( jvm, x, y ) );
	checkException( jvm );
}

void addVector3ToList( JNIEnv *jvm, jobject out, double x, double y, double z )
{
	// lookup meta info if needed
	if( g_listClass == NULL )
	{
		g_listClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( "java/util/List" ) );
		checkException( jvm );
	}
	if( g_listAddMethodId == NULL )
	{
		g_listAddMethodId = jvm->GetMethodID( g_listClass, "add", "(Ljava/lang/Object;)Z" );
		checkException( jvm );
	}

	// add the point
	jvm->CallBooleanMethod( out, g_listAddMethodId, newVector3( jvm, x, y, z ) );
	checkException( jvm );
}

jobject newDouble( JNIEnv *jvm, double x )
{
	// lookup meta info if needed
	if( g_doubleClass == NULL )
	{
		g_doubleClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( "java/lang/Double" ) );
	}
	if( g_doubleConstructorId == NULL )
	{
		g_doubleConstructorId = jvm->GetMethodID( g_doubleClass, "<init>", "(D)V" );
		checkException( jvm );
	}

	// return the vector
	jobject jDouble = jvm->NewObject( g_doubleClass, g_doubleConstructorId, x );
	checkException( jvm );
	return jDouble;
}

void addDoubleToList( JNIEnv *jvm, jobject out, double x )
{
	// lookup meta info if needed
	if( g_listClass == NULL )
	{
		g_listClass = (jclass)jvm->NewGlobalRef( jvm->FindClass( "java/util/List" ) );
		checkException( jvm );
	}
	if( g_listAddMethodId == NULL )
	{
		g_listAddMethodId = jvm->GetMethodID( g_listClass, "add", "(Ljava/lang/Object;)Z" );
		checkException( jvm );
	}

	// add the point
	jvm->CallBooleanMethod( out, g_listAddMethodId, newDouble( jvm, x ) );
	checkException( jvm );
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
	if( g_vector3Class != NULL )
	{
		jvm->DeleteGlobalRef( g_vector3Class );
		g_vector3Class = NULL;
	}
}

JNIEXPORT void JNICALL Java_edu_duke_cs_libprotnmr_cgal_curves_CurvesCgal_nativeCleanup( JNIEnv *jvm, jclass self )
{
	// TODO: what to cleanup here?
}
