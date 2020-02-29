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
