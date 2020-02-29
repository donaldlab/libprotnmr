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

import java.util.HashMap;

public class HashBijection<K,V> implements Bijection<K,V>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private HashMap<K,Bijection.Entry<K,V>> m_keyMap;
	private HashMap<V,Bijection.Entry<K,V>> m_valueMap;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public HashBijection( )
	{
		m_keyMap = new HashMap<K,Bijection.Entry<K,V>>();
		m_valueMap = new HashMap<V,Bijection.Entry<K,V>>();
	}
	
	// UNDONE: add capacity/load constructors
	// UNDONE: add copy constructor
	
	
	/**************************
	 *   Methods
	 **************************/
	

	@Override
	public void clear( )
	{
		m_keyMap.clear();
		m_valueMap.clear();
	}

	@Override
	public boolean containsKey( K key )
	{
		return m_keyMap.containsKey( key );
	}

	@Override
	public boolean containsValue( V value )
	{
		return m_valueMap.containsKey( value );
	}
	
	@Override
	public Iterable<Bijection.Entry<K,V>> entries( )
	{
		return m_keyMap.values();
	}
	
	@Override
	public V getValue( K key )
	{
		Entry<K,V> entry = m_keyMap.get( key );
		if( entry == null )
		{
			return null;
		}
		return entry.getValue();
	}

	@Override
	public K getKey( V value )
	{
		Entry<K,V> entry = m_valueMap.get( value );
		if( entry == null )
		{
			return null;
		}
		return entry.getKey();
	}

	@Override
	public boolean isEmpty( )
	{
		return m_keyMap.isEmpty();
	}

	@Override
	public int size( )
	{
		return m_keyMap.size();
	}

	@Override
	public Iterable<K> keys( )
	{
		return m_keyMap.keySet();
	}

	@Override
	public Iterable<V> values( )
	{
		return m_valueMap.keySet();
	}
	
	@Override
	public V put( K key, V value )
	{
		Bijection.Entry<K,V> entry = new Bijection.Entry<K,V>( key, value );
		m_keyMap.put( key, entry );
		m_valueMap.put( value, entry );
		return value;
	}

	@Override
	public void putAll( Bijection<? extends K, ? extends V> bijection )
	{
		for( Bijection.Entry<? extends K,? extends V> entry : bijection.entries() )
		{
			put( entry.getKey(), entry.getValue() );
		}
	}

	@Override
	public V removeKey( K key )
	{
		V value = getValue( key );
		m_keyMap.remove( key );
		m_valueMap.remove( value );
		return value;
	}

	@Override
	public K removeValue( V value )
	{
		K key = getKey( value );
		m_keyMap.remove( key );
		m_valueMap.remove( value );
		return key;
	}
}
