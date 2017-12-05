package alg.halfedge;

/**
 * Edges in a half-edge data structure.
 */
public class Edge {
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
}
