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
