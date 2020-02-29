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
package edu.duke.cs.libprotnmr.analysis;

import java.util.Collection;
import java.util.LinkedList;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Protein;


public class VarianceCalculator
{
	/**************************
	 *   Definitions
	 **************************/
	
	public static enum Metric
	{
		Variance
		{
			@Override
			public double evaluate( Collection<Vector3> points )
			{
				// compute the geometric mean
				Vector3 mean = Vector3.getOrigin();
				for( Vector3 pos : points )
				{
					mean.add( pos );
				}
				mean.scale( 1.0 / points.size() );
				
				// compute the average squared deviation from the mean
				double sum = 0.0;
				for( Vector3 pos : points )
				{
					sum += pos.getSquaredDistance( mean );
				}
				return sum / (double) points.size();
			}
		},
		Rmsd
		{
			@Override
			public double evaluate( Collection<Vector3> points )
			{
				return Math.sqrt( Variance.evaluate( points ) );
			}
		};
		
		public abstract double evaluate( Collection<Vector3> points );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getAverageRmsd( Collection<Protein> proteins )
	{
		return getAverageMetric( proteins, Metric.Rmsd, getFirstProtein( proteins ).atoms() );
	}
	
	public static double getAverageVariance( Collection<Protein> proteins )
	{
		return getAverageMetric( proteins, Metric.Variance, getFirstProtein( proteins ).atoms() );
	}
	
	public static double getAverageBackboneRmsd( Collection<Protein> proteins )
	{
		return getAverageMetric( proteins, Metric.Rmsd, getFirstProtein( proteins ).backboneAtoms() );
	}
	
	public static double getAverageBackboneVariance( Collection<Protein> proteins )
	{
		return getAverageMetric( proteins, Metric.Variance, getFirstProtein( proteins ).backboneAtoms() );
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static double getAverageMetric( Collection<Protein> proteins, Metric metric, Iterable<AtomAddressInternal> addresses )
	{
		double sum = 0.0;
		int count = 0;
		
		for( AtomAddressInternal address : addresses )
		{
			// collect the atom positions into a list
			LinkedList<Vector3> atomPositions = new LinkedList<Vector3>();
			for( Protein protein : proteins )
			{
				if( protein == null )
				{
					continue;
				}
				atomPositions.add( protein.getAtom( address ).getPosition() );
			}
			
			sum += metric.evaluate( atomPositions );
			count++;
		}
		
		// return the average
		return sum / (double)count;
	}
	
	private static Protein getFirstProtein( Collection<Protein> proteins )
	{
		for( Protein protein : proteins )
		{
			if( protein != null )
			{
				return protein;
			}
		}
		return null;
	}
}
