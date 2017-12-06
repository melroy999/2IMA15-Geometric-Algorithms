package alg.halfedge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Faces in a half-edge data structure.
 */
public class Face {
    // Here, we have one half edge that is part of the cycle around the face.
    public Edge outerComponent;

    // List of inner components.
    public final Set<Edge> innerComponents = new HashSet<>();

    // Give each edge an id, such that we can reliably delete and track it.
    private static int counter = 0;
    public final int id;

    public Face() {
        id = counter++;
    }

    private Face(int id) {
        this.id = id;
    }

    /**
     * Special class for the outer face.
     */
    public class OuterFace extends Face {
        public OuterFace() {
            super(-1);
        }
    }
}
