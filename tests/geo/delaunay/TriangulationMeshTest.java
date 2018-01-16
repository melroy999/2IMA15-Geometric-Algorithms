package geo.delaunay;

import geo.state.GameState;
import geo.store.halfedge.Vertex;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TriangulationMeshTest {
    @Test
    public void ConstructorTest() {
        TriangulationMesh mesh = new TriangulationMesh(Collections.singletonList(new Vertex<>(0, 5, GameState.PlayerTurn.RED)));
    }
}