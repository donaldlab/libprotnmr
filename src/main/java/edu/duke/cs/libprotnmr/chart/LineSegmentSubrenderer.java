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

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.LineSegment2;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;


public class LineSegmentSubrenderer implements Subrenderer
{
	@Override
	@SuppressWarnings( "unchecked" )
	public Range findDomainBounds( GeometryDataset dataset, int series )
	{
		List<LineSegment2> lines = (List<LineSegment2>)dataset.getData( series );
		
		if( lines.isEmpty() )
		{
			return new Range( 0, 0 );
		}
		
		// for each line...
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( LineSegment2 line : lines )
		{
			lower = Math.min( lower, line.start.x );
			upper = Math.max( upper, line.start.x );
			lower = Math.min( lower, line.stop.x );
			upper = Math.max( upper, line.stop.x );
		}
        
        return new Range( lower, upper );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public Range findRangeBounds( GeometryDataset dataset, int series )
	{
		List<LineSegment2> lines = (List<LineSegment2>)dataset.getData( series );
		
		if( lines.isEmpty() )
		{
			return new Range( 0, 0 );
		}
		
		// for each line...
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( LineSegment2 line : lines )
		{
			lower = Math.min( lower, line.start.y );
			upper = Math.max( upper, line.start.y );
			lower = Math.min( lower, line.stop.y );
			upper = Math.max( upper, line.stop.y );
		}
        
        return new Range( lower, upper );
	}
	
	@Override
	public void drawItem( Graphics2D g2, Rectangle2D dataArea, XYPlot plot,
		ValueAxis domainAxis, ValueAxis rangeAxis, GeometryDataset dataset,
		int series, int item, GeometryRenderer renderer )
	{
		LineSegment2 line = (LineSegment2)dataset.getData( series ).get( item );
		
		// calculate the coordinate transform
		double tranXStart = domainAxis.valueToJava2D( line.start.x, dataArea, plot.getDomainAxisEdge() );
		double tranYStart = rangeAxis.valueToJava2D( line.start.y, dataArea, plot.getRangeAxisEdge() );
		double tranXStop = domainAxis.valueToJava2D( line.stop.x, dataArea, plot.getDomainAxisEdge() );
		double tranYStop = rangeAxis.valueToJava2D( line.stop.y, dataArea, plot.getRangeAxisEdge() );
		
		Line2D drawLine = new Line2D.Double(
			tranXStart,
			tranYStart,
			tranXStop,
			tranYStop
		);
		
		// do we need to draw the line?
		if( !drawLine.intersects( dataArea ) )
        {
        	return;
        }
		
		// draw the ellipse
		if( renderer.getItemFillPaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemFillPaint( series, item ) );
			g2.fill( drawLine );
		}
		if( renderer.getItemOutlinePaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemOutlinePaint( series, item ) );
			g2.setStroke( renderer.getItemOutlineStroke( series, item ) );
			g2.draw( drawLine );
		}
	}
}
