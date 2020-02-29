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
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class DistanceRestraintReader
{
	/**************************
	 *   Methods
	 **************************/
	
	public ArrayList<DistanceRestraint<AtomAddressReadable>> read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public ArrayList<DistanceRestraint<AtomAddressReadable>> read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public ArrayList<DistanceRestraint<AtomAddressReadable>> read( InputStream in )
	throws IOException
	{
		// use an assign reader and map the assigns to noes
		ArrayList<Assign> assigns = new AssignReader().read( in );
		
		ArrayList<DistanceRestraint<AtomAddressReadable>> restraints = new ArrayList<DistanceRestraint<AtomAddressReadable>>( assigns.size() );
		for( Assign assign : assigns )
		{
			DistanceRestraint<AtomAddressReadable> restraint = new DistanceRestraint<AtomAddressReadable>();
			restraint.setLefts( new TreeSet<AtomAddressReadable>( assign.getAddresses().get( 0 ) ) );
			restraint.setRights( new TreeSet<AtomAddressReadable>( assign.getAddresses().get( 1 ) ) );
			restraint.setMinDistance( assign.getNumbers().get( 0 ) - assign.getNumbers().get( 1 ) );
			restraint.setMaxDistance( assign.getNumbers().get( 0 ) + assign.getNumbers().get( 2 ) );
			restraints.add( restraint );
		}
		
		return restraints;
	}
}
