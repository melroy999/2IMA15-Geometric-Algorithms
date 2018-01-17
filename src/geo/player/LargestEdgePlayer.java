package geo.player;

import geo.controller.GameController;
import geo.delaunay.TriangleFace;
import geo.state.GameState;
import geo.store.halfedge.Edge;
import geo.store.halfedge.Vertex;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Point;

public class LargestEdgePlayer extends AIPlayer {

    private JPanel rootPanel;
    private JTextField numPoints;

    //Boolean to check whether AI has finished its moves.
    private boolean isDone = false;
    //Current number of performed moves (since last reset()).
    private int turn = 0;
    //Number of failed moves.
    private int failedMoves = 0;

    public LargestEdgePlayer(GameController controller, HumanPlayer player, GameState.PlayerTurn turn){
        super(controller, player, turn);
    }

    public void runAI(GameState state){
        //Read number of moves from GUI.
        int moves;
        try {
            moves = Integer.parseInt(numPoints.getText());
        } catch (NumberFormatException e){
            moves = 10;
            System.out.println("Bad input for number of moves, running with moves = 10.");
        }
        //Perform moves.
        while (turn < moves){
            if (failedMoves > 2){
                System.out.println("Terminating AI run after "+turn+" moves, too many invalid moves.");
                isDone = true;
                return;
            }
            doMove(state);
        }
    }

    /**
     * Performs a move.
     * @param state The game state to read data from.
     */
    public void doMove(GameState state){
        //Obtain the largest edge.
        Edge largestEdge = findLargestEdge(state);

        //Interpolate the middle of this Edge.
        Vertex start = largestEdge.origin;
        Vertex end = largestEdge.twin.origin;

        Point centre = new Point((int)(start.x + end.x)/2, (int)(start.y + end.y)/2);

        GameState.FaultStatus status = addPoint(centre);

        if (status == GameState.FaultStatus.None){
            turn++;
        }   else {
            System.out.println(status);
            failedMoves++;
        }
    }

    /**
     * Finds largest Edge in the Delaunay triangulation.
     * @param state Gamestate to look at.
     * @return Edge for which edge.origin().distance(edge.next().origin) is maximised.
     */
    private Edge findLargestEdge(GameState state){
        //Put all Edges into one list
        List<Edge<TriangleFace>> edges = new ArrayList();
        state.getTriangulatedFaces()
                .stream()
                .forEach(a -> edges.addAll(a.edges()));

        //Find the largest Edge in this list.
        return edges
                .stream()
                .max(Comparator.comparingDouble(a -> a.origin.distance(a.twin.origin))).get();
    }

    @Override
    public void reset(){ turn = 0; failedMoves = 0; isDone = false;}

    @Override
    public boolean isRandom(){ return false; }

    @Override
    public JPanel getPanel(){ return rootPanel; }

    @Override
    public boolean isDone(){ return isDone; }
}
