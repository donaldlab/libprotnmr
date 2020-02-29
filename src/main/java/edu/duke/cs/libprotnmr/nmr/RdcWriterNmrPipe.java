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
package edu.duke.cs.libprotnmr.nmr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Sequence;


public class RdcWriterNmrPipe
{
	public void write( List<Rdc<AtomAddressReadable>> rdcs, Sequence sequence, File file )
	throws IOException
	{
		/* Looks like this:
			#  D  is the dipolar coupling, Hz.
			#  DD is the estimated uncertainty in the dipolar coupling, Hz.
			#  W  is a multipication factor used to scale couplings for statistics such as RMS.
			
			DATA SEQUENCE MQIFVKTLTG KTITLEVEPS DTIENVKAKI QDKEGIPPDQ QRLIFAGKQL
			DATA SEQUENCE EDGRTLSDYN IQKESTLHLV LRLRGG
			
			VARS   RESID_I RESNAME_I ATOMNAME_I RESID_J RESNAME_J ATOMNAME_J D DD W
			FORMAT %5d %6s %6s %5d %6s %6s %9.3f %9.3f %.2f
			
			    2    GLN      N     2    GLN     HN   -15.524     1.000 1.00
			    3    ILE      N     3    ILE     HN    10.521     1.000 1.00
			    4    PHE      N     4    PHE     HN     9.648     1.000 1.00
			    5    VAL      N     5    VAL     HN     6.082     1.000 1.00
			    6    LYS      N     6    LYS     HN     3.854     1.000 1.00
		 */
		
		OutputStreamWriter out = new OutputStreamWriter( new FileOutputStream( file ) );
		
		// write the sequence (40 AA per line, in groups of 10)
		int residueId = 0;
		out.write( "\n" );
		while( residueId < sequence.getLength() )
		{
			out.write( "DATA SEQUENCE" );
			for( int i=0; i<4; i++ )
			{
				if( residueId < sequence.getLength() )
				{
					out.write( " " );
					for( int j=0; j<10; j++ )
					{
						if( residueId < sequence.getLength() )
						{
							out.write( sequence.getAminoAcidById( residueId++ ).getCode() );
						}
					}
				}
			}
			out.write( "\n" );
		}
		
		out.write( "\n" );
		out.write( "VARS   RESID_I RESNAME_I ATOMNAME_I RESID_J RESNAME_J ATOMNAME_J D DD W\n" );
		out.write( "FORMAT %5d %6s %6s %5d %6s %6s %9.3f %9.3f %.2f\n" );
		out.write( "\n" );
		
		// write the RDCs
		// "    2    GLN      N     2    GLN     HN   -15.524     1.000 1.00"
		for( Rdc<AtomAddressReadable> rdc : rdcs )
		{
			out.write( String.format( "%5d %6s %6s %5d %6s %6s %9.3f %9.3f %.2f\n",
				rdc.getFrom().getResidueNumber(),
				sequence.getAminoAcidByNumber( rdc.getFrom().getResidueNumber() ).getAbbreviation(),
				rdc.getFrom().getAtomName(),
				rdc.getTo().getResidueNumber(),
				sequence.getAminoAcidByNumber( rdc.getTo().getResidueNumber() ).getAbbreviation(),
				rdc.getTo().getAtomName(),
				rdc.getValue(),
				rdc.getError(),
				1.0
			) );
		}
		
		out.close();
	}
}
