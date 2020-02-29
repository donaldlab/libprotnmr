
#ifndef TYPES_H_
#define TYPES_H_


// getters
Circle_3 *getCircle( JNIEnv *, jobject );
Circular_arc_3 *getCircularArc( JNIEnv *, jobject );

// constructors
jobject newCircle( JNIEnv *, const Circle_3 & );
jobject newCircularArc( JNIEnv *, const Circular_arc_3 & );


#endif /* TYPES_H_ */
