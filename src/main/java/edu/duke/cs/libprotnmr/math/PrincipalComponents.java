/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.math;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.geom.Vector3;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class PrincipalComponents
{
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static TreeMap<Double,Vector3> getPrincipalComponents( List<Vector3> points )
	{
		// compute the means
		double[] means = new double[3];
		for( int i=0; i<3; i++ )
		{
			means[i] = 0;
			for( Vector3 p : points )
			{
				means[i] += p.get( i );
			}
			means[i] /= points.size();
		}
		
		// compute the covariance matrix
		// see following URL for mathematical definition
		// http://en.wikipedia.org/wiki/Covariance_matrix
		Matrix cov = new Matrix( 3, 3 );
		for( int i=0; i<3; i++ )
		{
			for( int j=0; j<=i; j++ )
			{
				double c = 0;
				for( Vector3 p : points )
				{
					c += ( p.get( i ) - means[i] )*( p.get( j ) - means[j] );
				}
				c /= points.size();
				
				cov.set( i, j, c );
				cov.set( j, i, c );
			}
		}
		
		// put the eigenthings in a map
		EigenvalueDecomposition eig = cov.eig();
		TreeMap<Double,Vector3> principalComponents = new TreeMap<Double,Vector3>( new Comparator<Double>( )
		{
			@Override
			public int compare( Double a, Double b )
			{
				// we must compare eigenvalues by magnitude only
				return Double.compare( Math.abs( a ), Math.abs( b ) );
			}
		} );
		for( int i=0; i<3; i++ )
		{
			double eigenvalue = eig.getD().get( i, i );
			Vector3 eigenvector = new Vector3(
				eig.getV().get( 0, i ),
				eig.getV().get( 1, i ),
				eig.getV().get( 2, i )
			);
			principalComponents.put( eigenvalue, eigenvector );
		}
		return principalComponents;
	}
}
