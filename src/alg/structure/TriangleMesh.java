package alg.structure;

import alg.FaceSearcher;
import alg.structure.geom.Triangle2d;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import java.util.*;

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

        // Make sure that we have the vertices in our list of vertices.
        vertices.put(v1.id, v1);
        vertices.put(v2.id, v2);
        vertices.put(v3.id, v3);

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

    public void insertVertex(Vertex v) throws PointInsertedInOuterFaceException, EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // First, determine in which face the point is inserted.
        Face face = searcher.findFace(v);

        // If this face is the outer face, something is wrong and we should terminate.
        if(face instanceof Face.OuterFace) {
            throw new PointInsertedInOuterFaceException();
        }

        // Now, we should find out of it is inside of the triangle, or on one of the edges.
        if(face.contains(v) == Triangle2d.Location.INSIDE) {
            // Use the insert into inside face insertion.
            insertVertexInsideFace(v, face);
        } else {
            // Use the insert on edge of face insertion.
            insertVertexOnEdge(v, face);
        }

        // Add the vertex to the list of vertices in the mesh.
        this.vertices.put(v.id, v);
    }

    private void insertVertexInsideFace(Vertex v, Face face) throws FaceSearcher.AlreadyReplacedException {
        // We should track which faces we add, such that we can update our search structure.
        List<Face> faces = new ArrayList<>();

        // Keep a list of vertices that we can use such that we can fix twin references.
        List<Vertex> vertices = new ArrayList<>();

        // Iterate over all the edges in the cycle, such that we can draw all new triangle faces.
        for(Edge e : face.outerComponent) {
            // Create the triangle.
            faces.add(addTriangle(e, v));
            vertices.add(e.origin);

            // Reset the incident edge of e.origin to e, such that we can easily backtrack.
            // Important, since otherwise we cannot set the twin references correctly.
            e.origin.incidentEdge = e;
        }

        // Since we can be certain about the order of insertion (ccw order),
        // we can now use the list of vertices to fix the twin references.
        for(int i = 0; i < vertices.size(); i++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get((i + 1) % vertices.size());

            // we know that v1's incident edge points to v2, and v2 to v3. So use next + previous as twins.
            v1.incidentEdge.next.twin = v2.incidentEdge.previous;
            v2.incidentEdge.previous.twin = v1.incidentEdge.next;
        }

        // Update the search structure.
        searcher.replaceFaces(Collections.singletonList(face), faces);
    }

    private void insertVertexOnEdge(Vertex v, Face face) throws EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // If on the edge, find the two face neighbor of the edge. Connect to all the vertices in the two faces.
        // First we need to find the edge, iterate over all edges in the face and find the edge.
        Edge edge = null;
        for(Edge e : face.outerComponent) {
            if(e.isPointOnEdge(v)) {
                // We now know which edge is our target, so save it.
                edge = e;
                break;
            }
        }

        // If we have not found an edge, throw an error.
        if(edge == null) {
            throw new EdgeNotfoundException();
        }

        // Here, we will attempt to use the original edge for a while, such that we can done some harder twin connects.
        // So make sure that the face outer components are set to the edge we are about to remove.
        edge.incidentFace.outerComponent = edge;
        edge.twin.incidentFace.outerComponent = edge.twin;

        // Obviously, we should keep a reference to both original faces...
        Face f1 = edge.incidentFace;
        Face f2 = edge.twin.incidentFace;

        // Now, we do essentially the same as done in 'insertVertexInsideFace', but now we skip the outer component.
        insertVertexOnEdgeForFace(v, f1);
        insertVertexOnEdgeForFace(v, f2);

        // Now, we need to make sure that the twins are configured correctly for the edge we replaced with two edges.
        Edge v1_v = f1.outerComponent.previous.next;
        Edge v_v2 = f1.outerComponent.next.previous;

        // Set the twins correctly.
        v1_v.twin = f2.outerComponent.next.previous;
        v1_v.twin.twin = v1_v;

        v_v2.twin = f2.outerComponent.previous.next;
        v_v2.twin.twin = v_v2;
    }

    private void insertVertexOnEdgeForFace(Vertex v, Face face) throws FaceSearcher.AlreadyReplacedException {
        // We should track which faces we add, such that we can update our search structure.
        List<Face> faces = new ArrayList<>();

        // Keep a list of vertices that we can use such that we can fix twin references.
        List<Vertex> vertices = new ArrayList<>();

        // Iterate over all the edges in the cycle, such that we can draw all new triangle faces.
        for(Edge e : face.outerComponent) {
            // We skip the edge that will be removed.
            if(e == face.outerComponent) {
                continue;
            }

            // Create the triangle.
            faces.add(addTriangle(e, v));

            // We want to skip adding the vertex when the vertex is the origin of the next of the outer component.
            // This way, we will not have either of the endpoints of e in the list, as they need special care for merge.
            if(e.origin != face.outerComponent.next.origin) {
                vertices.add(e.origin);
            }

            // Reset the incident edge of e.origin to e, such that we can easily backtrack.
            // Important, since otherwise we cannot set the twin references correctly.
            e.origin.incidentEdge = e;
        }

        // Since we can be certain about the order of insertion (ccw order),
        // we can now use the list of vertices to fix the twin references.
        for(int i = 0; i < vertices.size(); i++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get((i + 1) % vertices.size());

            // we know that v1's incident edge points to v2, and v2 to v3. So use next + previous as twins.
            v1.incidentEdge.next.twin = v2.incidentEdge.previous;
            v2.incidentEdge.previous.twin = v1.incidentEdge.next;
        }

        // Update the search structure.
        searcher.replaceFaces(Collections.singletonList(face), faces);
    }

    /**
     * Create a triangle in the mesh, this handles in-triangle neighbor references and new face construction and ref.
     *
     * @param e The edge that will be one side of the triangle.
     * @param v The vertex both ends of e will connect to with a new edge.
     * @return The face associated with the new triangle.
     */
    private Face addTriangle(Edge e, Vertex v) {
        // Instead of already making the twin, we will create face by face.
        // So get the two points that define the edge.
        Vertex v1 = e.origin;
        Vertex v2 = e.next.origin;

        // Create the face we want, and assign e as its designated reference.
        Face face = new Face(v, v1, v2);
        face.outerComponent = e;

        // Since we move in counter clockwise order, the cycle will be: v1 -> v2, v2 -> v, v -> v1.
        // So we need to create v2 -> v, v -> v1, and assign the correct pointers.
        Edge v2_v = new Edge(v2, v);
        Edge v_v1 = new Edge(v, v1);

        // Set the neighbor information.
        e.next = v2_v;
        e.previous = v_v1;
        v2_v.next = v_v1;
        v2_v.previous = e;
        v_v1.next = e;
        v_v1.previous = v2_v;

        // Make sure all edges see the face as its neighbor.
        for(Edge e1 : e) {
            e1.incidentFace = face;
        }

        // Return the face such that we can store it.
        return face;
    }

    /**
     * Find the edge with vertex with id i as origin, moving to vertex j.
     *
     * @param i The id of the first vertex.
     * @param j The id of the second vertex.
     * @return The vertex if it exists, null otherwise.
     */
    private Edge findEdge(int i, int j) throws MissingVertexException {
        // First, check if the vertices exist.
        if(!vertices.containsKey(i) || !vertices.containsKey(j)) {
            throw new MissingVertexException();
        }

        // Fetch the origin vertex.
        Vertex vi = vertices.get(i);
        Vertex vj = vertices.get(j);

        // Iterate over all vertices originating from vi, such that we can find one with endpoint vj.
        for(Edge e : vi) {
            // Get the twin of the edge, as it may have origin vj.
            if(e.twin.origin.id == vj.id) {
                return e;
            }
        }

        // If we found none, return null.
        return null;
    }

    public class PointInsertedInOuterFaceException extends Exception {

    }

    public class EdgeNotfoundException extends Exception {

    }

    public class MissingVertexException extends Exception {

    }

}
