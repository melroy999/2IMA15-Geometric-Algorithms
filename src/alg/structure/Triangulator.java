package alg.structure;

import alg.FaceSearcher;
import alg.Point;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import java.awt.*;
import java.util.ArrayList;

/**
 * Class that takes care of the Delauney triangulation.
 */
public class Triangulator {
    // We start with a triangle mesh to insert our data into.
    TriangleMesh mesh = new TriangleMesh();

    public TriangleMesh getMesh() {
        return mesh;
    }

    public void insert(Point point) throws TriangleMesh.PointInsertedInOuterFaceException, TriangleMesh.EdgeNotfoundException, TriangleMesh.MissingVertexException {
        // Convert the point to a vertex.
        Vertex v = new Vertex(point);

        // Insert the voice into the triangle mesh.
        mesh.insertVertex(v);

        // Now, legalize all of the edges on the opposite side of the triangles of v.
        for(Edge e : v) {
            // We know that e originates from v, and since we only have triangles, the edge we want is the next edge.
            legalizeEdge(e.next);
        }
    }

    private void legalizeEdge(Edge edge) throws TriangleMesh.MissingVertexException {
        // If the edge is illegal, swap it.
        if(edge.isIllegal()) {
            // We want to find the previous edge of the original edge,
            // such that we can find the two edges we want to legalize as well.
            Edge parentEdge = edge.previous;

            // Call for a swap.
            mesh.swapEdge(edge);

            // Now, we essentially want to check the next of the parent edge,
            // and the next of the twin of the edge previous to the parent edge.
            legalizeEdge(parentEdge.next);
            legalizeEdge(parentEdge.previous.twin.next);
        }
    }
}
