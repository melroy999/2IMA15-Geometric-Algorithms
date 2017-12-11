package geo.structure.geo;

import geo.log.GeoLogger;
import geo.structure.math.Triangle2d;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A mesh structure that will hold the Delaunay triangulation.
 */
public class TriangulationMesh {
    // The face searcher is part of the triangulation mesh.
    private final FaceSearcher searcher = new FaceSearcher();

    // Create a log for the triangulation mesh.
    private final Logger log = GeoLogger.getLogger(TriangulationMesh.class.getName());

    /**
     * Initialize the triangle mesh, with a very large triangle in the initial state.
     */
    public TriangulationMesh() {
        // Log that we created a new triangulation mesh.
        log.info("Created new triangulation mesh.");

        // Initially, we should have a triangle already of sufficient size.
        Vertex v1 = new Vertex.SymbolicVertex(-10e6, -10e6);
        Vertex v2 = new Vertex.SymbolicVertex(10e6, -10e6);
        Vertex v3 = new Vertex.SymbolicVertex(0, 10e6);

//        Vertex v1 = new Vertex(10, 10);
//        Vertex v2 = new Vertex(1910, 10);
//        Vertex v3 = new Vertex(960, 900);

        // Create edges in CCW order.
        Edge v1_v2 = new Edge(v1, v2);
        Edge v2_v3 = new Edge(v2, v3);
        Edge v3_v1 = new Edge(v3, v1);

        // Create a new triangle face with these points as the corners.
        Face face = new Face(v1_v2, v2_v3, v3_v1);

        // The outside of this face has not been configured yet.
        v1_v2.twin.setNext(v3_v1.twin);
        v3_v1.twin.setNext(v2_v3.twin);
        v2_v3.twin.setNext(v1_v2.twin);

        // Make sure the outer face points to an edge, v1_v2 suffices.
        Face.outerFace.outerComponent = v1_v2.twin;

        // Make the outer edges all point to the outer face.
        v1_v2.twin.incidentFace = v2_v3.twin.incidentFace = v3_v1.twin.incidentFace = Face.outerFace;

        // We have to register this face in the searcher as a root face.
        searcher.insertRootFace(face);
    }

    /**
     * Insert a vertex into the mesh, and create edges to all visible surrounding vertices.
     *
     * @param v The vertex that should be inserted.
     * @throws PointInsertedInOuterFaceException If the vertex is contained in the outer face.
     * @throws EdgeNotFoundException If the vertex is on an edge, but the edge cannot be found.
     */
    public void insertVertex(Vertex v) throws PointInsertedInOuterFaceException, EdgeNotFoundException {
        // Start by finding the face that contains the vertex.
        Face face = searcher.findFace(v);

        // If this face is the outer face, something is wrong and we should terminate.
        if(face instanceof Face.OuterFace) {
            throw new PointInsertedInOuterFaceException(v);
        }

        // Now, we should find out of it is inside of the triangle, or on one of the edges.
        if(face.contains(v) == Triangle2d.Location.INSIDE) {
            // Log that we are inserting a vertex inside of a face.
            log.info(String.format("Inserting vertex %s into face %s.", v, face));

            // Use the insert into inside face insertion.
            insertVertexInsideFace(v, face);
        } else {
            // Find which edge the point is on.
            Optional<Edge> edge = face.edges().stream().filter(e -> e.isPointOnEdge(v)).findAny();

            if(!edge.isPresent()) {
                throw new EdgeNotFoundException(v);
            }

            // Log that we are inserting a vertex inside of a face.
            log.info(String.format("Inserting vertex %s on the edge %s in faces %s and %s.",
                    v, edge.get(), face, edge.get().twin.incidentFace));

            // Use the insert on edge of face insertion.
            throw new NotImplementedException();
        }
    }

    /**
     * Insert a vertex inside the given face.
     *
     * @param v The vertex we want to insert in to the face.
     * @param face The face we want to insert the vertex into.
     */
    private void insertVertexInsideFace(Vertex v, Face face) {
        // Start with finding all the edges that surround the face.
        List<Edge> edges = face.edges();

        // Create the new edges we need, edges going from the vertices of the edges in the cycle to the new vertex v.
        List<Edge> connectors = edges.stream().map(e -> new Edge(e.origin, v)).collect(Collectors.toList());

        // Now, construct the faces.
        List<Face> faces = new ArrayList<>();
        for(int i = 0; i < edges.size(); i++) {
            Edge v1_v2 = edges.get(i);
            Edge v2_v = connectors.get((i + 1) % edges.size());
            Edge v_v1 = connectors.get(i).twin;
            faces.add(new Face(v1_v2, v2_v, v_v1));
        }

        // Replace the original face by the new faces.
        searcher.replaceFaces(Collections.singletonList(face), faces);
    }


    /**
     * Swap the edge in a rectangle to the alternative triangular position.
     *
     * @param e The edge we want to swap out with another edge.
     */
    public void swapEdge(Edge e) {
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
        Vertex v1 = e.previous().origin;
        Vertex v2 = e.twin.previous().origin;

        // Determine what the neighboring edges will be.
        Edge tl = e.previous();
        Edge tr = e.next();
        Edge bl = e.twin.next();
        Edge br = e.twin.previous();

        // Create the new edge.
        Edge v2_v1 = new Edge(v2, v1);

        // Print what we are doing.
        log.info(String.format("Swapping the edge %s with the edge %s.", e, v2_v1));

        // Create the new faces.
        Face f1 = new Face(v2_v1, tl, bl);
        Face f2 = new Face(v2_v1.twin, br, tr);

        // We replace the original faces with two other faces.
        searcher.replaceFaces(Arrays.asList(e.incidentFace, e.twin.incidentFace), Arrays.asList(f1, f2));
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

            log.severe(String.format("PointInsertedInOuterFaceException: The vertex %s was inserted outside " +
                    "of the initial triangle, which is unsupported by the Delaunay triangulation algorithm.", v));
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

            log.severe(String.format("EdgeNotFoundException: Triangle position search reports that " +
                    "the vertex %s is on an edge, but an edge intersecting %s could not be found.", v, v));
        }
    }

    /**
     * Get the face searcher.
     *
     * @return The face searcher.
     */
    public FaceSearcher getSearcher() {
        return searcher;
    }
}
