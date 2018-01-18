package geo.delaunay;

import geo.store.gui.Circle;
import geo.store.gui.Label;
import geo.store.gui.Polygon;
import geo.store.gui.Point;

import geo.store.halfedge.Edge;
import geo.store.halfedge.Face;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.store.math.Triangle2d;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A face in a half edge structure, which is an extension of a Triangle2d.
 */
public class TriangleFace extends Triangle2d implements Iterable<Edge<TriangleFace>> {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge<TriangleFace> outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    // The label that we can drawPoints in the gui.
    private final Label label;

    // The shape of this label.
    private final Polygon shape;

    // The circum circle we can drawPoints in the gui.
    private final Circle circumCircleShape;

    // The circum center we can drawPoints in the gui.
    private final Point circumCenterShape;

    // We will always have an outer face, so keep a static reference to it.
    public final static TriangleFace outerFace = new OuterTriangleFace();

    // Values that influence the containment test.
    private final double epsilon = 10e-6;

    // A list of edges this triangle originally consisted of.
    private final List<Edge<TriangleFace>> edges;

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param e1 The first edge of the triangle.
     * @param e2 The second edge of the triangle.
     * @param e3 The third edge of the triangle.
     */
    public TriangleFace(Edge<TriangleFace> e1, Edge<TriangleFace> e2, Edge<TriangleFace> e3) {
        super(e1.origin, e2.origin, e3.origin);

        this.edges = Arrays.asList(e1, e2, e3);

        // Assign a new id.
        id = counter++;

        // Make the pointers of the triangle sound. I.e. make sure that the cycle is correct, set face relations etc.
        e1.setNext(e2);
        e2.setNext(e3);
        e3.setNext(e1);

        // Set the incident face for all the edges.
        e1.incidentFace = e2.incidentFace = e3.incidentFace = this;

        // Let the first edge be the outer component.
        outerComponent = e1;

        // Create a shapes.
        shape = new Polygon("", e1.origin, e2.origin, e3.origin);
        label = new Label(c.x, c.y, "f" + id);
        circumCircleShape = new Circle(cc.x, cc.y, ccr);
        circumCenterShape = new Point(cc.x, cc.y, Color.magenta);

        if(e1.origin.y == e2.origin.y && e2.origin.y == e3.origin.y) {
            System.out.println("Equal ys");
        }
    }

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param p1 The first corner point of the triangle.
     * @param p2 The second corner point of the triangle.
     * @param p3 The third corner point of the triangle.
     */
    private TriangleFace(Point2d p1, Point2d p2, Point2d p3) {
        super(p1, p2, p3);

        // Assign a new id.
        id = counter++;

        // Create no label.
        label = null;
        circumCircleShape = null;
        shape = null;
        circumCenterShape = null;
        this.edges = new ArrayList<>();
    }

    /**
     * Check whether this edge is illegal in its current context.
     * An edge cannot be illegal when the edge is between symbolic points.
     *
     * @param edge The edge we want to check the illegality of.
     * @return The edge is illegal if the outer corner point of the face of the twin of the edge is inside the
     * circum circle of the face neighboring this edge. We check this for both sides of the edge.
     */
    public boolean isIllegal(Edge<TriangleFace> edge) {
        return circumCircleContains(edge.twin.previous().origin) || edge.twin.incidentFace.circumCircleContains(edge.previous().origin);
    }

    /**
     * Get the string representation of the face.
     *
     * @return f concatenated with the id of the face, together with the string representation of all corner points.
     */
    @Override
    public String toString() {
        return "f" + id + "(" + p1.toString() + ", " + p2.toString() + ", " + p3.toString() +  ")";
    }

    /**
     * Get the edges of edges around this face.
     *
     * @return An arraylist of edges.
     */
    public ArrayList<Edge<TriangleFace>> edges() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<TriangleFace>> edges = new ArrayList<>();

        // The current edge we are on.
        Edge<TriangleFace> current = outerComponent;

        // Now, loop over all next vertices until we end up at the starting edge.
        do {
            edges.add(current);
            current = current.next();
        } while (current.id != outerComponent.id);

        // Return the arraylist.
        return edges;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void drawFace(Graphics2D g) {
        // We draw the shape in a grey color, with alpha.
        g.setColor(new Color(210, 210, 210, 50));

        if(shape != null) shape.draw(g);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void drawCircumCenter(Graphics2D g) {
        // Draw the label stored in the object.
        if(circumCenterShape != null) circumCenterShape.draw(g);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void drawCircumCircle(Graphics2D g) {
        // We draw circum circles in a magenta color.
        g.setColor(Color.magenta);

        // Draw the label stored in the object.
        if(circumCircleShape != null) circumCircleShape.draw(g);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void drawLabel(Graphics2D g) {
        if(label != null) label.draw(g);
    }

    /**
     * Iterate over all the edges that can be found in the next cycle.
     *
     * @return An iterator that visits all edges in the next cycle in the correct order.
     */
    @Override
    public Iterator<Edge<TriangleFace>> iterator() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<TriangleFace>> edges = edges();

        // Now, return the iterator over this array edges.
        return edges.iterator();
    }

    /**
     * Special class for the outer face.
     */
    public static class OuterTriangleFace extends TriangleFace {
        /**
         * Create an outer face.
         */
        private OuterTriangleFace() {
            super(new Point2d(), new Point2d(), new Point2d());
        }

        /**
         * The outer face always contains the points, so override it to always return the same value.
         *
         * @param p The point we want to check the position of.
         * @return Always return inside.
         */
        @Override
        public ContainsResult contains(Point2d p) {
            return new ContainsResult(Location.INSIDE, this, null);
        }
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
            if (checkRelativeSide(p1, p2, p) >= 0 && checkRelativeSide(p2, p3, p) >= 0
                    && checkRelativeSide(p3, p1, p) >= 0) return new ContainsResult(Location.INSIDE, this, null);
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
        double xMin = Math.min(Math.min(p1.x, p2.x), p3.x) - epsilon;
        double xMax = Math.max(Math.max(p1.x, p2.x), p3.x) + epsilon;
        double yMin = Math.min(Math.min(p1.y, p2.y), p3.y) - epsilon;
        double yMax = Math.max(Math.max(p1.y, p2.y), p3.y) + epsilon;

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
        for (Edge<TriangleFace> e : edges) {
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
     * An enumeration that represents the position of a point relative to the triangle.
     */
    public enum Location {
        INSIDE, BORDER, OUTSIDE
    }
}
