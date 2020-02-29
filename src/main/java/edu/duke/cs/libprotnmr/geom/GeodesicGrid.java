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

package edu.duke.cs.libprotnmr.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;


public class GeodesicGrid implements Iterable<GeodesicGrid.Face>
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static class Face
	{
		private List<Vector3> m_boundary; // always ccw
		private List<Face> m_neighbors; // first edge b/w first two vertices, then ccw
		private List<Edge> m_edges;
		private Vector3 m_normal;
		
		public Face( Vector3 a, Vector3 b, Vector3 c )
		{
			m_boundary = new ArrayList<Vector3>( 3 );
			m_boundary.clear();
			m_boundary.add( a );
			m_boundary.add( b );
			m_boundary.add( c );
			
			m_neighbors = new ArrayList<Face>( 3 );
			
			m_edges = new ArrayList<Edge>( 3 );
			m_edges.add( new Edge( a, b ) );
			m_edges.add( new Edge( b, c ) );
			m_edges.add( new Edge( c, a ) );
			
			// compute the normal later if it's needed
			m_normal = null;
		}
		
		public Iterable<Vector3> vertices( )
		{
			return m_boundary;
		}
		
		public Iterable<Face> neighbors( )
		{
			return m_neighbors;
		}
		
		public Iterable<Edge> edges( )
		{
			return m_edges;
		}
		
		public Iterable<EdgeFace> edgeNeighbors( )
		{
			List<EdgeFace> pairs = new ArrayList<EdgeFace>();
			for( int i=0; i<3; i++ )
			{
				pairs.add( new EdgeFace( m_edges.get( i ), m_neighbors.get( i ) ) );
			}
			return pairs;
		}
		
		@Override
		public int hashCode( )
		{
			int[] hashes = new int[m_boundary.size()];
			for( int i=0; i<m_boundary.size(); i++ )
			{
				hashes[i] = m_boundary.get( i ).hashCode();
			}
			return HashCalculator.combineHashes( hashes );
		}
		
		@Override
		public boolean equals( Object other )
		{
			if( !( other instanceof Face ) )
			{
				return false;
			}
			return equals( (Face)other );
		}
		
		public boolean equals( Face other )
		{
			return m_boundary.equals( other.m_boundary );
		}
		
		public Vector3 getMidpoint( )
		{
			Vector3 m = new Vector3( m_boundary.get( 0 ) );
			m.add( m_boundary.get( 1 ) );
			m.add( m_boundary.get( 2 ) );
			m.scale( 1.0 / 3.0 );
			return m;
		}
		
		public Vector3 getNormal( )
		{
			if( m_normal == null )
			{
				// compute the face normal (0->1 x 0->2)
				new Vector3();
				Vector3 a = new Vector3( m_boundary.get( 1 ) );
				a.subtract( m_boundary.get( 0 ) );
				Vector3 b = new Vector3( m_boundary.get( 2 ) );
				b.subtract( m_boundary.get( 0 ) );
				m_normal = new Vector3();
				a.getCross( m_normal, b );
				m_normal.normalize();
			}
			return m_normal;
		}
		
		private void setNeighbors( Face a, Face b, Face c )
		{
			m_neighbors.clear();
			m_neighbors.add( a );
			m_neighbors.add( b );
			m_neighbors.add( c );
		}
		
		private void setNeighbor( Edge edge, Face face )
		{
			// get the neighbor index from the edge
			// NOTE: edges are undirected
			int index = -1;
			
			// try to match the edge
			// NOTE: there are always 3 edges in the face, so just enumerate all the cases
			if( edge.m_left.equals( m_boundary.get( 0 ) ) )
			{
				if( edge.m_right.equals( m_boundary.get( 1 ) ) )
				{
					index = 0;
				}
				else if( edge.m_right.equals( m_boundary.get( 2 ) ) )
				{
					index = 2;
				}
			}
			else if( edge.m_left.equals( m_boundary.get( 1 ) ) )
			{
				if( edge.m_right.equals( m_boundary.get( 0 ) ) )
				{
					index = 0;
				}
				else if( edge.m_right.equals( m_boundary.get( 2 ) ) )
				{
					index = 1;
				}
			}
			else if( edge.m_left.equals( m_boundary.get( 2 ) ) )
			{
				if( edge.m_right.equals( m_boundary.get( 0 ) ) )
				{
					index = 2;
				}
				else if( edge.m_right.equals( m_boundary.get( 1 ) ) )
				{
					index = 1;
				}
			}
			
			if( index == -1 )
			{
				throw new Error( "edge is not in this face!" );
			}
			m_neighbors.set( index, face );
		}
		
		public boolean containsPoint( Vector3 q )
		{
			// precondition: q must lie on the unit sphere
			assert( CompareReal.eq( q.getSquaredLength(), 1.0 ) );
			
			// is the query point within a bounding cone for the face?
			Vector3 midpoint = getMidpoint();
			midpoint.normalize();
			double coneDot = 0.0;
			for( Vector3 v : m_boundary )
			{
				coneDot = Math.min( coneDot, v.getDot( midpoint ) );
			}
			if( q.getDot( midpoint ) < coneDot )
			{
				return false;
			}
			
			// project the point onto the plane of the face
			Matrix3 m = new Matrix3();
			Matrix3.getOrthogonalProjection( m, getNormal() );
			Vector3 qp = new Vector3( q );
			qp.subtract( m_boundary.get( 0 ) );
			m.multiply( qp );
			qp.add( m_boundary.get( 0 ) );
			
			// triangle test using Barycentric coords taken from:
			// http://www.blackpawn.com/texts/pointinpoly/default.html
			Vector3 d = new Vector3( m_boundary.get( 1 ) );
			d.subtract( m_boundary.get( 0 ) );
			Vector3 e = new Vector3( m_boundary.get( 2 ) );
			e.subtract( m_boundary.get( 0 ) );
			Vector3 f = new Vector3( qp );
			f.subtract( m_boundary.get( 0 ) );
			
			// compute dot products
			double dotdd = d.getDot( d );
			double dotee = e.getDot( e );
			double dotde = d.getDot( e );
			double dotdf = d.getDot( f );
			double dotef = e.getDot( f );
			
			// compute barycentric coordinates
			double denom = dotdd * dotee - dotde * dotde;
			double u = ( dotee * dotdf - dotde * dotef ) / denom;
			double v = ( dotdd * dotef - dotde * dotdf ) / denom;
			
			// check if point is in triangle
			return ( u >= 0.0 ) && ( v >= 0.0 ) && ( u + v < 1.0 );
		}
		
		public static boolean isPointInFaces( List<Face> faces, Vector3 point )
		{
			for( Face face : faces )
			{
				if( face.containsPoint( point ) )
				{
					return true;
				}
			}
			return false;
		}
	}
	
	public static class Edge
	{
		// NOTE: edges are undirected
		private Vector3 m_left;
		private Vector3 m_right;
		
		public Edge( Vector3 source, Vector3 target )
		{
			this.m_left = source;
			this.m_right = target;
		}
		
		public Vector3 getLeft( )
		{
			return m_left;
		}
		
		public Vector3 getRight( )
		{
			return m_right;
		}
		
		@Override
		public int hashCode( )
		{
			return HashCalculator.combineHashesCommutative( m_left.hashCode(), m_right.hashCode() );
		}
		
		@Override
		public boolean equals( Object other )
		{
			if( !( other instanceof Edge ) )
			{
				return false;
			}
			return equals( (Edge)other );
		}
		
		public boolean equals( Edge other )
		{
			return ( m_left.equals( other.m_left ) && m_right.equals( other.m_right ) )
				|| ( m_left.equals( other.m_right ) && m_right.equals( other.m_left ) );
		}
		
		@Override
		public String toString( )
		{
			return m_left.toString() + " - " + m_right.toString();
		}
	}
	
	public static class EdgeFace
	{
		public Edge edge;
		public Face face;
		
		public EdgeFace( Edge edge, Face face )
		{
			this.edge = edge;
			this.face = face;
		}
	}
	
	/**************************
	 *   Data Members
	 **************************/
	
	private List<Face> m_faces;
	private Map<Vector3,Vector3> m_vertices;
	private int m_numSubdivisions;
	

	/**************************
	 *   Constructors
	 **************************/
	
	public GeodesicGrid( )
	{
		this( 0 );
	}
	
	public GeodesicGrid( int numSubdivisions )
	{
		// |F| = 20*4^n
		int numFaces = 20 * ( 1 << ( numSubdivisions * 2 ) );
		
		m_faces = new ArrayList<Face>( numFaces );
		
		// compute the vertices of a regular icosahedron
		final double x = 0.525731112119133606;
		final double z = 0.850650808352039932;
		Vector3[] vertices =
		{
			new Vector3( -x, 0, z ), new Vector3( x, 0, z ), new Vector3( -x, 0, -z ), new Vector3( x, 0, -z ),
			new Vector3( 0, z, x ), new Vector3( 0, z, -x ), new Vector3( 0, -z, x ), new Vector3( 0, -z, -x ),
			new Vector3( z, x, 0 ), new Vector3( -z, x, 0 ), new Vector3( z, -x, 0 ), new Vector3( -z, -x, 0 )
		};
		
		// these points are already pretty close to the unit sphere, but project them just to be sure
		for( Vector3 v : vertices )
		{
			v.normalize();
		}
		
		// create the faces
		m_faces.add( new Face( vertices[1], vertices[6], vertices[10] ) );
		m_faces.add( new Face( vertices[1], vertices[10], vertices[8] ) );
		m_faces.add( new Face( vertices[1], vertices[8], vertices[4] ) );
		m_faces.add( new Face( vertices[1], vertices[4], vertices[0] ) );
		m_faces.add( new Face( vertices[1], vertices[0], vertices[6] ) );
		
		m_faces.add( new Face( vertices[6], vertices[7], vertices[10] ) );
		m_faces.add( new Face( vertices[10], vertices[3], vertices[8] ) );
		m_faces.add( new Face( vertices[8], vertices[5], vertices[4] ) );
		m_faces.add( new Face( vertices[4], vertices[9], vertices[0] ) );
		m_faces.add( new Face( vertices[0], vertices[11], vertices[6] ) );
		
		m_faces.add( new Face( vertices[10], vertices[7], vertices[3] ) );
		m_faces.add( new Face( vertices[8], vertices[3], vertices[5] ) );
		m_faces.add( new Face( vertices[4], vertices[5], vertices[9] ) );
		m_faces.add( new Face( vertices[0], vertices[9], vertices[11] ) );
		m_faces.add( new Face( vertices[6], vertices[11], vertices[7] ) );
		
		m_faces.add( new Face( vertices[7], vertices[2], vertices[3] ) );
		m_faces.add( new Face( vertices[3], vertices[2], vertices[5] ) );
		m_faces.add( new Face( vertices[5], vertices[2], vertices[9] ) );
		m_faces.add( new Face( vertices[9], vertices[2], vertices[11] ) );
		m_faces.add( new Face( vertices[11], vertices[2], vertices[7] ) );
		
		// do the connectivity
		m_faces.get( 0 ).setNeighbors( m_faces.get( 4 ), m_faces.get( 5 ), m_faces.get( 1 ) );
		m_faces.get( 1 ).setNeighbors( m_faces.get( 0 ), m_faces.get( 6 ), m_faces.get( 2 ) );
		m_faces.get( 2 ).setNeighbors( m_faces.get( 1 ), m_faces.get( 7 ), m_faces.get( 3 ) );
		m_faces.get( 3 ).setNeighbors( m_faces.get( 2 ), m_faces.get( 8 ), m_faces.get( 4 ) );
		m_faces.get( 4 ).setNeighbors( m_faces.get( 3 ), m_faces.get( 9 ), m_faces.get( 0 ) );
		
		m_faces.get( 5 ).setNeighbors( m_faces.get( 14 ), m_faces.get( 10 ), m_faces.get( 0 ) );
		m_faces.get( 6 ).setNeighbors( m_faces.get( 10 ), m_faces.get( 11 ), m_faces.get( 1 ) );
		m_faces.get( 7 ).setNeighbors( m_faces.get( 11 ), m_faces.get( 12 ), m_faces.get( 2 ) );
		m_faces.get( 8 ).setNeighbors( m_faces.get( 12 ), m_faces.get( 13 ), m_faces.get( 3 ) );
		m_faces.get( 9 ).setNeighbors( m_faces.get( 13 ), m_faces.get( 14), m_faces.get( 4 ) );
		
		m_faces.get( 10 ).setNeighbors( m_faces.get( 5 ), m_faces.get( 15 ), m_faces.get( 6 ) );
		m_faces.get( 11 ).setNeighbors( m_faces.get( 6 ), m_faces.get( 16 ), m_faces.get( 7 ) );
		m_faces.get( 12 ).setNeighbors( m_faces.get( 7 ), m_faces.get( 17 ), m_faces.get( 8 ) );
		m_faces.get( 13 ).setNeighbors( m_faces.get( 8 ), m_faces.get( 18 ), m_faces.get( 9 ) );
		m_faces.get( 14 ).setNeighbors( m_faces.get( 9 ), m_faces.get( 19 ), m_faces.get( 5 ) );
		
		m_faces.get( 15 ).setNeighbors( m_faces.get( 19 ), m_faces.get( 16 ), m_faces.get( 10 ) );
		m_faces.get( 16 ).setNeighbors( m_faces.get( 15 ), m_faces.get( 17 ), m_faces.get( 11 ) );
		m_faces.get( 17 ).setNeighbors( m_faces.get( 16 ), m_faces.get( 18 ), m_faces.get( 12 ) );
		m_faces.get( 18 ).setNeighbors( m_faces.get( 17 ), m_faces.get( 19 ), m_faces.get( 13 ) );
		m_faces.get( 19 ).setNeighbors( m_faces.get( 18 ), m_faces.get( 15 ), m_faces.get( 14 ) );
		
		// add the initial vertices
		m_vertices = new HashMap<Vector3,Vector3>();
		for( Vector3 v : vertices )
		{
			m_vertices.put( v, v );
		}
		
		// compute the subdivisions
		m_numSubdivisions = 0;
		for( int i=0; i<numSubdivisions; i++ )
		{
			subdivide();
		}
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	@Override
	public Iterator<Face> iterator( )
	{
		return m_faces.iterator();
	}
	
	public int getNumFaces( )
	{
		return m_faces.size();
	}
	
	public Collection<Vector3> vertices( )
	{
		return m_vertices.values();
	}
	
	public int getNumSubdivisions( )
	{
		return m_numSubdivisions;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void subdivide( )
	{
		Map<Edge,Face> faceLookup = new HashMap<Edge,Face>();
		int numNewFaces = m_faces.size() * 4;
		List<Face> newFaces = new ArrayList<Face>( numNewFaces );
		for( Face face : m_faces )
		{
			// get the vertices
			Iterator<Vector3> iter = face.vertices().iterator();
			Vector3 a = iter.next();
			Vector3 b = iter.next();
			Vector3 c = iter.next();
			
			// bisect the edges and project the midpoints to the unit sphere
			Vector3 ab = getEdgeMidpoint( a, b );
			Vector3 bc = getEdgeMidpoint( b, c );
			Vector3 ca = getEdgeMidpoint( c, a );
			
			// construct the new faces
			Face d = new Face( a, ab, ca );
			Face e = new Face( ab, bc, ca );
			Face f = new Face( ab, b, bc );
			Face g = new Face( ca, bc, c );
			
			newFaces.add( d );
			newFaces.add( e );
			newFaces.add( f );
			newFaces.add( g );
			
			// build the local neighborhood
			d.setNeighbors( null, e, null );
			e.setNeighbors( f, g, d );
			f.setNeighbors( null, null, e );
			g.setNeighbors( e, null, null );
			
			// try to get the rest of the neighbors
			tryToStapleFaces( d, new Edge( ab, a ), faceLookup );
			tryToStapleFaces( d, new Edge( a, ca ), faceLookup );
			tryToStapleFaces( f, new Edge( ab, b ), faceLookup );
			tryToStapleFaces( f, new Edge( b, bc ), faceLookup );
			tryToStapleFaces( g, new Edge( bc, c ), faceLookup );
			tryToStapleFaces( g, new Edge( c, ca ), faceLookup );
		}
		
		m_faces = newFaces;
		m_numSubdivisions++;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
		
	private void tryToStapleFaces( Face face, Edge edge, Map<Edge,Face> faceLookup )
	{
		Face other = faceLookup.get( edge );
		if( other != null )
		{
			// staple the faces together
			face.setNeighbor( edge, other );
			other.setNeighbor( edge, face );
		}
		else
		{
			// put the face in the table for next time
			faceLookup.put( edge, face );
		}
	}
	
	private Vector3 getEdgeMidpoint( Vector3 a, Vector3 b )
	{
		Vector3 midpoint = new Vector3( a );
		midpoint.add( b );
		midpoint.normalize();
		
		// did we already compute this midpoint?
		Vector3 oldMidpoint = m_vertices.get( midpoint );
		if( oldMidpoint != null )
		{
			// reuse that one instead
			return oldMidpoint;
		}
		else
		{
			// otherwise, save this midpoint
			m_vertices.put( midpoint, midpoint );
			return midpoint;
		}
	}
}
