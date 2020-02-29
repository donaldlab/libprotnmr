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

import java.util.List;

import edu.duke.cs.libprotnmr.math.CompareReal;


/*
 * Copyright (c) 2003-2007 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// Adapted for use in this project by Jeff Martin, 2012
// source obtained from:
// http://www.cs.miami.edu/60_Courses/75_UG_Project_Archives/_20082_CSC329_Group5/LuxGame/jme/src/com/jme/bounding/BoundingSphere.java


public class WelzlSphereSolver
{
	/**************************
	 *   Data Members
	 **************************/
	
	private List<Vector3> m_points;
	private double m_radius;
	private Vector3 m_center;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private WelzlSphereSolver( List<Vector3> points )
	{
		m_points = points;
		m_radius = 0.0;
		m_center = new Vector3();
		
        recurseMini( m_points.size(), 0 );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static Sphere getSphere( List<Vector3> points )
	{
		WelzlSphereSolver solver = new WelzlSphereSolver( points );
		return new Sphere( solver.m_center, solver.m_radius );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
    private void recurseMini( int totalNumPoints, int numPointsConsidered )
    {
        switch( numPointsConsidered )
        {
	        case 0:
	            m_radius = 0.0;
	            m_center.set( 0.0, 0.0, 0.0 );
	            break;
	        case 1:
	            m_radius = 0.0;
	            m_center.set( m_points.get( numPointsConsidered - 1 ) );
	            break;
	        case 2:
	            setSphere( m_points.get( numPointsConsidered - 1 ), m_points.get( numPointsConsidered - 2 ) );
	            break;
	        case 3:
	            setSphere( m_points.get( numPointsConsidered - 1 ), m_points.get( numPointsConsidered - 2 ), m_points.get( numPointsConsidered - 3 ) );
	            break;
	        case 4:
	            setSphere( m_points.get( numPointsConsidered - 1 ), m_points.get( numPointsConsidered - 2 ), m_points.get( numPointsConsidered - 3 ), m_points.get( numPointsConsidered - 4 ) );
	            return;
        }
        for( int i=0; i<totalNumPoints; i++ )
        {
        	Vector3 point = m_points.get( i + numPointsConsidered );
            if( !CompareReal.lte( point.getSquaredDistance( m_center ), ( m_radius * m_radius ) ) )
            {
                for( int j=i; j>0; j-- )
                {
                	Vector3 tempB = m_points.get( j + numPointsConsidered );
                	Vector3 tempC = m_points.get( j - 1 + numPointsConsidered );
                	m_points.set( j + numPointsConsidered, tempC );
                	m_points.set( j - 1 + numPointsConsidered, tempB );
                }
                recurseMini( i, numPointsConsidered + 1 );
            }
        }
    }

    private void setSphere( Vector3 o, Vector3 a, Vector3 b, Vector3 c )
    {
    	a = new Vector3( a );
    	b = new Vector3( b );
    	c = new Vector3( c );
    	
    	a.subtract( o );
    	b.subtract( o );
    	c.subtract( o );
    	
        double denominator = 2.0 * ( a.x * ( b.y * c.z - c.y * b.z ) - b.x * ( a.y * c.z - c.y * a.z ) + c.x * ( a.y * b.z - b.y * a.z ) );
        if( denominator == 0 )
        {
            m_center.set( 0.0, 0.0, 0.0 );
            m_radius = 0.0;
        }
        else
        {
        	Vector3 tempA = new Vector3();
        	a.getCross( tempA, b );
        	tempA.scale( c.getSquaredLength() );
        	
        	Vector3 tempB = new Vector3();
        	c.getCross( tempB, a );
        	tempB.scale( b.getSquaredLength() );
        	
        	Vector3 tempC = new Vector3();
        	b.getCross( tempC, c );
        	tempC.scale( a.getSquaredLength() );
        	
        	Vector3 out = new Vector3();
        	out.add( tempA );
        	out.add( tempB );
        	out.add( tempC );
        	out.scale( 1.0 / denominator );
        	
            m_radius = out.getLength();
            m_center.set( o );
            m_center.add( out );
        }
    }

    private void setSphere( Vector3 o, Vector3 a, Vector3 b )
    {
    	a = new Vector3( a );
    	b = new Vector3( b );
    	
    	a.subtract( o );
    	b.subtract( o );
    	
        Vector3 acrossB = new Vector3();
        a.getCross( acrossB, b );

        double denominator = 2.0 * acrossB.getDot( acrossB );
        if( denominator == 0 )
        {
            m_center.set( 0.0, 0.0, 0.0 );
            m_radius = 0.0;
        }
        else
        {

        	Vector3 tempA = new Vector3();
        	acrossB.getCross( tempA, a );
        	tempA.scale( b.getSquaredLength() );
        	
        	Vector3 tempB = new Vector3();
        	b.getCross( tempB, acrossB );
        	tempB.scale( a.getSquaredLength() );
        	
        	Vector3 out = new Vector3();
        	out.add( tempA );
        	out.add( tempB );
        	out.scale( 1.0 / denominator );
        	
        	m_radius = out.getLength();
            m_center.set( o );
            m_center.add( out );
        }
    }
    
    private void setSphere( Vector3 o, Vector3 a )
    {
        m_radius = Math.sqrt( ( ( a.x - o.x )*( a.x - o.x ) + ( a.y - o.y )*( a.y - o.y ) + ( a.z - o.z )*( a.z - o.z ) )/4.0 );
        m_center.set( o );
        m_center.add( a );
        m_center.scale( 0.5 );
    }
}
