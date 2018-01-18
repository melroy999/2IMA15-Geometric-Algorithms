package geo.player;

import geo.controller.GameController;
import geo.state.GameState;
import geo.store.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * An input interface for a player.
 */
public abstract class AbstractPlayer {
    // A reference to the engine, such that we can communicate our moves towards it.
    private final GameController controller;

    // The color of the player.
    public final GameState.PlayerTurn color;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param turn The turn this player should be active in.
     */
    public AbstractPlayer(GameController controller, GameState.PlayerTurn turn) {
        this.controller = controller;
        this.color = turn;
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
    protected final GameState.FaultStatus addPoint(Point p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<GameState.FaultStatus> runnable = new FutureTask<>(() -> controller.addPoint(p));
        SwingUtilities.invokeLater(runnable);
        try {
            return runnable.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        // If we reach this point, the addition of the point would have failed.
        return GameState.FaultStatus.Error;
    }

    /**
     * Allow the player to add multiple points to the playing field simultaniously.
     *
     * @param p The point the user wants to add.
     * @return Whether the insertion of the point was successful or not.
     */
    protected final List<GameState.FaultStatus> addPoints(Point[] p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<List<GameState.FaultStatus>> runnable = new FutureTask<>(() -> controller.addPoints(p));
        SwingUtilities.invokeLater(runnable);
        try {
            return runnable.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        // If we reach this point, the addition of the point would have failed.
        return Collections.singletonList(GameState.FaultStatus.Error);
    }

    /**
     * Remove the point.
     *
     * @param p The point to remove.
     * @return Whether the removal of the point was successful.
     */
    protected final boolean removePoint(Point p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<Boolean> runnable = new FutureTask<>(() -> controller.removePoint(p));
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
     * Allow the player to add a point to the playing field.
     *
     * @param p The point the user wants to add.
     * @return Whether the insertion of the point was successful or not.
     */
    protected final GameState.FaultStatus addPoint(Point2d p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<GameState.FaultStatus> runnable = new FutureTask<>(() -> controller.addPoint(p));
        SwingUtilities.invokeLater(runnable);
        try {
            return runnable.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        // If we reach this point, the addition of the point would have failed.
        return GameState.FaultStatus.Error;
    }

    /**
     * Allow the player to add multiple points to the playing field simultaniously.
     *
     * @param p The point the user wants to add.
     * @return Whether the insertion of the point was successful or not.
     */
    protected final List<GameState.FaultStatus> addPoints(Point2d[] p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<List<GameState.FaultStatus>> runnable = new FutureTask<>(() -> controller.addPoints(p));
        SwingUtilities.invokeLater(runnable);
        try {
            return runnable.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        // If we reach this point, the addition of the point would have failed.
        return Collections.singletonList(GameState.FaultStatus.Error);
    }

    /**
     * Remove the point.
     *
     * @param p The point to remove.
     * @return Whether the removal of the point was successful.
     */
    protected final boolean removePoint(Point2d p) {
        // Run the addition of a point on the event thread.
        RunnableFuture<Boolean> runnable = new FutureTask<>(() -> controller.removePoint(p));
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
