package geo.structure.gui;

import geo.util.Constants;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Draw a point in the gui.
 */
public class Point {
    // The point is represented as an elliptical shape.
    private final Ellipse2D shape;

    // The x and y coordinates as integers.
    private final int x, y;

    // Additional information for rendering the point, like the color and labels.
    private final String label;
    private final Color color;

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param label The label of the point.
     * @param color The color of the point.
     */
    public Point(double x, double y, String label, Color color) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        double radius = Constants.pointRadius;
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);

        // Save the x and y coordinates such that we can render the label when required.
        this.x = (int) x + 14;
        this.y = (int) y + 6;

        // Set the label and color.
        this.label = label;
        this.color = color;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     * @param debug Whether we should view debug information.
     */
    public void draw(Graphics2D g, boolean debug) {
        if(debug) {
            // If we debug, we also want to draw the label of the point.
            g.setFont(Constants.font);
            g.setColor(Color.BLACK);

            // Draw the text.
            g.drawString(label, x, y);
        }

        // Draw the shape, with the desired color.
        g.setColor(color);
        g.fill(shape);
    }
}
