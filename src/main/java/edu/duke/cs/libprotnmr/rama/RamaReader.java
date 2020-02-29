/*
 * This file is part of LibProtNMR
 *
 * Copyright (C) 2020 Bruce Donald Lab, Duke University
 *
 * LibProtNMR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibProtNMR.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Info:
 *    Bruce Donald
 *    Duke University
 *    Department of Computer Science
 *    Levine Science Research Center (LSRC)
 *    Durham
 *    NC 27708-0129
 *    USA
 *    e-mail: www.cs.duke.edu/brd/
 *
 * <signature of Bruce Donald>, February, 2020
 * Bruce Donald, Professor of Computer Science
 */

package edu.duke.cs.libprotnmr.rama;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class RamaReader
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static RamaMap read( File in )
	throws IOException
	{
		return read( new FileInputStream( in ) );
	}
	
	public static RamaMap read( InputStream in )
	throws IOException
	{
		RamaMap map = new RamaMap();
		String line = null;
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		while( ( line = reader.readLine() ) != null )
		{
			// skip comments
			if( line.startsWith( "#" ) )
			{
				continue;
			}
			
			// parse the sample point. it looks like this:
			// -179.0 -179.0 0.00782923406455425
			Scanner scanner = new Scanner( line );
			double phi = scanner.nextDouble();
			double psi = scanner.nextDouble();
			double value = scanner.nextDouble();
			
			map.setSample( phi, psi, value );
		}
		in.close();
		return map;
	}
	
	public static RamaMap readOptimized( File in )
	throws IOException
	{
		return readOptimized( new FileInputStream( in ) );
	}
	
	public static RamaMap readOptimized( InputStream in )
	throws IOException
	{
		DataInputStream din = new DataInputStream( in );
		double[][] samples = new double[RamaMap.NumSamplesPerAngle][RamaMap.NumSamplesPerAngle];
		for( int i=0; i<RamaMap.NumSamplesPerAngle; i++ )
		{
			for( int j=0; j<RamaMap.NumSamplesPerAngle; j++ )
			{
				samples[i][j] = din.readDouble();
			}
		}
		return new RamaMap( samples );
	}
}
