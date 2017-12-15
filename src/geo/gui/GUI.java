package geo.gui;

import geo.player.AbstractPlayer;

import javax.swing.*;
import java.awt.*;
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
            JFrame frame = new JFrame("Window");
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
     */
    public void init(AbstractPlayer[] players) {
        Arrays.stream(players).forEach(p -> playerOneOptions.addItem(p));
        Arrays.stream(players).forEach(p -> playerTwoOptions.addItem(p));
    }
}
