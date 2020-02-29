package edu.duke.cs.libprotnmr.resources;

public class Resources
{
	private static String m_resourcePath;

	static
	{
		// get the absolute path to the resources package independent of its actual package
		StringBuilder buf = new StringBuilder();
		buf.append( "/" );
		String[] packageComponents = Resources.class.getName().split( "\\." );
		for( int i=0; i<packageComponents.length-2; i++ )
		{
			buf.append( packageComponents[i] );
			buf.append( "/" );
		}
		m_resourcePath = buf.toString();
	}

	public static String getPath( String path )
	{
		// to my infinite sadness, parent directory references don't work in a jar classLoader!! ;_;
		// so we have to build absolute paths for all our resources
		return m_resourcePath + path;
	}
}
