package geo.controller;

import geo.engine.GameEngine;
import geo.state.GameState;

import java.awt.*;
import java.util.function.Predicate;

/**
 * A class in which all input is handled.
 */
public class GameController {
    // The game state to apply the actions to.
    private final GameState state;

    // The engine to correspond messages with.
    private final GameEngine engine;

    // Predicates used during the communication with the game state.
    private Predicate<Point> addPoint;

    /**
     * Create a game controller, which will execute its actions on the given game state.
     *
     * @param engine The game engine to communicate changes to.
     * @param state The game state to apply the changes to.
     */
    public GameController(GameEngine engine, GameState state) {
        this.state = state;
        this.engine = engine;

        // Ask the state for predicates to access private methods.
        this.state.setPredicates(this);
    }

    /**
     * Set the predicates used to access the gamestate, to provide immutability in the player objects.
     *
     * @param addPoint The predicate that adds points to the game state.
     */
    public final void setPredicates(Predicate<Point> addPoint) {
        this.addPoint = addPoint;
    }

    /**
     * Add the given point to the game state.
     *
     * @param p The point to add to the game state.
     * @return Whether the insertion of the point was successful or not.
     */
    public boolean addPoint(Point p) {
        return addPoint.test(p);
    }

    /**
     * End the turn of the player.
     */
    public void endTurn() {
        engine.endPlayerTurn();
    }

    /**
     * Start the game.
     */
    public void startGame() {
        engine.startGame();
    }

    /**
     * Reset the board.
     */
    public void resetGame() {
        engine.resetGame();
    }
}
