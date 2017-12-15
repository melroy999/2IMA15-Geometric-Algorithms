package geo.engine;

import geo.controller.GameController;
import geo.gui.GUI;
import geo.player.AbstractPlayer;
import geo.player.HumanPlayer;
import geo.state.GameState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that manages all components of the game, such as the GUI, state and input/output, turn mechanic logic etc.
 */
public class GameEngine {
    // The instance of the engine.
    private static GameEngine engine;

    // A reference to the current playing board.
    private final GameState state;

    // The controller used to change the game state.
    private final GameController controller;

    // Reference to the GUI component.
    private final GUI gui;

    // The pool in which we run player turns and other cpu heavy tasks.
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    // A list of player types that we have.
    private final AbstractPlayer[] players;

    /**
     * The game engine is a singleton, we only want one instance.
     */
    private GameEngine() {
        // Create and initialize the different components of the framework.
        state = new GameState();
        controller = new GameController(state, this);

        // Create the player types we have.
        players = new AbstractPlayer[] {
            new HumanPlayer(controller)
        };

        // Create the gui.
        gui = GUI.createAndShow();
        gui.init(players);
    }

    /**
     * Get the engine instance, create it if it does not exist yet.
     *
     * @return The singleton game engine, created if it does not exist yet.
     */
    public static GameEngine getEngine() {
        if (engine == null) engine = new GameEngine();
        return engine;
    }

    /**
     * Give the turn to the specified player.
     *
     * @param player The player whose turn it is.
     */
    public void startPlayerTurn(AbstractPlayer player) {
        // For this, we have to start a new task on the pool.
        pool.submit(() -> player.turn(state));
    }
}
