package geo.engine;

import geo.gui.ApplicationWindow;
import geo.log.GeoLogger;
import geo.state.GameState;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

/**
 * A class that manages the controls of the game.
 */
public class GameEngine {
    // The game engine has access to the game state.
    private final GameState state;

    // A reference to the game window, such that we can change display values when necessary.
    private final ApplicationWindow gui;

    // Logging the actions of the player.
    private static final Logger log = GeoLogger.getLogger(GameEngine.class.getName());

    /**
     * Create a new game engine, and give it access to the GUI.
     *
     * @param applicationWindow The GUI of the game.
     */
    public GameEngine(ApplicationWindow applicationWindow) {
        this.gui = applicationWindow;

        // Create a new game state.
        state = new GameState();

        // Log the creation of the engine.
        log.info("Successfully created the game engine.");
    }

    /**
     * Process the click of the user in the game panel.
     *
     * @param e The event associated with the click.
     */
    public void handleUserClickAction(MouseEvent e) {
        // Depending on which mouse button was pressed, execute an action.
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                // Register non-registered mouse clicks.
                log.info(String.format("Registered left mouse click on position %s.", e.getPoint().toString()));

                // On left mouse click, place a point.
                if(!state.addPoint(e.getPoint())) {
                    // Log the failure.
                    log.warning(String.format("Failed to add the point %s.", e.getPoint().toString()));

                    // If not added, return as we do not want to change the state.
                    return;
                }

                // Log the successful addition.
                log.info(String.format("Successfully added the point %s.", e.getPoint().toString()));

                break;
            default:
                // Register non-registered mouse clicks.
                log.info(String.format("The mouse click %s was not registered.", e.toString()));

                // Otherwise, return as we did not change anything.
                return;
        }

        // Repaint the content panel, as we made changes.
        gui.getGamePanel().repaint();
    }

    /**
     * Reset the game state.
     */
    public void reset() {
        // Write status to log.
        log.info("Resetting the game state.");

        // Reset the game state.
        state.reset();

        // Repaint the content panel, as we made changes.
        gui.getGamePanel().repaint();
    }

    /**
     * Switch the player's turns.
     */
    public void switchPlayer() {
        // Write status to log.
        log.info("Switching player turns.");

        // Give the turn to the other player.
        state.switchPlayer();
    }

    /**
     * Get the game state.
     *
     * @return The current game state.
     */
    public GameState getState() {
         return state;
    }

    /**
     * Update the area label for the two players.
     *
     * @param red The area of the red player.
     * @param blue The area of the blue player.
     */
    public void updateArea(double red, double blue) {
        // Update the values in the GUI.
        double sum = red + blue;

        // Calculate the percentages, and update.
        gui.setRedPlayerAreaLabel(String.format("%d", (int) (100 * (red / sum))) + "%");
        gui.setBluePlayerAreaLabel(String.format("%d", (int) (100 * (blue / sum))) + "%");
    }

    /**
     * Rebalance the triangulation mesh.
     */
    public void rebalance() {
        log.info("Rebalancing triangulation mesh.");
        state.rebalanceTriangulator();

        // Repaint the content panel, as we made changes.
        gui.getGamePanel().repaint();
    }
}
