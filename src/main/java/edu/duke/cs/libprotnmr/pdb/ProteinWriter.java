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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;


public class ProteinWriter
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final int MaxResiduesPerSqres = 13;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private boolean m_writePseudoatoms;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public ProteinWriter( )
	{
		m_writePseudoatoms = false;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	public boolean getWritePseudoatoms( )
	{
		return m_writePseudoatoms;
	}
	public void setWritePseudoatoms( boolean val )
	{
		m_writePseudoatoms = val;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public void write( Protein protein, String path )
	throws IOException
	{
		write( protein, new File( path ) );
	}
	
	public void write( Protein protein, File file )
	throws IOException
	{
		// open a file for writing
		FileWriter writer = new FileWriter( file );
		
		writeProtein( writer, protein );
		
		// cleanup
		writer.close();
		writer = null;
	}
	
	public void write( List<Protein> proteins, String path )
	throws IOException
	{
		write( proteins, new File( path ) );
	}
	
	public void write( List<Protein> proteins, File file )
	throws IOException
	{
		// open a file for writing
		FileWriter writer = new FileWriter( file );
		
		writeProteins( writer, proteins );
		
		// cleanup
		writer.close();
		writer = null;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void writeProtein( FileWriter writer, Protein protein )
	throws IOException
	{
		writeHeader( writer, protein );
		writeAtoms( writer, protein );
		writeFooter( writer );
	}
	
	private void writeProteins( FileWriter writer, List<Protein> proteins )
	throws IOException
	{
		// HACKHACK: use the first not null protein for the header
		for( Protein protein : proteins )
		{
			if( protein != null )
			{
				writeHeader( writer, protein );
				break;
			}
		}
		
		// for each model...
		int modelNum = 1;
		for( Protein protein : proteins )
		{
			writer.write( String.format( "MODEL %8d\n", modelNum++ ) );
			if( protein != null )
			{
				writeAtoms( writer, protein );
			}
			writer.write( "ENDMDL\n" );
		}
	}
	
	protected void writeHeader( FileWriter writer, Protein protein )
	throws IOException
	{
		// for each subunit...
		for( Subunit subunit : protein.getSubunits() )
		{
			writeDbref( writer, protein, subunit );
		}
		for( Subunit subunit : protein.getSubunits() )
		{
			writeSqres( writer, protein, subunit );
		}
	}
	
	protected void writeFooter( FileWriter writer )
	throws IOException
	{
		// Jeff: 01/14/2009 - CNS complains unless you end with this
		writer.write( "END\n" );
	}
	
	protected void writeDbref( FileWriter writer, Protein protein, Subunit subunit )
	throws IOException
	{
		writer.write( "DBREF" );
		writer.write( String.format( "%6s", protein.getName() ) );
		writer.write( String.format( "%2s", subunit.getName() ) );
		writer.write( String.format( "%5d", subunit.getFirstResidueNumber() ) );
		writer.write( String.format( "%6d", subunit.getLastResidueNumber() ) );
		writer.write( String.format( "%36d", subunit.getFirstResidueNumber() ) );
		writer.write( String.format( "%7d", subunit.getLastResidueNumber() ) );
		writer.write( "\n" );
	}
	
	protected void writeSqres( FileWriter writer, Protein protein, Subunit subunit )
	throws IOException
	{
		int lineId = 1;
		int numResidues = subunit.getResidues().size();
		
		// how many lines do we need?
		int maxLines = (int)Math.ceil( (float)numResidues / (float)MaxResiduesPerSqres );
		
		Iterator<Residue> iterResidue = subunit.getResidues().iterator();
		
		// for each line
		for( lineId=1; lineId <= maxLines; lineId++ )
		{
			writer.write( "SEQRES" );
			writer.write( String.format( "%4d", lineId ) );
			writer.write( String.format( "%2s", subunit.getName() ) );
			writer.write( String.format( "%5d", numResidues ) );
			writer.write( " " );
			
			for( int residuesOnThisLine=0; residuesOnThisLine<MaxResiduesPerSqres; residuesOnThisLine++ )
			{
				if( !iterResidue.hasNext() )
				{
					// just write out the spaces
					writer.write( "    " );
				}
				else
				{
					// get the residue
					Residue residue = iterResidue.next();
	
					// write out the 3-letter amino acid abbreviation
					writer.write( " " );
					writer.write( residue.getAminoAcid().getAbbreviation().toUpperCase() );
				}
			}
			
			writer.write( "\n" );
		}
	}
	
	private void writeAtoms( FileWriter writer, Protein protein )
	throws IOException
	{
		int atomId = 0;
		Residue lastResidue = null;
		
		// for each subunit...
		for( Subunit subunit : protein.getSubunits() )
		{
			// for each residue...
			for( Residue residue : subunit.getResidues() )
			{
				// for each atom...
				for( Atom atom : residue.getAtoms() )
				{
					// should we skip pseudoatoms?
					if( !m_writePseudoatoms && atom.isPseudoatom() )
					{
						continue;
					}
					
					writer.write( "ATOM" );
					writer.write( String.format( "%7d", ++atomId ) );
					
					/* HACKHACK: special rules for writing names for KiNG and molprobity
						Names 3 characters long or less should be written starting at the second position
						Names 4 characters long should be written starting at the first position
					*/
					writer.write( " " );
					if( atom.getName().length() == 4 )
					{
						writer.write( atom.getName() );
					}
					else if( atom.getName().length() <= 3 )
					{
						writer.write( " " );
						writer.write( String.format( "%-3s", atom.getName() ) );
					}
					
					writer.write( String.format( "%4s", residue.getAminoAcid().getAbbreviation().toUpperCase() ) );
					writer.write( String.format( "%2s", subunit.getName() ) );
					writer.write( String.format( "%4d", residue.getNumber() ) );
					writer.write( String.format( "%12.3f", atom.getPosition().x ).substring( 0, 12 ) );
					writer.write( String.format( "%8.3f", atom.getPosition().y ).substring( 0, 8 ) );
					writer.write( String.format( "%8.3f", atom.getPosition().z ).substring( 0, 8 ) );
					writer.write( String.format( "%6.2f", atom.getOccupancy() ) );
					writer.write( String.format( "%6.2f", atom.getTempFactor() ) );
					writer.write( String.format( "%12s", atom.getElement().getCode() ) );
					writer.write( "\n" );
				}
				
				lastResidue = residue;
			}
			
			// add the terminating record
			writer.write( "TER" );
			writer.write( String.format( "%8d", ++atomId ) );
			writer.write( String.format( "%9s", lastResidue.getAminoAcid().getAbbreviation().toUpperCase() ) );
			writer.write( String.format( "%2s", subunit.getName() ) );
			writer.write( String.format( "%4d", lastResidue.getNumber() ) );
			writer.write( "\n" );
		}
	}
}
