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
