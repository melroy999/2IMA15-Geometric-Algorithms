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

    // The x and y coordinates as integers.
    private final int x, y;

    // Additional information for rendering the point, like the color and labels.
    private final String label;
    private final Color color;

    // The radius of the point.
    private final double radius;

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param label The label of the point.
     * @param color The color of the point.
     * @param radius The radius of the point.
     */
    public Point(double x, double y, String label, Color color, double radius) {
        // Since the center point is (x,y), we have to do some calculations with the radius.
        this.shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);

        // Save the x and y coordinates such that we can render the label when required.
        this.x = (int) x + 14;
        this.y = (int) y + 6;

        // Set the label and color.
        this.label = label;
        this.color = color;
        this.radius = radius;
    }

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param label The label of the point.
     * @param color The color of the point.
     */
    public Point(double x, double y, String label, Color color) {
        this(x, y, label, color, 10);
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     * @param debug Whether we should view debug information.
     */
    @Override
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

    /**
     * Two points are equal when the distance between the point centers is less than the radius of the points.
     *
     * @param obj The object we want to check equality for.
     * @return True if the reference is the same, or the distance between the points is less than the radius.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            // Points are equal if the distance between them is less than the radius.
            Point p = (Point) obj;
            return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2)) <= radius;
        }
        return super.equals(obj);
    }

    /**
     * Create an 'unique' hash for the point.
     *
     * @return A hash for the point.
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
