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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.duke.cs.libprotnmr.math.CompareReal;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;


public class EqualAspectXYPlot extends XYPlot
{
	private static final long serialVersionUID = -7614890103415373686L;

	public EqualAspectXYPlot( XYDataset dataset, NumberAxis xAxis, NumberAxis yAxis )
	{
		super( dataset, xAxis, yAxis, null );
	}
	
	@Override
	public void draw( Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info )
	{
		// change the axes aspect to match the drawing area
		ValueAxis xAxis = getDomainAxis();
		ValueAxis yAxis = getRangeAxis();

		double dx = xAxis.getRange().getLength();
		double dy = yAxis.getRange().getLength();
		if( area.getWidth() / dx < area.getHeight() / dy )
		{
			double scale = dx / dy * area.getHeight() / area.getWidth();
			assert( scale >= 0.0 );
			yAxis.resizeRange( scale );
		}
		else
		{
			double scale = dy / dx * area.getWidth() / area.getHeight();
			assert( scale >= 0.0 );
			xAxis.resizeRange( scale );
		}
		
		// just in case...
		assert( CompareReal.eq(
			xAxis.getRange().getLength() / yAxis.getRange().getLength(),
			area.getWidth() / area.getHeight()
		) );
		
		super.draw( g2, area, anchor, parentState, info );
	}
}
