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
package edu.duke.cs.libprotnmr.nmr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;


public class Rdc<T extends AtomAddress<T>> implements Serializable, AssignmentSource<T>, Iterable<Assignment<T>>, HasAddresses<T>
{
	private static final long serialVersionUID = -3778138836037565671L;
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	public static SamplingModel DefaultSamplingModel = SamplingModel.Gaussian;
	public static enum SamplingModel
	{
		Gaussian
		{
			@Override
			public double sample( double value, double error )
			{
				// 95% of the values will be in the interval
				return m_random.nextGaussian()*error + value;
			}
		},
		Uniform
		{
			@Override
			public double sample( double value, double error )
			{
				// 100% of the values will be in the interval
				return (m_random.nextDouble()*2.0 - 1 )*error + value;
			}
		};
		
		public abstract double sample( double value, double error );
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static Random m_random;
	
	private Set<T> m_froms;
	private Set<T> m_tos;
	private double m_value;
	private double m_error;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		m_random = new Random();
	}
	
	public Rdc( T from, T to )
	{
		m_froms = new HashSet<T>();
		m_froms.add( from );
		m_tos = new HashSet<T>();
		m_tos.add( to );
		m_value = Double.NaN;
		m_error = Double.NaN;
	}
	
	public Rdc( Rdc<T> other )
	{
		m_froms = new HashSet<T>();
		for( T otherFrom : other.m_froms )
		{
			m_froms.add( otherFrom.newCopy() );
		}
		m_tos = new HashSet<T>();
		for( T otherTo : other.m_tos )
		{
			m_tos.add( otherTo.newCopy() );
		}
		m_value = other.m_value;
		m_error = other.m_error;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public Set<T> getFroms( )
	{
		return m_froms;
	}
	public T getFrom( )
	{
		if( m_froms.size() > 1 )
		{
			throw new IllegalArgumentException( "RDC has more than one from address!" );
		}
		return m_froms.iterator().next();
	}
	public void setFrom( T val )
	{
		m_froms.clear();
		m_froms.add( val );
	}
	@Override
	public Collection<T> getLefts( )
	{
		return getFroms();
	}
	
	public Set<T> getTos( )
	{
		return m_tos;
	}
	public T getTo( )
	{
		if( m_tos.size() > 1 )
		{
			throw new IllegalArgumentException( "RDC has more than one from address!" );
		}
		return m_tos.iterator().next();
	}
	public void setTo( T val )
	{
		m_tos.clear();
		m_tos.add( val );
	}
	@Override
	public Collection<T> getRights( )
	{
		return getTos();
	}
	
	@Override
	public Iterable<T> addresses( )
	{
		List<T> addresses = new ArrayList<T>();
		addresses.addAll( m_froms );
		addresses.addAll( m_tos );
		return addresses;
	}
	
	@Override
	public Iterator<Assignment<T>> iterator( )
	{
		return new AssignmentIterator<T>( this );
	}
	
	public double getValue( )
	{
		return m_value;
	}
	public void setValue( double val )
	{
		m_value = val;
	}
	
	public double getError( )
	{
		return m_error;
	}
	public void setError( double val )
	{
		m_error = val;
	}

	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static <T extends AtomAddress<T>> ArrayList<Rdc<T>> copyDeep( List<Rdc<T>> inRdcs )
	{
		ArrayList<Rdc<T>> outRdcs = new ArrayList<Rdc<T>>( inRdcs.size() );
		for( Rdc<T> rdc : inRdcs )
		{
			outRdcs.add( new Rdc<T>( rdc ) );
		}
		return outRdcs;
	}
	
	public static <T extends AtomAddress<T>> void sample( List<Rdc<T>> outRdcs, List<Rdc<T>> inRdcs )
	{
		sample( outRdcs, inRdcs, DefaultSamplingModel );
	}
	
	public static <T extends AtomAddress<T>> void sample( List<Rdc<T>> outRdcs, List<Rdc<T>> inRdcs, SamplingModel model )
	{
		// just in case...
		assert( outRdcs.size() == inRdcs.size() );
		
		// perform the sampling
		for( int i=0; i<inRdcs.size(); i++ )
		{
			outRdcs.get( i ).setError( 0.0 );
			outRdcs.get( i ).setValue( inRdcs.get( i ).sample( model ) );
		}
	}
	
	public static List<Character> getSubunitNames( List<Rdc<AtomAddressReadable>> rdcs )
	{
		// collect all the subunit names and sort them
		TreeSet<Character> subunitNames = new TreeSet<Character>();
		for( Rdc<AtomAddressReadable> rdc : rdcs )
		{
			subunitNames.add( rdc.getFrom().getSubunitName() );
			subunitNames.add( rdc.getTo().getSubunitName() );
		}
		return new ArrayList<Character>( subunitNames );
	}
	
	public static <T extends AtomAddress<T>> void setValues( List<Rdc<T>> rdcs, List<Double> values )
	{
		assert( rdcs.size() == values.size() );
		for( int i=0; i<rdcs.size(); i++ )
		{
			rdcs.get( i ).setValue( values.get( i ) );
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public double sample( )
	{
		return sample( DefaultSamplingModel );
	}
	
	public double sample( SamplingModel model )
	{
		return model.sample( m_value, m_error );
	}
	
	@Override
	public String toString( )
	{
		return m_froms.toString() + "->" + m_tos.toString() + " - " + m_value + " +- " + m_error;
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
		if( !( other instanceof Rdc<?> ) )
		{
			return false;
		}
		
		// UNDONE: there has to be a way to check this cast
		return equals( (Rdc<T>)other );
	}
	
	public boolean equals( Rdc<T> other )
	{
		return
			m_value == other.m_value
			&& m_error == other.m_error
			&& m_froms.equals( other.m_froms )
			&& m_tos.equals( other.m_tos );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Double.valueOf( m_value ).hashCode(),
			Double.valueOf( m_error ).hashCode(),
			m_froms.hashCode(),
			m_tos.hashCode()
		);
	}
}
