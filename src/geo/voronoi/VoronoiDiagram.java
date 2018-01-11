package geo.voronoi;

import geo.delaunay.TriangleFace;
import geo.state.GameState;
import geo.store.graph.DAG;
import geo.store.halfedge.Edge;
import geo.store.halfedge.Face;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.util.SutherlandHodgmanClipping;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class representing a Voronoi diagram, which is represented as a DAG.
 */
public class VoronoiDiagram extends DAG<Point2d> {
    // Keep a list of voronoi faces.
    List<Face> faces = new ArrayList<>();

    // Already created edges.
    HashMap<Integer, HashMap<Integer, Edge<Face>>> edgeMap = new HashMap<>();

    // The total areas of both the red and blue faces.
    private final double areaRed;
    private final double areaBlue;

    /**
     * Create a Voronoi diagram, based on the faces in the Delaunay triangulation.
     *
     * @param vertices The vertices in the Delaunay triangulation.
     */
    public VoronoiDiagram(List<Vertex<TriangleFace>> vertices) {
        // Counters for the areas.
        double areaRed = 0;
        double areaBlue = 0;

        // We want to iterate over all of the vertices, and then find all the circum centers in the surrounding faces.
        for (Vertex<TriangleFace> vertex : vertices) {
            if (vertex instanceof Vertex.SymbolicVertex) {
                // We don't want symbolic vertices.
                continue;
            }

            // For each vertex, we want to create a half-edge face between the circum circles.
            List<Edge<TriangleFace>> edges = vertex.edges();

            // Create an list that will contain the edges between circum centers surrounding the edge.
            List<Edge<Face>> vEdges = new ArrayList<>();

            // Gather all circum circle centers.
            List<Point2d> circumCenters = edges.stream().map(e -> e.incidentFace.cc).collect(Collectors.toList());

            // Now we have to make sure that the vertices are in range of the window.
            List<Vertex<Face>> vVertices = SutherlandHodgmanClipping.clipPolygon(circumCenters).stream().map(
                    (Function<Point2d, Vertex<Face>>) Vertex::new).collect(Collectors.toList());

            // Create an edge between each of the Voronoi vertices.
            for (int i = 0; i < vVertices.size(); i++) {
                Vertex<Face> v1 = vVertices.get(i);
                Vertex<Face> v2 = vVertices.get((i + 1) % vVertices.size());
                Edge<Face> edge = edgeMap.getOrDefault(v1.id, new HashMap<>()).getOrDefault(v2.id, new Edge<>(v1, v2));
                vEdges.add(edge);

                // Store the twin of the edge.
                if (!edgeMap.containsKey(v2.id)) {
                    edgeMap.put(v2.id, new HashMap<>());
                }

                // Add the twin.
                edgeMap.get(v2.id).put(v1.id, edge.twin);
            }

            // Create a face using these edges.
            Face face = new Face(vertex, vEdges);
            faces.add(face);

            // Add the area of the new face to the owner.
            if(vertex.player == GameState.PlayerTurn.RED) {
                areaRed += face.getArea();
            } else {
                areaBlue += face.getArea();
            }
        }

        // Set the area counters.
        this.areaRed = areaRed;
        this.areaBlue = areaBlue;
    }

    /**
     * Draw the shape.
     *
     * @param g The graphics object to draw in.
     */
    public void draw(Graphics2D g) {
        // Simply draw all the faces and the edges...
        for (Face face : faces) {
            // Draw the face first.
            face.drawFace(g);
        }

        // Now draw all the edges, in black.
        g.setColor(Color.black);

        // Draw all edges.
        for (Face face : faces) {
            for (Edge<Face> edge : face) {
                edge.drawEdge(g);
            }
        }
    }

    /**
     * Get the amount of area the red player owns.
     *
      * @return The total area in pixels of the red player.
     */
    public double getAreaRed() {
        return areaRed;
    }

    /**
     * Get the amount of area the blue player owns.
     *
     * @return The total area in pixels of the blue player.
     */
    public double getAreaBlue() {
        return areaBlue;
    }

    /**
     * Get the list of faces that make up this diagram.
     * @return List of faces.
     */
    public List<Face> getFaces() { return faces; }
}