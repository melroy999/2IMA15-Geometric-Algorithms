package geo.player;

import geo.controller.GameController;
import geo.gui.GUI;
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
     * We want to have voronoi faces of the same size if possible
     *
     * @param numPoints The number of points to be placed.
     */
    public void createGrid(int numPoints){
        // Use the square root of the number of points rounded up as the amount of rows
        int rows = (int) Math.ceil((Math.sqrt((double) numPoints)));
        System.out.println(rows);
        // The amount of points we still have to place
        int remainingPoints = numPoints;
        // The amount of rows that haven't had all points placed yet
        int remainingRows = rows;
        for (int i = 0; i < rows; i++){
            // y-coord of point is the height of a voronoi face * row a point is in -1 +
            // half of the height of a voronoi face
            int y = (int) Math.floor(GUI.createAndShow().getGamePanelDimensions().height / (rows))*(i) +
                    (int) (Math.floor(GUI.createAndShow().getGamePanelDimensions().height) / (rows) * 0.5);
            // The amount of points to be placed in a row
            int columns = (int) Math.ceil( (double)remainingPoints / remainingRows);
            System.out.println(columns);
            for (int j = 0; j < columns; j++) {
                // x-coord of point is the width of a voronoi face * column a point is in -1 +
                // half of the width of a voronoi face
                int x = (int) (Math.floor(GUI.createAndShow().getGamePanelDimensions().width / (columns))*(j)) +
                        (int) (Math.floor(GUI.createAndShow().getGamePanelDimensions().width / (columns)) * 0.5);
                // Add the point
                addPoint(new Point(x, y));
            }
            // Decrease the amount of points remaining and the amount of rows remaining
            remainingPoints -= columns;
            remainingRows -= 1;
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
        int numPointsValue;
        try {
            numPointsValue = Integer.parseInt(numPoints.getText());
        } catch (NumberFormatException n){
            System.out.println("Please tell me how many points to place.");
            return;
        }
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
