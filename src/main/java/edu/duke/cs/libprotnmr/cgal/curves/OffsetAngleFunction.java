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
