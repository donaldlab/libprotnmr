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
import java.util.ArrayList;
import java.util.List;

import edu.duke.cs.libprotnmr.io.ParseException;
import edu.duke.cs.libprotnmr.mapping.AddressMapper;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraintWriter;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.pdb.ProteinWriter;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.pseudoatoms.PseudoatomBuilder;
import edu.duke.cs.libprotnmr.resources.Resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StructureMinimizer extends XplorBase
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final URL DefaultScriptTemplate = StructureMinimizer.class.getResource( Resources.getPath("xplor/minimize.py.tmpl") );
	private static final String PdbInName = "in.pdb";
	private static final String NoesName = "noes.tbl";
	private static final String RdcsName = "rdcs.tbl";
	private static final int DefaultNumCycles = 1000;
	
	private enum Tags
	{
		PdbInPath,
		PdbOutPath,
		UseNoes,
		NoesPath,
		UseRdcs,
		RdcsPath,
		SaupeMatrix,
		NumSteps;
	}
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private static final Logger m_log = LogManager.getLogger(StructureMinimizer.class);
	
	private URL m_scriptTemplate;
	private List<DistanceRestraint<AtomAddressReadable>> m_distanceRestraints;
	// private List<Rdc> m_rdcs;
	private Matrix3 m_saupeMatrix;
	private int m_numSteps;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public StructureMinimizer( )
	{
		// use a temporary directory as the work directory
		this( new File( System.getProperty( "java.io.tmpdir" ) ), DefaultScriptTemplate );
	}
	
	public StructureMinimizer( File workDir )
	{
		this( workDir, DefaultScriptTemplate );
	}
	
	public StructureMinimizer( File workDir, URL scriptTemplate )
	{
		super( workDir );
		
		// save parameters
		m_scriptTemplate = scriptTemplate;
		
		// init defaults
		m_distanceRestraints = null;
		// m_rdcs = null;
		m_saupeMatrix = null;
		m_numSteps = DefaultNumCycles;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public void setNumSteps( int val )
	{
		m_numSteps = val;
	}
	public int getNumSteps( )
	{
		return m_numSteps;
	}
	
	public void setDistanceRestraints( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		// make a deep copy of the distance restraints so we can modify them
		m_distanceRestraints = new ArrayList<DistanceRestraint<AtomAddressReadable>>( restraints.size() );
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			m_distanceRestraints.add( new DistanceRestraint<AtomAddressReadable>( restraint ) );
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	/* UNDONE: implement RDCs
	public void setRdcs( List<Rdc> rdcs, Matrix3 saupeMatrix )
	{
		m_rdcs = rdcs;
		m_saupeMatrix = saupeMatrix;
	}
	*/
	
	public Protein minimize( Protein structure )
	throws IOException, XplorException
	{
		// perform the minimization
		File structureFile = File.createTempFile( "structure", ".pdb", getWorkDir() );
		minimize( structureFile, structure );
		
		// read in the protein
		try
		{
			Protein minimizedStructure = new ProteinReader().read( structureFile );
			NameMapper.ensureProtein( minimizedStructure, NameScheme.New );
			PseudoatomBuilder.getInstance().build( minimizedStructure );
			return minimizedStructure;
		}
		catch( ParseException ex )
		{
			m_log.error( "Unable to read Xplor's PDB file. Assuming severe steric clash!", ex );
			return null;
		}
		finally
		{
			// cleanup the temp file
			structureFile.delete();
		}
	}
	
	public void minimize( String outPdbPath, Protein structure )
	throws IOException, XplorException
	{
		minimize( new File( outPdbPath ), structure );
	}
	
	public void minimize( File pdbOutFile, Protein structure )
	throws IOException, XplorException
	{
		// write the in PDB file
		File pdbInFile = addTempFile( new File( getWorkDir(), PdbInName ) );
		new ProteinWriter().write( structure, pdbInFile );
		
		// set up script tags
		boolean useDistanceRestraints = m_distanceRestraints != null;
		boolean useRdcs = false; //m_rdcs != null;
		setTag( Tags.PdbInPath, quote( pdbInFile.getName() ) );
		setTag( Tags.PdbOutPath, quote( pdbOutFile.getAbsolutePath() ) );
		if( useDistanceRestraints )
		{
			setTag( Tags.UseNoes, PythonTrue );
			
			// xplor requires old-style atom names for NOEs
			NameMapper.ensureAddresses( structure.getSequences(), m_distanceRestraints, NameScheme.Old );
			
			// make sure NOEs with pseudoatoms use the masks, not the names
			AddressMapper.mapPseudoaomNamesToMasks( structure, m_distanceRestraints );
			
			// write the NOEs to the file
			File noesFile = addTempFile( new File( getWorkDir(), NoesName ) );
			new DistanceRestraintWriter().write( noesFile, m_distanceRestraints );
			
			setTag( Tags.NoesPath, quote( noesFile.getName() ) );
		}
		else
		{
			setTag( Tags.UseNoes, PythonFalse );
			setTag( Tags.NoesPath, PythonNull );
		}
		if( useRdcs )
		{
			// UNDONE: write the RDCs to the file
			File rdcsFile = addTempFile( new File( getWorkDir(), RdcsName ) );
			
			setTag( Tags.UseRdcs, PythonTrue );
			setTag( Tags.RdcsPath, quote( rdcsFile.getName() ) );
			setTag( Tags.SaupeMatrix, String.format(
				"SymMat3( %f, %f, %f, %f, %f, %f )",
				m_saupeMatrix.data[0][0],
				m_saupeMatrix.data[1][0], m_saupeMatrix.data[1][1],
				m_saupeMatrix.data[2][0], m_saupeMatrix.data[2][1], m_saupeMatrix.data[2][2]
			) );
		}
		else
		{
			setTag( Tags.UseRdcs, PythonFalse );
			setTag( Tags.RdcsPath, PythonNull );
			setTag( Tags.SaupeMatrix, PythonNull );
		}
		setTag( Tags.NumSteps, Integer.toString( m_numSteps ) );
		
		// run the script!
		File scriptFile = addTempFile( emitFile( m_scriptTemplate ) );
		runScript( scriptFile );
		waitForXplor();
		
		cleanupTempFiles();
		
		// just in case...
		assert( pdbOutFile.exists() );
	}
}
