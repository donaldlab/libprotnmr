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
