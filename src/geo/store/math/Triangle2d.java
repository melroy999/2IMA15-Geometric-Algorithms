package geo.store.math;

import geo.delaunay.TriangulationMesh;
import geo.store.halfedge.TriangleFace;
import geo.store.halfedge.Vertex;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A class representing a 2d triangle.
 */
public class Triangle2d {
    // The three corner points of the triangle.
    public final Point2d p1, p2, p3;

    // Other characteristics we can already gather for a triangle.
    protected final Point2d circumCenter;
    protected final double circumRadius;

    // Flags that track whether we have symbolic points.
    public final boolean topSymbolicPoint;
    public final boolean bottomSymbolicPoint;

    // Find the highest point in the list, using the predicate on page 210.
    public static Comparator<Point2d> heightComparator = (o1, o2) -> (o1.y > o2.y || (o1.y == o2.y && o2.x > o2.y)) ? 1 : -1;

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

        List<Point2d> points = Arrays.asList(p1, p2, p3);
        topSymbolicPoint = points.stream().anyMatch(p -> p instanceof Vertex.SymbolicTopVertex);
        bottomSymbolicPoint = points.stream().anyMatch(p -> p instanceof Vertex.SymbolicBottomVertex);

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
        // We have to keep in mind that we might have symbolic points. Check if we do.
        List<Point2d> points = Arrays.asList(p1, p2, p3);

        if(topSymbolicPoint || bottomSymbolicPoint) {
            // Collinearity will never happen here, period.

            // This will be a lot of fun. Lets start with the case where both are present.
            if(topSymbolicPoint && bottomSymbolicPoint) {
                // To be in CCW order, bottom must always be followed by top.
                for(int i = 0; i < points.size(); i++) {
                    if(points.get(i) instanceof Vertex.SymbolicBottomVertex) {
                        if(!(points.get((i + 1) % points.size()) instanceof Vertex.SymbolicTopVertex)) {
                            throw new ClockwiseException(this);
                        }
                    }
                }
            } else {
                for(int i = 0; i < points.size(); i++) {
                    // Get the next two points.
                    Point2d p1 = points.get((i + 1) % points.size());
                    Point2d p2 = points.get((i + 2) % points.size());

                    // Use the comparator in triangulation mesh.
                    if(points.get(i) instanceof Vertex.SymbolicTopVertex) {
                        if(heightComparator.compare(p1, p2) < 0) throw new ClockwiseException(this);
                    }

                    if(points.get(i) instanceof Vertex.SymbolicBottomVertex) {
                        if (heightComparator.compare(p2, p1) < 0) throw new ClockwiseException(this);
                    }
                }
            }
        } else {
            // Otherwise, just do what we usually do...
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
        // Depending on whether we have symbolic points, we have to do something different here.
        if(topSymbolicPoint && bottomSymbolicPoint) {
            // It is inside if it is below the non symbolic point in height order.
            // However, a point that does not qualify this condition does not exist by definition, so it is always inside.
            return true;
        } else if(topSymbolicPoint || bottomSymbolicPoint) {
            List<Point2d> points = Arrays.asList(p1, p2, p3);

            for (int i = 0; i < points.size(); i++) {
                // Get the next two points.
                Point2d p1 = points.get((i + 1) % points.size());
                Point2d p2 = points.get((i + 2) % points.size());

                // In any case, the points should be left of the line in question.
                if (points.get(i) instanceof Vertex.SymbolicTopVertex || points.get(i) instanceof Vertex.SymbolicBottomVertex) {
                    return checkRelativeSide(p1, p2, p) <= 0;
                }
            }

            // We should always have a match in the loop, thus this return value should not be required.
            return false;
        } else {
                // Just do the normal thing.
                return circumCenter.distance(p) < circumRadius;
            }
        }

    private double checkRelativeSide(Point2d p1, Point2d p2, Point2d p) {
        return (p2.y - p1.y) * (p.x - p1.x) + (-p2.x + p1.x) * (p.y - p1.y);
    }

    /**
     * Check if a value is equal to 0 considering floating point errors.
     *
     * @param v1 The value we want to check zero equality of.
     * @return Whether v1 is close enough to 0 to be considered 0.
     */
    public boolean equalsZero(double v1) {
        return equals(v1, 0d);
    }

    /**
     * Check whether the two values are equal.
     *
     * @param v1 The first value.
     * @param v2 The second value.
     * @return Whether x and y are close enough to each other, according to an epsilon comparison.
     */
    public boolean equals(double v1, double v2) {
        // Here we use the smallest difference between double values to determine if the doubles are close enough.
        long expectedBits = Double.doubleToLongBits(v1) < 0 ? 0x8000000000000000L - Double.doubleToLongBits(v1) : Double.doubleToLongBits(v1);
        long actualBits = Double.doubleToLongBits(v2) < 0 ? 0x8000000000000000L - Double.doubleToLongBits(v2) : Double.doubleToLongBits(v2);
        long difference = expectedBits > actualBits ? expectedBits - actualBits : actualBits - expectedBits;

        // So they are  equal when the difference is at most 5 ULPs.
        return !Double.isNaN(v1) && !Double.isNaN(v2) && difference <= 5;
    }

    @Override
    public String toString() {
        return "{" +
                "" + p1 +
                ", " + p2 +
                ", " + p3 +
                "}";
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

    /**
     * An exception that will be thrown when the points are given in clockwise order.
     */
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
