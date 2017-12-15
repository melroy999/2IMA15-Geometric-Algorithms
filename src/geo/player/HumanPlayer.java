package geo.player;

import geo.controller.GameController;
import geo.state.GameState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class HumanPlayer extends AbstractPlayer {
    /**
     * Create a player, given the game controller to communicate with.
     *
     * @param controller The game controller to communicate with.
     */
    public HumanPlayer(GameController controller) {
        super(controller);
    }

    /**
     * Execute the turn of the player, given a copy of the game state.
     *
     * @param state A (read-only) copy of the current state of the game.
     */
    @Override
    public void turn(GameState state) {
        throw new NotImplementedException();
    }
}
