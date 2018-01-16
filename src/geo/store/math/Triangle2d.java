package geo.store.math;

/**
 * A class representing a 2d triangle.
 */
public class Triangle2d {
    // The three corner points of the triangle.
    protected final Point2d p1, p2, p3;

    // Other characteristics we can already gather for a triangle.
    protected final Point2d circumCenter;
    protected final double circumRadius;

    /**
     * Create a triangle using the three points as the corner points.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @param p3 The third point.
     */
    public Triangle2d(Point2d p1, Point2d p2, Point2d p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        // Check for illegal cases.
        checkIllegality();

        // Calculate the circum center.
        circumCenter = calculateCircumCenter();
        circumRadius = p1.distance(circumCenter);
    }

    /**
     * We deep certain triangles illegal. We check those cases here.
     */
    private void checkIllegality() {
        // If all points are collinear, we have an illegal case.
        int orientation = orientation();
        if(orientation == 0) {
            throw new CollinearPointsException(this);
        }

        // Check if the points are in counter clockwise order.
        if(orientation == -1) {
            throw new ClockwiseException(this);
        }
    }

    /**
     * Find the orientation the points are given in, collinear, clockwise or counter clockwise.
     *
     * @return 1 for counter clockwise, 0 for collinear and -1 for clockwise.
     */
    private int orientation() {
        // First check if the points are collinear.
        double sign = (p2.y - p1.y) * (p3.x - p2.x) - (p2.x - p1.x) * (p3.y - p2.y);

        // If the sign is 0, we have a collinear case.
        if(equalsZero(sign)) return 0;

        // Otherwise, if val < 0, we have the points in counter clockwise order.
        return (sign < 0) ? 1 : -1;
    }

    /**
     * Find the circum center of the triangle.
     *
     * @return The center point of a circle that travels through all three corner points of the triangle.
     */
    public Point2d calculateCircumCenter() {
        double a2 = p1.x * p1.x + p1.y * p1.y;
        double b2 = p2.x * p2.x + p2.y * p2.y;
        double c2 = p3.x * p3.x + p3.y * p3.y;

        double det = 1 / (2 * (p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)));
        double x = det * (a2 * (p2.y - p3.y) + b2 * (p3.y - p1.y) + c2 * (p1.y - p2.y));
        double y = det * (a2 * (p3.x - p2.x) + b2 * (p1.x - p3.x) + c2 * (p2.x - p1.x));

        return new Point2d(x, y);
    }

    /**
     * Get the circum center of the triangle.
     *
     * @return The circum center of the triangle, which is the center point
     * of a circle traveling through all points of the triangle.
     */
    public Point2d getCircumCenter() {
        return circumCenter;
    }

    /**
     * Check if a point is within the circum circle.
     *
     * @param p The point to check the location of.
     * @return True if the radius is larger than the distance between the cc and p.
     */
    public boolean isInCircumCircle(Point2d p) {
        return circumCenter.distance(p) < circumRadius;
    }

    /**
     * Check if a value is equal to 0 considering floating point errors.
     *
     * @param v1 The value we want to check zero equality of.
     * @return Whether v1 is close enough to 0 to be considered 0.
     */
    private boolean equalsZero(double v1) {
        return equals(v1, 0d);
    }

    /**
     * Check whether the two values are equal.
     *
     * @param v1 The first value.
     * @param v2 The second value.
     * @return Whether x and y are close enough to each other, according to an epsilon comparison.
     */
    private boolean equals(double v1, double v2) {
        // Here we use the smallest difference between double values to determine if the doubles are close enough.
        long expectedBits = Double.doubleToLongBits(v1) < 0 ? 0x8000000000000000L - Double.doubleToLongBits(v1) : Double.doubleToLongBits(v1);
        long actualBits = Double.doubleToLongBits(v2) < 0 ? 0x8000000000000000L - Double.doubleToLongBits(v2) : Double.doubleToLongBits(v2);
        long difference = expectedBits > actualBits ? expectedBits - actualBits : actualBits - expectedBits;

        // So they are  equal when the difference is at most 5 ULPs.
        return !Double.isNaN(v1) && !Double.isNaN(v2) && difference <= 5;
    }

    /**
     * An exception that will be thrown when the area of a triangle is zero,
     * implying that the points are collinear.
     */
    public static class CollinearPointsException extends RuntimeException {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public CollinearPointsException(Triangle2d t) {
            super("The triangle " + t + " has zero area.");
        }
    }

    public static class ClockwiseException extends RuntimeException {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public ClockwiseException(Triangle2d t) {
            super("The triangle " + t + " is not defined in counter clockwise order.");
        }
    }
}
