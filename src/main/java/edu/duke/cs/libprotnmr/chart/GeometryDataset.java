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

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

public class GeometryDataset extends AbstractXYDataset implements XYDataset
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = -3761543910463983854L;
	
	private static class Entry
	{
		public String name;
		public Subrenderer subrenderer;
		public List<?> data;
		
		public Entry( String name, Subrenderer subrenderer, List<?> data )
		{
			this.name = name;
			this.subrenderer = subrenderer;
			this.data = data;
		}
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private LinkedList<Entry> m_entries;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public GeometryDataset( )
	{
		m_entries = new LinkedList<Entry>();
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void addSeries( String string, Subrenderer subrenderer, List<?> data )
	{
		m_entries.add( new Entry( string, subrenderer, data ) );
	}
	
	public String getName( int series )
	{
		return m_entries.get( series ).name;
	}
	
	public Subrenderer getSubrenderer( int series )
	{
		return m_entries.get( series ).subrenderer;
	}
	
	public List<?> getData( int series )
	{
		return m_entries.get( series ).data;
	}
	
	public int getLastSeries( )
	{
		return m_entries.size() - 1;
	}
	
	@Override
	public int getSeriesCount( )
	{
		return m_entries.size();
	}

	@Override
	@SuppressWarnings( "rawtypes" )
	public Comparable getSeriesKey( int series )
	{
		return m_entries.get( series ).name;
	}

	@Override
	public int getItemCount( int series )
	{
		return m_entries.get( series ).data.size();
	}

	@Override
	public Number getX( int series, int item )
	{
		// just return garbage
		// our subrenderers will never call this
		return 0;
	}

	@Override
	public Number getY( int series, int item )
	{
		// just return garbage
		// our subrenderers will never call this
		return 0;
	}
}
