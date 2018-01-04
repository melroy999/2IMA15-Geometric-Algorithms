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

    // A list of a list of moves to do.
    private List<List<Move>> moves;

    // The current index of the sublist we are iterating over.
    private int sublistid = 0;

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
        moves = new ArrayList<>();
        moves.add(new ArrayList<>());
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
        // Create the list of moves
        createGrid(numPointsValue);

        // Iterate over the moves, making them.
        for(Move p : moves.get(sublistid)) {
            p.doMove();

            // Do a small sleep...
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check whether the ai player is done with his/her turns.
     *
     * @return True if done, false otherwise.
     */
    @Override
    public boolean isDone() {
        return false;
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

    /**
     * A class that manages the addition of moves.
     */
    private class Move {
        private final Point p;

        /**
         * Create a move.
         *
         * @param p The subject point.
         */
        public Move( Point p) {
            this.p = p;
        }

        /**
         * Execute the stored move.
         */
        public void doMove() {
            addPoint(p);
        }
    }
}
