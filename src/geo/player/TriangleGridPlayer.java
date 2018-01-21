package geo.player;

import geo.controller.GameController;
import geo.gui.GUI;
import geo.state.GameState;
import geo.store.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TriangleGridPlayer extends AIPlayer {

    // Whether we are done or not, obviously.
    private boolean done;
    private JPanel rootPanel;
    private JTextField edgeWidth;
    private JTextField edgeHeight;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player     The human player we should follow up with when the AI is done.
     * @param turn       The turn this player should be active in.
     */
    public TriangleGridPlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);
    }

    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state) {
        int edgeLength;

        try {
            edgeLength = getTriangleWidth();
        } catch (NumberFormatException n){
            System.out.println("Please tell me the width of the triangle.");
            done = true;
            return;
        }

        if(edgeLength < 30) {
            System.out.println("The triangle should be at least 30 in size.");
            done = true;
            return;
        }


        // The triangles will have the following height.
        double triangleHeight;

        try {
            triangleHeight = getTriangleHeight(edgeLength);
        } catch (NumberFormatException n){
            System.out.println("Please tell me the height of the triangle.");
            done = true;
            return;
        }

        if(triangleHeight < Math.tan(Math.toRadians(60)) * 30 / 2d) {
            System.out.println("The triangle should be at least 30 in size.");
            done = true;
            return;
        }

        // Lets start with a pythagoras tiling.
        // The dimensions of the playing field.
        Dimension dimension = GUI.createAndShow().getGamePanelDimensions();

        // We will maintain a set of points, such that we will not end up with duplicate points.
        List<Point2d> points = new ArrayList<>();

        // We start in the center of the screen.
        Point2d p = new Point2d(dimension.width / 2d, dimension.height / 2d);

        // We use p as the center line, and move up and down simultaneously.
        for (int i = 0; i < (dimension.height / 2d) / triangleHeight; i++) {
            // If i is 0, we only draw one line. Otherwise we draw both.
            if(i != 0) {
                // Draw down.
                for(int j = 0; j < (dimension.width / 2d) / edgeLength; j++) {
                    if(j != 0 || i % 2 == 1) {
                        // Draw to the left.
                        Point2d pl = new Point2d(p.x - ((i % 2 == 1) ? edgeLength / 2d : 0) - j * edgeLength, p.y - i * triangleHeight);
                        if(isWithinBounds(pl, dimension)) points.add(pl);
                    }

                    // Draw to the right.
                    Point2d pr = new Point2d(p.x + ((i % 2 == 1) ? edgeLength / 2d : 0) + j * edgeLength, p.y - i * triangleHeight);
                    if(isWithinBounds(pr, dimension)) points.add(pr);
                }
            }

            // Draw up.
            for(int j = 0; j < (dimension.width / 2d) / edgeLength; j++) {
                if(j != 0 || i % 2 == 1) {
                    // Draw to the left.
                    Point2d pl = new Point2d(p.x - ((i % 2 == 1) ? edgeLength / 2d : 0) - j * edgeLength, p.y + i * triangleHeight);
                    if(isWithinBounds(pl, dimension)) points.add(pl);
                }

                // Draw to the right.
                Point2d pr = new Point2d(p.x + ((i % 2 == 1) ? edgeLength / 2d : 0) + j * edgeLength, p.y + i * triangleHeight);
                if(isWithinBounds(pr, dimension)) points.add(pr);
            }
        }

        // Add the points.
        addPoints(points.toArray(new Point2d[0]));

        // We are done.
        done = true;
    }

    public boolean isWithinBounds(Point2d p, Dimension dimension) {
        return p.x >= 0 && p.x < dimension.width && p.y >=0 && p.y < dimension.height;
    }

    protected double getTriangleHeight(int edgeLength) {
        return Integer.parseInt(edgeHeight.getText());
    }

    protected int getTriangleWidth() {
        return Integer.parseInt(edgeWidth.getText());
    }

    /**
     * Check whether the ai player is done with his/her turns.
     *
     * @return True if done, false otherwise.
     */
    @Override
    public boolean isDone() {
        return done;
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
        done = false;
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
     * Get the name of the player as a string.
     *
     * @return The simple name of the class.
     */
    @Override
    public String toString() {
        return super.toString() + "_" + edgeWidth.getText() + "_" + edgeHeight.getText();
    }
}
