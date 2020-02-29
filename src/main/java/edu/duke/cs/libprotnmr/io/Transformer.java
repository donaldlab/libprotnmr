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
