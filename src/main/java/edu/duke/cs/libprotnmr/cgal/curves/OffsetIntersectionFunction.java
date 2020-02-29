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
