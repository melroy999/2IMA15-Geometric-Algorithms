package geo.store.halfedge;

import geo.store.math.Point2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A face in a half edge structure, which not necessarily has to be a triangle.
 */
public abstract class Face<T> implements Iterable<Edge<T>> {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge<T> outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id = counter++;

    // The area of this face.
    private final double area;

    // The center point of the face.
    public final Point2d centerPoint;

    /**
     * Create a face, given the edges surrounding it in counter clock wise order.
     *
     * @param edges The edges of the face, in CCW order.
     */
    public Face(Point2d centerPoint, List<Edge<T>> edges) {
        // Set the center point.
        this.centerPoint = centerPoint;

        // Make the pointers of the face edges sound. I.e. make sure that the cycle is correct, set face relations etc.
        for(int i = 0; i < edges.size(); i++) {
            edges.get(i).setNext(edges.get((i + 1) % edges.size()));
            edges.get(i).incidentFace = this;
        }

        // Finally, make a reference to one of the edges in the cycle.
        if(edges.size() != 0) outerComponent = edges.get(0);

        // Calculate the area of the face.
        this.area = calculateArea();
    }

    /**
     * Create a face, given the edges surrounding it in counter clock wise order.
     *
     * @param edges The edges of the face, in CCW order.
     */
    public Face(List<Edge<T>> edges) {
        this(calculateCenterPoint(edges), edges);
    }

    /**
     * Calculate the center point by taking the average of the points.
     *
     * @param edges The edges to base the center point on.
     * @return A point at 0, 0 if no edges are given, average of the points otherwise.
     */
    private static <T> Point2d calculateCenterPoint(List<Edge<T>> edges) {
        Point2d p = new Point2d();

        for(Edge edge : edges) {
            p = p.add(edge.origin);
        }

        if (edges.size() > 0) p.scale(1d / edges.size());

        return p;
    }

    /**
     * Calculate the total area of this face in pixels.
     *
     * @return The area of the face measured in pixels.
     */
    private double calculateArea() {
        double doubleArea = 0;
        for(Edge<T> edge : this) {
            doubleArea += (edge.origin.y + edge.next().origin.y) * (edge.next().origin.x - edge.origin.x);
        }
        return doubleArea / 2;
    }

    /**
     * Get the area of the face.
     *
     * @return The area of the face.
     */
    public double getArea() {
        return area;
    }

    /**
     * Get the edges of edges around this face.
     *
     * @return An arraylist of edges.
     */
    public ArrayList<Edge<T>> edges() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<T>> edges = new ArrayList<>();

        // The current edge we are on.
        Edge<T> current = outerComponent;

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
    public Iterator<Edge<T>> iterator() {
        // First, make a edges of all edges we can reach.
        ArrayList<Edge<T>> edges = edges();

        // Now, return the iterator over this array edges.
        return edges.iterator();
    }

    /**
     * Get the string representation of the face.
     *
     * @return f concatenated with the id of the face, together with the string representation of all corner points.
     */
    @Override
    public String toString() {
        return "f" + id + "(" + edges().stream().map(e -> e.origin).collect(Collectors.toList()) +  ")";
    }
}
