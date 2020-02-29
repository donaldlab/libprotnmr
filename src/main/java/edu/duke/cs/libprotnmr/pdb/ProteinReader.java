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
package edu.duke.cs.libprotnmr.pdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Lines;
import edu.duke.cs.libprotnmr.io.ParseException;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.BackboneConformation;
import edu.duke.cs.libprotnmr.protein.Element;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.ResidueRange;
import edu.duke.cs.libprotnmr.protein.Subunit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ProteinReader
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final Logger m_log = LogManager.getLogger(ProteinReader.class);
	
	private static final int DefaultModelId = 0;
	private static final String BackboneNames = "N,CA,C";
	
	private enum Tag
	{
		Dbref( "DBREF" ),
		Model( "MODEL" ),
		Endmdl( "ENDMDL" ),
		Atom( "ATOM" ),
		Hetatm( "HETATM" ),
		Helix( "HELIX" ),
		Sheet( "SHEET" );
		
		private Pattern m_regex;
		
		private Tag( String code )
		{
			m_regex = Pattern.compile( "^\\s*\\Q" + code + "\\E(\\s+.*)?$" );
		}
		
		public boolean isInLine( String line )
		{
			return m_regex.matcher( line ).matches();
		}
	}
	
	private class AtomInfo
	{
		public int number;
		public char subunitName;
		public int residueNumber;
		public AminoAcid aminoAcid;
		public String name;
		public char atomicAlternateId;
		public char residueAlternateId;
		public Element element;
		public Vector3 position;
		public float occupancy;
		public float tempFactor;
		
		public AtomInfo( )
		{
			atomicAlternateId = ' ';
			residueAlternateId = ' ';
		}
		
		public boolean isAtomicAlternate( )
		{
			return atomicAlternateId != ' ' && atomicAlternateId != 'A' && atomicAlternateId != 'a';
		}
		
		public boolean isResidueAlternate( )
		{
			return residueAlternateId != ' ';
		}
	}
	

	/**************************
	 *   Methods
	 **************************/
	
	public Protein read( String path )
	throws IOException
	{
		return read( new File( path ), DefaultModelId );
	}
	
	public Protein read( String path, int modelNum )
	throws IOException
	{
		return read( new File( path ), modelNum );
	}
	
	public Protein read( File file )
	throws IOException
	{
		return read( file, DefaultModelId );
	}
	
	public Protein read( File file, int modelNum )
	throws IOException
	{
		return read( new FileInputStream( file ), modelNum );
	}
	
	public Protein read( InputStream in, int modelNum )
	throws IOException
	{
		return readModels( in, modelNum ).get( 0 );
	}
	
	public Protein read( InputStream in )
	throws IOException
	{
		return read( in, DefaultModelId );
	}
	
	public int readNumProteins( File in )
	throws IOException
	{
		return readNumProteins( new FileInputStream( in ) );
	}
	
	public int readNumProteins( InputStream in )
	throws IOException
	{
		int numModels = 0;
		for( String line : new Lines( new InputStreamReader( in ) ) )
		{
			if( Tag.Model.isInLine( line ) )
			{
				numModels++;
			}
		}
		return numModels;
	}
	
	public ArrayList<Protein> readAll( String path )
	throws IOException
	{
		return readAll( new File( path ) );
	}
	
	public ArrayList<Protein> readAll( File file )
	throws IOException
	{
		return readAll( new FileInputStream( file ) );
	}
	
	public ArrayList<Protein> readAll( InputStream in )
	throws IOException
	{
		return readModels( in, -1 );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private ArrayList<Protein> readModels( InputStream in, int modelId )
	throws IOException
	{
		// convert atom names into a more efficient format
		HashSet<String> backboneNames = new HashSet<String>();
		for( String name : BackboneNames.split( "," ) )
		{
			backboneNames.add( name.toUpperCase() );
		}
		
		boolean isSingleModel = modelId != -1;
		
		// pass 1: read the parts of the file we care about into a buffer and check model counts
		// NOTE: not the most efficient, but it gets the job done
		StringBuffer buf = new StringBuffer();
		int numModels = 0;
		for( String line : new Lines( new InputStreamReader( in ) ) )
		{
			if( !isSingleModel || numModels == modelId || Tag.Dbref.isInLine( line ) )
			{
				buf.append( line );
				buf.append( "\n" );
			}
			
			if( Tag.Endmdl.isInLine( line ) )
			{
				numModels++;
			}
			
			// if we've already read our model, short circuit
			if( isSingleModel && numModels > modelId )
			{
				break;
			}
		}
		String bufString = buf.toString();
		
		// but there's always at least 1 model
		if( numModels < 1 )
		{
			numModels = 1;
		}
		
		// but are we reading only one model?
		if( isSingleModel )
		{
			numModels = 1;
		}
		
		// allocate our proteins
		ArrayList<Protein> proteins = new ArrayList<Protein>( numModels );
		for( int i=0; i<numModels; i++ )
		{
			proteins.add( new Protein() );
		}
		
		// pass 2 : look for subunits, residues, and protein names (oh my!)
		boolean warnedAboutHetAtoms = false;
		boolean warnedAboutAlternates = false;
		int currentProteinId = 0;
		int lineNum = 0;
		Residue lastResidue = null;
		for( String line : new Lines( new StringReader( bufString ) ) )			
		{
			lineNum++;
			try
			{
				if( Tag.Atom.isInLine( line ) )
				{
					AtomInfo atomInfo = getAtomInfo( line );
					if( atomInfo != null )
					{
						if( !atomInfo.isResidueAlternate() )
						{
							if( !isSingleModel )
							{
								lastResidue = initProteinFromAtom( atomInfo, proteins.get( currentProteinId ), lastResidue );
							}
							else
							{
								lastResidue = initProteinFromAtom( atomInfo, proteins.get( 0 ), lastResidue );
							}
						}
						else if( !warnedAboutAlternates )
						{
							m_log.warn( "Ignoring alternate conformations for protein " + proteins.get( 0 ).getName() );
							warnedAboutAlternates = true;
						}
					}
				}
				else if( Tag.Hetatm.isInLine( line ) )
				{
					if( !warnedAboutHetAtoms )
					{
						m_log.warn( "Skipping Het atoms for protein " + proteins.get( 0 ).getName() );
						warnedAboutHetAtoms = true;
					}
				}
				else if( Tag.Endmdl.isInLine( line ) )
				{
					currentProteinId++;
				}
				else if( Tag.Dbref.isInLine( line ) )
				{
					// set the protein name
					String name = parseString( line, 8, 11 );
					for( Protein protein : proteins )
					{
						protein.setName( name );
					}
				}
			}
			catch( Exception ex )
			{
				throw new ParseException( line, lineNum, ex );
			}
		}
		
		// pass 3: look for SSEs
		TreeMap<Character,TreeMap<ResidueRange,BackboneConformation>> backboneConformations = new TreeMap<Character,TreeMap<ResidueRange,BackboneConformation>>();
		lineNum = 0;
		for( String line : new Lines( new StringReader( bufString ) ) )
		{
			lineNum++;
			try
			{
				if( Tag.Helix.isInLine( line ) )
				{
					parseHelix( backboneConformations, line );
				}
				else if( Tag.Sheet.isInLine( line ) )
				{
					parseSheet( backboneConformations, line );
				}
			}
			catch( Exception ex )
			{
				throw new ParseException( line, lineNum, ex );
			}
		}
		for( Map.Entry<Character,TreeMap<ResidueRange,BackboneConformation>> entry : backboneConformations.entrySet() )
		{
			for( Protein protein : proteins )
			{
				Subunit subunit = protein.getSubunit( entry.getKey() );
				subunit.setBackboneConformations( new TreeMap<ResidueRange,BackboneConformation>( entry.getValue() ) );
			}
		}
		
		// pass 4 : actually read in atom coords
		currentProteinId = 0;
		lineNum = 0;
		for( String line : new Lines( new StringReader( bufString ) ) )
		{
			lineNum++;
			try
			{
				if( Tag.Atom.isInLine( line ) )
				{
					AtomInfo atomInfo = getAtomInfo( line );
					if( atomInfo == null )
					{
						m_log.warn( "Skipping ATOM record: " + line );
					}
					else
					{
						if( !atomInfo.isResidueAlternate() && !atomInfo.isAtomicAlternate() )
						{
							if( !isSingleModel )
							{
								addAtom( atomInfo, proteins.get( currentProteinId ), backboneNames );
							}
							else
							{
								addAtom( atomInfo, proteins.get( 0 ), backboneNames );
							}
						}
						else if( !warnedAboutAlternates )
						{
							m_log.warn( "Ignoring alternate conformations for protein " + proteins.get( 0 ).getName() );
							warnedAboutAlternates = true;
						}
					}
				}
				else if( Tag.Endmdl.isInLine( line ) )
				{
					currentProteinId++;
				}
			}
			catch( Exception ex )
			{
				throw new ParseException( line, lineNum, ex );
			}
		}
		
		// update atom indices
		for( Protein protein : proteins )
		{
			for( Subunit subunit : protein.getSubunits() )
			{
				subunit.updateResidueIndex();
				subunit.updateAtomIndices();
			}
		}
		
		// for models that have no atoms, return a null structure
		for( int i=0; i<proteins.size(); i++ )
		{
			if( proteins.get( i ).getNumAtoms() <= 0 )
			{
				proteins.set( i, null );
			}
		}
		
		return proteins;
	}
	
	private void parseHelix( TreeMap<Character,TreeMap<ResidueRange,BackboneConformation>> backboneConformations, String line )
	{
		/*
			         1         2         3         4         5         6         7         8
			12345678901234567890123456789012345678901234567890123456789012345678901234567890
			HELIX    5   5 GLN A   83  TYR A  108  1                                  26    
			HELIX   13  13 VAL B   35  GLN B   39  1                                   5    
		*/
		char subunitName = parseChar( line, 20 );
		TreeMap<ResidueRange,BackboneConformation> map = backboneConformations.get( subunitName );
		if( map == null )
		{
			map = new TreeMap<ResidueRange,BackboneConformation>();
			backboneConformations.put( subunitName, map );
		}
		ResidueRange range = new ResidueRange(
			parseInt( line, 22, 25 ),
			parseInt( line, 34, 37)
		);
		map.put( range, BackboneConformation.AlphaHelix );
	}
	
	private void parseSheet( TreeMap<Character,TreeMap<ResidueRange,BackboneConformation>> backboneConformations, String line )
	{
		/*
			         1         2         3         4         5         6         7         8
			12345678901234567890123456789012345678901234567890123456789012345678901234567890
			SHEET    1   A 4 TRP A  28  VAL A  33  0                                        
			SHEET    2   A 4 TYR A   3  PHE A   8  1  N  TYR A   3   O  LYS A  29           
			SHEET    3   A 4 LYS A  54  ASP A  57 -1  N  GLN A  56   O  THR A   4           
			SHEET    4   A 4 LEU A  60  TYR A  63 -1  N  LEU A  62   O  PHE A  55           
			SHEET    1   B 4 TRP B  28  VAL B  33  0                                        
			SHEET    2   B 4 TYR B   3  PHE B   8  1  N  TYR B   3   O  LYS B  29           
			SHEET    3   B 4 LYS B  54  ASP B  57 -1  N  GLN B  56   O  THR B   4           
			SHEET    4   B 4 LEU B  60  TYR B  63 -1  N  LEU B  62   O  PHE B  55           
		*/
		
		char subunitName = parseChar( line, 22 );
		TreeMap<ResidueRange,BackboneConformation> map = backboneConformations.get( subunitName );
		if( map == null )
		{
			map = new TreeMap<ResidueRange,BackboneConformation>();
			backboneConformations.put( subunitName, map );
		}
		ResidueRange range = new ResidueRange(
			parseInt( line, 23, 26 ),
			parseInt( line, 34, 37 )
		);
		map.put( range, BackboneConformation.BetaStrand );
	}

	private int parseInt( String line, int start, int stop )
	{
		return Integer.parseInt( parseString( line, start, stop ) );
	}
	
	private float parseFloat( String line, int start, int stop )
	{
		return Float.parseFloat( parseString( line, start, stop ) );
	}
	
	private String parseString( String line, int start, int stop )
	{
		return line.substring( start - 1, stop ).trim();
	}
	
	private char parseChar( String line, int start )
	{
		return line.charAt( start - 1 );
	}
	
	private Residue initProteinFromAtom( AtomInfo atomInfo, Protein protein, Residue lastResidue )
	{
		// was this atom filtered out?
		if( atomInfo == null )
		{
			return lastResidue;
		}
		
		// make sure the protein has this subunit
		boolean addedSubunit = false;
		Subunit subunit = protein.getSubunit( atomInfo.subunitName );
		if( subunit == null )
		{
			subunit = new Subunit();
			subunit.setName( atomInfo.subunitName );
			protein.addSubunit( subunit );
			addedSubunit = true;
		}
		
		// add a new residue if needed
		if( addedSubunit || lastResidue == null || atomInfo.residueNumber != lastResidue.getNumber() )
		{
			Residue residue = new Residue();
			residue.setAminoAcid( atomInfo.aminoAcid );
			residue.setNumber( atomInfo.residueNumber );
			residue.setAtoms( new ArrayList<Atom>() );
			residue.setFirstAtomNumber( atomInfo.number );
			subunit.addResidue( residue );
			lastResidue = residue;
		}
		
		return lastResidue;
	}
	
	private void addAtom( AtomInfo atomInfo, Protein protein, HashSet<String> backboneNames )
	{
		// get the subunit and residue
		Subunit subunit = protein.getSubunit( atomInfo.subunitName );
		Residue residue = subunit.getResidueByNumber( atomInfo.residueNumber );
		
		// build the atom
		Atom atom = new Atom();
		atom.setId( residue.getAtoms().size() );
		atom.setNumber( atomInfo.number );
		atom.setName( atomInfo.name );
		atom.setResidueId( residue.getId() );
		atom.setElement( atomInfo.element );
		atom.setPosition( atomInfo.position );
		atom.setOccupancy( atomInfo.occupancy );
		atom.setTempFactor( atomInfo.tempFactor );
		atom.setIsBackbone( backboneNames.contains( atom.getName().toUpperCase() ) );
		residue.getAtoms().add( atom );
	}
	
	private AtomInfo getAtomInfo( String line )
	{
		/*
			         1         2         3         4         5         6         7         8
			12345678901234567890123456789012345678901234567890123456789012345678901234567890
			ATOM     25  HB1 GLN  1234       2.165 -13.066   6.037  1.00  0.00      A   
			ATOM      6  HA2 GLY A1234B     14.268  10.224  12.200  1.00  0.89           H
			ATOM      1  X   ANI   500      -0.615  -0.788  -0.000  1.00  0.00      AXIS  
		*/
		
		// pad the line to 80 characters
		line = String.format( "%-80s", line );
		
		// is this one of those pesky xplor AXIS records?
		if( line.substring( 72, 76 ).equalsIgnoreCase( "AXIS" ) )
		{
			// Ignore it
			return null;
		}
		
		AtomInfo atomInfo = new AtomInfo();
		atomInfo.number = parseInt( line, 7, 11 );
		
		// the subunit name can be in one of two spots, so check them both
		char a = parseChar( line, 22 );
		char b = parseChar( line, 73 );
		atomInfo.subunitName = a != ' ' ? a : b;
		if( atomInfo.subunitName == ' ' )
		{
			atomInfo.subunitName = 'A';
		}
		
		atomInfo.residueNumber = parseInt( line, 23, 26 );
		atomInfo.aminoAcid = AminoAcid.getByAbbreviation( parseString( line, 18, 20 ) );
		atomInfo.atomicAlternateId = parseChar( line, 17 );
		atomInfo.residueAlternateId = parseChar( line, 27 );
		atomInfo.name = parseString( line, 13, 16 );
		atomInfo.element = Element.getByCode( parseString( line, 77, 78 ) );
		atomInfo.position = new Vector3(
			parseFloat( line, 31, 38 ),
			parseFloat( line, 39, 46 ),
			parseFloat( line, 47, 54 )
		);
		atomInfo.occupancy = parseFloat( line, 55, 60 );
		atomInfo.tempFactor = parseFloat( line, 61, 66 );
		
		// HACKHACK: if the name begins with a number, move it to the end
		if( Character.isDigit( atomInfo.name.charAt( 0 ) ) )
		{
			atomInfo.name = "" + atomInfo.name.substring( 1 ) + atomInfo.name.charAt( 0 );
		}
		
		// if the element is unknown, guess it from the atom name
		if( atomInfo.element == Element.Unknown )
		{
			atomInfo.element = Element.getByCode( atomInfo.name.substring( 0, 1 ) );
		}
		
		return atomInfo;
	}
}
