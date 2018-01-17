package geo.delaunay;

import geo.store.graph.DAG;
import geo.store.graph.Node;
import geo.store.halfedge.TriangleFace;
import geo.store.math.Point2d;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A engine for point to face searches in the triangulation structure.
 * We extend the DAG data structure, as we need access to its data.
 */
public class FaceLocator extends DAG<TriangleFace> {
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
     * @return The corresponding face if it exists, the outer face otherwise.
     */
    public TriangleFace.ContainsResult findFace(Point2d p) {
        // Recursively search through the nodes.
        for(Node<TriangleFace> node : roots) {
            TriangleFace.ContainsResult hit = findFace(node, p);
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
    private TriangleFace.ContainsResult findFace(Node<TriangleFace> node, Point2d p) {
        // First, check if the point can be in this node, before proceeding checking the children.
        TriangleFace.ContainsResult result = node.value.contains(p);

        if(result.location != TriangleFace.Location.OUTSIDE) {
            // Check if we have children, if not, this is a leaf node and we return it as a result.
            if(node.children.isEmpty()) {
                return result;
            }

            // Otherwise, iterate over all children and do the same check.
            for(Node<TriangleFace> child : node.children) {
                TriangleFace.ContainsResult hit = findFace(child, p);
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
        return getLeaves();
    }
}
