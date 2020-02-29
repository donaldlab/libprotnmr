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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class AssignWriter
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final DecimalFormat m_formatter;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_formatter = new DecimalFormat( "#.#####" );
		m_formatter.setRoundingMode( RoundingMode.HALF_UP );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String writeToString( List<Assign> assigns )
	{
		StringWriter out = new StringWriter();
		try
		{
			for( Assign assign : assigns )
			{
				writeAssign( out, assign );
			}
		}
		catch( IOException ex )
		{
			// this will never happen on a StringWriter
			throw new Error( ex );
		}
		return out.toString();
	}
	
	public String writeToString( Assign assign )
	{
		StringWriter out = new StringWriter();
		try
		{
			writeAssign( out, assign );
		}
		catch( IOException ex )
		{
			// this will never happen on a StringWriter
			throw new Error( ex );
		}
		return out.toString();
	}
	
	public void write( String path, List<Assign> assigns )
	throws IOException
	{
		write( new File( path ), assigns );
	}
	
	public void write( File file, List<Assign> assigns )
	throws IOException
	{
		Writer out = new BufferedWriter( new FileWriter( file ) );
		write( out, assigns );
		out.close();
	}
	
	public void write( Writer out, List<Assign> assigns )
	throws IOException
	{
		for( Assign assign : assigns )
		{
			writeAssign( out, assign );
			out.write( "\n" );
		}
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void writeAssign( Writer out, Assign assign )
	throws IOException
	{
		/* lines should look like this:
			assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 2.2 4.0
			assign (resid 7 and name ha1 and segid A)((resid 15 and name hd# and segid C)
				or (resid 15 and name hd# and segid B)) 4.0 2.2 4.0
		*/
		out.write( "assign " );
		
		for( List<AtomAddressReadable> group : assign.getAddresses() )
		{
			writeAddresses( out, group );
		}
		
		for( double d : assign.getNumbers() )
		{
			out.write( " " );
			out.write( m_formatter.format( d ) );
		}
	}
	
	private void writeAddresses( Writer out, Collection<AtomAddressReadable> addresses )
	throws IOException
	{
		boolean hasMultipleEndpoints = addresses.size() > 1;
		
		if( hasMultipleEndpoints )
		{
			out.write( "(" );
		}
		
		boolean isFirst = true;
		for( AtomAddressReadable address : addresses )
		{
			if( !isFirst )
			{
				out.write( " or " );
			}
			isFirst = false;
			
			// only write if the subunit if it hasn't been omitted
			if( address.hasSubunitName() )
			{
				out.write( String.format( "(resid %d and name %s and segid %c)",
					address.getResidueNumber(),
					address.getAtomName(),
					address.getSubunitName()
				) );
			}
			else
			{
				out.write( String.format( "(resid %d and name %s)",
					address.getResidueNumber(),
					address.getAtomName()
				) );
			}
		}
		
		if( hasMultipleEndpoints )
		{
			out.write( ")" );
		}
	}
}
