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

package edu.duke.cs.libprotnmr.nmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;


public class AssignReader
{
	/**************************
	 *   Definitions
	 **************************/
	
	private enum Token
	{
		Assign( null, true, "assign" ),
		Segid( null, true, "segid" ),
		Resid( null, true, "resid", "residue" ),
		Name( null, true, "name" ),
		Or( null, true, "or" ),
		And( null, true, "and" ),
		OpenGroup( "(", true, "\\(" ),
		CloseGroup( ")", true, "\\)" ),
		UnambiguousComment( "!", true, "\\!" ),
		AmbiguousComment( "#", false, "#" ),
		Number( null, true, "\\-?\\d*\\.?\\d*" );
		
		private String m_replace;
		private boolean m_doSpacing;
		private ArrayList<String> m_patterns;
		
		public static Token lookup( String in )
		{
			for( Token token : Token.values() )
			{
				for( String pattern : token.getPatterns() )
				{
					Pattern regex = Pattern.compile( pattern, Pattern.CASE_INSENSITIVE );
					if( regex.matcher( in ).matches() )
					{
						return token;
					}
				}
			}
			return null;
		}
		
		private Token( String replace, boolean doSpacing, String ... patterns )
		{
			m_replace = replace;
			m_doSpacing = doSpacing;
			m_patterns = Transformer.toArrayList( patterns );
		}
		
		public String getReplace( )
		{
			return m_replace;
		}
		
		public boolean doSpacing( )
		{
			return m_doSpacing;
		}
		
		public List<String> getPatterns( )
		{
			return Collections.unmodifiableList( m_patterns );
		}
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public ArrayList<Assign> read( String path )
	throws IOException
	{
		return read( new File( path ) );
	}
	
	public ArrayList<Assign> read( File file )
	throws IOException
	{
		return read( new FileInputStream( file ) );
	}
	
	public ArrayList<Assign> read( InputStream in )
	throws IOException
	{
		/* need to be able to read lines that look like this:
			assign (resid 7 and name ha1 and segid A)(resid 15 and name hd# and segid C) 4.0 2.2 4.0
			assign (resid 7 and name ha1 and segid A)((resid 15 and name hd# and segid C)
				or (resid 15 and name hd# and segid B)) 4.0 2.2 4.0
		*/
		
		ArrayList<Assign> assigns = new ArrayList<Assign>();
		Assign assign = null;
		ArrayList<AtomAddressReadable> addresses = new ArrayList<AtomAddressReadable>();
		AtomAddressReadable address = new AtomAddressReadable();
		int groupDepth = 0;
		
		// for each line...
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = null;
		while( ( line = reader.readLine() ) != null )
		{
			// skip blank lines
			line = line.trim();
			if( line.length() <= 0 )
			{
				continue;
			}
			
			// preprocess to make sure tokens are spaced out
			line = performSpacing( line );
			
			// tokenize
			StringTokenizer tokenizer = new StringTokenizer( line );
			while( tokenizer.hasMoreTokens() )
			{
				// get the next token
				String stringToken = tokenizer.nextToken();
				Token token = Token.lookup( stringToken );
				if( token == null )
				{
					continue;
				}
				
				switch( token )
				{
					case Assign:
						// do we need to close the old assign?
						if( assign != null && assign.getNumbers().size() > 0 )
						{
							assigns.add( assign );
							assign = new Assign();
						}
						
						// start a new assign
						assign = new Assign();
					break;
					
					case Segid:
						address.setSubunitName( tokenizer.nextToken().charAt( 0 ) );
					break;
					
					case Resid:
						address.setResidueNumber( Integer.parseInt( tokenizer.nextToken() ) );
					break;
					
					case Name:
						address.setAtomName( tokenizer.nextToken() );
					break;
					
					case OpenGroup:
						groupDepth++;
						
						// make a new address if needed
						if( address == null )
						{
							address = new AtomAddressReadable();
						}
					break;
					
					case CloseGroup:
						groupDepth--;
						
						// finish the address if needed
						if( address != null )
						{
							addresses.add( address );
							address = null;
						}
						
						if( groupDepth == 0 )
						{
							// we just finished a group
							if( assign != null )
							{
								assign.getAddresses().add( addresses );
							}
							addresses = new ArrayList<AtomAddressReadable>();
						}
					break;
					
					case And:
						// just ignore
					break;
					
					case Or:
						// just ignore
					break;
					
					case UnambiguousComment:
						// skip the rest of this line
						while( tokenizer.hasMoreTokens() )
						{
							tokenizer.nextToken();
						}
					break;
					
					case AmbiguousComment:
						// only process this comment if it's the first token on the line
						String firstToken = new StringTokenizer( line ).nextToken();
						if( firstToken.equals( stringToken ) )
						{
							// skip the rest of this line
							while( tokenizer.hasMoreTokens() )
							{
								tokenizer.nextToken();
							}
						}
					break;
					
					case Number:
						if( assign != null )
						{
							assign.getNumbers().add( Double.parseDouble( stringToken ) );
						}
					break;
				}
			}
			
		}
		
		// do we need to close the current assign?
		if( assign != null && assign.getNumbers().size() > 0 )
		{
			assigns.add( assign );
			assign = new Assign();
		}
		
		return assigns;
	}
	
	/*
	private Assign processComment( ArrayList<Assign> assigns, Assign assign, StringTokenizer tokenizer )
	{
		// skip the rest of the line
		while( tokenizer.hasMoreTokens() )
		{
			//tokenizer.nextToken();
			// but if we commented an assign, close the current assign if necessary
			if( Token.lookup( tokenizer.nextToken() ) == Token.Assign )
			{
				if( assign != null )
				{
					if( assign.getNumbers().size() > 0 )
					{
						assigns.add( assign );
					}
					assign = null;
				}
			}
		}
		return assign;
	}
	*/
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private String performSpacing( String in )
	{
		for( Token token : Token.values() )
		{
			if( token.doSpacing() )
			{
				String replace = token.getReplace();
				if( replace != null )
				{
					for( String pattern : token.getPatterns() )
					{
						in = in.replaceAll( pattern, " " + replace + " " );
					}
				}
			}
		}
		
		return in;
	}
}
