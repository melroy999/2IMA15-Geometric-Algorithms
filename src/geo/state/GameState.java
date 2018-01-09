package geo.state;

import geo.controller.GameController;
import geo.delaunay.DelaunayMesh;
import geo.delaunay.DelaunayTriangulator;
import geo.delaunay.TriangleFace;
import geo.player.AbstractPlayer;
import geo.store.halfedge.Vertex;
import geo.voronoi.VoronoiDiagram;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The current state of the playing board.
 */
public class GameState {
    // The two players that are playing the game.
    private HashMap<PlayerTurn, AbstractPlayer> players = new HashMap<>();

    // The player that currently has the turn.
    private PlayerTurn currentPlayerTurn;

    // The points put down by the blue and red players.
    private final List<Vertex<TriangleFace>> bluePoints = new ArrayList<>();
    private final List<Vertex<TriangleFace>> redPoints = new ArrayList<>();

    // The current turn number, based on the amount of turns the red player has had.
    private int currentTurn;

    // The triangulator.
    private DelaunayTriangulator triangulator;

    // The resulting voronoi diagram.
    private VoronoiDiagram voronoiDiagram;

    // The random instance used for shuffling.
    private Random random;

    public GameState() {
        // To initialize, we should use the reset function.
        reset();
    }

    /**
     * Set the predicates in the game controller.
     *
     * @param controller The controller that should receive the predicates.
     */
    public final void setPredicates(GameController controller) {
        controller.setPredicates(this::addPoint, this::removePoint, this::reset);
    }

    /**
     * Add a point to the state.
     *
     * @param p The point to add to the state.
     * @return Whether the insertion of the point was successful or not.
     */
    private boolean addPoint(Point p) {
        // First, convert to our own vertex type.
        Vertex<TriangleFace> vertex = new Vertex<>(p.x, p.y, currentPlayerTurn);

        // If the point already exists, do nothing.
        if(checkPointExistence(vertex)) return false;

        // Blue is only allowed to have n-1 points.
        if(currentPlayerTurn == PlayerTurn.BLUE && getNumberOfRedPoints() <= getNumberOfBluePoints() + 1) {
            return false;
        }

        // Get the list of points, and reconstruct the triangulation/Voronoi diagram.
        List<Vertex<TriangleFace>> points = union(redPoints, bluePoints, Collections.singletonList(vertex));
        if(!reconstruct(points)) return false;

        // Only after all previous insertions pass, add the point to the list of points.
        if(currentPlayerTurn == PlayerTurn.RED) {
            redPoints.add(vertex);
        } else {
            bluePoints.add(vertex);
        }
        return true;
    }

    /**
     * Remove a point from the game state.
     *
     * @param p The point to remove.
     * @return Whether the point was removed successfully or not.
     */
    private boolean removePoint(Point p) {
        // First, convert to our own vertex type.
        Vertex<TriangleFace> vertex = new Vertex<>(p.x, p.y, currentPlayerTurn);

        // Remove the vertex, check if we removed any by checking the return value.
        boolean hasMatch = (currentPlayerTurn == PlayerTurn.RED ? redPoints : bluePoints).removeIf(v -> v.equals(vertex));

        // If we succeeded in removing, reconstruct. Otherwise return false.
        if(hasMatch) {
            reconstruct(union(redPoints, bluePoints));
        }
        return hasMatch;
    }

    /**
     * Reconstruct the triangulation and the Voronoi diagram.
     *
     * @param points The list of points to use in the triangulation.
     * @return Whether the operation was successful or not.
     */
    private boolean reconstruct(List<Vertex<TriangleFace>> points) {
        // We have to enforce randomized incremental construction for the Delaunay triangulation...
        triangulator = new DelaunayTriangulator();

        // The list of all points, shuffled straight after.
        Collections.shuffle(points, random);

        // Insert all already known points, and the new point, in random order.
        for(Vertex<TriangleFace> point : points) {
            try {
                triangulator.insert(point);
            } catch (DelaunayMesh.EdgeNotFoundException | DelaunayMesh.PointInsertedInOuterFaceException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Create the voronoi diagram.
        voronoiDiagram = new VoronoiDiagram(points);
        return true;
    }

    /**
     * Take the union of two lists.
     *
     * @param lists The lists we want to take the union of.
     * @param <T> The type of objects in the list.
     * @return The combination of the two lists.
     */
    @SafeVarargs
    private static <T> List<T> union(List<T>... lists) {
        Set<T> set = new LinkedHashSet<>();

        for(List<T> list : lists) set.addAll(list);

        return new ArrayList<>(set);
    }

    /**
     * Check whether the given point already exists for one of the users.
     * @param vertex The vertex we want to check the existence of.
     * @return Whether there exists any point in the red or blue sets that is equal to the given point.
     */
    public boolean checkPointExistence(Vertex<TriangleFace> vertex) {
        return redPoints.stream().anyMatch(v -> v.equals(vertex))
                || bluePoints.stream().anyMatch(v -> v.equals(vertex));
    }

    /**
     * Get the next player that should have the turn.
     *
     * @return The player object that should be used this turn.
     */
    public AbstractPlayer getCurrentPlayer() {
        // Change the current player's turn.
        return players.get(currentPlayerTurn);
    }

    /**
     * Get the current player turn.
     *
     * @return The current player turn, either red or blue.
     */
    public PlayerTurn getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    /**
     * Change the turn to be the next player's turn.
     */
    public void changeTurn() {
        // If we switch to the red player, increment the turn counter.
        if(currentPlayerTurn.next() == PlayerTurn.RED) {
            currentTurn++;
        }

        // Change to the next player's turn.
        currentPlayerTurn = currentPlayerTurn.next();
    }

    /**
     * Get the current turn number.
     *
     * @return The current turn number, between 0 and infinity.
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Return an immutable list containing the blue points.
     *
     * @return The blue points list as an immutable collection.
     */
    public List<Vertex<TriangleFace>> getBluePoints() {
        return Collections.unmodifiableList(bluePoints);
    }

    /**
     * Get the number of blue points.
     *
     * @return The number of blue points.
     */
    public int getNumberOfBluePoints() {
        return bluePoints.size();
    }

    /**
     * Return an immutable list containing the red points.
     *
     * @return The red points list as an immutable collection.
     */
    public List<Vertex<TriangleFace>> getRedPoints() {
        return Collections.unmodifiableList(redPoints);
    }

    /**
     * Get the number of red points.
     *
     * @return The number of red points.
     */
    public int getNumberOfRedPoints() {
        return redPoints.size();
    }

    /**
     * Return a list containing all points.
     *
     * @return The points list.
     */
    public List<Vertex<TriangleFace>> getPoints() {
        return union(redPoints, bluePoints);
    }

    /**
     * Reset the game state.
     */
    private void reset() {
        // Reset the turn system.
        currentPlayerTurn = PlayerTurn.RED;
        currentTurn = 0;

        // Reset all the stored data.
        bluePoints.clear();
        redPoints.clear();

        // Set a triangulator and voronoi diagram, to avoid null pointers...
        triangulator = new DelaunayTriangulator();
        voronoiDiagram = new VoronoiDiagram(new ArrayList<>());
        random = new Random(8988178178129387065L);
    }

    /**
     * Set the two players that should be used within the game.
     *
     * @param red The red player object.
     * @param blue The blue player object.
     */
    public void setPlayers(AbstractPlayer red, AbstractPlayer blue) {
        players.put(PlayerTurn.RED, red);
        players.put(PlayerTurn.BLUE, blue);
    }

    /**
     * Set the red player that should be used within the game.
     *
     * @param red The red player object.
     */
    public void setRedPlayer(AbstractPlayer red) {
        players.put(PlayerTurn.RED, red);
    }


    /**
     * Set the blue player that should be used within the game.
     *
     * @param blue The blue player object.
     */
    public void setBluePlayer(AbstractPlayer blue) {
        players.put(PlayerTurn.BLUE, blue);
    }

    /**
     * Get all the visible faces.
     *
     * @return The faces that are leaves of the DAG and the outer face.
     */
    public Set<TriangleFace> getTriangulatedFaces() {
        return triangulator.getTriangulatedFaces();
    }

    /**
     * Get the current Voronoi diagram.
     *
     * @return The Voronoi diagram instance.
     */
    public VoronoiDiagram getVoronoiDiagram() {
        return voronoiDiagram;
    }

    /**
     * Define the different players we have in the game, in this case by color.
     */
    public enum PlayerTurn {
        RED, BLUE;

        /**
         * Get the player that should have the next turn, given the current turn.
         *
         * @return The opposite color.
         */
        public PlayerTurn next() {
            return this == RED ? BLUE : RED;
        }
    }
}
