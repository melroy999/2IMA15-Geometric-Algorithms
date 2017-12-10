package geo.structure.math;

/**
 * A class representing a 2d point.
 */
public class Point2d extends Tuple2d<Point2d> {
    /**
     * Define a point by giving an x and y-coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point2d(double x, double y) {
        super(x, y);
    }

    /**
     * Define a point with coordinates 0, 0.
     */
    public Point2d() {
        super();
    }

    /**
     * Create a new instance of a tuple2d subclass.
     *
     * @param x The x-coordinate of the tuple.
     * @param y The y-coordinate of the point.
     * @return An instance of the desired type, that is an extension of Tuple2d.
     */
    @Override
    protected Point2d get(double x, double y) {
        return new Point2d(x, y);
    }
}
