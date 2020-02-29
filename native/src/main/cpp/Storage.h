
#ifndef STORAGE_H_
#define STORAGE_H_


template <class T>
class Storage
{
public:

	typedef std::map<int,T *,std::less<int> > Map;
	typedef typename std::map<int,T *,std::less<int> >::iterator MapIterator;

	// structors
	Storage( const char *className, bool deleteValue );
	~Storage( );

	// methods
	void add( JNIEnv *jvm, jobject obj, T *p );
	jobject addNew( JNIEnv *jvm, T *p );
	T *get( JNIEnv *jvm, jobject obj );
	T *get( int id );
	bool cleanup( int id );
	void cleanupAll( JNIEnv *jvm );

	// wrapper methods
	MapIterator begin( ) { return m_map.begin(); };
	MapIterator end( ) { return m_map.end(); };


protected:

	// data members
	Map m_map;
	const char *m_className;
	bool m_deleteValue;
	jclass m_class;
	jmethodID m_constructorId;

	// functions
	int insert( T *p );

};

template <class T>
Storage<T>::Storage( const char *className, bool deleteValue )
{
	m_className = className;
	m_deleteValue = deleteValue;
	m_class = NULL;
	m_constructorId = NULL;
}

template <class T>
Storage<T>::~Storage( )
{
	// nothing here anymore...
}

template <class T>
void Storage<T>::add( JNIEnv *jvm, jobject obj, T *p )
{
	int id = insert( p );

	setId( jvm, obj, id );
	setPointer( jvm, obj, p );
}

template <class T>
jobject Storage<T>::addNew( JNIEnv *jvm, T *p )
{
	// lookup meta info if needed
	if( m_class == NULL )
	{
		m_class = (jclass)jvm->NewGlobalRef( jvm->FindClass( m_className ) );
	}
	if( m_constructorId == NULL )
	{
		m_constructorId = jvm->GetMethodID( m_class, "<init>", "(IJ)V" );
		checkException( jvm );
	}

	int id = insert( p );

	jobject obj = jvm->NewObject( m_class, m_constructorId, id, p );
	checkException( jvm );
	return obj;
}

template <class T>
T *Storage<T>::get( JNIEnv *jvm, jobject obj )
{
	return (T *)getPointer( jvm, obj );
}

template <class T>
T *Storage<T>::get( int id )
{
	return m_map[id];
}

template <class T>
bool Storage<T>::cleanup( int id )
{
	MapIterator iter = m_map.find( id );
	
	// was the id found in the map?
	if( iter == m_map.end() )
	{
		return false;
	}
	
	if( m_deleteValue )
	{
		SAFE_DELETE( (*iter).second );
	}
	m_map.erase( iter );

	return true;
}

template <class T>
void Storage<T>::cleanupAll( JNIEnv *jvm )
{
	// cleanup constituent pointers
	if( m_deleteValue )
	{
		for( MapIterator iter = m_map.begin(); iter != m_map.end(); iter++ )
		{
			SAFE_DELETE( (*iter).second );
		}
	}
	m_map.clear();

	// cleanup cached java metainfo
	if( m_class != NULL )
	{
		jvm->DeleteGlobalRef( m_class );
		m_class = NULL;
	}
}

template <class T>
int Storage<T>::insert( T *p )
{
	// add the pointer to the map using a unique id
	int id;
	std::pair<MapIterator,bool> result;
	do
	{
		// get a random id
		id = rand();

		// Why, oh why is windows so terrible...
		if( RAND_MAX <= 0xffff )
		{
			id |= ( ( rand() & RAND_MAX ) << 15 );
		}
		
		// make sure the id is unique to this map
		result = m_map.insert( std::pair<int,T *>( id, p ) );
	}
	while( result.second == false );

	return id;
}


#endif /* STORAGE_H_ */
