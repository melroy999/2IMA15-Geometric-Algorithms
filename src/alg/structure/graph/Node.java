package alg.structure.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A graph structure representing nodes in a DAG.
 * @param <T> The type of the value of the node.
 */
public class Node<T> {
    // Each node has a value and children.
    public final T value;

    // The list of children.
    public final List<Node<T>> children = new ArrayList<>();

    /**
     * Create a node with the given value.
     *
     * @param value The value to assign to the node.
     */
    public Node(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                ", children=" + children +
                '}';
    }
}
