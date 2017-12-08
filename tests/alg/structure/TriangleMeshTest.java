package alg.structure;

import alg.FaceSearcher;
import alg.structure.geom.Point2d;
import alg.structure.halfedge.Face;
import alg.structure.halfedge.Vertex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TriangleMeshTest {
    @Test
    void constructorTest() {
        // We have to make sure that the initial state has a large triangle in it.
        TriangleMesh mesh = new TriangleMesh();
        Face face = mesh.getSearcher().findFace(new Point2d(0, 0));

        // The face should not be null, as this would indicate that it is in the outer face.
        Assertions.assertFalse(face instanceof Face.OuterFace);

        // Make sure that the corner points are correct.
        for(Vertex v : mesh.getVertices().values()) {
            Assertions.assertNotNull(v.incidentEdge);
            Assertions.assertNotNull(v.incidentEdge.incidentFace);
            Assertions.assertFalse(v.incidentEdge.incidentFace instanceof Face.OuterFace);
            Assertions.assertNotNull(v.incidentEdge.twin);
            Assertions.assertNotNull(v.incidentEdge.twin.incidentFace);
            Assertions.assertTrue(v.incidentEdge.twin.incidentFace instanceof Face.OuterFace);
        }
    }

    @Test
    void insertVertexInside() throws TriangleMesh.PointInsertedInOuterFaceException, TriangleMesh.EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // Simple test where we insert a vertex into the large triangle.
        TriangleMesh mesh = new TriangleMesh();
        Vertex v = new Vertex(0, 0);
        mesh.insertVertex(v);

        // Assert that we have three faces around the vertex.
        ArrayList<Face> faces = new ArrayList<>();
        v.iterator().forEachRemaining(e -> faces.add(e.incidentFace));

        Assertions.assertEquals(3, faces.size());

        // TODO more testing...
    }

    @Test
    void insertVertexOnEdge() throws TriangleMesh.PointInsertedInOuterFaceException, TriangleMesh.EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // We need two insertions.
        TriangleMesh mesh = new TriangleMesh();
        Vertex v = new Vertex(0, 0);
        mesh.insertVertex(v);
        Vertex v2 = new Vertex(0, 10);
        mesh.insertVertex(v2);

        // Assert that we have three faces around the vertex.
        ArrayList<Face> faces = new ArrayList<>();
        v2.iterator().forEachRemaining(e -> faces.add(e.incidentFace));

        Assertions.assertEquals(4, faces.size());
    }

    @Test
    void insertVertexInside2() throws TriangleMesh.PointInsertedInOuterFaceException, TriangleMesh.EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // We need two insertions.
        TriangleMesh mesh = new TriangleMesh();
        Vertex v = new Vertex(0, 0);
        mesh.insertVertex(v);
        Vertex v2 = new Vertex(1, 1);
        mesh.insertVertex(v2);

        // Assert that we have three faces around the vertex.
        ArrayList<Face> faces = new ArrayList<>();
        v2.iterator().forEachRemaining(e -> faces.add(e.incidentFace));

        Assertions.assertEquals(3, faces.size());
    }


    @Test
    void insertVertexOutsideBoundary() throws TriangleMesh.PointInsertedInOuterFaceException, TriangleMesh.EdgeNotfoundException, FaceSearcher.AlreadyReplacedException {
        // Simple test where we insert a vertex into the large triangle.
        TriangleMesh mesh = new TriangleMesh();

        try {
            mesh.insertVertex(new Vertex(Integer.MAX_VALUE, Integer.MAX_VALUE));
            Assertions.fail("Expected exception not thrown.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof TriangleMesh.PointInsertedInOuterFaceException);
        }
    }
}