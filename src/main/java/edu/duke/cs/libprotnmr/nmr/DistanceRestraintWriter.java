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
