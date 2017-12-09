package alg;

import alg.structure.geom.Point2d;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    // Define the colors we will use for points, lines and faces.
    private static final Color RED_POINT_COLOR = new Color(255, 0, 0, 125);
    private static final Color BLUE_POINT_COLOR = new Color(0, 0, 255, 125);
    private static final Color RED_LINE_COLOR = new Color(255, 85, 0, 125);
    private static final Color BLUE_LINE_COLOR = new Color(0, 85, 255, 125);
    private static final Color RED_FACE_COLOR = new Color(255, 170, 0, 125);
    private static final Color BLUE_FACE_COLOR = new Color(0, 170, 255, 125);

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
        drawTriangulation(g2, state);
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

    public void drawTriangulation(Graphics2D g, GameState state) {
        ArrayList<Face> faces = state.triangulator.getMesh().getSearcher().getFaces();

        // Draw all the edges, using the faces. Make sure that half edges are really half edges (only half length).
        for(Face face : faces) {
            // First, draw an id number.
            if(!(face instanceof Face.OuterFace)) {
                Point2d c = face.getCenter();
                g.drawString("id=" + face.id, (int) c.x, (int) c.y);
            }

            // Iterate over inner cycle.
            for(Edge e : face.outerComponent) {
                if (e == face.outerComponent && !(face instanceof Face.OuterFace)) {
                    g.setColor(BLUE_LINE_COLOR);
                } else {
                    g.setColor(RED_LINE_COLOR);
                }

                // Now draw the edges.
                g.setStroke(new BasicStroke(5));
                g.draw(e.shape);
            }
        }
    }
}
