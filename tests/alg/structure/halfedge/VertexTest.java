package alg.structure.halfedge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {
    public void testIterator(int expectedEdgeCount, Vertex target) {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            int i = 0;
            for(Edge e: target) i++;
            Assertions.assertEquals(expectedEdgeCount, i);
        }, "Infinite loop.");
    }

    @Test
    void emptyIteratorTest() {
        Vertex vertex = new Vertex(0, 0);
        testIterator(0, vertex);
    }

    @Test
    void oneEdgeIteratorTest() {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(1, 1);

        Edge e1 = new Edge(v1, v2);
        Edge e2 = new Edge(v2, v1);

        e1.twin = e2;
        e2.twin = e1;

        e1.next = e1.previous = e2;
        e2.next = e2.previous = e1;

        testIterator(1, v1);
        testIterator(1, v2);
    }

    @Test
    void threeEdgeIteratorTest() {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(2, 0);
        Vertex v3 = new Vertex(1, 1);

        Edge e1_2 = new Edge(v1, v2);
        Edge e2_1 = new Edge(v2, v1);
        Edge e2_3 = new Edge(v2, v3);
        Edge e3_2 = new Edge(v3, v2);
        Edge e3_1 = new Edge(v3, v1);
        Edge e1_3 = new Edge(v1, v3);

        e1_2.twin = e2_1;
        e2_1.twin = e1_2;
        e2_3.twin = e3_2;
        e3_2.twin = e2_3;
        e3_1.twin = e1_3;
        e1_3.twin = e3_1;

        e1_2.next = e2_3;
        e2_3.next = e3_1;
        e3_1.next = e1_2;
        e3_2.next = e2_1;
        e2_1.next = e1_3;
        e1_3.next = e3_2;

        e1_2.previous = e3_1;
        e2_3.previous = e1_2;
        e3_1.previous = e2_3;
        e3_2.previous = e1_3;
        e2_1.previous = e3_2;
        e1_3.previous = e2_1;

        testIterator(2, v1);
        testIterator(2, v2);
        testIterator(2, v3);
    }

}