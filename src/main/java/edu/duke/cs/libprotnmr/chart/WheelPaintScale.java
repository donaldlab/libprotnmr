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
