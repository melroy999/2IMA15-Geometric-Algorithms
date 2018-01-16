package geo.store.halfedge;

import geo.store.math.Point2d;
import geo.store.math.Triangle2d;

import java.util.*;

/**
 * A face in a half edge structure, which has to be a triangle.
 */
public class TriangleFace extends Face<TriangleFace> {
    // A triangle object to do triangle specific calculations with.
    private final Triangle2d t;

    // Values that influence the containment test.
    private final double epsilon = 10e-6;

    /**
     * Create a face, given the edges surrounding it in counter clock wise order.
     *
     * @param edges       The edges of the face, in CCW order.
     */
    public TriangleFace(List<Edge<TriangleFace>> edges) {
        super(edges);

        // Check for illegalities.
        if (edges.size() != 3) throw new TrianglePointCountException();

        // Create the triangle.
        t = new Triangle2d(edges.get(0).origin, edges.get(1).origin, edges.get(2).origin);
    }

    /**
     * Check whether the face contains the given point.
     *
     * @param p The point to check the containment of.
     * @return Returns INSIDE if inside, BORDER if the point is on the border, OUTSIDE if outside the face.
     *         Next to the location, the face and appropriate edge is returned when applicable.
     */
    public ContainsResult contains(Point2d p) {
        if(doesBoundingBoxContain(p)) {
            // First check if one of the edges contains the point by measuring the distances.
            Edge<TriangleFace> edge;
            if((edge = isPointOnEdge(p)) != null) return new ContainsResult(Location.BORDER, this, edge);

            // If not the above, do a side check to check containment.
            if (checkRelativeSide(t.p1, t.p2, p) <= 0 && checkRelativeSide(t.p2, t.p3, p) <= 0
                    && checkRelativeSide(t.p3, t.p1, p) <= 0) return new ContainsResult(Location.INSIDE, this, null);
        }

        // We can be certain that it is not inside or on the border, so return outside.
        return new ContainsResult(Location.OUTSIDE, null, null);
    }

    private double checkRelativeSide(Point2d p1, Point2d p2, Point2d p) {
        return (p2.y - p1.y) * (p.x - p1.x) + (-p2.x + p1.x) * (p.y - p1.y);
    }

    /**
     * Check if the given point is within the expected bounds.
     *
     * @param p The point to check the location of.
     * @return True if the point is close to the triangle, false otherwise.
     */
    public boolean doesBoundingBoxContain(Point2d p) {
        double xMin = Math.min(Math.min(t.p1.x, t.p2.x), t.p3.x) - epsilon;
        double xMax = Math.max(Math.max(t.p1.x, t.p2.x), t.p3.x) + epsilon;
        double yMin = Math.min(Math.min(t.p1.y, t.p2.y), t.p3.y) - epsilon;
        double yMax = Math.max(Math.max(t.p1.y, t.p2.y), t.p3.y) + epsilon;

        return !(p.x < xMin || p.x > xMax || p.y < yMin || p.y > yMax);
    }

    /**
     * Check if the point is on one of the edges.
     *
     * @param p The point we want to check the location of.
     * @return Whether the point is close enough to the edge to be considered on the edge.
     */
    public Edge<TriangleFace> isPointOnEdge(Point2d p) {
        // Find an edge which the point can be on, preferably the one with smallest distance.
        Map<Edge<TriangleFace>, Double> distances = new HashMap<>();

        // Gather the distance data, take absolute values.
        for (Edge<TriangleFace> e : edges()) {
            distances.put(e, Math.abs(e.getDistancePointToSegment(p)));
        }

        // Find the minimum.
        Optional<Map.Entry<Edge<TriangleFace>, Double>> min = distances.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue));

        // Check if it is close.
        return min.isPresent() && equalsZero(min.get().getValue()) ? min.get().getKey() : null;
    }

    /**
     * Check if a value is equal to 0 considering floating point errors.
     *
     * @param v1 The value we want to check zero equality of.
     * @return Whether v1 is close enough to 0 to be considered 0.
     */
    public boolean equalsZero(double v1) {
        return v1 < epsilon * epsilon;
    }

    /**
     * A wrapper class that helps with containment checks.
     */
    public class ContainsResult {
        // The location we found the point relative to this triangle.
        public final Location location;

        // The face we found this point in, if applicable.
        public final Face<TriangleFace> face;

        // The edge the point is on, if applicable.
        public final Edge<TriangleFace> edge;

        /**
         * Create a result.
         *
         * @param location The location relative to the face.
         * @param face The face the result is in, if applicable.
         * @param edge The edge the point is on, if applicable.
         */
        public ContainsResult(Location location, Face<TriangleFace> face, Edge<TriangleFace> edge) {
            this.location = location;
            this.face = face;
            this.edge = edge;
        }
    }

    /**
     * An enumeration that represents the position of a point relative to the triangle.
     */
    public enum Location {
        INSIDE, BORDER, OUTSIDE
    }

    /**
     * An exception that will be thrown when the points are given in clockwise order.
     */
    public class TrianglePointCountException extends RuntimeException {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public TrianglePointCountException() {
            super("Encountered a triangle face construction ( " + TriangleFace.this + " ) with more or less than 3 points.");
        }
    }
}
