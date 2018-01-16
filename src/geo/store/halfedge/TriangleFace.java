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
        // First check if we have symbolic vertices.
        if(t.bottomSymbolicPoint && t.topSymbolicPoint) {
            // If both are symbolic, by definition the face should contain it.
            // Find the non symbolic point.
            List<Point2d> points = Arrays.asList(t.p1, t.p2, t.p3);
            Point2d target = points.stream().filter(p1 -> !(p1 instanceof Vertex.SymbolicTopVertex || p1 instanceof Vertex.SymbolicBottomVertex)).findAny().get();

            // Now, if the point has the same y-coordinate, it is on an edge.
            // This is a tricky situation, as replacing the edge would be inappropriate here.
            // TODO handle this.
            if(equalsZero(target.y - p.y)) {
                // Find the appropriate edge.
                Edge<TriangleFace> edge;

                if(p.x < target.x) {
                    // It is the edge going to the bottom point, which should originate in our target.
                    edge = this.edges().stream().filter(e -> e.origin == target).findAny().get();
                } else {
                    // Edge originating from the top point.
                    edge = this.edges().stream().filter(e -> e.twin.origin == target).findAny().get();
                }

                return new ContainsResult(Location.BORDER_FLAGGED, this, edge);
            }

            // Otherwise it is inside though...
            return new ContainsResult(Location.INSIDE, this, null);

        } else if(t.bottomSymbolicPoint || t.topSymbolicPoint) {
            List<Point2d> points = Arrays.asList(t.p1, t.p2, t.p3);

            for (int i = 0; i < points.size(); i++) {
                // Get the next two points.
                Point2d p1 = points.get((i + 1) % points.size());
                Point2d p2 = points.get((i + 2) % points.size());

                if(points.get(i) instanceof Vertex.SymbolicTopVertex || points.get(i) instanceof Vertex.SymbolicBottomVertex) {
                    // Keep in mind however that it can be on the edges to the symbolic point as well.
                    Edge<TriangleFace> edge = this.edges().stream().filter(e -> e.origin == p1).findAny().get();
                    if (equalsZero(edge.getDistancePointToSegment(p))) {
                        // It is on the edge, so return the edge.
                        return new ContainsResult(Location.BORDER, this, edge);
                    }

                    if (points.get(i) instanceof Vertex.SymbolicTopVertex) {
                        // We need the point to be left of p1 and p2.
                        Point2d top = points.get(i);

                        // Now, check if it shares the y-coordinate with p1 or p2.
                        if (equalsZero(p1.y - p.y)) {
                            // If it does, and it is right of the point, it is on the edge originating from point i.
                            if (p1.x < p.x) {
                                edge = this.edges().stream().filter(e -> e.origin == top).findAny().get();
                                return new ContainsResult(Location.BORDER_FLAGGED, this, edge);
                            }
                        }

                        if (equalsZero(p2.y - p.y)) {
                            // If it does, and it is right of the point, it is on the edge pointing to point i.
                            if (p2.x < p.x) {
                                edge = this.edges().stream().filter(e -> e.origin == p2).findAny().get();
                                return new ContainsResult(Location.BORDER_FLAGGED, this, edge);
                            }
                        }

                        // Now, we can check if it is inside. It is inside if it is left of p1 -> p2, outside otherwise.
                        // It must be between p1 and p2 though in height!
                        if (checkRelativeSide(p1, p2, p) <= 0 && p1.y > p.y && p2.y < p.y) {
                            return new ContainsResult(Location.INSIDE, this, null);
                        }
                    } else {
                        // We need the point to be left of p1 and p2.
                        Point2d bottom = points.get(i);

                        // Now, check if it shares the y-coordinate with p1 or p2.
                        if (equalsZero(p1.y - p.y)) {
                            // If it does, and it is left of the point, it is on the edge originating from the symbolic point, to p2.
                            if (p1.x > p.x) {
                                edge = this.edges().stream().filter(e -> e.origin == bottom).findAny().get();
                                return new ContainsResult(Location.BORDER_FLAGGED, this, edge);
                            }
                        }

                        if (equalsZero(p2.y - p.y)) {
                            // If it does, and it is left of the point, it is on the edge originating from p1.
                            if (p2.x > p.x) {
                                edge = this.edges().stream().filter(e -> e.origin == p2).findAny().get();
                                return new ContainsResult(Location.BORDER_FLAGGED, this, edge);
                            }
                        }

                        // Now, we can check if it is inside. It is inside if it is left of p1 -> p2, outside otherwise.
                        // It must be between p1 and p2 though in height!
                        if (checkRelativeSide(p1, p2, p) <= 0 && p2.y > p.y && p1.y < p.y) {
                            return new ContainsResult(Location.INSIDE, this, null);
                        }
                    }


                }
            }
        } else {
            if(doesBoundingBoxContain(p)) {
                // First check if one of the edges contains the point by measuring the distances.
                Edge<TriangleFace> edge;
                if((edge = isPointOnEdge(p)) != null) return new ContainsResult(Location.BORDER, this, edge);

                // If not the above, do a side check to check containment.
                if (checkRelativeSide(t.p1, t.p2, p) <= 0 && checkRelativeSide(t.p2, t.p3, p) <= 0
                        && checkRelativeSide(t.p3, t.p1, p) <= 0) return new ContainsResult(Location.INSIDE, this, null);
            }
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
        return Math.abs(v1) < epsilon * epsilon;
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
        INSIDE, BORDER, OUTSIDE, BORDER_FLAGGED
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
