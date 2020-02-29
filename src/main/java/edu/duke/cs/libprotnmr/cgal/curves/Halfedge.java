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
import edu.duke.cs.libprotnmr.io.HashCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Halfedge
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	public static enum Direction
	{
		Forward
		{
			@Override
			public Vertex getSource( Edge edge )
			{
				return edge.getSource();
			}
			
			@Override
			public Vertex getTarget( Edge edge )
			{
				return edge.getTarget();
			}
		},
		Reverse
		{
			@Override
			public Vertex getSource( Edge edge )
			{
				return edge.getTarget();
			}
			
			@Override
			public Vertex getTarget( Edge edge )
			{
				return edge.getSource();
			}
		};

		public abstract Vertex getSource( Edge edge );
		public abstract Vertex getTarget( Edge edge );
	}
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Edge m_edge;
	private Direction m_direction;
	private Halfedge m_twin;
	private Face m_face;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public Halfedge( Edge edge, Direction direction )
	{
		m_edge = edge;
		m_direction = direction;
		m_twin = null;
		m_face = null;
		
		// just in case...
		assert( m_edge.getArc().containsPoint( getSource().getPoint() ) );
		assert( m_edge.getArc().containsPoint( getTarget().getPoint() ) );
	}
	
	public Halfedge( Edge edge, Vertex source )
	{
		m_edge = edge;
		
		// find the direction
		if( source == edge.getSource() )
		{
			m_direction = Direction.Forward;
		}
		else if( source == edge.getTarget() )
		{
			m_direction = Direction.Reverse;
		}
		else
		{
			assert( false );
		}
		
		m_twin = null;
		m_face = null;
	}
	
	public static Halfedge buildFromArc( CurveArc arc, Vertex source, Vertex target )
	{
		Edge edge = new Edge( arc, source, target );
		source.addEdge( edge );
		target.addEdge( edge );
		return new Halfedge( edge, Halfedge.Direction.Forward );
	}


	/*********************************
	 *   Accessors
	 *********************************/

	public Halfedge getTwin( )
	{
		return m_twin;
	}
	protected void setTwin( Halfedge val )
	{
		m_twin = val;
	}

	public Face getFace( )
	{
		return m_face;
	}
	protected void setFace( Face val )
	{
		m_face = val;
	}

	public Edge getEdge( )
	{
		return m_edge;
	}

	public Direction getDirection( )
	{
		return m_direction;
	}

	public Vertex getSource( )
	{
		return m_direction.getSource( m_edge );
	}

	public Vertex getTarget( )
	{
		return m_direction.getTarget( m_edge );
	}


	/*********************************
	 *   Methods
	 *********************************/

	public List<Vector3> samplePoints( )
	{
		List<Vector3> points = m_edge.getArc().samplePoints();

		// the edge won't necessarily be sampled in the right order, so fix the order if needed
		if( points.get( 0 ).approximatelyEquals( getTarget().getPoint(), 1e-12 ) )
		{
			Collections.reverse( points );
		}
		assert( points.get( 0 ).approximatelyEquals( getSource().getPoint() ) );
		assert( points.get( points.size() - 1 ).approximatelyEquals( getTarget().getPoint() ) );

		return points;
	}

	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_edge.hashCode(),
			m_direction.ordinal() + 1
		);
	}

	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Halfedge )
		{
			return equals( (Halfedge)other );
		}
		return false;
	}

	public boolean equals( Halfedge other )
	{
		return m_edge.equals( other.m_edge ) && m_direction == other.m_direction;
	}

	public List<Halfedge> split( Vertex v )
	{
		// split the arc
		List<? extends CurveArc> arcs = m_edge.getArc().split( v.getPoint() );
		assert( arcs.size() == 2 );

		// find out which arc goes with the sub-halfedge pointed into p
		CurveArc inArc = null;
		CurveArc outArc = null;
		if( arcs.get( 0 ).containsPoint( getSource().getPoint() ) )
		{
			inArc = arcs.get( 0 );
			outArc = arcs.get( 1 );
		}
		else
		{
			inArc = arcs.get( 1 );
			outArc = arcs.get( 0 );
		}

		// do some checking
		assert( m_edge.getArc().containsPoint( getSource().getPoint() ) );
		assert( m_edge.getArc().containsPoint( v.getPoint() ) );
		assert( m_edge.getArc().containsPoint( getTarget().getPoint() ) );
		assert( inArc.hasLength() );
		assert( inArc.containsPoint( getSource().getPoint() ) );
		assert( inArc.containsPoint( v.getPoint() ) );
		assert( !inArc.containsPoint( getTarget().getPoint() ) );
		assert( outArc.hasLength() );
		assert( !outArc.containsPoint( getSource().getPoint() ) );
		assert( outArc.containsPoint( v.getPoint() ) );
		assert( outArc.containsPoint( getTarget().getPoint() ) );

		// build the sub-halfedges
		Halfedge inHalfedge = new Halfedge( new Edge( inArc, getSource(), v ), Halfedge.Direction.Forward );
		Halfedge outHalfedge = new Halfedge( new Edge( outArc, v, getTarget() ), Halfedge.Direction.Forward );
		
		// TEMP: do some more checking
		samplePoints();
		inHalfedge.samplePoints();
		outHalfedge.samplePoints();
		
		// return the sub-halfedges in (in,out) order
		List<Halfedge> halfedges = new ArrayList<Halfedge>();
		halfedges.add( inHalfedge );
		halfedges.add( outHalfedge );
		return halfedges;
	}
}
