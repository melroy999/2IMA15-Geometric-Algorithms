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
                System.out.println("Terminating AI run, too many invalid moves.");
                return;
            }
            doMove(state);
        }
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
        opponentPoints.remove(largestPoint);
        Vertex<TriangleFace> nearestPoint = opponentPoints.stream()
                .min((a,b) -> Double.compare(a.distance(largestPoint),b.distance(largestPoint))).get();

        //Find the Vector pointing from nearestPoint to largestPoint,
        Vector2d direction = new Vector2d(largestPoint.x - nearestPoint.x, largestPoint.y - nearestPoint.y).normalize();
        // and place our move beside largestPoint in this direction.
        GameState.FaultStatus status = addPoint(new Point((int) (largestPoint.x + 5 * direction.x), (int) (largestPoint.y + 5 * direction.y)));

        if (status == GameState.FaultStatus.None){
            turn++;
            failedMoves = 0;
        }   else {
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
                .max((a,b) -> Double.compare(a.getArea(), b.getArea()))
                .get();
    }

    @Override
    public boolean isDone(){ return turn > 0; }

    @Override
    public void reset(){ turn = 0; }

    @Override
    public JPanel getPanel(){ return rootPanel; }

}
