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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Element;


public class ChemicalShiftReader
{
	/**************************
	 *   Methods
	 **************************/
	
	public List<ChemicalShift<AtomAddressReadable>> read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public List<ChemicalShift<AtomAddressReadable>> read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public List<ChemicalShift<AtomAddressReadable>> read( InputStream in )
	throws IOException
	{
		ArrayList<ChemicalShift<AtomAddressReadable>> shifts = new ArrayList<ChemicalShift<AtomAddressReadable>>();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// trim comments
			line = line.replaceFirst( "#.*$", "" );
			
			// skip blank lines
			line = line.trim();
			if( line.length() <= 0 )
			{
				continue;
			}
			
			ChemicalShift<AtomAddressReadable> shift = parseShift( line );
			if( shift != null )
			{
				shifts.add( shift );
			}
		}
		
		return shifts;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private ChemicalShift<AtomAddressReadable> parseShift( String line )
	{
		boolean isError = false;
		
		// lines look like this:
		// 56  5 VAL CG2  C  21.4500 0.2  2
		
		StringTokenizer tokenizer = new StringTokenizer( line );
		ChemicalShift<AtomAddressReadable> shift = new ChemicalShift<AtomAddressReadable>();
		try
		{
			shift.setNumber( Integer.parseInt( tokenizer.nextToken() ) );
			AtomAddressReadable address = new AtomAddressReadable();
			address.omitSubunitName();
			address.setResidueNumber( Integer.parseInt( tokenizer.nextToken() ) );
			shift.setAminoAcid( AminoAcid.getByAbbreviation( tokenizer.nextToken() ) );
			address.setAtomName( tokenizer.nextToken() );
			shift.setAddress( address );
			shift.setElement( Element.getByCode( tokenizer.nextToken() ) );
			shift.setValue( Double.parseDouble( tokenizer.nextToken() ) );
			shift.setError( Double.parseDouble( tokenizer.nextToken() ) );
			shift.setAmbiguityCode( Integer.parseInt( tokenizer.nextToken() ) );
		}
		catch( NumberFormatException ex )
		{
			isError = true;
		}
		catch( NoSuchElementException ex )
		{
			isError = true;
		}
		
		if( isError )
		{
			throw new IllegalArgumentException( "Malformed chemical shift entry: " + line );
		}
		
		return shift;
	}

}
