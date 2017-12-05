package alg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ApplicationWindow {
    private JPanel rootPanel;
    private JPanel contentPanel;
    private JPanel statusBar;
    private JPanel topBar;
    private JLabel mousePositionLabel;
    private JButton resetButton;
    private JButton endTurnButton;

    // The current state of the game and the manager of the game.
    private final GameManager manager;

    private ApplicationWindow() {
        // Initialize the game manager.
        manager = new GameManager(this);
        ((GamePanel) contentPanel).setManager(manager);

        // Initialize the listeners we use.
        initializeListeners();


    }

    private void initializeListeners() {
        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mousePositionLabel.setText("Cursor position: (" + e.getX() + ", " + e.getY() + ")");
            }
        });

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                // Depending on the mouse button, execute an action.
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        manager.addPoint(e.getPoint());
                        break;
                    case MouseEvent.BUTTON3:
                        manager.removePoint(e.getPoint());
                }

                // Since we made an update to the canvas, repaint.
                contentPanel.repaint();
            }
        });

        resetButton.addActionListener(e -> {
            // Reset the game state and the manager.
            manager.reset();

            // Since we made an update to the canvas, repaint.
            contentPanel.repaint();
        });

        endTurnButton.addActionListener(e -> {
            // Switch the currently active player in the game state.
            manager.switchPlayer();
        });
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new ApplicationWindow().rootPanel);
        frame.setPreferredSize(new Dimension(1000, 700));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ApplicationWindow::createAndShowGui);
    }

    private void createUIComponents() {
        // Create the content panel object.
        contentPanel = new GamePanel();
    }
}
