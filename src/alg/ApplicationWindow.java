package alg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ApplicationWindow {
    private JPanel rootPanel;

    private ApplicationWindow() {
        // Initialize the listeners we use.
        initializeListeners();
    }

    private void initializeListeners() {

    }

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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ApplicationWindow::createAndShowGui);
    }
}
