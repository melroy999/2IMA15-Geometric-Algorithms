package alg.structure.halfedge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {
    @Test
    void iterator() {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(1, 0);
        Vertex v3 = new Vertex(1, 1);

        Edge v1_v2 = new Edge(v1, v2);
        Edge v2_v3 = new Edge(v2, v3);
        Edge v3_v1 = new Edge(v3, v1);

        v1_v2.next = v2_v3;
        v2_v3.next = v3_v1;
        v3_v1.next = v1_v2;

        // Now check if we iterate over 3 edges.
        ArrayList<Edge> edges = new ArrayList<>();
        v1_v2.iterator().forEachRemaining(edges::add);
        Assertions.assertEquals(3, edges.size());
    }

}