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
package edu.duke.cs.libprotnmr.pdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class CnsPdbFixer extends ProteinWriter
{
	/**************************
	 *   Methods
	 **************************/
	
	public void fix( Protein protein, File brokenFile, File fixedFile )
	throws IOException
	{
		// open the broken file for reading
		BufferedReader reader = new BufferedReader( new FileReader( brokenFile ) );

		// open the fixed file for writing
		FileWriter writer =new FileWriter( fixedFile );
		
		boolean isFirstAtom = true;
		int nextAtomNumber = 1;
		int subunitId = 0;
		int lastSubunitId = 0;
		
		// for each line in the broken file
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// skip blank lines
			line = line.trim();
			if( line.equals( "" ) )
			{
				continue;
			}
			
			// get the type of the line
			String type = line.split( " " )[0];
			
			if( type.equals( "REMARK" ) )
			{
				// just pass the line through
				writer.write( line );
				writer.write( "\n" );
			}
			else if( type.equals( "ATOM" ) )
			{
				if( isFirstAtom )
				{
					// write the sequence information and extra stuff
					for( Subunit subunit : protein.getSubunits() )
					{
						writeDbref( writer, protein, subunit );
					}
					for( Subunit subunit : protein.getSubunits() )
					{
						writeSqres( writer, protein, subunit );
					}
					
					isFirstAtom = false;
				}
				
				// determine the subunit id from this record
				subunitId = line.substring( 72, 73 ).charAt( 0 ) - 'A';
				
				// add in the TER record if needed
				if( subunitId != lastSubunitId )
				{
					writer.write( "TER" );
					writer.write( String.format( "%8d", nextAtomNumber++ ) );
					writer.write( "\n" );
				}
				
				// fix the atom record
				writer.write( fixAtomRecord( line, nextAtomNumber++ ) );
				writer.write( "\n" );
				
				lastSubunitId = subunitId;
			}
		}
		
		// always add a TER/END
		writer.write( "TER" );
		writer.write( String.format( "%8d", nextAtomNumber++ ) );
		writer.write( "\n" );
		writer.write( "END\n" );
		
		// cleanup
		reader.close();
		reader = null;
		writer.close();
		writer = null;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private String fixAtomRecord( String line, int nextAtomNumber )
	{
		// change the atom number (positions 7-11)
		line = line.substring( 0, 6 ) + String.format( "%5d", nextAtomNumber ) + line.substring( 11 );
		
		// move the chain id to the right spot
		char chainId = line.substring( 72, 73 ).charAt( 0 );
		line = line.substring( 0, 21 ) + chainId + line.substring( 22 );
		
		// add the element symbol
		String atomName = line.substring( 12, 16 ).trim();
		String element = atomName.substring( 0, 1 );
		line = line.substring( 0, 66 ) + "           " + element;
		
		return line;
	}
}
