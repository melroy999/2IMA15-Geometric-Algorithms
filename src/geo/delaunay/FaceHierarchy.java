package geo.delaunay;

import geo.store.graph.DAG;
import geo.store.graph.Node;
import geo.store.math.Point2d;
import geo.store.math.Triangle2d;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A engine for point to face searches in the triangulation structure.
 * We extend the DAG data structure, as we need access to its data.
 */
public class FaceHierarchy extends DAG<TriangleFace> {
    // We keep a mapping of all faces from ids to instances... for easy replacements.
    private final HashMap<Integer, Node<TriangleFace>> idToFaceNode = new HashMap<>();

    /**
     * Insert a new node at the root level.
     */
    public void insertRootFace(TriangleFace face) {
        // Simply add the face to the root.
        Node<TriangleFace> node = new Node<>(face);

        // Add the face to the id map.
        idToFaceNode.put(face.id, node);
        roots.add(node);
    }

    /**
     * Add the replacement faces as children to the original faces.
     *
     * @param original The faces that are currently in the DAG.
     * @param replacement The faces we want to replace the original faces with by making them children of the originals.
     */
    public void replaceFaces(List<TriangleFace> original, List<TriangleFace> replacement) {
        // Lets first convert the replacements to nodes, and add them to the mapping.
        List<Node<TriangleFace>> replacementNodes = replacement.stream().map(Node::new).collect(Collectors.toList());
        replacementNodes.forEach(n -> idToFaceNode.put(n.value.id, n));

        // Now look up the corresponding node references in the mapping.
        for(TriangleFace f : original) {
            // Add the children.
            Node<TriangleFace> node = idToFaceNode.get(f.id);
            if(node != null) node.children.addAll(replacementNodes);
        }
    }

    /**
     * Find the face that contains the given point.
     *
     * @param p The point we want to find.
     * @return The corresponding face if it exists, the outer face otherwise.
     */
    public TriangleFace findFace(Point2d p) {
        // Recursively search through the nodes.
        Node<TriangleFace> face = findFace(f -> f.value.contains(p));
        if(face != null) {
            return face.value;
        }

        face = findFace(f -> f.value.containsAlternative(p));
        if(face != null) {
            return face.value;
        }

        // If not found, we know that it is the outside face, so return the outside face.
        return TriangleFace.outerFace;
    }

    /**
     * Find the face that contains the given point.
     *
     * @return The corresponding face if it exists, the outer face otherwise.
     */
    private Node<TriangleFace> findFace(Function<Node<TriangleFace>, Triangle2d.Location> function) {
        // Recursively search through the nodes.
        for(Node<TriangleFace> node : roots) {
            Node<TriangleFace> hit = findFace(node, function);
            if(hit != null) return hit;
        }

        // If not found, return null.
        return null;
    }

    /**
     * Find a face that contains the given point starting from the given node.
     *
     * @param node The node we want to start the search at.
     * @return The corresponding face if it exists, null otherwise.
     */
    private Node<TriangleFace> findFace(Node<TriangleFace> node, Function<Node<TriangleFace>, Triangle2d.Location> function) {
        // First, check if the point can be in this node, before proceeding checking the children.
        Triangle2d.Location location = function.apply(node);
        if(location != Triangle2d.Location.OUTSIDE) {
            // Check if we have children, if not, this is a leaf node and we return it as a result.
            if(node.children.isEmpty()) {
                return node;
            }

            // Otherwise, iterate over all children and do the same check.
            for(Node<TriangleFace> child : node.children) {
                Node<TriangleFace> hit = findFace(child, function);
                if(hit != null) return hit;
            }
        }

        // If no hits, return null.
        return null;
    }

    /**
     * Get all the visible faces.
     *
     * @return The faces that are leaves of the DAG and the outer face.
     */
    public Set<TriangleFace> getTriangulatedFaces() {
        // The currently active faces are all the leaves of the DAG.
        Set<TriangleFace> faces = getLeaves();
        faces.add(TriangleFace.outerFace);

        return faces;
    }
}
