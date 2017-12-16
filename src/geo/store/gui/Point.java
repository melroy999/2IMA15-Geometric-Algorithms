package geo.store.gui;

import geo.util.Constants;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draw a point in the gui.
 */
public class Point implements IDrawable {
    // The point is represented as an elliptical shape.
    private final Ellipse2D shape;

    // Additional information for rendering the point, like the color.
    private final Color color;

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param color The color of the point.
     * @param radius The radius of the point.
     */
    public Point(double x, double y, Color color, double radius) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);

        // Set the label and color.
        this.color = color;
    }

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param color The color of the point.
     */
    public Point(double x, double y, Color color) {
        this(x, y, color, 10);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        // Draw the shape, with the desired color.
        g.setColor(color);
        g.fill(shape);
    }
}
