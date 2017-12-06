package alg.halfedge;

import java.util.Iterator;
import java.util.List;

/**
 * Edges in a half-edge data structure.
 */
public class Edge implements Iterable<Edge> {
    // The vertex this half edge originates from.
    public final Vertex origin;

    // The face to the left of this half edge.
    public Face incidentFace;

    // The half edge that moves in the opposite direction.
    public Edge twin;

    // The next half edge in our cycle.
    public Edge next;

    // The previous half edge in our cycle.
    public Edge previous;

    /**
     * Create a half-edge originating from the given vertex.
     * @param origin The vertex that is the starting point of this half-edge.
     */
    public Edge(Vertex origin) {
        this.origin = origin;
        origin.incidentEdge = this;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "origin=" + origin +
                '}';
    }

    /**
     * Iterate over all the edges that can be found in the next cycle.
     *
     * @return An iterator that visits all edges in the next cycle in the correct order.
     */
    @Override
    public Iterator<Edge> iterator() {
        return new Iterator<Edge>() {
            // We need to know where we started, such that we will not accidentally report the same edge twice.
            int originId = Edge.this.origin.id;

            // A pointer to the current edge.
            final Edge[] current = { null };

            /**
             * Check whether we have a next edge we want to visit.
             *
             * @return true if we currently have no current edge, or when the next edge is not the original edge.
             */
            @Override
            public boolean hasNext() {
                return current[0] == null || current[0].next.origin.id != originId;
            }

            /**
             * Return the next edge in the cycle.
             *
             * @return if the current edge is null, we start with the first edge.
             * Otherwise, return the next edge of the current edge.
             */
            @Override
            public Edge next() {
                if(current[0] == null) {
                    current[0] = Edge.this;
                } else {
                    current[0] = current[0].next;
                }
                return current[0];
            }
        };


    }
}
