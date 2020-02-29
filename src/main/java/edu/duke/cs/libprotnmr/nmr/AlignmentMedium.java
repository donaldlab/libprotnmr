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

import java.io.Serializable;

public class AlignmentMedium implements Serializable
{
	private static final long serialVersionUID = -7433400066685491810L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private String m_name;
	private AlignmentTensor m_tensor;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AlignmentMedium( String name, AlignmentTensor tensor )
	{
		m_name = name;
		m_tensor = tensor;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public String getName( )
	{
		return m_name;
	}
	
	public AlignmentTensor getTensor( )
	{
		return m_tensor;
	}
	public void setTensor( AlignmentTensor val )
	{
		m_tensor = val;
	}
}
