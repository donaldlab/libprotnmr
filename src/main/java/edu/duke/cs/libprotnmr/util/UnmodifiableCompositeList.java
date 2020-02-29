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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UnmodifiableCompositeList <T> implements List<T>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private List<List<T>> m_lists;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public UnmodifiableCompositeList( List<List<T>> lists )
	{
		m_lists = lists;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean add( T e )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add( int index, T element )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll( Collection<? extends T> c )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll( int index, Collection<? extends T> c )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear( )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean contains( Object o )
	{
		for( List<T> list : m_lists )
		{
			if( list.contains( o ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll( Collection<?> c )
	{
		// UNDONE: I'm too lazy to implement this (It's not as simple as contains() was)
		throw new UnsupportedOperationException();
	}
	
	@Override
	public T get( int index )
	{
		return m_lists.get( getListId( index ) ).get( getItemId( index ) );
	}
	
	@Override
	public int indexOf( Object o )
	{
		for( int i=0; i<m_lists.size(); i++ )
		{
			int id = m_lists.indexOf( o );
			if( id >= 0 )
			{
				return getGlobalId( i, id );
			}
		}
		return -1;
	}
	
	@Override
	public boolean isEmpty( )
	{
		for( List<T> list : m_lists )
		{
			if( !list.isEmpty() )
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Iterator<T> iterator( )
	{
		List<Iterator<T>> iterators = new ArrayList<Iterator<T>>();
		for( List<T> list : m_lists )
		{
			iterators.add( list.iterator() );
		}
		return new ChainedIterator<T>( iterators );
	}
	
	@Override
	public int lastIndexOf( Object o )
	{
		for( int i=m_lists.size()-1; i>=0; i-- )
		{
			int id = m_lists.get( i ).lastIndexOf( o );
			if( id >= 0 )
			{
				return getGlobalId( i, id );
			}
		}
		return -1;
	}
	
	@Override
	public ListIterator<T> listIterator( )
	{
		// UNDONE: I'm too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ListIterator<T> listIterator( int index )
	{
		// UNDONE: I'm too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove( Object o )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public T remove( int index )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll( Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll( Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public T set( int index, T element )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size( )
	{
		int size = 0;
		for( List<T> list : m_lists )
		{
			size += list.size();
		}
		return size;
	}
	
	@Override
	public List<T> subList( int fromIndex, int toIndex )
	{
		// UNDONE: I'm too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object[] toArray( )
	{
		// UNDONE: I'm too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <V> V[] toArray( V[] a )
	{
		// UNDONE: I'm too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private int getGlobalId( int listId, int itemId )
	{
		int global = 0;
		for( int i=0; i<listId; i++ )
		{
			global += m_lists.get( i ).size();
		}
		return global + itemId;
	}
	
	private int getListId( int global )
	{
		for( int i=0; i<m_lists.size(); i++ )
		{
			List<T> list = m_lists.get( i );
			if( global < list.size() )
			{
				return i;
			}
			global -= list.size();
		}
		throw new IndexOutOfBoundsException();
	}
	
	private int getItemId( int global )
	{
		for( int i=0; i<m_lists.size(); i++ )
		{
			List<T> list = m_lists.get( i );
			if( global < list.size() )
			{
				return global;
			}
			global -= list.size();
		}
		throw new IndexOutOfBoundsException();
	}
}
