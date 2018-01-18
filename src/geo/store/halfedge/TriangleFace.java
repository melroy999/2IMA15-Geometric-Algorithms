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

    // The original edges in the triangle... Because of mutability issues.
    private final ArrayList<Edge<TriangleFace>> edges;

    /**
     * Create a face, given the edges surrounding it in counter clock wise order.
     *
     * @param edges       The edges of the face, in CCW order.
     */
    public TriangleFace(List<Edge<TriangleFace>> edges) {
        super(edges);
        this.edges = new ArrayList<>(edges);

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
        // The points as a list, for convenience.
        List<Point2d> points = Arrays.asList(t.p1, t.p2, t.p3);

        // First check if we have symbolic vertices.
        if(t.bottomSymbolicPoint && t.topSymbolicPoint) {
            // If both are symbolic, by definition the face should contain it, if the y is smaller than the top point.
            // So first, find the top point.
            Point2d top = points.stream().filter(e -> !(e instanceof Vertex.SymbolicBottomVertex || e instanceof Vertex.SymbolicTopVertex)).findAny().get();

            if(top.y >= p.y) {
                return new ContainsResult(Location.INSIDE, this, null);
            }
        } else if(t.bottomSymbolicPoint || t.topSymbolicPoint) {

            for (int i = 0; i < points.size(); i++) {
                // Get the next two points.
                Point2d p1 = points.get((i + 1) % points.size());
                Point2d p2 = points.get((i + 2) % points.size());

                if(points.get(i) instanceof Vertex.SymbolicTopVertex || points.get(i) instanceof Vertex.SymbolicBottomVertex) {
                    // Keep in mind however that it can be on the edges to the symbolic point as well.
                    Optional<Edge<TriangleFace>> edge = this.edges().stream().filter(e -> e.origin == p1).findAny();
                    if(!edge.isPresent()) {
                        throw new RuntimeException("Cannot find an edge originating from " + p1 + " in face " + this + ", triangle object is " + t);
                    }

                    if (equalsZero(edge.get().getDistancePointToSegment(p))) {
                        // It is on the edge with no symbolic vertices, so return the edge.
                        return new ContainsResult(Location.BORDER, this, edge.get());
                    }

                    if (points.get(i) instanceof Vertex.SymbolicTopVertex) {
                        // Now, we can check if it is inside. It is inside if it is left of p1 -> p2, outside otherwise.
                        // It must be between p1 and p2 though in height!
                        if (checkRelativeSide(p1, p2, p) <= 0 && p1.y >= p.y && p2.y <= p.y) {
                            return new ContainsResult(Location.INSIDE, this, null);
                        }
                    } else {
                        // Now, we can check if it is inside. It is inside if it is left of p1 -> p2, outside otherwise.
                        // It must be between p1 and p2 though in height!
                        if (checkRelativeSide(p1, p2, p) <= 0 && p2.y >= p.y && p1.y <= p.y) {
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
        public final TriangleFace face;

        // The edge the point is on, if applicable.
        public final Edge<TriangleFace> edge;

        /**
         * Create a result.
         *
         * @param location The location relative to the face.
         * @param face The face the result is in, if applicable.
         * @param edge The edge the point is on, if applicable.
         */
        public ContainsResult(Location location, TriangleFace face, Edge<TriangleFace> edge) {
            this.location = location;
            this.face = face;
            this.edge = edge;
        }
    }

    /**
     * Check whether the edge should be considered legal.
     *
     * @param edge The edge we want to check legality of.
     * @param p The highest point in our point set.
     * @return Whether the edge is legal according to the rules.
     */
    public boolean isEdgeLegal(Edge<TriangleFace> edge, Vertex<TriangleFace> p) {
        // This can be tricky. If the edge is part of the bounding triangle, it is always legal.
        if (isEdgePartOfInitialTriangle(edge, p)) return true;

        // The vertices v2, v1 for our convenience.
        Vertex<TriangleFace> v2 = edge.twin.previous().origin;

        return !t.isInCircumCircle(v2);

//        if(!t.bottomSymbolicPoint && !t.topSymbolicPoint) {
//            // Now, if we have no symbolic points in the triangle, we should treat it has a normal triangle.
//            /* The situation is as follows
//                                    v1
//                                  /    \
//                                 /      \
//                               tl        tr
//                              /            \
//                             v -- e ------- w
//                             v -- e.twin -- w
//                              \            /
//                               bl        br
//                                 \      /
//                                  \    /
//                                    v2
//             */
//            return !(t.isInCircumCircle(v2) || ((TriangleFace) edge.twin.incidentFace).t.isInCircumCircle(v1));
//        }
//
//        // Now, we might have that we have one of the symbolic points... I will have to think about this.
//        if(t.bottomSymbolicPoint && t.topSymbolicPoint) {
//            // We have both symbolic points, but not the largest point.
//            // We know that the edge cannot be connected to both symbolic points, so it is one or the other.
//            // This will only happen if the point is the bottom most point, which will thus have a circum circle of unlimited size.
//            // So only points below the point should be seen as illegal, which will not happen, so legal.
//            return true;
//        }
//
//        if(t.bottomSymbolicPoint) {
//            // TODO this is not working. Fix it. (Top vertex seems to work fine on illegality detection)
//
//            // DEBUG NOTE: Het lijkt erop dat vertices geplaatst worden zonder een error wanneer er eigenlijk een edge swap hoort plaats te vinden.
//            // Indien de insertion geen edge swap triggert, krijgen we een counter clockwise order exception.
//
//            // We have the bottom symbolic point in the triangle. Depending on whether we have a symbolic point in the edge...
//            if(edge.origin instanceof Vertex.SymbolicBottomVertex) {
//                // The starting point is a symbolic vertex.
//                // Suppose that we have this case, then w should not be left of the line segment v1 -> v2, as the hull would not be convex.
//                return checkRelativeSide(v1, v2, edge.twin.origin) <= 0;
//
//            } else if(edge.twin.origin instanceof Vertex.SymbolicBottomVertex) {
//                // The end point is a symbolic vertex.
//                // The same as above, but now the other way around, where we check illegality of the origin.
//                return checkRelativeSide(v2, v1, edge.origin) <= 0;
//
//            } else {
//                // The edge is not connected to symbolic vertices.
//                // In other words, the symbolic point will never be in the circum circle of the other triangle and vice versa.
//                // Thus, it is always legal.
//                return true;
//            }
//        } else {
//            // We have the bottom symbolic point in the triangle. Depending on whether we have a symbolic point in the edge...
//            if(edge.origin instanceof Vertex.SymbolicTopVertex) {
//                // The starting point is a symbolic vertex.
//                // Suppose that we have this case, then w should not be left of the line segment v2 -> v1, as the hull would not be convex.
//                return checkRelativeSide(v2, v1, edge.twin.origin) <= 0;
//
//            } else if(edge.twin.origin instanceof Vertex.SymbolicTopVertex) {
//                // The end point is a symbolic vertex.
//                // The same as above, but now the other way around, where we check illegality of the origin.
//                return checkRelativeSide(v1, v2, edge.origin) <= 0;
//
//            } else {
//                // The edge is not connected to symbolic vertices.
//                // In other words, the symbolic point will never be in the circum circle of the other triangle and vice versa.
//                // Thus, it is always legal.
//                return true;
//            }
//        }
    }

    /**
     * Check whether the edge is part of the initial triangle.
     *
     * @param edge The edge we want to check for.
     * @return True if both end points of the edge are either symbolic or point p0.
     */
    public boolean isEdgePartOfInitialTriangle(Edge<TriangleFace> edge, Vertex<TriangleFace> p) {
        boolean p1 = edge.origin == p || edge.origin instanceof Vertex.SymbolicTopVertex || edge.origin instanceof Vertex.SymbolicBottomVertex;
        boolean p2 = edge.twin.origin == p || edge.twin.origin instanceof Vertex.SymbolicTopVertex || edge.twin.origin instanceof Vertex.SymbolicBottomVertex;

        // If both are true, it is part of the initial triangle.
        return p1 && p2;
    }

    /**
     * Get the edges of edges around this face.
     *
     * @return An arraylist of edges.
     */
    @Override
    public ArrayList<Edge<TriangleFace>> edges() {
        if(edges != null) {
            return this.edges;
        } else {
            return super.edges();
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
