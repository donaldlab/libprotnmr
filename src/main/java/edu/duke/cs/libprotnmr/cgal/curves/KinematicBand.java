package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;

import java.io.Serializable;

public class KinematicBand implements Band, Serializable
{
	private static final long serialVersionUID = 9173855720491467988L;
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private Vector3 m_axis;
	private double m_theta;
	private double m_dTheta;
	private Matrix3 m_rotConeToMol;
	private Matrix3 m_rotMolToCone;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public KinematicBand( Vector3 axis, double theta, double dTheta )
	{
		init( axis, theta, dTheta );
	}
	
	public KinematicBand( Vector3 axis, Vector3 zeroDirection, Vector3 ninetyDirection, double theta, double dTheta )
	{
		m_axis = axis;
		m_theta = theta;
		m_dTheta = dTheta;
		computeRotations( zeroDirection, ninetyDirection );
	}
	
	public KinematicBand( KinematicBand other )
	{
		m_axis = new Vector3( other.m_axis );
		m_theta = other.m_theta;
		m_dTheta = other.m_dTheta;
		m_rotConeToMol = new Matrix3( other.m_rotConeToMol );
		m_rotMolToCone = new Matrix3( other.m_rotMolToCone );
	}
	
	protected KinematicBand( )
	{
		// no arg constructor so subclasses can do constructor argument transformation
	}
	
	protected void init( Vector3 axis, double theta, double dTheta )
	{
		m_axis = axis;
		m_theta = theta;
		m_dTheta = dTheta;
		computeArbitraryRotations();
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public Vector3 getAxis( )
	{
		return m_axis;
	}
	
	public double getTheta( )
	{
		return m_theta;
	}
	public void setTheta( double val )
	{
		m_theta = val;
	}
	
	public double getDTheta( )
	{
		return m_dTheta;
	}
	public void setDTheta( double val )
	{
		m_dTheta = val;
	}
	
	public double getMinTheta( )
	{
		return Math.max( 0, m_theta - m_dTheta );
	}
	
	public double getMaxTheta( )
	{
		return Math.min( Math.PI, m_theta + m_dTheta );
	}
	
	public Matrix3 getRotConeToMol( )
	{
		return m_rotConeToMol;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	public CircularCurve getCurve( BandPart part )
	{
		return new CircularCurve( m_rotConeToMol, part.getTheta( this ) );
	}
	
	public double getDihedral( Vector3 v )
	{
		Vector3 copy = new Vector3();
		copy.set( v );
		m_rotMolToCone.multiply( copy );
		return Math.atan2( copy.y, copy.x );
	}
	
	public void setOrientationFromDihedral( Vector3 out, double angle )
	{
		out.set(
			Math.cos( angle ) * Math.sin( m_theta ),
			Math.sin( angle ) * Math.sin( m_theta ),
			Math.cos( m_theta )
		);
		m_rotConeToMol.multiply( out );
	}
	
	@Override
	public boolean containsPoint( Vector3 point )
	{
		// is the angle between these two vectors in the range of the cone?
		double angle = Math.acos( point.getDot( m_axis ) );
		return CompareReal.gte( angle, m_theta - m_dTheta ) && CompareReal.lte( angle, m_theta + m_dTheta );
	}
	
	@Override
	public boolean boundaryContainsPoint( Vector3 point )
	{
		double angle = Math.acos( point.getDot( m_axis ) );
		return CompareReal.eq( angle, m_theta - m_dTheta ) || CompareReal.eq( angle, m_theta + m_dTheta );
	}
	
	@Override
	public boolean hasCurveOnBoundary( Curve curve )
	{
		return getCurve( BandPart.Max ).equals( curve )
			|| getCurve( BandPart.Min ).equals( curve );
	}
	
	
	/*********************************
	 *   Functions
	 *********************************/
	
	private void computeArbitraryRotations( )
	{
		m_rotConeToMol = new Matrix3();
		Matrix3.getArbitraryBasisFromZ( m_rotConeToMol, m_axis );
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
	}
	
	private void computeRotations( Vector3 zeroDirection, Vector3 ninetyDirection )
	{
		// compute the rotation from the kinematic circle to "cone space"
		
		// first build the x and y axes of the basis
		Vector3 x = new Vector3( zeroDirection );
		x.orthogonalProjection( m_axis );
		x.normalize();
		Vector3 y = new Vector3( ninetyDirection );
		y.orthogonalProjection( m_axis );
		y.normalize();
		
		if( !CompareReal.eq( x.getDot( y ), 0 ) )
		{
			throw new IllegalArgumentException( "Zero and Ninety points must actually be 90 degrees apart in dihedral space!" );
		}
		
		// build the rotations
		m_rotConeToMol = new Matrix3();
		m_rotConeToMol.setColumns( x, y, m_axis );
		m_rotMolToCone = new Matrix3( m_rotConeToMol );
		m_rotMolToCone.transpose();
	}
}
