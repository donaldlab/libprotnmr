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
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class GeometryRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = 8920477496090552659L;
	

	/**************************
	 *   Data Members
	 **************************/
	
	TreeMap<Integer,Boolean> m_useForBounds;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public GeometryRenderer( )
	{
		// init defaults
		m_useForBounds = new TreeMap<Integer,Boolean>();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public Range findDomainBounds( XYDataset dataset )
	{
        if( dataset == null )
        {
        	return null;
        }
        
        if( !( dataset instanceof GeometryDataset ) )
		{
			return null;
		}
		GeometryDataset geometryDataset = (GeometryDataset)dataset;
		
		// delegate to subrenderers
		Range range = null;
		for( int i=0; i<geometryDataset.getSeriesCount(); i++ )
		{
			// use this geometry in the bounds?
			Boolean useForBounds = m_useForBounds.get( i );
			if( useForBounds != null && !useForBounds )
			{
				continue;
			}
			
			Subrenderer subrenderer = geometryDataset.getSubrenderer( i );
			Range subRange = subrenderer.findDomainBounds( geometryDataset, i );
			range = Range.combine( range, subRange );
		}
		return range;
    }
	
	@Override
	public Range findRangeBounds( XYDataset dataset )
	{
        if( dataset == null )
        {
        	return null;
        }
        
        if( !( dataset instanceof GeometryDataset ) )
		{
			return null;
		}
		GeometryDataset geometryDataset = (GeometryDataset)dataset;

		// delegate to subrenderers
		Range range = null;
		for( int i=0; i<geometryDataset.getSeriesCount(); i++ )
		{
			// use this geometry in the bounds?
			Boolean useForBounds = m_useForBounds.get( i );
			if( useForBounds != null && !useForBounds )
			{
				continue;
			}
			
			Subrenderer subrenderer = geometryDataset.getSubrenderer( i );
			Range subRange = subrenderer.findRangeBounds( geometryDataset, i );
			range = Range.combine( range, subRange );
		}
		
		// pad the bounds just a little
		return range;
    }
	
	@Override
	public void drawItem( Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass )
	{
		// return straight away if the item is not visible
		if( !getItemVisible( series, item ) )
		{
		    return;
		}
		
		if( !( dataset instanceof GeometryDataset ) )
		{
			return;
		}
		GeometryDataset geometryDataset = (GeometryDataset)dataset;
		
		// delegate to the subrenderer
		Subrenderer subrenderer = geometryDataset.getSubrenderer( series );
		subrenderer.drawItem(
			g2, dataArea, plot, domainAxis, rangeAxis,
			geometryDataset, series, item, this
		);
	}
	
	@Override
	public XYItemRendererState initialise( Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info )
	{
		// turn off view checking
		XYItemRendererState state = new XYItemRendererState( info );
		state.setProcessVisibleItemsOnly( false );
		return state;
	}
	
	@Override
	public Object clone()
	throws CloneNotSupportedException
	{
        return super.clone();
    }
	
	public void setSeriesStyle( int series, Paint fillPaint, Paint outlinePaint, Stroke outlineStroke )
	{
		setSeriesFillPaint( series, fillPaint );
		setSeriesOutlinePaint( series, outlinePaint );
		setSeriesOutlineStroke( series, outlineStroke );
	}
	
	@Override
	public Paint getItemFillPaint( int series, int item )
	{
		return super.getSeriesFillPaint( series );
	}
	
	@Override
	public Paint getItemOutlinePaint( int series, int item )
	{
		return super.getSeriesOutlinePaint( series );
	}
	
	@Override
	public Stroke getItemOutlineStroke( int series, int item )
	{
		return super.getSeriesOutlineStroke( series );
	}

	public void setUseForBounds( int i, boolean val )
	{
		m_useForBounds.put( i, val );
	}
}
