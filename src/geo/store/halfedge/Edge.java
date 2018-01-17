package geo.store.halfedge;

import geo.store.gui.Line;
import geo.store.math.Point2d;
import geo.store.math.Vector2d;

import java.awt.*;

/**
 * A half-edge in a half edge structure.
 */
public class Edge<T> {
    // The vertex this half edge originates from.
    public final Vertex<T> origin;

    // The face to the left of this half edge.
    public Face<T> incidentFace;

    // The half edge that moves in the opposite direction.
    public final Edge<T> twin;

    // The next half edge in our cycle.
    private Edge<T> next;

    // The previous half edge in our cycle.
    private Edge<T> previous;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id = counter++;

    // The visual representation of the edge.
    private final Line shape;

    /**
     * Create a half-edge pair originating from the given vertex.
     *
     * @param origin The vertex that is the start point of this half-edge.
     * @param target The vertex that is the end point of this half-edge.
     */
    public Edge(Vertex<T> origin, Vertex<T> target) {
        // Set the origin of the edge.
        this.origin = origin;

        // Set the twin of the edge.
        this.twin = new Edge<>(target, origin, this);

        // Ensure that at least one incident edge is set for the vertex.
        origin.incidentEdge = this;

        // Create a visual representation of the line.
        if(!(origin instanceof Vertex.SymbolicTopVertex || origin instanceof Vertex.SymbolicBottomVertex)) {
            if(!(target instanceof Vertex.SymbolicTopVertex || target instanceof Vertex.SymbolicBottomVertex)) {
                shape = new Line(origin, origin.interpolate(target, 0.5d));
            } else {
                // Draw a straight line with the same y coordinate.
                shape = new Line(origin, new Point2d(target instanceof Vertex.SymbolicTopVertex ? 4000 : -10, origin.y));
            }
        } else {
            if(!(target instanceof Vertex.SymbolicTopVertex || target instanceof Vertex.SymbolicBottomVertex)) {
                shape = new Line(new Point2d(origin instanceof Vertex.SymbolicTopVertex ? 4000 : -10, target.y), target);
            } else {
                // Both are symbolic, so don't draw.
                shape = null;
            }
        }
    }

    /**
     * Create a half-edge pair originating from the given vertex.
     *
     * @param origin The vertex that is the start point of this half-edge.
     * @param target The vertex that is the end point of this half-edge.
     * @param twin The twin of this half-edge.
     */
    private Edge(Vertex<T> origin, Vertex<T> target, Edge<T> twin) {
        // Set the origin of the edge.
        this.origin = origin;

        // Set the twin of the edge.
        this.twin = twin;

        // Ensure that at least one incident edge is set for the vertex.
        origin.incidentEdge = this;

        // Create a shape we can drawPoints for this structure.
        // Create a visual representation of the line.
        if(!(origin instanceof Vertex.SymbolicTopVertex || origin instanceof Vertex.SymbolicBottomVertex)) {
            if(!(target instanceof Vertex.SymbolicTopVertex || target instanceof Vertex.SymbolicBottomVertex)) {
                shape = new Line(origin, origin.interpolate(target, 0.5d));
            } else {
                // Draw a straight line with the same y coordinate.
                shape = new Line(origin, new Point2d(target instanceof Vertex.SymbolicTopVertex ? 4000 : -10, origin.y));
            }
        } else {
            if(!(target instanceof Vertex.SymbolicTopVertex || target instanceof Vertex.SymbolicBottomVertex)) {
                shape = new Line(new Point2d(origin instanceof Vertex.SymbolicTopVertex ? 4000 : -10, target.y), target);
            } else {
                // Both are symbolic, so don't draw.
                shape = null;
            }
        }
    }

    /**
     * Add the given edge as the next of this edge, while also setting the previous of that edge to this edge.
     *
     * @param e The edge we want as the next edge.
     */
    public void setNext(Edge<T> e) {
        this.next = e;
        e.previous = this;
    }

    /**
     * Get the follow up edge in the cycle around the incident face.
     *
     * @return The next edge in the cycle.
     */
    public Edge<T> next() {
        return next;
    }

    /**
     * Get the previous edge in the cycle around the incident face.
     *
     * @return The previous edge in the cycle.
     */
    public Edge<T> previous() {
        return previous;
    }

    /**
     * Get the minimum distance between the line segment and the point.
     *
     * @param p The point to measure the distance to.
     * @return The minimum distance between p and this edge.
     */
    public double getDistancePointToSegment(Point2d p) {
        // Calculate the values we want to work with, which are the square length and dot product.
        Point2d p1 = origin;
        Point2d p2 = twin.origin;
        double lengthSquared = (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y);
        if(lengthSquared == 0) return p1.distance(p);

        // Now, we have the line through p1 and p2: p1 + x * (p2 - p1), we want to find the projection of p onto this line.
        // This holds when x = [(p-p1) * (p2-p1)] / lengthSquared. Clamp x to the range 0-1, such that we cannot get out of the segment.
        Vector2d v1 = new Vector2d(p1, p);
        Vector2d v2 = new Vector2d(p1, p2);
        double x = Math.max(0, Math.min(1, v1.dot(v2) / lengthSquared));
        Point2d projection = p1.add(v2.scale(x));
        return p.distance(projection);
    }

    /**
     * Draw the object.
     *
     * @param g     The graphics object to use.
     */
    public void drawEdge(Graphics2D g) {
        // Draw the shape stored in the object.
        if(shape != null) shape.draw(g);
    }

    /**
     * Get the string representation of the edge.
     *
     * @return e concatenated with the string presentations of the points.
     */
    @Override
    public String toString() {
        return "e" + id + "(" + origin.toString() + "->" + twin.origin.toString() + ")";
    }
}
