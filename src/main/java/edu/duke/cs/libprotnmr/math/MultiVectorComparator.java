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

import java.util.Comparator;

public class MultiVectorComparator implements Comparator<MultiVector>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private int m_axis;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public MultiVectorComparator( )
	{
		m_axis = -1;
	}
	
	public MultiVectorComparator( int axis )
	{
		m_axis = axis;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public int compare( MultiVector left, MultiVector right )
	{
		// if axis is defined, sort one-dimensionally on that axis
		if( m_axis >= 0 )
		{
			return Double.compare( left.get( m_axis ), right.get( m_axis ) );
		}
		// otherwise, sort lexicographically
		else
		{
			for( int d=0; d<left.getDimension(); d++ )
			{
				int diff = Double.compare( left.get( d ), right.get( d ) );
				if( diff != 0 )
				{
					return diff; 
				}
			}
			
			return 0;
		}
	}
}
