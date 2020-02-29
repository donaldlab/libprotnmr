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

package edu.duke.cs.libprotnmr.cgal.curves;

public enum BandPart
{
	Min
	{
		@Override
		public double getRdcValue( RdcBand band )
		{
			return Math.max( band.getTensor().getDyy(), band.getRdcValue() - band.getRdcError() );
		}
		
		@Override
		public double getTheta( KinematicBand band )
		{
			return Math.max( -Math.PI, band.getTheta() - band.getDTheta() );
		}
	},
	Mid
	{
		@Override
		public double getRdcValue( RdcBand band )
		{
			return band.getRdcValue();
		}
		
		@Override
		public double getTheta( KinematicBand band )
		{
			return band.getTheta();
		}
	},
	Max
	{
		@Override
		public double getRdcValue( RdcBand band )
		{
			return Math.min( band.getTensor().getDzz(), band.getRdcValue() + band.getRdcError() );
		}
		
		@Override
		public double getTheta( KinematicBand band )
		{
			return Math.min( Math.PI, band.getTheta() + band.getDTheta() );
		}
	};
	
	public abstract double getRdcValue( RdcBand band );
	public abstract double getTheta( KinematicBand band );
}
