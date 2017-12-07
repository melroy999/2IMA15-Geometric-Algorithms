package alg.structure.halfedge;

import java.awt.*;
import java.util.Iterator;

/**
 * Vertices in a half-edge graph structure.
 */
public class Vertex implements Iterable<Edge> {
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

    /**
     * Iterate over all edges that have as origin this vertex.
     *
     * @return An iterator that can iterate over all edges that have this vertex as origin point.
     */
    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {
            // We want to keep track of the edge we started on, so store the origin of the twin edge.
            int originId = Vertex.this.incidentEdge == null ? -1 : Vertex.this.incidentEdge.twin.origin.id;

            // A pointer to the current edge.
            final Edge[] current = {null};

            /**
             * Check whether we have a next edge we want to visit.
             *
             * @return true if we currently have no current edge, or when the next edge is not the original edge.
             */
            @Override
            public boolean hasNext() {
                return originId != -1 && (current[0] == null || current[0].twin.next.twin.origin.id != originId);
            }

            /**
             * Return the next edge in the cycle.
             *
             * @return if the current edge is null, we start with the first edge.
             * Otherwise, return the next edge of the current edge.
             */
            @Override
            public Edge next() {
                if (current[0] == null) {
                    current[0] = Vertex.this.incidentEdge;
                } else {
                    current[0] = current[0].twin.next;
                }
                System.out.println(current[0]);
                return current[0];
            }
        };
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

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", id=" + id +
                '}';
    }
}
