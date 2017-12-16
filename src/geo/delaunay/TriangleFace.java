package geo.delaunay;

import geo.store.gui.Circle;
import geo.store.gui.Label;
import geo.store.gui.Polygon;
import geo.store.gui.Point;

import geo.store.halfedge.Edge;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.store.math.Triangle2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A face in a half edge structure, which is an extension of a Triangle2d.
 */
public class TriangleFace extends Triangle2d implements Iterable<Edge<TriangleFace>> {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge<TriangleFace> outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    // The label that we can draw in the gui.
    private final Label label;

    // The shape of this label.
    private final Polygon shape;

    // The circum circle we can draw in the gui.
    private final Circle circumCircleShape;

    // The circum center we can draw in the gui.
    private final Point circumCenterShape;

    // We will always have an outer face, so keep a static reference to it.
    public final static TriangleFace outerFace = new OuterTriangleFace();

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param e1 The first edge of the triangle.
     * @param e2 The second edge of the triangle.
     * @param e3 The third edge of the triangle.
     */
    public TriangleFace(Edge<TriangleFace> e1, Edge<TriangleFace> e2, Edge<TriangleFace> e3) {
        super(e1.origin, e2.origin, e3.origin);

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
        circumCenterShape = new Point(cc.x, cc.y, "", Color.magenta);
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
        return !(edge.origin instanceof Vertex.SymbolicVertex && edge.twin.origin instanceof Vertex.SymbolicVertex) &&
                (circumCircleContains(edge.twin.previous().origin)
                        || edge.twin.incidentFace.circumCircleContains(edge.previous().origin));
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
        public Triangle2d.Location contains(Point2d p) {
            return Triangle2d.Location.INSIDE;
        }
    }
}
