package geo.structure.math;

import geo.structure.geo.Face;
import geo.structure.geo.Vertex;

/**
 * Data structure representing a triangle.
 */
public class Triangle2d {
    // Each triangle consists of three corner points, represented as points.
    protected final Point2d p1, p2, p3;

    // The center point of the triangle.
    public final Point2d c;

    // The circumcenter of the triangle, and the radius of the circumcircle.
    public final Vertex<Face> cc;
    public final double ccr;

    /**
     * Create a triangle, given the three corner points.
     *
     * @param p1 The first corner point of the triangle.
     * @param p2 The second corner point of the triangle.
     * @param p3 The third corner point of the triangle.
     */
    public Triangle2d(Point2d p1, Point2d p2, Point2d p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        // Find the center point.
        c = p1.add(p2).add(p3).scale(1/3d);

        // Find the circumcenter and radius of the circum circle.
        cc = getCircumCenter();
        ccr = cc.distance(p1);
    }

    /**
     * Get the circum circle of the triangle.
     *
     * @return The circumcenter, I.e. the center of the circle that goes through all the corner points of the triangle.
     */
    private Vertex<Face> getCircumCenter() {
        // We first need the midpoint of p1->p2 and p2->p3.
        Point2d mid_p1_p2 = p1.interpolate(p2, 0.5d);
        Point2d mid_p2_p3 = p2.interpolate(p3, 0.5d);
        System.out.println(mid_p1_p2 + ", " + mid_p2_p3);

        // Now find the negative reciprocal of the slope, such that we get the slope of the perpendicular bisector.
        double slope_p1_p2 = -1 / ((p2.y - p1.y + 10e-32) / (p2.x - p1.x + 10e-32));
        double slope_p2_p3 = -1 / ((p3.y - p2.y + 10e-32) / (p3.x - p2.x + 10e-32));

        // Now, solve y = mx + b for b, b = y - mx where m is the slope and x and y are taken from the center point.
        double b_p1_p2 = mid_p1_p2.y - slope_p1_p2 * mid_p1_p2.x;
        double b_p2_p3 = mid_p2_p3.y - slope_p2_p3 * mid_p2_p3.x;

        // Now, find the x and y-coordinate of the intersection point.
        double x = (b_p1_p2 - b_p2_p3) / (slope_p2_p3 - slope_p1_p2);
        double y = (slope_p1_p2 * x) + b_p1_p2;

        // Return the center.
        return new Vertex<>(x, y, null);
    }

    /**
     * Check where the given point is relatively to the triangle.
     *
     * @param p The point we want to query the location of.
     * @return INSIDE if inside the triangle, BORDER if on the edge of the triangle, OUTSIDE otherwise.
     */
    public Location contains(Point2d p) {
        // For this, we will use barycentric coordinates.
        // The point p can be redefined in terms of p1, p2 and p3 together with scalars, such that:
        //      p = a * p1 + b * p2 + c * p3
        double div = 1d / ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y) + 10e-32);
        double a = div * ((p2.y - p3.y) * (p.x - p3.x) + (p3.x - p2.x) * (p.y - p3.y));
        double b = div * ((p3.y - p1.y) * (p.x - p3.x) + (p1.x - p3.x) * (p.y - p3.y));
        double c = 1.0d - a - b;

        // Check the ranges for a, b, c to be in [0,1].
        if(a >= 0d && a <= 1d && b >= 0d && b <= 1d && c >= 0d && c <= 1d) {
            // The point is in the triangle if and only if a, b and c are in the range (0,1).
            if(a > 0d && a < 1d && b > 0d && b < 1d && c > 0d && c < 1d) return Location.INSIDE;

            // Otherwise, we are on the border if we are in the range [0,1] for a, b and c.
            return Location.BORDER;
        } else {
            // If none of the above, it has to be outside.
            return Location.OUTSIDE;
        }
    }

    /**
     * Check if the given point is contained in the circumcircle.
     * @param p The point we want to check the position of.
     * @return True if the distance between the point and the circumcenter is smaller than the circumcircle radius.
     */
    public boolean circumCircleContains(Point2d p) {
        return cc.distance(p) < ccr;
    }

    /**
     * An enumeration that represents the position of a point relative to the triangle.
     */
    public enum Location {
        INSIDE, BORDER, OUTSIDE
    }
}
