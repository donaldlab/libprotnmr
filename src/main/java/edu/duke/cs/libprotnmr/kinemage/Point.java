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
package edu.duke.cs.libprotnmr.kinemage;

import edu.duke.cs.libprotnmr.geom.Vector3;

public class Point extends ContainerNode
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Vector3 m_pos;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Point( Vector3 pos )
	{
		this( "", pos );
	}
	
	public Point( String name, Vector3 pos )
	{
		super( name );
		m_pos = pos;
		
		// writing NaNs or infs to the file apparently hangs King. Blow up instead
		assert( !Double.isNaN( pos.x ) );
		assert( !Double.isNaN( pos.y ) );
		assert( !Double.isNaN( pos.z ) );
		assert( !Double.isInfinite( pos.x ) );
		assert( !Double.isInfinite( pos.y ) );
		assert( !Double.isInfinite( pos.z ) );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	// UNDONE: add accessors for the pos
	
	
	/**************************
	 *   Methods
	 **************************/

	public void addColor( KinemageColor color )
	{
		addOption( color.name() );
	}
	

	/**************************
	 *   Functions
	 **************************/
	
	protected void render( )
	{
		StringBuffer buf = new StringBuffer();
		
		renderName( buf );
		buf.append( " " );
		renderOptions( buf );
		
		// render the position
		for( int i=0; i<Vector3.Dimension; i++ )
		{
			buf.append( " " );
			buf.append( String.format( "%.8f", m_pos.get( i ) ) );
		}
		
		m_text = buf.toString();
	}
}
