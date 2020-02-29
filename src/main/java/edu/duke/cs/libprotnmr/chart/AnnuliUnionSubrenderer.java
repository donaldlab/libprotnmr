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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.duke.cs.libprotnmr.geom.Annulus2;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;


public class AnnuliUnionSubrenderer implements Subrenderer
{
	@Override
	@SuppressWarnings( "unchecked" )
	public Range findDomainBounds( GeometryDataset dataset, int series )
	{
		List<List<Annulus2>> annuli = (List<List<Annulus2>>)dataset.getData( series );
		
		// for each group...
		boolean isNull = true;
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( List<Annulus2> group : annuli )
		{
			for( Annulus2 annulus : group )
			{
				double x = annulus.center.x;
				double radius = annulus.maxRadius;
				
				lower = Math.min( lower, x - radius );
				upper = Math.max( upper, x + radius );
				isNull = false;
			}
		}
		
		if( isNull )
		{
			return null;
		}
        
        return new Range( lower, upper );
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public Range findRangeBounds( GeometryDataset dataset, int series )
	{
		List<List<Annulus2>> annuli = (List<List<Annulus2>>)dataset.getData( series );
		
		// for each group...
		boolean isNull = true;
		double lower = Double.POSITIVE_INFINITY;
		double upper = Double.NEGATIVE_INFINITY;
		for( List<Annulus2> group : annuli )
		{
			for( Annulus2 annulus : group )
			{
				double y = annulus.center.y;
				double radius = annulus.maxRadius;
				
				lower = Math.min( lower, y - radius );
				upper = Math.max( upper, y + radius );
				isNull = false;
			}
		}
        
		if( isNull )
		{
			return null;
		}
		
        return new Range( lower, upper );
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public void drawItem( Graphics2D g2, Rectangle2D dataArea, XYPlot plot,
		ValueAxis domainAxis, ValueAxis rangeAxis, GeometryDataset dataset,
		int series, int item, GeometryRenderer renderer )
	{
		List<Annulus2> annuli = (List<Annulus2>)dataset.getData( series ).get( item );
		
		// compute the union of annuli
		Area union = new Area();
		for( Annulus2 annulus : annuli )
		{
			// calculate the coordinate transform
			double tranX = domainAxis.valueToJava2D( annulus.center.x, dataArea, plot.getDomainAxisEdge() );
			double tranXZero = domainAxis.valueToJava2D( 0.0, dataArea, plot.getDomainAxisEdge() );
			double tranXMaxRadius = Math.abs( domainAxis.valueToJava2D( annulus.maxRadius, dataArea, plot.getDomainAxisEdge() ) - tranXZero );
			double tranXMinRadius = Math.abs( domainAxis.valueToJava2D( annulus.minRadius, dataArea, plot.getDomainAxisEdge() ) - tranXZero );
			double tranY = rangeAxis.valueToJava2D( annulus.center.y, dataArea, plot.getRangeAxisEdge() );
			double tranYZero = rangeAxis.valueToJava2D( 0.0, dataArea, plot.getRangeAxisEdge() );
			double tranYMaxRadius = Math.abs( rangeAxis.valueToJava2D( annulus.maxRadius, dataArea, plot.getRangeAxisEdge() ) - tranYZero );
			double tranYMinRadius = Math.abs( rangeAxis.valueToJava2D( annulus.minRadius, dataArea, plot.getRangeAxisEdge() ) - tranYZero );
			
			// start with the outer circle
			Area area = new Area( new Ellipse2D.Double(
				tranX - tranXMaxRadius,
				tranY - tranYMaxRadius,
				tranXMaxRadius * 2.0,
				tranYMaxRadius * 2.0
			) );
			
			// subtract the inner circle
			area.subtract( new Area( new Ellipse2D.Double(
				tranX - tranXMinRadius,
				tranY - tranYMinRadius,
				tranXMinRadius * 2.0,
				tranYMinRadius * 2.0
			) ) );
			
			// add to the union
			union.add( area );
		}
		
		// do we need to draw the annuli?
		if( !union.intersects( dataArea ) || union.contains( dataArea ) )
        {
        	return;
        }
		
		// draw the annuli
		if( renderer.getItemFillPaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemFillPaint( series, item ) );
			g2.fill( union );
		}
		if( renderer.getItemOutlinePaint( series, item ) != null )
		{
			g2.setPaint( renderer.getItemOutlinePaint( series, item ) );
			g2.setStroke( renderer.getItemOutlineStroke( series, item ) );
			g2.draw( union );
		}
	}
}
