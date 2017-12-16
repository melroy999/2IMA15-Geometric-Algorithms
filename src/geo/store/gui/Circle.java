package geo.store.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draw a hollow circle in the gui.
 */
public class Circle implements IDrawable {
    // The point is represented as an elliptical shape.
    private final Ellipse2D shape;

    /**
     * Create a circle graphic shape.
     *
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param radius The radius of the circle.
     */
    public Circle(double x, double y, double radius) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
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
        g.draw(shape);
    }
}
