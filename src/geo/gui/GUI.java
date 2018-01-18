package geo.gui;

import geo.player.AIPlayer;
import geo.player.AbstractPlayer;
import geo.player.HumanPlayer;
import geo.player.ImportFilePlayer;
import geo.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

/**
 * The GUI of the application, which is a singleton object.
 */
public class GUI {
    // Reference to the singleton instance.
    private static GUI gui;

    // The main frame.
    private static JFrame frame;

    // Components of the GUI.
    private JPanel rootPanel;
    private JButton nextTurnButton;
    private JButton resetBoardButton;
    private JButton startButton;
    private JComboBox<AbstractPlayer> playerBlueOptions;
    private JComboBox<AbstractPlayer> playerRedOptions;
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
    private JCheckBox drawVoronoiDiagramCheckBox;
    private JPanel playerRedSettings;
    private JPanel playerBlueSettings;
    private JButton createScreenshotButton;
    private JButton startExperimentButton;
    private JTextField numberOfTrialsField;

    /**
     * The GUI is a singleton.
     */
    private GUI() {

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
            frame = new JFrame("[2IMA15] Geometric Algorithms - Voronoi");
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
     * @param players The list of players the first user can choose from.
     * @param players The list of players the second user can choose from.
     * @param player  The player that will control the game through the GUI.
     */
    public void init(AbstractPlayer[] players, AbstractPlayer[] players2, HumanPlayer player) {
        // Set the player options.
        Arrays.stream(players).forEach(p -> playerRedOptions.addItem(p));
        Arrays.stream(players2).forEach(p -> playerBlueOptions.addItem(p));

        // Add the player as a listener to the buttons and click events on the board panel.
        nextTurnButton.addActionListener(player);
        resetBoardButton.addActionListener(player);
        startButton.addActionListener(player);
        startExperimentButton.addActionListener(player);
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

        // Now, add action listeners to all objects that should trigger a redraw.
        drawDebugLabelsCheckBox.addActionListener(e -> redrawGamePanel());
        drawCircumCirclesCheckBox.addActionListener(e -> redrawGamePanel());
        drawCircumCentersCheckBox.addActionListener(e -> redrawGamePanel());
        drawDelaunayTriangulationCheckBox.addActionListener(e -> redrawGamePanel());
        drawVoronoiDiagramCheckBox.addActionListener(e -> redrawGamePanel());

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        playerRedOptions.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Switch to the correct panel by emptying the panel, and filling it with a new one.
                playerRedSettings.removeAll();
                if(e.getItem() instanceof AIPlayer) {
                    playerRedSettings.add(((AIPlayer) e.getItem()).getPanel(), BorderLayout.CENTER);
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        playerBlueOptions.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Switch to the correct panel by emptying the panel, and filling it with a new one.
                playerBlueSettings.removeAll();
                if(e.getItem() instanceof AIPlayer) {
                    playerBlueSettings.add(((AIPlayer) e.getItem()).getPanel(), BorderLayout.CENTER);
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        createScreenshotButton.addActionListener(e -> {
            BufferedImage image = new BufferedImage(boardPanel.getWidth(), boardPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            boardPanel.paint(image.createGraphics());

            TransferableImage trans = new TransferableImage(image);
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents(trans, null);
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
        return (AbstractPlayer) playerRedOptions.getSelectedItem();
    }

    /**
     * Set the current red player by id.
     *
     * @param id The id of the player we want to set.
     */
    public void setCurrentRedPlayer(int id) {
        playerRedOptions.setSelectedIndex(id);
    }

    /**
     * Get the currently selected red button.
     *
     * @return The red player in the combobox.
     */
    public AbstractPlayer getCurrentBluePlayer() {
        return (AbstractPlayer) playerBlueOptions.getSelectedItem();
    }

    /**
     * Set the current blue player by id.
     *
     * @param id The id of the player we want to set.
     */
    public void setCurrentBluePlayer(int id) {
        playerBlueOptions.setSelectedIndex(id);
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
     */
    public void updateGameStateCounters(int redCount, int blueCount, double redArea) {
        redPointsCountLabel.setText(String.valueOf(redCount));
        bluePointsCountLabel.setText(String.valueOf(blueCount));
        if(redCount == blueCount + 1 && blueCount != 0) {
            System.out.println("Score: " + redArea + ", " + (100d - redArea));
        }
        redBoardControlLabel.setText(String.format( "%.1f", Math.min(redArea, 100)) + "%");
        blueBoardControlLabel.setText(String.format( "%.1f", Math.max(redArea == 0d ? 0 : 100d - redArea, 0)) + "%");
    }

    /**
     * Whether to draw the Delaunay triangulation.
     *
     * @return True if we want to draw the Delaunay triangulation, false otherwise.
     */
    public boolean drawDelaunayTriangulation() {
        return drawDelaunayTriangulationCheckBox.isSelected();
    }

    /**
     * Whether to draw the circum centers of the circum circles.
     *
     * @return True if we want to draw the circum centers, false otherwise.
     */
    public boolean drawCircumCenters() {
        return drawCircumCentersCheckBox.isSelected();
    }

    /**
     * Whether to draw the circum circles of the triangles in the triangulation.
     *
     * @return True if we want to draw the circum circles, false otherwise.
     */
    public boolean drawCircumCircles() {
        return drawCircumCirclesCheckBox.isSelected();
    }

    /**
     * Whether to draw the debug labels.
     *
     * @return True if we want to draw the debug labels, false otherwise.
     */
    public boolean drawDebugLabels() {
        return drawDebugLabelsCheckBox.isSelected();
    }

    /**
     * Whether to draw the Voronoi diagram the debug labels.
     *
     * @return True if we want to draw the Voronoi diagram, false otherwise.
     */
    public boolean drawVoronoiDiagram() {
        return drawVoronoiDiagramCheckBox.isSelected();
    }

    /**
     * Set the state to render in the board panel.
     *
     * @param state The state we want to render in the game panel.
     */
    public void setState(GameState state) {
        ((GamePanel) boardPanel).setState(state);
    }

    /**
     * Repaint the game panel.
     */
    public void redrawGamePanel() {
        boardPanel.repaint();
    }

    /**
     * Get the size of the game panel.
     *
     * @return The size of the game panel as a dimension object.
     */
    public Dimension getGamePanelDimensions() {
        return boardPanel.getSize();
    }

    /**
     * Create the GUI components that are not automatically created.
     */
    private void createUIComponents() {
        // We initialize the spinner using a special model, since we want to start at a different point.
        numberOfTurnsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        // Obviously, we have to initialize our drawing panel.
        boardPanel = new GamePanel(this);
    }

    /**
     * Return the number of trials the user desires.
     *
     * @return The number if given, -1 otherwise.
     */
    public int getNumberOfTrials() {
        try {
            return Integer.parseInt(numberOfTrialsField.getText());
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}
