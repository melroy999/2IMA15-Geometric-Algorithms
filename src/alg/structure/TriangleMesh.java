package alg.structure;

import alg.FaceSearcher;
import alg.structure.geom.Triangle2d;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import java.util.HashMap;

/**
 * A mesh consisting of half-edge components, where the faces are only allowed to be triangles.
 */
public class TriangleMesh {
    // The mesh also tracks the face searcher.
    FaceSearcher searcher = new FaceSearcher();

    // List of vertices that are currently in the mesh.
    private final HashMap<Integer, Vertex> vertices = new HashMap<>();

    /**
     * Initialize the triangle mesh, which includes the creation of a very large initial triangle for Delauney.
     */
    public TriangleMesh() {
        // Initially, we should have a triangle already of sufficient size.
        Vertex v1 = new Vertex(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Vertex v2 = new Vertex(Integer.MAX_VALUE, Integer.MIN_VALUE);
        Vertex v3 = new Vertex(0, Integer.MAX_VALUE);

        // Create a new triangle face with these points as the corners.
        Face face = new Face(v1, v2, v3);

        // We have to register this face in the searcher as a root face.
        searcher.insertRootFace(face);

        // Create the required edges.
        // Since we know the order in which the vertices are inserted, we know which half-edge is the inside edge.
        // We know that v1 -> v2 -> v3 is in ccw order, thus v1 -> v2, v2 -> v3 and v3 -> v1 have 'face' as neighbor.
        Edge v1_v2 = new Edge(v1, v2);
        v1_v2.twin = new Edge(v2, v1);
        v1_v2.twin.twin = v1_v2;

        Edge v2_v3 = new Edge(v2, v3);
        v2_v3.twin = new Edge(v3, v2);
        v2_v3.twin.twin = v2_v3;

        Edge v3_v1 = new Edge(v3, v1);
        v3_v1.twin = new Edge(v1, v3);
        v3_v1.twin.twin = v3_v1;

        // Make sure that the edges point to the correct neighbors.
        v1_v2.next = v2_v3;
        v2_v3.next = v3_v1;
        v3_v1.next = v1_v2;
        v1_v2.previous = v3_v1;
        v2_v3.previous = v1_v2;
        v3_v1.previous = v2_v3;
        v1_v2.twin.next = v1_v2.previous.twin;
        v2_v3.twin.next = v2_v3.previous.twin;
        v3_v1.twin.next = v3_v1.previous.twin;
        v1_v2.twin.previous = v1_v2.next.twin;
        v2_v3.twin.previous = v2_v3.next.twin;
        v3_v1.twin.previous = v3_v1.next.twin;

        // Set the face information correctly, and make sure that the face has a reference back to one of the edges.
        v1_v2.incidentFace = v2_v3.incidentFace = v3_v1.incidentFace = face;
        face.outerComponent = v1_v2;
        v1_v2.twin.incidentFace = v2_v3.twin.incidentFace = v3_v1.twin.incidentFace = Face.outerFace;
        Face.outerFace.outerComponent = v1_v2.twin;
    }

    public void insertVertex(Vertex v) throws PointInsertedInOuterFaceException {
        // First, determine in which face the point is inserted.
        Face face = searcher.findFace(v);

        // If this face is the outer face, something is wrong and we should terminate.
        if(face instanceof Face.OuterFace) {
            throw new PointInsertedInOuterFaceException();
        }

        // Now, we should find out of it is inside of the triangle, or on one of the edges.
        if(face.contains(v) == Triangle2d.Location.INSIDE) {
            // If it is inside, connect the vertex to all points in the face.
            for (Edge edge : face.outerComponent) {

            }
        } else {
            // If it is on an edge, find the two face neighbor of the edge.
            // Delete the original edge, and connect the vertex to all vertices in both faces.
            // TODO keep in mind that we don't want to add the same edge twice.
        }
    }

    public class PointInsertedInOuterFaceException extends Exception {

    }

}
