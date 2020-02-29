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

public interface Bijection<K,V>
{
	public static class Entry<K,V>
	{
		private K key;
		private V value;
		
		public Entry( K key, V value )
		{
			this.key = key;
			this.value = value;
		}
		
		public K getKey( )
		{
			return key;
		}
		
		public V getValue( )
		{
			return value;
		}
	}
	
	void clear();
	boolean containsKey( K key );
	boolean containsValue( V value );
	Iterable<Bijection.Entry<K,V>> entries( );
	V getValue( K key );
	K getKey( V value );
	boolean isEmpty( );
	int size( );
	Iterable<K> keys( );
	Iterable<V> values( );
	V put( K key, V value );
	void putAll( Bijection<? extends K,? extends V> bijection );
	V removeKey( K key );
	K removeValue( V value );
}
