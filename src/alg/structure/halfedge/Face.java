package alg.structure.halfedge;

import alg.structure.geom.Point2d;
import alg.structure.geom.Triangle2d;

import java.awt.geom.Ellipse2D;

/**
 * TODO temporal class, should be merged with the real face.
 */
public class Face {
    // Each face should have a Triangle2d object, such that we can easily execute a search.
    // Each face should thus be initialized with three points.
    private final Triangle2d triangle;

    // The coordinates of the circumcircle center point.
    public final Point2d circumcircle;

    // The circumcircle as a drawable object.
    public final Ellipse2D c_shape;

    // We will always have an outer face, so keep a static reference to it.
    public final static Face outerFace = new OuterFace();

    // Here, we have one half edge that is part of the cycle around the face.
    public Edge outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    /**
     * Create a face for the triangle using the three border points.
     *
     * @param p1 The first position.
     * @param p2 The second position.
     * @param p3 The third position.
     */
    public Face(Point2d p1, Point2d p2, Point2d p3) {
        // Create the corresponding triangle.
        this.triangle = new Triangle2d(p1, p2, p3);

        // Calculate the circumcircle, using the triangle.
        this.circumcircle = this.triangle.getCircumCircleCenter();

        // Use the point to find the distance to one of the points, say p1.
        double radius = p3.distance(this.circumcircle);

        // Create a filling ellipse.
        this.c_shape = new Ellipse2D.Double(circumcircle.x - radius, circumcircle.y - radius, 2 * radius, 2 * radius);

        // Assign a new id.
        id = counter++;
    }

    /**
     * Get the center of mass of the triangle.
     *
     * @return The center of mass of the triangle.
     */
    public Point2d getCenter() {
        return triangle.getCenter();
    }

    /**
     * Get the circumcircle as a drawable object.
     *
     * @return The shape containing the circumcircle.
     */
    public Ellipse2D getCircumCircle() {
        return this.c_shape;
    }

    /**
     * A simple wrapper for the contains function of the triangle.
     *
     * @param p The point we want to check the position of.
     * @return See javadoc of Triangle2d.contains.
     */
    public Triangle2d.Location contains(Point2d p) {
        return this.triangle.contains(p);
    }

    /**
     * Special class for the outer face.
     */
    public static class OuterFace extends Face {
        /**
         * Create an outer face.
         */
        private OuterFace() {
            super(new Point2d(0, 0), new Point2d(0, 0), new Point2d(0, 0));
        }

        /**
         * The outer face always contains the points, so override it to always return the same value.
         *
         * @param p The point we want to check the position of.
         * @return Always return inside.
         */
        @Override
        public Triangle2d.Location contains(Point2d p) {
            return Triangle2d.Location.INSIDE;
        }
    }

    /**
     * Check whether the face contains at least one symbolic point.
     *
     * @return True if a symbolic point is contained, false otherwise.
     */
    public boolean hasSymbolicPoint() {
        return outerComponent.list().stream().anyMatch(e -> e.origin instanceof Vertex.SymbolicVertex);
    }

    @Override
    public String toString() {
        return "Face{" +
                "triangle=" + triangle +
                "id=" + id +
                '}';
    }
}
