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
package edu.duke.cs.libprotnmr.dataStructures;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FuzzyMap<K extends Fuzzy<K>,V> extends AbstractMap<K,V>
{
	/**************************
	 *   Definitions
	 **************************/
	
	private class Entry implements Map.Entry<K,V>
	{
		private K m_key;
		private V m_value;
		
		public Entry( K key, V value )
		{
			m_key = key;
			m_value = value;
		}
		
		@Override
		public K getKey( )
		{
			return m_key;
		}
		
		@Override
		public V getValue( )
		{
			return m_value;
		}
		
		@Override
		public V setValue( V val )
		{
			m_value = val;
			return m_value;
		}
		
		@Override
		public int hashCode( )
		{
			// yes, we want to disable hash code comparisons
			// yes, I know this is slow
			// it essentially turns the hash table into a linked list
			// we need to call equals() on all entries for fuzzy comparisons to work properly
			// unless I wanted to implement some kind of range query thingy...
			// which I don't think is worth the effort right now
			return 1;
		}
		
		@Override
		@SuppressWarnings( "unchecked" )
		public boolean equals( Object other )
		{
			if( other instanceof FuzzyMap.Entry )
			{
				return equals( (Entry)other );
			}
			if( other instanceof Fuzzy )
			{
				return equals( (K)other );
			}
			return false;
		}
		
		public boolean equals( Entry other )
		{
			if( m_epsilon != null )
			{
				return m_key.approximatelyEquals( other.m_key, m_epsilon );
			}
			else
			{
				return m_key.approximatelyEquals( other.m_key );
			}
		}
		
		public boolean equals( K other )
		{
			if( m_epsilon != null )
			{
				return m_key.approximatelyEquals( (K)other, m_epsilon );
			}
			else
			{
				return m_key.approximatelyEquals( (K)other );
			}
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private HashSet<Map.Entry<K,V>> m_entries;
	private Double m_epsilon;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public FuzzyMap( )
	{
		this( null );
	}
	
	public FuzzyMap( Double epsilon )
	{
		m_entries = new HashSet<Map.Entry<K,V>>();
		m_epsilon = epsilon;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public Set<Map.Entry<K,V>> entrySet( )
	{
		return m_entries;
	}
	
	@Override
	public V put( K key, V value )
	{
		Entry entry = new Entry( key, value );
		if( m_entries.contains( entry ) )
		{
			m_entries.remove( entry );
		}
		m_entries.add( entry );
		return value;
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public V get( Object obj )
	{
		if( obj instanceof Fuzzy )
		{
			return get( (K)obj );
		}
		return null;
	}
	
	public V get( K key )
	{
		for( Map.Entry<K,V> entry : m_entries )
		{
			if( entry.equals( key ) )
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public boolean containsKey( Object obj )
	{
		if( obj instanceof Fuzzy )
		{
			return containsKey( (K)obj );
		}
		return false;
	}
	
	public boolean containsKey( K key )
	{
		for( Map.Entry<K,V> entry : m_entries )
		{
			if( entry.equals( key ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public V remove( Object obj )
	{
		if( obj instanceof Fuzzy )
		{
			return remove( (K)obj );
		}
		return null;
	}
	
	public V remove( K key )
	{
		Iterator<Map.Entry<K,V>> iter = m_entries.iterator();
		while( iter.hasNext() )
		{
			Map.Entry<K,V> entry = iter.next();
			if( entry.equals( key ) )
			{
				iter.remove();
				return entry.getValue();
			}
		}
		return null;
	}
}
