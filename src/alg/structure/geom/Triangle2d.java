package alg.structure.geom;

import alg.structure.halfedge.Face;

/**
 * A graph structure representing a triangle, with some triangle based operations.
 */
public class Triangle2d {
    // Each triangle has 3 points.
    private final Point2d p1, p2, p3;

    /**
     * Define a triangle using three of its border points.
     *
     * @param p1 The first position.
     * @param p2 The second position.
     * @param p3 The third position.
     */
    public Triangle2d(Point2d p1, Point2d p2, Point2d p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Point2d getCenter() {
        return p1.add(p2).add(p3).mult(1/3f);
    }

    /**
     * Check where the given point is relatively to the triangle.
     *
     * @param p The point we want to query the location of.
     * @return INSIDE if inside the triangle, BORDER if on the edge of the triangle, OUTSIDE otherwise.
     */
    public Location contains(Point2d p) {
        // Use barycentric coordinates for this.
        // I.e, define p as p = a * p1 + b * p2 + c * p3.
        double a = ((p2.y - p3.y) * (p.x - p3.x) + (p3.x - p2.x) * (p.y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        double b = ((p3.y - p1.y) * (p.x - p3.x) + (p1.x - p3.x) * (p.y - p3.y)) /
                ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        double c = 1.0d - a - b;

        if(a > 0d && a < 1d && b > 0d && b < 1d && c > 0d && c < 1d) {
            // The point is in the triangle if and only if a, b and c are in the range (0,1).
            return Location.INSIDE;
        } else if(a >= 0d && a <= 1d && b >= 0d && b <= 1d && c >= 0d && c <= 1d) {
            // Otherwise, we are on the border if we are in the range [0,1] for a, b and c.
            return Location.BORDER;
        } else {
            // If none of the above, it has to be outside.
            return Location.OUTSIDE;
        }
    }

    /**
     * An enumeration that represents the position of a point relative to the triangle.
     */
    public enum Location {
        INSIDE, BORDER, OUTSIDE;
    }

    @Override
    public String toString() {
        return "Triangle2d{" +
                p1 +
                ", " + p2 +
                ", " + p3 +
                '}';
    }
}
