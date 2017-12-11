package geo.structure.geo;

import geo.structure.math.Triangle2d;

/**
 * A face in a half edge structure, which is an extension of a Triangle2d.
 */
public class Face extends Triangle2d {
    // Here, we have one half edge that is part of the cycle enclosing the face.
    public Edge outerComponent;

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    /**
     * Create a triangle face, given the three edges surrounding it in counter clock wise order.
     *
     * @param e1 The first edge of the triangle.
     * @param e2 The second edge of the triangle.
     * @param e3 The third edge of the triangle.
     */
    public Face(Edge e1, Edge e2, Edge e3) {
        super(e1.origin, e2.origin, e3.origin);

        // Assign a new id.
        id = counter++;

        // Make the pointers of the triangle sound. I.e. make sure that the cycle is correct, set face relations etc.
        e1.setNext(e2);
        e2.setNext(e3);
        e3.setNext(e1);

        // Set the incident face for all the edges.
        e1.incidentFace = e2.incidentFace = e3.incidentFace = this;

        // Let the first edge be the outer component.
        outerComponent = e1;
    }
}
