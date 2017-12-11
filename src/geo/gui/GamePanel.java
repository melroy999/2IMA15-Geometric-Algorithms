package geo.gui;

import geo.engine.GameEngine;
import geo.state.GameState;
import geo.structure.geo.Edge;
import geo.structure.geo.Face;
import geo.structure.geo.Vertex;
import geo.structure.gui.Point;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * The panel containing the graphics of the game.
 */
public class GamePanel extends JPanel {
    // Keep a reference to the game engine, so that we can request the game state.
    private GameEngine engine;

    // States we can use to disable/enable certain drawing layers.
    public boolean drawTriangulation;
    public boolean drawCircumcircles;
    public boolean drawDebugLabels;

    /**
     * Set the game engine, such that we can access the game state.
     *
     * @param engine The game engine.
     */
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Draw onto the canvas.
     *
     * @param g The graphics object to draw on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Request the game state.
        GameState state = engine.getState();

        // Get the list of faces, as we seem to be using it everywhere.
        Set<Face> faces = state.getTriangulator().getMesh().getSearcher().getFaces();

        // Only draw the triangulation when asked for it.
        if (drawTriangulation) drawTriangulation(g2, faces);

        // Only draw the circumcircles when asked for them.
        if (drawCircumcircles) drawCircumcircles(g2, faces);

        // Always draw the points.
        drawPoints(g2, state);

        // Draw the debug labels last, as they are considered an overlay and should just be rendered on top.
        if (drawDebugLabels) drawDebugLabels(g2, faces);
    }

    /**
     * Draw the points placed by the players.
     *
     * @param g The graphics object.
     * @param state The current game state.
     */
    public void drawPoints(Graphics2D g, GameState state) {
        // Paint all the points.
        for(Point p : state.getRedPoints()) {
            p.draw(g, false);
        }
        for(Point p : state.getBluePoints()) {
            p.draw(g, false);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawTriangulation(Graphics2D g, Set<Face> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(Face face : faces) {
            // Draw the face.
            face.draw(g, false);

            // Iterate over all the edges in the cycle around the face.
            for(Edge edge : face) {
                // Check if e is related to a symbolic vertex.
                if(edge.origin instanceof Vertex.SymbolicVertex || edge.twin.origin instanceof Vertex.SymbolicVertex) {
                    // If it is, do not render.
                    continue;
                }

                // We use a green color for triangulation edges. Cyan for invalid edges!
                if(edge.isIllegal()) {
                    g.setColor(Color.CYAN);
                } else {
                    g.setColor(Color.GREEN);
                }

                // Draw the edge.
                edge.draw(g, false);
            }
        }
    }

    /**
     * Draw the circum circles in the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawCircumcircles(Graphics2D g, Set<Face> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(1));

        // Draw all the edges, using the faces as reference.
        for(Face face : faces) {
            if(face instanceof Face.OuterFace || face.ccr > 5 * 10e3) {
                continue;
            }

            // Draw the circumcircle.
            face.drawcc(g, false);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawDebugLabels(Graphics2D g, Set<Face> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(Face face : faces) {
            // Draw the face debug label.
            face.drawOverlay(g, false);

            // Iterate over all the edges in the cycle around the face.
            for(Edge edge : face) {
                edge.origin.draw(g, false);
            }
        }
    }
}
