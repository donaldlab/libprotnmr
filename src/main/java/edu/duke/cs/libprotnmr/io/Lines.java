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

package edu.duke.cs.libprotnmr.io;

import java.io.Reader;
import java.util.Iterator;

public class Lines implements Iterable<String>
{
	/**************************
	 *   DataMembers
	 **************************/
	
	private Reader m_reader;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public Lines( Reader reader )
	{
		m_reader = reader;
	}
	
	
	/**************************
	 *   Methods
	 **************************/

	@Override
	public Iterator<String> iterator( )
	{
		return new LineIterator( m_reader );
	}
}
