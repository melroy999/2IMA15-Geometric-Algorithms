package alg.structure;

import alg.FaceSearcher;
import alg.structure.geom.Triangle2d;
import alg.structure.halfedge.Edge;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;

import javax.naming.OperationNotSupportedException;
import java.util.*;

/**
 * A mesh consisting of half-edge components, where the faces are only allowed to be triangles.
 */
public class TriangleMesh {
    // The mesh also tracks the face searcher.
    private FaceSearcher searcher = new FaceSearcher();

    // List of vertices that are currently in the mesh.
    private final HashMap<Integer, Vertex> vertices = new HashMap<>();

    public FaceSearcher getSearcher() {
        return searcher;
    }

    public HashMap<Integer, Vertex> getVertices() {
        return vertices;
    }

    /**
     * Initialize the triangle mesh, which includes the creation of a very large initial triangle for Delauney.
     */
    public TriangleMesh() {
//        // Initially, we should have a triangle already of sufficient size.
//        Vertex v1 = new Vertex(Integer.MIN_VALUE / 10, Integer.MIN_VALUE / 10);
//        Vertex v2 = new Vertex(Integer.MAX_VALUE / 10, Integer.MIN_VALUE / 10);
//        Vertex v3 = new Vertex(0, Integer.MAX_VALUE / 10);
        Vertex v1 = new Vertex(10, 10);
        Vertex v2 = new Vertex(1910, 10);
        Vertex v3 = new Vertex(960, 900);

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
        Edge v1_v3 = new Edge(v1, v3);
        Edge v2_v1 = new Edge(v2, v1);
        Edge v3_v2 = new Edge(v3, v2);

        Edge v1_v2 = new Edge(v1, v2);
        v1_v2.twin = v2_v1;
        v1_v2.twin.twin = v1_v2;

        Edge v2_v3 = new Edge(v2, v3);
        v2_v3.twin = v3_v2;
        v2_v3.twin.twin = v2_v3;

        Edge v3_v1 = new Edge(v3, v1);
        v3_v1.twin = v1_v3;
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

    public void swapEdge(Edge e) throws MissingVertexException {
        // Derive the vertices we want to draw an edge between, using the edge input.
        // Since we use CCW, the point "above" the line is the origin of the previous line.
        // The one "below" it, is the origin of the previous edge of the twin edge of the edge.
        Vertex v1 = e.previous.origin;
        Vertex v2 = e.twin.previous.origin;
        Vertex v = e.origin;
        Vertex w = e.twin.origin;

        // Print what we are doing.
        System.out.println("Replacing edge " + v.id + " to " + w.id + " with " + v2.id + " to " + v1.id + ".");

        // Swap the edge e with the edge v1 to v2. First create the two new edges we want to use.
        Edge v1_v2 = new Edge(v1, v2);
        v1_v2.twin = new Edge(v2, v1);
        v1_v2.twin.twin = v1_v2;

        /*
         We want to replace "e" with an edge from v1 to v2.

                v1
              /    \
             /      \
           tl        tr
          /            \
         v --- edge --- w
         v -- edge.t -- w
          \            /
           bl        br
             \      /
              \    /
                v2
         */

        // Determine what the neighboring edges will be.
        Edge tl = e.previous;
        Edge tr = e.next;
        Edge bl = e.twin.next;
        Edge br = e.twin.previous;

        // Using the diagram above, we have the following neighbor assignments:
        tl.next = bl;
        bl.previous = tl;
        bl.next = v1_v2.twin;
        v1_v2.twin.previous = bl;
        v1_v2.twin.next = tl;
        tl.previous = v1_v2.twin;

        br.next = tr;
        tr.previous = br;
        tr.next = v1_v2;
        v1_v2.previous = tr;
        v1_v2.next = br;
        br.previous = v1_v2;

        // Now, do the face swaps and new face assignments!
        Face f1 = new Face(v1, v, v2);
        Face f2 = new Face(v1, v2, w);
        f1.outerComponent = v1_v2.twin;
        f2.outerComponent = v1_v2;

        for(Edge edge : f1.outerComponent) {
            edge.incidentFace = f1;
        }

        for(Edge edge : f2.outerComponent) {
            edge.incidentFace = f2;
        }

        // We replace the original faces with two other faces.
        searcher.replaceFaces(Arrays.asList(e.incidentFace, e.twin.incidentFace), Arrays.asList(f1, f2));
    }

    /**
     * Insert a vertex into the mesh. Here, edges to all visible vertices are created automatically.
     *
     * @param v The vertex we want to insert.
     * @throws EdgeNotfoundException It may occur that a vertex is on a line, but we cannot find the line.
     * @throws PointInsertedInOuterFaceException Since we start with a large triangle,
     * we should not encounter insertions that are outside of the initial triangle face.
     * Since we do not support that, throw an exception.
     */
    public void insertVertex(Vertex v) throws PointInsertedInOuterFaceException, EdgeNotfoundException {
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

    /**
     * Insert a vertex inside of the given face.
     *
     * @param v The vertex we want to add.
     * @param face The face we want to insert the vertex into.
     */
    private void insertVertexInsideFace(Vertex v, Face face) {
        // First, lets find all the vertices we will need.
        Edge v0_v1 = face.outerComponent;
        Vertex v0 = v0_v1.origin;
        Edge v1_v2 = v0_v1.next;
        Vertex v1 = v1_v2.origin;
        Edge v2_v0 = v1_v2.next;
        Vertex v2 = v2_v0.origin;

        // We know that we will create 3 faces. Create them already.
        Face v0v1v = new Face(v0, v1, v);
        v0v1v.outerComponent = v0_v1;
        Face v1v2v = new Face(v1, v2, v);
        v1v2v.outerComponent = v1_v2;
        Face v2v0v = new Face(v2, v0, v);
        v2v0v.outerComponent = v2_v0;

        // Create all the edge pairs.
        Edge v0_v = new Edge(v0, v);
        Edge v1_v = new Edge(v1, v);
        Edge v2_v = new Edge(v2, v);

        // Do the twins.
        v0_v.setTwin(new Edge(v, v0));
        v1_v.setTwin(new Edge(v, v1));
        v2_v.setTwin(new Edge(v, v2));

        // Register next references.
        v0_v1.setNext(v1_v);
        v1_v.setNext(v0_v.twin);
        v0_v.twin.setNext(v0_v1);

        v1_v2.setNext(v2_v);
        v2_v.setNext(v1_v.twin);
        v1_v.twin.setNext(v1_v2);

        v2_v0.setNext(v0_v);
        v0_v.setNext(v2_v.twin);
        v2_v.twin.setNext(v2_v0);

        // Make sure that all edges point to the correct face.
        for(Edge e : v0v1v.outerComponent) {
            e.incidentFace = v0v1v;
        }
        for(Edge e : v1v2v.outerComponent) {
            e.incidentFace = v1v2v;
        }
        for(Edge e : v2v0v.outerComponent) {
            e.incidentFace = v2v0v;
        }

        // Update the search structure.
        searcher.replaceFaces(Collections.singletonList(face), Arrays.asList(v1v2v, v0v1v, v2v0v));
    }

    /**
     * Insert a vertex on an edge of the given face.
     *
     * @param v The vertex we want to add.
     * @param face The face that contains the edge we want to insert the vertex onto.
     * @throws EdgeNotfoundException It may occur that a vertex is on a line, but we cannot find the line.
     */
    private void insertVertexOnEdge(Vertex v, Face face) throws EdgeNotfoundException {
        throw new UnsupportedOperationException();
//
//        // If on the edge, find the two face neighbor of the edge. Connect to all the vertices in the two faces.
//        // First we need to find the edge, iterate over all edges in the face and find the edge.
//        Edge edge = null;
//        for(Edge e : face.outerComponent) {
//            if(e.isPointOnEdge(v)) {
//                // We now know which edge is our target, so save it.
//                edge = e;
//                break;
//            }
//        }
//
//        // If we have not found an edge, throw an error.
//        if(edge == null) {
//            throw new EdgeNotfoundException();
//        }
//
//        // Here, we will attempt to use the original edge for a while, such that we can done some harder twin connects.
//        // So make sure that the face outer components are set to the edge we are about to remove.
//        edge.incidentFace.outerComponent = edge;
//        edge.twin.incidentFace.outerComponent = edge.twin;
//
//        // Obviously, we should keep a reference to both original faces...
//        Face f1 = edge.incidentFace;
//        Face f2 = edge.twin.incidentFace;
//
//        // Now, we do essentially the same as done in 'insertVertexInsideFace', but now we skip the outer component.
//        insertVertexOnEdgeForFace(v, f1);
//        insertVertexOnEdgeForFace(v, f2);
//
//        // Now, we need to make sure that the twins are configured correctly for the edge we replaced with two edges.
//        Edge v1_v = f1.outerComponent.previous.next;
//        Edge v_v2 = f1.outerComponent.next.previous;
//
//        // Set the twins correctly.
//        v1_v.twin = f2.outerComponent.next.previous;
//        v1_v.twin.twin = v1_v;
//
//        v_v2.twin = f2.outerComponent.previous.next;
//        v_v2.twin.twin = v_v2;
    }

    /**
     * Find the edge with vertex with id i as origin, moving to vertex j.
     *
     * @param i The id of the first vertex.
     * @param j The id of the second vertex.
     * @return The vertex if it exists, null otherwise.
     */
    public Edge findEdge(int i, int j) throws MissingVertexException {
        // First, check if the vertices exist.
        if(!vertices.containsKey(i) || !vertices.containsKey(j)) {
            throw new MissingVertexException();
        }

        // Fetch the origin vertex.
        Vertex vi = vertices.get(i);
        Vertex vj = vertices.get(j);

        // Find the edge.
        return findEdge(vi, vj);
    }

    /**
     * Find the edge with vertex with id i as origin, moving to vertex j.
     *
     * @param v1 The id first vertex.
     * @param v2 The id second vertex.
     * @return The vertex if it exists, null otherwise.
     */
    private Edge findEdge(Vertex v1, Vertex v2) {
        // Iterate over all vertices originating from vi, such that we can find one with endpoint vj.
        for(Edge e : v1) {
            // Get the twin of the edge, as it may have origin vj.
            if(e.twin.origin.id == v2.id) {
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
