package alg.halfedge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class MeshTest {
    Mesh mesh;

    @BeforeEach
    void Setup() {
        mesh = new Mesh();
    }

    @Test
    void insertVertex() {
        // Setup.
        Point point = new Point(0, 0);

        // Test.
        Vertex vertex = mesh.insertVertex(point);

        // Assert.
        Assertions.assertTrue(mesh.getVertex(vertex.id) != null);
        Assertions.assertTrue(vertex.x == point.x && vertex.y == point.y);
    }

    @Test
    void simpleRemoveVertex() throws Mesh.InnerComponentsNotSupportedException {
        // Setup.
        Point point = new Point(0, 0);
        Vertex vertex = mesh.insertVertex(point);

        // Test.
        Vertex removed = mesh.removeVertex(vertex.id);

        // Assert.
        Assertions.assertTrue(mesh.getVertex(vertex.id) == null);
        Assertions.assertTrue(removed.id == vertex.id);
        Assertions.assertTrue(mesh.getVertices().isEmpty());
    }

    @Test
    void removeEdge() {
    }

}