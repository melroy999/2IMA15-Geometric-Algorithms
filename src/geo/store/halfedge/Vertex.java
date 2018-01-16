package geo.store.halfedge;

import geo.state.GameState;
import geo.store.math.Point2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * A vertex in a half edge structure, which is an extension of a Point2d.
 */
public class Vertex<T> extends Point2d implements Iterable<Edge<T>> {
    // One of the half edges originating from this vertex.
    public Edge<T> incidentEdge;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id = counter++;

    // Which player this vertex belongs to.
    public final GameState.PlayerTurn player;

    // The static radius of a vertex.
    private final static int radius = 10;

    /**
     * Create a vertex at the given coordinates.
     *
     * @param x The x-coordinate of the vertex.
     * @param y The y-coordinate of the vertex.
     * @param player The player this vertex belongs to.
     */
    public Vertex(double x, double y, GameState.PlayerTurn player) {
        super(x, y);

        // Set the player.
        this.player = player;
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
     * Two points are equal when the distance between the point centers is less than the radius of the points.
     *
     * @param obj The object we want to check equality for.
     * @return True if the reference is the same, or the distance between the points is less than the radius.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            // Points are equal if the distance between them is less than the radius.
            Vertex<T> v = (Vertex<T>) obj;
            return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2)) <= radius;
        }
        return super.equals(obj);
    }

    /**
     * Create an 'unique' hash for the point.
     *
     * @return A hash for the point.
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
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
}
