package dataStructure;

import dataStructure.halfEdge.Face;
import dataStructure.halfEdge.HalfEdge;
import dataStructure.halfEdge.Vertex;

import java.util.*;

/**
 * The mesh class holds a collection of faces, half edges and vertices.
 */
public class TriangulationMesh {
    // The objects of which the mesh consist. These are Sets, such that we can easily do remove operations.
    private final Set<Face> faces = new HashSet<>();
    private final Set<Vertex> vertices = new HashSet<>();
    private final Set<HalfEdge> edges = new HashSet<>();

    // A special outer face.
    private final Face outerFace = new Face();

    /**
     * Add a face to the mesh.
     *
     * @param vertices The vertices of the face we want to add, in counter clockwise order.
     */
    public void addInitialFace(Vertex[] vertices) {
        // Create the face.
        Face face = new Face();

        // Keep a reference to all the half edges we created here, such that we can fix certain internal references.
        HalfEdge[] sourceEdges = new HalfEdge[vertices.length];
        HalfEdge[] targetEdges = new HalfEdge[vertices.length];

        // For each consecutive vertex pair, create half edges.
        for(int i = 0; i < vertices.length; i++) {
            // Create half-edges for both directions.
            Vertex source = vertices[i];
            Vertex target = vertices[(i + 1) % vertices.length];

            sourceEdges[i] = new HalfEdge(source).setIncidentFace(face);
            targetEdges[i] = new HalfEdge(target).setIncidentFace(outerFace);

            // Set the twins of the half edges.
            sourceEdges[i].setTwin(targetEdges[i]);
            targetEdges[i].setTwin(sourceEdges[i]);
        }

        // Set the correct next/previous points in the half edges.
        for(int i = 0; i < vertices.length; i++) {
            // Here, point to the next half-edge in the list.
            // For the target edges, we do the opposite!
            sourceEdges[i].setNext(sourceEdges[(i + 1) % vertices.length]);
            targetEdges[i].setNext(targetEdges[(i - 1 + vertices.length) % vertices.length]);

            // Here, point to the previous, which obviously is the opposite for the target edges.
            sourceEdges[i].setPrevious(sourceEdges[(i - 1 + vertices.length) % vertices.length]);
            targetEdges[i].setPrevious(targetEdges[(i + 1) % vertices.length]);
        }

        // Now, add one of the source edges to the face.
        face.setOuterComponent(sourceEdges[0]);
        outerFace.setOuterComponent(targetEdges[0]);

        // Add the references to the mesh.
        this.vertices.addAll(Arrays.asList(vertices));
        this.edges.addAll(Arrays.asList(sourceEdges));
        this.edges.addAll(Arrays.asList(targetEdges));
        this.faces.add(face);
    }
}
