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

import java.util.List;

public class PolyGeodesicCurveArc extends PolyGeodesicCurve implements CurveArc
{
	public PolyGeodesicCurveArc( List<Vector3> vertices )
	{
		super( vertices );
	}
	
	@Override
	public Curve getCurve( )
	{
		return this;
	}
	
	@Override
	public boolean isClosed( )
	{
		return false;
	}
	
	@Override
	public Vector3 getSource( )
	{
		return getVertices().get( 0 );
	}

	@Override
	public Vector3 getTarget( )
	{
		return getVertices().get( getVertices().size() - 1 );
	}

	@Override
	public Vector3 getMidpoint( )
	{
		return getVertices().get( getVertices().size()/2 );
	}

	@Override
	public Vector3 getOtherEndpoint( Vector3 p )
	{
		if( p == getSource() )
		{
			return getTarget();
		}
		else if( p == getTarget() )
		{
			return getSource();
		}
		throw new IllegalArgumentException( "point is not an endpoint!" );
	}

	@Override
	public Vector3 getOtherEndpoint( Vector3 p, double epsilon )
	{
		if( p.approximatelyEquals( getSource(), epsilon ) )
		{
			return getTarget();
		}
		else if( p.approximatelyEquals( getTarget(), epsilon ) )
		{
			return getSource();
		}
		throw new IllegalArgumentException( "point is not approximately an endpoint!" );
	}

	@Override
	public boolean containsPointOnBoundary( Vector3 p )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsPointOnBoundary( Vector3 p, double epsilon )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsPointInInterior( Vector3 p )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsPointInInterior( Vector3 p, double epsilon )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}

	@Override
	public double getApproximateLength( int numSamples )
	{
		// UNDONE: lazy programmer
		throw new UnsupportedOperationException();
	}
}
