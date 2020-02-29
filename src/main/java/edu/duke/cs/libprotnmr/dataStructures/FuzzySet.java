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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class FuzzySet<T extends Fuzzy<T>> implements Set<T>
{
	/**************************
	 *   Data Members
	 **************************/
	
	private ArrayList<T> m_objects;
	private Double m_epsilon;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public FuzzySet( )
	{
		this( null );
	}
	
	public FuzzySet( Double epsilon )
	{
		m_epsilon = epsilon;
		m_objects = new ArrayList<T>();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public boolean add( T obj )
	{
		if( !contains( obj ) )
		{
			return m_objects.add( obj );
		}
		return false;
	}
	
	@Override
	public boolean addAll( Collection<? extends T> objects )
	{
		boolean result = true;
		for( T obj : objects )
		{
			result &= add( obj );
		}
		return result;
	}
	
	@Override
	public void clear( )
	{
		m_objects.clear();
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public boolean contains( Object obj )
	{
		if( obj instanceof Fuzzy )
		{
			return contains( (Fuzzy<T>)obj );
		}
		return false;
	}
	
	public boolean contains( Fuzzy<T> obj )
	{
		return find( obj ) != null;
	}
	
	@Override
	public boolean containsAll( Collection<?> objects )
	{
		for( Object obj : objects )
		{
			if( !contains( obj ) )
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isEmpty( )
	{
		return m_objects.isEmpty();
	}
	
	@Override
	public Iterator<T> iterator( )
	{
		return m_objects.iterator();
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public boolean remove( Object obj )
	{
		if( obj instanceof Fuzzy )
		{
			return remove( (Fuzzy<T>)obj );
		}
		return false;
	}
	
	public boolean remove( Fuzzy<T> obj )
	{
		T target = find( obj );
		if( target != null )
		{
			return m_objects.remove( target );
		}
		return false;
	}
	
	@Override
	public boolean removeAll( Collection<?> objects )
	{
		boolean allRemoved = true;
		for( Object obj : objects )
		{
			allRemoved &= remove( obj );
		}
		return allRemoved;
	}
	
	@Override
	public boolean retainAll( Collection<?> objects )
	{
		// too lazy to implement this
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size( )
	{
		return m_objects.size();
	}
	
	@Override
	public Object[] toArray( )
	{
		return m_objects.toArray();
	}
	
	@Override
	public <U> U[] toArray( U[] array )
	{
		return m_objects.toArray( array );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private T find( Fuzzy<T> target )
	{
		for( T obj : m_objects )
		{
			boolean found = m_epsilon != null ? target.approximatelyEquals( obj, m_epsilon ) : target.approximatelyEquals( obj );
			if( found )
			{
				return obj;
			}
		}
		return null;
	}
}
