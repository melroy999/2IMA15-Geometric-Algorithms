package geo.structure.geo;

import geo.structure.graph.DAG;
import geo.structure.graph.Node;
import geo.structure.math.Point2d;
import geo.structure.math.Triangle2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A engine for point to face searches in the triangulation structure.
 * We extend the DAG data structure, as we need access to its data.
 */
public class FaceSearcher extends DAG<TriangleFace> {
    /**
     * Insert a new node at the root level.
     */
    public void insertRootFace(TriangleFace face) {
        // Simply add the face to the root.
        roots.add(new Node<>(face));
    }

    /**
     * Add the replacement faces as children to the original faces.
     *
     * @param original The faces that are currently in the DAG.
     * @param replacement The faces we want to replace the original faces with by making them children of the originals.
     */
    public void replaceFaces(List<TriangleFace> original, List<TriangleFace> replacement) {
//        System.out.println("Replacing faces " + Arrays.toString(original.stream().map(f -> f.id).toArray(Integer[]::new)) + " with " + Arrays.toString(replacement.stream().map(f -> f.id).toArray(Integer[]::new)));

        // Lets first convert the replacements to nodes.
        List<Node<TriangleFace>> replacementNodes = new ArrayList<>();
        for(TriangleFace r : replacement) replacementNodes.add(new Node<>(r));

        // We first have to find the children, and then add the replacements to the children.
        // Recursively search through the nodes...
        for(Node<TriangleFace> node : roots) {
            replaceFaces(original, replacementNodes, node);
        }
    }

    /**
     * Add the replacement faces as children to the original faces, starting from the given node.
     *
     * @param original THe faces that are currently in the DAG.
     * @param replacement The faces we want to replace the original faces with, as nodes.
     * @param node The node we want to start searching from.
     */
    private void replaceFaces(List<TriangleFace> original, List<Node<TriangleFace>> replacement, Node<TriangleFace> node) {
        // If the value of the node is one of the faces we are looking for, replace it.
        if(original.contains(node.value)) {
            // If the face already has children, we have visited it already. So do not add the replacements again.
            if(node.children.isEmpty()) {
                node.children.addAll(replacement);
            }
        } else {
            // Else, continue searching.
            for(Node<TriangleFace> c : node.children) {
                replaceFaces(original, replacement, c);
            }
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
        for(Node<TriangleFace> node : roots) {
            TriangleFace hit = findFace(p, node);
            if(hit != null) return hit;
        }

        // If not found, we know that it is the outside face, so return the outside face.
        return TriangleFace.outerFace;
    }

    /**
     * Find a face that contains the given point starting from the given node.
     *
     * @param p The point we want to find.
     * @param node The node we want to start the search at.
     * @return The corresponding face if it exists, null otherwise.
     */
    private TriangleFace findFace(Point2d p, Node<TriangleFace> node) {
        // First, check if the point can be in this node, before proceeding checking the children.
        Triangle2d.Location location = node.value.contains(p);
        if(location != Triangle2d.Location.OUTSIDE) {
            // Check if we have children, if not, this is a leaf node and we return it as a result.
            if(node.children.isEmpty()) {
                return node.value;
            }

            // Otherwise, iterate over all children and do the same check.
            for(Node<TriangleFace> child : node.children) {
                TriangleFace hit = findFace(p, child);
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
    public Set<TriangleFace> getFaces() {
        // The currently active faces are all the leaves of the DAG.
        Set<TriangleFace> faces = getLeaves();
        faces.add(TriangleFace.outerFace);
        return faces;
    }
}
