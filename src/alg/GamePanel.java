package alg;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    // Define the colors we will use for points, lines and faces.
    private static final Color RED_POINT_COLOR = new Color(255, 0, 0);
    private static final Color BLUE_POINT_COLOR = new Color(0, 0, 255);
    private static final Color RED_LINE_COLOR = new Color(255, 85, 0);
    private static final Color BLUE_LINE_COLOR = new Color(0, 85, 255);
    private static final Color RED_FACE_COLOR = new Color(255, 170, 0);
    private static final Color BLUE_FACE_COLOR = new Color(0, 170, 255);

    // We want to store the manager such that we can request game state access.
    private GameManager manager;

    public void setManager(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Request the game state.
        GameState state = manager.getGameState();
        paintPoints(g2, state);
    }

    public void paintPoints(Graphics2D g, GameState state) {
        // Start with painting the red points.
        g.setColor(RED_POINT_COLOR);

        // Paint all the points.
        for(Point p : state.getRedPoints()) {
            g.fill(p.shape);
        }

        // Now do the same for the blue points.
        g.setColor(BLUE_POINT_COLOR);

        // Paint all the points.
        for(Point p : state.getBluePoints()) {
            g.fill(p.shape);
        }
    }
}
