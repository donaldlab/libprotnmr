package edu.duke.cs.libprotnmr.cgal.curves;

public class Edge
{
	/*********************************
	 *   Data Members
	 *********************************/
	
	private CurveArc m_arc;
	private Vertex m_source;
	private Vertex m_target;
	private Halfedge m_forwardHalfedge;
	private Halfedge m_reverseHalfedge;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public Edge( CurveArc arc, Vertex source, Vertex target )
	{
		m_arc = arc;
		m_source = source;
		m_target = target;
		
		// just in case...
		assert( m_arc.containsPoint( m_source.getPoint() ) );
		assert( m_arc.containsPoint( m_target.getPoint() ) );
		
		// make the halfedges
		m_forwardHalfedge = new Halfedge( this, Halfedge.Direction.Forward );
		m_reverseHalfedge = new Halfedge( this, Halfedge.Direction.Reverse );
		m_forwardHalfedge.setTwin( m_reverseHalfedge );
		m_reverseHalfedge.setTwin( m_forwardHalfedge );
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public CurveArc getArc( )
	{
		return m_arc;
	}
	
	public Vertex getSource( )
	{
		return m_source;
	}
	
	public Vertex getTarget( )
	{
		return m_target;
	}
	
	public Vertex getOtherEndpoint( Vertex endpoint )
	{
		if( endpoint.equals( m_source ) )
		{
			return m_target;
		}
		else if( endpoint.equals( m_target ) )
		{
			return m_source;
		}
		else
		{
			throw new IllegalArgumentException( "endpoint must be an endpoint of the edge" );
		}
	}
	
	public Halfedge getForwardHalfedge( )
	{
		return m_forwardHalfedge;
	}
	
	public Halfedge getReverseHalfedge( )
	{
		return m_reverseHalfedge;
	}
	
	public Halfedge getHalfedgeBySource( Vertex vertex )
	{
		if( m_forwardHalfedge.getSource().equals( vertex ) )
		{
			return m_forwardHalfedge;
		}
		else if( m_reverseHalfedge.getSource().equals( vertex ) )
		{
			return m_reverseHalfedge;
		}
		return null;
	}


	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public int hashCode( )
	{
		return m_arc.hashCode();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof Edge )
		{
			return equals( (Edge)other );
		}
		return false;
	}
	
	public boolean equals( Edge other )
	{
		// don't care about endpoint order
		return m_arc.equals( other.m_arc );
	}
}
