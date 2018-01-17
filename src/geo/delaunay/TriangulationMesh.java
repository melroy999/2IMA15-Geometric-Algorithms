package geo.delaunay;

import geo.store.halfedge.Edge;
import geo.store.halfedge.TriangleFace;
import geo.store.halfedge.Vertex;
import geo.store.math.Triangle2d;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * The mesh in which we store the Delaunay triangulation.
 */
public class TriangulationMesh {
    // The mesh requires a search structure, which is the face locator.
    private final FaceLocator searcher = new FaceLocator();

    // The symbolic points, including p0.
    private Vertex<TriangleFace> p0;
    private Vertex<TriangleFace> top;
    private Vertex<TriangleFace> bottom;

    /**
     * Initialize the triangle mesh, using the method described in the book on page 210.
     *
     * @param points The points that will be stored in the triangulation, used to find the highest point.
     */
    public TriangulationMesh(List<Vertex<TriangleFace>> points) {
        // Check if we have points to work with.
        if(points.isEmpty()) {
            throw new RuntimeException("Empty initial mesh? Seriously?");
        }

        // Find the highest point, which will be one of the corner points.
        p0 = points.stream().max(Triangle2d.heightComparator).get();

        // Create the infinity points.
        top = new Vertex.SymbolicTopVertex();
        bottom = new Vertex.SymbolicBottomVertex();

        // Create the edges in the right order.
        Edge<TriangleFace> e1 = new Edge<>(p0, bottom);
        Edge<TriangleFace> e2 = new Edge<>(bottom, top);
        Edge<TriangleFace> e3 = new Edge<>(top, p0);

        // Create the initial triangle.
        TriangleFace t = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Register the face in the searcher.
        searcher.insertRootFace(t);
    }

    /**
     * Insert a vertex into the mesh, and create edges to all visible surrounding vertices.
     *
     * @param v The vertex that should be inserted.
     */
    public void insertVertex(Vertex<TriangleFace> v) {
        // Check if v is equal to the p0, as we don't want to add it twice.
        if(v == p0) return;

        System.out.println("Attempting insertion of point " + v);

        // First, we should determine where to insert the vertex.
        TriangleFace.ContainsResult result = searcher.findFace(v);

        if(result.location == TriangleFace.Location.INSIDE) {
            // No doubt that we can insert it inside the face.
            insertVertexInFace(v, result.face);
        } else if(result.location == TriangleFace.Location.BORDER) {
            insertVertexOnEdge(v, result.edge);
        } else {
            throw new RuntimeException("Received a report that the point " + v + " is not in any of the triangles.");
        }

        // Legalize when we are done with insertions.
        legalize(v);
    }

    /**
     * Insert a vertex inside the given face.
     *
     * @param v The vertex we want to insert in to the face.
     * @param face The face we want to insert the vertex into.
     */
    public void insertVertexInFace(Vertex<TriangleFace> v, TriangleFace face) {
        System.out.println("Inserting " + v + " into the face " + face);

        // We simply have to create edges to all surrounding vertices in the face, making sure we do it in CCW order.
        createNewFaces(v, face.edges(), Collections.singletonList(face));
        System.out.println();
    }

    public void insertVertexOnEdge(Vertex<TriangleFace> v, Edge<TriangleFace> edge) {
        System.out.println("Inserting " + v + " onto the edge " + edge + " bordering " + edge.incidentFace + " and " + edge.twin.incidentFace);

        // First, find the two faces, and the edges they have.
        List<TriangleFace> faces = Arrays.asList(
                (TriangleFace) edge.incidentFace,
                (TriangleFace) edge.twin.incidentFace
        );

        // The edges, added to a list.
        List<Edge<TriangleFace>> edges = Arrays.asList(
                edge.next(),
                edge.next().next(),
                edge.twin.next(),
                edge.twin.next().next()
        );

        // Create the faces and edges.
        createNewFaces(v, edges, faces);
        System.out.println();
    }

    /**
     * Create the new faces that the insertion creates.
     *
     * @param v The vertex to connect the edges to.
     * @param edges The edges, in CCW order, which will be at the opposide side of v for all new triangles.
     * @param originalFaces The original faces, a list of side 1 or 2, having the same order as the edges.
     */
    private void createNewFaces(Vertex<TriangleFace> v, List<Edge<TriangleFace>> edges, List<TriangleFace> originalFaces) {
        System.out.println("Converting the faces " + originalFaces + " to faces having the corner edges " + edges);

        // First, make sure that all the edges have their next and previous pointers correct.
        for(int i = 0; i < edges.size(); i++) {
            // Set the pointers.
            edges.get(i).setNext(edges.get((i + 1) % edges.size()));
        }

        // Now, create the triangles in the order defined by the edges list, which should help with face replacement.
        // Create the new edges first, such that we can correctly use the twin references.
        List<Edge<TriangleFace>> connectors = new ArrayList<>();
        for (Edge<TriangleFace> edge : edges) {
            // We should be careful here. In essence, we want the first edge created here to be the origin of the twin.
            // However, we cannot be sure whether that pointer is correct, so use next instead.
            connectors.add(new Edge<>(edge.next().origin, v));
        }

        // Now, create the new faces, using the new list of connectors.
        List<TriangleFace> faces = new ArrayList<>();
        for(int i = 0; i < edges.size(); i++) {
            // Create a new face, taking care it is in CCW order.
            faces.add(new TriangleFace(Arrays.asList(edges.get(i), connectors.get(i), connectors.get((i - 1 + edges.size()) % edges.size()).twin)));
        }

        // Do the face replacements, depending on the side of the original faces list.
        if(originalFaces.size() == 1) {
            // We replace one face by the three new faces.
            searcher.replaceFaces(originalFaces, faces);
            System.out.println("Replaced the face " + originalFaces.get(0) + " by the faces " + faces);
        } else {
            // We replace the first face by the first two new faces, and the other by the latter two.
            searcher.replaceFaces(originalFaces.subList(0, 1), faces.subList(0, 2));
            System.out.println("Replaced the face " + originalFaces.get(0) + " by the faces " + faces.subList(0, 2));
            searcher.replaceFaces(originalFaces.subList(1, 2), faces.subList(2, 4));
            System.out.println("Replaced the face " + originalFaces.get(1) + " by the faces " + faces.subList(2, 4));
        }
    }

    /**
     * Swap the given edge.
     *
     * @param e The edge we want to swap.
     */
    private void swapEdge(Edge<TriangleFace> e) {
        System.out.println("Swapping edge " + e + " concerning faces " + e.incidentFace + " and " + e.twin.incidentFace);
        System.out.println();

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
        TriangleFace f1 = new TriangleFace(Arrays.asList(v2_v1, tl, bl));
        TriangleFace f2 = new TriangleFace(Arrays.asList(v2_v1.twin, br, tr));

        // We replace the original faces with two other faces.
        searcher.replaceFaces(Arrays.asList((TriangleFace) e.incidentFace, (TriangleFace) e.twin.incidentFace), Arrays.asList(f1, f2));
    }

    /**
     * Legalize the newly inserted vertex.
     *
     * @param v The vertex of which we want to legalize the surrounding edges.
     */
    private void legalize(Vertex<TriangleFace> v) {
        // We want to legalize all the edges opposite of the vertex v. Which is always the next edge.
        for (Edge<TriangleFace> edge : v.edges()) {
            legalizeEdge(edge.next());
        }
    }

    /**
     * Legalize the given edge, if illegal.
     *
     * @param edge The edge we want to legalize.
     */
    private void legalizeEdge(Edge<TriangleFace> edge) {
        if (!((TriangleFace) edge.incidentFace).isEdgeLegal(edge, p0)) {
            // If the edge is not legal, we will have to legalize it.
            swapEdge(edge);

            // Legalize the edges that might be affected, at the opposite side of the change.
            legalizeEdge(edge.twin.next());
            legalizeEdge(edge.twin.previous());
        }
    }
}
