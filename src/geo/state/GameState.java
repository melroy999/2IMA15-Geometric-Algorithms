package geo.state;

import geo.structure.geo.TriangulationMesh;
import geo.structure.gui.Point;
import geo.voronoi.DelaunayTriangulator;

import java.awt.*;
import java.util.ArrayList;

public class GameState {
    // The points put down by the blue and red players.
    private final ArrayList<Point> bluePoints;
    private final ArrayList<Point> redPoints;

    // The color of the player that currently has the turn.
    private Player currentPlayer;

    // The current state of the triangulator.
    private DelaunayTriangulator triangulator;

    /**
     * Initialize the game state.
     */
    public GameState() {
        this.bluePoints = new ArrayList<>();
        this.redPoints = new ArrayList<>();
        currentPlayer = Player.RED;
        triangulator = new DelaunayTriangulator();
    }

    /**
     * Add the point to the game state.
     *
     * @param p The point we want to add.
     * @return whether the point was added to the game state or not.
     */
    public boolean addPoint(java.awt.Point p) {
        // First, convert to our own clickPosition type.
        Point point = new Point(p.x, p.y, "", currentPlayer == Player.RED ? Color.RED : Color.BLUE);

        // If the point already exists, do nothing.
        if(checkPointExistence(point)) return false;

        // Insert the point into the triangulation.
        try {
            triangulator.insert(p);
        } catch (TriangulationMesh.PointInsertedInOuterFaceException | TriangulationMesh.EdgeNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // Depending on whose turn it is, add a point to the corresponding list.
        if(currentPlayer == Player.RED) {
            redPoints.add(point);
        } else {
            bluePoints.add(point);
        }

        // Return that we successfully added the point to the game state.
        return true;
    }

    /**
     * Check whether the given point already exists for one of the users.
     * @param point The point we want to check the existence of.
     * @return Whether there exists any point in the red or blue sets that is equal to the given point.
     */
    public boolean checkPointExistence(Point point) {
        return redPoints.stream().anyMatch(point::equals) || bluePoints.stream().anyMatch(point::equals);
    }

    /**
     * Get the points of the red player.
     *
     * @return The points of the red player.
     */
    public ArrayList<Point> getRedPoints() {
        return redPoints;
    }

    /**
     * Get the points of the blue player.
     *
     * @return The points of the blue player.
     */
    public ArrayList<Point> getBluePoints() {
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
        currentPlayer = Player.RED;
        triangulator = new DelaunayTriangulator();
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
     * Define the different players we have in the game, in this case by color.
     */
    public enum Player {
        RED, BLUE;

        public Player next() {
            return this == RED ? BLUE : RED;
        }
    }
}
