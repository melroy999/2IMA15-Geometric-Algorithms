package alg.structure.geom;

import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import java.awt.geom.Ellipse2D;

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

    public Ellipse2D.Double getCircumCircle() {
        // We first need the midpoint of p1->p2 and p2->p3.
        Point2d mid_p1_p2 = p1.midpoint(p2);
        Point2d mid_p2_p3 = p2.midpoint(p3);

        // Now find the negative reciprocal of the slope, such that we get the slope of the perpendicular bisector.
        double slope_p1_p2 = -1 / ((p2.y - p1.y) / (p2.x - p1.x));
        double slope_p2_p3 = -1 / ((p3.y - p2.y) / (p3.x - p2.x));

        // Now, solve y = mx + b for b, b = y - mx where m is the slope and x and y are taken from the center point.
        double b_p1_p2 = mid_p1_p2.y - slope_p1_p2 * mid_p1_p2.x;
        double b_p2_p3 = mid_p2_p3.y - slope_p2_p3 * mid_p2_p3.x;

        // Now, find the x and y-coordinate of the intersection point.
        double x = (b_p1_p2 - b_p2_p3) / (slope_p2_p3 - slope_p1_p2);
        double y = (slope_p1_p2 * x) + b_p1_p2;

        // Use the point to find the distance to one of the points, say p1.
        double radius = p3.distance(new Point2d(x, y));

        // Create a filling ellipse.
        return new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
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
