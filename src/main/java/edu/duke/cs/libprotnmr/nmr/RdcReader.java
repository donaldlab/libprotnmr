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

package edu.duke.cs.libprotnmr.nmr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class RdcReader
{
	public ArrayList<Rdc<AtomAddressReadable>> read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public ArrayList<Rdc<AtomAddressReadable>> read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public ArrayList<Rdc<AtomAddressReadable>> read( InputStream in )
	throws IOException
	{
		// read the assigns
		ArrayList<Assign> assigns = new AssignReader().read( in );
		
		// convert to RDCs
		ArrayList<Rdc<AtomAddressReadable>> rdcs = new ArrayList<Rdc<AtomAddressReadable>>( assigns.size() );
		for( Assign assign : assigns )
		{
			Rdc<AtomAddressReadable> rdc = new Rdc<AtomAddressReadable>(
				assign.getAddresses().get( 4 ).get( 0 ),
				assign.getAddresses().get( 5 ).get( 0 )
			);
			rdc.setValue( assign.getNumbers().get( 0 ) );
			rdc.setError( assign.getNumbers().get( 1 ) );
			rdcs.add( rdc );
		}
		
		return rdcs;
	}
}
