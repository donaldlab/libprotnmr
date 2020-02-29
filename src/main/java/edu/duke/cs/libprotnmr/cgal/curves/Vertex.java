package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.util.CircularList;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;

import java.util.*;

public class Vertex
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Vector3 m_point;
	private List<Edge> m_incidentEdges;
	private boolean m_isSorted;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public Vertex( Vector3 point )
	{
		m_point = point;
		m_incidentEdges = new ArrayList<Edge>();
		m_isSorted = false;
	}
	
	public Vertex( Vertex other )
	{
		m_point = other.m_point;
		m_incidentEdges = new ArrayList<Edge>( other.m_incidentEdges );
		m_isSorted = other.m_isSorted;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getPoint( )
	{
		return m_point;
	}
	
	public Iterable<Edge> getIncidentEdges( )
	{
		// sort the edges on demand
		if( !m_isSorted && m_incidentEdges.size() > 2 )
		{
			sortEdges();
		}
		return m_incidentEdges;
	}
	
	public int getNumIncidentEdges( )
	{
		return m_incidentEdges.size();
	}
	
	public int getDegree( )
	{
		return m_incidentEdges.size();
	}
	
	public boolean isIsolated( )
	{
		return m_incidentEdges.isEmpty();
	}
	
	public Edge getNextClockwiseEdge( Edge edge )
	{
		// sort the edges on demand
		if( !m_isSorted && m_incidentEdges.size() > 2 )
		{
			sortEdges();
		}
		
		for( int i=0; i<m_incidentEdges.size(); i++ )
		{
			if( m_incidentEdges.get( i ).equals( edge ) )
			{
				// DEBUG
				if( false )
				{
					Kinemage kin = new Kinemage();
					KinemageBuilder.appendAxes( kin, 1, 0.2 );
					KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
					KinemageBuilder.appendPoint( kin, m_point, "Vertex", KinemageColor.Orange, 7 );
					KinemageBuilder.appendCurve( kin, edge.getArc(), "Query edge", KinemageColor.Green, 2 );
					for( Edge e : m_incidentEdges )
					{
						KinemageBuilder.appendCurve( kin, e.getArc(), "Edge", KinemageColor.Cobalt, 1 );
					}
					new KinemageWriter().show( kin );
				}
				
				return CircularList.getNext( m_incidentEdges, i );
			}
		}
		
		// no match?
		throw new IllegalArgumentException( "Query edge must first be added to the incident edges!" );
	}
	
	public boolean containsEdge( Edge edge )
	{
		return m_incidentEdges.contains( edge );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public boolean addEdge( Edge edge )
	{
		// don't add edges more than once
		if( new ArrayList<Edge>( m_incidentEdges ).contains( edge ) )
		{
			throw new IllegalArgumentException( "Can't add an edge more than once!" );
		}
		
		// sadly, we can't add edges with closed arcs or arcs that are too small
		// or the shrinking ordering circle heuristic will fail
		assert( !edge.getArc().isClosed() );
		assert( !edge.getArc().getSource().approximatelyEquals( edge.getArc().getTarget(), 1e-12 ) );
		
		// add the edge and mark dirty
		m_isSorted = false;
		return m_incidentEdges.add( edge );
	}
	
	public boolean removeEdge( Edge edge )
	{
		return m_incidentEdges.remove( edge );
	}
	
	@Override
	public int hashCode( )
	{
		return m_point.hashCode();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Vertex )
		{
			return equals( (Vertex)other );
		}
		return false;
	}
	
	public boolean equals( Vertex other )
	{
		return m_point.equals( other.m_point );
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private void sortEdges( )
	{
		// find the shortest incident edge (approximately)
		Edge shortestEdge = null;
		double minLength = Double.POSITIVE_INFINITY;
		for( Edge edge : m_incidentEdges )
		{
			double length = edge.getArc().getApproximateLength( 20 );
			if( length < minLength )
			{
				minLength = length;
				shortestEdge = edge;
			}
		}
		
		// estimate an initial ordering circle
		final double MaxAngle = Math.toRadians( 1.0 );
		double smallestAngle = Math.min(
			Math.acos( m_point.getDot( shortestEdge.getArc().getMidpoint() ) ),
			MaxAngle
		);
		CircularCurve orderingCircle = new CircularCurve( m_point, smallestAngle );
		
		// compute all the intersections. Make sure we get exactly one for each edge
		Map<Edge,Vector3> allIntersections = getUniqueIntersections( orderingCircle );
		while( allIntersections == null )
		{
			// ordering circle is too big, shrink it
			orderingCircle = new CircularCurve(
				orderingCircle.getRotConeToMol(),
				orderingCircle.getConeTheta() * 0.5
			);
			
			// but now is it too small?
			if( CompareReal.eq( orderingCircle.getConeHeight(), 1.0, 1e-10 ) )
			{
				// DEBUG
				if( false )
				{
					Kinemage kin = new Kinemage();
					KinemageBuilder.appendAxes( kin, 1, 0.2 );
					KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
					KinemageBuilder.appendPoint( kin, m_point, "Vertex", KinemageColor.Lime, 7 );
					for( Edge edge : m_incidentEdges )
					{
						KinemageBuilder.appendCurve( kin, edge.getArc(), "Edge", KinemageColor.Cobalt, 1 );
					}
					KinemageBuilder.appendCurve( kin, orderingCircle, "Ordering circle", KinemageColor.Orange, 1 );
					new KinemageWriter().show( kin );
				}
				throw new Error( "Shrinking ordering circle heuristic failed! Circle is too small!" );
			}
			
			// try again
			allIntersections = getUniqueIntersections( orderingCircle );
		}
		
		// compute all the angles
		final Map<Edge,Double> angles = new HashMap<Edge,Double>();
		for( Edge edge : m_incidentEdges )
		{
			angles.put( edge, orderingCircle.getAngle( allIntersections.get( edge ) ) );
		}
		
		// finally sort the edges
		Collections.sort( m_incidentEdges, new Comparator<Edge>()
		{
			@Override
			public int compare( Edge a, Edge b )
			{
				// sort in clockwise order
				return Double.compare( angles.get( b ), angles.get( a ) ); 
			}
		} );
		m_isSorted = true;
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendPoint( kin, m_point, "Vertex", KinemageColor.Lime, 7 );
			for( Edge edge : m_incidentEdges )
			{
				KinemageBuilder.appendCurve( kin, edge.getArc(), String.format( "Edge %.1f", Math.toDegrees( angles.get( edge ) ) ), KinemageColor.Cobalt, 1 );
			}
			KinemageBuilder.appendCurve( kin, orderingCircle, "Ordering circle", KinemageColor.Orange, 1 );
			new KinemageWriter().showAndWait( kin );
		}
	}
	
	private Map<Edge,Vector3> getUniqueIntersections( CircularCurve orderingCircle )
	{
		Map<Edge,Vector3> allIntersections = new HashMap<Edge,Vector3>();
		for( Edge edge : m_incidentEdges )
		{
			List<Vector3> intersections = Intersector.getIntersectionPoints( orderingCircle, edge.getArc() );
			if( intersections.size() != 1 )
			{
				return null;
			}
			allIntersections.put( edge, intersections.get( 0 ) );
		}
		return allIntersections;
	}
}
