package alg.structure;

import alg.FaceSearcher;
import alg.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TriangulatorTest {
    @Test
    void insert() throws TriangleMesh.EdgeNotfoundException, FaceSearcher.AlreadyReplacedException, TriangleMesh.MissingVertexException, TriangleMesh.PointInsertedInOuterFaceException {
        // Insert one single point such that we can test a simple case.
        Triangulator t = new Triangulator();

        // Note down the ids of the original lines.
        Integer[] results = t.getMesh().getVertices().values().stream().map(v -> v.id).toArray(Integer[]::new);

        // Make sure that these edges already exist initially...
        for(int i = 0; i < results.length; i++) {
            int k = results[i];
            int l = results[(i + 1) % results.length];
            Assertions.assertNotNull(t.getMesh().findEdge(k, l));
            System.out.println("Before: Edge " + k + " to " + l + " exist.");
        }

        // Do the insertion.
        t.insert(new Point(new java.awt.Point(0, 0)));

        // Now, we want to be sure that there are still edges between all values in results.
        for(int i = 0; i < results.length; i++) {
            int k = results[i];
            int l = results[(i + 1) % results.length];
            Assertions.assertNotNull(t.getMesh().findEdge(k, l), "Edge " + k + " to " + l + " does not exist.");
            System.out.println("After: Edge " + k + " to " + l + " exist.");
        }

        // TODO test fails because insert is not working correctly, the edges are not maintained correctly.
    }

}