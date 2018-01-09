package geo.delaunay;

import geo.state.GameState;
import geo.store.halfedge.Edge;
import geo.store.halfedge.Vertex;
import geo.store.math.Triangle2d;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A mesh structure that will hold the Delaunay triangulation.
 */
public class DelaunayMesh {
    // The face hierarchy, such that we can easily find the face that contains a specific point.
    private final FaceHierarchy faceIndex = new FaceHierarchy();

    /**
     * Initialize the triangle mesh, with a very large triangle in the initial state.
     */
    public DelaunayMesh() {
        // Initially, we should have a triangle already of sufficient size.
        Vertex<TriangleFace> v1 = new Vertex.SymbolicVertex<>(-10e6, -10e6);
        Vertex<TriangleFace> v2 = new Vertex.SymbolicVertex<>(10e6, -10e6);
        Vertex<TriangleFace> v3 = new Vertex.SymbolicVertex<>(0, 10e6);

        // Create edges in CCW order.
        Edge<TriangleFace> v1_v2 = new Edge<>(v1, v2);
        Edge<TriangleFace> v2_v3 = new Edge<>(v2, v3);
        Edge<TriangleFace> v3_v1 = new Edge<>(v3, v1);

        // Create a new triangle face with these points as the corners.
        TriangleFace face = new TriangleFace(v1_v2, v2_v3, v3_v1);

        // The outside of this face has not been configured yet.
        v1_v2.twin.setNext(v3_v1.twin);
        v3_v1.twin.setNext(v2_v3.twin);
        v2_v3.twin.setNext(v1_v2.twin);

        // Make sure the outer face points to an edge, v1_v2 suffices.
        TriangleFace.outerFace.outerComponent = v1_v2.twin;

        // Make the outer edges all point to the outer face.
        v1_v2.twin.incidentFace = v2_v3.twin.incidentFace = v3_v1.twin.incidentFace = TriangleFace.outerFace;

        // We have to register this face in the searcher as a root face.
        faceIndex.insertRootFace(face);
    }

    /**
     * Insert a vertex into the mesh, and create edges to all visible surrounding vertices.
     *
     * @param v The vertex that should be inserted.
     * @throws PointInsertedInOuterFaceException If the vertex is contained in the outer face.
     * @throws EdgeNotFoundException If the vertex is on an edge, but the edge cannot be found.
     */
    public void insertVertex(Vertex<TriangleFace> v) throws PointInsertedInOuterFaceException, EdgeNotFoundException {
        // Start by finding the face that contains the vertex.
        TriangleFace face = faceIndex.findFace(v);

        // If this face is the outer face, something is wrong and we should terminate.
        if(face instanceof TriangleFace.OuterTriangleFace) {
            throw new PointInsertedInOuterFaceException(v);
        }

        // Now, we should find out of it is inside of the triangle, or on one of the edges.
        if(face.contains(v) == Triangle2d.Location.INSIDE) {
            // Report.
            System.out.println("Inserting vertex " + v + " into face " + face);

            // Use the insert into inside face insertion.
            insertVertexInsideFace(v, face);
        } else {
            // Find which edge the point is on.
            Optional<Edge<TriangleFace>> edge = face.edges().stream().filter(e -> e.isPointOnEdge(v)).findAny();

            if(!edge.isPresent()) {
                throw new EdgeNotFoundException(v);
            }


            // Report.
            System.out.println("Inserting vertex " + v + " on edge " + edge.get() + " with neighboring faces " + edge.get().incidentFace + " and " + edge.get().twin.incidentFace);

            // Insert the vertex on the edge.
            insertVertexOnEdge(v, edge.get());
        }
    }

    /**
     * Insert a vertex inside the given face.
     *
     * @param v The vertex we want to insert in to the face.
     * @param face The face we want to insert the vertex into.
     */
    private void insertVertexInsideFace(Vertex<TriangleFace> v, TriangleFace face) {
        // Start with finding all the edges that surround the face.
        List<Edge<TriangleFace>> edges = face.edges();

        // Create the new edges we need, edges going from the vertices of the edges in the cycle to the new vertex v.
        List<Edge<TriangleFace>> connectors = edges.stream().map(
                e -> new Edge<>(e.origin, v)).collect(Collectors.toList());

        // Now, construct the faces.
        List<TriangleFace> faces = new ArrayList<>();
        for(int i = 0; i < edges.size(); i++) {
            Edge<TriangleFace> v1_v2 = edges.get(i);
            Edge<TriangleFace> v2_v = connectors.get((i + 1) % edges.size());
            Edge<TriangleFace> v_v1 = connectors.get(i).twin;
            faces.add(new TriangleFace(v1_v2, v2_v, v_v1));
        }

        // Replace the original face by the new faces.
        faceIndex.replaceFaces(Collections.singletonList(face), faces);
    }

    private void insertVertexOnEdge(Vertex<TriangleFace> v, Edge<TriangleFace> edge) {
        // Start with finding the edges surrounding the two faces, of which we have 4.
        List<Edge<TriangleFace>> edges = new ArrayList<>();
        edges.add(edge.next());
        edges.add(edge.next().next());
        edges.add(edge.twin.next());
        edges.add(edge.twin.next().next());

        // Create the new edges we need, edges going from the vertices of the edges in the cycle to the new vertex v.
        List<Edge<TriangleFace>> connectors = edges.stream().map(
                e -> new Edge<>(e.origin, v)).collect(Collectors.toList());

        // Now, construct the faces.
        List<TriangleFace> faces = new ArrayList<>();
        for(int i = 0; i < edges.size(); i++) {
            Edge<TriangleFace> v1_v2 = edges.get(i);
            Edge<TriangleFace> v2_v = connectors.get((i + 1) % edges.size());
            Edge<TriangleFace> v_v1 = connectors.get(i).twin;
            faces.add(new TriangleFace(v1_v2, v2_v, v_v1));
        }

        // Replace the two original faces by the new faces.
        faceIndex.replaceFaces(Collections.singletonList(edge.incidentFace), faces.subList(0,2));
        faceIndex.replaceFaces(Collections.singletonList(edge.twin.incidentFace), faces.subList(2, 4));
    }


    /**
     * Swap the edge in a rectangle to the alternative triangular position.
     *
     * @param e The edge we want to swap out with another edge.
     */
    public void swapEdge(Edge<TriangleFace> e) {
        System.out.println("Swapping edge " + e);

        // First, a sketch of the situation.
        /* We want to replace "e" with an edge from v1 to v2.

                                v1
                              /    \
                             /      \
                           tl        tr
                          /            \
                         v -- e ------- w
                         v -- e.twin -- w
                          \            /
                           bl        br
                             \      /
                              \    /
                                v2
         */

        // Using the names of the vertices as sketched above, we get:
        Vertex<TriangleFace> v1 = e.previous().origin;
        Vertex<TriangleFace> v2 = e.twin.previous().origin;

        // Determine what the neighboring edges will be.
        Edge<TriangleFace> tl = e.previous();
        Edge<TriangleFace> tr = e.next();
        Edge<TriangleFace> bl = e.twin.next();
        Edge<TriangleFace> br = e.twin.previous();

        // Create the new edge.
        Edge<TriangleFace> v2_v1 = new Edge<>(v2, v1);

        // Create the new faces.
        TriangleFace f1 = new TriangleFace(v2_v1, tl, bl);
        TriangleFace f2 = new TriangleFace(v2_v1.twin, br, tr);

        // We replace the original faces with two other faces.
        faceIndex.replaceFaces(Arrays.asList(e.incidentFace, e.twin.incidentFace), Arrays.asList(f1, f2));
    }

    /**
     * An exception for placing a point outside of the initial triangle.
     */
    public class PointInsertedInOuterFaceException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public PointInsertedInOuterFaceException(Vertex v) {
            super(String.format("The vertex %s was inserted outside of the initial triangle, " +
                    "which is unsupported by the Delaunay triangulation algorithm.", v));
        }
    }

    /**
     * An exception that is thrown when an edge cannot be found.
     */
    public class EdgeNotFoundException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public EdgeNotFoundException(Vertex v) {
            super(String.format("Triangle position search reports that the vertex %s " +
                    "is on an edge, but an edge intersecting %s could not be found.", v, v));
        }
    }

    /**
     * Get all the visible faces.
     *
     * @return The faces that are leaves of the DAG and the outer face.
     */
    public Set<TriangleFace> getTriangulatedFaces() {
        return faceIndex.getTriangulatedFaces();
    }
}
