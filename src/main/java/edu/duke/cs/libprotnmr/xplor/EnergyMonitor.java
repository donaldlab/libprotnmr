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
