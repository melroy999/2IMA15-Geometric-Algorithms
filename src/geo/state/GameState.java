package geo.state;

import geo.controller.GameController;
import geo.player.AbstractPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameState {
    // The two players that are playing the game.
    private HashMap<PlayerTurn, AbstractPlayer> players = new HashMap<>();

    // The player that currently has the turn.
    private PlayerTurn currentPlayerTurn;

    // The current turn number, based on the amount of turns the red player has had.
    private int currentTurn;

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
        return FaultStatus.Error;
    }

    /**
     * Add the points to the state.
     *
     * @param points The points to add to the state.
     * @return Whether the insertion of all points was successful or not.
     */
    private List<FaultStatus> addPoints(Point[] points) {
        return null;
    }

    /**
     * Remove a point from the game state.
     *
     * @param p The point to remove.
     * @return Whether the point was removed successfully or not.
     */
    private boolean removePoint(Point p) {
        return false;
    }

    /**
     * Reset the game state.
     */
    private void reset() {
        // Reset the turn system.
        currentPlayerTurn = PlayerTurn.RED;
        currentTurn = 0;
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
