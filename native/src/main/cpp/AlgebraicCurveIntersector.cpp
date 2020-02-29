
#include "AlgebraicCurveIntersector.h"


AlgebraicCurveIntersector::AlgebraicCurveIntersector( )
: m_ccurve( m_traits.construct_curve_2_object() ),
  m_cpoint( m_traits.construct_point_2_object() ),
  m_cxsegment( m_traits.construct_x_monotone_segment_2_object() ),
  m_is_on( m_traits.is_on_2_object() )
{
	// nothing else to do
}

Curve_2 AlgebraicCurveIntersector::constructCurve( Poly_rat_2 ratPoly )
{
	Poly_int_2 intPoly;
	Integer denominator;

	// convert the polynomial from rational coefficients to integer coefficients
	m_decompose( ratPoly, intPoly, denominator );

	// convert to a curve
	return m_ccurve( intPoly );
}

X_monotone_curve_2 AlgebraicCurveIntersector::constructXCurve( Point_2 source, Point_2 target )
{
	std::vector<X_monotone_curve_2> xcurves;
	m_cxsegment( source, target, std::back_inserter( xcurves ) );
	return *xcurves.begin();
}

void AlgebraicCurveIntersector::add( Curve_2 curve )
{
	m_curves.push_back( curve );
}

void AlgebraicCurveIntersector::add( X_monotone_curve_2 xcurve )
{
	m_xcurves.push_back( xcurve );
}

Arrangement_2 AlgebraicCurveIntersector::getArrangement( )
{
	Arrangement_2 arrangement;
	CGAL::insert( arrangement, m_curves.begin(), m_curves.end() );
	CGAL::insert( arrangement, m_xcurves.begin(), m_xcurves.end() );
	return arrangement;
}

void AlgebraicCurveIntersector::getIntersectionPoints( std::vector<Point_rat_2> *pPoints )
{
	Arrangement_2 arrangement = getArrangement();
	for( Arrangement_2::Vertex_iterator iter = arrangement.vertices_begin(); iter != arrangement.vertices_end(); iter++ )
	{
		Arrangement_2::Vertex vertex = *iter;

		// is the vertex on at least two source curves?
		int numCurves = 0;
		for( std::vector<Curve_2>::iterator iter = m_curves.begin(); iter != m_curves.end() && numCurves < 2; iter++ )
		{
			if( m_is_on( vertex.point(), *iter ) )
			{
				numCurves++;
			}
		}
		for( std::vector<X_monotone_curve_2>::iterator iter = m_xcurves.begin(); iter != m_xcurves.end() && numCurves < 2; iter++ )
		{
			if( m_is_on( vertex.point(), *iter ) )
			{
				numCurves++;
			}
		}
		if( numCurves >= 2 )
		{
			// convert to Rational type
			std::pair<double,double> point = vertex.point().to_double();
			pPoints->push_back( Point_rat_2( Rational( point.first ), Rational( point.second ) ) );
		}
	}
}
