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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

public class SansonFlamsteedRenderer extends AbstractXYItemRenderer implements XYItemRenderer
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = 8698681429560244564L;
	
	public static final Range ThetaRange = new Range( -Math.PI, Math.PI );
	public static final Range PhiRange = new Range( -Math.PI / 2.0, Math.PI / 2.0 );
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public Range findDomainBounds( XYDataset dataset )
	{
		return ThetaRange;
	}
	
	@Override
	public Range findRangeBounds( XYDataset dataset )
	{
		return PhiRange;
	}
	
	@Override
	public void drawDomainGridLine( Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double theta )
	{
		Paint paint = plot.getDomainGridlinePaint();
		if( paint == null )
		{
			paint = Plot.DEFAULT_OUTLINE_PAINT;
		}
		Stroke stroke = plot.getDomainGridlineStroke();
		if( stroke == null )
		{
			stroke = Plot.DEFAULT_OUTLINE_STROKE;
		}
		drawDomainLine( g2, plot, axis, dataArea, theta, paint, stroke );
    }
	
	@Override
	public void drawDomainLine( Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double theta, Paint paint, Stroke stroke )
	{
		Range range = axis.getRange();
		if( !range.contains( theta ) )
		{
			return;
		}
		
		ValueAxis thetaAxis = axis;
		ValueAxis phiAxis = plot.getRangeAxis();
		
		g2.setPaint( paint );
		g2.setStroke( stroke );
		
		// sample points along the line
		final int NumSamples = 40;
		double lastX = 0.0;
		double lastY = 0.0;
		for( int i=0; i<NumSamples; i++ )
		{
			// convert index to phi range, to s-f space, to screen space
			double phi = (double)i / (double)( NumSamples - 1 ) * Math.PI - Math.PI / 2.0;
			double x = getX( theta, phi );
			double y = getY( theta, phi );
			double transX = thetaAxis.valueToJava2D( x, dataArea, plot.getDomainAxisEdge() );
			double transY = phiAxis.valueToJava2D( y, dataArea, plot.getRangeAxisEdge() );
			
			// draw lines
			if( i > 0 )
			{
				g2.drawLine( (int)lastX, (int)lastY, (int)transX, (int)transY );
			}
			
			lastX = transX;
			lastY = transY;
		}
	}
	
	@Override
	public void drawRangeLine( Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double phi, Paint paint, Stroke stroke )
	{
		Range range = axis.getRange();
		if( !range.contains( phi ) )
		{
			return;
		}
		
		ValueAxis thetaAxis = plot.getDomainAxis();
		ValueAxis phiAxis = axis;
		
		// convert coords to s-f space, to screen space
		double theta0 = -Math.PI;
		double theta1 = Math.PI;
		double startX = thetaAxis.valueToJava2D( getX( theta0, phi ), dataArea, plot.getDomainAxisEdge() );
		double startY = phiAxis.valueToJava2D( getY( theta0, phi ), dataArea, plot.getRangeAxisEdge() );
		double stopX = thetaAxis.valueToJava2D( getX( theta1, phi ), dataArea, plot.getDomainAxisEdge() );
		double stopY = phiAxis.valueToJava2D( getY( theta1, phi ), dataArea, plot.getRangeAxisEdge() );
		
		// draw line
		g2.setPaint( paint );
		g2.setStroke( stroke );
		g2.drawLine( (int)startX, (int)startY, (int)stopX, (int)stopY );
	}
	
	@Override
	public void drawItem( Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass )
	{
		ValueAxis thetaAxis = plot.getDomainAxis();
		ValueAxis phiAxis = plot.getRangeAxis();
		
		// get the coords of the marker
		double theta = dataset.getXValue( series, item );
		double phi = dataset.getYValue( series, item );
		double x = getX( theta, phi );
		double y = getY( theta, phi );
		double transX = thetaAxis.valueToJava2D( x, dataArea, plot.getDomainAxisEdge() );
		double transY = phiAxis.valueToJava2D( y, dataArea, plot.getRangeAxisEdge() );
		
		// draw the marker
		Paint paint = getSeriesFillPaint( series );
		g2.setPaint( paint != null ? paint : Color.black );
		Shape shape = getSeriesShape( series );
		if( shape != null )
		{
			shape = ShapeUtilities.createTranslatedShape( shape, transX, transY );
			g2.fill( shape );
		}
		else
		{
			final double radius = 1.0;
			g2.fillOval(
				(int)( transX - radius ),
				(int)( transY - radius ),
				(int)( radius * 2 ),
				(int)( radius * 2 )
			);
		}
	}
	
	private double getX( double theta, double phi )
	{
		return theta * Math.cos( phi );
	}
	
	private double getY( double theta, double phi )
	{
		return phi;
	}
}
