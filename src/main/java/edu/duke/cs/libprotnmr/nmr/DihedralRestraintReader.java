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


public class DihedralRestraintReader
{
	public ArrayList<DihedralRestraint<AtomAddressReadable>> read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public ArrayList<DihedralRestraint<AtomAddressReadable>> read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public ArrayList<DihedralRestraint<AtomAddressReadable>> read( InputStream in )
	throws IOException
	{
		// read the assigns
		ArrayList<Assign> assigns = new AssignReader().read( in );
		
		// convert to dihedral restraints
		ArrayList<DihedralRestraint<AtomAddressReadable>> restraints = new ArrayList<DihedralRestraint<AtomAddressReadable>>( assigns.size() );
		for( Assign assign : assigns )
		{
			// get the value and error
			double value;
			double error;
			if( assign.getNumbers().size() == 2 )
			{
				value = assign.getNumbers().get( 0 );
				error = assign.getNumbers().get( 1 );
			}
			else if( assign.getNumbers().size() == 4 )
			{
				value = assign.getNumbers().get( 1 );
				error = assign.getNumbers().get( 2 );
			}
			else
			{
				throw new Error( "Unable to understand numbers for dihedral restraints!" );
			}
			
			DihedralRestraint<AtomAddressReadable> restraint = new DihedralRestraint<AtomAddressReadable>(
				assign.getAddresses().get( 0 ).get( 0 ),
				assign.getAddresses().get( 1 ).get( 0 ),
				assign.getAddresses().get( 2 ).get( 0 ),
				assign.getAddresses().get( 3 ).get( 0 ),
				value,
				error
			);
			restraints.add( restraint );
		}
		
		return restraints;
	}
}
