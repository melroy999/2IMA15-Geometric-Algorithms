package geo.engine;

import geo.controller.GameController;
import geo.gui.GUI;

import geo.player.AIPlayer;
import geo.player.AbstractPlayer;
import geo.player.HumanPlayer;
import geo.player.ImportFilePlayer;
import geo.state.GameState;
import geo.voronoi.VoronoiDiagram;

import java.awt.*;
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
    private final ExecutorService pool = Executors.newCachedThreadPool();

    /**
     * The game engine is a singleton, we only want one instance.
     */
    private GameEngine() {
        // Create and initialize the different components of the framework.
        state = new GameState();
        controller = new GameController(this, state);

        // Create the player types we have for player 1.
        HumanPlayer humanRed = new HumanPlayer(controller, GameState.PlayerTurn.RED);
        AbstractPlayer[] players = new AbstractPlayer[]{
                humanRed,
                new ImportFilePlayer(controller, humanRed, GameState.PlayerTurn.RED)
        };

        // Do the same for player 2.
        HumanPlayer humanBlue = new HumanPlayer(controller, GameState.PlayerTurn.BLUE);
        AbstractPlayer[] players2 = new AbstractPlayer[] {
                humanBlue,
                new ImportFilePlayer(controller, humanBlue, GameState.PlayerTurn.BLUE)
        };

        // Create the gui.
        gui = GUI.createAndShow();
        gui.init(players, players2, humanRed);
        gui.setState(state);
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
     * Start the game.
     */
    public void startGame() {
        // Set the initial players.
        state.setPlayers(gui.getCurrentRedPlayer(), gui.getCurrentBluePlayer());

        // Reset the ai, if applicable.
        if(gui.getCurrentRedPlayer() instanceof AIPlayer) ((AIPlayer) gui.getCurrentRedPlayer()).reset();
        if(gui.getCurrentBluePlayer() instanceof AIPlayer) ((AIPlayer) gui.getCurrentBluePlayer()).reset();

        // Disable the start button.
        gui.changeStartButtonEnabled(false);

        // Notify that the current player can start his turn.
        startPlayerTurn(state.getCurrentPlayer());
    }

    /**
     * Give the turn to the specified player.
     *
     * @param player The player whose turn it is.
     */
    public void startPlayerTurn(AbstractPlayer player) {
        // Notify the GUI that the current player label should be changed.
        gui.changeCurrentPlayerLabel(state.getCurrentPlayerTurn());

        // Depending on whether this is a player's turn, disable the next turn and reset buttons.
        gui.changeNextButtonEnabled(player instanceof HumanPlayer);
        gui.changeResetButtonEnabled(player instanceof HumanPlayer);

        // For this, we have to start a new task on the pool.
        pool.execute(() -> player.turn(state));
    }

    /**
     * End the current players turn.
     */
    public void endPlayerTurn() {
        // End the current players turn, which means that we should start the turn of the other player.
        state.changeTurn();

        // Get the current player.
        AbstractPlayer current = state.getCurrentPlayer();

        // If the player is an AI player, and it tells us it is done, convert it to a human player.
        if((current instanceof AIPlayer && ((AIPlayer) current).isDone())) {
            if(state.getCurrentPlayerTurn() == GameState.PlayerTurn.RED) {
                state.setRedPlayer(((AIPlayer) current).getPlayer());
            } else {
                state.setBluePlayer(((AIPlayer) current).getPlayer());
            }
            current = state.getCurrentPlayer();
        }

        // Check whether we have a limited amount of turns. If not, just change the turn.
        // We cannot limit an AI.
        if(gui.getMaximumNumberOfTurns() == -1 && !(current instanceof AIPlayer)) {
            // Initiate the turn change.
            startPlayerTurn(current);
        } else {
            // Otherwise, only change the turn if we are below the maximum amount of turns.
            if(state.getCurrentTurn() < gui.getMaximumNumberOfTurns()
                    || (current instanceof AIPlayer && !((AIPlayer) current).isDone())) {
                // Initiate the turn change.
                startPlayerTurn(current);
            } else {
                // Disable the next button, as the game is over.
                gui.changeNextButtonEnabled(false);
                gui.changeResetButtonEnabled(true);
            }
        }
    }

    /**
     * End the game.
     */
    public void resetGame() {
        // Enable the start button, disable the other buttons.
        gui.changeStartButtonEnabled(true);
        gui.changeNextButtonEnabled(false);
        gui.changeResetButtonEnabled(false);
        gui.updateGameStateCounters(0, 0, 0, 0);

        // Set the player label to be empty.
        gui.changeCurrentPlayerLabel(null);
        gui.redrawGamePanel();
    }

    /**
     * Get the color of the player that currently gets to add points.
     *
     * @return The color of the player that currently is active.
     */
    public GameState.PlayerTurn getPlayerTurn() {
        return state.getCurrentPlayerTurn();
    }

    /**
     * Update the count and area displays in the GUI of the two players.
     */
    public void updatePlayerCounters() {
        // Update the status, and ask for a game panel redraw.
        VoronoiDiagram d = state.getVoronoiDiagram();

        // First, calculate the area in percentages.
        Dimension dim = gui.getGamePanelDimensions();
        int t = dim.width * dim.height;
        int redArea = (int) Math.round(100 * (d.getAreaRed() / t));
        int blueArea = (int) Math.round(100 * (d.getAreaBlue() / t));

        gui.updateGameStateCounters(state.getNumberOfRedPoints(), state.getNumberOfBluePoints(), redArea, blueArea);
        gui.redrawGamePanel();
    }
}
