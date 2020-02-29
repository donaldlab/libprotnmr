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
