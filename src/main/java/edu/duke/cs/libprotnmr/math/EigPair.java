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
