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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Vector2;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.util.ShapeUtilities;


public class PointSubrenderer implements Subrenderer
{
	@Override
	@SuppressWarnings( "unchecked" )
	public Range findDomainBounds( GeometryDataset dataset, int series )
	{
		List<Vector2> points = (List<Vector2>)dataset.getData( series );
		
		if( points.isEmpty() )
		{
			return new Range( 0, 0 );
		}
		
		// for each point...
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( Vector2 point : points )
		{
			lower = Math.min( lower, point.x );
			upper = Math.max( upper, point.x );
		}
        
        return new Range( lower, upper );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public Range findRangeBounds( GeometryDataset dataset, int series )
	{
		List<Vector2> points = (List<Vector2>)dataset.getData( series );
		
		if( points.isEmpty() )
		{
			return new Range( 0, 0 );
		}
		
		// for each point...
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( Vector2 point : points )
		{
			lower = Math.min( lower, point.y );
			upper = Math.max( upper, point.y );
		}
        
        return new Range( lower, upper );
	}
	
	@Override
	public void drawItem( Graphics2D g2, Rectangle2D dataArea, XYPlot plot,
		ValueAxis xAxis, ValueAxis yAxis, GeometryDataset dataset,
		int series, int item, GeometryRenderer renderer )
	{
		Vector2 point = (Vector2)dataset.getData( series ).get( item );
		
		// calculate the coordinate transform
		double tranX = xAxis.valueToJava2D( point.x, dataArea, plot.getDomainAxisEdge() );
		double tranY = yAxis.valueToJava2D( point.y, dataArea, plot.getRangeAxisEdge() );
		
		// draw the shape if needed
		Shape shape = renderer.getItemShape( series, item );
        shape = ShapeUtilities.createTranslatedShape( shape, tranX, tranY );
        if( !shape.intersects( dataArea ) )
        {
        	return;
        }
        
    	if( renderer.getItemFillPaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemFillPaint( series, item ) );
			g2.fill( shape );
		}
		if( renderer.getItemOutlinePaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemOutlinePaint( series, item ) );
			g2.setStroke( renderer.getItemOutlineStroke( series, item ) );
			g2.draw( shape );
		}
	}
}
