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

import edu.duke.cs.libprotnmr.dataStructures.FuzzySet;
import edu.duke.cs.libprotnmr.geom.Sphere;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.util.MinDistanceComparator;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Face
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	List<Halfedge> m_outerBoundary; // counterclockwise order
	List<List<Halfedge>> m_innerBoundaries; // clockwise order
	List<Vertex> m_vertices;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public Face( List<Halfedge> outerBoundary )
	{
		m_outerBoundary = new ArrayList<Halfedge>( outerBoundary );
		m_innerBoundaries = new ArrayList<List<Halfedge>>();
		
		// collect all the vertices
		m_vertices = new ArrayList<Vertex>();
		for( Halfedge halfedge : m_outerBoundary )
		{
			m_vertices.add( halfedge.getSource() );
		}
		
		// DEBUG: make sure this is a valid face
		if( true )
		{
			// all vertices must have at least two edges
			for( Vertex v : m_vertices )
			{
				assert( v.getNumIncidentEdges() >= 2 );
			}
			
			// make sure the edges on the outer boundary form a chain
			for( int i=0; i<m_outerBoundary.size(); i++ )
			{
				Halfedge thisHalfedge = m_outerBoundary.get( i );
				Halfedge nextHalfedge = m_outerBoundary.get( ( i + 1 ) % m_outerBoundary.size() );
				
				assert( thisHalfedge.getTarget() == nextHalfedge.getSource() );
				assert( thisHalfedge.getSource().containsEdge( thisHalfedge.getEdge() ) );
				assert( thisHalfedge.getTarget().containsEdge( thisHalfedge.getEdge() ) );
			}
		}
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public List<Halfedge> boundary( )
	{
		List<Halfedge> boundary = new ArrayList<Halfedge>();
		boundary.addAll( m_outerBoundary );
		for( List<Halfedge> innerBoundary : m_innerBoundaries )
		{
			boundary.addAll( innerBoundary );
		}
		return boundary;
	}
	
	public List<Halfedge> outerBoundary( )
	{
		return Collections.unmodifiableList( m_outerBoundary );
	}
	
	public List<Vertex> vertices( )
	{
		return Collections.unmodifiableList( m_vertices );
	}
	
	public int getNumOuterBoundaryEdges( )
	{
		return m_outerBoundary.size();
	}
	
	public void addHole( Iterable<Halfedge> halfedges )
	{
		List<Halfedge> innerBoundary = new ArrayList<Halfedge>();
		for( Halfedge halfedge : halfedges )
		{
			innerBoundary.add( halfedge );
		}
		m_innerBoundaries.add( innerBoundary );
	}
	
	public void addHole( Halfedge halfedge )
	{
		m_innerBoundaries.add( Arrays.asList( halfedge ) );
	}
	
	public List<List<Halfedge>> holes( )
	{
		return Collections.unmodifiableList( m_innerBoundaries );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public FuzzySet<Vector3> getBoundaryIntersections( CurveArc arc )
	{
		FuzzySet<Vector3> intersectionPoints = new FuzzySet<Vector3>();
		for( Halfedge halfedge : boundary() )
		{
			intersectionPoints.addAll( Intersector.getIntersectionPoints( halfedge.getEdge().getArc(), arc ) );
		}
		return intersectionPoints;
	}
	
	public FuzzySet<Vector3> getBoundaryIntersections( Curve curve )
	{
		FuzzySet<Vector3> intersectionPoints = new FuzzySet<Vector3>( 1e-10 );
		for( Halfedge halfedge : boundary() )
		{
			intersectionPoints.addAll( Intersector.getIntersectionPoints( halfedge.getEdge().getArc(), curve ) );
		}
		return intersectionPoints;
	}
	
	public boolean boundaryContainsPoint( Vector3 p )
	{
		return boundaryContainsPoint( p, Curve.DefaultEpsilon );
	}
	
	public boolean boundaryContainsPoint( Vector3 p, double epsilon )
	{
		// is p on the boundary?
		for( Halfedge halfedge : boundary() )
		{
			if( halfedge.getEdge().getArc().containsPoint( p, epsilon ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean containsPoint( Vector3 p )
	{
		return containsPoint( p, Curve.DefaultEpsilon );
	}
	
	public boolean containsPoint( final Vector3 q, double boundaryEpsilon )
	{
		// NOTE: this is a pretty expensive predicate to evaluate =(
		
		// if the point is on the boundary of the face, then the face contains the point
		if( boundaryContainsPoint( q, boundaryEpsilon ) )
		{
			return true;
		}
		
		// find the vertex closest to the query point
		Vertex closestVertex = Collections.min(
			m_vertices,
			new MinDistanceComparator<Vertex>( )
			{
				@Override
				public double getDist( Vertex v )
				{
					return Math.acos( v.getPoint().getDot( q ) );
				}
			}
		);
		
		// build a geodesic to the vertex to see if there's a closer intersection with the boundary
		GeodesicCurve geodesic = GeodesicCurveArc.newByPointsWithArbitraryNormal( q, closestVertex.getPoint() ).getCurve();
		final double PointEpsilon = 1e-8;
		FuzzySet<Vector3> boundaryIntersections = new FuzzySet<Vector3>( PointEpsilon );
		boundaryIntersections.add( closestVertex.getPoint() );
		boundaryIntersections.addAll( getBoundaryIntersections( geodesic ) );
		Vector3 closestBoundaryIntersection = Collections.min(
			boundaryIntersections,
			new MinDistanceComparator<Vector3>( )
			{
				@Override
				public double getDist( Vector3 p )
				{
					return Math.acos( p.getDot( q ) );
				}
			}
		);
		
		// get the full boundary just once
		List<Halfedge> boundary = boundary();
		
		// get/build a vertex and local edges at the intersection point
		Vertex intersectionVertex = null;
		Halfedge outHalfedge = null;
		
		// is the closest intersection at a vertex?
		for( Halfedge halfedge : boundary )
		{
			if( halfedge.getSource().getPoint().approximatelyEquals( closestBoundaryIntersection, PointEpsilon ) )
			{
				intersectionVertex = new Vertex( halfedge.getSource() );
				outHalfedge = halfedge;
				break;
			}
		}
		
		// was the intersection not at a vertex? build a new vertex from scratch
		if( intersectionVertex == null )
		{
			intersectionVertex = new Vertex( closestBoundaryIntersection );
			
			// find the intersected edge
			Halfedge intersectedHalfedge = null;
			for( Halfedge halfedge : boundary )
			{
				if( halfedge.getEdge().getArc().containsPoint( closestBoundaryIntersection ) )
				{
					intersectedHalfedge = halfedge;
					break;
				}
			}
			assert( intersectedHalfedge != null );
			
			// split the halfedge add the local edges to the new vertex
			List<Halfedge> subHalfedges = intersectedHalfedge.split( intersectionVertex );
			Halfedge inHalfedge = subHalfedges.get( 0 );
			outHalfedge = subHalfedges.get( 1 );
			intersectionVertex.addEdge( inHalfedge.getEdge() );
			intersectionVertex.addEdge( outHalfedge.getEdge() );
		}
		
		// sanity check: the intersection should always have at least two edges
		assert( intersectionVertex.getNumIncidentEdges() >= 2 );
		
		// if the next clockwise edge from our query edge is the out edge at this vertex, then the point is inside
		GeodesicCurveArc arc = GeodesicCurveArc.newByPointsWithArbitraryNormal( q, intersectionVertex.getPoint() );
		Edge queryEdge = new Edge( arc, new Vertex( q ), intersectionVertex );
		intersectionVertex.addEdge( queryEdge );
		Edge nextEdge = intersectionVertex.getNextClockwiseEdge( queryEdge );
		assert( nextEdge != queryEdge );
		boolean isInside = nextEdge == outHalfedge.getEdge();
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, m_outerBoundary, "Boundary", KinemageColor.Cobalt, 1 );
			KinemageBuilder.appendCurve( kin, queryEdge.getArc(), "Query Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendHalfedge( kin, outHalfedge, "Out Halfedge", KinemageColor.Lime, 1 );
			KinemageBuilder.appendCurve( kin, nextEdge.getArc(), "Next Edge", isInside ? KinemageColor.Green : KinemageColor.Red, 3 );
			KinemageBuilder.appendPoints( kin, Arrays.asList( q ), "Query Point", KinemageColor.Lime, 7 );
			KinemageBuilder.appendPoints( kin, Arrays.asList( closestBoundaryIntersection ), "Boundary Intersection", KinemageColor.Orange, 7 );
			new KinemageWriter().show( kin );
		}
		
		return isInside;
	}
	
	public Vector3 getArbitraryInteriorPoint( )
	{
		Vector3 interiorPoint = null;
		
		// find a geodesic circle guaranteed to intersect the boundary at least twice
		List<Halfedge> boundary = boundary();
		if( boundary.size() == 1 )
		{
			// only one option to try, really...
			Vector3 a = boundary.get( 0 ).getSource().getPoint();
			Vector3 b = boundary.get( 0 ).getEdge().getArc().getMidpoint();
			GeodesicCurve curve = GeodesicCurveArc.newByPointsWithArbitraryNormal( a, b ).getCurve();
			interiorPoint = getInteriorPoint( curve );
		}
		else
		{
			// for more complicated faces, we might have to try a number of point combinations
			// use the vertices and the edge midpoints
			
			// first, get a list of points to try
			List<Vector3> points = new ArrayList<Vector3>();
			for( Halfedge halfedge : boundary )
			{
				points.add( halfedge.getSource().getPoint() );
				points.add( halfedge.getEdge().getArc().getMidpoint() );
			}
			
			// try all pairs of points
			for( int i=0; i<points.size() && interiorPoint == null; i++ )
			{
				for( int j=i+1; j<points.size() && interiorPoint == null; j++ )
				{
					Vector3 a = points.get( i );
					Vector3 b = points.get( j );
					GeodesicCurve curve = GeodesicCurveArc.newByPointsWithArbitraryNormal( a, b ).getCurve();
					interiorPoint = getInteriorPoint( curve );
				}
			}
		}
		
		// DEBUG
		if( true && interiorPoint == null )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, boundary, "Boundary", KinemageColor.Cobalt, 1 );
			new KinemageWriter().show( kin );
		}
		
		assert( interiorPoint != null );
		return interiorPoint;
	}
	
	public CircularCurve getCircularBound( )
	{
		// heuristically pick a set of curve samples
		FuzzySet<Vector3> samplesSet = new FuzzySet<Vector3>();
		for( Halfedge halfedge : m_outerBoundary )
		{
			samplesSet.add( halfedge.getEdge().getArc().getSource() );
			samplesSet.add( halfedge.getEdge().getArc().getMidpoint() );
			samplesSet.add( halfedge.getEdge().getArc().getTarget() );
		}
		List<Vector3> samples = new ArrayList<Vector3>( samplesSet );
		
		// compute the cone center and lower bound
		Sphere boundingSphere = new Sphere( samples );
		Vector3 center = boundingSphere.center;
		center.normalize();
		double min = Math.asin( boundingSphere.radius );
		
		// pick a rotation for that axis
		Matrix3 rot = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( rot, center );
		
		/* TEMP
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendArrangementFaces( kin, Arrays.asList( face ), "Face", KinemageColor.Lime );
		KinemageBuilder.appendPoints( kin, Arrays.asList( center ), "Center", KinemageColor.Green, 7 );
		KinemageBuilder.appendPoints( kin, samples, "Samples", KinemageColor.Lime, 7 );
		KinemageBuilder.appendCurve( kin, new CircularCurve( rot, min ), "Bound", KinemageColor.Green, 1 );
		*/
		
		// the angle of the bounding cone must be between min and PI, so use binary search to narrow it down
		final double Epsilon = Math.toRadians( 1.0 );
		double max = Math.PI;
		while( max - min > Epsilon )
		{
			double midpoint = ( max + min ) / 2.0;
			CircularCurve bound = new CircularCurve( rot, midpoint );
			
			// does this cone intersect the face?
			boolean intersects = false;
			for( Halfedge faceHalfedge : m_outerBoundary )
			{
				if( !Intersector.getIntersectionPoints( faceHalfedge.getEdge().getArc(), bound ).isEmpty() )
				{
					intersects = true;
					break;
				}
			}
			
			// TEMP
			//KinemageBuilder.appendCurve( kin, bound, "Bound", intersects ? KinemageColor.Green : KinemageColor.Red, 1 );
			
			if( intersects )
			{
				min = midpoint;
			}
			else
			{
				max = midpoint;
			}
		}
		
		CircularCurve bound = new CircularCurve( rot, max );
		
		/* TEMP
		KinemageBuilder.appendCurve( kin, bound, "Bound", KinemageColor.Green, 2 );
		new KinemageWriter().showAndWait( kin );
		*/
		
		return bound;
	}
	
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private Vector3 getInteriorPoint( GeodesicCurve curve )
	{
		// segment this curve using the face boundary
		List<? extends CurveArc> subArcs = curve.split( getBoundaryIntersections( curve ) );
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, boundary(), "Face", KinemageColor.Cobalt, 1 );
			for( CurveArc subArc : subArcs )
			{
				KinemageBuilder.appendCurve( kin, subArc, "Query Arc", KinemageColor.Orange, 1 );
				KinemageBuilder.appendPoint( kin, subArc.getMidpoint(), "Query Point", KinemageColor.Orange, 7 );
			}
			new KinemageWriter().show( kin );
		}
		
		// one of the sub arc midpoints might be in the face
		for( CurveArc subArc : subArcs )
		{
			Vector3 midpoint = subArc.getMidpoint();
			if( boundaryContainsPoint( midpoint ) )
			{
				// on the boundary isn't good enough
				continue;
			}
			
			if( containsPoint( midpoint ) )
			{
				// we found an interior point!
				return midpoint;
			}
		}
		return null;
	}
}
