package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;

import java.io.Serializable;

public class CircularCurve extends ParametricCurve implements Serializable
{
	private static final long serialVersionUID = -4733965121203546476L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/

	private Vector3 m_normal;
	private double m_halfWidth;
	private double m_height;
	private Matrix3 m_rotConeToMol;
	private Matrix3 m_rotMolToCone;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public CircularCurve( Vector3 normal, double theta )
	{
		this( normal, Math.sin( theta ), Math.cos( theta ) );
	}
	
	public CircularCurve( Vector3 normal, Vector3 p )
	{
		// construct the unique circle containing p
		this( normal, Math.acos( normal.getDot( p ) ) );
	}
	
	public CircularCurve( Vector3 normal, double halfWidth, double height )
	{
		m_normal = normal;
		m_halfWidth = Math.abs( halfWidth );
		m_height = height;
		m_rotConeToMol = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( m_rotConeToMol, normal );
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
	}
	
	public CircularCurve( Matrix3 rotConeToMol, double theta )
	{
		this( rotConeToMol, Math.sin( theta ), Math.cos( theta ) );
	}
	
	public CircularCurve( Matrix3 rotConeToMol, double halfWidth, double height )
	{
		m_normal = new Vector3();
		rotConeToMol.getZAxis( m_normal );
		m_halfWidth = Math.abs( halfWidth );
		m_height = height;
		m_rotConeToMol = rotConeToMol;
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
	}
	
	public CircularCurve( Vector3 a, Vector3 b, Vector3 c )
	{
		// PRECONDITION: a, b, c all lie on the unit sphere
		assert( CompareReal.eq( a.getSquaredLength(), 1.0 ) );
		assert( CompareReal.eq( b.getSquaredLength(), 1.0 ) );
		assert( CompareReal.eq( c.getSquaredLength(), 1.0 ) );
		
		// PRECONDITION: a, b, c are all distinct
		final double Epsilon = 1e-12;
		assert( !a.approximatelyEquals( b, Epsilon ) );
		assert( !b.approximatelyEquals( c, Epsilon ) );
		assert( !c.approximatelyEquals( a, Epsilon ) );
		
		// compute the normal (always such that a,b,c are in counterclockwise order looking into the normal)
		Vector3 ba = new Vector3( a );
		ba.subtract( b );
		ba.normalize();
		Vector3 bc = new Vector3( c );
		bc.subtract( b );
		bc.normalize();
		m_normal = new Vector3();
		bc.getCross( m_normal, ba );
		m_normal.normalize();
		assert( m_normal.getSquaredLength() > 0.0 );
		
		// compute the cone parameters
		m_height = m_normal.getDot( a );
		m_halfWidth = Math.sqrt( 1.0 - m_height*m_height );
		
		m_rotConeToMol = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( m_rotConeToMol, m_normal );
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
		
		assert( containsPoint( a ) );
		assert( containsPoint( b ) );
		assert( containsPoint( c ) );
	}
	
	public CircularCurve( GeodesicCurve circle )
	{
		this( circle.getRotParamToMol(), 1.0, 0.0 );
	}
	
	public CircularCurve( CircularCurve other )
	{
		m_normal = new Vector3( other.m_normal );
		m_halfWidth = other.m_halfWidth;
		m_height = other.m_height;
		m_rotConeToMol = new Matrix3( other.m_rotConeToMol );
		m_rotMolToCone = new Matrix3( other.m_rotMolToCone );
	}
	

	/*********************************
	 *   Accessors
	 *********************************/
	
	public Matrix3 getRotConeToMol( )
	{
		return m_rotConeToMol;
	}
	
	public Matrix3 getRotMolToCone( )
	{
		return m_rotMolToCone;
	}
	
	public double getConeHeight( )
	{
		return m_height;
	}
	
	public double getConeHalfWidth( )
	{
		return m_halfWidth;
	}
	
	public double getConeTheta( )
	{
		return Math.atan2( m_halfWidth, m_height );
	}
	
	public void getCenter( Vector3 out )
	{
		out.set( m_normal );
		out.scale( m_height );
	}
	
	public double getRadius( )
	{
		return m_halfWidth;
	}
	
	public Vector3 getNormal( )
	{
		return m_normal;
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static CircularCurve getOrientedCircle( Vector3 normal, Vector3 zeroPoint, Vector3 ninetyPoint )
	{
		if( zeroPoint.approximatelyEquals( ninetyPoint ) )
		{
			throw new IllegalArgumentException( "zero and ninety points are too close!" );
		}
		
		double height = normal.getDot( zeroPoint );
		double halfwidth = Math.sqrt( 1.0 - height*height );
		
		Vector3 xAxis = new Vector3( zeroPoint );
		xAxis.orthogonalProjection( normal );
		xAxis.normalize();
		Vector3 yAxis = new Vector3( ninetyPoint );
		yAxis.orthogonalProjection( normal );
		yAxis.normalize();
		Matrix3 basis = new Matrix3();
		basis.setColumns( xAxis, yAxis, normal );
		
		return new CircularCurve( basis, halfwidth, height );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getAngle( Vector3 p )
	{
		// PRECONDITION: p is on the curve
		assert( containsPoint( p, 1e-4 ) );
		
		Vector3 copy = new Vector3( p );
		m_rotMolToCone.multiply( copy );
		return Math.atan2( copy.y, copy.x );
	}
	
	@Override
	public Vector3 getPoint( double angle )
	{
		Vector3 point = new Vector3();
		point.x = Math.cos( angle ) * m_halfWidth;
		point.y = Math.sin( angle ) * m_halfWidth;
		point.z = m_height;
		
		// rotate the point into the molecular frame
		m_rotConeToMol.multiply( point );
		
		return point;
	}
	
	@Override
	public Vector3 getDerivative( double angle )
	{
		Vector3 point = new Vector3();
		point.x = -Math.sin( angle ) * m_halfWidth;
		point.y = Math.cos( angle ) * m_halfWidth;
		point.z = 0.0;
		
		// rotate the point into the molecular frame
		m_rotConeToMol.multiply( point );
		
		return point;
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		return CompareReal.eq( p.getSquaredLength(), 1.0, epsilon ) && CompareReal.eq( p.getDot( m_normal ), m_height, epsilon );
	}
	
	@Override
	public boolean hasLength( )
	{
		return !CompareReal.eq( m_halfWidth, 0.0 );
	}
	
	@Override
	public CircularCurveArc newClosedArc( )
	{
		return new CircularCurveArc( this );
	}
	
	@Override
	public CircularCurveArc newClosedArc( Vector3 p )
	{
		return new CircularCurveArc( this, p );
	}
	
	public boolean enclosesPoint( Vector3 p )
	{
		return CompareReal.gte( p.getDot( m_normal ), m_height );
	}
	
	public CircularCurve getReverseCurve( )
	{
		Vector3 invertedNormal = new Vector3( m_normal );
		invertedNormal.negate();
		return new CircularCurve( invertedNormal, m_halfWidth, -m_height );
	}
	
	public CircularCurve getRotatedCurve( Quaternion q )
	{
		CircularCurve rotatedCurve = new CircularCurve( this );
		q.rotate( rotatedCurve.m_rotConeToMol );
		rotatedCurve.m_rotConeToMol.getZAxis( rotatedCurve.m_normal );
		m_rotMolToCone.set( m_rotConeToMol );
		m_rotMolToCone.transpose();
		return rotatedCurve;
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_normal.hashCode(),
			Double.valueOf( m_halfWidth ).hashCode(),
			Double.valueOf( m_height ).hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof CircularCurve )
		{
			return equals( (CircularCurve)other );
		}
		return false;
	}
	
	public boolean equals( CircularCurve other )
	{
		return m_normal.equals( other.m_normal )
			&& m_halfWidth == other.m_halfWidth
			&& m_height == other.m_height;
	}
}
