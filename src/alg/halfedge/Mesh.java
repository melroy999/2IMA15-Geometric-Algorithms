package alg.halfedge;

import java.awt.*;
import java.util.HashMap;

/**
 * The object that ties the entire half-edge structure together and manages the operations upon the set.
 */
public class Mesh {
    // The list of vertices, mapped from integer to vertex such that we can access vertices by their id.
    private final HashMap<Integer, Vertex> vertices = new HashMap<>();

    /**
     * Insert the given point object as a vertex into the mesh.
     *
     * @param point The coordinates of the vertex to create and add to the mesh.
     */
    public void insertVertex(Point point) {
        // We simply add the new vertex to the list.
        Vertex vertex = new Vertex(point);
        vertices.put(vertex.id, vertex);
    }

    /**
     * Remove the vertex with the given id.
     *
     * @param id The id of the vertex we want to remove.
     * @return The vertex object that was removed from the id mapping.
     */
    public Vertex removeVertex(int id) {
        // First check if the vertex is present.
        Vertex target = vertices.get(id);

        // Depending on whether it exists or not, we have to do some bookkeeping:
        // - Remove edges that are connected to the vertex;
        // - Update faces.
        if(target != null) {
            // Visit all edges originating from this vertex, and remove them.
            for(Edge e : target) {
                // This automatically removes the half-edge as well.
                removeEdge(e);

                // Remove all references of the edge, which currently will only be in the original edge.
                e.origin = null;
            }
        }

        // Now we can remove the vertex from the list.
        return vertices.remove(id);
    }

    /**
     * Remove the edge between vertices with id i and j.
     *
     * @param i The id of the source vertex.
     * @param j The id of the target vertex.
     * @return The edge that was removed if it exists, null otherwise.
     */
    public Edge removeEdge(int i, int j) throws MissingVertexException {
       // Find the edge.
       Edge e = findEdge(i, j);

       if(e != null) {
           // Call the remove.
           e = removeEdge(e);
       }

       // Return the edge we removed, null if it does not exist.
       return e;
    }

    /**
     * Remove the given edge from the mesh, and validate/correct the references.
     * Removes both sides of the half-edge.
     *
     * @param edge The edge we want to remove.
     * @return The removed edge.
     */
    private Edge removeEdge(Edge edge) {
        // When we remove the edge, we have to update the previous and next pointers of the surrounding edges.
        // We have to keep in mind that it may occur that the vertex/vertices of the edge lose their last edge.
        updatePointersToSkipEdgeOnOrigin(edge);
        
        // Do the same for the other endpoint, using the twin of the edge as origin point.
        updatePointersToSkipEdgeOnOrigin(edge.twin);

        // We possibly have that two faces have to be merged.
        // This is only the case when both of the two endpoints still are connected to edges.
        if(edge.origin.incidentEdge != null && edge.twin.origin.incidentEdge != null) {
            // Choose one of the two faces, giving priority to the outer face.
            // On default, we choose the face of the original edge.
            Face merge = edge.twin.incidentFace instanceof Face.OuterFace ? edge.twin.incidentFace : edge.incidentFace;

            // Make sure that all edges have the same face as reference.
            // Since we did not set the references to null on the edge, we should still be able to find neighbors.
            // We take the previous, such that we are actually able to return to the starting position.
            for(Edge e : merge.outerComponent.previous) {
                e.incidentFace = merge;
            }
        }

        // We should make sure that the face has an edge that still exists.
        // We have a giant problem here if we have two points without edges in another face.
        // Otherwise, take next of the next, since the next of the next cannot be the twin of the removed edge.
        if(edge.origin.incidentEdge == null && edge.twin.origin.incidentEdge == null) {
            // TODO fix that problem...
            // We should introduce inner components for this probably... That complicates things a lot more.
            System.exit(666);
        } else {
            edge.incidentFace.outerComponent = edge.next.next;
        }

        // Return the original edge.
        return edge;
    }

    /**
     * Find the edge with vertex with id i as origin, moving to vertex j.
     *
     * @param i The id of the first vertex.
     * @param j The id of the second vertex.
     * @return The vertex if it exists, null otherwise.
     */
    private Edge findEdge(int i, int j) throws MissingVertexException {
        // First, check if the vertices exist.
        if(!vertices.containsKey(i) || !vertices.containsKey(j)) {
            throw new MissingVertexException();
        }

        // Fetch the origin vertex.
        Vertex vi = vertices.get(i);
        Vertex vj = vertices.get(j);

        // Iterate over all vertices originating from vi, such that we can find one with endpoint vj.
        for(Edge e : vi) {
            // Get the twin of the edge, as it may have origin vj.
            if(e.twin.origin.id == vj.id) {
                return e;
            }
        }

        // If we found none, return null.
        return null;
    }

    /**
     * Update the edge pointers connected to the origin point of the edge such that the given edge is skipped when
     * querying previous/next.
     * IMPORTANT: This is only done at the side of the origin of the given edge!
     *
     * @param edge The edge we want to forget about.
     */
    private void updatePointersToSkipEdgeOnOrigin(Edge edge) {
        // We start with fixing the references at the starting point of the edge, i.e. origin of edge.
        // The vertex will lose its only connection if the next of the twin is the current edge.
        // In such a case, we should not edit the pointers, as it would be 'pointless', pun intended.
        if(edge.twin.next != edge) {
            edge.previous.next = edge.twin.next;
            edge.twin.next.previous = edge.previous;

            // We should set an incident edge we know that exists, which is the next of the twin, because of the origin.
            edge.origin.incidentEdge = edge.twin.next;
        } else {
            // Set the incident edge on null, as the vertex becomes disconnected.
            edge.origin.incidentEdge = null;
        }
    }

    public class EdgeNotFoundException extends Exception {

    }

    public class MissingVertexException extends Exception {

    }
}
