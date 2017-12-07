package alg.structure.halfedge;

import alg.structure.geom.Vector2d;

import java.util.Iterator;

/**
 * Edges in a half-edge graph structure.
 */
public class Edge implements Iterable<Edge> {
    // The vertex this half edge originates from.
    public Vertex origin;

    // The face to the left of this half edge.
    public Face incidentFace;

    // The half edge that moves in the opposite direction.
    public Edge twin;

    // The next half edge in our cycle.
    public Edge next;

    // The previous half edge in our cycle.
    public Edge previous;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    // The vector representation of this edge.
    public final Vector2d vector;

    /**
     * Create a half-edge originating from the given vertex.
     * @param origin The vertex that is the starting point of this half-edge.
     */
    public Edge(Vertex origin, Vertex target) {
        // Set informational fields.
        this.origin = origin;
        origin.incidentEdge = this;

        // Calculate the vector.
        vector = new Vector2d(target.x - origin.x, target.y - origin.y);

        // Give an unique id.
        id = counter++;
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

    /**
     * Check whether the given edge would be the next edge, theoretically.
     *
     * @param edge The edge we potentially want to insert as the next edge.
     * @return Whether the edge is the logical follow up of this edge.
     */
    public boolean isEdgeCandidateForNext(Edge edge) {
        // First, we need to get 'vectors' of all the edges in question.
        Vector2d a = this.vector;
        Vector2d b = edge.vector;
        Vector2d c = this.vector;

        // First, get the determinant of a and c, to determine the approximate size of the angle ac.
        double det = a.det(c);

        if(det <= 0) {
            // If the determinant is smaller than 0, the angle between a and c is smaller than 180 degrees.
            // Thus, we need that the determinant of a - b and b - c is also smaller than 0.
            return a.det(b) <= 0 && b.det(c) <= 0;
        } else {
            // If the determinant is larger than 0, the angle between a and c is larger than 180 degrees.
            // Thus, if we switch a and c, c - b and b - a should not have a determinant smaller than 0.
            return !(c.det(b) <= 0 && b.det(a) <= 0);
        }
    }
}
