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

package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChainBuilder
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	private static double Epsilon = 1e-10;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private List<Halfedge> m_chain;
	private Vertex m_lastVertex;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public ChainBuilder( )
	{
		m_chain = new ArrayList<Halfedge>();
		m_lastVertex = null;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getLastPoint( )
	{
		return m_lastVertex.getPoint();
	}
	
	public boolean isClosed( )
	{
		if( m_chain.isEmpty() )
		{
			return false;
		}
		
		Vertex first = m_chain.get( 0 ).getSource();
		Vertex last = m_chain.get( m_chain.size() - 1 ).getTarget();
		
		// yes, compare by reference
		return first == last;
	}
	
	public List<Halfedge> getChain( )
	{
		return m_chain;
	}
	
	public int getNumArcs( )
	{
		return m_chain.size();
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static List<Halfedge> getReverseChain( List<Halfedge> chain )
	{
		ChainBuilder builder = new ChainBuilder();
		Halfedge lastHalfedge = chain.get( chain.size() - 1 );
		builder.add( lastHalfedge.getEdge().getArc(), lastHalfedge.getSource().getPoint() );
		for( int i=chain.size()-2; i>=0; i-- )
		{
			builder.add( chain.get( i ).getEdge().getArc() );
		}
		return builder.getChain();
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public void add( CurveArc arc )
	{
		if( arc.isClosed() || !arc.hasLength() )
		{
			throw new IllegalArgumentException( "Cannot add a closed or zero length arc!" );
		}
		if( isClosed() )
		{
			throw new IllegalArgumentException( "Cannot add to a closed chain!" );
		}
		
		// how do we add the next arc?
		if( m_chain.isEmpty() )
		{
			// don't care about the halfedge order yet, just add the arc
			m_chain.add( Halfedge.buildFromArc(
				arc,
				new Vertex( arc.getSource() ),
				new Vertex( arc.getTarget() )
			) );
		}
		else
		{
			if( m_chain.size() == 1 )
			{
				// make sure this arc connects to the first arc
				if( !arc.containsPointOnBoundary( m_chain.get( 0 ).getSource().getPoint(), Epsilon )
					&& !arc.containsPointOnBoundary( m_chain.get( 0 ).getTarget().getPoint(), Epsilon ) )
				{
					// DEBUG
					if( true )
					{
						Kinemage kin = new Kinemage();
						KinemageBuilder.appendAxes( kin, 1, 0.2 );
						KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
						KinemageBuilder.appendBoundary( kin, m_chain, "Chain", KinemageColor.Orange, 1 );
						KinemageBuilder.appendCurve( kin, arc, "Arc", KinemageColor.Yellow, 2 );
						new KinemageWriter().show( kin );
					}
					
					throw new IllegalArgumentException( "Second arc does not connect to first arc in chain!" );
				}
				
				// was the first halfedge put on backwards?
				if( !arc.containsPointOnBoundary( m_chain.get( 0 ).getTarget().getPoint(), Epsilon ) )
				{
					// flip it
					CurveArc firstArc = m_chain.get( 0 ).getEdge().getArc();
					m_chain.set( 0, Halfedge.buildFromArc(
						firstArc,
						new Vertex( firstArc.getTarget() ),
						new Vertex( firstArc.getSource() )
					) );
				}
				assert( arc.containsPointOnBoundary( m_chain.get( 0 ).getTarget().getPoint(), Epsilon ) );
				
				m_lastVertex = m_chain.get( 0 ).getTarget();
			}
			else
			{
				// make sure this arc connects to the previous arc
				Halfedge previousHalfedge = m_chain.get( m_chain.size() - 1 );
				if( !arc.containsPointOnBoundary( previousHalfedge.getTarget().getPoint(), Epsilon ) )
				{
					throw new IllegalArgumentException( "Arc does not connect to the end of the chain!" );
				}
			}
			
			// add the next arc in order
			addArcToNonemptyChain( arc );
		}
	}
	
	public void add( CurveArc arc, Vector3 target )
	{
		if( arc.isClosed() || !arc.hasLength() )
		{
			throw new IllegalArgumentException( "Cannot add a closed or zero length arc!" );
		}
		if( isClosed() )
		{
			throw new IllegalArgumentException( "Cannot add to a closed chain!" );
		}
		
		// how do we add the next arc?
		if( m_chain.isEmpty() )
		{
			m_lastVertex = new Vertex( target );
			m_chain.add( Halfedge.buildFromArc(
				arc,
				new Vertex( arc.getOtherEndpoint( target ) ),
				m_lastVertex
			) );
		}
		else
		{
			// make sure this arc connects to the previous arc
			Halfedge previousHalfedge = m_chain.get( m_chain.size() - 1 );
			if( !arc.containsPointOnBoundary( previousHalfedge.getTarget().getPoint(), Epsilon ) )
			{
				throw new IllegalArgumentException( "Arc does not connect to the end of the chain!" );
			}
			
			// add the next arc in order
			addArcToNonemptyChain( arc, target );
		}
	}
	
	public void addAll( List<CurveArc> arcs )
	{
		for( CurveArc arc : arcs )
		{
			add( arc );
		}
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private void addArcToNonemptyChain( CurveArc arc )
	{
		assert( !m_chain.isEmpty() );
		
		// get the next point
		Halfedge previousHalfedge = m_chain.get( m_chain.size() - 1 );
		Vector3 nextPoint = arc.getOtherEndpoint( previousHalfedge.getTarget().getPoint(), Epsilon );
		
		addArcToNonemptyChain( arc, nextPoint );
	}
	
	private void addArcToNonemptyChain( CurveArc arc, Vector3 nextPoint )
	{
		assert( !m_chain.isEmpty() );
		
		// do we need to close the chain?
		Vertex nextVertex;
		if( nextPoint.approximatelyEquals( m_chain.get( 0 ).getSource().getPoint(), Epsilon ) )
		{
			nextVertex = m_chain.get( 0 ).getSource();
		}
		else
		{
			nextVertex = new Vertex( nextPoint );
		}
		
		addArcToNonemptyChain( arc, nextVertex );
	}
	
	private void addArcToNonemptyChain( CurveArc arc, Vertex nextVertex )
	{
		assert( !m_chain.isEmpty() );
		assert( m_lastVertex != null );
		
		m_chain.add( Halfedge.buildFromArc( arc, m_lastVertex, nextVertex ) );
		m_lastVertex = nextVertex;
	}
}
