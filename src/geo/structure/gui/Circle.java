package geo.structure.gui;

import geo.structure.IDrawable;
import geo.util.Constants;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draw a hollow circle in the gui.
 */
public class Circle implements IDrawable {
    // The point is represented as an elliptical shape.
    private final Ellipse2D shape;

    // Additional information for rendering the circle, currently only the color.
    private final Color color;

    /**
     * Create a circle graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param color The color of the circle.
     * @param radius The radius of the circle.
     */
    public Circle(double x, double y, Color color, double radius) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);

        // Set the label and color.
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
