package geo.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The panel in which the game is played.
 */
public class GamePanel extends JPanel {
    // Reference to the GUI this panel is part of.
    private final GUI gui;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     *
     * @param gui The gui this panel is part of.
     */
    public GamePanel(GUI gui) {
        this.gui = gui;
    }

    /**
     * Draw onto the canvas.
     *
     * @param g The graphics object to draw on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Convert to a two-dimensional space graphics object.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
