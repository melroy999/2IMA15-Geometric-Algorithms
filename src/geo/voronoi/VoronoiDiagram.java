package geo.voronoi;

import geo.engine.GameEngine;
import geo.gui.ApplicationWindow;
import geo.state.GameState;
import geo.structure.IDrawable;
import geo.structure.geo.Edge;
import geo.structure.geo.Face;
import geo.structure.geo.TriangleFace;
import geo.structure.geo.Vertex;
import geo.structure.graph.DAG;
import geo.structure.math.Point2d;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a Voronoi diagram, which is represented as a DAG.
 */
public class VoronoiDiagram extends DAG<Point2d> {
    // Keep a list of voronoi faces.
    List<Face> faces = new ArrayList<>();

    // Already created edges.
    HashMap<Integer, HashMap<Integer, Edge<Face>>> edgeMap = new HashMap<>();

    /**
     * Create a Voronoi diagram, based on the faces in the Delaunay triangulation.
     *
     * @param vertices The vertices in the Delaunay triangulation.
     */
    public VoronoiDiagram(ArrayList<Vertex<TriangleFace>> vertices) {
        // We want to iterate over all of the vertices, and then find all the circum centers in the surrounding faces.
        for(Vertex<TriangleFace> vertex : vertices) {
            if(vertex instanceof Vertex.SymbolicVertex) {
                // We don't want symbolic vertices.
                continue;
            }

            // For each vertex, we want to create a half-edge face between the circum circles.
            List<Edge<TriangleFace>> edges = vertex.edges();

            // Create an list that will contain the edges between circum centers surrounding the edge.
            List<Edge<Face>> vEdges = new ArrayList<>();

            // Gather all circum circle centers.
            List<Vertex<Face>> vVertices = edges.stream().map(
                    e -> e.incidentFace.cc).collect(Collectors.toList());

            // Create an edge between each of the Voronoi vertices.
            // TODO reuse already constructed edges here.
            for(int i = 0; i < vVertices.size(); i++) {
                Vertex<Face> v1 = vVertices.get(i);
                Vertex<Face> v2 = vVertices.get((i + 1) % vVertices.size());
                Edge<Face> edge = edgeMap.getOrDefault(v1.id, new HashMap<>()).getOrDefault(v2.id, new Edge<>(v1, v2));
                vEdges.add(edge);

                // Store the twin of the edge.
                if(!edgeMap.containsKey(v2.id)) {
                    edgeMap.put(v2.id, new HashMap<>());
                }

                // Add the twin.
                edgeMap.get(v2.id).put(v1.id, edge.twin);
            }

            // Create a face using these edges.
            faces.add(new Face(vertex, vEdges));
        }
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void draw(Graphics2D g, GameEngine engine) {
        // Keep track of the area sizes.
        double red = 0;
        double blue = 0;

        // Simply draw all the faces and the edges...
        for(Face face : faces) {
            // Draw the face first.
            face.draw(g, false);

            // Now draw all the edges, in black.
            g.setColor(Color.black);
            for(Edge<Face> edge : face) {
                edge.draw(g, false);
            }

            // Increment the appropriate counter.
            if(face.centerPoint.player == GameState.Player.RED) {
                red += face.getArea();
            } else {
                blue += face.getArea();
            }
        }

        // Call for a GUI update.
        engine.updateArea(red, blue);
    }
}
