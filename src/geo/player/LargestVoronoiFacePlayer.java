package geo.player;

import geo.controller.GameController;
import geo.delaunay.TriangleFace;
import geo.state.GameState;
import geo.store.halfedge.Face;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.store.math.Vector2d;
import geo.gui.GUI;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.*;
import java.awt.Point;
import java.awt.Dimension;

/**
 * AI Player that attempts to place a point in the current largest Voronoi Face.
 * Goal is to place a point just beside the opponent's point,
 * away from Voronoi Edges.
 */
public class LargestVoronoiFacePlayer extends AIPlayer {

    private JPanel rootPanel;
    private JTextField numPoints;

    //Number of turns performed by the player.
    private int turn = 0;
    private boolean isDone = false;

    //Counter to see whether we're making bad moves (preventing eternal loops).
    private int failedMoves = 0;

    public LargestVoronoiFacePlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn){
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

        // The faces that resulted in an error.
        Set<Face> failures = new HashSet<>();


        //Do stuff. If we fail 3 moves in a row, give up (something is probably wrong).
        while (turn < moves){
            if (failedMoves > 2){
                System.out.println("Terminating AI run after "+turn+" moves, too many consecutive invalid moves.");
                isDone = true;
                return;
            }
            doMove(state, failures);
        }

        // We are done, set the done flag.
        isDone = true;
    }

    /**
     * Find the best move to perform at this moment, and execute it.
     * @param state current GameState.
     * @param failures The faces that lead to a failure.
     */
    protected void doMove(GameState state, Set<Face> failures){
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

        // Calculate the move and get the status.
        GameState.FaultStatus status = calculateMove(state, failures);

        if (status == GameState.FaultStatus.None){
            turn++;
            failedMoves = 0;
        }   else {
            System.out.println(status);
            failedMoves ++;
        }
    }

    protected GameState.FaultStatus calculateMove(GameState state, Set<Face> failures) {
        GameState.FaultStatus status;
        Face largestFace = null;
        do {
            if(largestFace != null) failures.add(largestFace);

            //First, find the largest Face and the point inside that face.
            largestFace = findLargestFace(state, failures);
            Vertex<TriangleFace> largestPoint = largestFace.centerPoint;

            //Then, find the nearest other point, since we wish to place our point away from it.
            //Remove our largestPoint, obviously it would be closest.
            Vertex<TriangleFace> nearestPoint = state.getPoints().stream()
                    .filter((a) -> a.id != largestPoint.id)
                    .min(Comparator.comparingDouble(a -> a.distance(largestPoint))).get();

            //Find the Vector pointing from nearestPoint to largestPoint,
            Vector2d direction = new Vector2d(largestPoint.x - nearestPoint.x, largestPoint.y - nearestPoint.y).normalize().scale(Vertex.staticRadius * 1.1d);

            // and place our move beside largestPoint in this direction.
            status = addPoint(new Point2d(largestPoint.x + direction.x, largestPoint.y + direction.y));

            if(status == GameState.FaultStatus.PointExists) {
                System.out.println("Point exists.");
            }

        } while(status == GameState.FaultStatus.PointExists);

        return status;
    }

    /**
     * Find largest Face in Voronoi Diagram according to Face.getArea().
     * @param state current GameState.
     * @return Face that has the largest area.
     */
    protected Face findLargestFace(GameState state, Set<Face> failures){
        return state.getVoronoiDiagram().getFaces()
                .stream()
                .filter((a) -> a.centerPoint.player != getPlayer().color && !failures.contains(a))
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

    @Override
    public String toString() {
        return super.toString() + "_" + numPoints.getText();
    }
}
