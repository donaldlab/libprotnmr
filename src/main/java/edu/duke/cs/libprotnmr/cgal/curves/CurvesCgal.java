package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.cgal.Cgal;

import java.io.File;

public class CurvesCgal extends Cgal
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	private static CurvesCgal m_instance;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	static
	{
		m_instance = null;
	}
	
	private CurvesCgal( ) {}
	

	/*********************************
	 *   Static Methods
	 *********************************/
		
	public static CurvesCgal getInstance( )
	{
		if( m_instance == null )
		{
			m_instance = new CurvesCgal();
		}
		return m_instance;
	}
	
	
	/*********************************
	 *   Events
	 *********************************/
	
	@Override
	protected void cleanupNativeResources( )
	{
		nativeCleanup();
	}
	
	private static native void nativeCleanup( );
}
