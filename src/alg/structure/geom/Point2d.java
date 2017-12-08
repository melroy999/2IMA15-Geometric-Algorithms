package alg.structure.geom;

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
}
