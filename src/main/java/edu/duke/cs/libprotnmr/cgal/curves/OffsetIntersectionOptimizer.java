package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.io.HashCalculator;
import edu.duke.cs.libprotnmr.optimization.OptimizerFailureException;
import edu.duke.cs.libprotnmr.optimization.SimpleCircleOptimizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class OffsetIntersectionOptimizer
{
	/*********************************
	 *   Definitions
	 *********************************/
	
	private static class OptimaCacheEntry
	{
		public RdcOffsetCurve curve;
		public Vector3 point;
		
		public OptimaCacheEntry( RdcOffsetCurve curve, Vector3 point )
		{
			this.curve = curve;
			this.point = point;
		}
		
		@Override
		public int hashCode( )
		{
			return HashCalculator.combineHashes( curve.hashCode(), point.hashCode() );
		}
		
		@Override
		public boolean equals( Object other )
		{
			if( other instanceof OptimaCacheEntry )
			{
				return equals( (OptimaCacheEntry)other );
			}
			return false;
		}
		
		public boolean equals( OptimaCacheEntry other )
		{
			return curve.equals( other.curve ) && point.equals( other.point );
		}
	}
	
	private static class RootsCacheEntry
	{
		public RdcOffsetCurve curve;
		public CircularCurve circle;
		
		public RootsCacheEntry( RdcOffsetCurve curve, CircularCurve circle )
		{
			this.curve = curve;
			this.circle = circle;
		}
		
		@Override
		public int hashCode( )
		{
			return HashCalculator.combineHashes( curve.hashCode(), circle.hashCode() );
		}
		
		@Override
		public boolean equals( Object other )
		{
			if( other instanceof RootsCacheEntry )
			{
				return equals( (RootsCacheEntry)other );
			}
			return false;
		}
		
		public boolean equals( RootsCacheEntry other )
		{
			return curve.equals( other.curve ) && circle.equals( other.circle );
		}
	}
	
	
	/*********************************
	 *   Data Members
	 *********************************/
	
	private static Map<OptimaCacheEntry,List<Double>> m_optimaCache;
	private static Map<RootsCacheEntry,List<Double>> m_rootsCache;
	
	
	/*********************************
	 *   Constructors
	 *********************************/
	
	static
	{
		// NOTE: weak hash maps make poor caches since they get cleared every GC cycle,
		// but I'm too lazy to write something better
		// also, patches share a LOT of circles (since they're on a grid), so caching gives us a modest speedup here
		m_optimaCache = new WeakHashMap<OptimaCacheEntry,List<Double>>();
		m_rootsCache = new WeakHashMap<RootsCacheEntry,List<Double>>();
	}
	
	
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static List<Vector3> getIntersectionPoints( RdcOffsetCurve curve, GeodesicCurve circle )
	{
		return getIntersectionPoints( curve, new CircularCurve( circle ) );
	}
	
	public static List<Vector3> getIntersectionPoints( RdcOffsetCurve curve, CircularCurve circle )
	{
		// find the intersections by bounding all the roots of the distance function by finding all the optima
		OffsetIntersectionFunction f = new OffsetIntersectionFunction( curve, circle );
		
		// first, check the cache for the optima
		OptimaCacheEntry optimaEntry = new OptimaCacheEntry( curve, circle.getNormal() );
		List<Double> optima = m_optimaCache.get( optimaEntry );
		if( optima == null )
		{
			// cache miss, compute the optima
			try
			{
				optima = SimpleCircleOptimizer.getOptima( f );
				
				// update the cache
				m_optimaCache.put( optimaEntry, optima );
			}
			catch( OptimizerFailureException ex )
			{
				// DEBUG: save the function for later inspection
				if( true )
				{
					f.save( new File( "output/function.dat" ) );
				}
				throw new Error( "Unable to find intersection points. Numerical methods failed. =(", ex );
			}
		}
		
		// check the cache for the roots
		RootsCacheEntry rootsEntry = new RootsCacheEntry( curve, circle );
		List<Double> roots = m_rootsCache.get( rootsEntry );
		if( roots == null )
		{
			// cache miss, compute the roots
			roots = SimpleCircleOptimizer.getRoots( f, optima );
			
			// update the cache
			m_rootsCache.put( rootsEntry, roots );
		}
		
		// build the intersection points
		List<Vector3> intersectionPoints = new ArrayList<Vector3>();
		for( Double root : roots )
		{
			intersectionPoints.add( curve.getPoint( root ) );
		}
		return intersectionPoints;
	}
}