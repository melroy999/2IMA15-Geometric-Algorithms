import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashSet;

public class GameState {
    // The points put down by the blue and red players.
    private final ArrayList<Point> bluePoints;
    private final ArrayList<Point> redPoints;

    // The color of the player that currently has the turn.
    private Player currentPlayer;

    public GameState() {
        this.bluePoints = new ArrayList<>();
        this.redPoints = new ArrayList<>();
        currentPlayer = Player.RED;
    }

    public void addPoint(java.awt.Point clickPosition) {
        // First, convert to our own clickPosition type.
        Point point = new Point(clickPosition);

        // If the point already exists, do nothing.
        if(checkPointExistence(point)) return;

        // Depending on whose turn it is, add a point to the corresponding list.
        if(currentPlayer == Player.RED) {
            redPoints.add(point);
        } else {
            bluePoints.add(point);
        }

        System.out.println("Adding clickPosition: " + clickPosition);
        System.out.println(redPoints.size());
    }

    public void removePoint(java.awt.Point clickPosition) {
        // First, convert to our own point type.
        Point point = new Point(clickPosition);

        // Depending on whose turn it is, remove a point from the corresponding list.
        if(currentPlayer == Player.RED) {
            redPoints.remove(point);
        } else {
            bluePoints.remove(point);
        }

        System.out.println("Removing point: " + point);
        System.out.println(redPoints.size());
    }

    /**
     * Check whether the given point already exists for one of the users.
     * @param point The point we want to check the existence of.
     * @return Whether there exists any point in the red or blue sets that is equal to the given point.
     */
    public boolean checkPointExistence(Point point) {
        return redPoints.stream().anyMatch(point::equals) || bluePoints.stream().anyMatch(point::equals);
    }

    public ArrayList<Point> getBluePoints() {
        return bluePoints;
    }

    public ArrayList<Point> getRedPoints() {
        return redPoints;
    }

    public void reset() {
        bluePoints.clear();
        redPoints.clear();
        currentPlayer = Player.RED;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.next();
    }

    public enum Player {
        RED, BLUE;

        public Player next() {
            return this == RED ? BLUE : RED;
        }
    }
}
