package geo.controller;

import geo.engine.GameEngine;
import geo.state.GameState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

/**
 * A class in which all user related input is handled.
 */
public class GameController {
    // The game state to apply the actions to.
    private final GameState state;

    // The engine to correspond messages with.
    private final GameEngine engine;

    /**
     * Create a game controller, which will execute its actions on the given game state.
     *
     * @param engine The game engine to communicate changes to.
     * @param state The game state to apply the changes to.
     */
    public GameController(GameEngine engine, GameState state) {
        this.state = state;
        this.engine = engine;
    }

    /**
     * Add the given point to the game state.
     *
     * @param p The point to add to the game state.
     * @return Whether the insertion of the point was successful or not.
     */
    public boolean addPoint(Point p) {
        throw new NotImplementedException();
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
