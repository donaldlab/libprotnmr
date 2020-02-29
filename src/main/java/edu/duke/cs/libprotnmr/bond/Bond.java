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
package edu.duke.cs.libprotnmr.bond;

import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;

public class Bond implements Comparable<Bond>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_leftName;
	private String m_rightName;
	private BondStrength m_strength;
	private boolean m_bondsToNextResidue;
	private Protein m_protein;
	private AtomAddressInternal m_leftAddress;
	private AtomAddressInternal m_rightAddress;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Bond( String leftName, String rightName, BondStrength strength )
	{
		this( leftName, rightName, strength, false );
	}
	
	public Bond( String leftName, String rightName, BondStrength strength, boolean bondsToNextResidue )
	{
		m_leftName = leftName;
		m_rightName = rightName;
		m_strength = strength;
		m_bondsToNextResidue = bondsToNextResidue;
		m_protein = null;
		m_leftAddress = null;
		m_rightAddress = null;
	}
	
	public Bond( Protein protein, AtomAddressInternal leftAddress, AtomAddressInternal rightAddress, BondStrength strength )
	{
		m_leftName = protein.getAtom( leftAddress ).getName();
		m_rightName = protein.getAtom( rightAddress ).getName();
		m_strength = strength;
		m_protein = protein;
		m_leftAddress = leftAddress;
		m_rightAddress = rightAddress;
	}
	
	public Bond( Bond other )
	{
		m_leftName = other.m_leftName;
		m_rightName = other.m_rightName;
		m_strength = other.m_strength;
		m_protein = null;
		m_leftAddress = null;
		m_rightAddress = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getLeftName( )
	{
		return m_leftName;
	}
	public void setLeftName( String value )
	{
		m_leftName = value;
	}
	
	public String getRightName( )
	{
		return m_rightName;
	}
	public void setRightName( String value )
	{
		m_rightName = value;
	}
	
	public BondStrength getStrength( )
	{
		return m_strength;
	}
	
	public boolean bondsToNextResidue( )
	{
		return m_bondsToNextResidue;
	}
	
	public AtomAddressInternal getLeftAddress( )
	{
		return m_leftAddress;
	}
	public void setLeftAddress( AtomAddressInternal value )
	{
		m_leftAddress = value;
	}
	
	public Protein getProtein( )
	{
		return m_protein;
	}
	public void setProtein( Protein value )
	{
		m_protein = value;
	}
	
	public AtomAddressInternal getRightAddress( )
	{
		return m_rightAddress;
	}
	public void setRightAddress( AtomAddressInternal value )
	{
		m_rightAddress = value;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		if( m_leftAddress != null && m_rightAddress != null )
		{
			return m_leftAddress + " " + m_rightAddress;
		}
		
		else return m_leftName + " " + m_rightName;
	}
	
	public String getOtherName( String atom )
	{
		if( atom.equals( m_leftName ) )
		{
			return m_rightName;
		}
		else
		{
			return m_leftName;
		}
	}
	
	public AtomAddressInternal getOtherAddress( AtomAddressInternal address )
	{
		if( address.equals( m_leftAddress ) )
		{
			return m_rightAddress;
		}
		else
		{
			return m_leftAddress;
		}
	}
	
	public double getSquaredLength( )
	{
		Atom leftAtom = m_protein.getAtom( m_leftAddress );
		Atom rightAtom = m_protein.getAtom( m_rightAddress );
		return leftAtom.getPosition().getSquaredDistance( rightAtom.getPosition() );
	}
	
	public double getLength( )
	{
		Atom leftAtom = m_protein.getAtom( m_leftAddress );
		Atom rightAtom = m_protein.getAtom( m_rightAddress );
		return leftAtom.getPosition().getDistance( rightAtom.getPosition() );
	}
	
	public int compareTo( Bond other )
	{
		int diff = 0;
		
		diff = m_leftAddress.compareTo( other.m_leftAddress );
		if( diff != 0 )
		{
			return diff;
		}
		
		diff = m_rightAddress.compareTo( other.m_rightAddress );
		if( diff != 0 )
		{
			return diff;
		}
		
		return 0;
	}
	
	public int hashCode( )
	{
		int leftHash = m_leftAddress.hashCode();
		int rightHash = m_rightAddress.hashCode();
		
		/* Jeff: 12/01/2008 - NOTE:
			We want an atom address pair to hash to the same value regardless
			of in which order the atoms appear. So, sort the left and right
			hashes before combining them.
		*/
		
		int smallestHash = 0;
		int largestHash = 0;
		if( leftHash < rightHash )
		{
			smallestHash = leftHash;
			largestHash = rightHash;
		}
		else
		{
			smallestHash = rightHash;
			largestHash = leftHash;
		}
		
		// now, just do a simple two-integer hash
		return smallestHash * 31 + largestHash;
	}
}
