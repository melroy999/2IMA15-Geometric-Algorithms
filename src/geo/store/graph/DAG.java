package geo.store.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The graph structure representing a directed acyclic graph.
 */
public class DAG<T> {
    // The DAG might have multiple starting points, represented as a edges of nodes.
    protected final List<Node<T>> roots = new ArrayList<>();

    /**
     * Get all the leaves in the DAG.
     *
     * @return The leaves of the DAG.
     */
    public Set<T> getLeaves() {
        // Create a structure that will contain the values in the leaves.
        Set<T> leaves = new HashSet<>();

        // Now, iterate over all the roots, and get the leaves.
        for(Node<T> root : roots) {
            root.getLeaves(leaves, new HashSet<>());
        }

        // Return the resulting leaves.
        return leaves;
    }

    /**
     * Get the string representation of the DAG.
     *
     * @return A string representation of the DAG.
     */
    @Override
    public String toString() {
        return "DAG{" +
                "roots=" + roots +
                '}';
    }
}
