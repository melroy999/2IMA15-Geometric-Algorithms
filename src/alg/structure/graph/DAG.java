package alg.structure.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The graph structure representing a directed acyclic graph.
 */
public class DAG<T> {
    // The DAG might have multiple starting points, represented as a list of nodes.
    protected final List<Node<T>> roots = new ArrayList<>();
}
