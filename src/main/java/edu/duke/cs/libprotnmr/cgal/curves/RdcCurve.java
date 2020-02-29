package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;

import java.io.Serializable;

public class RdcCurve extends ParametricCurve implements Serializable
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	private static final long serialVersionUID = 2437784719361998359L;
	
	public static int NumArcs = 2;
	
	private static enum SymmetryAxis
	{
		Z
		{
			@Override
			public void getPointAndDerivatives( Vector3 outPoint, Vector3 outDerivative, Vector3 outSecondDerivative,
				double A, double B, double C, double d, double arcFactor, double theta )
			{
				double cos = Math.cos( theta );
				double sin = Math.sin( theta );
				
				double cx = Math.sqrt( (d-C)/(A-C) );
				double cy = Math.sqrt( (d-C)/(B-C) );
				double x = cx*cos;
				double y = cy*sin;
				double z = liftToSphere( x, y, arcFactor );
				
				if( outPoint != null )
				{
					outPoint.set( x, y, z );
					assert( outPoint.isValid() );
				}
				if( outDerivative == null && outSecondDerivative == null )
				{
					return;
				}
				
				double dx = cx*-sin;
				double dy = cy*cos;
				double dz = -( x*dx + y*dy )/z;
				
				if( outDerivative != null )
				{
					outDerivative.set( dx, dy, dz );
				}
				if( outSecondDerivative == null )
				{
					return;
				}
				
				double d2x = cx*-cos;
				double d2y = cy*-sin;
				double d2z = -( dx*dx + x*d2x + dy*dy + y*d2y + dz*dz )/z;
				
				outSecondDerivative.set( d2x, d2y, d2z );
			}
			
			@Override
			public double getDot( Vector3 p )
			{
				return p.z;
			}
			
			@Override
			public void getAxis( Vector3 out )
			{
				Vector3.getUnitZ( out );
			}
			
			@Override
			public void setRotMolToQuasiPof( Matrix3 rot, AlignmentTensor tensor )
			{
				rot.setColumns(
					tensor.getXAxis(),
					tensor.getYAxis(),
					tensor.getZAxis()
				);
				rot.transpose();
			}
		},
		Y
		{
			@Override
			public void getPointAndDerivatives( Vector3 outPoint, Vector3 outDerivative, Vector3 outSecondDerivative,
				double A, double B, double C, double d, double arcFactor, double theta )
			{
				double cos = Math.cos( theta );
				double sin = Math.sin( theta );
				
				double cx = Math.sqrt( (d-B)/(A-B) );
				double cz = Math.sqrt( (d-B)/(C-B) );
				double x = cx*cos;
				double z = cz*sin;
				double y = liftToSphere( x, z, arcFactor );
				
				if( outPoint != null )
				{
					outPoint.set( x, y, z );
					assert( outPoint.isValid() );
				}
				if( outDerivative == null && outSecondDerivative == null )
				{
					return;
				}
				
				double dx = cx*-sin;
				double dz = cz*cos;
				double dy = -( x*dx + z*dz )/y;
				
				if( outDerivative != null )
				{
					outDerivative.set( dx, dy, dz );
				}
				if( outSecondDerivative == null )
				{
					return;
				}
				
				double d2x = cx*-cos;
				double d2z = cz*-sin;
				double d2y = -( dx*dx + x*d2x + dz*dz + z*d2z + dy*dy )/y;
				
				outSecondDerivative.set( d2x, d2y, d2z );
			}
			
			@Override
			public double getDot( Vector3 p )
			{
				return p.y;
			}
			
			@Override
			public void getAxis( Vector3 out )
			{
				Vector3.getUnitY( out );
			}
			
			@Override
			public void setRotMolToQuasiPof( Matrix3 rot, AlignmentTensor tensor )
			{
				rot.setColumns(
					tensor.getXAxis(),
					tensor.getZAxis(),
					tensor.getYAxis()
				);
				rot.transpose();
			}
		};
		
		public abstract void getPointAndDerivatives( Vector3 outPoint, Vector3 outDerivative, Vector3 outSecondDerivative,
			double A, double B, double C, double d, double arcFactor, double theta );
		public abstract double getDot( Vector3 p );
		public abstract void getAxis( Vector3 out );
		public abstract void setRotMolToQuasiPof( Matrix3 rot, AlignmentTensor tensor );
		
		private static double liftToSphere( double x, double y, double arcFactor )
		{
			double f = 1.0 - x*x - y*y;
			if( f < 0 )
			{
				f = 0;
			}
			return Math.sqrt( f ) * arcFactor;
		}
	}
	

	/*********************************
	 *   Data Members
	 *********************************/
	
	private AlignmentTensor m_tensor;
	private double m_d;
	private int m_arcnum;
	private SymmetryAxis m_axis;
	private Matrix3 m_rotPofToMol;
	private Matrix3 m_rotMolToPof;
	private Matrix3 m_rotMolToQuasiPof; // quasi since the z axis might actually be the y axis of the real pof
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public RdcCurve( AlignmentTensor tensor, double d, int arcnum )
	{
		// just in case...
		if( arcnum < 0 || arcnum > 1 )
		{
			throw new IllegalArgumentException( "arcnum must be 0 or 1." );
		}
		
		// make sure the RDC value is in range
		if( !tensor.isRdcInRange( d ) )
		{
			throw new IllegalArgumentException( "Rdc value is not in range of the tensor!" );
		}
		
		m_tensor = tensor;
		m_d = d;
		m_arcnum = arcnum;
		
		m_rotPofToMol = new Matrix3();
		tensor.getRotPofToMol( m_rotPofToMol );
		m_rotMolToPof = new Matrix3();
		tensor.getRotMolToPof( m_rotMolToPof );
		
		// figure out which symmetry axis we should use
		m_rotMolToQuasiPof = new Matrix3();
		if( m_tensor.getDzz() > 0 )
		{
			m_axis = m_d >= m_tensor.getDxx() ? SymmetryAxis.Z : SymmetryAxis.Y;
		}
		else
		{
			m_axis = m_d <= m_tensor.getDxx() ? SymmetryAxis.Z : SymmetryAxis.Y;
		}
		m_axis.setRotMolToQuasiPof( m_rotMolToQuasiPof, m_tensor );
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public AlignmentTensor getTensor( )
	{
		return m_tensor;
	}
	
	public double getD( )
	{
		return m_d;
	}
	
	public int getArcnum( )
	{
		return m_arcnum;
	}
	
	public void getAxis( Vector3 out )
	{
		m_axis.getAxis( out );
		m_rotPofToMol.multiply( out );
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getAngle( Vector3 p )
	{
		// PRECONDITION: p is on the curve
		assert( containsPoint( p ) );
		
		Vector3 copy = new Vector3( p );
		m_rotMolToQuasiPof.multiply( copy );
		return Math.atan2( copy.y, copy.x );
	}
	
	@Override
	public Vector3 getPoint( double angle )
	{
		double dx = m_tensor.getDxx();
		double dy = m_tensor.getDyy();
		double dz = m_tensor.getDzz();
		double arcFactor = m_arcnum * 2 - 1;
		Vector3 p = new Vector3();
		m_axis.getPointAndDerivatives( p, null, null, dx, dy, dz, m_d, arcFactor, angle );
		
		// rotate the point into the molecular frame
		m_rotPofToMol.multiply( p );
		
		return p;
	}
	
	@Override
	public Vector3 getDerivative( double angle )
	{
		double dx = m_tensor.getDxx();
		double dy = m_tensor.getDyy();
		double dz = m_tensor.getDzz();
		double arcFactor = m_arcnum * 2 - 1;
		Vector3 out = new Vector3();
		m_axis.getPointAndDerivatives( null, out, null, dx, dy, dz, m_d, arcFactor, angle );
		
		// make sure the derivative actually exists (it won't for theta=0,pi and d = Dxx)
		if( Double.isNaN( out.x ) || Double.isNaN( out.y ) || Double.isNaN( out.z ) )
		{
			return null;
		}
		
		// rotate the point into the molecular frame
		m_rotPofToMol.multiply( out );
		
		// the derivative should be perpendicular to the point!
		assert( CompareReal.eq( getPoint( angle ).getDot( out ), 0.0 ) );
		
		return out;
	}
	
	public Vector3 getSecondDerivative( double angle )
	{
		double dx = m_tensor.getDxx();
		double dy = m_tensor.getDyy();
		double dz = m_tensor.getDzz();
		double arcFactor = m_arcnum * 2 - 1;
		Vector3 out = new Vector3();
		m_axis.getPointAndDerivatives( null, null, out, dx, dy, dz, m_d, arcFactor, angle );
		
		// make sure the second derivative actually exists
		if( Double.isNaN( out.x ) || Double.isNaN( out.y ) || Double.isNaN( out.z ) )
		{
			return null;
		}
		
		// rotate the point into the molecular frame
		m_rotPofToMol.multiply( out );
		
		return out;
	}
	
	public boolean getPointAndDerivatives( Vector3 outPoint, Vector3 outDerivative, Vector3 outSecondDerivative, double angle )
	{
		double dx = m_tensor.getDxx();
		double dy = m_tensor.getDyy();
		double dz = m_tensor.getDzz();
		double arcFactor = m_arcnum * 2 - 1;
		m_axis.getPointAndDerivatives( outPoint, outDerivative, outSecondDerivative, dx, dy, dz, m_d, arcFactor, angle );
		
		// rotate the points into the molecular frame
		if( outPoint != null )
		{
			m_rotPofToMol.multiply( outPoint );
		}
		if( outDerivative != null )
		{
			// make sure the derivative exists
			if( Double.isNaN( outDerivative.x ) || Double.isNaN( outDerivative.y ) || Double.isNaN( outDerivative.z ) )
			{
				return false;
			}
			m_rotPofToMol.multiply( outDerivative );
		}
		if( outSecondDerivative != null )
		{
			// make sure the second derivative exists
			if( Double.isNaN( outSecondDerivative.x ) || Double.isNaN( outSecondDerivative.y ) || Double.isNaN( outSecondDerivative.z ) )
			{
				return false;
			}
			m_rotPofToMol.multiply( outSecondDerivative );
		}
		return true;
	}
	
	public double getRdcValue( Vector3 p )
	{
		return m_tensor.backComputeRdc( p );
	}
	
	@Override
	public boolean containsPoint( Vector3 p, double epsilon )
	{
		if( !CompareReal.eq( p.getSquaredLength(), 1.0, epsilon ) || !CompareReal.eq( m_tensor.backComputeRdc( p ), m_d, epsilon ) )
		{
			return false;
		}
		
		// check the arcnum (rotate point into the pof)
		p = new Vector3( p );
		m_rotMolToPof.multiply( p );
		if( m_axis.getDot( p ) > 0.0 )
		{
			return m_arcnum == 1;
		}
		else
		{
			return m_arcnum == 0;
		}
	}
	
	@Override
	public boolean hasLength( )
	{
		return !CompareReal.eq( m_d, m_tensor.getDzz() ) && !CompareReal.eq( m_d, m_tensor.getDyy() );
	}
	
	public RdcCurveArc[] split( Vector3 a, Vector3 b )
	{
		RdcCurveArc[] arcs = new RdcCurveArc[2];
		arcs[0] = new RdcCurveArc( this, a, b );
		arcs[1] = new RdcCurveArc( this, b, a );
		return arcs;
	}
	
	@Override
	public RdcCurveArc newClosedArc( )
	{
		return new RdcCurveArc( this );
	}
	
	@Override
	public RdcCurveArc newClosedArc( Vector3 p )
	{
		return new RdcCurveArc( this, p );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			m_tensor.hashCode(),
			Double.valueOf( m_d ).hashCode(),
			m_arcnum + 1
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof RdcCurve )
		{
			return equals( (RdcCurve)other );
		}
		return false;
	}
	
	public boolean equals( RdcCurve other )
	{
		return m_tensor.equals( other.m_tensor )
			&& m_d == other.m_d
			&& m_arcnum == other.m_arcnum;
	}
}
