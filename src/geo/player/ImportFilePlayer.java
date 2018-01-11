package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportFilePlayer extends AIPlayer {
    // A list of a list of moves to do.
    private List<List<Move>> moves;

    // The current index of the sublist we are iterating over.
    private int sublistid = 0;
    private JPanel rootPanel;
    private JTextField selectedFileField;
    private JButton selectFileButton;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player The human player we should follow up with when the AI is done.
     * @param turn The turn this player should be active in.
     */
    public ImportFilePlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);

        selectFileButton.addActionListener(e -> {
            File directory = new File("runs");
            if (!directory.exists()) directory.mkdir();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(directory);
            int result = fileChooser.showOpenDialog(rootPanel);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFileField.setText(selectedFile.getAbsolutePath());
                try {
                    this.setReader(new FileReader(selectedFile));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
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
                    // Check if it is a removal.
                    boolean remove = line.startsWith("-");

                    // Otherwise, we can add a point. Parse it.
                    String[] values = line.replace("-", "")
                            .replace("java.awt.Point[x=", "")
                            .replace("]", "")
                            .replace("y=", "").split(",");

                    moves.get(moves.size() - 1).add(
                            new Move(remove, new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1])))
                    );
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
        return !(sublistid < moves.size());
    }

    /**
     * Whether the AI has a random part.
     *
     * @return True if randomness is used, false otherwise.
     */
    @Override
    public boolean isRandom() {
        return false;
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
