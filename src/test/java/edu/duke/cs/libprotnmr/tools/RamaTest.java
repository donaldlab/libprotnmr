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

package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.chart.ChartWriter;
import edu.duke.cs.libprotnmr.chart.Plotter;
import edu.duke.cs.libprotnmr.rama.RamaCase;

import java.io.File;

public class RamaTest
{
	public static void main( String[] args )
	throws Exception
	{
		RamaCase ramaCase = RamaCase.Proline;
		System.out.println( "plotting..." );
		ChartWriter.writePng(
			Plotter.plotRamaSampledArea( ramaCase, Math.toRadians( 2.0 ) ),
			new File( "output/rama." + ramaCase.name() + ".png" ),
			1080, 1080
		);
		System.out.println( "Done!" );
	}
}
