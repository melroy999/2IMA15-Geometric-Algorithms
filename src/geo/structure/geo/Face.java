package geo.structure.geo;

import geo.structure.IDrawable;
import geo.structure.gui.Polygon;
import geo.structure.math.Point2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A face in a half edge structure, which not necessarily has to be a triangle.
 */
public class Face implements IDrawable, Iterable<Edge<Face>> {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge<Face> outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    // The shape of this label.
    private final Polygon shape;

    // The center point of the face.
    private final Vertex<Face> centerPoint;

    /**
     * Create a face, given the edges surrounding it in counter clock wise order.
     *
     * @param edges The edges of the face, in CCW order.
     */
    public Face(Vertex<Face> centerPoint, Edge<Face>... edges) {
        // Assign a new id.
        id = counter++;

        // Set the center point.
        this.centerPoint = centerPoint;

        // Make the pointers of the face edges sound. I.e. make sure that the cycle is correct, set face relations etc.
        // TODO

        // Create the shapes.
        shape = new Polygon("poep^2", Arrays.stream(edges).map(e -> e.origin).toArray(Point2d[]::new));
    }

    /**
     * Draw the object.
     *
     * @param g     The graphics object to use.
     * @param debug Whether to view debug information.
     */
    @Override
    public void draw(Graphics2D g, boolean debug) {
        // We draw the shape in a grey color, with alpha.
        g.setColor(new Color(210, 210, 210, 50));

        // Draw the label stored in the object.
        shape.draw(g, debug);
    }

    /**
     * Get the edges of edges around this face.
     *
     * @return An arraylist of edges.
     */
    public ArrayList<Edge<Face>> edges() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<Face>> edges = new ArrayList<>();

        // The current edge we are on.
        Edge<Face> current = outerComponent;

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
    public Iterator<Edge<Face>> iterator() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<Face>> edges = edges();

        // Now, return the iterator over this array edges.
        return edges.iterator();
    }
}
