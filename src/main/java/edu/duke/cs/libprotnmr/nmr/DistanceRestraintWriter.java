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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class DistanceRestraintWriter
{
	/**************************
	 *   Methods
	 **************************/
	
	public String writeToString( DistanceRestraint<AtomAddressReadable> restraint )
	{
		return new AssignWriter().writeToString( toAssign( restraint ) );
	}
	
	public String writeToString( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		return new AssignWriter().writeToString( toAssign( restraints ) );
	}
	
	public void write( String path, List<DistanceRestraint<AtomAddressReadable>> restraints )
	throws IOException
	{
		write( new File( path ), restraints );
	}
	
	public void write( File file, List<DistanceRestraint<AtomAddressReadable>> restraints )
	throws IOException
	{
		FileWriter out = new FileWriter( file );
		write( out, restraints );
		out.close();
	}
	
	public void write( Writer out, List<DistanceRestraint<AtomAddressReadable>> restraints )
	throws IOException
	{
		new AssignWriter().write( out, toAssign( restraints ) );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private List<Assign> toAssign( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		List<Assign> assigns = new ArrayList<Assign>( restraints.size() );
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			assigns.add( toAssign( restraint ) );
		}
		return assigns;
	}
	
	private Assign toAssign( DistanceRestraint<AtomAddressReadable> restraint )
	{
		Assign assign = new Assign();
		assign.getAddresses().add( new ArrayList<AtomAddressReadable>( restraint.getLefts() ) );
		assign.getAddresses().add( new ArrayList<AtomAddressReadable>( restraint.getRights() ) );
		assign.getNumbers().add( restraint.getMinDistance() );
		assign.getNumbers().add( 0.0 );
		assign.getNumbers().add( restraint.getMaxDistance() - restraint.getMinDistance() );
		return assign;
	}
}
