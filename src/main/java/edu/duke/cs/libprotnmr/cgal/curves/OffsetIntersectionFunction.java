package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;


public class OffsetIntersectionFunction extends PreconditionedFunction implements Serializable
{
	private static final long serialVersionUID = 1981149713881524689L;
	private static final Logger m_log = LogManager.getLogger(OffsetIntersectionFunction.class);
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private RdcOffsetCurve m_rdcOffsetCurve;
	private CircularCurve m_circularCurve;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	public OffsetIntersectionFunction( RdcOffsetCurve rdcOffsetCurve, CircularCurve circularCurve )
	{
		super( 8 );
		m_rdcOffsetCurve = rdcOffsetCurve;
		m_circularCurve = circularCurve;
	}
	
	
	/*********************************
	 *   Accessors
	 *********************************/
	
	public RdcOffsetCurve getRdcOffsetCurve( )
	{
		return m_rdcOffsetCurve;
	}
	
	public CircularCurve getCircularCurve( )
	{
		return m_circularCurve;
	}
	
	
	/*********************************
	 *   Methods
	 *********************************/
	
	@Override
	public double getUnconditionedValue( double theta )
	{
		Vector3 value = m_rdcOffsetCurve.getPoint( theta );
		Vector3 n = m_circularCurve.getNormal();
		return value.getDot( n ) - m_circularCurve.getConeHeight();
	}
	
	@Override
	public double getUnconditionedDerivative( double theta )
	{
		Vector3 derivative = m_rdcOffsetCurve.getDerivative( theta );
		Vector3 n = m_circularCurve.getNormal();
		return derivative.getDot( n );
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
