package geo.structure.gui;

import geo.structure.IDrawable;
import geo.structure.math.Point2d;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Draw a line in the gui.
 */
public class Line implements IDrawable {
    // The line is represented as a line... captain obvious.
    private final Line2D.Double shape;

    // The color of the line.
    private final Color color;

    /**
     * Create a line between the two points.
     *
     * @param from The starting point of the line segment.
     * @param to The end point of the line segment.
     * @param color The color of the line segment.
     */
    public Line(Point2d from, Point2d to, Color color) {
        // Create a shape.
        shape = new Line2D.Double(from.x, from.y, to.x, to.y);

        // Set the other data.
        this.color = color;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     * @param debug Whether to view debug information.
     */
    @Override
    public void draw(Graphics2D g, boolean debug) {
        // Draw the shape, with the desired color.
        g.setColor(color);
        g.draw(shape);
    }
}
