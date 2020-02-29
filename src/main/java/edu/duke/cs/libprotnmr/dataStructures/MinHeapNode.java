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

package edu.duke.cs.libprotnmr.dataStructures;

public class MinHeapNode<E>
{
	/**************************
	 *   Data Memebers
	 **************************/
	
	private E m_data;
	private int m_index;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public MinHeapNode( E data )
	{
		// save parameters
		m_data = data;
		
		// init defaults
		m_index = -1;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public E getData( )
	{
		return m_data;
	}
	
	public int getIndex( )
	{
		return m_index;
	}
	public void setIndex( int val )
	{
		m_index = val;
	}
}
