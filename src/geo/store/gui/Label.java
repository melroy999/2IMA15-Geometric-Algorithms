package geo.store.gui;

import geo.util.Constants;

import java.awt.*;

/**
 * Draw a text label in the gui.
 */
public class Label implements IDrawable {
    // The x and y coordinates as integers.
    private final int x, y;

    // Additional information for rendering the point, like the color and labels.
    private final String label;

    /**
     * Create a point graphic shape.
     *
     * @param x The x-coordinate of the center of the point.
     * @param y The y-coordinate of the center of the point.
     * @param label The label of the point.
     */
    public Label(double x, double y, String label) {
        // Save the x and y coordinates such that we can render the label when required.
        this.x = (int) x + 14;
        this.y = (int) y + 6;

        // Set the label and color.
        this.label = label;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to drawPoints in.
     */
    @Override
    public void draw(Graphics2D g) {
        // If we debug, we also want to drawPoints the label of the point.
        g.setFont(Constants.font);
        g.setColor(Color.BLACK);

        // Draw the text.
        g.drawString(label, x, y);
    }
}
