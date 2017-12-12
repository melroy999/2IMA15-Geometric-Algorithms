package geo.voronoi;

import geo.state.GameState;
import geo.structure.geo.Edge;
import geo.structure.geo.TriangleFace;
import geo.structure.geo.TriangulationMesh;
import geo.structure.geo.Vertex;

import java.awt.*;

/**
 * A class that computes the Delaunay triangulation for a set of points.
 */
public class DelaunayTriangulator {
    // The triangulation will be stored in the triangulation mesh.
    private final TriangulationMesh mesh = new TriangulationMesh();

    /**
     * Insert a vertex into the mesh, and triangulate it.
     *
     * @param v The vertex we want to insert.
     * @throws TriangulationMesh.PointInsertedInOuterFaceException If the point is contained in the outer face.
     * @throws TriangulationMesh.EdgeNotFoundException If the point is on an edge, but the edge cannot be found.
     */
    public void insert(Vertex<TriangleFace> v) throws TriangulationMesh.EdgeNotFoundException,
            TriangulationMesh.PointInsertedInOuterFaceException {

        // Insert the point into the triangle mesh.
        mesh.insertVertex(v);

        // Now, legalize all the edges on the opposite side of v in the triangles surrounding v.
        for(Edge<TriangleFace> e : v) {
            // Legalize the edge e.next, which is the edge opposing v.
            legalizeEdge(e.next());
        }
    }

    /**
     * Legalize the given edge, if illegal.
     *
     * @param e The edge we want to legalize.
     */
    private void legalizeEdge(Edge<TriangleFace> e) {
        /* The situation is as follows

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

        // If the edge is illegal, swap it.
        if(e.incidentFace.isIllegal(e)) {
            // Swappy.
            mesh.swapEdge(e);

            // Now, we want to legalize edges bl and br.
            legalizeEdge(e.twin.next());
            legalizeEdge(e.twin.previous());
        }
    }

    /**
     * Get the mesh the triangulator is drawing in.
     *
     * @return The triangulation mash.
     */
    public TriangulationMesh getMesh() {
        return mesh;
    }
}
