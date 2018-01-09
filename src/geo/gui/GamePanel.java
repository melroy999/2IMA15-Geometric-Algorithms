package geo.gui;

import geo.delaunay.TriangleFace;
import geo.state.GameState;
import geo.store.halfedge.Edge;
import geo.store.halfedge.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * The panel in which the game is played.
 */
public class GamePanel extends JPanel {
    // Reference to the GUI this panel is part of.
    private final GUI gui;

    // The game state we will attempt to render.
    private GameState state;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     *
     * @param gui The gui this panel is part of.
     */
    public GamePanel(GUI gui) {
        this.gui = gui;
    }

    /**
     * Set the game state for the game panel.
     *
     * @param state The game state we want to render.
     */
    void setState(GameState state) {
        this.state = state;
    }

    /**
     * Draw onto the canvas.
     *
     * @param g The graphics object to drawPoints on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Convert to a two-dimensional space graphics object.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the faces we currently have to drawPoints.
        Set<TriangleFace> faces = state.getTriangulatedFaces();

        // Depending on the options in the GUI, we want to drawPoints/not drawPoints certain components.
        if(gui.drawVoronoiDiagram() && state.getVoronoiDiagram() != null) state.getVoronoiDiagram().draw(g2);
        if(gui.drawDelaunayTriangulation()) drawDelaunayTriangulation(g2, faces);
        if(gui.drawCircumCenters()) drawCircumCenters(g2, faces);
        if(gui.drawCircumCircles()) drawCircumCircles(g2, faces);
        if(gui.drawDebugLabels()) drawDebugLabels(g2, faces, gui.drawDelaunayTriangulation());

        // Lastly, draw the points, as we always want them on top.
        drawPoints(g2, state);
    }

    /**
     * Draw the points placed by the players.
     *
     * @param g The graphics object.
     * @param state The current game state.
     */
    public void drawPoints(Graphics2D g, GameState state) {
        // Paint all the points.
        for(Vertex v : state.getPoints()) {
            v.drawPoint(g);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     */
    public void drawDelaunayTriangulation(Graphics2D g, Set<TriangleFace> faces) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            // Draw the face.
            face.drawFace(g);

            // Iterate over all the edges in the cycle around the face.
            for(Edge<TriangleFace> edge : face) {
                // Check if e is related to a symbolic vertex.
                if(edge.origin instanceof Vertex.SymbolicVertex || edge.twin.origin instanceof Vertex.SymbolicVertex) {
                    // If it is, do not render.
//                    continue;
                }

                // We use a gray color for triangulation edges. Purple for invalid edges!
                if(face.isIllegal(edge)) {
                    g.setColor(new Color(160, 0, 255));
                } else {
                    g.setColor(Color.gray);
                }

                // Draw the edge.
                edge.drawEdge(g);
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
            face.drawCircumCenter(g);
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
            face.drawCircumCircle(g);
        }
    }

    /**
     * Draw the structure of the Delaunay triangulation.
     *
     * @param g The graphics object.
     * @param faces The faces in the triangulation.
     * @param facesVisible Whether the faces are visible currently...
     */
    public void drawDebugLabels(Graphics2D g, Set<TriangleFace> faces, boolean facesVisible) {
        // Set the lines to be 3 pixels wide.
        g.setStroke(new BasicStroke(3));

        // Draw all the edges, using the faces as reference.
        for(TriangleFace face : faces) {
            // Draw the face debug label.
            if(facesVisible) face.drawLabel(g);

            // Iterate over all the edges in the cycle around the face.
            for(Edge edge : face) {
                edge.origin.drawLabel(g);
            }
        }
    }
}
