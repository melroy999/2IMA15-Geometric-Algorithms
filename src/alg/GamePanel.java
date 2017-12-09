package alg;

import alg.structure.geom.Point2d;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    // Define the colors we will use for points, lines and faces.
    private static final Color RED_POINT_COLOR = new Color(255, 0, 0);
    private static final Color BLUE_POINT_COLOR = new Color(0, 0, 255);
    private static final Color RED_LINE_COLOR = new Color(255, 85, 0);
    private static final Color BLUE_LINE_COLOR = new Color(0, 85, 255);
    private static final Color RED_FACE_COLOR = new Color(255, 170, 0);
    private static final Color BLUE_FACE_COLOR = new Color(0, 170, 255);

    // States we can use to disable/enable certain drawing layers.
    public boolean drawTriangulation;
    public boolean drawCircumcircles;
    public boolean drawDebugOverlay;

    // Reference to a font.
    private Font font = new Font("SansSerif", Font.PLAIN, 18);

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

        if (drawTriangulation) drawTriangulation(g2, state);
        if (drawCircumcircles) drawCircumcircles(g2, state);
        paintPoints(g2, state);
        if (drawDebugOverlay) drawDebugOverlay(g2, state);
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

    public void drawDebugOverlay(Graphics2D g, GameState state) {
        ArrayList<Face> faces = state.triangulator.getMesh().getSearcher().getFaces();

        // Draw all the edges, using the faces. Make sure that half edges are really half edges (only half length).
        for(Face face : faces) {
            // First, draw an id number.
            if(!(face instanceof Face.OuterFace)) {
                g.setFont(font);
                g.setColor(Color.BLACK);
                Point2d c = face.getCenter();
                g.drawString("f=" + face.id, (int) c.x + 14, (int) c.y + 6);
            }

            // Iterate over inner cycle.
            for(Edge e : face.outerComponent) {
                // Draw the id of the origin vertex.
                g.setFont(font);
                g.setColor(Color.BLACK);
                g.drawString("v=" + e.origin.id, (int) e.origin.x + 14, (int) e.origin.y + 6);
            }
        }
    }

    public void drawTriangulation(Graphics2D g, GameState state) {
        ArrayList<Face> faces = state.triangulator.getMesh().getSearcher().getFaces();

        // These lines can be wider.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces. Make sure that half edges are really half edges (only half length).
        for(Face face : faces) {

            // Iterate over inner cycle.
            for(Edge e : face.outerComponent) {
                // Check if e is related to a symbolic vertex.
                if(e.origin instanceof Vertex.SymbolicVertex || e.twin.origin instanceof Vertex.SymbolicVertex) {
                    // If it is, do not render.
                    continue;
                }

                // We use a green color for triangulation edges.
                g.setColor(Color.GREEN);

                // Now draw the edges.
                g.draw(e.shape);
            }
        }
    }

    public void drawCircumcircles(Graphics2D g, GameState state) {
        ArrayList<Face> faces = state.triangulator.getMesh().getSearcher().getFaces();

        // We want thin lines here.
        g.setStroke(new BasicStroke(1));

        // Draw all the edges, using the faces. Make sure that half edges are really half edges (only half length).
        for(Face face : faces) {
            if(face instanceof Face.OuterFace || face.hasSymbolicPoint()) {
                continue;
            }

            g.setColor(Color.magenta);
            g.draw(face.getCircumCircle());
        }
    }
}
