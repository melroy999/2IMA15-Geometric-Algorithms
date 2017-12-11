package geo.gui;

import geo.engine.GameEngine;
import geo.log.GeoLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * A window in which the game runs.
 */
public class ApplicationWindow {
    // The components of the GUI.
    private JPanel rootPanel;
    private JPanel topPanel;
    private JPanel contentPanel;
    private JPanel statusPanel;
    private JPanel settingsPanel;
    private JPanel controlPanel;
    private JLabel cursorPositionLabel;
    private JButton resetButton;
    private JButton nextTurnButton;
    private JCheckBox drawTriangulationCheckBox;
    private JCheckBox drawCircumcirclesCheckBox;
    private JCheckBox drawDebugLabelsCheckBox;

    // The game engine.
    private final GameEngine engine;

    /**
     * Create an application window.
     */
    private ApplicationWindow() {
        // Initialize the game engine.
        engine = new GameEngine(this);
        getGamePanel().setEngine(engine);

        // Initialize the listeners we use.
        initializeListeners();

        // Set the triangulation to be checked automatically.
        drawTriangulationCheckBox.setSelected(true);
        getGamePanel().drawTriangulation = true;
    }

    /**
     * Initialize the listeners on the components of the GUI.
     */
    private void initializeListeners() {
        // Actions originating from the content panel.
        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                cursorPositionLabel.setText("(" + e.getX() + ", " + e.getY() + ")");
            }
        });
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                engine.handleUserClickAction(e);
            }
        });

        // Listeners for the buttons.
        resetButton.addActionListener(e -> {
            // Reset the game state and the manager.
            engine.reset();
        });
        nextTurnButton.addActionListener(e -> {
            // Switch the currently active player in the game state.
            engine.switchPlayer();
        });

        // Listeners for the checkboxes.
        drawTriangulationCheckBox.addActionListener(e -> {
            getGamePanel().drawTriangulation = drawTriangulationCheckBox.isSelected();
            contentPanel.repaint();
        });

        drawCircumcirclesCheckBox.addActionListener(e -> {
            getGamePanel().drawCircumcircles = drawCircumcirclesCheckBox.isSelected();
            contentPanel.repaint();
        });

        drawDebugLabelsCheckBox.addActionListener(e -> {
            getGamePanel().drawDebugLabels = drawDebugLabelsCheckBox.isSelected();
            contentPanel.repaint();
        });
    }

    /**
     * Create the GUI, and make it visible.
     */
    private static void createAndShowGui() {
        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new ApplicationWindow().rootPanel);


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setPreferredSize(screenSize);
        frame.setMinimumSize(screenSize);
        frame.pack();
        frame.setVisible(true);

        GeoLogger.getLogger(ApplicationWindow.class.getName());
    }

    /**
     * Start the GUI.
     *
     * @param args N.A.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ApplicationWindow::createAndShowGui);
    }

    /**
     * Initialize specific UI components ourselves.
     */
    private void createUIComponents() {
        contentPanel = new GamePanel();
    }

    /**
     * Get the game panel.
     *
     * @return The content panel, cast to a game panel.
     */
    public GamePanel getGamePanel() {
        return (GamePanel) contentPanel;
    }
}
