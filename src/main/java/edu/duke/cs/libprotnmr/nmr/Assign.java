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

package edu.duke.cs.libprotnmr.nmr;

import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class Assign
{
	/**************************
	 *   Data Members
	 **************************/
	
	private List<List<AtomAddressReadable>> m_addresses;
	private List<Double> m_numbers;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Assign( )
	{
		m_addresses = new ArrayList<List<AtomAddressReadable>>();
		m_numbers = new ArrayList<Double>();
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public List<List<AtomAddressReadable>> getAddresses( )
	{
		return m_addresses;
	}
	public void setAddresses( List<List<AtomAddressReadable>> value )
	{
		m_addresses = value;
	}
	
	public List<Double> getNumbers( )
	{
		return m_numbers;
	}
	public void setNumbers( List<Double> numbers )
	{
		m_numbers = numbers;
	}
}
