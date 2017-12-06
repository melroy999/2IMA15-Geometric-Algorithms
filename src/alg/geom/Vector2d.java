package alg.geom;

import java.awt.geom.Point2D;

/**
 * A class that can do some vector calculations.
 */
public class Vector2d extends Point2D.Double {
    /**
     * Constructs and initializes a <code>Point2D</code> with the
     * specified coordinates.
     *
     * @param x the X coordinate of the newly
     *          constructed <code>Point2D</code>
     * @param y the Y coordinate of the newly
     *          constructed <code>Point2D</code>
     * @since 1.2
     */
    public Vector2d(double x, double y) {
        super(x, y);
    }

    /**
     * Calculate the dot product of the two vectors.
     *
     * @param v The vector we want the dot product with.
     * @return The dot product of the two vectors.
     */
    public double dotProduct(Vector2d v) {
        return x * v.x + y * v.y;
    }

    /**
     * Calculate the cross product of the two vectors.
     *
     * @param v The vector we want to take the cross product with.
     * @return The cross product of the two vectors.
     */
    public double det(Vector2d v) {
        return x * v.y - y * v.x;
    }
}
