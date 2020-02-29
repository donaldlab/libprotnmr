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

package edu.duke.cs.libprotnmr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import edu.duke.cs.libprotnmr.bond.Bond;
import edu.duke.cs.libprotnmr.clustering.Cluster;
import edu.duke.cs.libprotnmr.clustering.distance.DistanceMatrix;
import edu.duke.cs.libprotnmr.geom.Annulus2;
import edu.duke.cs.libprotnmr.geom.Sphere;
import edu.duke.cs.libprotnmr.geom.Vector2;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.Logging;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.math.IndexPair;
import edu.duke.cs.libprotnmr.math.IndexPairIterator;
import edu.duke.cs.libprotnmr.math.Matrix3;
import edu.duke.cs.libprotnmr.math.Matrix4;
import edu.duke.cs.libprotnmr.math.MultiAxisAlignedBox;
import edu.duke.cs.libprotnmr.math.MultiVector;
import edu.duke.cs.libprotnmr.math.Quaternion;
import edu.duke.cs.libprotnmr.nmr.DistanceRestraint;
import edu.duke.cs.libprotnmr.protein.Atom;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Residue;
import edu.duke.cs.libprotnmr.protein.Subunit;

public abstract class ExtendedTestCase extends TestCase
{
	protected static Random m_rand = new Random();
	
	public void setUp( )
	throws Exception
	{
		Logging.Normal.init();
	}
	
	protected <T> void assertSameInList( Collection<T> expected, T observed )
	{
		boolean found = false;
		for( T t : expected )
		{
			if( t == observed )
			{
				found = true;
			}
		}
		assertTrue( found );
	}
	
	protected void assertEqualsReal( double expected, double observed )
	{
		assertEquals( expected, observed, CompareReal.getEpsilon() );
	}
	
	protected void assertEquals( Vector2 expected, Vector2 observed )
	{
		assertEquals( expected, observed, CompareReal.getEpsilon() );
	}
	
	protected void assertEquals( Vector2 expected, Vector2 observed, double epsilon )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		boolean same =
			CompareReal.eq( expected.x, observed.x, epsilon )
			&& CompareReal.eq( expected.y, observed.y, epsilon );
		if( !same )
		{
			fail( "Vectors not same! expected: " + expected + " but was: " + observed + "" );
		}
	}
	
	protected void assertEquals( Vector3 expected, Vector3 observed )
	{
		assertEquals( expected, observed, CompareReal.getEpsilon() );
	}
	
	protected void assertEquals( Vector3 expected, Vector3 observed, double epsilon )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		boolean same =
			CompareReal.eq( expected.x, observed.x, epsilon )
			&& CompareReal.eq( expected.y, observed.y, epsilon )
			&& CompareReal.eq( expected.z, observed.z, epsilon );
		if( !same )
		{
			fail( "Vectors not same! expected: " + expected + " but was: " + observed + "" );
		}
	}
	
	protected void assertEquals( Sphere expected, Sphere observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.center, observed.center );
		assertEquals( expected.radius, observed.radius, CompareReal.getEpsilon() );
	}
	
	protected void assertEquals( Matrix4 expected, Matrix4 observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		for( int i=0; i<Matrix4.Dimension; i++ )
		{
			for( int j=0; j<Matrix4.Dimension; j++ )
			{
				assertEquals( expected.data[i][j], observed.data[i][j], CompareReal.getEpsilon() );
			}
		}
	}
	
	protected void assertEquals( Matrix3 expected, Matrix3 observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		for( int i=0; i<Matrix3.Dimension; i++ )
		{
			for( int j=0; j<Matrix3.Dimension; j++ )
			{
				assertEquals( expected.data[i][j], observed.data[i][j], CompareReal.getEpsilon() );
			}
		}
	}
	
	protected void assertEquals( Annulus2 expected, Annulus2 observed )
	{
		assertEquals( expected, observed, CompareReal.getEpsilon() );
	}
	
	protected void assertEquals( Annulus2 expected, Annulus2 observed, double epsilon )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		try
		{
			assertEquals( expected.center, observed.center, epsilon );
			assertEquals( expected.minRadius, observed.minRadius, epsilon );
			assertEquals( expected.maxRadius, observed.maxRadius, epsilon );
		}
		catch( AssertionFailedError err )
		{
			throw new AssertionFailedError( "expected:<" + expected + "> but was:<" + observed + ">" );
		}
	}
	
	protected void assertEquals( AtomAddressInternal expected, AtomAddressInternal observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getSubunitId(), observed.getSubunitId() );
		assertEquals( expected.getResidueId(), observed.getResidueId() );
		assertEquals( expected.getAtomId(), observed.getAtomId() );
	}
	
	protected void assertEquals( Atom expected, Atom observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getId(), observed.getId() );
		assertEquals( expected.getNumber(), observed.getNumber() );
		assertEquals( expected.getName(), observed.getName() );
		assertEquals( expected.getResidueId(), observed.getResidueId() );
		assertEquals( expected.getElement(), observed.getElement() );
		assertEquals( expected.isBackbone(), observed.isBackbone() );
		assertNotSame( expected.getPosition(), observed.getPosition() );
		assertEquals( expected.getPosition(), observed.getPosition() );
		assertEquals( expected.getOccupancy(), observed.getOccupancy() );
		assertEquals( expected.getTempFactor(), observed.getTempFactor() );
	}
	
	protected void assertEquals( Residue expected, Residue observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getId(), observed.getId() );
		assertEquals( expected.getNumber(), observed.getNumber() );
		assertEquals( expected.getFirstAtomNumber(), observed.getFirstAtomNumber() );
		assertEquals( expected.getAtoms().size(), observed.getAtoms().size() );
		assertEquals( expected.getAminoAcid(), observed.getAminoAcid() );
		
		// check all the atoms just in case
		for( int i=0; i<expected.getAtoms().size(); i++ )
		{
			assertNotSame( expected.getAtoms().get( i ), observed.getAtoms().get( i ) );
			assertEquals( expected.getAtoms().get( i ), observed.getAtoms().get( i ) );
		}
	}
	
	protected void assertEquals( Subunit expected, Subunit observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getId(), observed.getId() );
		assertEquals( expected.getFirstResidueNumber(), observed.getFirstResidueNumber() );
		assertEquals( expected.getLastResidueNumber(), observed.getLastResidueNumber() );
		assertEquals( expected.getResidues().size(), observed.getResidues().size() );
		assertEquals( expected.atoms().size(), observed.atoms().size() );
		assertEquals( expected.backboneAtoms().size(), observed.backboneAtoms().size() );
		
		// check residues
		for( int i=0; i<expected.getResidues().size(); i++ )
		{
			assertNotSame( expected.getResidues().get( i ), observed.getResidues().get( i ) );
			assertEquals( expected.getResidues().get( i ), observed.getResidues().get( i ) );
		}
		
		// check atom indices
		for( int i=0; i<expected.atoms().size(); i++ )
		{
			assertNotSame( expected.atoms().get( i ), observed.atoms().get( i ) );
			assertEquals( expected.atoms().get( i ), observed.atoms().get( i ) );
		}
		for( int i=0; i<expected.backboneAtoms().size(); i++ )
		{
			assertNotSame( expected.backboneAtoms().get( i ), observed.backboneAtoms().get( i ) );
			assertEquals( expected.backboneAtoms().get( i ), observed.backboneAtoms().get( i ) );
		}
	}
	
	protected void assertEquals( Protein expected, Protein observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getName(), observed.getName() );
		assertEquals( expected.getSubunits().size(), observed.getSubunits().size() );
		assertEquals( expected.getNumAtoms(), observed.getNumAtoms() );
		
		// check subunits
		for( int i=0; i<expected.getSubunits().size(); i++ )
		{
			assertNotSame( expected.getSubunits().get( i ), observed.getSubunits().get( i ) );
			assertEquals( expected.getSubunits().get( i ), observed.getSubunits().get( i ) );
		}
		
		// check atom indices
		for( int i=0; i<expected.atoms().size(); i++ )
		{
			assertNotSame( expected.atoms().get( i ), observed.atoms().get( i ) );
			assertEquals( expected.atoms().get( i ), observed.atoms().get( i ) );
		}
		for( int i=0; i<expected.backboneAtoms().size(); i++ )
		{
			assertNotSame( expected.backboneAtoms().get( i ), observed.backboneAtoms().get( i ) );
			assertEquals( expected.backboneAtoms().get( i ), observed.backboneAtoms().get( i ) );
		}
	}
	
	protected <T extends AtomAddress<T>> void assertEquals( DistanceRestraint<T> expected, DistanceRestraint<T> observed )
	{
		try
		{
			assertNotNull( expected );
			assertNotNull( observed );
			assertNotSame( expected.getLefts(), observed.getLefts() );
			assertEquals( expected.getLefts().size(), observed.getLefts().size() );
			assertContentsNotSame( expected.getLefts(), observed.getLefts() );
			assertEquals( expected.getLefts(), observed.getLefts() );
			assertEquals( expected.getRights().size(), observed.getRights().size() );
			assertNotSame( expected.getRights(), observed.getRights() );
			assertContentsNotSame( expected.getRights(), observed.getRights() );
			assertEquals( expected.getRights(), observed.getRights() );
			assertEquals( expected.getMinDistance(), observed.getMinDistance() );
			assertEquals( expected.getMaxDistance(), observed.getMaxDistance() );
		}
		catch( AssertionFailedError err )
		{
			throw new AssertionFailedError( "expected:<" + expected + "> but was:<" + observed + ">" );
		}
	}
	
	protected <T> void assertContentsNotSame( Collection<T> a, Collection<T> b )
	{
		Iterator<T> iterA = a.iterator();
		Iterator<T> iterB = b.iterator();
		while( iterA.hasNext() && iterB.hasNext() )
		{
			assertNotSame( iterA.next(), iterB.next() );
		}
		assertTrue( iterA.hasNext() == iterB.hasNext() );
	}
	
	protected void assertEquals( AtomAddressReadable expected, AtomAddressReadable observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals(
			Character.toLowerCase( expected.getSubunitName() ),
			Character.toLowerCase( observed.getSubunitName() )
		);
		assertEquals( expected.getResidueNumber(), observed.getResidueNumber() );
		assertEquals(
			expected.getAtomName().toLowerCase(),
			observed.getAtomName().toLowerCase()
		);
	}
	
	protected void assertEquals( Bond expected, Bond observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getLeftName(), observed.getLeftName() );
		assertEquals( expected.getRightName(), observed.getRightName() );
		assertEquals( expected.getStrength(), observed.getStrength() );
		assertSame( expected.getLeftAddress() , observed.getLeftAddress() );
		assertSame( expected.getRightAddress(), observed.getRightAddress() );
	}
	
	protected void assertEquals( MultiVector expected, MultiVector observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getDimension(), observed.getDimension() );
		for( int i=0; i<expected.getDimension(); i++ )
		{
			assertEquals( expected.get( i ), observed.get( i ) );
		}
	}
	
	protected void assertEquals( MultiAxisAlignedBox expected, MultiAxisAlignedBox observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.min, observed.min );
		assertEquals( expected.max, observed.max );
	}
	
	protected void assertEquals( Cluster expected, Cluster observed )
	{
		assertNotNull( expected );
		assertNotNull( observed );
		assertEquals( expected.getCenter(), observed.getCenter() );
		//assertEquals( expected.getBoundingBox(), observed.getBoundingBox() );
	}
	
	protected void assertEquals( Quaternion expected, Quaternion observed )
	{
		// remember, q represents the same rotation as -q
		// so expected can equal observed or -observed
		// thus, we check their rotational result and not their components directly
		assertNotNull( expected );
		assertNotNull( observed );
		
		Vector3 a = new Vector3();
		Vector3 b = new Vector3();
		
		Vector3.getUnitX( a );
		expected.rotate( a );
		Vector3.getUnitX( b );
		observed.rotate( b );
		assertEquals( a, b );
		
		Vector3.getUnitY( a );
		expected.rotate( a );
		Vector3.getUnitY( b );
		observed.rotate( b );
		assertEquals( a, b );
		
		Vector3.getUnitZ( a );
		expected.rotate( a );
		Vector3.getUnitZ( b );
		observed.rotate( b );
		assertEquals( a, b );
	}
	
	protected void assertEquals( DistanceMatrix expected, DistanceMatrix observed )
	{
		assertEquals( expected.getNumPoints(), observed.getNumPoints() );
		assertEquals( expected.getNumDistances(), observed.getNumDistances() );
		
		IndexPairIterator iter = new IndexPairIterator( expected.getNumPoints() );
		while( iter.hasNext() )
		{
			IndexPair pair = iter.next();
			
			assertEquals( expected.get( pair ), observed.get( pair ) );
		}
	}
	
	private String getNextLine( BufferedReader reader )
	throws IOException
	{
		String line = "";
		
		while( line.equals( "" ) )
		{
			line = reader.readLine();
			
			if( line == null )
			{
				break;
			}
			
			// post process the lines
			line = line.trim();
		}
		
		return line;
	}
	
	protected void assertEqualsTextFile( InputStream expectedInputStream, String observedPath )
	{
		assertNotNull( expectedInputStream );
		assertNotNull( observedPath );
		
		try
		{
			// open files for reading
			BufferedReader expectedReader = new BufferedReader( new InputStreamReader( expectedInputStream ) );
			BufferedReader observedReader = new BufferedReader( new FileReader( observedPath ) );
			
			// check the files line by line
			while( true )
			{
				String expectedLine = getNextLine( expectedReader );
				String observedLine = getNextLine( observedReader );
				
				if( expectedLine == null && observedLine == null )
				{
					// all's ok
					break;
				}
				else if( expectedLine == null || expectedLine == null )
				{
					// error!
					fail( "Uneven number of non-blank lines!" );
				}
				
				// check the two lines
				assertEquals( expectedLine, observedLine );
			}
		}
		catch( IOException ex )
		{
			fail( ex.toString() );
		}
	}
	
	protected void assertGte( int expectedMin, int observed )
	{
		if( observed < expectedMin )
		{
			fail( "expected >= <" + expectedMin + "> but was:<" + observed + ">" );
		}
	}
	
	protected void assertGte( double expectedMin, double observed )
	{
		if( !CompareReal.gte( observed, expectedMin ) )
		{
			fail( "expected >= <" + expectedMin + "> but was:<" + observed + ">" );
		}
	}
	
	protected void assertLte( int expectedMax, int observed )
	{
		if( observed > expectedMax )
		{
			fail( "expected <= <" + expectedMax + "> but was:<" + observed + ">" );
		}
	}
	
	protected void assertLte( double expectedMax, double observed )
	{
		if( !CompareReal.lte( observed, expectedMax ) )
		{
			fail( "expected <= <" + expectedMax + "> but was:<" + observed + ">" );
		}
	}
	
	protected void assertInRange( int expectedMin, int expectedMax, int observed )
	{
		assertGte( expectedMin, observed );
		assertLte( expectedMax, observed );
	}
	
	protected void assertInRange( double expectedMin, double expectedMax, double observed )
	{
		assertGte( expectedMin, observed );
		assertLte( expectedMax, observed );
	}

	protected void assertProteinIndicesCorrect( Protein protein )
	{
		Iterator<AtomAddressInternal> iterAddress = null;
		
		// check the subunits atom index
		for( Subunit subunit : protein.getSubunits() )
		{
			iterAddress = subunit.atoms().iterator();
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					AtomAddressInternal address = new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
					assertTrue( iterAddress.hasNext() );
					assertEquals( address, iterAddress.next() );
				}
			}
		}		
		
		// check the subunits backbone atom index
		for( Subunit subunit : protein.getSubunits() )
		{
			iterAddress = subunit.backboneAtoms().iterator();
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					if( atom.isBackbone() )
					{
						AtomAddressInternal address = new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
						assertTrue( iterAddress.hasNext() );
						assertEquals( address, iterAddress.next() );
					}
				}
			}
		}		
		
		// check the protein atom index
		iterAddress = protein.atoms().iterator();
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					AtomAddressInternal address = new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
					assertTrue( iterAddress.hasNext() );
					assertEquals( address, iterAddress.next() );
				}
			}
		}

		// check the protein backbone atom index
		iterAddress = protein.backboneAtoms().iterator();
		for( Subunit subunit : protein.getSubunits() )
		{
			for( Residue residue : subunit.getResidues() )
			{
				for( Atom atom : residue.getAtoms() )
				{
					if( atom.isBackbone() )
					{
						AtomAddressInternal address = new AtomAddressInternal( subunit.getId(), residue.getId(), atom.getId() );
						assertTrue( iterAddress.hasNext() );
						assertEquals( address, iterAddress.next() );
					}
				}
			}
		}
	}

	protected Vector3 getRandomVector( double min, double max )
	{
		return new Vector3(
			getRandomDouble( min, max ),
			getRandomDouble( min, max ),
			getRandomDouble( min, max )
		);
	}
	
	protected Quaternion getRandomRotation( )
	{
		/* Jeff: 12/04/2008 - NOTE:
			There are much, Much more sophisticated ways to do this!
			But we don't need any of that here.
		*/
		Quaternion q = new Quaternion();
		Quaternion.getRotation(
			q,
			getRandomVector( -1.0, 1.0 ),
			getRandomDouble( -Math.PI, Math.PI )
		);
		q.normalize();
		
		return q;
	}
	
	protected double getRandomDouble( double low, double high )
	{
		return m_rand.nextDouble() * ( high - low ) + low;
	}
}
