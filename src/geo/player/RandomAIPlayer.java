package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAIPlayer extends AIPlayer {
    // A list of a list of moves to do.
    private List<List<Move>> moves;

    // The playing board panel.
    private JPanel boardPanel;

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
        // Create the list of moves.
        moves = new ArrayList<>();
        moves.add(new ArrayList<>());
        Random generator = new Random(seed);
        //TODO: Fill list with numPoints amount of points that are in the playing field.
        for (int i = 0; i < numPoints; i++){
            // Generate x and y coords
            int x = generator.nextInt(boardPanel.getWidth()/*Limit of x playing field, get width of panel: panel.element.getWidth()*/);
            int y = generator.nextInt(boardPanel.getHeight()/*Limit of y playing field, get height of panel: panel.element.getHeight()*/);
            moves.get(moves.size() - 1).add(new Move(false, new Point(x, y)));
        }
    }


    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state) {
        System.out.println("Do stuff");
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
        return boardPanel;
    }




    /**
     * A class that manages the removal or addition of moves.
     */
    private class Move {
        private final boolean remove;
        private final Point p;

        /**
         * Create a move, which is either an insert or remove move.
         *
         * @param remove Whether we want to add or remove the given point.
         * @param p The subject point.
         */
        public Move(boolean remove, Point p) {
            this.remove = remove;
            this.p = p;
        }

        /**
         * Execute the stored move.
         */
        public void doMove() {
            if(remove) {
                removePoint(p);
            } else {
                addPoint(p);
            }
        }
    }
}