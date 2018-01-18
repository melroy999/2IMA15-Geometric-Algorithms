package geo.player;

import geo.controller.GameController;
import geo.gui.GUI;
import geo.state.GameState;
import geo.store.math.Point2d;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class EquilateralTriangleGridPlayer extends TriangleGridPlayer {

    // Whether we are done or not, obviously.
    private JPanel rootPanel;
    private JTextField edgeSize;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player     The human player we should follow up with when the AI is done.
     * @param turn       The turn this player should be active in.
     */
    public EquilateralTriangleGridPlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, player, turn);
    }

    public boolean isWithinBounds(Point2d p, Dimension dimension) {
        return p.x >= 0 && p.x < dimension.width && p.y >=0 && p.y < dimension.height;
    }

    @Override
    protected double getTriangleHeight(int edgeLength) {
        return Math.tan(Math.toRadians(60)) * edgeLength / 2d;
    }

    protected int getTriangleWidth() {
        return Integer.parseInt(edgeSize.getText());
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
