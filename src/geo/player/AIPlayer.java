package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

import javax.swing.*;

/**
 * Player template for artificial intelligence based players.
 */
public abstract class AIPlayer extends AbstractPlayer {
    // The player that follows the AI.
    private final HumanPlayer player;

    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param player The human player we should follow up with when the AI is done.
     * @param turn The turn this player should be active in.
     */
    public AIPlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn) {
        super(controller, turn);
        this.player = player;
        try {
            // Make the application look like a windows application.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the turn of the player, given a copy of the game state.
     *
     * @param state A (read-only) copy of the current state of the game.
     */
    @Override
    public final void turn(GameState state) {
        // call the AI logic loop.
        runAI(state);

        // We should always end the turn.
        this.endTurn();
    }

    /**
     * Run the AI of this player.
     *
     * @param state The game state to read data from.
     */
    protected abstract void runAI(GameState state);

    /**
     * Check whether the ai player is done with his/her turns.
     *
     * @return True if done, false otherwise.
     */
    public abstract boolean isDone();

    /**
     * Whether the AI has a random part.
     *
     * @return True if randomness is used, false otherwise.
     */
    public abstract boolean isRandom();

    /**
     * Reset the state of the AI so that we can use it again.
     */
    public abstract void reset();

    /**
     * Get a panel containing the controls for this specific AI player.
     *
     * @return A JPanel which contains all required components to control the AI.
     */
    public abstract JPanel getPanel();

    /**
     * Get the human player that follows up this AI player.
     *
     * @return The human player.
     */
    public HumanPlayer getPlayer() {
        return player;
    }
}
