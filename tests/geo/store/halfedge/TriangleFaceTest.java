package geo.store.halfedge;

import geo.state.GameState;
import geo.store.math.Point2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TriangleFaceTest {
    @Test
    void containsTest1() {
        // Create a set of edges.
        Edge<TriangleFace> e1 = new Edge<>(new Vertex<>(0, 0, GameState.PlayerTurn.RED), new Vertex<>(1, 0, GameState.PlayerTurn.RED));
        Edge<TriangleFace> e2 = new Edge<>(new Vertex<>(1, 0, GameState.PlayerTurn.RED), new Vertex<>(0, 1, GameState.PlayerTurn.RED));
        Edge<TriangleFace> e3 = new Edge<>(new Vertex<>(0, 1, GameState.PlayerTurn.RED), new Vertex<>(0, 0, GameState.PlayerTurn.RED));
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(0.25, 0.25));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(1.5, 1.5));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(1.0, 0.5));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0.5, 0.5));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e2, result.edge);

        result = face.contains(new Point2d(0.0, 0.5));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e3, result.edge);
    }

    @Test
    void containsTest2() {
        // Create a set of edges.
        Edge<TriangleFace> e1 = new Edge<>(new Vertex<>(0, 0, GameState.PlayerTurn.RED), new Vertex<>(10e9, 0, GameState.PlayerTurn.RED));
        Edge<TriangleFace> e2 = new Edge<>(new Vertex<>(10e9, 0, GameState.PlayerTurn.RED), new Vertex<>(0, 10e9, GameState.PlayerTurn.RED));
        Edge<TriangleFace> e3 = new Edge<>(new Vertex<>(0, 10e9, GameState.PlayerTurn.RED), new Vertex<>(0, 0, GameState.PlayerTurn.RED));
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(0.25 * 10e9, 0.25 * 10e9));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(1.5 * 10e9, 1.5 * 10e9));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(1.0 * 10e9, 0.5 * 10e9));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0.5 * 10e9, 0.5 * 10e9));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e2, result.edge);

        result = face.contains(new Point2d(0.0, 0.5 * 10e9));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e3, result.edge);
    }
}