package geo;

import geo.engine.GameEngine;

import javax.swing.*;

/**
 * Small class to start up the game/application.
 */
public class Main {
    public static void main(String[] args) {
        // The application starts when initializing the engine.
        SwingUtilities.invokeLater(GameEngine::getEngine);
    }
}
