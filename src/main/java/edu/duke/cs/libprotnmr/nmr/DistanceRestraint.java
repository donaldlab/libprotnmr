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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.io.Transformer;
import edu.duke.cs.libprotnmr.math.CompareReal;
import edu.duke.cs.libprotnmr.protein.AtomAddress;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.HasAddresses;
import edu.duke.cs.libprotnmr.protein.HasAtoms;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.protein.Sequence;
import edu.duke.cs.libprotnmr.protein.Sequences;


public class DistanceRestraint<T extends AtomAddress<T>> implements Serializable, AssignmentSource<T>, Iterable<Assignment<T>>, HasAddresses<T>
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final long serialVersionUID = -3533389135607795851L;
	
	
	/**************************
	 *   Data Members
	 **************************/
	
	private Set<T> m_lefts;
	private Set<T> m_rights;
	private double m_minDistance;
	private double m_maxDistance;
	
	
	/**************************
	 *   Constructors
	 **************************/
	
	public DistanceRestraint( )
	{
		m_lefts = new TreeSet<T>();
		m_rights = new TreeSet<T>();
		m_minDistance = 0.0f;
		m_maxDistance = 0.0f;
	}
	
	public DistanceRestraint( DistanceRestraint<T> other )
	{
		// deep copy the address lists
		m_lefts = new TreeSet<T>();
		for( T address : other.m_lefts )
		{
			m_lefts.add( address.newCopy() );
		}
		m_rights = new TreeSet<T>();
		for( T address : other.m_rights )
		{
			m_rights.add( address.newCopy() );
		}
		m_minDistance = other.m_minDistance;
		m_maxDistance = other.m_maxDistance;
	}
	
	
	/**************************
	 *   Accessors
	 **************************/
	
	@Override
	public Set<T> getLefts( )
	{
		return m_lefts;
	}
	public void setLefts( Set<T> value )
	{
		m_lefts = value;
	}
	public void setLefts( T ... values )
	{
		m_lefts = Transformer.toTreeSet( values );
	}
	
	@Override
	public Set<T> getRights( )
	{
		return m_rights;
	}
	public void setRights( Set<T> value )
	{
		m_rights = value;
	}
	public void setRights( T ... values )
	{
		m_rights = Transformer.toTreeSet( values );
	}
	
	@Override
	public Iterable<T> addresses( )
	{
		List<T> addresses = new ArrayList<T>();
		addresses.addAll( m_lefts );
		addresses.addAll( m_rights );
		return addresses;
	}
	
	public double getMinDistance( )
	{
		return m_minDistance;
	}
	public void setMinDistance( double value )
	{
		m_minDistance = value;
	}

	public double getMaxDistance( )
	{
		return m_maxDistance;
	}
	public void setMaxDistance( double value )
	{
		m_maxDistance = value;
	}
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static List<Character> getSubunitNamesFromReadable( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		// collect all the subunit names and sort them
		TreeSet<Character> subunitNames = new TreeSet<Character>();
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			for( AtomAddressReadable address : restraint.getLefts() )
			{
				subunitNames.add( address.getSubunitName() );
			}
			for( AtomAddressReadable address : restraint.getRights() )
			{
				subunitNames.add( address.getSubunitName() );
			}
		}
		return new ArrayList<Character>( subunitNames );
	}
	
	public static List<Character> getSubunitNamesFromInternal( Protein protein, List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		// collect all the subunit names and sort them
		TreeSet<Character> subunitNames = new TreeSet<Character>();
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			for( AtomAddressInternal address : restraint.getLefts() )
			{
				subunitNames.add( protein.getSubunit( address.getSubunitId() ).getName() );
			}
			for( AtomAddressInternal address : restraint.getRights() )
			{
				subunitNames.add( protein.getSubunit( address.getSubunitId() ).getName() );
			}
		}
		return new ArrayList<Character>( subunitNames );
	}
	
	public static void shiftResidueNumbersIfNeeded( ArrayList<DistanceRestraint<AtomAddressReadable>> restraints, Sequences sequences )
	{
		// get the range of residue numbers from the protein
		int minProteinNumber = Integer.MAX_VALUE;
		int maxProteinNumber = Integer.MIN_VALUE;
		for( Map.Entry<Character,Sequence> sequence : sequences )
		{
			for( Sequence.Entry entry : sequence.getValue() )
			{
				minProteinNumber = Math.min( minProteinNumber, entry.residueNumber );
				maxProteinNumber = Math.max( maxProteinNumber, entry.residueNumber );
			}
		}
		
		// get the range of residue numbers from the restraints
		int minRestraintNumber = Integer.MAX_VALUE;
		int maxRestraintNumber = Integer.MIN_VALUE;
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			for( AtomAddressReadable address : restraint.getLefts() )
			{
				minRestraintNumber = Math.min( minRestraintNumber, address.getResidueNumber() );
				maxRestraintNumber = Math.max( maxRestraintNumber, address.getResidueNumber() );
			}
			for( AtomAddressReadable address : restraint.getRights() )
			{
				minRestraintNumber = Math.min( minRestraintNumber, address.getResidueNumber() );
				maxRestraintNumber = Math.max( maxRestraintNumber, address.getResidueNumber() );
			}
		}
		
		// does the residue number range of the restraints match the protein?
		if( minRestraintNumber >= minProteinNumber && maxRestraintNumber <= maxProteinNumber )
		{
			return;
		}
		
		// shift the residue numbers of the restraints
		int shift = minProteinNumber - 1;
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			for( AtomAddressReadable address : restraint.getLefts() )
			{
				address.setResidueNumber( address.getResidueNumber() + shift );
			}
			for( AtomAddressReadable address : restraint.getRights() )
			{
				address.setResidueNumber( address.getResidueNumber() + shift );
			}
		}
	}
	
	public static <T extends AtomAddress<T>> void applyPadding( List<DistanceRestraint<T>> restraints, double paddingPercent )
	{
		for( DistanceRestraint<T> restraint : restraints )
		{
			restraint.setMinDistance( restraint.getMinDistance() * ( 1.0 - paddingPercent ) );
			restraint.setMaxDistance( restraint.getMaxDistance() * ( 1.0 + paddingPercent ) );
		}
	}
	
	public static List<DistanceRestraint<AtomAddressInternal>> cloneInternal( List<DistanceRestraint<AtomAddressInternal>> restraints )
	{
		ArrayList<DistanceRestraint<AtomAddressInternal>> newRestraints = new ArrayList<DistanceRestraint<AtomAddressInternal>>( restraints.size() );
		for( DistanceRestraint<AtomAddressInternal> restraint : restraints )
		{
			newRestraints.add( new DistanceRestraint<AtomAddressInternal>( restraint ) );
		}
		return newRestraints;
	}
	
	public static List<DistanceRestraint<AtomAddressReadable>> cloneReadable( List<DistanceRestraint<AtomAddressReadable>> restraints )
	{
		ArrayList<DistanceRestraint<AtomAddressReadable>> newRestraints = new ArrayList<DistanceRestraint<AtomAddressReadable>>( restraints.size() );
		for( DistanceRestraint<AtomAddressReadable> restraint : restraints )
		{
			newRestraints.add( new DistanceRestraint<AtomAddressReadable>( restraint ) );
		}
		return newRestraints;
	}
	
	public static double getMinViolation( DistanceRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		double minViolation = Double.POSITIVE_INFINITY;
		for( Assignment<AtomAddressInternal> assignment : restraint )
		{
			minViolation = Math.min( minViolation, restraint.getViolation( assignment, structure ) );
		}
		return minViolation;
	}
	
	public static List<Assignment<AtomAddressInternal>> getMinViolationAssignments( DistanceRestraint<AtomAddressInternal> restraint, HasAtoms structure )
	{
		double minViolation = Double.POSITIVE_INFINITY;
		List<Assignment<AtomAddressInternal>> minAssignments = new ArrayList<Assignment<AtomAddressInternal>>();
		for( Assignment<AtomAddressInternal> assignment : restraint )
		{
			double violation = restraint.getViolation( assignment, structure );
			if( violation < minViolation )
			{
				minAssignments.clear();
			}
			if( violation <= minViolation )
			{
				minViolation = violation;
				minAssignments.add( assignment );
			}
		}
		return minAssignments;
	}
	
	public static boolean isSatisfied( DistanceRestraint<AtomAddressInternal> restraint, Protein protein )
	{
		return CompareReal.lte( getMinViolation( restraint, protein ), 0 );
	}
	
	
	/**************************
	 *   Methods
	 **************************/
	
	public boolean isAmbiguous( )
	{
		return isAtomAmbiguous() || isSubunitAmbiguous();
	}
	
	public boolean isSubunitAmbiguous( )
	{
		return isSubunitAmbiguous( m_lefts ) || isSubunitAmbiguous( m_rights );
	}
	
	public boolean isAtomAmbiguous( )
	{
		return isAtomAmbiguous( m_lefts ) || isAtomAmbiguous( m_rights );
	}
	
	public int getNumAssignments( )
	{
		return m_lefts.size() * m_rights.size();
	}
	
	@Override
	public Iterator<Assignment<T>> iterator()
	{
		return new AssignmentIterator<T>( this );
	}
	
	public void swap( )
	{
		Set<T> swap = m_lefts;
		m_lefts = m_rights;
		m_rights = swap;
	}
	
	@Override
	public String toString( )
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append( "[DistanceRestraint] " );
		buf.append( m_minDistance );
		buf.append( "," );
		buf.append( m_maxDistance );
		buf.append( "\t(" );
		renderAddresses( buf, m_lefts );
		buf.append( ")\t(" );
		renderAddresses( buf, m_rights );
		buf.append( ")" );
		
		return buf.toString();
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
		{
            return false;
		}
		if( other == this )
		{
			return true;
		}
		if( !( other instanceof DistanceRestraint<?> ) )
		{
			return false;
		}
		
		// UNDONE: there has to be a way to check this cast
		// NOTE: nope, there isn't. Java's runtime doesn't know about generic types
		return equals( (DistanceRestraint<T>)other );
	}
	
	public boolean equals( DistanceRestraint<T> other )
	{
		return
			m_minDistance == other.m_minDistance
			&& m_maxDistance == other.m_maxDistance
			&&
			(
				(
					m_lefts.equals( other.m_lefts )
					&& m_rights.equals( other.m_rights )
				)
				||
				(
					m_lefts.equals( other.m_rights )
					&& m_rights.equals( other.m_lefts )
				)
			);
	}
	
	@Override
	public int hashCode( )
	{
		return HashCalculator.combineHashes(
			Double.valueOf( m_minDistance ).hashCode(),
			Double.valueOf( m_maxDistance ).hashCode(),
			HashCalculator.combineHashesCommutative( m_lefts.hashCode(), m_rights.hashCode() )
		);
	}
	
	public boolean isSatisfied( double dist )
	{
		return CompareReal.gte( dist, m_minDistance ) && CompareReal.lte( dist, m_maxDistance );
	}
	
	public double getViolation( double dist )
	{
		if( dist < m_minDistance )
		{
			return m_minDistance - dist;
		}
		else if( dist > m_maxDistance )
		{
			return dist - m_maxDistance;
		}
		else
		{
			return 0.0;
		}
	}
	
	public double getViolation( Assignment<AtomAddressInternal> assignment, HasAtoms structure )
	{
		Vector3 left = structure.getAtom( assignment.getLeft() ).getPosition();
		Vector3 right = structure.getAtom( assignment.getRight() ).getPosition();
		return getViolation( left.getDistance( right ) );
	}
	
	
	/**************************
	 *   Functions
	 **************************/
	
	private void renderAddresses( StringBuffer buf, Set<T> addresses )
	{
		boolean renderComma = false;
		for( T address : addresses )
		{
			if( renderComma )
			{
				buf.append( "," );
			}
			buf.append( address );
			renderComma = true;
		}
	}
	
	private boolean isSubunitAmbiguous( Set<T> addresses )
	{
		T firstAddress = addresses.iterator().next();
		for( T address : addresses )
		{
			if( !firstAddress.isSameSubunit( address ) )
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isAtomAmbiguous( Set<T> addresses )
	{
		T firstAddress = addresses.iterator().next();
		for( T address : addresses )
		{
			if( !firstAddress.isSameAtom( address ) )
			{
				return true;
			}
		}
		return false;
	}
}
