package alg;

public class GameManager {
    // Variable in which we can keep the current state of the game.
    private final GameState gameState;

    // A reference to the GUI, such that we can push updates when necessary.
    private final ApplicationWindow gui;

    GameManager(ApplicationWindow gui) {
        this.gui = gui;
        gameState = new GameState();
    }

    public void addPoint(java.awt.Point point) {
        gameState.addPoint(point);
    }

    public void removePoint(java.awt.Point point) {
        gameState.removePoint(point);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void reset() {
        gameState.reset();
    }

    public void switchPlayer() {
        gameState.switchPlayer();
    }
}
