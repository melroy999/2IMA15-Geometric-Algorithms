package geo.player;

import geo.controller.GameController;
import geo.engine.GameEngine;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * An input interface for a player.
 */
public abstract class AbstractPlayer {
    // A reference to the engine, such that we can communicate our moves towards it.
    private final GameController controller;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     */
    public AbstractPlayer(GameController controller) {
        this.controller = controller;
    }

    /**
     * Execute the turn of the player, given a copy of the game state.
     *
     * @param state A (read-only) copy of the current state of the game.
     */
    public abstract void turn(GameState state);

    /**
     * Allow the player to add a point to the playing field.
     *
     * @param p The point the user wants to add.
     * @return Whether the insertion of the point was successful or not.
     */
    protected final boolean addPoint(Point p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<Boolean> runnable = new FutureTask<>(() -> controller.addPoint(p));
        SwingUtilities.invokeLater(runnable);
        try {
            return runnable.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        // If we reach this point, the addition of the point would have failed.
        return false;
    }

    /**
     * Notify the controller that the player wishes to end its turn.
     */
    protected final void endTurn() {
        SwingUtilities.invokeLater(controller::endTurn);
    }

    /**
     * Get the name of the player as a string.
     *
     * @return The simple name of the class.
     */
    @Override
    public final String toString() {
        return this.getClass().getSimpleName();
    }
}
