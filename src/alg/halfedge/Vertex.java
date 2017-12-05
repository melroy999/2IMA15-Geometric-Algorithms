package alg.halfedge;

import java.awt.*;

/**
 * Vertices in a half-edge data structure.
 */
public class Vertex {
    // The coordinates of the vertex in the plane.
    public final int x, y;

    // One of the half edges originating from this vertex.
    public Edge incidentEdge;

    // Since we don't want to add the same point twice, we will use an unique id based system.
    private static int counter = 0;
    public final int id;

    /**
     * Create a vertex at the given coordinates.
     *
     * @param x The x-coordinate of the vertex.
     * @param y The y-coordinate of the vertex.
     */
    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
        this.id = counter++;
    }

    /**
     * Create a vertex using a geometric point.
     *
     * @param point The location we want to put the vertex at represented as a point.
     */
    public Vertex(Point point) {
        this(point.x, point.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex p = (Vertex) obj;
            return x == p.x && y == p.y;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
