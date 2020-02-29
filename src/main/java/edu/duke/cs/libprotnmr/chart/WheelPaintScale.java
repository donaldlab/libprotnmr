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
import java.awt.Paint;

import org.jfree.chart.renderer.PaintScale;

public class WheelPaintScale implements PaintScale
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_min;
	private double m_max;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public WheelPaintScale( double min, double max )
	{
		m_min = min;
		m_max = max;
	}
	
	
	/**************************
	 *   Methods
	 **************************/

	@Override
	public double getLowerBound( )
	{
		return m_min;
	}

	@Override
	public double getUpperBound( )
	{
		return m_max;
	}

	@Override
	public Paint getPaint( double value )
	{
		double factor = 1.0 + Math.min( ( value - m_max ) / ( m_max - m_min ), 0.0 );
		return Color.getHSBColor( (float)( ( 1.0f - (float)factor ) * 0.75 ), 0.75f, 1.0f );
	}
}
