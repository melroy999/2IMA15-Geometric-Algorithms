package geo.gui;

import geo.engine.GameEngine;
import geo.state.GameState;
import geo.structure.geo.Edge;
import geo.structure.geo.TriangleFace;
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
    public boolean drawTriangulations;
    public boolean drawCircumCenters;
    public boolean drawCircumCircles;
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
        Set<TriangleFace> faces = state.getTriangulator().getMesh().getSearcher().getFaces();

        // Always draw the voronoi diagram.
        state.getDiagram().draw(g2, engine);

        // Only draw the triangulation when asked for it.
        if (drawTriangulations) drawTriangulations(g2, faces);

        // Only draw the circum centers when asked for them.
        if (drawCircumCenters) drawCircumCenters(g2, faces);

        // Only draw the circumcircles when asked for them.
        if (drawCircumCircles) drawCircumCircles(g2, faces);

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
        for(Vertex v : state.getRedPoints()) {
            v.draw(g, false);
        }
        for(Vertex v : state.getBluePoints()) {
            v.draw(g, false);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawTriangulations(Graphics2D g, Set<TriangleFace> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            // Draw the face.
            face.draw(g, false);

            // Iterate over all the edges in the cycle around the face.
            for(Edge<TriangleFace> edge : face) {
                // Check if e is related to a symbolic vertex.
                if(edge.origin instanceof Vertex.SymbolicVertex || edge.twin.origin instanceof Vertex.SymbolicVertex) {
                    // If it is, do not render.
                    continue;
                }

                // We use a gray color for triangulation edges. Purple for invalid edges!
                if(face.isIllegal(edge)) {
                    g.setColor(new Color(160, 0, 255));
                } else {
                    g.setColor(Color.gray);
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
    public void drawCircumCenters(Graphics2D g, Set<TriangleFace> faces) {
        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            if(face instanceof TriangleFace.OuterTriangleFace || face.ccr > 5 * 10e3) {
                continue;
            }

            // Draw the circumcircle.
            face.drawCircumCenter(g, false);
        }
    }

    /**
     * Draw the circum circles in the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawCircumCircles(Graphics2D g, Set<TriangleFace> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(1));

        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            if(face instanceof TriangleFace.OuterTriangleFace || face.ccr > 5 * 10e3) {
                continue;
            }

            // Draw the circumcircle.
            face.drawCircumCircle(g, false);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawDebugLabels(Graphics2D g, Set<TriangleFace> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            // Draw the face debug label.
            face.drawOverlay(g, false);

            // Iterate over all the edges in the cycle around the face.
            for(Edge edge : face) {
                edge.origin.drawDebug(g, false);
            }
        }
    }
}
