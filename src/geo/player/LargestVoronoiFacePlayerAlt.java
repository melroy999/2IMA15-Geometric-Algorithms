package geo.player;

import geo.controller.GameController;
import geo.delaunay.TriangleFace;
import geo.gui.GUI;
import geo.state.GameState;
import geo.store.halfedge.Face;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.store.math.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI Player that attempts to place a point in the current largest Voronoi Face.
 * Goal is to place a point just beside the opponent's point,
 * away from Voronoi Edges.
 */
public class LargestVoronoiFacePlayerAlt extends LargestVoronoiFacePlayer {

    public LargestVoronoiFacePlayerAlt(GameController controller, HumanPlayer player, GameState.PlayerTurn turn){
        super(controller, player, turn);
    }

    @Override
    protected GameState.FaultStatus calculateMove(GameState state, Set<Face> failures) {
        GameState.FaultStatus status;
        Face largestFace = null;
        do {
            if(largestFace != null) failures.add(largestFace);

            //First, find the largest Face and the point inside that face.
            largestFace = findLargestFace(state, failures);

            // Now that we have the largest point, we want to find the average of the points in the face.
            List<Vertex<Face>> vertices = largestFace.edges().stream().map(e -> e.origin).collect(Collectors.toList());
            Vertex<TriangleFace> largestPoint = largestFace.centerPoint;

            // Calculate the center of mass.
            Point2d center = new Point2d(vertices.stream().mapToDouble(v -> v.x).average().orElse(0), vertices.stream().mapToDouble(v -> v.y).average().orElse(0));

            //Find the Vector pointing from the face center point to the average point.
            Vector2d direction = new Vector2d(largestPoint.x - center.x, largestPoint.y - center.y).normalize().scale(Vertex.staticRadius * 1.1d);

            // The new point we are about to add...
            Point2d newPoint = new Point2d(largestPoint.x - direction.x, largestPoint.y - direction.y);

            // Check if finite.
            if(Double.isFinite(newPoint.x) && Double.isFinite(newPoint.y)) {
                // and place our move beside largestPoint in this direction.
                status = addPoint(new Point2d(largestPoint.x - direction.x, largestPoint.y - direction.y));
            } else {
                // Attempt the original strategy.
                Vertex<TriangleFace> nearestPoint = state.getPoints().stream()
                        .filter((a) -> a.id != largestPoint.id)
                        .min(Comparator.comparingDouble(a -> a.distance(largestPoint))).get();

                //Find the Vector pointing from nearestPoint to largestPoint,
                direction = new Vector2d(largestPoint.x - nearestPoint.x, largestPoint.y - nearestPoint.y).normalize().scale(Vertex.staticRadius * 1.1d);

                // and place our move beside largestPoint in this direction.
                status = addPoint(new Point2d(largestPoint.x + direction.x, largestPoint.y + direction.y));
            }

            if(status == GameState.FaultStatus.PointExists) {
                System.out.println("Point exists.");
            }

        } while(status == GameState.FaultStatus.PointExists || status == GameState.FaultStatus.Error);

        return status;
    }
}
