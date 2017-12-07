package alg.structure.halfedge;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * The object that ties the entire half-edge structure together and manages the operations upon the set.
 */
public class Mesh {
    // The list of vertices, mapped from integer to vertex such that we can access vertices by their id.
    // The same holds for faces and edges.
    private final HashMap<Integer, Vertex> vertices = new HashMap<>();
    private final HashMap<Integer, Edge> edges = new HashMap<>();
    private final HashMap<Integer, Face> faces = new HashMap<>();

    // A boolean for tracking special cases.
    boolean encounteredStart = false;

    // The outer face.
    Face outerFace = new Face.OuterFace();

    /**
     * Get a list of all vertices in the mesh.
     *
     * @return The list of vertices we have in the mesh as a collection.
     */
    public Collection<Vertex> getVertices() {
        return vertices.values();
    }

    /**
     * Get a list of all half-edges in the mesh.
     *
     * @return The list of half-edges we have in the mesh as a collection.
     */
    public Collection<Edge> getEdges() {
        return edges.values();
    }

    /**
     * Get a list of all faces in the mesh.
     *
     * @return The list of faces we have in the mesh as a collection.
     */
    public Collection<Face> getFaces() {
        return faces.values();
    }

    /**
     * Insert the given point object as a vertex into the mesh.
     *
     * @param point The coordinates of the vertex to create and add to the mesh.
     * @return The vertex object that was added to the id mapping.
     */
    public Vertex insertVertex(Point point) {
        // We simply add the new vertex to the list.
        Vertex vertex = new Vertex(point);
        vertices.put(vertex.id, vertex);
        return vertex;
    }

    /**
     * Remove the vertex with the given id.
     *
     * @param id The id of the vertex we want to remove.
     * @return The vertex object that was removed from the id mapping.
     */
    public Vertex removeVertex(int id) throws InnerComponentsNotSupportedException {
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
     * Get the vertex with the given id.
     *
     * @param id The id of the vertex to find.
     * @return The vertex with the given id, null if not found.
     */
    public Vertex getVertex(int id) {
        return vertices.get(id);
    }

    /**
     * Create an edge between the two given vertices.
     *
     * @param i The starting point of the edge.
     * @param j The endpoint of the edge.
     * @return The edge as an object if created successfully, null otherwise.
     */
    public Edge insertEdge(int i, int j) throws MissingVertexException, UnableToInsertEdgeException, InnerComponentsNotSupportedException {
        // First, check if the vertices exist.
        if(!vertices.containsKey(i) || !vertices.containsKey(j)) {
            throw new MissingVertexException();
        }

        // Check whether the edge already exists, if it does return null.
        if(findEdge(i, j) != null) return null;

        // Fetch the two vertices.
        Vertex vi = vertices.get(i);
        Vertex vj = vertices.get(j);

        // First, we want to check for "simple" vertices, i.e. they have no other connections yet to worry about.
        boolean viIsSimple = vi.incidentEdge == null;
        boolean vjIsSimple = vj.incidentEdge == null;

        // Create two half-edges, one originating from vi and the other from vj.
        Edge vi_vj = new Edge(vi, vj);
        Edge vj_vi = new Edge(vj, vi);

        // Make the edges twins of one another.
        vi_vj.twin = vj_vi;
        vj_vi.twin = vi_vj;

        // For the simple vertices, we can just add a connection between the edge and the twin.
        if(viIsSimple) {
            vi_vj.twin.next = vi_vj;
            vj_vi.previous = vi_vj.twin;
        } else {
            // Otherwise, insert it at the appropriate location.
            insertEdgeIntoOriginChain(vi_vj);
        }

        if(vjIsSimple) {
            vi_vj.next = vi_vj.twin;
            vi_vj.twin.previous = vi_vj;
        } else {
            // Otherwise, insert it at the appropriate location.
            insertEdgeIntoOriginChain(vj_vi);
        }

        // It could be that we created a new face. This will only be the case if both vertices are not simple.
        if(!viIsSimple && !vjIsSimple) {
            // This is more complicated when we have split the outside plane, since we do not know what the inside is.
            if(vi_vj.incidentFace instanceof Face.OuterFace) {
                // We don't want to end up here a second time, as we cannot be certain that it is the outer face.
                if(encounteredStart) throw new InnerComponentsNotSupportedException();

                // TODO how do we see the difference between the inside, and the outside of the figure?
                // TODO whether the current implementation makes sense.
                // Since we normally would have convex areas when having only 4 vertices, we could use that.
                // Could we do something with the sum of the (counter)clockwise angles?

                // I think that we always have a lower sum of angles inside of the figure than outside.
                // Lets use that.
                if(getCycleAngleSum(vi_vj) > getCycleAngleSum(vi_vj.twin)) {
                    // If vi_vj is larger, it will be at the outside. So initialize a new face at the twin inside.
                    Face face = outerFace;
                    face.outerComponent = vi_vj.twin;

                    // Now set the new face on all edges we have in the twin's cycle.
                    for(Edge edge : vi_vj.twin) {
                        edge.incidentFace = face;
                    }
                } else {
                    // If vi_vj is smaller, it will be at the inside. So initialize a new face at the edge inside.
                    Face face = outerFace;
                    face.outerComponent = vi_vj;

                    // Now set the new face on all edges we have in the twin's cycle.
                    for(Edge edge : vi_vj) {
                        edge.incidentFace = face;
                    }
                }

                // Change the flag.
                encounteredStart = true;
            } else {
                // If it is not an outer face, we just replace all of the faces of the twin cycle.
                // Obviously make sure that the face has a reference to at least one edge pointing towards it.
                Face face = new Face();
                face.outerComponent = vi_vj.twin;

                // Now set the new face on all edges we have in the twin's cycle.
                for(Edge edge : vi_vj.twin) {
                    edge.incidentFace = face;
                }
            }
        } else if(viIsSimple && vjIsSimple) {
            // TODO where do we assign the outer faces for the initial vertices that are inserted...?
            // If they are both simple, I assume that they are both in the outer face,
            // and thus we should assign the outer face to both sides.
            Face face = outerFace;
            face.outerComponent = vi_vj;
            vi_vj.incidentFace = face;
            vi_vj.twin.incidentFace = face;
        }

        // TODO make sure that the edges and faces are added to list.

        // Return the edge.
        return vi_vj;
    }

    /**
     * Remove the edge between vertices with id i and j.
     *
     * @param i The id of the source vertex.
     * @param j The id of the target vertex.
     * @return The edge that was removed if it exists, null otherwise.
     */
    public Edge removeEdge(int i, int j) throws MissingVertexException, InnerComponentsNotSupportedException {
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
    private Edge removeEdge(Edge edge) throws InnerComponentsNotSupportedException {
        // When we remove the edge, we have to update the previous and next pointers of the surrounding edges.
        // We have to keep in mind that it may occur that the vertex/vertices of the edge lose their last edge.
        updatePointersToSkipEdgeOnOrigin(edge);
        
        // Do the same for the other endpoint, using the twin of the edge as origin point.
        updatePointersToSkipEdgeOnOrigin(edge.twin);

        // We possibly have that two faces have to be merged.
        // This is only the case when both of the two endpoints still are connected to edges.
        if(edge.origin.incidentEdge != null && edge.twin.origin.incidentEdge != null) {
            // If both are connected, and already have the same face, we are creating an inner component.
            if(edge.incidentFace == edge.twin.incidentFace) {
                // However, which side is the inner component, and which side is not?
                throw new InnerComponentsNotSupportedException();
            }

            // Choose one of the two faces, giving priority to the outer face.
            // On default, we choose the face of the original edge.
            Face merge = edge.twin.incidentFace instanceof Face.OuterFace ? edge.twin.incidentFace : edge.incidentFace;

            // Make sure that all edges have the same face as reference.
            // Since we did not set the references to null on the edge, we should still be able to find neighbors.
            // We take the previous, such that we are actually able to return to the starting position.
            for(Edge e : merge.outerComponent.previous) {
                e.incidentFace = merge;
            }

            // We have to remove the face that we are replacing here.
            Face delete = !(edge.twin.incidentFace instanceof Face.OuterFace) ? edge.twin.incidentFace : edge.incidentFace;
            faces.remove(delete.id);
        }

        // We should make sure that the face has an edge that still exists.
        // We have a giant problem here if we have two points without edges in another face.
        // Otherwise, take next of the next, since the next of the next cannot be the twin of the removed edge.
        if(edge.origin.incidentEdge == null && edge.twin.origin.incidentEdge == null) {
            // TODO fix that problem...
            // We should somehow find another edge that is part of the face...
            System.exit(666);
        } else {
            edge.incidentFace.outerComponent = edge.next.next;
        }

        // Remove the edges from the mappings.
        edges.remove(edge.id);
        edges.remove(edge.twin.id);

        // Return the original edge.
        return edge;
    }

    /**
     * Get the half-edge with the given id if it exists.
     *
     * @param id The id of the desired edge.
     * @return The half-edge with the given id, null otherwise.
     */
    private Edge getEdge(int id) {
        return edges.get(id);
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
     * Insert the edge at the correct position in the edge origin rotational chain.
     * This also takes care of setting the appropriate face references for the new edges.
     *
     * @param edge The edge we want to insert.
     * @throws UnableToInsertEdgeException Whenever we are unable to insert the edge.
     */
    private void insertEdgeIntoOriginChain(Edge edge) throws UnableToInsertEdgeException {
        // The insertion will be done on the origin of the edge:
        Vertex origin = edge.origin;

        // We essentially want to iterate over all the edges, and check if it first somewhere as the next edge.
        for(Edge e : origin) {
            if(e.isEdgeCandidateForNext(edge)) {
                // Insert the edge, by changing the appropriate pointers.
                // Be careful of the order!
                edge.previous = e;
                edge.twin.next = e.next;
                e.next.previous = edge.twin;
                e.next = edge;

                // Copy the face of the previous edge that was added.
                edge.incidentFace = e.incidentFace;

                // We are done, so return.
                return;
            }
        }

        // If we reach this point, we did not manage to insert the edge.
        throw new UnableToInsertEdgeException();
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

    /**
     * Get the sum of all the angles between edges in the cycle.
     *
     * @param edge The starting edge.
     * @return The sum.
     */
    private double getCycleAngleSum(Edge edge) {
        double sum = 0;
        for(Edge e : edge) {
            sum += e.vector.angle(e.next.vector);
        }
        return sum;
    }

    public class UnableToInsertEdgeException extends Exception {

    }

    public class MissingVertexException extends Exception {

    }

    public class InnerComponentsNotSupportedException extends  Exception {

    }
}
