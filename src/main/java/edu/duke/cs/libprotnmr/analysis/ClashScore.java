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
package edu.duke.cs.libprotnmr.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import edu.duke.cs.libprotnmr.geom.Line3;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.pdb.ProteinWriter;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClashScore
{
	// this class uses Probe (from Molprobity) to compute the clash score
	// calculations are based on the clashlist shell script from the molprobity 3 distribution
	// http://molprobity.biochem.duke.edu/
	
	private static final Logger m_log = LogManager.getLogger(ClashScore.class);
	
	
	/**************************
	 *   Definitions
	 **************************/
	
	private static enum Type
	{
		Hbond( "hb", true, false ),
		BadOverlap( "bo", false, true ),
		SlightOverlap( "so", false, true );
		
		private String m_code;
		private boolean m_isHbond;
		private boolean m_isClash;
		
		private static Map<String,Type> m_lookup;
		
		static
		{
			m_lookup = new TreeMap<String,Type>();
			for( Type type : values() )
			{
				m_lookup.put( type.getCode(), type );
			}
		}
		
		private Type( String code, boolean isHbond, boolean isClash )
		{
			m_code = code;
			m_isHbond = isHbond;
			m_isClash = isClash;
		}
		
		public String getCode( )
		{
			return m_code;
		}
		
		public boolean isHbond( )
		{
			return m_isHbond;
		}
		
		public boolean isClash( )
		{
			return m_isClash;
		}
		
		public static Type lookup( String code )
		{
			return m_lookup.get( code );
		}
	}
	
	public static class AddressPair
	{
		public AtomAddressReadable left;
		public AtomAddressReadable right;
		
		public AddressPair( AtomAddressReadable left, AtomAddressReadable right )
		{
			this.left = left;
			this.right = right;
		}
		
		@Override
		public int hashCode( )
		{
			if( left.compareTo( right ) < 0 )
			{
				return HashCalculator.combineHashes(
					left.hashCode(),
					right.hashCode()
				);
			}
			else
			{
				return HashCalculator.combineHashes(
					right.hashCode(),
					left.hashCode()
				);
			}
		}
		
		@Override
		public boolean equals( Object other )
		{
			if( other instanceof AddressPair )
			{
				return equals( (AddressPair)other );
			}
			return false;
		}
		
		public boolean equals( AddressPair other )
		{
			// address can be given in either order
			return ( left.equals( other.left ) && right.equals( other.right ) )
				|| ( left.equals( other.right ) && right.equals( other.left ) );
		}
		
		@Override
		public String toString( )
		{
			return left.toString() + " <-> " + right.toString();
		}
	}
	
	public static class Spike
	{
		public Type type;
		public AddressPair addresses;
		public double gap;
		public Line3 line;
		
		@Override
		public String toString( )
		{
			return String.format( "%s %6.3f %s", type.getCode(), gap, addresses.toString() );
		}
	}
	
	private static double MinAllowedGap = -0.4;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static double getClashScore( Protein protein )
	{
		return computeClashScore(
			getClashes( protein ).size(),
			protein.getNumAtoms()
		);
	}
	
	public static double getClashScore( File inFile )
	throws IOException
	{
		return computeClashScore(
			getClashes( inFile ).size(),
			new ProteinReader().read( inFile ).getNumAtoms()
		);
	}
	
	public static List<Spike> getClashes( Protein protein )
	{
		try
		{
			// write the protein out to a temp file
			File outFile = File.createTempFile( "getClashes.", ".pdb" );
			new ProteinWriter().write( protein, outFile );
			outFile.deleteOnExit();
			
			List<Spike> clashes = getClashes( outFile );
			
			// cleanup the temp file
			outFile.delete();
			
			return clashes;
		}
		catch( IOException ex )
		{
			m_log.warn( "Unable to compute clashes!", ex );
			return null;
		}
	}
	
	public static List<Spike> getClashes( File inFile )
	throws IOException
	{
		return getClashes( getSpikes( inFile ) );
	}
	
	public static List<Spike> getClashes( Map<AddressPair,List<Spike>> spikes )
	{
		// filter down to the smallest gap for each address pair
		Map<AddressPair,Spike> clashes = new HashMap<AddressPair,Spike>();
		for( Entry<AddressPair,List<Spike>> entry : spikes.entrySet() )
		{
			// find the min gap
			double minGap = Double.POSITIVE_INFINITY;
			for( Spike spike : entry.getValue() )
			{
				if( spike.gap < minGap )
				{
					minGap = spike.gap;
					clashes.put( entry.getKey(), spike );
				}
			}
		}
		
		// sort the clashes
		List<Spike> sortedClashes = new ArrayList<Spike>( clashes.size() );
		sortedClashes.addAll( clashes.values() );
		Collections.sort( sortedClashes, new Comparator<Spike>( )
		{
			@Override
			public int compare( Spike a, Spike b )
			{
				return Double.compare( a.gap, b.gap );
			}
		} );
		
		return sortedClashes;
	}
	
	public static Map<AddressPair,List<Spike>> getSpikes( Protein protein )
	{
		try
		{
			// write the protein out to a temp file
			File outFile = File.createTempFile( "getSpikes.", ".pdb" );
			new ProteinWriter().write( protein, outFile );
			outFile.deleteOnExit();
			
			Map<AddressPair,List<Spike>> spikes = getSpikes( outFile );
			
			// cleanup the temp file
			outFile.delete();
			
			return spikes;
		}
		catch( IOException ex )
		{
			m_log.warn( "Unable to compute spikes!", ex );
			return null;
		}
	}
	
	
	public static Map<AddressPair,List<Spike>> getSpikes( File inFile )
	throws IOException
	{
		// launch the probe tool to compute the clashes
		// -u -q -mc -het -once "alta ogt$ocutval not water" "alta ogt$ocutval"
		Process process = Runtime.getRuntime().exec(
			new String[] {
				"probe",
				"-q",
				"-u",
				"-mc",
				"-self",
				"all",
				inFile.getAbsolutePath()
			}
		);
		
		// read the result
		// NOTE: I don't think we need a separate thread for this since I want to block anyway
		// we'll see if it works... =P
		Set<AddressPair> hbondedAtoms = new HashSet<AddressPair>();
		Map<AddressPair,List<Spike>> spikes = new HashMap<AddressPair,List<Spike>>();
		BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		String line = null;
		while( ( line = in.readLine() ) != null )
		{
			Spike spike = parseSpike( line );
			if( spike == null )
			{
				continue;
			}
			
			if( spike.type.isHbond() )
			{
				// save all the hbonds
				hbondedAtoms.add( spike.addresses );
			}
			else if( spike.type.isClash() )
			{
				// does this entry meet our threshold?
				if( spike.gap < MinAllowedGap )
				{
					// add the spike to the list for this address pair
					List<Spike> spikesForAddresses = spikes.get( spike.addresses );
					if( spikesForAddresses == null )
					{
						spikesForAddresses = new ArrayList<Spike>();
						spikes.put( spike.addresses, spikesForAddresses );
					}
					spikesForAddresses.add( spike );
				}
			}
		}
		
		// remove clashes between hbonded atoms
		for( AddressPair pair : hbondedAtoms )
		{
			spikes.remove( pair );
		}
		
		return spikes;
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static double computeClashScore( int numClashes, int numAtoms )
	{
		// the clash score is pretty simple
		return numClashes*1000.0/numAtoms;
	}
	
	private static Spike parseSpike( String line )
	{
		// lines come in like this:
		// 12    3  4                5                6     7     8       9     10     11    12     131415      16    17     18   19
		// :1->2:wc:     1 GLY  C   :     2 TYR  O   :0.128:0.370:114.363:2.854:-6.082:0.000:0.0070:C:O:114.363:2.854:-6.082:0.00:0.00
		// 12    3  4                5                6      7      8     9     10    11    12      131415    16    17    18   19
		// :1->1:bo:   500 ANI  Y   :   500 ANI  PA2 :-1.286:-0.740:0.081:0.934:0.409:0.370:-0.2312:Y:P:0.081:0.918:0.778:0.00:0.00
		
		// here are the fields
		// 1    2   3    4       5        6      7   8   9   10  11       12    13    14    15161718    19
		// name:pat:type:srcAtom:targAtom:mingap:gap:spX:spY:spZ:spikeLen:score:stype:ttype:x:y:z:sBval:tBval:

		// we want type, srcAtom, targAtom, gap
		String[] parts = line.split( ":" );
		
		// get the type and filter
		Type type = Type.lookup( parts[2] );
		if( type == null )
		{
			return null;
		}
		
		Spike entry = new Spike();
		entry.type = type;
		entry.addresses = new AddressPair( parseAddress( parts[3] ), parseAddress( parts[4] ) );
		entry.gap = Double.parseDouble( parts[6] );
		entry.line = new Line3( parsePoint( parts, 7 ), parsePoint( parts, 14 ) );
		return entry;
	}
	
	private static AtomAddressReadable parseAddress( String text )
	{
		// the address is given in fixed-width format
		// "     1 GLY  C   "
		// "     2 TYR  O   "
		// "   500 ANI  OO2 "
		// "    47 ASN HD22 "
		// "    31 GLY  C   "
		// "    33 GLU  HN  "
		// " A   2 TYR  HA  "
		
		try
		{
			String subunitName = text.substring( 0, 2 ).trim();
			Integer residueNumber = Integer.parseInt( text.substring( 2, 6 ).trim() );
			String atomName = text.substring( 11, 16 ).trim();
			if( subunitName.isEmpty() )
			{
				return new AtomAddressReadable( residueNumber, atomName );
			}
			else
			{
				return new AtomAddressReadable( subunitName.charAt( 0 ), residueNumber, atomName );
			}
		}
		catch( Throwable ex )
		{
			throw new Error( "Unable to parse address: \"" + text + "\"!", ex );
		}
	}
	
	private static Vector3 parsePoint( String[] parts, int xPos )
	{
		return new Vector3(
			Double.parseDouble( parts[xPos + 0] ),
			Double.parseDouble( parts[xPos + 1] ),
			Double.parseDouble( parts[xPos + 2] )
		);
	}
}
