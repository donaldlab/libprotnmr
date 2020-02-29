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
