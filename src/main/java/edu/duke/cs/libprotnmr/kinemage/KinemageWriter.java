/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.kinemage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KinemageWriter
{
	private static final Logger m_log = LogManager.getLogger(KinemageWriter.class);
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void showAndWait( Kinemage kin )
	{
		show( kin, true );
	}
	
	public void show( Kinemage kin )
	{
		show( kin, false );
	}
	
	public void show( Kinemage kin, boolean wait )
	{
		show( Arrays.asList( kin ), wait );
	}
	
	public void show( List<Kinemage> kins )
	{
		show( kins, false );
	}
	
	public void showAndWait( List<Kinemage> kins )
	{
		show( kins, true );
	}
	
	public void show( List<Kinemage> kins, boolean wait )
	{
		try
		{
			// append the kinamges all into one temp file
			final File file = File.createTempFile( "KinemageWriter.", ".kin" );
			write( kins, file );
			file.deleteOnExit();
			
			// launch King in another process so it can wait for the file to be available
			Thread kingWatcher = new Thread( )
			{
				@Override
				public void run( )
				{
					try
					{
						Process process = Runtime.getRuntime().exec( "king " + file.getAbsolutePath() );
						
						// tie this thread to the king process so the VM stays alive long enough to cleanup temp files after king exits
						process.waitFor();
					}
					catch( Exception ex )
					{
						m_log.warn( ex );
					}
				}
			};
			kingWatcher.start();
			
			// wait for king if needed
			if( wait )
			{
				try
				{
					kingWatcher.join();
				}
				catch( InterruptedException ex )
				{
					m_log.warn( ex );
				}
			}
		}
		catch( IOException ex )
		{
			ex.printStackTrace( System.err );
		}
	}

	public void write( Kinemage kin, File file )
	throws IOException
	{
		write( Arrays.asList( kin ), file );
	}
	
	public void write( List<Kinemage> kins, File file )
	throws IOException
	{
		// open the file for writing
		BufferedWriter writer = new BufferedWriter( new FileWriter( file ) );
		
		// write out the document
		for( Kinemage kin : kins )
		{
			writeNode( writer, kin.getRoot() );
		}
		
		// cleanup
		writer.close();
		writer = null;
		
		m_log.info( "Wrote kinemage to:\n\t" + file.getAbsolutePath() );
	}
	
	private void writeNode( BufferedWriter writer, Node node )
	throws IOException
	{
		// write this node
		writer.write( node.getText() );
		writer.write( "\n" );
		
		// recurse
		for( Node child : node.getChildren() )
		{
			writeNode( writer, child );
		}
	}
}
