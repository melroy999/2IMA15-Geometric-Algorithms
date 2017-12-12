package geo.structure.geo;

import geo.structure.IDrawable;
import geo.structure.gui.Label;
import geo.structure.math.Point2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * A vertex in a half edge structure, which is an extension of a Point2d.
 */
public class Vertex<T> extends Point2d implements IDrawable, Iterable<Edge<T>> {
    // One of the half edges originating from this vertex.
    public Edge<T> incidentEdge;

    // Since we don't want to add the same point twice, we will use an unique id based system for duplicate detection.
    private static int counter = 0;
    public final int id;

    // The shape that we can draw in the gui.
    private final Label shape;

    /**
     * Create a vertex at the given coordinates.
     *
     * @param x The x-coordinate of the vertex.
     * @param y The y-coordinate of the vertex.
     */
    public Vertex(double x, double y) {
        super(x, y);

        // Assign a new id.
        id = counter++;

        // Create a drawable figure.
        shape = new Label(x, y, "v" + id);
    }

    /**
     * Get the string representation of the vertex.
     *
     * @return v concatenated with the id, together with its coordinates.
     */
    @Override
    public String toString() {
        return "v" + id + "(" + String.format(Locale.ROOT, "%.1f", x) + ","
                + String.format(Locale.ROOT, "%.1f", y) + ")";
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
     * Get all the edge originating from this vertex.
     *
     * @return An arraylist of edges.
     */
    public ArrayList<Edge<T>> edges() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<T>> edges = new ArrayList<>();

        // The current edge we are on.
        Edge<T> current = incidentEdge;

        // Now, loop over all next vertices until we end up at the starting edge.
        do {
            edges.add(current);
            current = current.twin.next();
        } while (current.id != incidentEdge.id);

        // Return the arraylist.
        return edges;
    }

    /**
     * Iterate over all the edges originating from this vertex.
     *
     * @return An iterator that visits all edges originating from this vertex in CCW order.
     */
    @Override
    public Iterator<Edge<T>> iterator() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<T>> edges = edges();

        // Now, return the iterator over this array edges.
        return edges.iterator();
    }

    /**
     * A vertex subclass for which no edges should be rendered.
     */
    public static class SymbolicVertex<T> extends Vertex<T> {
        /**
         * Create a vertex at the given coordinates.
         *
         * @param x The x-coordinate of the vertex.
         * @param y The y-coordinate of the vertex.
         */
        public SymbolicVertex(double x, double y) {
            super(x, y);
        }
    }
}
