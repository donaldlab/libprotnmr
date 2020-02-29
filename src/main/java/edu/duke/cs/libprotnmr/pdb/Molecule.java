/*
 * This file is part of LibProtNMR
 *
 * Copyright (C) 2020 Bruce Donald Lab, Duke University
 *
 * LibProtNMR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibProtNMR.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Info:
 *    Bruce Donald
 *    Duke University
 *    Department of Computer Science
 *    Levine Science Research Center (LSRC)
 *    Durham
 *    NC 27708-0129
 *    USA
 *    e-mail: www.cs.duke.edu/brd/
 *
 * <signature of Bruce Donald>, February, 2020
 * Bruce Donald, Professor of Computer Science
 */

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
