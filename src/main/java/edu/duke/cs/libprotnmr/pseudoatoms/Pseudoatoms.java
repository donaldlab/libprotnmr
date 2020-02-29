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
package edu.duke.cs.libprotnmr.pseudoatoms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.duke.cs.libprotnmr.protein.AminoAcid;


public class Pseudoatoms
{
	/**************************
	 *   Definitions
	 **************************/
	
	private class Maps
	{
		public HashMap<String,ArrayList<String>> nameToAtoms;
		public HashMap<String,String> maskToName;
		public HashMap<String,String> nameToMask;
		public HashMap<String,Double> nameToCorrection;
		
		public Maps( )
		{
			nameToAtoms = new HashMap<String,ArrayList<String>>();
			maskToName = new HashMap<String,String>();
			nameToMask = new HashMap<String,String>();
			nameToCorrection = new HashMap<String,Double>();
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private HashMap<AminoAcid,Maps> m_maps;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Pseudoatoms( )
	{
		m_maps = new HashMap<AminoAcid,Maps>();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addNameToAtoms( AminoAcid aminoAcid, String pseudoatomName, ArrayList<String> atomNames )
	{
		pseudoatomName = pseudoatomName.toUpperCase();
		addMaps( aminoAcid ).nameToAtoms.put( pseudoatomName, atomNames );
	}
	
	public void addMaskToName( AminoAcid aminoAcid, String mask, String pseudoatomName )
	{
		mask = normalizeMask( mask );
		pseudoatomName = pseudoatomName.toUpperCase();
		addMaps( aminoAcid ).maskToName.put( mask, pseudoatomName );
	}
	
	public void addNameToMask( AminoAcid aminoAcid, String pseudoatomName, String mask )
	{
		mask = normalizeMask( mask );
		pseudoatomName = pseudoatomName.toUpperCase();
		addMaps( aminoAcid ).nameToMask.put( pseudoatomName, mask );
	}
	
	public void addCorrection( AminoAcid aminoAcid, String pseudoatomName, double correction )
	{
		pseudoatomName = pseudoatomName.toUpperCase();
		addMaps( aminoAcid ).nameToCorrection.put( pseudoatomName, correction );
	}
	
	public Set<String> getPseudoatomNames( AminoAcid aminoAcid )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return null;
		}
		
		return maps.nameToAtoms.keySet();
	}
	
	public boolean isPseudoatom( AminoAcid aminoAcid, String atomName )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return false;
		}
		return maps.nameToAtoms.get( atomName ) != null;
	}
	
	public ArrayList<String> getAtoms( AminoAcid aminoAcid, String pseudoatomName )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return null;
		}
		
		pseudoatomName = pseudoatomName.toUpperCase();
		return maps.nameToAtoms.get( pseudoatomName );
	}
	
	public String getPseudoatomName( AminoAcid aminoAcid, String mask )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return null;
		}
		
		return maps.maskToName.get( normalizeMask( mask ) );
	}
	
	public String getMask( AminoAcid aminoAcid, String pseudoatomName )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return null;
		}
		
		pseudoatomName = pseudoatomName.toUpperCase();
		return maps.nameToMask.get( pseudoatomName );
	}
	
	public double getCorrection( AminoAcid aminoAcid, String pseudoatomName )
	{
		Maps maps = getMaps( aminoAcid );
		if( maps == null )
		{
			return Double.NaN;
		}
		
		pseudoatomName = pseudoatomName.toUpperCase();
		Double f = maps.nameToCorrection.get( pseudoatomName );
		if( f == null )
		{
			return Double.NaN;
		}
		
		return f;
	}
	
	
	/**************************
	 *   Functions
	 **************************/

	private Maps addMaps( AminoAcid aminoAcid )
	{
		// does it already exist?
		if( m_maps.containsKey( aminoAcid ) )
		{
			return getMaps( aminoAcid );
		}
		
		// create the new maps
		Maps maps = new Maps();
		m_maps.put( aminoAcid, maps );
		
		return maps;
	}
	
	private Maps getMaps( AminoAcid aminoAcid )
	{
		return m_maps.get( aminoAcid );
	}
	
	private String normalizeMask( String mask )
	{
		return mask.toUpperCase().replace( "*", "#" );
	}
}
