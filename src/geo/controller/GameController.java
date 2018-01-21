package geo.controller;

import geo.engine.GameEngine;
import geo.state.GameState;
import geo.store.math.Point2d;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A class in which all input is handled.
 */
public class GameController {
    // The engine to correspond messages with.
    private final GameEngine engine;

    // Predicates used during the communication with the game state.
    private Function<Point, GameState.FaultStatus> addPoint;
    private Function<Point[], List<GameState.FaultStatus>> addPoints;
    private Predicate<Point> removePoint;
    private Function<Point2d, GameState.FaultStatus> addDoublePoint;
    private Function<Point2d[], List<GameState.FaultStatus>> addDoublePoints;
    private Predicate<Point2d> removeDoublePoint;
    private Runnable resetGame;

    // The two files in which we will log the player moves.
//    private PrintWriter redWriter;
//    private PrintWriter blueWriter;

    /**
     * Create a game controller, which will execute its actions on the given game state.
     *
     * @param engine The game engine to communicate changes to.
     * @param state The game state to apply the changes to.
     */
    public GameController(GameEngine engine, GameState state) {
        this.engine = engine;

        // Ask the state for predicates to access private methods.
        state.setPredicates(this);
    }

    /**
     * Set the predicates used to access the gamestate, to provide immutability in the player objects.
     *
     * @param addPoint The predicate that adds points to the game state.
     */
    public final void setPredicates(Function<Point, GameState.FaultStatus> addPoint, Predicate<Point> removePoint, Runnable resetGame, Function<Point[], List<GameState.FaultStatus>> addPoints, Function<Point2d, GameState.FaultStatus> addDoublePoint, Predicate<Point2d> removeDoublePoint, Function<Point2d[], List<GameState.FaultStatus>> addDoublePoints) {
        this.addPoint = addPoint;
        this.removePoint = removePoint;
        this.resetGame = resetGame;
        this.addPoints = addPoints;
        this.addDoublePoint = addDoublePoint;
        this.addDoublePoints = addDoublePoints;
        this.removeDoublePoint = removeDoublePoint;
    }

    /**
     * Add the given point to the game state.
     *
     * @param p The point to add to the game state.
     * @return Whether the insertion of the point was successful or not.
     */
    public GameState.FaultStatus addPoint(Point p) {
        // Write the moves of the player to the log.
//        if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//            redWriter.println(p.toString());
//        } else {
//            blueWriter.println(p.toString());
//        }

        GameState.FaultStatus status = addPoint.apply(p);

        if(status == GameState.FaultStatus.None) {
            engine.updatePlayerCounters();
        }

        return status;
    }

    /**
     * Add the given points to the game state.
     *
     * @param points The points to add to the game state.
     * @return Whether the insertion of all points was successful or not.
     */
    public List<GameState.FaultStatus> addPoints(Point[] points) {
        // Write the moves of the player to the log.
//        for(Point p : points) {
//            if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//                redWriter.println(p.toString());
//            } else {
//                blueWriter.println(p.toString());
//            }
//        }

        List<GameState.FaultStatus> success = addPoints.apply(points);

        // Since we add multiple points, we probably have added single points anyhow.
        engine.updatePlayerCounters();

        return success;
    }

    /**
     * Remove the given point.
     *
     * @param p The point to remove.
     * @return Whether the point was removed successfully.
     */
    public boolean removePoint(Point p) {
        // Write the moves of the player to the log.
//        if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//            redWriter.println("-" + p.toString());
//        } else {
//            blueWriter.println("-" + p.toString());
//        }

        if(removePoint.test(p)) {
            engine.updatePlayerCounters();
            return true;
        }
        return false;
    }

    /**
     * Add the given point to the game state.
     *
     * @param p The point to add to the game state.
     * @return Whether the insertion of the point was successful or not.
     */
    public GameState.FaultStatus addPoint(Point2d p) {
        // Write the moves of the player to the log.
//        if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//            redWriter.println(p.toString());
//        } else {
//            blueWriter.println(p.toString());
//        }

        GameState.FaultStatus status = addDoublePoint.apply(p);

        if(status == GameState.FaultStatus.None) {
            engine.updatePlayerCounters();
        }

        return status;
    }

    /**
     * Add the given points to the game state.
     *
     * @param points The points to add to the game state.
     * @return Whether the insertion of all points was successful or not.
     */
    public List<GameState.FaultStatus> addPoints(Point2d[] points) {
        // Write the moves of the player to the log.
//        for(Point2d p : points) {
//            if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//                redWriter.println(p.toString());
//            } else {
//                blueWriter.println(p.toString());
//            }
//        }

        List<GameState.FaultStatus> success = addDoublePoints.apply(points);

        // Since we add multiple points, we probably have added single points anyhow.
        engine.updatePlayerCounters();

        return success;
    }

    /**
     * Remove the given point.
     *
     * @param p The point to remove.
     * @return Whether the point was removed successfully.
     */
    public boolean removePoint(Point2d p) {
        // Write the moves of the player to the log.
//        if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//            redWriter.println("-" + p.toString());
//        } else {
//            blueWriter.println("-" + p.toString());
//        }

        if(removeDoublePoint.test(p)) {
            engine.updatePlayerCounters();
            return true;
        }
        return false;
    }

    /**
     * End the turn of the player.
     */
    public void endTurn() {
        // Write an empty line in the log of the player that is currently active.
//        if(engine.getPlayerTurn() == GameState.PlayerTurn.RED) {
//            redWriter.println();
//        } else {
//            blueWriter.println();
//        }

        engine.endPlayerTurn();
    }

    /**
     * Start the game.
     */
    public void startGame() {
        // On the start of the game, create two new writers.
        // First make the directory in which we will store results.
//        File directory = new File("runs");
//        if (! directory.exists()) directory.mkdir();
//
//        // Determine the unique file quantifier.
//        String fileQuantifier = "" + System.currentTimeMillis();

        // Create the file writers.
//        try {
//            redWriter = new PrintWriter(new FileWriter("runs/" + fileQuantifier + "-red.txt"));
//            blueWriter = new PrintWriter(new FileWriter("runs/" + fileQuantifier + "-blue.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        engine.startGame();
    }

    /**
     * Reset the board.
     */
    public void resetGame() {
        // Close the file writers.
//        redWriter.close();
//        blueWriter.close();

        // Ask the engine to reset the GUI related components.
        engine.resetGame();

        // Reset the game state.
        resetGame.run();
    }

    /**
     * Start the trials.
     */
    public void startTrials() {
        engine.startTrials();
    }
}
