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
package edu.duke.cs.libprotnmr.clustering;

import java.util.LinkedList;

import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.math.MultiVectorImpl;

//import edu.duke.donaldLab.share.math.MultiAxisAlignedBox;

public class Cluster
{
	/**************************
	 *   Data Members
	 **************************/
	
	private LinkedList<MultiVector> m_points;
	private MultiVector m_center;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Cluster( MultiVector point )
	{
		m_points = new LinkedList<MultiVector>();
		m_points.add( point );
		
		m_center = new MultiVectorImpl( point );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public MultiVector getCenter( )
	{
		return m_center;
	}
	
	public LinkedList<MultiVector> getPoints( )
	{
		return m_points;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String toString( )
	{
		return m_center.toString();
	}
	
	public void join( Cluster other )
	{
		// get the dimension
		int dimension = m_points.get( 0 ).getDimension();
		
		// save the original number of points
		int oldNumPoints = m_points.size();
		
		MultiVector extraSum = new MultiVectorImpl( dimension );
		for( MultiVector point : other.m_points )
		{
			// update the list
			m_points.add( point );
			
			for( int d=0; d<dimension; d++ )
			{
				// update the sum
				extraSum.set( d, extraSum.get( d ) + point.get( d ) );
			}
		}
		
		// update the center
		for( int d=0; d<dimension; d++ )
		{
			m_center.set(
				d,
				( m_center.get( d ) * oldNumPoints + extraSum.get( d ) ) / m_points.size()
			);
		}
	}
	
	public MultiVector getRepresentativePoint( )
	{
		// ideally, we want to find the point closest to the center
		// this cluster shouldn't be absolutely huge, so a linear-time algorithm will be ok
		// although, I suppose we could use a kd tree here to speed things up and do NN in log(n)
		
		MultiVector closestPoint = null;
		double closestDistSq = Double.POSITIVE_INFINITY;
		
		for( MultiVector point : m_points )
		{
			double distSq = point.getDistanceSquared( m_center );
			if( distSq < closestDistSq )
			{
				closestDistSq = distSq;
				closestPoint = point;
			}
		}
		
		return closestPoint;
	}
}
