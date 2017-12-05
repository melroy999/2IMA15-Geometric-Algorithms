package alg;

import java.awt.geom.Ellipse2D;

public class Point {
    // The position of the points.
    public final int x;
    public final int y;

    // Graphical representation of the point.
    public final Ellipse2D shape;

    // More generic stuff for points, such as its radius (in pixels).
    private final int radius = 10;

    /**
     * Create a point that has a shape.
     *
     * @param clickPosition The location on the canvas the user clicked on.
     */
    public Point(java.awt.Point clickPosition) {
        // Take over the x position.
        this.x = clickPosition.x;
        this.y = clickPosition.y;

        // Create the shape.
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            // Points are equal if the distance between them is less than the radius.
            Point p = (Point) obj;
            return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2)) <= radius;
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
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
