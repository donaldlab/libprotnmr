package edu.duke.cs.libprotnmr.cgal.curves;

import edu.duke.cs.libprotnmr.geom.GeodesicGrid;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;

import java.util.*;

public class IntersectionFaceBuilder
{
	/*********************************
	 *   Static Methods
	 *********************************/
	
	public static List<Face> getIntersectionFaces( KinematicBand kinematicBand, RdcBand rdcBand )
	{
		// compute the arrangement
		Arrangement arrangement = new Arrangement();
		arrangement.addArc( new CircularCurveArc( kinematicBand.getCurve( BandPart.Min ) ) );
		arrangement.addArc( new CircularCurveArc( kinematicBand.getCurve( BandPart.Max ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Min, 0 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Max, 0 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Min, 1 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Max, 1 ) ) );
		
		// DEBUG
		if( false )
		{
			showArrangementKinemage( kinematicBand, rdcBand, arrangement );
		}
		
		List<Face> faces = arrangement.computeFaces();
		
		// DEBUG
		if( false )
		{
			showArrangementKinemage( kinematicBand, rdcBand, faces );
		}
		
		faces = getMostSatisfyingFaces( faces, Arrays.asList( kinematicBand ), Arrays.asList( rdcBand ) );
		
		// DEBUG
		if( false )
		{
			showArrangementKinemage( kinematicBand, rdcBand, faces );
		}
		
		return faces;
	}
	
	public static List<Face> getIntersectionFaces( RdcBand rdcBand )
	{
		return getIntersectionFaces( rdcBand, 0.0 );
	}
	
	public static List<Face> getIntersectionFaces( RdcBand rdcBand, double uncertainty )
	{
		// compute the arrangement
		Arrangement arrangement = new Arrangement();
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Min, 0 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Max, 0 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Min, 1 ) ) );
		arrangement.addArc( new RdcCurveArc( rdcBand.getCurve( BandPart.Max, 1 ) ) );
		
		List<Face> faces = arrangement.computeFaces();
		
		// DEBUG
		if( false )
		{
			showArrangementKinemage( null, rdcBand, faces );
		}
		
		faces = getMostSatisfyingFaces( faces, new ArrayList<Band>(), Arrays.asList( rdcBand ) );
		
		// DEBUG
		if( false )
		{
			showArrangementKinemage( null, rdcBand, faces );
		}
		
		if( uncertainty > 0.0 )
		{
			faces = getUncertainFaces( faces, uncertainty, rdcBand );
			
			// DEBUG
			if( false )
			{
				showArrangementKinemage( null, rdcBand, faces );
			}
		}
		
		return faces;
	}
	

	/*********************************
	 *   Static Functions
	 *********************************/
	
	private static List<Face> getMostSatisfyingFaces( List<Face> faces, List<? extends Band> requiredBands, List<? extends Band> desiredBands )
	{
		class Entry
		{
			public Face face;
			public boolean[] enclosingBands;
			private int m_numRequiredBands;
			
			public Entry( Face face, int numRequiredBands, int numDesiredBands )
			{
				this.face = face;
				this.enclosingBands = new boolean[numRequiredBands + numDesiredBands];
				m_numRequiredBands = numRequiredBands;
			}

			public Entry( Face face, Entry other )
			{
				this.face = face;
				this.enclosingBands = Arrays.copyOf( other.enclosingBands, other.enclosingBands.length );
				this.m_numRequiredBands = other.m_numRequiredBands;
			}
			
			public int getRequiredDepth( )
			{
				int depth = 0;
				for( int i=0; i<m_numRequiredBands; i++ )
				{
					depth += enclosingBands[i] ? 1: 0;
				}
				return depth;
			}

			public int getDesiredDepth( )
			{
				int depth = 0;
				for( int i=m_numRequiredBands; i<enclosingBands.length; i++ )
				{
					depth += enclosingBands[i] ? 1: 0;
				}
				return depth;
			}
			
			public void flipBand( int bandId )
			{
				enclosingBands[bandId] = !enclosingBands[bandId];
			}
		}
		
		// build the master list of bands
		// NOTE: the required bands must all be before the desired bands!!
		List<Band> allBands = new ArrayList<Band>();
		allBands.addAll( requiredBands );
		allBands.addAll( desiredBands );
		
		// pick an arbitrary starting point that does not lie on an edge
		Vector3 startingPoint = getNonBoundaryPoint( allBands );
		
		// find the face that contains the starting point
		Face startingFace = null;
		for( Face face : faces )
		{
			if( face.containsPoint( startingPoint ) )
			{
				startingFace = face;
				break;
			}
		}
		assert( startingFace != null );
		
		// which bands enclose this face?
		Entry startingEntry = new Entry( startingFace, requiredBands.size(), desiredBands.size() );
		for( int i=0; i<allBands.size(); i++ )
		{
			startingEntry.enclosingBands[i] = allBands.get( i ).containsPoint( startingPoint );
		}
		
		// initialize the band lookup map
		Map<Curve,Integer> bandIndex = new HashMap<Curve,Integer>();
		
		List<Face> deepestFaces = new ArrayList<Face>();
		int maxDepth = 1;
		
		// do BFS in the face graph
		HashSet<Face> visitedFaces = new HashSet<Face>();
		Deque<Entry> queue = new ArrayDeque<Entry>();
		queue.add( startingEntry );
		while( !queue.isEmpty() )
		{
			Entry entry = queue.pollFirst();
			visitedFaces.add( entry.face );
			
			// are the required bands satisfied?
			if( entry.getRequiredDepth() == requiredBands.size() )
			{
				// is this a deepest face?
				int depth = entry.getDesiredDepth();
				if( depth > maxDepth )
				{
					maxDepth = depth;
					deepestFaces.clear();
				}
				if( depth == maxDepth )
				{
					deepestFaces.add( entry.face );
				}
			}
			
			// for each neighboring face...
			for( Halfedge halfedge : entry.face.boundary() )
			{
				Face neighbor = halfedge.getTwin().getFace();
				assert( neighbor != null );
				
				// already visited? skip it
				if( visitedFaces.contains( neighbor ) )
				{
					continue;
				}
				
				// already in the queue? skip it
				boolean found = false;
				for( Entry otherEntry : queue )
				{
					if( otherEntry.face == neighbor )
					{
						found = true;
						break;
					}
				}
				if( found )
				{
					continue;
				}
				
				// what's the band associated with this edge?
				Curve curve = halfedge.getEdge().getArc().getCurve();
				Integer bandId = bandIndex.get( curve );
				if( bandId == null )
				{
					for( int i=0; i<allBands.size(); i++ )
					{
						if( allBands.get( i ).hasCurveOnBoundary( curve ) )
						{
							bandId = i;
							break;
						}
					}
					assert( bandId != null );
					bandIndex.put( curve, bandId );
				}
				
				// get the neighbor entry
				Entry neighborEntry = new Entry( neighbor, entry );
				neighborEntry.flipBand( bandId );
				queue.addLast( neighborEntry );
			}
		}
		
		// DEBUG: show the bfs results
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendAxes( kin, 1, 0.2 );
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			for( Band band : allBands )
			{
				if( band instanceof KinematicBand )
				{
					KinemageBuilder.appendKinematicBand( kin, (KinematicBand)band, "Kinematic Band", KinemageColor.Orange, 1 );
				}
				else if( band instanceof RdcBand )
				{
					KinemageBuilder.appendRdcBand( kin, (RdcBand)band, "Rdc Band", KinemageColor.Cobalt, 1 );
				}
			}
			KinemageBuilder.appendArrangementFaces( kin, deepestFaces, "Deepest Faces", KinemageColor.Lime );
			KinemageBuilder.appendPoint( kin, startingPoint, "Starting Point", KinemageColor.Green, 7 );
			KinemageBuilder.appendBoundary( kin, startingFace.boundary(), "Starting Face", KinemageColor.Green, 1 );
			new KinemageWriter().show( kin );
		}
		
		return deepestFaces;
	}
	
	private static Vector3 getNonBoundaryPoint( List<Band> bands )
	{
		GeodesicGrid grid = new GeodesicGrid( 2 );
		for( GeodesicGrid.Face gridFace : grid )
		{
			Vector3 point = gridFace.getMidpoint();
			point.normalize();
			
			boolean onBoundary = false;
			for( int i=0; i<bands.size() && !onBoundary; i++ )
			{
				onBoundary |= bands.get( i ).boundaryContainsPoint( point );
			}
			if( !onBoundary )
			{
				return point;
			}
		}
		
		throw new Error( "Unable to find non-boundary point. Maybe try multi-resolution grid?" );
	}

	@Deprecated
	private static List<Face> getMostSatisfyingFacesPerFace( List<Face> faces, KinematicBand kinematicBand, List<RdcBand> rdcBands )
	{
		List<Face> deepestFaces = new ArrayList<Face>();
		int currentNumSatisfied = 0;
		for( Face face : faces )
		{
			// get an arbitrary point in the interior of the face
			Vector3 interiorPoint = face.getArbitraryInteriorPoint();
			
			// is this face outside the kinematic band?
			if( kinematicBand != null && !kinematicBand.containsPoint( interiorPoint ) )
			{
				continue;
			}
			
			// how many RDC bands does it satisfy?
			int numSatisfied = 0;
			for( RdcBand rdcBand : rdcBands )
			{
				if( rdcBand.containsPoint( interiorPoint ) )
				{
					numSatisfied++;
				}
			}
			
			// do we have deeper faces now?
			if( numSatisfied > currentNumSatisfied )
			{
				currentNumSatisfied = numSatisfied;
				deepestFaces = new ArrayList<Face>();
			}
			
			if( numSatisfied > 0 && numSatisfied == currentNumSatisfied )
			{
				deepestFaces.add( face );
			}
		}
		return deepestFaces;
	}
	
	private static List<Face> getUncertainFaces( List<Face> faces, double uncertainty, RdcBand rdcBand )
	{
		List<Face> uncertainFaces = new ArrayList<Face>();
		for( Face face : faces )
		{
			uncertainFaces.add( getUncertainFace( face, uncertainty, rdcBand ) );
		}
		return uncertainFaces;
	}
	
	private static Face getUncertainFace( Face face, double uncertainty, RdcBand rdcBand )
	{
		Face uncertainFace = new Face( getUncertainBoundary( face.outerBoundary(), uncertainty, rdcBand ) );
		for( List<Halfedge> hole : face.holes() )
		{
			uncertainFace.addHole( getUncertainBoundary( hole, uncertainty, rdcBand ) );
		}
		
		// set the face pointers
		for( Halfedge halfedge : uncertainFace.boundary() )
		{
			halfedge.setFace( uncertainFace );
		}
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, face.boundary(), "Original Face", KinemageColor.Cobalt, 1 );
			KinemageBuilder.appendBoundary( kin, uncertainFace.boundary(), "Uncertain Face", KinemageColor.LightGrey, 1 );
			new KinemageWriter().showAndWait( kin );
		}
		
		return uncertainFace;
	}
	
	private static List<Halfedge> getUncertainBoundary( List<Halfedge> boundary, double uncertainty, RdcBand rdcBand )
	{
		// compute offset arcs for the halfedges first
		List<RdcOffsetCurveArc> offsetArcs = new ArrayList<RdcOffsetCurveArc>( boundary.size() );
		for( Halfedge halfedge : boundary )
		{
			// determine the offset direction (NOTE: we know these will always be RDC arcs)
			// also, "outside" is always towards the outside of the RDC band
			// HACKHACK: we'll have to find a better way to determine this later when we have multiple RDC bands
			RdcCurveArc rdcArc = (RdcCurveArc)halfedge.getEdge().getArc();
			double arcD = rdcArc.getCurve().getD();
			int arcnum = rdcArc.getCurve().getArcnum();
			double centerD = rdcBand.getRdcValue();
			
			double offsetDirection = arcD < centerD ? 1.0 : -1.0;
			offsetDirection *= arcnum * 2 - 1;
			
			// we know all the edges are RDC curves
			RdcCurveArc arc = (RdcCurveArc)halfedge.getEdge().getArc();
			offsetArcs.add( new RdcOffsetCurveArc( arc, offsetDirection*uncertainty ) );
		}
		
		// compute the offset vertices
		List<Vertex> offsetVertices = new ArrayList<Vertex>( offsetArcs.size() * 2 );
		for( RdcOffsetCurveArc offsetArc : offsetArcs )
		{
			offsetVertices.add( new Vertex( offsetArc.getSource() ) );
			offsetVertices.add( new Vertex( offsetArc.getTarget() ) );
		}
		
		// compose the offset curves and vertices into halfedges
		List<Halfedge> offsetBoundary = new ArrayList<Halfedge>();
		for( int i=0; i<boundary.size(); i++ )
		{
			Vertex startVertex = offsetVertices.get( i*2 );
			Vertex endVertex = offsetVertices.get( i*2 + 1 );
			Vertex nextVertex = offsetVertices.get( ( i*2 + 2 ) % offsetVertices.size() );
			
			Halfedge halfedge = boundary.get( i );
			RdcOffsetCurveArc offsetArc = offsetArcs.get( i );
			
			// determine the direction of the offset halfedge
			RdcCurve rdcCurve = (RdcCurve)halfedge.getEdge().getArc().getCurve();
			Vector3 offsetSource = offsetArc.getCurve().getPoint( rdcCurve.getAngle( halfedge.getSource().getPoint() ) );
			boolean sourcesMatch = offsetSource.approximatelyEquals( startVertex.getPoint() );
			Halfedge.Direction direction = sourcesMatch ? Halfedge.Direction.Forward : Halfedge.Direction.Reverse;
			
			// do we need to compute the offset vertex arc?
			if( !endVertex.getPoint().approximatelyEquals( nextVertex.getPoint() ) )
			{
				// add the offset arc
				Edge offsetArcEdge = new Edge( offsetArc, startVertex, endVertex );
				startVertex.addEdge( offsetArcEdge );
				endVertex.addEdge( offsetArcEdge );
				offsetBoundary.add( new Halfedge( offsetArcEdge, direction ) );
				
				RdcCurveArc arc = (RdcCurveArc)halfedge.getEdge().getArc();
				CircularCurveArc offsetVertexArc = new CircularCurveArc(
					new CircularCurve( arc.getTarget(), uncertainty ),
					endVertex.getPoint(), nextVertex.getPoint()
				);
				
				// add the offset vertex arc
				Edge offsetVertexEdge = new Edge( offsetVertexArc, endVertex, nextVertex );
				endVertex.addEdge( offsetVertexEdge );
				nextVertex.addEdge( offsetVertexEdge );
				offsetBoundary.add( new Halfedge( offsetVertexEdge, Halfedge.Direction.Forward ) );
			}
			else
			{
				// add just the offset arc (make sure to use the right vertices)
				Edge offsetArcEdge = new Edge( offsetArc, startVertex, nextVertex );
				startVertex.addEdge( offsetArcEdge );
				nextVertex.addEdge( offsetArcEdge );
				offsetBoundary.add( new Halfedge( offsetArcEdge, direction ) );
			}
		}
		
		// check the vertex edges
		for( Halfedge halfedge : offsetBoundary )
		{
			assert( halfedge.getSource().getDegree() == 2 );
		}
		
		// DEBUG
		if( false )
		{
			Kinemage kin = new Kinemage();
			KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
			KinemageBuilder.appendBoundary( kin, boundary, "Original Boundary", KinemageColor.Cobalt, 1 );
			KinemageBuilder.appendBoundary( kin, offsetBoundary, "Uncertain Boundary", KinemageColor.LightGrey, 1 );
			new KinemageWriter().showAndWait( kin );
		}
		
		return offsetBoundary;
	}

	private static void showArrangementKinemage( KinematicBand kinematicBand, RdcBand rdcBand, Arrangement arrangement )
	{
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Min, 0 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Max, 0 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Min, 1 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Max, 1 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, kinematicBand.getCurve( BandPart.Min ), "Kinematic Curve", KinemageColor.Orange, 1 );
		KinemageBuilder.appendCurve( kin, kinematicBand.getCurve( BandPart.Max ), "Kinematic Curve", KinemageColor.Orange, 1 );
		KinemageBuilder.appendDetailedArrangement( kin, arrangement, "Arrangement", KinemageColor.Lime, 1 );
		new KinemageWriter().show( kin );
	}
	
	private static void showArrangementKinemage( KinematicBand kinematicBand, RdcBand rdcBand, List<Face> faces )
	{
		Kinemage kin = new Kinemage();
		KinemageBuilder.appendAxes( kin, 1, 0.2 );
		KinemageBuilder.appendUnitSphere( kin, KinemageColor.DarkGrey );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Min, 0 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Max, 0 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Min, 1 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		KinemageBuilder.appendCurve( kin, rdcBand.getCurve( BandPart.Max, 1 ), "Rdc Curve", KinemageColor.Cobalt, 1 );
		if( kinematicBand != null )
		{
			KinemageBuilder.appendCurve( kin, kinematicBand.getCurve( BandPart.Min ), "Kinematic Curve", KinemageColor.Orange, 1 );
			KinemageBuilder.appendCurve( kin, kinematicBand.getCurve( BandPart.Max ), "Kinematic Curve", KinemageColor.Orange, 1 );
		}
		KinemageBuilder.appendArrangementFaces( kin, faces, "Arrangement", KinemageColor.Lime );
		new KinemageWriter().show( kin );
	}
}
