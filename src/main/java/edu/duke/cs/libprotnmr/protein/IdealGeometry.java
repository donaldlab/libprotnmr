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
package edu.duke.cs.libprotnmr.protein;

public class IdealGeometry
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	// Ideal geometry values from Engh and Huber '91
	// also grabbed H atom bond lengths and the out-of-plane angle from REDUCE
	
	// dihedral angles
	public static final double AngleOmega = Math.toRadians( 180.0 ); // UNDONE: the ideal value isn't exactly 180.
	
	// planar angles
	public static final double AngleNCaC = Math.toRadians( 111.2 ); // sigma = 2.8
	public static final double AngleCaCN = Math.toRadians( 116.2 ); // sigma = 2.0
	public static final double AngleCNCa = Math.toRadians( 121.7 ); // sigma = 1.8
	public static final double AngleHNCa = Math.PI - AngleCNCa / 2.0;
	public static final double AngleCaCO = Math.toRadians( 120.8 ); // sigma = 1.7
	public static final double AngleOCN = Math.PI * 2.0 - AngleCaCO - AngleCaCN;
	public static final double AngleCNH = AngleHNCa;
	public static final double AngleHaOutOfPlane = Math.toRadians( 126.5 );
	
	// bond lengths
	public static final double LengthNCa = 1.458; // sigma = 0.019 (for glycine, 1.451 sigma = 0.016)
	public static final double LengthCaC = 1.515; // sigma = 0.021 (for glycine, 1.516 sigma = 0.018)
	public static final double LengthCN = 1.329; // sigma = 0.014
	public static final double LengthNH = 1.0; // polar H atoms are 1.0 A away (non-polar are 1.1)
	public static final double LengthCO = 1.231; // sigma = 0.020
	public static final double LengthCaHa = 1.1;
	
	// average phi,psis for SSEs
	public static final double HelixPhi = Math.toRadians( -65.3 );
	public static final double HelixPsi = Math.toRadians( -39.4 );
	public static final double StrandPhi = Math.toRadians( -120.0 );
	public static final double StrandPsi = Math.toRadians( 138.0 );
}
