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
package edu.duke.cs.libprotnmr.mapping;

import java.util.List;

import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.ResidueType;
import edu.duke.cs.libprotnmr.protein.Sequences;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class NameMapper
{
	/**************************
	 *   Data Members
	 **************************/
	
	NameMapperProvider m_mapper;
	NameScheme m_source;
	NameScheme m_destination;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public NameMapper( NameScheme source, NameScheme destination )
	{
		m_mapper = NameMapperProvider.getInstance();
		m_source = source;
		m_destination = destination;
	}


	/**************************
	 *   Static Methods
	 **************************/
	
	public static void ensureProtein( Protein protein, NameScheme scheme )
	{
		NameMapper mapper = new NameMapper( NameScheme.getSchemeForProtein( protein ), scheme );
		if( mapper.hasMap() )
		{
			mapper.mapProtein( protein );
		}
	}
	
	public static void ensureProtein( Subunit subunit, NameScheme scheme )
	{
		ensureProtein( new Protein( subunit ), scheme );
	}
	
	public static void ensureAddresses( Sequences sequences, List<? extends HasAddresses<AtomAddressReadable>> addresses, NameScheme scheme )
	{
		NameMapper mapper = new NameMapper( NameScheme.getSchemeForAddresses( addresses ), scheme );
		if( mapper.hasMap() )
		{
			mapper.mapAddresses( sequences, addresses );
		}
	}
	

	/**************************
	 *   Methods
	 **************************/
	
	public boolean hasMap( )
	{
		return m_mapper.hasMap( m_source, m_destination );
	}

	public String mapName( AminoAcid aminoAcid, ResidueType residueType, String atomName )
	{
		return m_mapper.mapName( m_source, m_destination, aminoAcid, residueType, atomName );
	}
	
	public void mapAtom( AminoAcid aminoAcid, ResidueType residueType, Atom atom )
	{
		m_mapper.mapAtom( m_source, m_destination, aminoAcid, residueType, atom );
	}
	
	public void mapProtein( Protein protein )
	{
		m_mapper.mapProtein( m_source, m_destination, protein );
	}
	
	public void mapAddresses( Sequences sequences, List<? extends HasAddresses<AtomAddressReadable>> restraints )
	{
		m_mapper.mapAddresses( m_source, m_destination, sequences, restraints );
	}
}
