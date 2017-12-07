package alg.structure.graph;

import alg.structure.geom.Point2d;
import alg.structure.geom.Triangle2d;

/**
 * TODO temporal class, should be merged with the real face.
 */
public class Face {
    // Each face should have a Triangle2d object, such that we can easily execute a search.
    // Each face should thus be initialized with three points.
    private final Triangle2d triangle;

    // We will always have an outer face, so keep a static reference to it.
    public final static Face outerFace = new OuterFace();

    /**
     * Create a face for the triangle using the three border points.
     *
     * @param p1 The first position.
     * @param p2 The second position.
     * @param p3 The third position.
     */
    public Face(Point2d p1, Point2d p2, Point2d p3) {
        this.triangle = new Triangle2d(p1, p2, p3);
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
    private static class OuterFace extends Face {
        /**
         * Create an outer face.
         */
        private OuterFace() {
            super(null, null, null);
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
}
