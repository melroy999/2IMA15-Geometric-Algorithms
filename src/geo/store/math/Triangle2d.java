package geo.store.math;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Data structure representing a triangle.
 */
public class Triangle2d {
    // Each triangle consists of three corner points, represented as points.
    protected final Point2d p1, p2, p3;

    // The center point of the triangle.
    public final Point2d c;

    // The circumcenter of the triangle, and the radius of the circumcircle.
    public final Point2d cc;
    public final double ccr;

    // Create a 2d polygon to test contains.
    private final Path2D shape;

    /**
     * Create a triangle, given the three corner points.
     *
     * @param p1 The first corner point of the triangle.
     * @param p2 The second corner point of the triangle.
     * @param p3 The third corner point of the triangle.
     */
    public Triangle2d(Point2d p1, Point2d p2, Point2d p3) {
        // Make sure that the edges are in CCW order!
        double sign = (p2.x - p1.x) * (p2.y + p1.y) + (p3.x - p2.x) * (p3.y + p2.y) + (p1.x - p3.x) * (p1.y + p3.y);

        // If the sign is negative, we are in CW order and should thus swap two points.
        if(sign < 0) {
            this.p1 = p1;
            this.p2 = p3;
            this.p3 = p2;
        } else {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        // Find the center point.
        c = p1.add(p2).add(p3).scale(1/3d);

        // Find the circumcenter and radius of the circum circle.
        cc = getCircumCenter();
        ccr = cc.distance(p1);

        // Create the shape.
        shape = new Path2D.Double();
        shape.moveTo(p1.x, p1.y);
        shape.lineTo(p2.x, p2.y);
        shape.lineTo(p3.x, p3.y);
        shape.closePath();
    }

    /**
     * Get the circum circle of the triangle.
     *
     * @return The circumcenter, I.e. the center of the circle that goes through all the corner points of the triangle.
     */
    private Point2d getCircumCenter() {
        double a2 = p1.x * p1.x + p1.y * p1.y;
        double b2 = p2.x * p2.x + p2.y * p2.y;
        double c2 = p3.x * p3.x + p3.y * p3.y;

        double det = 1 / (2 * (p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)));
        double x = det * (a2 * (p2.y - p3.y) + b2 * (p3.y - p1.y) + c2 * (p1.y - p2.y));
        double y = det * (a2 * (p3.x - p2.x) + b2 * (p1.x - p3.x) + c2 * (p2.x - p1.x));

        return new Point2d(x, y);
    }

    /**
     * Check if the given point is contained in the circumcircle.
     * @param p The point we want to check the position of.
     * @return True if the distance between the point and the circumcenter is smaller than the circumcircle radius.
     */
    public boolean circumCircleContains(Point2d p) {
        return cc.distance(p) < ccr;
    }
}
