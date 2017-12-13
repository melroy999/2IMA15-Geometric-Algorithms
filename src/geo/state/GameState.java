package geo.state;

import geo.structure.geo.TriangleFace;
import geo.structure.geo.TriangulationMesh;
import geo.structure.geo.Vertex;
import geo.structure.gui.Point;
import geo.voronoi.DelaunayTriangulator;
import geo.voronoi.VoronoiDiagram;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class GameState {
    // The points put down by the blue and red players.
    private final ArrayList<Vertex<TriangleFace>> bluePoints;
    private final ArrayList<Vertex<TriangleFace>> redPoints;

    // An arraylist of all vertices.
    private final ArrayList<Vertex<TriangleFace>> points;

    // The color of the player that currently has the turn.
    private Player currentPlayer;

    // The current state of the triangulator.
    private DelaunayTriangulator triangulator;

    // The current state of the Voronoi diagram.
    private VoronoiDiagram diagram;

    // Enable/disable automated rebalancing.
    public boolean rebalanceTree;

    /**
     * Initialize the game state.
     */
    public GameState() {
        this.bluePoints = new ArrayList<>();
        this.redPoints = new ArrayList<>();
        this.points = new ArrayList<>();
        currentPlayer = Player.RED;
        triangulator = new DelaunayTriangulator();
        diagram = new VoronoiDiagram(new ArrayList<>());
    }

    /**
     * Add the point to the game state.
     *
     * @param p The point we want to add.
     * @return whether the point was added to the game state or not.
     */
    public boolean addPoint(java.awt.Point p) {
        // First, convert to our own vertex type.
        Vertex<TriangleFace> vertex = new Vertex<>(p.x, p.y, currentPlayer);

        // If the point already exists, do nothing.
        if(checkPointExistence(vertex)) return false;

        // Rebalance every 5 points.
        if(rebalanceTree && points.size() % 5 == 0) {
            System.out.println("Rebalancing.");
            rebalanceTriangulator();
        }

        long time = System.currentTimeMillis();
        // Insert the point into the triangulation.
        try {
            triangulator.insert(vertex);
        } catch (TriangulationMesh.PointInsertedInOuterFaceException | TriangulationMesh.EdgeNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Triangulation time: " + (System.currentTimeMillis() - time));

        // Depending on whose turn it is, add a point to the corresponding list.
        if(currentPlayer == Player.RED) {
            redPoints.add(vertex);
        } else {
            bluePoints.add(vertex);
        }
        points.add(vertex);

        // Now, calculate the voronoi diagram...
        time = System.currentTimeMillis();
        diagram = new VoronoiDiagram(points);
        System.out.println("Voronoi diagram time: " + (System.currentTimeMillis() - time));

        // Return that we successfully added the point to the game state.
        return true;
    }

    /**
     * Check whether the given point already exists for one of the users.
     * @param vertex The vertex we want to check the existence of.
     * @return Whether there exists any point in the red or blue sets that is equal to the given point.
     */
    public boolean checkPointExistence(Vertex<TriangleFace> vertex) {
        return redPoints.stream().anyMatch(v -> v.shape.equals(vertex.shape))
                || bluePoints.stream().anyMatch(v -> v.shape.equals(vertex.shape));
    }

    /**
     * Get the points of the red player.
     *
     * @return The points of the red player.
     */
    public ArrayList<Vertex<TriangleFace>> getRedPoints() {
        return redPoints;
    }

    /**
     * Get the points of the blue player.
     *
     * @return The points of the blue player.
     */
    public ArrayList<Vertex<TriangleFace>> getBluePoints() {
        return bluePoints;
    }

    /**
     * Switch the currently active player.
     */
    public void switchPlayer() {
        currentPlayer = currentPlayer.next();
    }

    /**
     * Reset the game state.
     */
    public void reset() {
        bluePoints.clear();
        redPoints.clear();
        points.clear();
        currentPlayer = Player.RED;
        triangulator = new DelaunayTriangulator();
        diagram = new VoronoiDiagram(new ArrayList<>());
    }

    /**
     * Rebalance the triangulation tree by inserting the vertices in a random order.
     */
    public void rebalanceTriangulator() {
        triangulator = new DelaunayTriangulator();

        // Add all the points to the triangulator, in a random order.
        Collections.shuffle(points);
        for(Vertex<TriangleFace> point : points) {
            try {
                triangulator.insert(point);
            } catch (TriangulationMesh.EdgeNotFoundException | TriangulationMesh.PointInsertedInOuterFaceException e) {
                e.printStackTrace();
            }
        }

        // Regenerate the Voronoi diagram, which should not have changed, but just to be sure.
        diagram = new VoronoiDiagram(points);
    }

    /**
     * Get the Delaunay triangulator.
     *
     * @return The Delaunay triangulator.
     */
    public DelaunayTriangulator getTriangulator() {
        return triangulator;
    }

    /**
     * Get the voronoi diagram.
     *
     * @return The voronoi diagram in the game state.
     */
    public VoronoiDiagram getDiagram() {
        return diagram;
    }

    /**
     * Define the different players we have in the game, in this case by color.
     */
    public enum Player {
        RED, BLUE;

        public Player next() {
            return this == RED ? BLUE : RED;
        }
    }
}
