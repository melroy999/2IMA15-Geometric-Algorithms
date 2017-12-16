package geo.store.math;

/**
 * A class representing a 2d vector.
 */
public class Vector2d extends Tuple2d<Vector2d> {
    /**
     * Define a vector by giving an x and y-coordinates.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     */
    public Vector2d(double x, double y) {
        super(x, y);
    }

    /**
     * Create a new instance of a tuple2d subclass.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the point.
     * @return An instance of the desired type, that is an extension of Tuple2d.
     */
    @Override
    protected Vector2d get(double x, double y) {
        return new Vector2d(x, y);
    }

    /**
     * Calculate the dot product between the two vectors.
     *
     * @param v The other vector.
     * @return The dot product between this vector and the vector v.
     */
    public double dot(Vector2d v) {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Get the length of the vector.
     *
     * @return The length of the vector.
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Give a normalized representation of this vector.
     *
     * @return This vector, normalized.
     */
    public Vector2d normalize() {
        double l = length();
        return new Vector2d(x / l, y / l);
    }
}
