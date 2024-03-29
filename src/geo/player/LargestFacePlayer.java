package geo.player;

import geo.controller.GameController;
import geo.delaunay.TriangleFace;
import geo.state.GameState;
import geo.store.halfedge.Face;
import geo.store.halfedge.Vertex;
import geo.store.math.Vector2d;
import geo.gui.GUI;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Comparator;
import java.util.List;
import java.awt.Point;
import java.awt.Dimension;

/**
 * AI Player that attempts to place a point in the current largest Voronoi Face.
 * Goal is to place a point just beside the opponent's point,
 * away from Voronoi Edges.
 */
public class LargestFacePlayer extends AIPlayer {

    private JPanel rootPanel;
    private JTextField numPoints;

    //Number of turns performed by the player.
    private int turn = 0;
    private boolean isDone = false;

    //Counter to see whether we're making bad moves (preventing eternal loops).
    private int failedMoves = 0;

    public LargestFacePlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn){
        super(controller, player, turn);
    }

    /**
     * Run this AI.
     * @param state The game state to read data from.
     */
    @Override
    protected void runAI(GameState state){
        //Read out number of moves allowed.
        int moves;
        try {
            moves = Integer.parseInt(numPoints.getText());
        }   catch (NumberFormatException e){
            moves = 10;
            System.out.println("Bad input for number of moves, running with moves = 10.");
        }
        //Do stuff. If we fail 3 moves in a row, give up (something is probably wrong).
        while (turn < moves){
            if (failedMoves > 2){
                System.out.println("Terminating AI run after "+turn+" moves, too many consecutive invalid moves.");
                isDone = true;
                return;
            }
            doMove(state);
        }

        // We are done, set the done flag.
        isDone = true;
    }

    /**
     * Find the best move to perform at this moment, and execute it.
     * @param state current GameState.
     */
    protected void doMove(GameState state){
        //Look at opponent's points.
        List<Vertex<TriangleFace>> opponentPoints = (getPlayer().color == GameState.PlayerTurn.RED ? state.getBluePoints() : state.getRedPoints());

        //If opponent has not placed any points, then place a point in the centre.
        if (opponentPoints.isEmpty()){
            Dimension dim = GUI.createAndShow().getGamePanelDimensions();
            GameState.FaultStatus status = addPoint(new Point(dim.width/2, dim.height/2));

            if (status == GameState.FaultStatus.None){
                turn++;
                failedMoves = 0;
            }   else {
                failedMoves ++;
            }
            return;
        }

        //First, find the largest Face and the point inside that face.
        Face largestFace = findLargestFace(state);
        Vertex<TriangleFace> largestPoint = largestFace.centerPoint;
        //Then, find the nearest other point, since we wish to place our point away from it.
        //Remove our largestPoint, obviously it would be closest.
        Vertex<TriangleFace> nearestPoint = state.getPoints().stream()
                .filter((a) -> a.id != largestPoint.id)
                .min(Comparator.comparingDouble(a -> a.distance(largestPoint))).get();
        //Find the Vector pointing from nearestPoint to largestPoint,
        Vector2d direction = new Vector2d(largestPoint.x - nearestPoint.x, largestPoint.y - nearestPoint.y).normalize().scale(12);
        // and place our move beside largestPoint in this direction.
        int x = (int) (largestPoint.x + direction.x);
        int y = (int) (largestPoint.y + direction.y);
        GameState.FaultStatus status = addPoint(new Point(x, y));

        if (status == GameState.FaultStatus.None){
            turn++;
            failedMoves = 0;
        }   else {
            System.out.println(status);
            failedMoves ++;
        }
    }

    /**
     * Find largest Face in Voronoi Diagram according to Face.getArea().
     * @param state current GameState.
     * @return Face that has the largest area.
     */
    private Face findLargestFace(GameState state){
        return state.getVoronoiDiagram().getFaces()
                .stream()
                .filter((a) -> a.centerPoint.player != getPlayer().color)
                .max(Comparator.comparingDouble(Face::getArea))
                .get();
    }

    @Override
    public boolean isDone(){ return isDone; }

    /**
     * Whether the AI has a random part.
     *
     * @return True if randomness is used, false otherwise.
     */
    @Override
    public boolean isRandom() {
        return false;
    }

    @Override
    public void reset(){ turn = 0; failedMoves = 0; isDone = false; }

    @Override
    public JPanel getPanel(){ return rootPanel; }

}
