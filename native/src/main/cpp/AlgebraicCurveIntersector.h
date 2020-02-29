
#ifndef ALGEBRAIC_CURVE_INTERSECTOR_H_
#define ALGEBRAIC_CURVE_INTERSECTOR_H_


#include <CGAL/basic.h>
#include <CGAL/CORE_BigInt.h>
#include <CGAL/Arrangement_2.h>
#include <CGAL/Arr_algebraic_segment_traits_2.h>
#include <CGAL/Arrangement_2.h>
#include <CGAL/Cartesian.h>

typedef CORE::BigInt Integer;
typedef CORE::BigRat Rational;
typedef CGAL::Arr_algebraic_segment_traits_2<Integer> Traits;
typedef Traits::Polynomial_2 Poly_int_2;
typedef Traits::Curve_2 Curve_2;
typedef Traits::X_monotone_curve_2 X_monotone_curve_2;
typedef Traits::Point_2 Point_2;
typedef CGAL::Polynomial_traits_d<Poly_int_2>::Rebind<Rational,2>::Other::Polynomial_d Poly_rat_2;
typedef CGAL::Arrangement_2<Traits> Arrangement_2;

typedef CGAL::Cartesian<Rational>::Point_2 Point_rat_2;

class AlgebraicCurveIntersector
{
public:

	AlgebraicCurveIntersector( );
	Curve_2 constructCurve( Poly_rat_2 ratPoly );
	X_monotone_curve_2 constructXCurve( Point_2 source, Point_2 target );
	void add( Curve_2 curve );
	void add( X_monotone_curve_2 xcurve );
	Arrangement_2 getArrangement( );
	void getIntersectionPoints( std::vector<Point_rat_2> *pPoints );

private:

	Traits m_traits;
	Traits::Construct_curve_2 m_ccurve;
	Traits::Construct_point_2 m_cpoint;
	Traits::Construct_x_monotone_segment_2 m_cxsegment;
	Traits::Is_on_2 m_is_on;
	CGAL::Fraction_traits<Poly_rat_2>::Decompose m_decompose;
	std::vector<Curve_2> m_curves;
	std::vector<X_monotone_curve_2> m_xcurves;

};


#endif /* ALGEBRAIC_CURVE_INTERSECTOR_H_ */
