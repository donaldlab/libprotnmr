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

public class IndexPair
{
	/**************************
	 *   Fields
	 **************************/
	
	public int left;
	public int right;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public IndexPair( int left, int right )
	{
		this.left = left;
		this.right = right;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public String toString( )
	{
		return Integer.toString( left ) + "," + Integer.toString( right );
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
		{
            return false;
		}
        if( other == this )
        {
            return true;
        }
        if( other.getClass() != getClass() )
        {
            return false;
        }
        
        return equals( (IndexPair)other );
	}
	
	public boolean equals( IndexPair other )
	{
		return left == other.left && right == other.right;
	}
	
	@Override
	public int hashCode( )
	{
		if( left < right )
		{
			return ( left * 37 ) ^ right;
		}
		else
		{
			return ( right * 37 ) ^ left;
		}
	}
}
