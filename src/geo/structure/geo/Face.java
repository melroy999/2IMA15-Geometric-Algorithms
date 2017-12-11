package geo.structure.geo;

import geo.structure.IDrawable;
import geo.structure.gui.Polygon;
import geo.structure.math.Point2d;
import geo.structure.math.Triangle2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A face in a half edge structure, which is an extension of a Triangle2d.
 */
public class Face extends Triangle2d implements IDrawable, Iterable<Edge> {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    // The shape that we can draw in the gui.
    private final Polygon shape;

    // We will always have an outer face, so keep a static reference to it.
    public final static Face outerFace = new OuterFace();

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param e1 The first edge of the triangle.
     * @param e2 The second edge of the triangle.
     * @param e3 The third edge of the triangle.
     */
    public Face(Edge e1, Edge e2, Edge e3) {
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

        // Create a shape.
        shape = new Polygon("f" + id, p1, p2, p3);
    }

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param p1 The first corner point of the triangle.
     * @param p2 The second corner point of the triangle.
     * @param p3 The third corner point of the triangle.
     */
    private Face(Point2d p1, Point2d p2, Point2d p3) {
        super(p1, p2, p3);

        // Assign a new id.
        id = counter++;

        // Create no shape.
        shape = null;
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
     * Draw the object.
     *
     * @param g     The graphics object to use.
     * @param debug Whether to view debug information.
     */
    @Override
    public void draw(Graphics2D g, boolean debug) {
        // Draw the shape stored in the object.
        shape.draw(g, debug);
    }

    /**
     * Get the edges of edges around this face.
     *
     * @return An arraylist of edges.
     */
    public ArrayList<Edge> edges() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge> edges = new ArrayList<>();

        // The current edge we are on.
        Edge current = outerComponent;

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
    public Iterator<Edge> iterator() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge> edges = edges();

        // Now, return the iterator over this array edges.
        return edges.iterator();
    }

    /**
     * Special class for the outer face.
     */
    public static class OuterFace extends Face {
        /**
         * Create an outer face.
         */
        private OuterFace() {
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

        /**
         * Draw the object.
         *
         * @param g     The graphics object to use.
         * @param debug Whether to view debug information.
         */
        @Override
        public void draw(Graphics2D g, boolean debug) {
            // Do nothing. We should not draw something that is not visible in the first place.
        }
    }
}
