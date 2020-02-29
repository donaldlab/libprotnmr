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
package edu.duke.cs.libprotnmr.math;

import java.io.Serializable;

import edu.duke.cs.libprotnmr.geom.Vector3;

import Jama.EigenvalueDecomposition;

public class EigPair implements Comparable<EigPair>, Serializable
{
	private static final long serialVersionUID = 6833929630511562983L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_value;
	private Vector3 m_vector;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public EigPair( EigenvalueDecomposition eig, int index )
	{
		m_value = eig.getD().get( index, index );
		m_vector = new Vector3(
			eig.getV().get( 0, index ),
			eig.getV().get( 1, index ),
			eig.getV().get( 2, index )
		);
		m_vector.normalize();
	}
	
	public EigPair( double value, Vector3 vector )
	{
		m_value = value;
		m_vector = vector;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getEigenvalue( )
	{
		return m_value;
	}
	
	public Vector3 getEigenvector( )
	{
		return m_vector;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public int compareTo( EigPair other )
	{
		return Double.compare( Math.abs( m_value ), Math.abs( other.m_value ) );
	}
	
	@Override
	public String toString( )
	{
		return "" + m_value + ", " + m_vector.toString();  
	}
}
