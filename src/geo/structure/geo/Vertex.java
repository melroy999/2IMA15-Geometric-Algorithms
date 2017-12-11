package geo.structure.geo;

import geo.structure.math.Point2d;

/**
 * A vertex in a half edge structure, which is an extension of a Point2d.
 */
public class Vertex extends Point2d {
    // One of the half edges originating from this vertex.
    public Edge incidentEdge;

    // Since we don't want to add the same point twice, we will use an unique id based system for duplicate detection.
    private static int counter = 0;
    public final int id;

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
    }
}
