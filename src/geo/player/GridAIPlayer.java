package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridAIPlayer extends AIPlayer  {
    private JPanel rootPanel;
    private JTextField numPoints;

    private int turn = 0;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player     The human player we should follow up with when the AI is done.
     * @param turn       The turn this player should be active in.
     */
    public GridAIPlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);
    }

    /**
     * Create the list of moves in a grid pattern for the AI to take.
     *
     * @param numPoints The number of points to be placed.
     */
    public void createGrid(int numPoints){
        // TODO: Create a grid
        // Use the square root of the number of points rounded up as the amount of rows
        int rows = (int) Math.ceil((Math.sqrt((double) numPoints)));
        //
        for (int i = 0; i < rows; i++){

        }
        turn++;
    }

    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state) {
        // Get the value for numPoints
        int numPointsValue = Integer.parseInt(numPoints.getText());
        // Create the moves and do them
        createGrid(numPointsValue);
    }

    /**
     * Check whether the ai player is done with his/her turns.
     *
     * @return True if done, false otherwise.
     */
    @Override
    public boolean isDone() {
        return (turn > 0);
    }

    /**
     * Reset the state of the AI so that we can use it again.
     */
    @Override
    public void reset() {
        turn = 0;
    }

    /**
     * Get a panel containing the controls for this specific AI player.
     *
     * @return A JPanel which contains all required components to control the AI.
     */
    @Override
    public JPanel getPanel() {
        return rootPanel;
    }

}
