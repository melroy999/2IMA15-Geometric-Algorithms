package alg.structure.geom;

import alg.Point;
import alg.structure.halfedge.Vertex;

/**
 * A simple two dimensional point.
 */
public class Point2d {
    // The x and y coordinates of the point.
    public final double x, y;

    /**
     * Construct a simple two dimensional point.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a simple two dimensional point.
     *
     * @param p The integer point to base this double point on.
     */
    public Point2d(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * The euclidean distance between this point and another.
     *
     * @param v The point we want to know the distance to.
     * @return The distance between this and v.
     */
    public double distance(Point2d v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }

    @Override
    public String toString() {
        return "Point2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Point2d add(Point2d p) {
        return new Point2d(x + p.x, y + p.y);
    }

    public Point2d subtract(Point2d p) {
        return new Point2d(x - p.x, y - p.y);
    }

    public Point2d mult(double d) {
        return new Point2d(d * x, d * y);
    }

}
