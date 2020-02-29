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

package edu.duke.cs.libprotnmr.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.analysis.ScalarCouplingCalculator;
import edu.duke.cs.libprotnmr.geom.CircleRange;
import edu.duke.cs.libprotnmr.geom.LineSegment2;
import edu.duke.cs.libprotnmr.geom.Vector2;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.math.DistributionDouble;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensorAxis;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.rama.RamaCase;
import edu.duke.cs.libprotnmr.rama.RamaSample;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;


public class Plotter
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	public static final Color Red = new Color( 255, 66, 14 );
	public static final Color Green = new Color( 87, 157, 28 );
	public static final Color Blue = new Color( 0, 69, 134 );
	public static final Color Purple = new Color( 75, 35, 111 );
	public static final Color MediumGrey = new Color( 180, 180, 180 );
	private static final Color[] Colors = { Blue, Red, Green, Purple, MediumGrey };
	
	private static final double DefaultRdcStdError = 1.0; // Hz
	private static final double HistogramXAxisPad = 0.05;
	
	private static class RdcComparator<T extends AtomAddress<T>> implements Comparator<Rdc<T>>
	{
		@Override
		public int compare( Rdc<T> a, Rdc<T> b )
		{
			return Double.compare( a.getValue(), b.getValue() );
		}
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static JFreeChart plotRdcFit( List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor, HasAtoms protein )
	{
		return plotRdcFit( rdcs, tensor, protein, null, DefaultRdcStdError );
	}
	
	public static JFreeChart plotRdcFit( List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor, HasAtoms protein, String title )
	{
		return plotRdcFit( rdcs, tensor, protein, title, DefaultRdcStdError );
	}
	
	public static JFreeChart plotRdcFit( List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor, HasAtoms protein, String title, double rdcStdError )
	{
		GeometryDataset dataset = new GeometryDataset();
		GeometryRenderer renderer = new GeometryRenderer();
		
		// build the data series
		List<Double> backcomputedRdcValues = tensor.backComputeRdcs( protein, rdcs );
		List<Vector2> points = new ArrayList<Vector2>( rdcs.size() );
		for( int i=0; i<rdcs.size(); i++ )
		{
			Rdc<AtomAddressInternal> rdc = rdcs.get( i );
			points.add( new Vector2(
				backcomputedRdcValues.get( i ),
				rdc.getValue()
			) );
		}
		
		// get the RDC bounds
		Collections.sort( points, new Comparator<Vector2>( )
		{
			@Override
			public int compare( Vector2 a, Vector2 b )
			{
				// compare experimental values
				return Double.compare( a.y, b.y );
			}
		} );
		double minVal = points.get( 0 ).y;
		double maxVal = points.get( points.size() - 1 ).y;
		
		// scale the bounds a bit
		final double BoundScaling = 0.1;
		double pad = ( maxVal - minVal )*BoundScaling;
		minVal -= pad;
		maxVal += pad;
		
		// build the error bars
		final double ErrorBarWidth = ( maxVal - minVal )/100.0;
		List<LineSegment2> errorBars = new ArrayList<LineSegment2>();
		for( int i=0; i<rdcs.size(); i++ )
		{
			Rdc<AtomAddressInternal> rdc = rdcs.get( i );
			Vector2 point = points.get( i );
			
			LineSegment2 verticalLine = new LineSegment2(
				new Vector2( point.x, point.y - rdc.getError() ),
				new Vector2( point.x, point.y + rdc.getError() )
			);
			errorBars.add( verticalLine );
			LineSegment2 topLine = new LineSegment2(
				new Vector2( point.x - ErrorBarWidth, point.y + rdc.getError() ),
				new Vector2( point.x + ErrorBarWidth, point.y + rdc.getError() )
			);
			errorBars.add( topLine );
			LineSegment2 bottomLine = new LineSegment2(
				new Vector2( point.x - ErrorBarWidth, point.y - rdc.getError() ),
				new Vector2( point.x + ErrorBarWidth, point.y - rdc.getError() )
			);
			errorBars.add( bottomLine );
		}
		dataset.addSeries( "Error bars", GeometryType.LineSegment.getSubrenderer(), errorBars );
		renderer.setSeriesStyle( dataset.getLastSeries(), null, Color.darkGray, new BasicStroke( 1.0f ) );
		
		// build the points
		dataset.addSeries( "Points", GeometryType.Point.getSubrenderer(), points );
		renderer.setSeriesStyle( dataset.getLastSeries(), Blue, null, new BasicStroke( 1.0f ) );
		renderer.setSeriesShape( dataset.getLastSeries(), ShapeUtilities.createDiamond( 5.0f ) );
		
		// build the center line
		LineSegment2 centerLine = new LineSegment2(
			new Vector2( minVal, minVal ),
			new Vector2( maxVal, maxVal )
		);
		dataset.addSeries( "Line", GeometryType.LineSegment.getSubrenderer(), Arrays.asList( centerLine ) );
		renderer.setSeriesStyle( dataset.getLastSeries(), null, Color.darkGray, new BasicStroke( 1.0f ) );
		
		// build the std error lines
		LineSegment2 belowLine = new LineSegment2(
			new Vector2( minVal, minVal - rdcStdError ),
			new Vector2( maxVal + rdcStdError, maxVal )
		);
		LineSegment2 aboveLine = new LineSegment2(
			new Vector2( minVal - rdcStdError, minVal ),
			new Vector2( maxVal, maxVal + rdcStdError )
		);
		dataset.addSeries( "Line", GeometryType.LineSegment.getSubrenderer(), Arrays.asList( belowLine, aboveLine ) );
		BasicStroke dashedLine = new BasicStroke( 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{ 10.0f, 16.0f }, 0.0f );
		renderer.setSeriesStyle( dataset.getLastSeries(), null, Color.gray, dashedLine );
		
		// set up the axes
		NumberAxis xAxis = new NumberAxis( "Back-computed RDC" );
		NumberAxis yAxis = new NumberAxis( "Experimental RDC" );
		xAxis.setAutoRangeIncludesZero( false );
		yAxis.setAutoRangeIncludesZero( false );
		xAxis.setRange( minVal, maxVal );
		yAxis.setRange( minVal, maxVal );
		
		// set up the plot
		EqualAspectXYPlot plot = new EqualAspectXYPlot( dataset, xAxis, yAxis );
		plot.setRenderer( renderer );
		plot.setSeriesRenderingOrder( SeriesRenderingOrder.FORWARD );
		String titleSuffix = String.format( "(RMSD: %.2f Hz, Q: %.2f)", tensor.getRmsd( protein, rdcs ), tensor.getQFactor( protein, rdcs ) );
		JFreeChart chart = new JFreeChart(
			String.format( "%s %s", title != null ? title : "RDC Fit", titleSuffix ),
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		return chart;
	}
	
	public static JFreeChart plotRdcFitSequence( List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor, Protein protein )
	{
		return plotRdcFitSequence( rdcs, tensor, protein, null );
	}
	
	public static JFreeChart plotRdcFitSequence( List<Rdc<AtomAddressInternal>> rdcs, AlignmentTensor tensor, Protein protein, String title )
	{
		// build the dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<Double> backcomputedRdcValues = tensor.backComputeRdcs( protein, rdcs );
		for( int i=0; i<rdcs.size(); i++ )
		{
			Rdc<AtomAddressInternal> rdc = rdcs.get( i );
			int residueNumber = protein.getResidue( rdc.getFrom() ).getNumber();
			double diff = Math.abs( backcomputedRdcValues.get( i ) - rdc.getValue() );
			dataset.setValue( diff, "", Integer.toString( residueNumber ) );
		}
		
		String titleSuffix = String.format( "(RMSD: %.2f Hz, Q: %.2f)", tensor.getRmsd( protein, rdcs ), tensor.getQFactor( protein, rdcs ) );
		return ChartFactory.createBarChart(
			String.format( "%s %s", title != null ? title : protein.getName(), titleSuffix ),
			"Residue",
			"RDC Value difference",
			dataset, 
			PlotOrientation.VERTICAL,
			false,
			true,
			false
		);
	}
	
	public static <T extends AtomAddress<T>> JFreeChart plotRdcHistogram( List<Rdc<T>> rdcs, int numBuckets )
	{
		return plotRdcHistogram( rdcs, (AlignmentTensor)null, numBuckets, null );
	}
	
	public static <T extends AtomAddress<T>> JFreeChart plotRdcHistogram( List<Rdc<T>> rdcs, int numBuckets, String title )
	{
		// build the dataset
		double[] rdcValues = new double[rdcs.size()];
		for( int i=0; i<rdcs.size(); i++ )
		{
			rdcValues[i] = rdcs.get( i ).getValue();
		}
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries( "", rdcValues, numBuckets );
		
		// build the plot
		return ChartFactory.createHistogram(
            title != null ? title : "RDCs Histogram",
            "RDC Value",
            "Count",
            dataset
        );
	}
	
	public static <T extends AtomAddress<T>> JFreeChart plotRdcHistogram( List<Rdc<T>> rdcs, AlignmentTensor tensor, int numBuckets )
	{
		return plotRdcHistogram( rdcs, tensor, numBuckets, null );
	}
	
	public static <T extends AtomAddress<T>> JFreeChart plotRdcHistogram( List<Rdc<T>> rdcs, AlignmentTensor tensor, int numBuckets, String title )
	{
		// build the plot
		JFreeChart chart = plotRdcHistogram( rdcs, numBuckets, title );
		XYPlot plot = chart.getXYPlot();
		
		// reset the x-axis range
		double rdcMin = Collections.min( rdcs, new RdcComparator<T>() ).getValue();
		double rdcMax = Collections.max( rdcs, new RdcComparator<T>() ).getValue();
		double rangeMin = Collections.min( Arrays.asList( rdcMin, tensor.getDyy(), tensor.getDzz() ) );
		double rangeMax = Collections.max( Arrays.asList( rdcMax, tensor.getDyy(), tensor.getDzz() ) );
		plot.getDomainAxis().setAutoRange( false );
		double padding = ( rangeMax - rangeMin )*HistogramXAxisPad;
		plot.getDomainAxis().setRange( new Range( rangeMin - padding, rangeMax + padding ) );
		
		// add markers for the tensor eigenvalues
		BasicStroke dashedLine = new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{ 10.0f, 16.0f }, 0.0f );
		plot.addDomainMarker( new ValueMarker( tensor.getDxx(), Blue, dashedLine ) );
		plot.addDomainMarker( new ValueMarker( tensor.getDyy(), Blue, dashedLine ) );
		plot.addDomainMarker( new ValueMarker( tensor.getDzz(), Blue, dashedLine ) );
		
		return chart;
	}
	
	public static <T extends AtomAddress<T>> JFreeChart plotRdcHistogram( List<Rdc<T>> rdcs, List<AlignmentTensor> tensors, int numBuckets, String title )
	{
		// build the plot
		JFreeChart chart = plotRdcHistogram( rdcs, numBuckets, title );
		XYPlot plot = chart.getXYPlot();
		
		// get bounds on the tensor eigenvalues
		double tensorMinEigMin = Double.POSITIVE_INFINITY;
		double tensorMinEigMax = Double.NEGATIVE_INFINITY;
		double tensorMaxEigMin = Double.POSITIVE_INFINITY;
		double tensorMaxEigMax = Double.NEGATIVE_INFINITY;
		for( AlignmentTensor tensor : tensors )
		{
			// sort the eigenvalues
			double minEig = tensor.getDyy();
			double maxEig = tensor.getDzz();
			if( tensor.getDzz() < 0 )
			{
				minEig = tensor.getDzz();
				maxEig = tensor.getDyy();
			}
			assert( minEig < 0 );
			assert( maxEig > 0 );
			
			// compute the bounds
			tensorMinEigMin = Math.min( tensorMinEigMin, minEig );
			tensorMinEigMax = Math.max( tensorMinEigMax, minEig );
			tensorMaxEigMin = Math.min( tensorMaxEigMin, maxEig );
			tensorMaxEigMax = Math.max( tensorMaxEigMax, maxEig );
		}
		
		// reset the x-axis range
		double rdcMin = Collections.min( rdcs, new RdcComparator<T>() ).getValue();
		double rdcMax = Collections.max( rdcs, new RdcComparator<T>() ).getValue();
		double rangeMin = Collections.min( Arrays.asList( rdcMin, tensorMinEigMin ) );
		double rangeMax = Collections.max( Arrays.asList( rdcMax, tensorMaxEigMax ) );
		plot.getDomainAxis().setAutoRange( false );
		double padding = ( rangeMax - rangeMin )*HistogramXAxisPad;
		plot.getDomainAxis().setRange( new Range( rangeMin - padding, rangeMax + padding ) );
		
		// add markers for the tensor eigenvalues
		BasicStroke dashedLine = new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{ 10.0f, 16.0f }, 0.0f );
		plot.addDomainMarker( new IntervalMarker( tensorMinEigMin, tensorMinEigMax, Blue, dashedLine, null, null, 0.1f ) );
		plot.addDomainMarker( new IntervalMarker( tensorMaxEigMin, tensorMaxEigMax, Blue, dashedLine, null, null, 0.1f ) );
		plot.addDomainMarker( new ValueMarker( tensorMinEigMin, Blue, dashedLine ) );
		plot.addDomainMarker( new ValueMarker( tensorMinEigMax, Blue, dashedLine ) );
		plot.addDomainMarker( new ValueMarker( tensorMaxEigMin, Blue, dashedLine ) );
		plot.addDomainMarker( new ValueMarker( tensorMaxEigMax, Blue, dashedLine ) );
		
		return chart;
	}
	

	public static JFreeChart plotAlignmentTensors( List<AlignmentTensor> tensors )
	{
		// build the dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries xaxis = new XYSeries( "X Axis" );
		XYSeries yaxis = new XYSeries( "Y Axis" );
		XYSeries zaxis = new XYSeries( "Z Axis" );
		dataset.addSeries( xaxis );
		dataset.addSeries( yaxis );
		dataset.addSeries( zaxis );
		double[] angles = new double[2];
		for( AlignmentTensor tensor : tensors )
		{
			tensor.getXAxis().toAngles( angles );
			xaxis.add( angles[0], angles[1] );
			tensor.getYAxis().toAngles( angles );
			yaxis.add( angles[0], angles[1] );
			tensor.getZAxis().toAngles( angles );
			zaxis.add( angles[0], angles[1] );
		}
		
		// set the colors and make the plot
		SansonFlamsteedRenderer renderer = new SansonFlamsteedRenderer();
		renderer.setSeriesFillPaint( 0, Color.red );
		renderer.setSeriesFillPaint( 1, Color.green );
		renderer.setSeriesFillPaint( 2, Color.blue );
		return ChartFactory.createSansonFlamsteedChart( "", dataset, renderer );
	}
	
	public static JFreeChart plotAlignmentTensorsAxis( List<AlignmentTensor> tensors, AlignmentTensorAxis axis )
	{
		// extract the Z axes and plot
		ArrayList<Vector3> axes = new ArrayList<Vector3>();
		for( AlignmentTensor tensor : tensors )
		{
			axes.add( tensor.getAxis( axis ) );
		}
		return plotOrientations( axes );
	}
	
	public static JFreeChart plotAlignmentTensorsAxis( List<AlignmentTensor> tensors, AlignmentTensor bestTensor, AlignmentTensorAxis axis )
	{
		// extract the Z axes and plot
		ArrayList<Vector3> axes = new ArrayList<Vector3>();
		for( AlignmentTensor tensor : tensors )
		{
			axes.add( tensor.getAxis( axis ) );
		}
		return plotOrientations( axes, bestTensor.getAxis( axis ) );
	}
	
	public static JFreeChart plotAlignmentTensorsAxisWithReference( List<AlignmentTensor> tensors, AlignmentTensor bestTensor, AlignmentTensorAxis axis, Vector3 referenceOrientation )
	{
		// extract the Z axes and plot
		ArrayList<List<Vector3>> manyAxes = new ArrayList<List<Vector3>>();
		
		// add the sampled axes
		ArrayList<Vector3> axes = new ArrayList<Vector3>();
		for( AlignmentTensor tensor : tensors )
		{
			axes.add( tensor.getAxis( axis ) );
		}
		manyAxes.add( axes );
		
		// add the reference axis
		axes = new ArrayList<Vector3>();
		axes.add( referenceOrientation );
		manyAxes.add( axes );
		
		return plotManyOrientationsCartesian( manyAxes, bestTensor.getAxis( axis ), new Color[] { MediumGrey, Red } );
	}
	
	public static JFreeChart plotOrientationSampling( List<AlignmentTensor> tensors, AlignmentTensor bestTensor, AlignmentTensorAxis axis, List<Vector3> sampledOrientations )
	{
		// extract the Z axes and plot
		ArrayList<Vector3> axes = new ArrayList<Vector3>();
		for( AlignmentTensor tensor : tensors )
		{
			axes.add( tensor.getAxis( axis ) );
		}
		
		ArrayList<List<Vector3>> orientations = new ArrayList<List<Vector3>>();
		orientations.add( axes );
		orientations.add( sampledOrientations );
		return plotManyOrientationsCartesian(
			orientations,
			bestTensor.getAxis( axis ),
			new Color[] { MediumGrey, Blue }
		);
	}
	
	public static JFreeChart plotOrientations( List<Vector3> orientations )
	{
		// build the dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries( "" );
		dataset.addSeries( series );
		double[] angles = new double[2];
		for( Vector3 orientation : orientations )
		{
			orientation.toAngles( angles );
			series.add( angles[0], angles[1] );
		}
		
		// set the colors and make the plot
		SansonFlamsteedRenderer renderer = new SansonFlamsteedRenderer();
		renderer.setSeriesFillPaint( 0, Color.blue );
		return ChartFactory.createSansonFlamsteedChart( "", dataset, renderer );
	}
	
	public static JFreeChart plotAlignmentTensorsAxisWithReferenceAndSampling( List<AlignmentTensor> tensors, AlignmentTensor bestTensor, AlignmentTensorAxis axis, Vector3 referenceOrientation, List<Vector3> orientations )
	{
		// extract the Z axes and plot
		ArrayList<List<Vector3>> manyAxes = new ArrayList<List<Vector3>>();
		
		// add the sampled axes
		ArrayList<Vector3> axes = new ArrayList<Vector3>();
		for( AlignmentTensor tensor : tensors )
		{
			axes.add( tensor.getAxis( axis ) );
		}
		manyAxes.add( axes );
		
		// add the reference axis
		axes = new ArrayList<Vector3>();
		axes.add( referenceOrientation );
		manyAxes.add( axes );
		
		// add the sampled orientations
		manyAxes.add( orientations );
		
		return plotManyOrientationsCartesian( manyAxes, bestTensor.getAxis( axis ), new Color[] { MediumGrey, Red, Blue } );
	}
	
	public static JFreeChart plotOrientations( List<Vector3> orientations, Vector3 center )
	{
		List<List<Vector3>> list = new ArrayList<List<Vector3>>();
		list.add( orientations );
		return plotManyOrientations( list, center );
	}
	
	public static JFreeChart plotManyOrientations( List<List<Vector3>> orientations )
	{
		return plotManyOrientations( orientations, Vector3.getUnitX(), Colors );
	}
	
	public static JFreeChart plotManyOrientations( List<List<Vector3>> orientations, Vector3 center )
	{
		return plotManyOrientations( orientations, center, Colors );
	}
	
	public static JFreeChart plotManyOrientations( List<List<Vector3>> orientations, Vector3 center, Color[] colors )
	{
		// build the dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		double[] angles = new double[2];
		Quaternion rotation = new Quaternion();
		Quaternion.getRotation( rotation, center, Vector3.getUnitX() );
		SansonFlamsteedRenderer renderer = new SansonFlamsteedRenderer();
		
		Vector3 rotatedOrientation = new Vector3();
		for( int i=0; i<orientations.size(); i++ )
		{
			XYSeries series = new XYSeries( "" );
			dataset.addSeries( series );
			for( Vector3 orientation : orientations.get( i ) )
			{
				// rotate the vector
				rotatedOrientation.set( orientation );
				rotation.rotate( rotatedOrientation );
				
				// get the angles and add to the dataset
				rotatedOrientation.toAngles( angles );
				series.add( angles[0], angles[1] );
			}
			
			renderer.setSeriesFillPaint( i, getColor( colors, i ) );
		}
		
		// set the colors and make the plot
		return ChartFactory.createSansonFlamsteedChart( "", dataset, renderer );
	}
	
	public static JFreeChart plotScoredOrientations( List<Vector3> orientations, List<Double> scores )
	{
		return plotScoredOrientations( orientations, scores, "Score" );
	}
	
	public static JFreeChart plotScoredOrientations( List<Vector3> orientations, List<Double> scores, String scoreTitle )
	{
		assert( orientations.size() == scores.size() );
		
		PaintScale paintScale = getPaintScale( RedBluePaintScale.class, scores );
		Shape shape = ShapeUtilities.createDiamond( 3.0f );
		
		// build the dataset
		SansonFlamsteedRenderer renderer = new SansonFlamsteedRenderer();
		XYSeriesCollection dataset = new XYSeriesCollection();
		double[] angles = new double[2];
		for( int i=0; i<orientations.size(); i++ )
		{
			orientations.get( i ).toAngles( angles );
			XYSeries series = new XYSeries( "" );
			dataset.addSeries( series );
			series.add( angles[0], angles[1] );
			
			// set the point style
			renderer.setSeriesShape( i, shape );
			renderer.setSeriesFillPaint( i, paintScale.getPaint( scores.get( i ) ) );
		}
		
		// set the colors and make the plot
		JFreeChart chart = ChartFactory.createSansonFlamsteedChart( "", dataset, renderer );
		addPaintScale( chart, paintScale, scoreTitle );
		return chart;
	}
	
	public static void keepScoresBelow( List<Vector3> axes, List<Double> scores, double maxScoreAllowed )
	{
		assert( axes.size() == scores.size() );
		
		// apply the filter
		List<Vector3> newAxes = new ArrayList<Vector3>();
		List<Double> newScores = new ArrayList<Double>();
		for( int i=0; i<axes.size(); i++ )
		{
			double score = scores.get( i );
			if( score <= maxScoreAllowed )
			{
				newAxes.add( axes.get( i ) );
				newScores.add( score );
			}
		}
		
		// update the inputs
		axes.clear();
		axes.addAll( newAxes );
		scores.clear();
		scores.addAll( newScores );
	}
	
	public static JFreeChart plotOrientationsCartesian( List<Vector3> orientations, Vector3 center )
	{
		List<List<Vector3>> list = new ArrayList<List<Vector3>>();
		list.add( orientations );
		return plotManyOrientationsCartesian( list, center );
	}
	
	public static JFreeChart plotManyOrientationsCartesian( List<List<Vector3>> orientations, Vector3 center )
	{
		return plotManyOrientationsCartesian( orientations, center, Colors );
	}
	
	public static JFreeChart plotManyOrientationsCartesian( List<List<Vector3>> orientations, Vector3 center, Color[] colors )
	{
		// build the dataset
		GeometryDataset dataset = new GeometryDataset();
		GeometryRenderer renderer = new GeometryRenderer();
		
		double[] angles = new double[2];
		Quaternion rotation = new Quaternion();
		Quaternion.getRotation( rotation, center, Vector3.getUnitX() );
		Vector3 rotatedOrientation = new Vector3();
		
		for( int i=0; i<orientations.size(); i++ )
		{
			// build the series
			List<Vector2> sampledPoints = new ArrayList<Vector2>( orientations.size() );
			for( Vector3 orientation : orientations.get( i ) )
			{
				// rotate the vector to the y-axis (ish)
				rotatedOrientation.set( orientation );
				rotation.rotate( rotatedOrientation );
				
				// get the angles (convert to degrees)
				rotatedOrientation.toAngles( angles );
				sampledPoints.add( new Vector2( Math.toDegrees( angles[0] ), Math.toDegrees( angles[1] ) ) );
			}
			
			dataset.addSeries( "Series " + i, GeometryType.Point.getSubrenderer(), sampledPoints );
			renderer.setSeriesStyle( dataset.getLastSeries(), getColor( colors, i ), null, null );
		}
		
		return ChartFactory.createGeometryChart(
			"Equatorial",
			"Polar",
			renderer,
			dataset
		);
	}
	
	public static JFreeChart plotScoredOrientationsCartesian( List<Vector3> orientations, Vector3 center, List<Double> scores, String scoreTitle )
	{
		assert( orientations.size() == scores.size() );
		
		PaintScale paintScale = getPaintScale( RedBluePaintScale.class, scores );
		Shape shape = ShapeUtilities.createDiamond( 3.0f );
		
		// build the dataset
		GeometryDataset dataset = new GeometryDataset();
		GeometryRenderer renderer = new GeometryRenderer();
		double[] angles = new double[2];
		Quaternion rotation = new Quaternion();
		Quaternion.getRotation( rotation, center, Vector3.getUnitX() );
		Vector3 rotatedOrientation = new Vector3();
		for( int i=0; i<orientations.size(); i++ )
		{
			Vector3 orientation = orientations.get( i );
			
			// rotate the vector to the y-axis (ish)
			rotatedOrientation.set( orientation );
			rotation.rotate( rotatedOrientation );
			
			// get the angles (convert to degrees)
			rotatedOrientation.toAngles( angles );
			Vector2 point = new Vector2( Math.toDegrees( angles[0] ), Math.toDegrees( angles[1] ) );
			
			// set the point style
			renderer.setSeriesShape( i, shape );
			renderer.setSeriesFillPaint( i, paintScale.getPaint( scores.get( i ) ) );
			dataset.addSeries( "Series " + i, GeometryType.Point.getSubrenderer(), Arrays.asList( point ) );
		}
		
		// set the colors and make the plot
		JFreeChart chart = ChartFactory.createGeometryChart( "Equatorial", "Polar", renderer, dataset );
		addPaintScale( chart, paintScale, scoreTitle );
		return chart;
	}
	
	public static JFreeChart plotAlignmentTensorAxisDeviation( List<AlignmentTensor> tensors, AlignmentTensor bestTensor, AlignmentTensorAxis axis )
	{
		Vector3 bestAxis = bestTensor.getAxis( axis );
		
		// convert to a list of deviation angles
		double maxDeviation = 0.0;
		double[] deviations = new double[tensors.size()];
		for( int i=0; i<tensors.size(); i++ )
		{
			deviations[i] = Math.toDegrees( Math.acos( bestAxis.getDot( tensors.get( i ).getAxis( axis ) ) ) );
			maxDeviation = Math.max( maxDeviation, deviations[i] );
		}
		
		// build the dataset
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries( "", deviations, 16, 0, Math.ceil( maxDeviation ) );
		
		return ChartFactory.createHistogram(
            null,
            "Deviation angle (deg)",
            "Number of Axes",
            dataset
        );
	}
	
	public static void addOrientationMarker( JFreeChart chart, Vector3 marker, Color color, double size )
	{
		addOrientationMarkers( chart, Arrays.asList( marker ), color, size );
	}
	
	public static void addOrientationMarkers( JFreeChart chart, List<Vector3> markers, Color color, double size )
	{
		// get our chart objects
		XYPlot plot = (XYPlot)chart.getPlot();
		XYSeriesCollection dataset = (XYSeriesCollection)plot.getDataset();
		AbstractXYItemRenderer renderer = (AbstractXYItemRenderer)plot.getRenderer();
		
		// add a new series for the markers
		XYSeries series = new XYSeries( "" );
		dataset.addSeries( series );
		double[] angles = new double[2];
		for( Vector3 v : markers )
		{
			v.toAngles( angles );
			series.add( angles[0], angles[1] );
		}
		int lastSeries = dataset.getSeriesCount() - 1;
		renderer.setSeriesShape( lastSeries, ShapeUtilities.createDiagonalCross( (float)size, 1.5f ) );
		renderer.setSeriesFillPaint( lastSeries, color );
	}
	
	public static ValueAxis getColorAxis( JFreeChart chart )
	{
		for( Object obj : chart.getSubtitles() )
		{
			if( obj instanceof PaintScaleLegend )
			{
				return ((PaintScaleLegend)obj).getAxis();
			}
		}
		return null;
	}
	
	public static void setBounds( JFreeChart chart, double xmin, double xmax, double ymin, double ymax )
	{
		XYPlot plot = (XYPlot)chart.getPlot();
		plot.getDomainAxis().setRange( xmin, xmax );
		plot.getRangeAxis().setRange( ymin, ymax );
	}
	
	public static void scaleMarkers( JFreeChart chart, double scaleFactor )
	{
		// build the transform
		AffineTransform transform = new AffineTransform();
		transform.scale( scaleFactor, scaleFactor );
	    
		// scale all the shapes!!
		XYPlot plot = (XYPlot)chart.getPlot();
		XYDataset dataset = plot.getDataset();
		XYItemRenderer renderer = plot.getRenderer();
		for( int i=0; i<dataset.getSeriesCount(); i++ )
		{
			Shape shape = renderer.getSeriesShape( i );
			if( shape != null )
			{
				Shape scaledShape = transform.createTransformedShape( shape );
				renderer.setSeriesShape( i, scaledShape );
			}
		}
	}
	
	public static JFreeChart plotKarplusCurve( ScalarCouplingCalculator calc, double minHz, double maxHz )
	{
		return plotKarplusCurve( calc, minHz, maxHz, null );
	}
	
	public static JFreeChart plotKarplusCurve( ScalarCouplingCalculator calc, double minHz, double maxHz, List<CircleRange> intervals )
	{
		// sample the karplus curve
		List<Double> angles = CircleRange.newCircle().samplePoints( Math.toRadians( 1.0 ) );
		// drop the last angle so we don't sample the origin twice
		angles = angles.subList( 0, angles.size() - 1 );
		double[][] samples = new double[][] { new double[angles.size()], new double[angles.size()] };
		for( int i=0; i<angles.size(); i++ )
		{
			samples[0][i] = Math.toDegrees( CircleRange.mapZeroToTwoPi( angles.get( i ) ) );
			samples[1][i] = calc.getHz( angles.get( i ) );
		}
		
		// make the dataset
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries( "karplus", samples );
		
		// build the plot
		JFreeChart chart = ChartFactory.createXYChart( "", "Angle", "Karplus curve (Hz)", dataset );
		
		// show a range for the hz
		XYPlot plot = (XYPlot)chart.getPlot();
		plot.addRangeMarker( new IntervalMarker( minHz, maxHz, Color.pink ) );
		
		// make the karplus curve black
		plot.getRenderer().setSeriesPaint( dataset.getSeriesCount() - 1, Color.black );
		
		if( intervals != null )
		{
			// show the intervals
			for( CircleRange interval : intervals )
			{
				double source = CircleRange.mapZeroToTwoPi( interval.getSource() );
				double target = CircleRange.mapZeroToTwoPi( interval.getTarget() );
				if( source < target )
				{
					plot.addDomainMarker( new IntervalMarker(
						Math.toDegrees( source ),
						Math.toDegrees( target ),
						Color.blue
					) );
				}
				else
				{
					plot.addDomainMarker( new IntervalMarker(
						Math.toDegrees( 0 ),
						Math.toDegrees( source ),
						Color.blue
					) );
					plot.addDomainMarker( new IntervalMarker(
						Math.toDegrees( target ),
						Math.toDegrees( Math.PI*2 ),
						Color.blue
					) );
				}
			}
		}
		
		return chart;
	}
	
	public static JFreeChart plotRama( Protein protein, Iterable<Integer> residueNumbers )
	{
		// init the case map
		Map<RamaCase,List<Vector2>> categorizedPoints = new TreeMap<RamaCase,List<Vector2>>();
		for( RamaCase ramaCase : RamaCase.values() )
		{
			categorizedPoints.put( ramaCase, new ArrayList<Vector2>() );
		}
		
		// convert the structure to phi,psis and categorize by rama case
		for( int residueNumber : residueNumbers )
		{
			for( Subunit subunit : protein.getSubunits() )
			{
				RamaCase ramaCase = RamaCase.getCaseByNumber( subunit.getSequence(), residueNumber );
				categorizedPoints.get( ramaCase ).add( new Vector2(
					subunit.getPhiAngleByNumber( residueNumber ),
					subunit.getPsiAngleByNumber( residueNumber )
				) );
			}
		}
		
		// get all the sub plots and combine them
		CombinedRangeXYPlot superplot = new CombinedRangeXYPlot();
		for( Map.Entry<RamaCase,List<Vector2>> entry : categorizedPoints.entrySet() )
		{
			RamaCase ramaCase = entry.getKey();
			List<Vector2> points = entry.getValue();
			XYPlot subplot = (XYPlot)plotRama( ramaCase, points ).getPlot();
			// HACKHACK: only the domain axis label is shown in the combined plot
			// use it to show the rama case
			subplot.getDomainAxis().setLabel( ramaCase.name() );
			superplot.add( subplot );
		}
		return new JFreeChart( "Ramachandran Statistics", JFreeChart.DEFAULT_TITLE_FONT, superplot, false );
	}
	
	public static JFreeChart plotRama( RamaCase ramaCase, Iterable<Vector2> points )
	{
		JFreeChart chart = plotRamaArea( ramaCase );
		
		// convert the point to degrees
		List<Vector2> pointsDegrees = new ArrayList<Vector2>();
		for( Vector2 point : points )
		{
			pointsDegrees.add( new Vector2( Math.toDegrees( point.x ), Math.toDegrees( point.y ) ) );
		}
		
		// add the points
		EqualAspectXYPlot plot = (EqualAspectXYPlot)chart.getPlot();
		GeometryDataset dataset = (GeometryDataset)plot.getDataset();
		GeometryRenderer renderer = (GeometryRenderer)plot.getRenderer();
		for( Vector2 point : pointsDegrees )
		{
			dataset.addSeries( "Sampled Points", GeometryType.Point.getSubrenderer(), Arrays.asList( point ) );
			renderer.setSeriesFillPaint( dataset.getLastSeries(), Color.blue );
			renderer.setSeriesShape( dataset.getLastSeries(), ShapeUtilities.createDiamond( 2.0f ) );
		}
		
		// change the rama color to grey
		renderer.setSeriesFillPaint( 0, Color.lightGray );
		
		return chart;
	}
	
	public static JFreeChart plotRamaProbability( RamaCase ramaCase )
	{
		// build the dataset
		GeometryDataset dataset = new GeometryDataset();
		GeometryRenderer renderer = new GeometryRenderer();
		
		// set up the color axis
		PaintScale paintScale = new RedBluePaintScale( 0.0, 1.0 );
		
		// plot the samples
		for( RamaSample sample : ramaCase.getMap() )
		{
			Vector2 point = new Vector2( sample.phi, sample.psi );
			dataset.addSeries( "Sampled Points", GeometryType.Point.getSubrenderer(), Arrays.asList( point ) );
			renderer.setSeriesFillPaint( dataset.getLastSeries(), paintScale.getPaint( sample.value ) );
			renderer.setSeriesShape( dataset.getLastSeries(), ShapeUtilities.createDiamond( 1.0f ) );
		}
		
		JFreeChart chart = ChartFactory.createPhiPsiChart( renderer, dataset );
		addPaintScale( chart, paintScale, "Probability" );
		return chart;
	}
	
	public static JFreeChart plotRamaArea( RamaCase ramaCase )
	{
		// build the dataset
		List<Vector2> points = new ArrayList<Vector2>();
		for( RamaSample sample : ramaCase.getMap() )
		{
			if( ramaCase.isAllowed( sample.phi, sample.psi ) )
			{
				points.add( new Vector2( sample.phi, sample.psi ) );
			}
		}
		GeometryDataset dataset = new GeometryDataset();
		dataset.addSeries( "Sampled Points", GeometryType.Point.getSubrenderer(), points );
		
		// plot the samples
		GeometryRenderer renderer = new GeometryRenderer();
		renderer.setSeriesFillPaint( dataset.getLastSeries(), new Color( 0, 0, 0 ) );
		renderer.setSeriesShape( dataset.getLastSeries(), ShapeUtilities.createDiamond( 1.0f ) );
		
		return ChartFactory.createPhiPsiChart( renderer, dataset );
	}
	
	public static JFreeChart plotRamaSampledArea( RamaCase ramaCase, double resolutionRadians )
	{
		// build the dataset
		List<Vector2> points = new ArrayList<Vector2>();
		int numSamples = Math.max( 2, (int)( Math.PI/2/resolutionRadians ) );
		for( int i=0; i<numSamples; i++ )
		{
			double phi = (double)i/(double)(numSamples-1) * 360.0 - 180.0;
			for( int j=0; j<numSamples; j++ )
			{
				double psi = (double)j/(double)(numSamples-1) * 360.0 - 180.0;
				if( ramaCase.isAllowed( phi, psi ) )
				{
					points.add( new Vector2( phi, psi ) );
				}
			}
		}
		GeometryDataset dataset = new GeometryDataset();
		dataset.addSeries( "Sampled Points", GeometryType.Point.getSubrenderer(), points );
		
		// plot the samples
		GeometryRenderer renderer = new GeometryRenderer();
		renderer.setSeriesFillPaint( dataset.getLastSeries(), Color.black );
		renderer.setSeriesShape( dataset.getLastSeries(), ShapeUtilities.createDiamond( 3.0f ) );
		
		return ChartFactory.createPhiPsiChart( renderer, dataset );
	}
	
	
	/*********************************
	 *   Static Functions
	 *********************************/
	
	private static Color getColor( Color[] colors, int i )
	{
		return colors[ i % colors.length ];
	}
	
	protected static void addPaintScale( JFreeChart chart, PaintScale paintScale, String title )
	{
		NumberAxis colorAxis = new NumberAxis( "Score" );
		colorAxis.setLowerBound( paintScale.getLowerBound() );
		colorAxis.setUpperBound( paintScale.getUpperBound() );
		colorAxis.setLabel( title );
		chart.removeLegend();
		PaintScaleLegend legend = new PaintScaleLegend( paintScale, colorAxis );
		legend.setPosition( RectangleEdge.RIGHT );
		legend.setAxisLocation( AxisLocation.BOTTOM_OR_RIGHT );
		legend.setMargin( 10, 10, 30, 10 );
		chart.addSubtitle( legend );
		ChartFactory.getChartTheme().apply( chart );
	}
	
	private static PaintScale getPaintScale( Class<? extends PaintScale> cc, List<Double> scores )
	{
		// define the range
		double min = 0;
		double max = 1;
		if( scores.size() >= 2 )
		{
			DistributionDouble dist = new DistributionDouble( scores );
			min = dist.getMinDouble();
			max = dist.getMaxDouble();
		}
		else if( scores.size() == 1 )
		{
			min = scores.get( 0 ) - 0.5;
			max = scores.get( 0 ) + 0.5;
		}
		
		// instantiate the scale
		try
		{
			Constructor<? extends PaintScale> constructor = cc.getConstructor( double.class, double.class );
			return constructor.newInstance( min, max );
		}
		catch( Exception ex )
		{
			// rethrow as error
			throw new Error( "Unable to instantiate paint scale!", ex );
		}
	}
}
