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
