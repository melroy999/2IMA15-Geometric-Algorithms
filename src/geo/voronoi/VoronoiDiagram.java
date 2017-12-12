package geo.voronoi;

import geo.structure.geo.TriangleFace;
import geo.structure.graph.DAG;
import geo.structure.math.Point2d;

import java.util.HashMap;
import java.util.Set;

/**
 * Class representing a Voronoi diagram, which is represented as a DAG.
 */
public class VoronoiDiagram extends DAG<Point2d> {
    /**
     * Create a Voronoi diagram, based on the faces in the Delaunay triangulation.
     *
     * @param faces The faces to base the Voronoi diagram on.
     */
    public VoronoiDiagram(Set<TriangleFace> faces) {
        // We want to map each face id to the circumcenter point.
        HashMap<Integer, Point2d> faceIdToCircumCenter = new HashMap<>();

    }
}
