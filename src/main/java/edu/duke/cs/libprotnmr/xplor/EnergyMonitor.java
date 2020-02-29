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
package edu.duke.cs.libprotnmr.xplor;

import edu.duke.cs.libprotnmr.io.FilterMatchListener;
import edu.duke.cs.libprotnmr.io.StreamConsumer;

public class EnergyMonitor implements FilterMatchListener
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final String PrefixRegex = "van der Waals energy:";
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private double m_energy;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public EnergyMonitor( StreamConsumer streamConsumer )
	{
		// init defaults
		m_energy = Double.POSITIVE_INFINITY;
		
		// attach to the stream consumer
		streamConsumer.setFilter( PrefixRegex + ".*", this );
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public double getEnergy( )
	{
		return m_energy;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public void filterMatch( String line )
	{
		// remove the prefix and trim the result
		line = line.replaceFirst( PrefixRegex, "" ).trim();
		
		// the rest should just be a number
		m_energy = Double.parseDouble( line );
	}
}
