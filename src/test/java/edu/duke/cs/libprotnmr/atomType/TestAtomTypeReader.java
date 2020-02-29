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
package edu.duke.cs.libprotnmr.atomType;

import java.util.TreeMap;

import edu.duke.cs.libprotnmr.ExtendedTestCase;
import edu.duke.cs.libprotnmr.resources.Resources;


public class TestAtomTypeReader extends ExtendedTestCase
{
	public void testAtomTypeReader( )
	throws Exception
	{
		AtomTypeReader reader = new AtomTypeReader();
		TreeMap<String,TreeMap<String,AtomType>> map = reader.read( getClass().getResourceAsStream( Resources.getPath("backbone.atomTypes") ) );
		
		// not-so-random sampling of the read-in values
		assertEquals( AtomType.Nam, map.get( "CTERMINUS" ).get( "N" ) );
		assertEquals( AtomType.Oco2, map.get( "CTERMINUS" ).get( "O" ) );
		assertEquals( AtomType.Oco2, map.get( "CTERMINUS" ).get( "OXT" ) );
		assertEquals( AtomType.H, map.get( "CTERMINUS" ).get( "H" ) );
		assertEquals( AtomType.C3, map.get( "NONTERMINAL" ).get( "CA" ) );
		assertEquals( AtomType.O2, map.get( "NTERMINUS" ).get( "O" ) );
		assertEquals( AtomType.O2, map.get( "NTERMINUS" ).get( "O" ) );
	}
}
