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

import edu.duke.cs.libprotnmr.dataStructures.FuzzyMap;
import edu.duke.cs.libprotnmr.dataStructures.FuzzySet;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;

import java.util.*;

public class Arrangement
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	private FuzzyMap<Vector3,Vertex> m_vertices;
	private List<Edge> m_edges;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public Arrangement( )
	{
		m_vertices = new FuzzyMap<Vector3,Vertex>( 1e-10 );
		m_edges = new ArrayList<Edge>();
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Iterable<Vertex> vertices( )
	{
		return m_vertices.values();
	}
	
	public Iterable<Edge> edges( )
	{
		return m_edges;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public void addCurve( Curve curve )
	{
		addArc( curve.newClosedArc() );
	}
	
	public void addArc( CurveArc arc )
	{
		// NOTE: this incremental construction algorithm is neither fast nor robust
		// however, it's precise enough and gets the job done until the big guns from Halperin's group are ready
		
		// ignore degenerate arcs
		if( !arc.hasLength() )
		{
			return;
		}
		
		// decompose the arc into non-closed edges and add them
		if( arc.isClosed() )
		{
			// split the arc into two edges
			Vertex a = newVertex( arc.getSource() );
			Vertex b = newVertex( arc.getMidpoint() );
			List<? extends CurveArc> subArcs = arc.split( b.getPoint() );
			addEdge( new Edge( subArcs.get( 0 ), a, b ) );
			addEdge( new Edge( subArcs.get( 1 ), b, a ) );
		}
		else
		{
			addEdge( new Edge( arc, newVertex( arc.getSource() ), newVertex( arc.getTarget() ) ) );
		}
	}
	
	public void addEdge( Edge edge )
	{
		// don't allow annoying edges
		assert( !edge.getArc().isClosed() );
		assert( edge.getArc().hasLength() );
		
		// is this the first curve?
		if( m_edges.isEmpty() )
		{
			addEdgeAndVertices( edge );
		}
		else
		{
			// try to (naively) intersect the new curve with all the other curves
			Map<Edge,List<Vector3>> intersectionPoints = new HashMap<Edge,List<Vector3>>();
			for( Edge other : m_edges )
			{
				// don't check against your own curve
				if( edge.getArc().getCurve().equals( other.getArc().getCurve() ) )
				{
					continue;
				}
				
				List<Vector3> points = Intersector.getIntersectionPoints( edge.getArc(), other.getArc() );
				if( !points.isEmpty() )
				{
					intersectionPoints.put( other, points );
				}
			}
			
			if( intersectionPoints.isEmpty() )
			{
				addEdgeAndVertices( edge );
			}
			else
			{
				// split the existing edges as needed
				FuzzySet<Vector3> allIntersectionPoints = new FuzzySet<Vector3>();
				for( Map.Entry<Edge,List<Vector3>> entry : intersectionPoints.entrySet() )
				{
					removeEdge( entry.getKey() );
					for( Edge subEdge : getSubEdges( entry.getKey(), entry.getValue() ) )
					{
						addEdgeAndVertices( subEdge );
					}
					allIntersectionPoints.addAll( entry.getValue() );
				}
				
				// split the new edge
				for( Edge subEdge : getSubEdges( edge, allIntersectionPoints ) )
				{
					addEdgeAndVertices( subEdge );
				}
			}
		}
	}
	
	public void removeEdge( Edge edge )
	{
		m_edges.remove( edge );
		
		boolean wasRemoved = edge.getSource().removeEdge( edge );
		assert( wasRemoved );
		if( edge.getSource().isIsolated() )
		{
			m_vertices.remove( edge.getSource().getPoint() );
		}
		
		wasRemoved = edge.getTarget().removeEdge( edge );
		assert( wasRemoved );
		if( edge.getTarget().isIsolated() )
		{
			m_vertices.remove( edge.getTarget().getPoint() );
		}
	}
	
	public Iterable<Edge> getEdgesInBfsOrder( )
	{
		class Entry
		{
			public Vertex vertex;
			public Edge edge;
			
			public Entry( Vertex vertex, Edge edge )
			{
				this.vertex = vertex;
				this.edge = edge;
			}
		}
		
		// collect all the edges
		HashSet<Edge> allEdges = new HashSet<Edge>( m_edges );
		
		// keep doing BFS until all edges have been visited
		LinkedHashSet<Edge> visitedEdges = new LinkedHashSet<Edge>();
		while( !allEdges.isEmpty() )
		{
			// get a starting edge
			Edge firstEdge = allEdges.iterator().next();
			
			// do BFS in the edge graph starting with an arbitrary edge
			Deque<Entry> queue = new ArrayDeque<Entry>();
			queue.add( new Entry( firstEdge.getSource(), firstEdge ) );
			while( !queue.isEmpty() )
			{
				Entry entry = queue.pollFirst();
				
				// did we already visit this edge?
				if( visitedEdges.contains( entry.edge ) )
				{
					continue;
				}

				visitedEdges.add( entry.edge );
				allEdges.remove( entry.edge );
				
				// add unvisited neighbors
				Vertex otherEndpoint = entry.edge.getOtherEndpoint( entry.vertex );
				for( Edge neighbor : otherEndpoint.getIncidentEdges() )
				{
					if( !visitedEdges.contains( neighbor ) )
					{
						queue.addLast( new Entry( otherEndpoint, neighbor ) );
					}
				}
			}
		}
		assert( visitedEdges.size() == m_edges.size() );
		return visitedEdges;
	}
	
	public List<Face> computeFaces( )
	{
		Set<List<Halfedge>> boundaries = new HashSet<List<Halfedge>>();
		
		// make a list of all the halfedges
		// NOTE: this arrangement implementation should probably use halfedges natively, but I'm too lazy to code them in now.
		// Just fake halfedges for now and we'll get by
		Set<Halfedge> unusedHalfedges = new HashSet<Halfedge>();
		for( Edge edge : m_edges )
		{
			unusedHalfedges.add( edge.getForwardHalfedge() );
			unusedHalfedges.add( edge.getReverseHalfedge() );
		}
		
		while( !unusedHalfedges.isEmpty() )
		{
			// pick an arbitrary halfedge
			Halfedge halfedge = unusedHalfedges.iterator().next();
			Vertex startingVertex = halfedge.getSource();
			
			// collect the boundary edges (in counterclockwise order) into a new face
			List<Halfedge> boundary = new ArrayList<Halfedge>();
			boundary.add( halfedge );
			unusedHalfedges.remove( halfedge );
			Vertex nextVertex = halfedge.getTarget();
			while( nextVertex != startingVertex )
			{
				// DEBUG
				//assert( nextVertex.areEdgesInCounterclockwiseOrder() );
				
				// get the next halfedge along the boundary
				halfedge = nextVertex.getNextClockwiseEdge( halfedge.getEdge() ).getHalfedgeBySource( nextVertex );
				boundary.add( halfedge );
				boolean wasRemoved = unusedHalfedges.remove( halfedge );
				
				// DEBUG
				if( false && !wasRemoved )
				{
					// show the boundary
					Kinemage kin = new Kinemage();
					KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
					KinemageBuilder.appendDetailedArrangement( kin, this, "Arrangement", KinemageColor.Grey, 1 );
					KinemageBuilder.appendBoundary( kin, boundary, "Boundary", KinemageColor.Cobalt, 1 );
					new KinemageWriter().show( kin );
				}
				
				assert( wasRemoved );
				nextVertex = halfedge.getTarget();
			}
			boundaries.add( boundary );
			
			// DEBUG
			if( false )
			{
				// show the boundary
				Kinemage kin = new Kinemage();
				KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
				KinemageBuilder.appendBoundary( kin, boundary, "Boundary", KinemageColor.Cobalt, 1 );
				new KinemageWriter().showAndWait( kin );
			}
		}
		
		// assemble the boundaries into faces (don't forget to check for holes)
		List<Face> faces = new ArrayList<Face>();
		while( !boundaries.isEmpty() )
		{
			// pick a boundary
			List<Halfedge> boundary = boundaries.iterator().next();
			boundaries.remove( boundary );
			
			// construct the face (add the holes if needed)
			Face face = new Face( boundary );
			List<List<Halfedge>> nestedBoundaries = getNestedBoundaries( boundary, boundaries );
			if( !nestedBoundaries.isEmpty() )
			{
				for( List<Halfedge> outermostBoundary : getOutermostBoundaries( boundary, nestedBoundaries ) )
				{
					face.addHole( outermostBoundary );
					boundaries.remove( outermostBoundary );
				}
			}
			faces.add( face );
			
			// set the face pointers
			for( Halfedge halfedge : face.boundary() )
			{
				halfedge.setFace( face );
			}
		}
		return faces;
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private Vertex newVertex( Vector3 point )
	{
		// is this point the at (approximately) any of the vertices?
		Vertex vertex = m_vertices.get( point );
		if( vertex != null )
		{
			return vertex;
		}
		
		// guess not, make a new vertex
		return new Vertex( point );
	}
	
	private void addEdgeAndVertices( Edge edge )
	{
		m_edges.add( edge );
		edge.getSource().addEdge( edge );
		edge.getTarget().addEdge( edge );
		m_vertices.put( edge.getSource().getPoint(), edge.getSource() );
		m_vertices.put( edge.getTarget().getPoint(), edge.getTarget() );
	}
	
	private List<Edge> getSubEdges( Edge edge, Iterable<Vector3> splitPoints )
	{
		// PRECONDITION: all split points are on the edge's arc
		List<? extends CurveArc> subArcs = edge.getArc().split( splitPoints );
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendCurve( kin, edge.getArc(), "Edge", KinemageColor.Cobalt, 1 );
			for( CurveArc arc : subArcs )
			{
				KinemageBuilder.appendCurve( kin, arc, "Arc", KinemageColor.Orange, 1 );
			}
			new KinemageWriter().show( kin );
		}
		
		Vertex lastVertex = edge.getSource();
		List<Edge> subEdges = new ArrayList<Edge>();
		for( int i=0; i<subArcs.size()-1; i++ )
		{
			CurveArc arc = subArcs.get( i );
			Vertex nextVertex = newVertex( arc.getTarget() );
			subEdges.add( new Edge( arc, lastVertex, nextVertex ) );
			lastVertex = nextVertex;
		}
		subEdges.add( new Edge( subArcs.get( subArcs.size() - 1 ), lastVertex, edge.getTarget() ) );
		return subEdges;
	}
	
	private List<List<Halfedge>> getNestedBoundaries( List<Halfedge> outerBoundary, Set<List<Halfedge>> boundaries )
	{
		// make a temporary face so we can test for point inclusion
		Face outerFace = new Face( outerBoundary );
		
		List<List<Halfedge>> nestedBoundaries = new ArrayList<List<Halfedge>>();
		for( List<Halfedge> boundary : boundaries )
		{
			if( boundariesShareVertex( boundary, outerBoundary ) )
			{
				// boundary is not nested
				continue;
			}
			
			// if any point on the boundary is inside the outer boundary, then the boundary is nested
			Vector3 arbitraryPoint = boundary.get( 0 ).getSource().getPoint();
			if( outerFace.containsPoint( arbitraryPoint ) )
			{
				// DEBUG
				if( false )
				{
					Kinemage kin = new Kinemage();
					KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
					KinemageBuilder.appendBoundary( kin, outerFace.boundary(), "Outer Boundary", KinemageColor.Cobalt, 1 );
					KinemageBuilder.appendBoundary( kin, boundary, "Query Boundary", KinemageColor.Orange, 1 );
					KinemageBuilder.appendPoint( kin, arbitraryPoint, "Arbitrary Point", KinemageColor.Lime, 7 );
					new KinemageWriter().show( kin );
				}
				
				nestedBoundaries.add( boundary );
			}
		}
		
		// DEBUG
		if( false && !nestedBoundaries.isEmpty() )
		{
			// show the nested boundaries
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			for( List<Halfedge> boundary : boundaries )
			{
				if( nestedBoundaries.contains( boundary ) )
				{
					KinemageBuilder.appendBoundary( kin, boundary, "Nested Boundary", KinemageColor.Green, 1 );
				}
				else
				{
					KinemageBuilder.appendBoundary( kin, boundary, "Boundary", KinemageColor.Grey, 1 );
				}
			}
			KinemageBuilder.appendBoundary( kin, outerBoundary, "Outer Boundary", KinemageColor.Cobalt, 1 );
			new KinemageWriter().showAndWait( kin );
		}
		
		return nestedBoundaries;
	}
	
	private boolean boundariesShareVertex( List<Halfedge> a, List<Halfedge> b )
	{
		HashSet<Vertex> vertices = new HashSet<Vertex>();
		for( Halfedge halfedge : a )
		{
			vertices.add( halfedge.getSource() );
		}
		for( Halfedge halfedge : b )
		{
			if( vertices.contains( halfedge.getSource() ) )
			{
				return true;
			}
		}
		return false;
	}
	
	private List<List<Halfedge>> getOutermostBoundaries( List<Halfedge> outerBoundary, List<List<Halfedge>> nestedBoundaries )
	{
		// which of the nested boundaries "surround" the outer boundary? (ie, only pick one from each pair of halfedge chains)
		List<List<Halfedge>> candidates = new ArrayList<List<Halfedge>>();
		for( List<Halfedge> nestedBoundary : nestedBoundaries )
		{
			// if any point on the outer boundary is inside the nested boundary, then the outer boundary is "surrounded"
			Face testFace = new Face( nestedBoundary );
			Vector3 aribtraryPoint = outerBoundary.get( 0 ).getSource().getPoint();
			if( testFace.containsPoint( aribtraryPoint ) )
			{
				candidates.add( nestedBoundary );
			}
		}
		
		// of the candidates, which are the outermost?
		List<List<Halfedge>> outermostBoundaries = new ArrayList<List<Halfedge>>();
		for( List<Halfedge> candidate : candidates )
		{
			// if any curve from the candidate boundary to the outer boundary intersects any other boundary an odd number of times, it cannot be outermost
			
			// innocent until proven guilty...
			boolean isOutermost = true;
			
			// construct the curve
			GeodesicCurveArc arc = GeodesicCurveArc.newByPointsWithArbitraryNormal(
				outerBoundary.get( 0 ).getSource().getPoint(),
				candidate.get( 0 ).getSource().getPoint()
			);
			for( List<Halfedge> otherBoundary : candidates )
			{
				if( otherBoundary == candidate )
				{
					continue;
				}
				
				// how many times does this arc intersect the boundary?
				FuzzySet<Vector3> intersectionPoints = new FuzzySet<Vector3>();
				for( Halfedge halfedge : otherBoundary )
				{
					intersectionPoints.addAll( Intersector.getIntersectionPoints( arc, halfedge.getEdge().getArc() ) );
				}
				
				// DEBUG
				if( false )
				{
					Kinemage kin = new Kinemage();
					KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
					KinemageBuilder.appendArrangementFaces( kin, Arrays.asList( new Face( outerBoundary ) ), "Outer Boundary", KinemageColor.Cobalt );
					KinemageBuilder.appendBoundary( kin, otherBoundary, "Other Boundary", KinemageColor.Grey, 1 );
					KinemageBuilder.appendBoundary( kin, candidate, "Candidate Boundary", KinemageColor.Green, 1 );
					KinemageBuilder.appendCurve( kin, arc, "Query Arc", KinemageColor.Orange, 1 );
					KinemageBuilder.appendPoints( kin, intersectionPoints, "" + intersectionPoints.size() + " Intersection Points", KinemageColor.Lime, 7 );
					new KinemageWriter().show( kin );
				}
				
				if( intersectionPoints.size() % 2 == 1 )
				{
					isOutermost = false;
					break;
				}
			}
			
			if( isOutermost )
			{
				outermostBoundaries.add( candidate );
			}
		}
		
		// DEBUG
		if( false )
		{
			// show the nested candidate boundaries
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, outerBoundary, "Outer Boundary", KinemageColor.Cobalt, 1 );
			for( List<Halfedge> boundary : candidates )
			{
				if( outermostBoundaries.contains( boundary ) )
				{
					KinemageBuilder.appendBoundary( kin, boundary, "Outermost Boundary", KinemageColor.Green, 1 );
				}
				else
				{
					KinemageBuilder.appendBoundary( kin, boundary, "Candidate Boundary", KinemageColor.Grey, 1 );
				}
			}
			new KinemageWriter().showAndWait( kin );
		}
		
		return outermostBoundaries;
	}
}
	