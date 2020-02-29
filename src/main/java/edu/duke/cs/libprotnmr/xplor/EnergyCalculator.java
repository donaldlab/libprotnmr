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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.duke.cs.libprotnmr.io.StreamConsumer;
import edu.duke.cs.libprotnmr.pdb.ProteinWriter;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.resources.Resources;


public class EnergyCalculator extends XplorBase
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final URL DefaultScriptTemplate = EnergyCalculator.class.getResource( Resources.getPath("xplor/vdw.py.tmpl") );
	private static final String PdbInName = "in.pdb";
	
	private enum Tags
	{
		pdbInPath;
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private URL m_scriptTemplate;
	private EnergyMonitor m_energyMonitor;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public EnergyCalculator( )
	{
		// use a temporary directory as the work directory
		this( new File( System.getProperty( "java.io.tmpdir" ) ), DefaultScriptTemplate );
	}
	
	public EnergyCalculator( File workDir )
	{
		this( workDir, DefaultScriptTemplate );
	}
	
	public EnergyCalculator( File workDir, URL scriptTemplate )
	{
		super( workDir );
		m_scriptTemplate = scriptTemplate;
		
		// init defaults
		m_energyMonitor = null;
	}
	
	
	/**************************
	 *   Events
	 **************************/
	
	@Override
	protected void setStreamFilters( StreamConsumer outConsumer, StreamConsumer errConsumer )
	{
		m_energyMonitor = new EnergyMonitor( outConsumer );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public double getEnergy( Subunit structure )
	throws IOException, XplorException
	{
		return getEnergy( new Protein( structure ) );
	}
	
	public double getEnergy( Protein structure )
	throws IOException, XplorException
	{
		// write the in PDB file
		File pdbInFile = new File( getWorkDir(), PdbInName );
		new ProteinWriter().write( structure, pdbInFile );
		addTempFile( pdbInFile );
		
		// set up script tags
		setTag( Tags.pdbInPath, quote( pdbInFile.getName() ) );
		
		// run the script!
		File scriptFile = emitFile( m_scriptTemplate );
		addTempFile( scriptFile );
		runScript( scriptFile );
		
		// just in case...
		assert( m_energyMonitor != null );
		
		waitForXplor();
		cleanupTempFiles();
		
		return m_energyMonitor.getEnergy();
	}
}
