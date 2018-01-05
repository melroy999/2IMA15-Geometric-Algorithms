package geo.player;

import geo.controller.GameController;
import geo.state.GameState;
import geo.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RandomAIPlayer extends AIPlayer {

    private boolean canPlace;

    private JPanel rootPanel;
    private JTextField seed;
    private JTextField numPoints;

    private int turn = 0;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player The human player we should follow up with when the AI is done.
     * @param turn The turn this player should be active in.
     */
    public RandomAIPlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn){
        super(controller, player, turn);
    }

    /**
     * Create the list of random moves for the AI to take.
     *
     * @param seed The seed with which to generate the random numbers.
     * @param numPoints The number of points to be placed.
     */
    private void generateRandomMoves(long seed, int numPoints){
        Random generator = new Random(seed);
        int i = 0;
        // While there are not enough points, generate new random coordinates and try to add them
        while (i < numPoints) {
            int x = (int) Math.floor(generator.nextDouble()*GUI.createAndShow().getGamePanelDimensions().width);
            int y = (int) Math.floor(generator.nextDouble()*GUI.createAndShow().getGamePanelDimensions().height);
            System.out.println(x +" "+ y);
            // Check if the point can be placed
            canPlace = addPoint(new Point(x, y));
            System.out.println(canPlace);
            // If the random point can be placed
            if (canPlace) {
                i++;
            }
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
        // Get the values for seed and numPoints
        long seedValue = Long.parseLong(seed.getText());
        int numPointsValue = Integer.parseInt(numPoints.getText());
        // Create the list of moves to make
        generateRandomMoves(seedValue, numPointsValue);
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
