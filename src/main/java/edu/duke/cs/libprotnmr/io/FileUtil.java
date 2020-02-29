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
package edu.duke.cs.libprotnmr.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil
{
	private static final Logger m_log = LogManager.getLogger(FileUtil.class);
	
	public static void copyFile( File sourceFile, File destFile )
	throws IOException
	{
		if( !destFile.exists() )
		{
			destFile.createNewFile();
		}
		
		FileChannel source = null;
		FileChannel destination = null;
		try
		{
			source = new FileInputStream( sourceFile ).getChannel();
			destination = new FileOutputStream( destFile ).getChannel();
			destination.transferFrom( source, 0, source.size() );
		}
		finally
		{
			if( source != null )
			{
				source.close();
			}
			if( destination != null )
			{
				destination.close();
			}
		}
	}
	
	public static int getNumBytesPerDouble( )
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( buf );
		try
		{
			out.writeDouble( 42.0 );
			out.close();
		}
		catch( IOException ex )
		{
			m_log.error( "Unable to measure number of bytes per double", ex );
			return 0;
		}
		return buf.toByteArray().length;
	}
}
