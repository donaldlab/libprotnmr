package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PolyGeodesicCurve implements Curve
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	private List<Vector3> m_vertices;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public PolyGeodesicCurve( List<Vector3> vertices )
	{
		m_vertices = vertices;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public List<Vector3> getVertices( )
	{
		return m_vertices;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public List<Vector3> samplePoints( )
	{
		 return samplePoints( Math.toRadians( 1.0 ) );
	}
	
	@Override
	public List<Vector3> samplePoints( double stepRadians )
	{
		List<Vector3> samples = new ArrayList<Vector3>();
		for( int i=1; i<m_vertices.size(); i++ )
		{
			Vector3 prevPoint = m_vertices.get( i-1 );
			Vector3 thisPoint = m_vertices.get( i );
			List<Vector3> arcSamples = new GeodesicCurveArc( prevPoint, thisPoint ).samplePoints( stepRadians );
			samples.addAll( arcSamples.subList( 0, arcSamples.size() - 1 ) );
		}
		samples.add( m_vertices.get( m_vertices.size() - 1 ) );
		return samples;
	}
	
	@Override
	public List<Vector3> samplePoints( int numSamples )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsPoint( Vector3 p )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasLength( )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<? extends CurveArc> split( Vector3 point )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<? extends CurveArc> split( Iterable<Vector3> points )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public CurveArc newClosedArc( )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
	
	@Override
	public CurveArc newClosedArc( Vector3 p )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
}
