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

import java.util.Iterator;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector3;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class RotationOptimizer
{
	/**************************
	 *   Methods
	 **************************/
	
	public static Quaternion getOptimalRotation( List<Vector3> reference, List<Vector3> computed )
	{
		// just in case...
		assert( reference.size() == computed.size() );
		
		return getOptimalRotation( reference.iterator(), computed.iterator() );
	}
	
	public static Quaternion getOptimalRotation( Iterator<Vector3> iterReference, Iterator<Vector3> iterComputed )
	{
		// compute that weird N matrix
		Matrix N = new Matrix( 4, 4, 0.0 );
		double axbx = 0.0;
		double axby = 0.0;
		double axbz = 0.0;
		double aybx = 0.0;
		double ayby = 0.0;
		double aybz = 0.0;
		double azbx = 0.0;
		double azby = 0.0;
		double azbz = 0.0;
		
		// for each atom pair (only for corresponding atoms)...
		while( iterReference.hasNext() && iterComputed.hasNext() )
		{
			// get our atom positions
			Vector3 b = iterReference.next();
			Vector3 a = iterComputed.next();
			
			// update N
			axbx = a.x * b.x;
			axby = a.x * b.y;
			axbz = a.x * b.z;
			aybx = a.y * b.x;
			ayby = a.y * b.y;
			aybz = a.y * b.z;
			azbx = a.z * b.x;
			azby = a.z * b.y;
			azbz = a.z * b.z;
			
			// NOTE: N appears to be symmetric. This could probably be optimized more
			N.set( 0, 0, N.get( 0, 0 ) + axbx + ayby + azbz );
			N.set( 0, 1, N.get( 0, 1 ) + aybz - azby );
			N.set( 0, 2, N.get( 0, 2 ) - axbz + azbx );
			N.set( 0, 3, N.get( 0, 3 ) + axby - aybx );
			
			N.set( 1, 0, N.get( 1, 0 ) - azby + aybz );
			N.set( 1, 1, N.get( 1, 1 ) + axbx - azbz - ayby );
			N.set( 1, 2, N.get( 1, 2 ) + axby + aybx );
			N.set( 1, 3, N.get( 1, 3 ) + axbz + azbx );
			
			N.set( 2, 0, N.get( 2, 0 ) + azbx - axbz );
			N.set( 2, 1, N.get( 2, 1 ) + aybx + axby );
			N.set( 2, 2, N.get( 2, 2 ) + ayby - azbz - axbx );
			N.set( 2, 3, N.get( 2, 3 ) + aybz + azby );
			
			N.set( 3, 0, N.get( 3, 0 ) - aybx + axby );
			N.set( 3, 1, N.get( 3, 1 ) + azbx + axbz );
			N.set( 3, 2, N.get( 3, 2 ) + azby + aybz );
			N.set( 3, 3, N.get( 3, 3 ) + azbz - ayby - axbx );
		}
		
		// just in case...
		assert( iterReference.hasNext() == iterComputed.hasNext() );
		
		// find the largest eigenvalue of N and the corresponding eigenvector
		EigenvalueDecomposition decomp = N.eig();
		Matrix eigenValues = decomp.getD();
		Matrix eigenVectors = decomp.getV();
		double maxEigenValue = Double.NEGATIVE_INFINITY;
		Matrix maxEigenVector = null;
		for( int i=0; i<4; i++ )
		{
			double eigenValue = eigenValues.get( i, i );
			if( eigenValue > maxEigenValue )
			{
				maxEigenValue = eigenValue;
				maxEigenVector = eigenVectors.getMatrix( 0, 3, i, i );
			}
		}
		
		// get our quaternion from the max eigen vector
		Quaternion optimalRotation = new Quaternion();
		optimalRotation.set(
			maxEigenVector.get( 0, 0 ),
			maxEigenVector.get( 1, 0 ),
			maxEigenVector.get( 2, 0 ),
			maxEigenVector.get( 3, 0 )
		);
		
		return optimalRotation;
	}
}
