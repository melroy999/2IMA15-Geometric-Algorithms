package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * The human player, which is controlled by the buttons in the GUI.
 */
public class HumanPlayer extends AbstractPlayer implements ActionListener {
    // Human players have full control over the controller.
    private final GameController controller;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param turn The turn this player should be active in.
     */
    public HumanPlayer(GameController controller, GameState.PlayerTurn turn) {
        super(controller, turn);

        // Set the controller to give the human player more functionality.
        this.controller = controller;
    }

    /**
     * Execute the turn of the player, given a copy of the game state.
     *
     * @param state A (read-only) copy of the current state of the game.
     */
    @Override
    public void turn(GameState state) {
        // A human player does nothing in the turn! We will just have to wait until the player ends its own turn.
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The action event that occurred.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Depending on where the event comes from...
        switch (e.getActionCommand()) {
            case "Next turn":
                endTurn();
                return;
            case "Reset board":
                resetGame();
                return;
            case "Start":
                startGame();
                return;
            case "Start trials":
                startTrials();
                return;
        }

        // Print any actions we have not handled.
        System.out.println("Unhandled action: " + e.getActionCommand());
    }

    /**
     * Process the user's mouse clicks.
     *
     * @param e The mouse click event.
     */
    public void userMouseClickEvent(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            // Here, we avoid using updatePlayerCounters, since we are running in the EDT thread already for human players!
            addPointSynchronously(e.getPoint());
        } else if(e.getButton() == MouseEvent.BUTTON3){
            // Same here, but we remove instead of add.
            removePointSynchronously(e.getPoint());
        }
    }

    /**
     * Do a direct non-threaded call to the add point function.
     *
     * @param p The point the user wants to add.
     */
    private void addPointSynchronously(Point p) {
        controller.addPoint(p);
    }

    /**
     * Do a direct non-threaded call to the remove point function.
     *
     * @param p The point the user wants to remove.
     */
    private void removePointSynchronously(Point p) {
        controller.removePoint(p);
    }

    /**
     * Start the game.
     */
    public void startGame() {
        SwingUtilities.invokeLater(controller::startGame);
    }

    /**
     * Start the game.
     */
    public void startTrials() {
        SwingUtilities.invokeLater(controller::startTrials);
    }

    /**
     * Initiate a game reset.
     */
    public void resetGame() {
        SwingUtilities.invokeLater(controller::resetGame);
    }
}
