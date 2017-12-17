package geo.store.math;

/**
 * A simple tuple 2d coordinate object.
 */
public abstract class Tuple2d<T> {
    // The x and y-coordinates of the tuple.
    public final double x, y;

    /**
     * Define a tuple by giving an x and y-coordinates.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the tuple.
     */
    public Tuple2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Define a tuple with coordinates 0, 0.
     */
    public Tuple2d() {
        this(0d, 0d);
    }

    /**
     * Add the given vector to this vector and return it as a new vector.
     *
     * @param t The vector we want to take the sum of.
     * @return The sum between this vector and vector t, as a new instance.
     */
    public T add(Tuple2d t) {
        return get(x + t.x, y + t.y);
    }

    /**
     * Subtract the given vector from this vector and return it as a new vector.
     *
     * @param t The vector we want to subtract.
     * @return The subtraction between this vector and vector t, as a new instance.
     */
    public T sub(Tuple2d t) {
        return get(x - t.x, y - t.y);
    }

    /**
     * Scale this tuple by the given amount.
     *
     * @param s The scaling factor.
     * @return The tuple scaled, as a new instance.
     */
    public T scale(double s) {
        return get(s * x, s * y);
    }

    /**
     * Interpolate between this vector and the given vector, using the formula (1-a)*this + a*t.
     *
     * @param t The tuple we want to interpolate with.
     * @param a The interpolation ratio, if 0 we return this vector.
     * @return The interpolation of this vector and vector t with interpolation scaling factor a.
     */
    public T interpolate(Tuple2d t, double a) {
        return get((1 - a) * x + a * t.x, (1 - a) * y + a * t.y);
    }

    /**
     * Check if the distance between this tuple and the given tuple is less than the epsilon value.
     *
     * @param t The vector we want to check equality for.
     * @param e The distance in which we consider tuples to be equal.
     * @return True if the distance between this and t is less than e, false otherwise.
     */
    public boolean epsilonEquals(Tuple2d t, double e) {
        return distance(t) < e;
    }

    /**
     * Calculate the euclidean distance between this tuple and the given tuple.
     *
     * @param t The tuple we want to measure the euclidean distance to from this tuple.
     * @return The euclidean distance between this tuple and the tuple t.
     */
    public double distance(Tuple2d t) {
        return Math.sqrt(Math.pow(this.x - t.x, 2) + Math.pow(this.y - t.y, 2));
    }

    /**
     * Create a new instance of a tuple2d subclass.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the point.
     * @return An instance of the desired type, that is an extension of Tuple2d.
     */
    protected abstract T get(double x, double y);

    /**
     * Check whether the tuples are equal to one another.
     *
     * @param o The object we want to check the equality of.
     * @return Whether the tuples have the same x and y coordinates.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2d<?> tuple2d = (Tuple2d<?>) o;

        if (Double.compare(tuple2d.x, x) != 0) return false;
        return Double.compare(tuple2d.y, y) == 0;
    }

    /**
     * Get an unique hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
