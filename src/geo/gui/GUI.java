package geo.gui;

import geo.engine.GameEngine;
import geo.player.AbstractPlayer;
import geo.player.HumanPlayer;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

/**
 * The GUI of the application, which is a singleton object.
 */
public class GUI {
    // Reference to the singleton instance.
    private static GUI gui;

    // Components of the GUI.
    private JPanel rootPanel;
    private JButton nextTurnButton;
    private JButton resetBoardButton;
    private JButton startButton;
    private JComboBox<AbstractPlayer> playerTwoOptions;
    private JComboBox<AbstractPlayer> playerOneOptions;
    private JPanel statusBar;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JCheckBox drawDelaunayTriangulationCheckBox;
    private JCheckBox drawCircumCentersCheckBox;
    private JCheckBox drawCircumCirclesCheckBox;
    private JCheckBox drawDebugLabelsCheckBox;
    private JCheckBox snapToGridCheckBox;
    private JLabel currentPlayerLabel;
    private JLabel redBoardControlLabel;
    private JLabel redPointsCountLabel;
    private JLabel bluePointsCountLabel;
    private JLabel blueBoardControlLabel;
    private JCheckBox limitNumberOfTurnsCheckBox;
    private JSpinner numberOfTurnsSpinner;
    private JLabel cursorPositionLabel;
    private JCheckBox showVoronoiPreviewCheckBox;

    /**
     * The GUI is a singleton.
     */
    private GUI() {
        boardPanel.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        boardPanel.addMouseListener(new MouseAdapter() {
        });
    }

    /**
     * Create a GUI instance, and make it visible.
     *
     * @return The singleton GUI instance.
     */
    public static GUI createAndShow() {
        if(gui == null) {
            try {
                // Make the application look like a windows application.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Initialize the singleton instance.
            JFrame frame = new JFrame("[2IMA15] Geometric Algorithms - Voronoi");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            gui = new GUI();
            frame.setContentPane(gui.rootPanel);


            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setPreferredSize(screenSize);
            frame.setMinimumSize(screenSize);
            frame.pack();
            frame.setVisible(true);
        }

        // Return the instance of the GUI.
        return gui;
    }

    /**
     * Initialize the GUI with the desired values.
     *
     * @param players The list of players the user can choose from.
     * @param player  The player that will control the game through the GUI.
     */
    public void init(AbstractPlayer[] players, HumanPlayer player) {
        // Set the player options.
        Arrays.stream(players).forEach(p -> playerOneOptions.addItem(p));
        Arrays.stream(players).forEach(p -> playerTwoOptions.addItem(p));

        // Add the player as a listener to the buttons and click events on the board panel.
        nextTurnButton.addActionListener(player);
        resetBoardButton.addActionListener(player);
        startButton.addActionListener(player);
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Only allow mouse clicks if the next button is enabled, as this indicates that it is a player's turn.
                if(nextTurnButton.isEnabled()) player.userMouseClickEvent(e);
            }
        });

        // Add other listeners.
        boardPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                cursorPositionLabel.setText("(" + e.getX() + ", " + e.getY() + ")");
            }
        });
    }

    /**
     * Change the current player to the given turn.
     *
     * @param turn The player's turn.
     */
    public void changeCurrentPlayerLabel(GameState.PlayerTurn turn) {
        currentPlayerLabel.setText(turn == null ? "" : turn.toString());
    }

    /**
     * Set the current activation state of the next button.
     *
     * @param value The value to set the state to, true of false.
     */
    public void changeNextButtonEnabled(boolean value) {
        nextTurnButton.setEnabled(value);
    }

    /**
     * Set the current activation state of the reset button.
     *
     * @param value The value to set the state to, true of false.
     */
    public void changeResetButtonEnabled(boolean value) {
        resetBoardButton.setEnabled(value);
    }

    /**
     * Set the state of the start button.
     *
     * @param value The value to set the state to, true of false.
     */
    public void changeStartButtonEnabled(boolean value) {
        startButton.setEnabled(value);
    }

    /**
     * Get the currently selected red button.
     *
     * @return The red player in the combobox.
     */
    public AbstractPlayer getCurrentRedPlayer() {
        return (AbstractPlayer) playerOneOptions.getSelectedItem();
    }

    /**
     * Get the currently selected red button.
     *
     * @return The red player in the combobox.
     */
    public AbstractPlayer getCurrentBluePlayer() {
        return (AbstractPlayer) playerTwoOptions.getSelectedItem();
    }

    /**
     * Get the maximum number of turns each player has.
     *
     * @return The amount of turns each player has, -1 if unlimited.
     */
    public int getMaximumNumberOfTurns() {
        return !limitNumberOfTurnsCheckBox.isSelected() ? -1 : (int) numberOfTurnsSpinner.getValue();
    }

    /**
     * Update the red player number of points and possessed area counters.
     *
     * @param redCount The amount of red points.
     * @param blueCount The amount of blue points.
     * @param redArea The area the red player possesses.
     * @param blueArea The area the blue player possesses.
     */
    public void updateGameStateCounters(int redCount, int blueCount, int redArea, int blueArea) {
        redPointsCountLabel.setText(String.valueOf(redCount));
        bluePointsCountLabel.setText(String.valueOf(blueCount));
        redBoardControlLabel.setText(redArea + "%");
        blueBoardControlLabel.setText(blueArea + "%");
    }

    private void createUIComponents() {
        // We initialize the spinner using a special model, since we want to start at a different point.
        numberOfTurnsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        // Obviously, we have to initialize our drawing panel.
        boardPanel = new GamePanel(this);
    }
}
