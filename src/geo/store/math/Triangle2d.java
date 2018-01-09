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
        // Calculate the slopes of the two lines.
        double s1 = (p2.y - p1.y) / (p2.x - p1.x + 10e-32);
        double s2 = (p3.y - p2.y) / (p3.x - p2.x + 10e-32);

        // Calculate x.
        double x = 0.5d * (s1 * s2 * (p1.y - p3.y) + s2 * (p1.x + p2.x) - s1 * (p2.x + p3.x)) / (s2 - s1);
        double y = -1/s1 * (x - 0.5d * (p1.x + p2.x)) + 0.5d * (p1.y + p2.y);

        // If not a number, try something differently.
        if(!Double.isFinite(y)) {
            y = -1/s2 * (x - 0.5d * (p2.x + p3.x)) + 0.5d * (p2.y + p3.y);
        }

        if(!Double.isFinite(y)) {
            System.out.println("Bad circum circle found.");
            System.out.println("Circum center of " + p1 + " " + p2 + " " + p3 + " is at " + (new Point2d(x, y)));
            System.out.println("s1=" + s1 + ", s2=" + s2 + ", s3=" + (p1.y - p3.y) / (p1.x - p3.x + 10e-32));
        }

        // Return the center.
        return new Point2d(x, y);
    }

    /**
     * Check where the given point is relatively to the triangle.
     *
     * @param p The point we want to query the location of.
     * @return INSIDE if inside the triangle, BORDER if on the edge of the triangle, OUTSIDE otherwise.
     */
    public Location contains(Point2d p) {
        // We have trouble with equal y searches, so hardcode it.
        if(p.y == p1.y && p.y == p2.y) {
            if(isOnVerticalLine(p, p1, p2)) return Location.BORDER;
        }
        if(p.y == p2.y && p.y == p3.y) {
            if(isOnVerticalLine(p, p2, p3)) return Location.BORDER;
        }
        if(p.y == p3.y && p.y == p1.y) {
            if(isOnVerticalLine(p, p3, p1)) return Location.BORDER;
        }

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

    public boolean isOnVerticalLine(Point2d p, Point2d p1, Point2d p2) {
        if(Math.min(p1.x, p2.x) == p1.x) {
            if(Math.min(p1.x, p.x) == p1.x && Math.max(p2.x, p.x) == p2.x) {
                return true;
            }
        } else {
            if(Math.min(p2.x, p.x) == p2.x && Math.max(p1.x, p.x) == p1.x) {
                return true;
            }
        }
        return false;
    }

    /**
     * In the worst case... use awt to check the contains, as it isn't error prone.
     *
     * @param p The point to check the existence of.
     * @return Whether the point is contained in the shape.
     */
    public Location containsAlternative(Point2d p) {
        return shape.contains(p.x, p.y) ? Location.INSIDE : Location.OUTSIDE;
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
