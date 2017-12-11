package geo.structure.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A graph structure representing nodes in a DAG.
 * @param <T> The type of the value of the node.
 */
public class Node<T> {
    // Each node has a value and children.
    public final T value;

    // The list of children.
    public final List<Node<T>> children = new ArrayList<>();

    // We give each node an id, such that we can easily check for already visited conditions.
    private static int counter = 0;
    private final int id;

    /**
     * Create a node with the given value.
     *
     * @param value The value to assign to the node.
     */
    public Node(T value) {
        this.value = value;

        // Assign a new id.
        id = counter++;
    }

    /**
     * Get the leaves of this node.
     *
     * @param leaves The current list of leaves.
     * @param visited The ids of the nodes we have already encountered.
     */
    void getLeaves(Set<T> leaves, Set<Integer> visited) {
        // We should skip the check if we already visited this node. Otherwise, proceed.
        if(!visited.contains(id)) {
            // Mark it as visited.
            visited.add(id);

            // Check if a leaf, and take action accordingly.
            if(children.isEmpty()) {
                leaves.add(value);
            } else {
                // Otherwise, proceed to children.
                for(Node<T> child : children) {
                    child.getLeaves(leaves, visited);
                }
            }
        }
    }

    /**
     * Get the string representation of the node.
     *
     * @return A string representation of the node.
     */
    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                ", children=" + children +
                '}';
    }
}
