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
