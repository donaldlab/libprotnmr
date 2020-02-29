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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.EigPair;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.perf.LoggingMessageListener;
import edu.duke.cs.libprotnmr.perf.MessageListener;
import edu.duke.cs.libprotnmr.perf.Progress;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.HasAtoms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class AlignmentTensor implements Serializable
{
	private static final long serialVersionUID = 7665466111987682915L;
	

	/**************************
	 *   Data Members
	 **************************/
	
	private static final Logger m_log = Logging.getLog( AlignmentTensor.class );
	
	private double m_Sxy;
	private double m_Sxz;
	private double m_Syy;
	private double m_Syz;
	private double m_Szz;
	
	private EigPair[] m_eigs;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public AlignmentTensor( double Sxy, double Sxz, double Syy, double Syz, double Szz )
	{
		m_Sxy = Sxy;
		m_Sxz = Sxz;
		m_Syy = Syy;
		m_Syz = Syz;
		m_Szz = Szz;
		
		// construct the saupe matrix
		Matrix saupe = new Matrix( 3, 3 );
		getSaupe().toJama( saupe );
		
		// sort eigenvalues (along with eigenvectors) in order of increasing magnitude
		EigenvalueDecomposition eig = saupe.eig();
		PriorityQueue<EigPair> q = new PriorityQueue<EigPair>( 3 );
		q.add( new EigPair( eig, 0 ) );
		q.add( new EigPair( eig, 1 ) );
		q.add( new EigPair( eig, 2 ) );
		m_eigs = new EigPair[3];
		m_eigs[AlignmentTensorAxis.X.ordinal()] = q.poll();
		m_eigs[AlignmentTensorAxis.Y.ordinal()] = q.poll();
		m_eigs[AlignmentTensorAxis.Z.ordinal()] = q.poll();
		
		// force the basis to be right-handed
		if( !isBasisRightHanded() )
		{
			m_eigs[AlignmentTensorAxis.Z.ordinal()].getEigenvector().negate();
		}
	}
	
	public AlignmentTensor( double Dxx, double Dyy, double Dzz, Vector3 x, Vector3 y, Vector3 z )
	{
		// compute the saupe elements
		m_Sxy = Dxx * x.x * x.y  +  Dyy * y.x * y.y  +  Dzz * z.x * z.y;
		m_Sxz = Dxx * x.x * x.z  +  Dyy * y.x * y.z  +  Dzz * z.x * z.z;
		m_Syy = Dxx * x.y * x.y  +  Dyy * y.y * y.y  +  Dzz * z.y * z.y;
		m_Syz = Dxx * x.y * x.z  +  Dyy * y.y * y.z  +  Dzz * z.y * z.z;
		m_Szz = Dxx * x.z * x.z  +  Dyy * y.z * y.z  +  Dzz * z.z * z.z;
		
		// save the eigenvectors
		PriorityQueue<EigPair> q = new PriorityQueue<EigPair>( 3 );
		q.add( new EigPair( Dxx, x ) );
		q.add( new EigPair( Dyy, y ) );
		q.add( new EigPair( Dzz, z ) );
		m_eigs = new EigPair[3];
		m_eigs[AlignmentTensorAxis.X.ordinal()] = q.poll();
		m_eigs[AlignmentTensorAxis.Y.ordinal()] = q.poll();
		m_eigs[AlignmentTensorAxis.Z.ordinal()] = q.poll();
	}
	

	/**************************
	 *   Accessors
	 **************************/
	
	public double getSxx( )
	{
		return 0.0 - m_Syy - m_Szz;
	}
	
	public double getSxy( )
	{
		return m_Sxy;
	}
	
	public double getSxz( )
	{
		return m_Sxz;
	}
	
	public double getSyx( )
	{
		return m_Sxy;
	}
	
	public double getSyy( )
	{
		return m_Syy;
	}
	
	public double getSyz( )
	{
		return m_Syz;
	}
	
	public double getSzx( )
	{
		return m_Sxz;
	}
	
	public double getSzy( )
	{
		return m_Syz;
	}
	
	public double getSzz( )
	{
		return m_Szz;
	}
	
	public double getDxx( )
	{
		return getEigenvalue( AlignmentTensorAxis.X );
	}
	
	public double getDyy( )
	{
		return getEigenvalue( AlignmentTensorAxis.Y );
	}
	
	public double getDzz( )
	{
		return getEigenvalue( AlignmentTensorAxis.Z );
	}
	
	public double getEigenvalue( AlignmentTensorAxis axis )
	{
		return m_eigs[axis.ordinal()].getEigenvalue();
	}
	
	public Vector3 getXAxis( )
	{
		return getAxis( AlignmentTensorAxis.X );
	}
	
	public Vector3 getYAxis( )
	{
		return getAxis( AlignmentTensorAxis.Y );
	}
	
	public Vector3 getZAxis( )
	{
		return getAxis( AlignmentTensorAxis.Z );
	}
	
	public Vector3 getAxis( AlignmentTensorAxis axis )
	{
		return m_eigs[axis.ordinal()].getEigenvector();
	}
	
	public double getAsymmetry( )
	{
		return ( getDxx() - getDyy() ) / getDzz();
	}
	
	public double getRhombicity( )
	{
		return getAsymmetry() * 2.0 / 3.0;
	}
	
	public void getBasis( Matrix3 out )
	{
		// represents a rotation from the PoF to the molecular frame
		out.setColumns( getXAxis(), getYAxis(), getZAxis() );
	}
	
	public void getRotPofToMol( Matrix3 out )
	{
		getBasis( out );
	}
	
	public void getRotMolToPof( Matrix3 out )
	{
		getBasis( out );
		out.transpose();
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static AlignmentTensor newTensorWithRotation( AlignmentTensor tensor, Matrix3 rotPofToMol )
	{
		return newTensorWithRotation(
			tensor.getDxx(),
			tensor.getDyy(),
			tensor.getDzz(),
			rotPofToMol
		);
	}
	
	public static AlignmentTensor newTensorWithRotation( double Dxx, double Dyy, double Dzz, Matrix3 rotPofToMol )
	{
		Vector3 xAxis = new Vector3();
		Vector3 yAxis = new Vector3();
		Vector3 zAxis = new Vector3();
		rotPofToMol.getXAxis( xAxis );
		rotPofToMol.getYAxis( yAxis );
		rotPofToMol.getZAxis( zAxis );
		return new AlignmentTensor(
			Dxx, Dyy, Dzz,
			xAxis, yAxis, zAxis
		);
	}
	
	public static AlignmentTensor compute( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs, Matrix3 rotPofToMol )
	{
		// compute the new optimal eigenvalues
		
		// get the other rotation
		Matrix3 rotMolToPof = new Matrix3( rotPofToMol );
		rotMolToPof.transpose();
		
		// build the matrix of vector products
		Matrix A = new Matrix( rdcs.size(), 2 );
		int row = 0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			Atom fromAtom = protein.getAtom( rdc.getFrom() );
			Atom toAtom = protein.getAtom( rdc.getTo() );
			
			// no atoms?
			if( fromAtom == null || toAtom == null )
			{
				continue;
			}
			
			// get the internuclear vector
			Vector3 vec = new Vector3( toAtom.getPosition() );
			vec.subtract( fromAtom.getPosition() );
			vec.normalize();
			
			// rotate it into the PoF
			rotMolToPof.multiply( vec );
			
			A.set( row, 0, vec.z*vec.z - vec.x*vec.x );
			A.set( row, 1, vec.z*vec.z - vec.y*vec.y );
			
			row++;
		}
		if( row < 2 )
		{
			throw new IllegalArgumentException( "Must have at least 2 RDCs to compute alignment tensor eigenvalues. Only found " + row );
		}
		
		// build the vector of rdc values
		Matrix b = new Matrix( rdcs.size(), 1 );
		row = 0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			b.set( row++, 0, rdc.getValue() );
		}
		
		// solve Ax = b for x
		// this implementation uses QR decomposition to find the least-squares solution
		Matrix x = A.solve( b );
		assert( x.getRowDimension() == 2 && x.getColumnDimension() == 1 );
		
		// the eigenvalues should all add up to zero
		double e1 = x.get( 0, 0 );
		double e2 = x.get( 1, 0 );
		double e3 = 0 - e1 - e2;
		
		// build the tensor, but make sure we get the eigenvalue signs right
		// there are only two choices. Pick the one that minimizes the RMSD
		AlignmentTensor tensorA = newTensorWithRotation( e1, e2, e3, rotPofToMol );
		AlignmentTensor tensorB = newTensorWithRotation( -e1, -e2, -e3, rotPofToMol );
		if( tensorA.getRmsd( protein, rdcs ) < tensorB.getRmsd( protein, rdcs ) )
		{
			return tensorA;
		}
		else
		{
			return tensorB;
		}
	}
	
	public static AlignmentTensor compute( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		// build the matrix of vector products
		Matrix A = new Matrix( rdcs.size(), 5 );
		int row = 0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			Atom fromAtom = protein.getAtom( rdc.getFrom() );
			Atom toAtom = protein.getAtom( rdc.getTo() );
			
			// no atoms?
			if( fromAtom == null || toAtom == null )
			{
				continue;
			}
			
			// get the internuclear vector
			Vector3 vec = new Vector3( toAtom.getPosition() );
			vec.subtract( fromAtom.getPosition() );
			vec.normalize();
			
			A.set( row, 0, 2.0 * vec.x * vec.y );
			A.set( row, 1, 2.0 * vec.x * vec.z );
			A.set( row, 2, vec.y * vec.y - vec.x * vec.x );
			A.set( row, 3, 2.0 * vec.y * vec.z );
			A.set( row, 4, vec.z * vec.z - vec.x * vec.x );
			
			row++;
		}
		if( row < 5 )
		{
			throw new IllegalArgumentException( "Must have at least 5 RDCs to compute an alignment tensor. Only found " + row );
		}
		
		// build the vector of rdc values
		Matrix b = new Matrix( rdcs.size(), 1 );
		row = 0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			b.set( row++, 0, rdc.getValue() );
		}
		
		// solve Ax = b for x
		// this implementation uses QR decomposition to find the least-squares solution
		Matrix x = A.solve( b );
		
		/* if we need it, we could use SVD explicitly
		SingularValueDecomposition svd = A.svd();
		Matrix sigmaInverse = svd.getS();
		for( int i=0; i<5; i++ )
		{
			sigmaInverse.set( i, i, 1.0 / sigmaInverse.get( i, i ) );
		}
		Matrix x = svd.getV().times( sigmaInverse.times( svd.getU().transpose().times( b ) ) );
		*/
		
		// build the tensor
		assert( x.getRowDimension() == 5 && x.getColumnDimension() == 1 );
		AlignmentTensor tensor = new AlignmentTensor(
			x.get( 0, 0 ),
			x.get( 1, 0 ),
			x.get( 2, 0 ),
			x.get( 3, 0 ),
			x.get( 4, 0 )
		);		
		return tensor;
	}
	
	public static List<AlignmentTensor> compute( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs, int numSamples )
	{
		return compute( protein, rdcs, numSamples, Rdc.DefaultSamplingModel );
	}
	
	public static List<AlignmentTensor> compute( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs, int numSamples, Rdc.SamplingModel model )
	{
		return compute( protein, rdcs, numSamples, model, new LoggingMessageListener( m_log, Level.INFO ) );
	}
	
	public static List<AlignmentTensor> compute( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs, int numSamples, Rdc.SamplingModel model, MessageListener listener )
	{
		// make a copy of the RDCs so we can modify them
		List<Rdc<AtomAddressInternal>> sampledRdcs = Rdc.copyDeep( rdcs );
		
		// start a progresss bar if needed
		Progress progress = null;
		if( listener != null )
		{
			listener.message( "Sampling " + numSamples + " sets of RDCs..." );
			progress = new Progress( numSamples, 5000 );
			progress.setMessageListener( listener );
		}
		
		// for each sample...
		ArrayList<AlignmentTensor> tensors = new ArrayList<AlignmentTensor>();
		double maxDeviation = 0.0;
		for( int i=0; i<numSamples; i++ )
		{
			// compute and score the tensor computed from the sampled RDCs
			Rdc.sample( sampledRdcs, rdcs, model );
			tensors.add( compute( protein, sampledRdcs ) );
			
			// update max rdc value deviation
			for( int j=0; j<rdcs.size(); j++ )
			{
				maxDeviation = Math.max( maxDeviation, Math.abs( rdcs.get( j ).getValue() - sampledRdcs.get( j ).getValue() ) );
			}
			
			// update progress if needed
			if( progress != null )
			{
				progress.incrementProgress();
			}
		}
		
		// find the max RDC value deviation
		listener.message( "Max RDC value deviation is " + maxDeviation );
		
		return tensors;
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public String getStats( )
	{
		return getStats( null, null );
	}
	
	public String getStats( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		StringBuilder buf = new StringBuilder();
		buf.append( "Computed Alignment Tensor:" );
		buf.append( "\n\tEigenvalues:" );
		buf.append( String.format( "\n\t\tDxx: %f", getDxx() ) );
		buf.append( String.format( "\n\t\tDyy: %f", getDyy() ) );
		buf.append( String.format( "\n\t\tDzz: %f", getDzz() ) );
		buf.append( "\n\tProperties:" );
		buf.append( String.format( "\n\t\tAsymmetry: %f", getAsymmetry() ) );
		buf.append( String.format( "\n\t\tRhombicity: %f", getRhombicity() ) );
		if( protein != null && rdcs != null )
		{
			buf.append( String.format( "\n\t\tRDC RMSD: %f", getRmsd( protein, rdcs ) ) );
		}
		return buf.toString();
	}
	
	public Matrix3 getSaupe( )
	{
		return new Matrix3(
			-m_Syy-m_Szz, m_Sxy, m_Sxz,
			m_Sxy, m_Syy, m_Syz,
			m_Sxz, m_Syz, m_Szz
		);
	}
	
	public double getViolation( HasAtoms protein, Rdc<AtomAddressInternal> rdc )
	{
		Vector3 vec = new Vector3( protein.getAtom( rdc.getTo() ).getPosition() );
		vec.subtract( protein.getAtom( rdc.getFrom() ).getPosition() );
		vec.normalize();
		double backcalcRdc = backComputeRdc( vec );
		
		return Math.max( 0, Math.abs( backcalcRdc - rdc.getValue() ) - rdc.getError() );
	}
	
	public boolean isSatisfied( HasAtoms protein, Rdc<AtomAddressInternal> rdc )
	{
		return CompareReal.lte( getViolation( protein, rdc ), 0 );
	}
	
	public double getRmsd( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		double sum = 0.0;
		ArrayList<Double> values = backComputeRdcs( protein, rdcs );
		for( int i=0; i<values.size(); i++ )
		{
			Double value = values.get( i );
			if( value != null )
			{
				double diff = Math.abs( value - rdcs.get( i ).getValue() );
				sum += diff * diff;
			}
		}
		return Math.sqrt( sum / values.size() );
	}
	
	public double getQFactor( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		return getRmsd( protein, rdcs ) / getRmsValue( protein, rdcs );
	}
	
	public double getRmsValue( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		double sum = 0.0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			sum += rdc.getValue() * rdc.getValue();
		}
		return Math.sqrt( sum / rdcs.size() );
	}
	
	public double getRmsViolation( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		double sum = 0.0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			double violation = getViolation( protein, rdc );
			sum += violation*violation;
		}
		return Math.sqrt( sum / rdcs.size() );
	}
	
	public double getQViolation( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		return getRmsViolation( protein, rdcs ) / getRmsValue( protein, rdcs );
	}
	
	public double getMaxViolation( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		double maxViolation = 0.0;
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			double violation = getViolation( protein, rdc );
			maxViolation = Math.max( maxViolation, violation );
		}
		return maxViolation;
	}
	
	public double backComputeRdc( Vector3 vec )
	{
		return m_Sxy * 2.0 * vec.x * vec.y
				+ m_Sxz * 2.0 * vec.x * vec.z
				+ m_Syy * ( vec.y * vec.y - vec.x * vec.x )
				+ m_Syz * 2.0 * vec.y * vec.z
				+ m_Szz * ( vec.z * vec.z - vec.x * vec.x );
	}
	
	public ArrayList<Double> backComputeRdcs( HasAtoms protein, List<Rdc<AtomAddressInternal>> rdcs )
	{
		ArrayList<Double> values = new ArrayList<Double>( rdcs.size() );
		for( Rdc<AtomAddressInternal> rdc : rdcs )
		{
			values.add( backComputeRdc( protein, rdc ) );
		}
		return values;
	}
	
	public Double backComputeRdc( HasAtoms protein, Rdc<AtomAddressInternal> rdc )
	{
		double sum = 0.0;
		int numAssignmentsUsed = 0;
		for( Assignment<AtomAddressInternal> assignment : rdc )
		{
			// get the internuclear vector
			Atom fromAtom = protein.getAtom( assignment.getLeft() );
			assert( fromAtom != null );
			Atom toAtom = protein.getAtom( assignment.getRight() );
			assert( toAtom != null );
			Vector3 vec = new Vector3( toAtom.getPosition() );
			vec.subtract( fromAtom.getPosition() );
			vec.normalize();
			
			// update the value
			sum += backComputeRdc( vec );
			numAssignmentsUsed++;
		}
		
		if( numAssignmentsUsed > 0 )
		{
			return sum;
		}
		return null;
	}
	
	public <T extends AtomAddress<T>> boolean isRdcInRange( Rdc<T> rdc )
	{
		return isRdcInRange( rdc.getValue() );
	}
	
	public boolean isRdcInRange( double d )
	{
		if( getDzz() > 0 )
		{
			return d >= getDyy() && d <= getDzz();
		}
		else
		{
			return d >= getDzz() && d <= getDyy();
		}
	}
	
	public <T extends AtomAddress<T>> List<Rdc<T>> getOutOfRangeRdcs( List<Rdc<T>> rdcs )
	{
		List<Rdc<T>> outOfRangeRdcs = new ArrayList<Rdc<T>>();
		for( Rdc<T> rdc : rdcs )
		{
			if( !isRdcInRange( rdc ) )
			{
				outOfRangeRdcs.add( rdc );
			}
		}
		return outOfRangeRdcs;
	}
	
	public double getDistance( AlignmentTensor other )
	{
		// convert each tensor to a quaternion
		Matrix3 basis = new Matrix3();
		this.getBasis( basis );
		Quaternion qMine = new Quaternion();
		Quaternion.getRotation( qMine, basis );
		
		other.getBasis( basis );
		Quaternion qOther = new Quaternion();
		Quaternion.getRotation( qOther, basis );
		
		return qMine.getDistance( qOther );
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Double.valueOf( m_Sxy ).hashCode(),
			Double.valueOf( m_Sxz ).hashCode(),
			Double.valueOf( m_Syy ).hashCode(),
			Double.valueOf( m_Syz ).hashCode(),
			Double.valueOf( m_Szz ).hashCode()
		);
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other instanceof AlignmentTensor )
		{
			return equals( (AlignmentTensor)other );
		}
		return false;
	}
	
	public boolean equals( AlignmentTensor other )
	{
		return m_Sxy == other.m_Sxy
			&& m_Sxz == other.m_Sxz
			&& m_Syy == other.m_Syy
			&& m_Syz == other.m_Syz
			&& m_Szz == other.m_Szz;
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private boolean isBasisRightHanded( )
	{
		Vector3 x = m_eigs[AlignmentTensorAxis.X.ordinal()].getEigenvector();
		Vector3 y = m_eigs[AlignmentTensorAxis.Y.ordinal()].getEigenvector();
		Vector3 z = m_eigs[AlignmentTensorAxis.Z.ordinal()].getEigenvector();
		
		// ( x cross y ) dot z should be positive
		Vector3 temp = new Vector3();
		x.getCross( temp, y );
		return temp.getDot( z ) > 0.0;
	}
}
