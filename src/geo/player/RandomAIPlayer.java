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

    // The current index of the sublist we are iterating over.
    private int sublistid = 0;

    private boolean duplicate;

    private JPanel rootPanel;
    private JTextField seed;
    private JTextField numPoints;

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
        int i = 0;
        // While there are not enough points, generate new random coordinates and try to add them
        while (i < numPoints) {
            int x = generator.nextInt(100/*Limit of x playing field, get width of panel*/);
            int y = generator.nextInt(100/*Limit of y playing field, get height of panel*/);
            System.out.println(x +" "+ y);
            // Reset duplicate value and j value
            duplicate = false;
            int j = 0;
            // Check if the point already exists in moves
            for(Move p : moves.get(j)){
                // If x and y coordinates exists in list of moves, ignore and generate new x and y
                if (p.p.x == x && p.p.y == y) {
                    duplicate = true;
                }
                j++;
            }
            // If the random point is not duplicate, add the point to the list of moves and increase list size.
            if (!duplicate) {
                moves.get(moves.size() - 1).add(new Move(new Point(x, y)));
                i++;
            }
        }
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
        // For test purposes, remove later
        Move pointx = moves.get(0).get(0);

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

        // Increment the sublist id.
        sublistid++;
    }

    /**
     * Check whether the ai player is done with his/her turns.
     *
     * @return True if done, false otherwise.
     */
    @Override
    public boolean isDone() {
        if (sublistid == 0) {
            return false;
        } else {
            return !(sublistid < moves.size());
        }
    }

    /**
     * Reset the state of the AI so that we can use it again.
     */
    @Override
    public void reset() {
        sublistid = 0;
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
