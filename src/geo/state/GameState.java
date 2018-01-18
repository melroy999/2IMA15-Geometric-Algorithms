package geo.state;

import geo.controller.GameController;
import geo.delaunay.TriangulationMesh;
import geo.player.AbstractPlayer;
import geo.store.halfedge.TriangleFace;
import geo.store.halfedge.Vertex;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    // The two players that are playing the game.
    private HashMap<PlayerTurn, AbstractPlayer> players = new HashMap<>();

    // The player that currently has the turn.
    private PlayerTurn currentPlayerTurn;

    // The current turn number, based on the amount of turns the red player has had.
    private int currentTurn;

    // The points put down by the blue and red players.
    private final List<Vertex<TriangleFace>> bluePoints = new ArrayList<>();
    private final List<Vertex<TriangleFace>> redPoints = new ArrayList<>();

    // The delaunay mesh that is currently being drawn on.
    private TriangulationMesh mesh;

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
        controller.setPredicates(this::addPoint, this::removePoint, this::reset, this::addPoints);
    }

    /**
     * Take the union of two lists.
     *
     * @param lists The lists we want to take the union of.
     * @param <T> The type of objects in the list.
     * @return The combination of the two lists.
     */
    private static <T> List<T> union(List<T>... lists) {
        Set<T> set = new LinkedHashSet<>();

        for(List<T> list : lists) set.addAll(list);

        return new ArrayList<>(set);
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
     * Add a point to the state.
     *
     * @param p The point to add to the state.
     * @return Whether the insertion of the point was successful or not.
     */
    private FaultStatus addPoint(Point p) {
        // First, convert to our own vertex type.
        Vertex<TriangleFace> vertex = new Vertex<>(p.x, p.y, currentPlayerTurn);

        // If the point already exists, do nothing.
        if(checkPointExistence(vertex)) return FaultStatus.PointExists;

        // Blue is only allowed to have n-1 points.
        if(currentPlayerTurn == PlayerTurn.BLUE && redPoints.size() <= bluePoints.size() + 1) {
            return FaultStatus.TooManyPoints;
        }

        // Get the list of points, and reconstruct the triangulation/Voronoi diagram.
        List<Vertex<TriangleFace>> vertices = union(redPoints, bluePoints, Collections.singletonList(vertex));

        // Recalculate the mesh.
        calculate(vertices);

        // If we managed to reach this point, we can conclude that the addition of the points was successful.
        if(currentPlayerTurn == PlayerTurn.RED) {
            redPoints.add(vertex);
        } else {
            bluePoints.add(vertex);
        }

        // We had no errors, so return fault status none.
        return FaultStatus.None;
    }

    /**
     * Add the points to the state.
     *
     * @param points The points to add to the state.
     * @return Whether the insertion of all points was successful or not.
     */
    private List<FaultStatus> addPoints(Point[] points) {
        // The collection of fault codes we want to return...
        List<FaultStatus> status = new ArrayList<>();

        // First, convert all the points to our own vertex type.
        List<Vertex<TriangleFace>> vertices = Arrays.stream(points).map(p -> new Vertex<TriangleFace>(p.x, p.y, currentPlayerTurn)).collect(Collectors.toList());

        // Filter out the points that already exist.
        vertices = vertices.stream().filter(e -> !checkPointExistence(e)).collect(Collectors.toList());
        if(vertices.size() != points.length) status.add(FaultStatus.PointExists);

        // Blue is only allowed to have n-1 points.
        if(currentPlayerTurn == PlayerTurn.BLUE && redPoints.size() <= bluePoints.size() + points.length) {
            // We cannot place all points. take a subset and add those we can.
            status.add(FaultStatus.TooManyPoints);
            vertices = vertices.subList(0, redPoints.size() - bluePoints.size() - 1);
        }

        // Get the full list of points, and reconstruct the triangulation/Voronoi diagram.
        vertices = union(redPoints, bluePoints, vertices);

        // Recalculate the mesh.
        calculate(vertices);

        // Only after all previous insertions pass, add the point to the list of points.
        for(Vertex<TriangleFace> vertex : vertices) {
            if(currentPlayerTurn == PlayerTurn.RED) {
                redPoints.add(vertex);
            } else {
                bluePoints.add(vertex);
            }
        }

        // Now, return None if status is empty, status otherwise.
        return status.isEmpty() ? Collections.singletonList(FaultStatus.None) : status;
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
        if(hasMatch) calculate(union(redPoints, bluePoints));
        return hasMatch;
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
     * Reconstruct the triangulation and the Voronoi diagram.
     *
     * @param vertices The list of points to use in the triangulation.
     */
    private void calculate(List<Vertex<TriangleFace>> vertices) {
        if(!vertices.isEmpty()) {
            // Create a new mesh to work with, and pass the list of points to find the largest point.
            mesh = new TriangulationMesh(vertices);

            // The list of all points, which are shuffled with the use of a random seed.
//            Collections.shuffle(vertices, random);
        }

        // Insert all already known points, and the new point, in random order.
        for(Vertex<TriangleFace> v : vertices) {
            mesh.insertVertex(v);
        }
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

        // Reset the mesh.
        mesh = null;

        // Reset the random seed.
        random = new Random(8988178178129387065L);
    }

    /**
     * Draw the edges in the triangulation mesh.
     *
     * @param g The graphics object to draw objects with.
     */
    public void drawTriangulationMesh(Graphics2D g) {
        if(mesh != null) mesh.drawTriangulationMesh(g);
    }

    /**
     * Draw the points that have been placed on the board.
     *
     * @param g The graphics object to draw objects with.
     */
    public void drawVertices(Graphics2D g) {
        // Paint all the points.
        for(Vertex v : union(redPoints, bluePoints)) {
            if(v != mesh.getP0()) {
                v.drawVertex(g);
            } else {
                v.drawVertex(g, Color.GREEN);
            }
        }
    }

    public int getNumberOfRedPoints() {
        return redPoints.size();
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

    public enum FaultStatus {
        PointExists, TooManyPoints, Error, None
    }
}
