package geo.player;

import geo.controller.GameController;
import geo.gui.GUI;
import geo.state.GameState;
import geo.store.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PythagoreanTiling extends AIPlayer {
    private JPanel rootPanel;
    private JTextField t1SizeField;
    private JTextField t2SizeField;

    // Whether we are done or not, obviously.
    private boolean done;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player     The human player we should follow up with when the AI is done.
     * @param turn       The turn this player should be active in.
     */
    public PythagoreanTiling(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);
    }

    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state) {
        // Get the value for numPoints
        int bigTriangleSize;
        try {
            bigTriangleSize = Integer.parseInt(t1SizeField.getText());
        } catch (NumberFormatException n){
            System.out.println("Please tell me the size of the large triangle.");
            done = true;
            return;
        }

        int smallTriangleSize;
        try {
            smallTriangleSize = Integer.parseInt(t2SizeField.getText());
        } catch (NumberFormatException n){
            System.out.println("Please tell me the size of the small triangle.");
            done = true;
            return;
        }

        if(smallTriangleSize < 30) {
            System.out.println("The small triangle should be at least 30 in size.");
            done = true;
            return;
        }

        if(bigTriangleSize - smallTriangleSize < 30) {
            System.out.println("The difference between the two triangles should be at least 30 in size.");
        }

        // Lets start with a pythagoras tiling.
        // The dimensions of the playing field.
        Dimension dimension = GUI.createAndShow().getGamePanelDimensions();

        // We will maintain a set of points, such that we will not end up with duplicate points.
        Set<Point> points = new HashSet<>();

        // In essence, we can draw the entire figure by only inserting the large triangle corner points.
        // We start by drawing a large triangle in the top left corner.
        // We start in the center of the screen.
        Point p = new Point(dimension.width / 2, dimension.height / 2);

        // Start gathering the points.
        recursiveDraw(p, points, bigTriangleSize, smallTriangleSize, dimension);

        // Draw the points.
        addPoints(points.toArray(new Point[0]));

        // We are done.
        done = true;
    }

    /**
     * Recursively gather points that would be in the pythagorean tiling.
     *
     * @param bottomLeft The bottom left point in the triangle.
     * @param points The set of points we can add new points to.
     * @param s1 The size of the large triangle.
     * @param s2 The size of the small triangle.
     */
    private void recursiveDraw(Point bottomLeft, Set<Point> points, int s1, int s2, Dimension dimension) {
        // Create the three additional points we need.
        Point bottomRight = new Point(bottomLeft.x + s1, bottomLeft.y);
        Point topLeft = new Point(bottomLeft.x, bottomLeft.y + s1);
        Point topRight = new Point(bottomLeft.x + s1, bottomLeft.y + s1);

        // For each of the points, check if they already exist or are out of bounds.
        boolean bottomLeftFlag = points.contains(bottomLeft) || !isWithinBounds(bottomLeft, dimension);
        boolean bottomRightFlag = points.contains(bottomRight) || !isWithinBounds(bottomRight, dimension);
        boolean topLeftFlag = points.contains(topLeft) || !isWithinBounds(topLeft, dimension);
        boolean topRightFlag = points.contains(topRight) || !isWithinBounds(topRight, dimension);

        // If all are flagged, we don't have to do anything.
        if(bottomLeftFlag && bottomRightFlag && topLeftFlag && topRightFlag) {
            return;
        }

        // Otherwise, add those that are flagged false and proceed.
        if(!bottomLeftFlag) points.add(bottomLeft);
        if(!bottomRightFlag) points.add(bottomRight);
        if(!topLeftFlag) points.add(topLeft);
        if(!topRightFlag) points.add(topRight);

        // Do the recursive calls.
        recursiveDraw(new Point(topLeft.x - s1, topLeft.y - s1 + s2), points, s1, s2, dimension);
        recursiveDraw(new Point(topRight.x - s1 + s2, topRight.y), points, s1, s2, dimension);
        recursiveDraw(new Point(bottomRight.x, bottomRight.y - s2), points, s1, s2, dimension);
        recursiveDraw(new Point(bottomLeft.x - s2, bottomLeft.y - s1), points, s1, s2, dimension);
    }

    public boolean isWithinBounds(Point p, Dimension dimension) {
        return p.x >= 0 && p.x < dimension.width && p.y >=0 && p.y < dimension.height;
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
}
