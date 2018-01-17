package geo.store.halfedge;

import geo.state.GameState;
import geo.store.math.Point2d;
import geo.store.math.Triangle2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TriangleFaceTest {
    @Test
    void containsTest1() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(1, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(0, 1, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check part of initial triangle flags.
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, v3));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, v3));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, v3));

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
        Vertex<TriangleFace> v1 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(10e9, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(0, 10e9, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check part of initial triangle flags.
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, v3));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, v3));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, v3));

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

    @Test
    void containsTest3() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex.SymbolicBottomVertex();
        Vertex<TriangleFace> v2 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(5, 0, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check part of initial triangle flags.
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e1, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e1, v3));
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e2, v3));
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e3, v3));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(0.25d, 0.25d));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(10, 0));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0, 0));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);
    }

    @Test
    void containsTest3b() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex.SymbolicBottomVertex();
        Vertex<TriangleFace> v2 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(846.0,386.0, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(1409.0,418.0));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);
    }

    @Test
    void containsTest4() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(-5, -10, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(5, 0, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check part of initial triangle flags.
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, v3));
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e2, v3));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, v3));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(0.25d, 0.25d));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(100, 1));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(100, -11));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(-100, -11));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(10, 0));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0, 0));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(10, -10));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0, -5));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e3, result.edge);

        result = face.contains(new Point2d(100, -5));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);
    }

    @Test
    void containsTest5() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(-5, -10, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(5, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex.SymbolicBottomVertex();

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        // Check part of initial triangle flags.
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e2, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, new Vertex<>(0, 10e10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e1, v2));
        Assertions.assertTrue(face.isEdgePartOfInitialTriangle(e2, v2));
        Assertions.assertFalse(face.isEdgePartOfInitialTriangle(e3, v2));

        // Check the containment of points.
        TriangleFace.ContainsResult result = face.contains(new Point2d(0.25d, 0.25d));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(100, 1));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(100, -11));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(-100, -11));
        Assertions.assertEquals(TriangleFace.Location.OUTSIDE, result.location);
        Assertions.assertEquals(null, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(-10, 0));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(-10, -10));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0, -5));
        Assertions.assertEquals(TriangleFace.Location.BORDER, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(e1, result.edge);

        result = face.contains(new Point2d(-100, -5));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);

        result = face.contains(new Point2d(0, 0));
        Assertions.assertEquals(TriangleFace.Location.INSIDE, result.location);
        Assertions.assertEquals(face, result.face);
        Assertions.assertEquals(null, result.edge);
    }

    @Test
    void illegalityTest1() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(-5, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(0, -5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(5, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(0, 5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = new Edge<>(v3, v4);
        Edge<TriangleFace> e5 = new Edge<>(v4, v1);
        Edge<TriangleFace> e6 = e3.twin;
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertTrue(face.isEdgeLegal(e3, v4));
        Assertions.assertTrue(face2.isEdgeLegal(e3, v4));
        Assertions.assertTrue(face.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face2.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
    }

    @Test
    void illegalityTest2() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(-10, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(0, -5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(10, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(0, 5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = new Edge<>(v3, v4);
        Edge<TriangleFace> e5 = new Edge<>(v4, v1);
        Edge<TriangleFace> e6 = e3.twin;
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertFalse(face.isEdgeLegal(e3, v4));
        Assertions.assertFalse(face2.isEdgeLegal(e3, v4));
        Assertions.assertFalse(face.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertFalse(face2.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
    }

    @Test
    void illegalityTest3() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(5, -2, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(0, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(-5, -2, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = e3.twin;
        Edge<TriangleFace> e5 = new Edge<>(v3, v4);
        Edge<TriangleFace> e6 = new Edge<>(v4, v1);
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        Edge<TriangleFace> e7 = e6.twin;
        Edge<TriangleFace> e8 = new Edge<>(v4, v2);
        Edge<TriangleFace> e9 = e1.twin;
        TriangleFace face3 = new TriangleFace(Arrays.asList(e7, e8, e9));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertTrue(face.isEdgeLegal(e3, v3));
        Assertions.assertTrue(face2.isEdgeLegal(e4, v3));
        Assertions.assertTrue(face2.isEdgeLegal(e6, v3));
        Assertions.assertTrue(face3.isEdgeLegal(e7, v3));
        Assertions.assertTrue(face.isEdgeLegal(e1, v3));
        Assertions.assertTrue(face3.isEdgeLegal(e9, v3));
        Assertions.assertTrue(face.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face2.isEdgeLegal(e4, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face2.isEdgeLegal(e6, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face3.isEdgeLegal(e7, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face.isEdgeLegal(e1, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face3.isEdgeLegal(e9, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
    }

    @Test
    void illegalityTest4() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(5, 10, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex.SymbolicBottomVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(10, 5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = new Edge<>(v3, v4);
        Edge<TriangleFace> e5 = new Edge<>(v4, v1);
        Edge<TriangleFace> e6 = e3.twin;
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertTrue(face.isEdgeLegal(e3, v1));
        Assertions.assertTrue(face2.isEdgeLegal(e6, v1));
    }

    @Test
    void illegalityTest5() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex.SymbolicBottomVertex();
        Vertex<TriangleFace> v2 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(5, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(5, -5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = e1.twin;
        Edge<TriangleFace> e5 = new Edge<>(v1, v4);
        Edge<TriangleFace> e6 = new Edge<>(v4, v2);
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertFalse(face.isEdgeLegal(e1, v3));
        Assertions.assertFalse(face2.isEdgeLegal(e4, v3));
    }

    @Test
    void illegalityTest6() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v2 = new Vertex<>(5, 10, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v3 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(-10, 5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = new Edge<>(v2, v4);
        Edge<TriangleFace> e5 = new Edge<>(v4, v3);
        Edge<TriangleFace> e6 = e2.twin;
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertTrue(face.isEdgeLegal(e2, v2));
        Assertions.assertTrue(face2.isEdgeLegal(e6, v2));
    }

    @Test
    void illegalityTest7() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(5, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex<>(5, -5, GameState.PlayerTurn.RED);

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = e1.twin;
        Edge<TriangleFace> e5 = new Edge<>(v1, v4);
        Edge<TriangleFace> e6 = new Edge<>(v4, v2);
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertFalse(face.isEdgeLegal(e1, v3));
        Assertions.assertFalse(face2.isEdgeLegal(e4, v3));
    }

    @Test
    void illegalityTest8() {
        // Create a set of edges.
        Vertex<TriangleFace> v1 = new Vertex<>(0, 0, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex.SymbolicTopVertex();
        Vertex<TriangleFace> v3 = new Vertex<>(0, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v4 = new Vertex.SymbolicBottomVertex();

        Edge<TriangleFace> e1 = new Edge<>(v1, v2);
        Edge<TriangleFace> e2 = new Edge<>(v2, v3);
        Edge<TriangleFace> e3 = new Edge<>(v3, v1);
        TriangleFace face = new TriangleFace(Arrays.asList(e1, e2, e3));

        Edge<TriangleFace> e4 = e3.twin;
        Edge<TriangleFace> e5 = new Edge<>(v3, v4);
        Edge<TriangleFace> e6 = new Edge<>(v4, v1);
        TriangleFace face2 = new TriangleFace(Arrays.asList(e4, e5, e6));

        Edge<TriangleFace> e7 = e6.twin;
        Edge<TriangleFace> e8 = new Edge<>(v4, v2);
        Edge<TriangleFace> e9 = e1.twin;
        TriangleFace face3 = new TriangleFace(Arrays.asList(e7, e8, e9));

        // Do the illegality test on one of the edges they have in common, which is e3 and e6.
        Assertions.assertTrue(face.isEdgeLegal(e3, v3));
        Assertions.assertTrue(face2.isEdgeLegal(e4, v3));
        Assertions.assertTrue(face2.isEdgeLegal(e6, v3));
        Assertions.assertTrue(face3.isEdgeLegal(e7, v3));
        Assertions.assertTrue(face.isEdgeLegal(e1, v3));
        Assertions.assertTrue(face3.isEdgeLegal(e9, v3));
        Assertions.assertTrue(face.isEdgeLegal(e3, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face2.isEdgeLegal(e4, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face2.isEdgeLegal(e6, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face3.isEdgeLegal(e7, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face.isEdgeLegal(e1, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
        Assertions.assertTrue(face3.isEdgeLegal(e9, new Vertex<>(0, 10, GameState.PlayerTurn.RED)));
    }

    @Test
    public void TrianglePointCountExceptionTest1() {
        Vertex<TriangleFace> v1 = new Vertex<>(5, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(5, -5, GameState.PlayerTurn.RED);

        assertThrows(TriangleFace.TrianglePointCountException.class,
                ()-> new TriangleFace(Arrays.asList(new Edge<>(v1, v2))));
    }

    @Test
    public void TrianglePointCountExceptionTest2() {
        Vertex<TriangleFace> v1 = new Vertex<>(5, 5, GameState.PlayerTurn.RED);
        Vertex<TriangleFace> v2 = new Vertex<>(5, -5, GameState.PlayerTurn.RED);

        assertThrows(TriangleFace.TrianglePointCountException.class,
                ()-> new TriangleFace(Arrays.asList(new Edge<>(v1, v2), new Edge<>(v1, v2), new Edge<>(v1, v2), new Edge<>(v1, v2))));
    }

}