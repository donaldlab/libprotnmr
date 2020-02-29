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

import edu.duke.cs.libprotnmr.math.CompareReal;

import org.jfree.chart.renderer.PaintScale;


public class GradientPaintScale implements PaintScale
{
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_min;
	private double m_max;
	private Color m_minColor;
	private Color m_maxColor;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public GradientPaintScale( double min, double max, Color minColor, Color maxColor )
	{
		m_min = min;
		m_max = max;
		m_minColor = minColor;
		m_maxColor = maxColor;
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
		// clamp the value to prevent roundoff error causing problems
		final double Epsilon = 1e-10;
		if( value < m_min && CompareReal.eq( value, m_min, Epsilon ) )
		{
			value = m_min;
		}
		if( value > m_max && CompareReal.eq( value, m_max, Epsilon ) )
		{
			value = 1.0;
		}
		if( value < m_min || value > m_max )
		{
			throw new IllegalArgumentException( "Value " + value + " is out of range [" + m_min + "," + m_max + "]!" );
		}

		// linearly interpolate between the two colors
		double f = ( value - m_min )/( m_max - m_min );
		double omf = 1.0 - f;
		return new Color(
			(int)( omf*m_minColor.getRed() + f*m_maxColor.getRed() ),
			(int)( omf*m_minColor.getGreen() + f*m_maxColor.getGreen() ),
			(int)( omf*m_minColor.getBlue() + f*m_maxColor.getBlue() )
		);
	}
}
