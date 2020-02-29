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

public enum GeometryType
{
	/**************************
	 *   Values
	 **************************/
	
	Point
	{
		@Override
		protected Subrenderer newSubrenderer( )
		{
			return new PointSubrenderer();
		}
	},
	AnnuliUnion
	{
		@Override
		protected Subrenderer newSubrenderer( )
		{
			return new AnnuliUnionSubrenderer();
		}
	},
	LineSegment
	{
		@Override
		protected Subrenderer newSubrenderer( )
		{
			return new LineSegmentSubrenderer();
		}
	};
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private Subrenderer m_subrenderer;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	private GeometryType( )
	{
		m_subrenderer = null;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	protected abstract Subrenderer newSubrenderer( );

	public Subrenderer getSubrenderer( )
	{
		if( m_subrenderer == null )
		{
			m_subrenderer = newSubrenderer();
		}
		return m_subrenderer;
	}
}
