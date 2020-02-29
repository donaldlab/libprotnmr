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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class RdcWriter
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public void write( File file, List<Rdc<AtomAddressReadable>> rdcs )
	throws IOException
	{
		new AssignWriter().write( file, toAssign( rdcs ) );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private List<Assign> toAssign( List<Rdc<AtomAddressReadable>> rdcs )
	{
		List<Assign> assigns = new ArrayList<Assign>( rdcs.size() );
		for( Rdc<AtomAddressReadable> rdc : rdcs )
		{
			assigns.add( toAssign( rdc ) );
		}
		return assigns;
	}
	
	private Assign toAssign( Rdc<AtomAddressReadable> rdc )
	{
		Assign assign = new Assign();
		assign.getAddresses().add( Arrays.asList( rdc.getFrom() ) );
		assign.getAddresses().add( Arrays.asList( rdc.getTo() ) );
		assign.getNumbers().add( rdc.getValue() );
		assign.getNumbers().add( rdc.getError() );
		return assign;
	}
}