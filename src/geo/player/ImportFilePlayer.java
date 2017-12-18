package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportFilePlayer extends AIPlayer {
    // A list of a list of moves to do.
    private List<List<Point>> moves;

    // The current index of the sublist we are iterating over.
    private int sublistid = 0;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player The human player we should follow up with when the AI is done.
     * @param turn The turn this player should be active in.
     */
    public ImportFilePlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);
    }

    /**
     * Set the file reader for this player.
     *
     * @param reader The file to read the input from.
     */
    public void setReader(FileReader reader) {
        // Create the list of moves.
        moves = new ArrayList<>();
        moves.add(new ArrayList<>());

        // Start with importing the lines in the file to determine which moves we should take.
        try(BufferedReader readerer = new BufferedReader(reader)) {
            String line;
            while((line = readerer.readLine()) != null) {
                // Check if the line is empty.
                if(line.equals("")) {
                    // If empty, we want to do the next turn. So add new arraylist.
                    moves.add(new ArrayList<>());
                } else {
                    // Otherwise, we can add a point. Parse it.
                    String[] values = line.replace("java.awt.Point[x=", "")
                            .replace("]", "")
                            .replace("y=", "").split(",");

                    moves.get(moves.size() - 1).add(new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the last entry if it is empty.
        if(moves.get(moves.size() - 1).isEmpty()) {
            moves.remove(moves.size() - 1);
        }
    }

    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state) {
        // Iterate over the moves, making them.
        for(Point p : moves.get(sublistid)) {
            addPoint(p);

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
        return !(sublistid < moves.size());
    }

    /**
     * Reset the state of the AI so that we can use it again.
     */
    @Override
    public void reset() {
        sublistid = 0;
    }
}
