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

package edu.duke.cs.libprotnmr.perf;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggingMessageListener implements MessageListener
{
	/**************************
	 *   Data Members
	 **************************/
	
	private Logger m_log;
	private Level m_level;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public LoggingMessageListener( Logger log, Level level )
	{
		m_log = log;
		m_level = level;
	}
	

	/**************************
	 *   Methods
	 **************************/
	
	@Override
	public void message( String msg )
	{
		m_log.log( m_level, msg );
	}
}
