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
package edu.duke.cs.libprotnmr.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

public class Transformer
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static <T> ArrayList<T> toArrayList( T ... in )
	{
		ArrayList<T> items = new ArrayList<T>();
		fill( items, in );
		return items;
	}
	
	public static <T> HashSet<T> toHashSet( T ... in )
	{
		HashSet<T> items = new HashSet<T>();
		fill( items, in );
		return items;
	}
	
	public static <T> TreeSet<T> toTreeSet( T ... in )
	{
		TreeSet<T> items = new TreeSet<T>();
		fill( items, in );
		return items;
	}
	
	private static <T> void fill( Collection<T> items, T ... in )
	{
		for( T item : in )
		{
			items.add( item );
		}
	}
}
