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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.duke.cs.libprotnmr.pdb.Molecule.Transformation;
import edu.duke.cs.libprotnmr.protein.AminoAcid;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Subunit;
import edu.duke.cs.libprotnmr.protein.tools.ProteinGeometry;


public class BiologicalAssembler
{
	/**************************
	 *   Static Methods
	 **************************/
	
	public static List<Molecule> readMolecules( File file )
	throws IOException
	{
		String currentMolecule = null;
		Set<String> currentChains = null;
		Map<String,Map<String,List<Transformation>>> moleculeTransformations = new HashMap<String,Map<String,List<Transformation>>>();
		Map<String,StringBuilder> sequenceBuffers = new HashMap<String,StringBuilder>();
		
		// read the file line-by-line and pull out the relevant info
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			if( line.startsWith( "REMARK 350 BIOMOLECULE:" ) )
			{
				currentMolecule = line.substring( 23 ).trim().toUpperCase();
				moleculeTransformations.put( currentMolecule, new HashMap<String,List<Transformation>>() );
			}
			else if( line.startsWith( "REMARK 350 APPLY THE FOLLOWING TO CHAINS:" ) )
			{
				String[] chainNames = line.substring( 41 ).replaceAll(  "\\s", "" ).split( "," );
				
				// init the number of transformations for each chain
				currentChains = new HashSet<String>();
				for( String chainName : chainNames )
				{
					chainName = chainName.toUpperCase();
					moleculeTransformations.get( currentMolecule ).put( chainName, new ArrayList<Transformation>() );
					currentChains.add( chainName );
				}
			}
			else if( line.startsWith( "REMARK 350   BIOMT" ) )
			{
				// if there are no current chains, assume chain "A"
				if( currentChains == null )
				{
					currentChains = new HashSet<String>();
					currentChains.add( "A" );
				}
				
				// get the transformation line
				int lineNumber = Integer.parseInt( line.substring( 18, 19 ) ) - 1;
				
				// apply these transformations to the active chains
				for( String chainName : currentChains )
				{
					Scanner scanner = new Scanner( line.substring( 24 ) );
					
					// get the transformation or make a new one
					List<Transformation> transformations = moleculeTransformations.get( currentMolecule ).get( chainName );
					Transformation transformation = null;
					if( lineNumber == 0 )
					{
						transformation = new Transformation();
						transformations.add( transformation );
					}
					else
					{
						transformation = transformations.get( transformations.size() - 1 );
					}
					
					// save the transformation parameters
					transformation.rotation.data[lineNumber][0] = scanner.nextDouble();
					transformation.rotation.data[lineNumber][1] = scanner.nextDouble();
					transformation.rotation.data[lineNumber][2] = scanner.nextDouble();
					transformation.translation.set( lineNumber, scanner.nextDouble() );
				}
			}
			if( line.startsWith( "SEQRES" ) )
			{
				// get the name of the chain
				String chainName = getChainName( line );
				
				// get the buffer
				StringBuilder sequenceBuffer = sequenceBuffers.get( chainName );
				if( sequenceBuffer == null )
				{
					sequenceBuffer = new StringBuilder();
					sequenceBuffers.put( chainName, sequenceBuffer );
				}
				
				// append the sequence info to the buffer
				if( sequenceBuffer.length() > 0 )
				{
					sequenceBuffer.append( " " );
				}
				sequenceBuffer.append( line.substring( 19 ).trim() );
			}
			else if( line.startsWith( "ATOM" ) )
			{
				break;
			}
		}
		reader.close();
		
		// parse the sequence buffers
		Map<String,List<AminoAcid>> sequences = new HashMap<String,List<AminoAcid>>();
		for( Map.Entry<String,StringBuilder> entry : sequenceBuffers.entrySet() )
		{
			List<AminoAcid> sequence = new ArrayList<AminoAcid>();
			String[] parts = entry.getValue().toString().split( " " );
			for( String part : parts )
			{
				sequence.add( AminoAcid.getByAbbreviation( part ) );
			}
			
			if( isAminoAcidSequence( sequence ) )
			{
				sequences.put( entry.getKey(), sequence );
			}
			else
			{
				// remove the transformations for non-protein chains
				for( Map<String,List<Transformation>> chainTransformations : moleculeTransformations.values() )
				{
					chainTransformations.remove( entry.getKey() );
				}
			}
		}
		
		// build the molecule list
		List<Molecule> molecules = new ArrayList<Molecule>( moleculeTransformations.size() );
		for( Map<String,List<Transformation>> chainTransformations : moleculeTransformations.values() )
		{
			Molecule molecule = new Molecule();
			
			// how many unique amino acid sequences?
			Set<List<AminoAcid>> uniqueSequences = new HashSet<List<AminoAcid>>();
			for( String chainName : chainTransformations.keySet() )
			{
				List<AminoAcid> sequence = sequences.get( chainName );
				if( sequence != null )
				{
					uniqueSequences.add( sequence );
				}
			}
			molecule.setSequences( new ArrayList<List<AminoAcid>>( uniqueSequences ) );
			
			// how many subunits?
			int numSubunits = 0;
			for( Map.Entry<String,List<Transformation>> entry : chainTransformations.entrySet() )
			{
				// is this an amino acid sequence?
				if( sequences.containsKey( entry.getKey() ) )
				{
					numSubunits += entry.getValue().size();
				}
			}
			molecule.setNumSubunits( numSubunits );
			
			// save the transformations
			molecule.setTransformations( chainTransformations );
			
			molecules.add( molecule );
		}
		
		return molecules;
	}
	
	public static Protein readAssembly( File file, Molecule molecule )
	throws IOException
	{
		// read the protein structure
		Protein subunits = new ProteinReader().read( file );
		assert( !subunits.getSubunits().isEmpty() );
		
		// build the oligomer from the biological assembly
		Protein oligomer = new Protein();
		oligomer.setName( subunits.getName() );
		for( Map.Entry<String,List<Transformation>> entry : molecule.getTransformations().entrySet() )
		{
			String chainName = entry.getKey();
			for( Transformation transformation : entry.getValue() )
			{
				// clone the subunit
				Subunit subunit = new Subunit( subunits.getSubunit( chainName.charAt( 0 ) ) );
				ProteinGeometry.transform( subunit, transformation.rotation );
				ProteinGeometry.translate( subunit, transformation.translation );
				oligomer.addSubunit( subunit );
			}
		}
		
		// rename the subunits
		for( int i=0; i<oligomer.getSubunits().size(); i++ )
		{
			oligomer.getSubunits().get( i ).setName( (char)( 'A' +  i ) );
		}
		oligomer.updateSubunitIndex();
		
		return oligomer;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static String getChainName( String line )
	{
		return line.substring( 11, 14 ).trim().toUpperCase();
	}
	
	private static boolean isAminoAcidSequence( List<AminoAcid> sequence )
	{
		// do we have more than 1 amino acid?
		int numAminoAcids = 0;
		for( AminoAcid aminoAcid : sequence )
		{
			if( aminoAcid != AminoAcid.Unknown )
			{
				numAminoAcids++;
				
				if( numAminoAcids > 1 )
				{
					return true;
				}
			}
		}
		return false;
	}
}
