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

package edu.duke.cs.libprotnmr.geom;

import java.util.ArrayList;

import edu.duke.cs.libprotnmr.math.CompareReal;


public class CircularArc
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static enum State
	{
		Top,
		Bottom,
		Degenerate;
		
		public static State lookup( double angle )
		{
			if( angle == 0.0 || CompareReal.eq( angle, Math.PI ) )
			{
				return Degenerate;
			}
			else if( angle > 0.0 )
			{
				return Top;
			}
			else
			{
				return Bottom;
			}
		}
	}
	
	
	/**************************
	 *   Fields
	 **************************/
	
	public Circle circle;
	public double thetaStart;
	public double thetaStop;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public CircularArc( Circle circle, double thetaStart, double thetaStop )
	{
		set( circle, thetaStart, thetaStop );
	}
	
	public CircularArc( Circle circle, Vector2 start, Vector2 stop )
	{
		set( circle, start, stop );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void set( Circle circle, double thetaStart, double thetaStop )
	{
		thetaStart = normalize( thetaStart );
		thetaStop = normalize( thetaStop );
		
		this.circle = circle;
		this.thetaStart = thetaStart;
		this.thetaStop = thetaStop;
		
		// check the arc for x-monotonicity
		if( !isXMonotone() )
		{
			// attempt to fix epsilon-close cases
			fixXMonotonicity();
			
			if( !isXMonotone() )
			{
				throw new IllegalArgumentException( "This implementation requires all circular arcs to be x-monotone." );
			}
		}
	}
	
	public void set( Circle circle, Vector2 start, Vector2 stop )
	{
		set(
			circle,
			Math.atan2( start.y - circle.center.y, start.x - circle.center.x ),
			Math.atan2( stop.y - circle.center.y, stop.x - circle.center.x )
		);
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public Vector2 getStart( )
	{
		return new Vector2(
			circle.center.x + Math.cos( thetaStart ) * circle.radius,
			circle.center.y + Math.sin( thetaStart ) * circle.radius
		);
	}
	
	public Vector2 getStop( )
	{
		return new Vector2(
			circle.center.x + Math.cos( thetaStop ) * circle.radius,
			circle.center.y + Math.sin( thetaStop ) * circle.radius
		);
	}
	
	public State getState( )
	{
		State stateStart = State.lookup( thetaStart );
		State stateStop = State.lookup( thetaStop );
		
		if( stateStart == State.Degenerate && stateStop == State.Degenerate )
		{
			return State.Degenerate;
		}
		else if( stateStart != State.Degenerate )
		{
			return stateStart;
		}
		else
		{
			return stateStop;
		}
	}

	public Vector2 getPointFarthestFrom( Vector2 q )
	{
		double maxDistSq = 0.0;
		Vector2 farthestPoint = null;
		for( Vector2 point : getCheckPoints( q ) )
		{
			double distSq = point.getSquaredDistance( q );
			if( distSq > maxDistSq )
			{
				maxDistSq = distSq;
				farthestPoint = point;
			}
		}
		return farthestPoint;
	}

	public Vector2 getPointClosestTo( Vector2 q )
	{
		double minDistSq = Double.POSITIVE_INFINITY;
		Vector2 closestPoint = null;
		for( Vector2 point : getCheckPoints( q ) )
		{
			double distSq = point.getSquaredDistance( q );
			if( distSq < minDistSq )
			{
				minDistSq = distSq;
				closestPoint = point;
			}
		}
		return closestPoint;
	}
	
	public boolean isInWedge( Vector2 q )
	{
		return isInWedge( q.x, q.y );
	}
	
	public boolean isInWedge( double x, double y )
	{
		double thetaQ = Math.atan2( y - circle.center.y, x - circle.center.x );
		
		if( thetaStart == thetaStop )
		{
			return thetaQ == thetaStart;
		}
		
		return thetaQ >= thetaStart && thetaQ <= thetaStop;
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof CircularArc )
		{
			return equals( (CircularArc)other );
		}
		
		return false;
	}
	
	public boolean equals( CircularArc other )
	{
		if( !circle.equals( other.circle ) )
		{
			return false;
		}
		
		// NOTE: Circular arcs are not directed, so the order of the endpoints is not important
		return ( thetaStart == other.thetaStart && thetaStop == other.thetaStop )
			|| ( thetaStart == other.thetaStop && thetaStop == other.thetaStart );
	}
	
	@Override
	public String toString( )
	{
		return circle.toString() + ", " + thetaStart + ", " + thetaStop;
	}
	
	public boolean isAdjacentTo( CircularArc other )
	{
		double myStartX = circle.center.x + Math.cos( thetaStart ) * circle.radius;
		double myStartY = circle.center.y + Math.sin( thetaStart ) * circle.radius;
		double myStopX = circle.center.x + Math.cos( thetaStop ) * circle.radius;
		double myStopY = circle.center.y + Math.sin( thetaStop ) * circle.radius;
		
		double otherStartX = other.circle.center.x + Math.cos( other.thetaStart ) * other.circle.radius;
		double otherStartY = other.circle.center.y + Math.sin( other.thetaStart ) * other.circle.radius;
		double otherStopX = other.circle.center.x + Math.cos( other.thetaStop ) * other.circle.radius;
		double otherStopY = other.circle.center.y + Math.sin( other.thetaStop ) * other.circle.radius;
		
		// use a very small epsilon here!
		final double Epsilon = 1e-12;
		
		// NOTE: Circular arcs are not directed, so the order of the endpoints is not important
		return ( CompareReal.eq( myStartX, otherStopX, Epsilon ) && CompareReal.eq( myStartY, otherStopY, Epsilon ) )
			|| ( CompareReal.eq( myStopX, otherStartX, Epsilon ) && CompareReal.eq( myStopY, otherStartY, Epsilon ) );
	}
	
	public Vector2 getVerticalLineIntersection( double x )
	{
		// skip lines that don't intersect the circle
		double dx = Math.abs( x - circle.center.x );
		if( dx > circle.radius )
		{
			return null;
		}
		
		// get the y coord of the circle/line intersection point
		double dy = Math.sqrt( circle.radius * circle.radius - dx * dx );
		double y = circle.center.y;
		switch( getState() )
		{
			case Top:
				y += dy;
			break;
			
			case Bottom:
				y -= dy;
			break;
			
			case Degenerate:
				break;
		}
		
		// skip lines that don't intersect the arc
		if( !isInWedge( x, y ) )
		{
			return null;
		}
		
		return new Vector2( x, y );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private double normalize( double theta )
	{
		// normalize angles to the interval [-pi,pi]
		while( theta < -Math.PI )
		{
			theta += 2.0 * Math.PI;
		}
		
		while( theta > Math.PI )
		{
			theta -= 2.0 * Math.PI;
		}
		
		return theta;
	}
	
	private boolean isXMonotone( )
	{
		if( Math.signum( thetaStart ) == 0.0 )
		{
			return true;
		}
		
		if( Math.signum( thetaStop ) == 0.0 )
		{
			return true;
		}
		
		return Math.signum( thetaStart ) == Math.signum( thetaStop );
	}
	
	private void fixXMonotonicity( )
	{
		/* HACKHACK:
			If one theta value is near the "seam" at PI radians,
			just change its sign to match the other theta value.
		*/
		if( CompareReal.eq( Math.abs( thetaStart ), Math.PI ) )
		{
			thetaStart = Math.copySign( thetaStart, thetaStop );
		}
		else if( CompareReal.eq( Math.abs( thetaStop ), Math.PI ) )
		{
			thetaStop = Math.copySign( thetaStop, thetaStart );
		}
	}
	
	private ArrayList<Vector2> getCheckPoints( Vector2 q )
	{
		ArrayList<Vector2> points = new ArrayList<Vector2>();
		
		// always check the arc endpoints
		points.add( getStart() );
		points.add( getStop() );
		
		// check the point on the circle if needed
		Vector2 circlePoint = circle.getPointClosestTo( q );
		if( isInWedge( circlePoint ) )
		{
			points.add( circlePoint );
		}
		
		return points;
	}
}
