package geo.store.gui;

import geo.store.math.Point2d;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Draw a line in the gui.
 */
public class Line implements IDrawable {
    // The line is represented as a line... captain obvious.
    private final Line2D.Double shape;

    /**
     * Create a line between the two points.
     *
     * @param from The starting point of the line segment.
     * @param to The end point of the line segment.
     */
    public Line(Point2d from, Point2d to) {
        // Create a shape.
        shape = new Line2D.Double(from.x, from.y, to.x, to.y);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        // Draw the shape, with the desired color.
        g.draw(shape);
    }
}
