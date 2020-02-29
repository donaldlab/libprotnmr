package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;


public class OffsetAngleFunction extends PreconditionedFunction implements Serializable
{
	private static final long serialVersionUID = -1682732175925875923L;
	private static final Logger m_log = LogManager.getLogger(OffsetAngleFunction.class);
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private RdcCurve m_rdcCurve;
	private Vector3 m_point;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public OffsetAngleFunction( RdcCurve rdcCurve, Vector3 point )
	{
		super( 8 );
		m_rdcCurve = rdcCurve;
		m_point = point;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public RdcCurve getRdcCurve( )
	{
		return m_rdcCurve;
	}
	
	public Vector3 getPoint( )
	{
		return m_point;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getUnconditionedValue( double theta )
	{
		Vector3 d = m_rdcCurve.getDerivative( theta );
		double l = d.getLength();
		return d.getDot( m_point )/l;
	}
	
	@Override
	public double getUnconditionedDerivative( double theta )
	{
		Vector3 d = m_rdcCurve.getDerivative( theta );
		Vector3 e = m_rdcCurve.getSecondDerivative( theta );
		double l = d.getLength();
		double dl = d.getDot( e )/l;
		return ( l*e.getDot( m_point ) - dl*d.getDot( m_point ) )/l/l;
	}
	
	public void save( File file )
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
			out.writeObject( this );
			out.close();
			m_log.info( "Wrote function out to: " + file.getAbsolutePath() );
		}
		catch( IOException ex )
		{
			m_log.error( "Unable to save function", ex );
		}
	}
}
