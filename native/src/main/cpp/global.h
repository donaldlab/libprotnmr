

#ifndef GLOBAL_H_
#define GLOBAL_H_

#define UNDER( a, b ) a##_##b

#define CLASSTOKEN( name )      UNDER(Java_edu_duke_cs_libprotnmr, name)
#define CLASSFN( c, fn )		UNDER( c, fn )

#define PACKAGE_PATH "edu/duke/cs/libprotnmr/"
#define SPHERICALCLASS( name )  PACKAGE_PATH "cgal/spherical/" name
#define CGALCLASS( name )       PACKAGE_PATH "cgal/" name
#define GEOMCLASS( name )       PACKAGE_PATH "geom/" name

#define SAFE_DELETE( p )	{ if( (p) != NULL ) { delete (p); (p) = NULL; } }

// signal handling to give code an escape hatch back to Java
#define START_SIGNAL_HANDLING	try
#define STOP_SIGNAL_HANDLING	catch( AbortSignal &s ) { s.doNothing(); }
class AbortSignal { public: void doNothing( ) { } };
extern AbortSignal g_abortSignal;

// global funcs
void checkException( JNIEnv *jvm );
void throwException( JNIEnv *jvm, const char *message );
void throwIllegalArgumentException( JNIEnv *jvm, const char *message );
int getId( JNIEnv *jvm, jobject self );
void setId( JNIEnv *jvm, jobject self, int id );
void *getPointer( JNIEnv *jvm, jobject self );
void setPointer( JNIEnv *jvm, jobject self, void *p );
jobject newVector3( JNIEnv *jvm, double x, double y, double z );
double getVectorX( JNIEnv *jvm, jobject vector );
double getVectorY( JNIEnv *jvm, jobject vector );
double getVectorZ( JNIEnv *jvm, jobject vector );

#endif /* GLOBAL_H_ */
