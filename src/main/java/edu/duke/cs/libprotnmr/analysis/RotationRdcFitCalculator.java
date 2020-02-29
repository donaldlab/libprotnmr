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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.duke.cs.libprotnmr.chart.Plotter;
import edu.duke.cs.libprotnmr.geom.GeodesicGrid;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.perf.Progress;
import edu.duke.cs.libprotnmr.perf.WorkCrew;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;

import org.jfree.chart.JFreeChart;


public class RotationRdcFitCalculator
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	private static class ScoredAxis
	{
		public Vector3 axis;
		public double minScore;
		public double maxScore;
		
		public ScoredAxis( Vector3 axis )
		{
			this.axis = axis;
			this.minScore = Double.POSITIVE_INFINITY;
			this.maxScore = Double.NEGATIVE_INFINITY;
		}
		
		public void addScore( double score )
		{
			minScore = Math.min( minScore, score );
			maxScore = Math.max( maxScore, score );
		}
		
		public double getScoreRange( )
		{
			return maxScore - minScore;
		}
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static ScoredAxis analyzeHelixOrientation( AlignmentTensor tensor, Subunit helix, List<Rdc<AtomAddressInternal>> rdcs, Vector3 newAxis, double axialRotationResolution )
	{
		// make a copies of the helix we can toy with
		Subunit helixToyA = new Subunit( helix );
		Subunit helixToyB = new Subunit( helix );
		
		// rotate the toy helical axis
		Vector3 axis = ProteinGeometry.getBackboneAxis( helix );
		Quaternion q = new Quaternion();
		Quaternion.getRotation( q, axis, newAxis );
		ProteinGeometry.rotate( helixToyA, helix, q );
		
		// aggregate scores for all the axial rotations
		int numSteps = (int)(2.0*Math.PI/axialRotationResolution);
		ScoredAxis scoredAxis = new ScoredAxis( axis );
		for( int i=0; i<numSteps; i++ )
		{
			double angle = Math.PI*2*i/numSteps;
			
			// rotate the toy helix about its axis
			Quaternion.getRotation( q, newAxis, angle );
			ProteinGeometry.rotate( helixToyB, helixToyA, q );
			
			// score the orientation
			scoredAxis.addScore( tensor.getQFactor( helixToyB, rdcs ) );
		}
		
		return scoredAxis;
	}
	
	public static List<ScoredAxis> analyzeHelixOrientations( AlignmentTensor tensor, Subunit helix, List<Rdc<AtomAddressInternal>> rdcs, int gridDepth, double axialRotationResolution )
	{
		// we'll assume that rotations about the helix axis don't change the fit much.
		// so the results will show primarily the effects of changing the helical axis
		// and aggregate over the remaining rotations about the axis.
		
		Vector3 axis = ProteinGeometry.getBackboneAxis( helix );
		
		// make a copies of the helix we can toy with
		Subunit helixToyA = new Subunit( helix );
		Subunit helixToyB = new Subunit( helix );
		
		Quaternion q = new Quaternion();
		GeodesicGrid grid = new GeodesicGrid( gridDepth );
		int numSteps = (int)(2.0*Math.PI/axialRotationResolution);
		
		// sample axis orientations
		Progress progress = new Progress( grid.vertices().size(), 5000 );
		List<ScoredAxis> scoredAxes = new ArrayList<ScoredAxis>();
		for( Vector3 newAxis : grid.vertices() )
		{
			// rotate the toy helical axis
			Quaternion.getRotation( q, axis, newAxis );
			ProteinGeometry.rotate( helixToyA, helix, q );
			
			// aggregate scores for all the axial rotations
			ScoredAxis scoredAxis = new ScoredAxis( newAxis );
			for( int i=0; i<numSteps; i++ )
			{
				double angle = Math.PI*2*i/numSteps;
				
				// rotate the toy helix about its axis
				Quaternion.getRotation( q, newAxis, angle );
				ProteinGeometry.rotate( helixToyB, helixToyA, q );
				
				// score the orientation
				scoredAxis.addScore( tensor.getQFactor( helixToyB, rdcs ) );
			}
			scoredAxes.add( scoredAxis );
			
			progress.incrementProgress();
		}
		return scoredAxes;
	}
	
	public static List<ScoredAxis> analyzeHelixOrientations( final AlignmentTensor tensor, final Subunit helix, final List<Rdc<AtomAddressInternal>> rdcs, int gridDepth, double axialRotationResolution, int numWorkers )
	{
		final int numSteps = (int)(2.0*Math.PI/axialRotationResolution);
		final Vector3 originalAxis = ProteinGeometry.getBackboneAxis( helix );
		
		// init the work crew
		WorkCrew<ScoredAxis> workCrew = new WorkCrew<ScoredAxis>( )
		{
			class WorkerState
			{
				// make thread-local copies of objects for speed (it actually helps a lot!!)
				public Subunit localHelix = new Subunit( helix );
				public Vector3 localOriginalAxis = new Vector3( originalAxis );
				
				public Quaternion q = new Quaternion();
				public Subunit helixToyA = new Subunit( helix );
				public Subunit helixToyB = new Subunit( helix );
			}
			
			private ThreadLocal<WorkerState> m_workerState = new ThreadLocal<WorkerState>( )
			{
				@Override
				protected WorkerState initialValue( )
				{
					return new WorkerState();
				}
			};
					
			@Override
			protected void processInWorkerThread( ScoredAxis scoredAxis )
			{
				WorkerState state = m_workerState.get();
				
				// rotate the toy helix to the axis
				Quaternion.getRotation( state.q, state.localOriginalAxis, scoredAxis.axis );
				ProteinGeometry.rotate( state.helixToyA, state.localHelix, state.q );
				
				// score all the axial rotations
				for( int i=0; i<numSteps; i++ )
				{
					double angle = Math.PI*2*i/numSteps;
					
					// rotate the toy helix about its axis
					Quaternion.getRotation( state.q, scoredAxis.axis, angle );
					ProteinGeometry.rotate( state.helixToyB, state.helixToyA, state.q );
					
					// score the orientation
					scoredAxis.addScore( tensor.getQFactor( state.helixToyB, rdcs ) );
				}
			}
		};
		workCrew.startWorkers( numWorkers, 100 );
		
		// score all the axes using the work crew
		List<ScoredAxis> scoredAxes = new ArrayList<ScoredAxis>();
		GeodesicGrid grid = new GeodesicGrid( gridDepth );
		workCrew.setProgress( new Progress( grid.vertices().size(), 5000 ) );
		for( Vector3 axis : grid.vertices() )
		{
			// add the work for this axis
			ScoredAxis scoredAxis = new ScoredAxis( axis );
			scoredAxes.add( scoredAxis );
			workCrew.addWork( scoredAxis );
		}
		workCrew.waitUntilWorkIsFinished();
		
		return scoredAxes;
	}
	
	public static Vector3 getOptimalAxis( List<List<ScoredAxis>> scoredAxesList )
	{
		List<ScoredAxis> baseAxes = scoredAxesList.get( 0 );
		Vector3 optimalAxis = null;
		double optimalScore = Double.POSITIVE_INFINITY;
		for( int i=0; i<baseAxes.size(); i++ )
		{
			Vector3 axis = baseAxes.get( i ).axis;
			
			// average the scores across the lists
			double score = 0;
			for( int j=0; j<scoredAxesList.size(); j++ )
			{
				// check the axis while we're here
				ScoredAxis scoredAxis = scoredAxesList.get( j ).get( i );
				if( !scoredAxis.axis.equals( axis ) )
				{
					double angle = Math.toDegrees( Math.acos( scoredAxis.axis.getDot( axis ) ) );
					throw new IllegalArgumentException( "Axes at index " + i + " do not match across lists! (angle: " + angle + ")" );
				}
				score += scoredAxis.minScore;
			}
			score /= scoredAxesList.size();
			
			// update the optimal score
			if( score < optimalScore )
			{
				optimalScore = score;
				optimalAxis = axis;
			}
		}
		
		return optimalAxis;
	}
	
	public static JFreeChart plotHelixOrientations( Subunit helix, List<ScoredAxis> scoredAxes )
	{
		return plotHelixOrientations( helix, scoredAxes, null );
	}
	
	@SuppressWarnings( "unchecked" )
	public static JFreeChart plotHelixOrientations( Subunit helix, List<ScoredAxis> scoredAxes, Double qFactorRange )
	{
		return plotHelixOrientationsMulti( helix, Arrays.asList( scoredAxes ), qFactorRange ); 
	}
	
	public static JFreeChart plotHelixOrientationsMulti( Subunit helix, List<List<ScoredAxis>> scoredAxesList )
	{
		return plotHelixOrientationsMulti( helix, scoredAxesList, null );
	}
	
	public static JFreeChart plotHelixOrientationsMulti( Subunit helix, List<List<ScoredAxis>> scoredAxesList, Double qFactorRange )
	{
		for( List<ScoredAxis> scoredAxes : scoredAxesList )
		{
			if( scoredAxes.size() != scoredAxesList.get( 0 ).size() )
			{
				throw new IllegalArgumentException( "Each list of scored axes must have the same number of axes!" );
			}
		}
		
		// build the plot
		List<ScoredAxis> baseAxes = scoredAxesList.get( 0 );
		List<Vector3> axes = new ArrayList<Vector3>( baseAxes.size() );
		List<Double> scores = new ArrayList<Double>( baseAxes.size() );
		Vector3 optimalAxis = null;
		double optimalScore = Double.POSITIVE_INFINITY;
		double maxAxisScore = Double.NEGATIVE_INFINITY;
		for( int i=0; i<baseAxes.size(); i++ )
		{
			Vector3 axis = baseAxes.get( i ).axis;
			
			// average the scores across the lists
			double score = 0;
			for( int j=0; j<scoredAxesList.size(); j++ )
			{
				// check the axis while we're here
				ScoredAxis scoredAxis = scoredAxesList.get( j ).get( i );
				if( !scoredAxis.axis.equals( axis ) )
				{
					double angle = Math.toDegrees( Math.acos( scoredAxis.axis.getDot( axis ) ) );
					throw new IllegalArgumentException( "Axes at index " + i + " do not match across lists! (angle: " + angle + ")" );
				}
				score += scoredAxis.minScore;
			}
			score /= scoredAxesList.size();
			
			// update the optimal score
			if( score < optimalScore )
			{
				optimalScore = score;
				optimalAxis = axis;
			}
			maxAxisScore = Math.max( maxAxisScore, score );
			
			// update the plot vars
			axes.add( axis );
			scores.add( score );
		}
		
		if( qFactorRange != null )
		{
			// apply the adaptive cutoff
			double cutoff = optimalScore + qFactorRange*( maxAxisScore - optimalScore );
			Plotter.keepScoresBelow( axes, scores, cutoff );
		}
		
		// build the chart
		JFreeChart chart = Plotter.plotScoredOrientations( axes, scores );
		
		// add a marker for the given helix axis
		Vector3 axis = ProteinGeometry.getBackboneAxis( helix );
		Plotter.addOrientationMarker( chart, axis, Color.black, 14 );
		axis.negate();
		Plotter.addOrientationMarker( chart, axis, Color.black, 8 );
		
		// add a marker for the optimal helix axis
		optimalAxis = new Vector3( optimalAxis );
		Plotter.addOrientationMarker( chart, optimalAxis, Color.green, 14 );
		optimalAxis.negate();
		Plotter.addOrientationMarker( chart, optimalAxis, Color.green, 8 );
		
		return chart;
	}
	
	public static JFreeChart plotHelixOrientationsRange( Subunit helix, List<ScoredAxis> scoredAxes )
	{
		// build the plot
		List<Vector3> axes = new ArrayList<Vector3>( scoredAxes.size() );
		List<Double> scores = new ArrayList<Double>( scoredAxes.size() );
		for( ScoredAxis scoredAxis : scoredAxes )
		{
			axes.add( scoredAxis.axis );
			scores.add( scoredAxis.getScoreRange() );
		}
		return Plotter.plotScoredOrientations( axes, scores );
	}
}
