package geo.structure;

import java.awt.*;

/**
 * An interface for drawable object.
 */
public interface IDrawable {
    /**
     * Draw the object.
     *
     * @param g The graphics object to use.
     * @param debug Whether to view debug information.
     */
    void draw(Graphics2D g, boolean debug);
}
