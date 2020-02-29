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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.Tick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class AngleAxis extends NumberAxis
{
	private static final long serialVersionUID = -4074710627063455571L;
	
	public static enum AngleType
	{
		Equatorial,
		Polar;
	}
	
	private AngleType m_type;
	
	public AngleAxis( AngleType type )
	{
		super( type.name() + " Angle" );
		m_type = type;
	}
	
	public List<?> refreshTicks( Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge )
	{
		// I don't know what this is for, but just in case it's necessary...
		g2.setFont( getTickLabelFont() );
		if( isAutoTickUnitSelection() )
		{
			selectAutoTickUnit( g2, dataArea, edge );
		}
		
		// build the ticks
		List<Tick> result = new ArrayList<Tick>();
		switch( m_type )
		{
			case Equatorial:
				result.add( new NumberTick( -8.0 * Math.PI / 8.0, "-180", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -7.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -6.0 * Math.PI / 8.0, "-135", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -5.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -4.0 * Math.PI / 8.0, "-90", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -3.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -2.0 * Math.PI / 8.0, "-45", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -1.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 0.0 * Math.PI / 8.0, "0", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 1.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 2.0 * Math.PI / 8.0, "45", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 3.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 4.0 * Math.PI / 8.0, "90", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 5.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 6.0 * Math.PI / 8.0, "135", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 7.0 * Math.PI / 8.0, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 8.0 * Math.PI / 8.0, "180", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0 ) );
	        break;
	        
			case Polar:
				result.add( new NumberTick( -4.0 * Math.PI / 8.0, "-90", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -3.0 * Math.PI / 8.0, "", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -2.0 * Math.PI / 8.0, "-45", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( -1.0 * Math.PI / 8.0, "", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 0.0 * Math.PI / 8.0, "0", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 1.0 * Math.PI / 8.0, "", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 2.0 * Math.PI / 8.0, "45", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 3.0 * Math.PI / 8.0, "", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
				result.add( new NumberTick( 4.0 * Math.PI / 8.0, "90", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0 ) );
			break;
		}
		return result;
	}
}
