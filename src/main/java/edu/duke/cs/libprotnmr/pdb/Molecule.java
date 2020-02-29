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

import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.protein.AminoAcid;


public class Molecule
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static class Transformation
	{
		public Matrix3 rotation;
		public Vector3 translation;
		
		public Transformation( )
		{
			rotation = new Matrix3();
			rotation.setIdentity();
			translation = new Vector3();
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private List<List<AminoAcid>> m_sequences;
	private int m_numSubunits;
	private Map<String,List<Transformation>> m_transformations;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Molecule( )
	{
		m_sequences = null;
		m_numSubunits = 0;
		m_transformations = null;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public List<List<AminoAcid>> getSequences( )
	{
		return m_sequences;
	}

	public void setSequences( List<List<AminoAcid>> sequences )
	{
		this.m_sequences = sequences;
	}

	public int getNumSubunits( )
	{
		return m_numSubunits;
	}

	public void setNumSubunits( int numSubunits )
	{
		this.m_numSubunits = numSubunits;
	}
	
	public boolean isHomoOligomer( )
	{
		return m_numSubunits > 1 && m_sequences.size() == 1;
	}
	
	public Map<String,List<Transformation>> getTransformations( )
	{
		return m_transformations;
	}
	
	public void setTransformations( Map<String,List<Transformation>> transformations )
	{
		this.m_transformations = transformations;
	}
	
	
	/**************************
	 *   Methods
	 **************************/

	@Override
	public String toString( )
	{
		return String.format( "[Molecule] %d sequences, %d subunits", m_sequences.size(), m_numSubunits );
	}
}
