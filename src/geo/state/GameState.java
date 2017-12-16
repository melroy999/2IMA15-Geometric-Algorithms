package geo.state;

import geo.controller.GameController;
import geo.player.AbstractPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private final List<Point> bluePoints = new ArrayList<>();
    private final List<Point> redPoints = new ArrayList<>();

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
        controller.setPredicates(this::addPoint, this::reset);
    }

    /**
     * Add a point to the state.
     *
     * @param p The point to add to the state.
     * @return Whether the insertion of the point was successful or not.
     */
    private boolean addPoint(Point p) {
        if(currentPlayerTurn == PlayerTurn.RED) {
            redPoints.add(p);
        } else {
            if(getNumberOfBluePoints() + 1 < getNumberOfRedPoints()) {
                bluePoints.add(p);
            } else {
                // We are not allowed to add more points than the red player, thus refuse.
                return false;
            }
        }
        return true;
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
    public List<Point> getBluePoints() {
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
    public List<Point> getRedPoints() {
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
     * Reset the game state.
     */
    private void reset() {
        // Reset the turn system.
        currentPlayerTurn = PlayerTurn.RED;
        currentTurn = 0;

        // Reset all the stored data.
        bluePoints.clear();
        redPoints.clear();
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
