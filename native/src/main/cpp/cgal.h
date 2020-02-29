

#ifndef CGAL_H_
#define CGAL_H_

#include <CGAL/Exact_spherical_kernel_3.h>


typedef CGAL::Exact_spherical_kernel_3 Spherical_k;

typedef CGAL::Point_3<Spherical_k> Point_3;
typedef CGAL::Circle_3<Spherical_k> Circle_3;
typedef CGAL::Circular_arc_3<Spherical_k> Circular_arc_3;


#endif /* CGAL_H_ */
