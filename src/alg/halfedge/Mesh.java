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
            // The remove function already removes the twin, so we only have to do the removal in one direction.

            // TODO implement edge and face updates.
        }

        // Now we can remove the vertex from the list.
        return vertices.remove(id);
    }

    /**
     * Insert a half-edge pair between the vertices with id i and j.
     *
     * @param i The id of the first vertex.
     * @param j The id of the second vertex.
     */
    public void insertEdge(int i, int j) {
        // Find the vertices that we are targeting.
        Vertex v1 = vertices.get(i);
        Vertex v2 = vertices.get(j);

        // One of the half edges will originate from the first vertex, the other from the second.
        Edge v1_v2 = new Edge(v1);
        Edge v2_v1 = new Edge(v2);

        // Make sure that the twin data is set correctly.
        v1_v2.twin = v2_v1;
        v2_v1.twin = v1_v2;

        // We now have to do bookkeeping.
        // TODO do the bookkeeping...
    }

    /**
     * Remove the edge between the vertices with id i and j.
     *
     * @param i The id of the first vertex.
     * @param j The id of the second vertex.
     */
    public void removeEdge(int i, int j) throws MissingVertexException, EdgeNotFoundException {
        // Check if the ids exist.
        if(!vertices.containsKey(i) || !vertices.containsKey(j)) {
            throw new MissingVertexException();
        }

        // Find the vertices that we are targeting.
        Vertex v1 = vertices.get(i);
        Vertex v2 = vertices.get(j);

        // We want to find the correct half-edge to remove.
        // The half-edge we are looking for has a twin that has the origin vertex with id j.
        Edge target = v1.incidentEdge;
        int originalTarget = target.twin.origin.id;

        // Loop until we have visited all options, or until we found a match.
        while(target.twin.origin.id != j) {
            // If we have not found a match, take the next of the twin as our new target and check again.
            target = target.twin.next;

            // If the next vertex has the same target id as the original, break the loop since the edge does not exist.
            if(target.twin.origin.id == originalTarget) {
                throw new EdgeNotFoundException();
            }
        }

        // Get the twin for easy access.
        Edge twin = target.twin;

        // If we found a match, remove all traces of the edge.
        // - The next and previous pointers should be adjusted for both the next and previous line segments.
        target.previous.next = twin.next;
        twin.next.previous = target.previous;
        target.next.previous = twin.previous;
        twin.previous.next = target.next;

        // Since we created a gap, we should merge the two faces.
        // We will "Destroy" the face of the twin, unless it is the outer face.
        Face merge = twin.incidentFace instanceof Face.OuterFace ? twin.incidentFace : target.incidentFace;

        // Set the other's incident face to null, and make sure that the merged face outer edge exists.
        Face delete = !(twin.incidentFace instanceof Face.OuterFace) ? twin.incidentFace : target.incidentFace;

        // It may occur however that the faces are already equal, do not delete if this is the case.
        merge.outerComponent = target.previous;
        if(merge != delete) {
            // Deletion by null assignment.
            delete.outerComponent = null;

            // Change the face reference of all edges in the cycle.
            Edge target2 = merge.outerComponent;
            int originalTarget2 = target.origin.id;

            // Set the face to merge as long as we have not encountered our original target again.
            do {
                target2.incidentFace = merge;
                target2 = target2.next;
            } while(target2.origin.id != originalTarget2);
        }

        // Make sure that the endpoints of the removed line have valid edge pointers...
        v1.incidentEdge = twin.next;
        v2.incidentEdge = target.next;

        // Make sure that all the pointers for the original edges have been set to null for the garbage collection.
        target.next = target.previous = null;
        twin.next = twin.previous = null;

        // TODO what if the vertex has no connected vertices anymore? this could happen and is not handled correctly.
        // TODO we could even have that both endpoints become edgeless.
    }

    public class EdgeNotFoundException extends Exception {

    }

    public class MissingVertexException extends Exception {

    }
}
