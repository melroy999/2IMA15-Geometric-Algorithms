package geo.player;

import geo.controller.GameController;
import geo.state.GameState;

public abstract class AIPlayer extends AbstractPlayer {
    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     * @param turn The turn this player should be active in.
     */
    public AIPlayer(GameController controller, GameState.PlayerTurn turn) {
        super(controller, turn);
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
     * Reset the state of the AI so that we can use it again.
     */
    public abstract void reset();
}
