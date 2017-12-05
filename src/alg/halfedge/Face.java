package alg.halfedge;

import java.util.ArrayList;
import java.util.List;

/**
 * Faces in a half-edge data structure.
 */
public class Face {
    // Here, we have one half edge that is part of the cycle around the face.
    public Edge outerComponent;

    /**
     * Special class for the outer face.
     */
    public class OuterFace extends Face {

    }
}
