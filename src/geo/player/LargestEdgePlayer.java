package geo.player;

import geo.controller.GameController;
import geo.delaunay.TriangleFace;
import geo.gui.GUI;
import geo.state.GameState;
import geo.store.halfedge.Edge;
import geo.store.halfedge.Vertex;
import geo.store.math.Vector2d;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.Dimension;

public class LargestEdgePlayer extends AIPlayer {

    private JPanel rootPanel;
    private JTextField numPoints;

    //Boolean to check whether AI has finished its moves.
    private boolean isDone = false;
    //Current number of performed moves (since last reset()).
    private int turn = 0;
    //Number of failed moves.
    private int failedMoves = 0;
    //List with the Edges that we have used before (so we do not re-use them in later turns).
    List<Edge> visitedEdges = new ArrayList();

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
            visitedEdges.add(largestEdge);
            visitedEdges.add(largestEdge.twin);
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
                .filter(a -> isValidEdge(a))
                .max(Comparator.comparingDouble(a -> a.origin.distance(a.twin.origin))).get();
    }

    /**
     * Function to determine whether this edge is actually located on the gameboard or has already been used.
     * (thus this edge is not one of the edges placed at infinity).
     * @param e Edge to evaluate.
     * @return whether this edge is actually entirely on the gameboard.
     */
    private boolean isValidEdge(Edge e){
        //If this edge has the same start/end as an already used edge, do not use it.
        //Cannot use id's for this part since the random construction may re-assign id's between moves (it seems).
        if (visitedEdges.stream()
                .anyMatch(a -> a.origin.x == e.origin.x
                        && a.origin.y == e.origin.y && a.twin.origin.x == e.twin.origin.x
                        && a.twin.origin.y == e.twin.origin.y)) return false;

        Vertex start = e.origin;
        Vertex end = e.twin.origin;
        Dimension boardSize = GUI.createAndShow().getGamePanelDimensions();

        //Check if this edge's start and end are within bounds.
        return 0 <= start.x && start.x <= boardSize.width
                && 0 <= end.x && end.x <= boardSize.width
                && 0 <= start.y && start.y <= boardSize.height
                && 0 <= end.y && end.y <= boardSize.height;
    }

    @Override
    public void reset(){ turn = 0; failedMoves = 0; isDone = false; visitedEdges.clear();}

    @Override
    public boolean isRandom(){ return false; }

    @Override
    public JPanel getPanel(){ return rootPanel; }

    @Override
    public boolean isDone(){ return isDone; }
}
