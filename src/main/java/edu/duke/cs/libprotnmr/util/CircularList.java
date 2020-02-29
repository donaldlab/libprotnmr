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

package edu.duke.cs.libprotnmr.util;

import java.util.List;

public class CircularList
{
	public static <T> T get( List<T> list, int i )
	{
		return list.get( i );
	}
	
	public static <T> T getRelative( List<T> list, int i, int offset )
	{
		i += offset;
		if( i >= 0 )
		{
			return list.get( i % list.size() );
		}
		else
		{
			// NOTE: the % operator in Java in technically 'remainder' and not 'modulus'
			// to get it to work like mod for negative numbers, we have to do some extra work
			int realMod = ( i % list.size() + list.size() ) % list.size();
			return list.get( realMod ); 
		}
	}
	
	public static <T> T getNext( List<T> list, int i )
	{
		return getRelative( list, i, 1 );
	}
	
	public static <T> T getPrevious( List<T> list, int i )
	{
		return getRelative( list, i, -1 );
	}
}
