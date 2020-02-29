package edu.duke.cs.libprotnmr.io;

import org.junit.Test;

public class TestLogging
{
	@Test
	public void testInit( )
	{
		for( Logging logging : Logging.values() )
		{
			logging.init();
		}
		
		// put it back to normal logging for unit tests
		Logging.Normal.init();
	}
}
