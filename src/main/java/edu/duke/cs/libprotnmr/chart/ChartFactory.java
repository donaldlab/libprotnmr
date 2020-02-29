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
package edu.duke.cs.libprotnmr.chart;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class ChartFactory extends org.jfree.chart.ChartFactory
{
	/**************************
	 *   Constructors
	 **************************/
	
	static
	{
		// set the chart theme
		StandardChartTheme theme = new StandardChartTheme( "Share" );
		theme.setPlotBackgroundPaint( Color.white );
		theme.setDomainGridlinePaint( Color.darkGray );
		theme.setRangeGridlinePaint( Color.darkGray );
		theme.setAxisOffset( new RectangleInsets( 0, 0, 0, 0 ) );
		theme.setExtraLargeFont( new Font( Font.SANS_SERIF, theme.getExtraLargeFont().getStyle(), 28 ) );
		theme.setLargeFont( new Font( Font.SANS_SERIF, theme.getLargeFont().getStyle(), 22 ) );
		theme.setRegularFont( new Font( Font.SANS_SERIF, theme.getRegularFont().getStyle(), 20 ) );
		theme.setSmallFont( new Font( Font.SANS_SERIF, theme.getSmallFont().getStyle(), 16 ) );
		theme.setBarPainter( new StandardBarPainter() );
        theme.setXYBarPainter( new StandardXYBarPainter() );
        theme.setShadowVisible( false );
		org.jfree.chart.ChartFactory.setChartTheme( theme );
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static JFreeChart createGeometryChart( GeometryRenderer renderer, GeometryDataset dataset )
	{
		return createGeometryChart( "", "", "", renderer, dataset );
	}
	
	public static JFreeChart createGeometryChart( String xaxis, String yaxis, GeometryRenderer renderer, GeometryDataset dataset )
	{
		return createGeometryChart( "", xaxis, yaxis, renderer, dataset );
	}
	
	public static JFreeChart createGeometryChart( String xaxis, String yaxis, GeometryRenderer renderer, GeometryDataset dataset, boolean isEqualAspect )
	{
		return createGeometryChart( "", xaxis, yaxis, renderer, dataset, isEqualAspect );
	}
	
	public static JFreeChart createGeometryChart( String title, String xaxis, String yaxis,
		GeometryRenderer renderer, GeometryDataset dataset )
	{
		return createGeometryChart( title, xaxis, yaxis, renderer, dataset, true );
	}
	
	public static JFreeChart createGeometryChart( String title, String xaxis, String yaxis,
		GeometryRenderer renderer, GeometryDataset dataset, boolean isEqualAspect )
	{
		NumberAxis xAxis = new NumberAxis( xaxis );
		NumberAxis yAxis = new NumberAxis( yaxis );
		xAxis.setAutoRangeIncludesZero( false );
		yAxis.setAutoRangeIncludesZero( false );
		XYPlot plot = null;
		if( isEqualAspect )
		{
			plot = new EqualAspectXYPlot( dataset, xAxis, yAxis );
		}
		else
		{
			plot = new XYPlot( dataset, xAxis, yAxis, renderer );
		}
		plot.setRenderer( renderer );
		plot.setSeriesRenderingOrder( SeriesRenderingOrder.FORWARD );
		JFreeChart chart = new JFreeChart(
			title,
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		getChartTheme().apply( chart );
		return chart;
	}
	
	public static JFreeChart createPhiPsiChart( GeometryRenderer renderer, GeometryDataset dataset )
	{
		return createPhiPsiChart( "", renderer, dataset );
	}
	
	public static JFreeChart createPhiPsiChart( String title, GeometryRenderer renderer, GeometryDataset dataset )
	{
		NumberAxis xAxis = new NumberAxis( "Phi" );
		NumberAxis yAxis = new NumberAxis( "Psi" );
		xAxis.setAutoRangeIncludesZero( false );
		yAxis.setAutoRangeIncludesZero( false );
		xAxis.setRange( -180.0, 180.0 );
		yAxis.setRange( -180.0, 180.0 );
		EqualAspectXYPlot plot = new EqualAspectXYPlot( dataset, xAxis, yAxis );
		plot.setRenderer( renderer );
		plot.setSeriesRenderingOrder( SeriesRenderingOrder.FORWARD );
		JFreeChart chart = new JFreeChart(
			title,
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		getChartTheme().apply( chart );
		return chart;
	}
	
	public static JFreeChart createSansonFlamsteedChart( String title, XYSeriesCollection dataset )
	{
		return createSansonFlamsteedChart( title, dataset, new SansonFlamsteedRenderer() );
	}
	
	public static JFreeChart createSansonFlamsteedChart( String title, XYSeriesCollection dataset, SansonFlamsteedRenderer renderer )
	{
		AngleAxis xAxis = new AngleAxis( AngleAxis.AngleType.Equatorial );
		AngleAxis yAxis = new AngleAxis( AngleAxis.AngleType.Polar );
		xAxis.setAutoRangeIncludesZero( false );
		yAxis.setAutoRangeIncludesZero( false );
		XYPlot plot = new EqualAspectXYPlot( dataset, xAxis, yAxis );
		plot.setRenderer( renderer );
		plot.setSeriesRenderingOrder( SeriesRenderingOrder.FORWARD );
		JFreeChart chart = new JFreeChart(
			title,
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		getChartTheme().apply( chart );
		return chart;
	}
	
	public static JFreeChart createHistogram( String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset )
	{
		NumberAxis xAxis = new NumberAxis( xAxisLabel );
		xAxis.setAutoRangeIncludesZero( false );
		ValueAxis yAxis = new NumberAxis( yAxisLabel );
		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setDrawBarOutline( true );
		renderer.setBaseOutlinePaint( Color.darkGray );
		XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
		plot.setDomainZeroBaselineVisible( true );
		plot.setRangeZeroBaselineVisible( true );
		JFreeChart chart = new JFreeChart(
			title,
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		getChartTheme().apply( chart );
		return chart;
	}
	
	public static JFreeChart createXYChart( String title, String xaxisLabel, String yaxisLabel, XYDataset dataset )
	{
		NumberAxis xaxis = new NumberAxis( xaxisLabel );
		NumberAxis yaxis = new NumberAxis( yaxisLabel );
		XYPlot plot = new XYPlot( dataset, xaxis, yaxis, null );
		plot.setRenderer( new XYLineAndShapeRenderer( true, false ) );
		JFreeChart chart = new JFreeChart(
			title,
			JFreeChart.DEFAULT_TITLE_FONT,
			plot,
			false
		);
		getChartTheme().apply( chart );
		return chart;
	}
}
