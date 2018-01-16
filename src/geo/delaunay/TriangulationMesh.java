package geo.delaunay;

import geo.store.halfedge.Edge;
import geo.store.halfedge.TriangleFace;
import geo.store.halfedge.Vertex;
import geo.store.math.Point2d;
import geo.store.math.Triangle2d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
            throw new RuntimeException("Empty mesh?");
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
}
